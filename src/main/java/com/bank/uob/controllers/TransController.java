package com.bank.uob.controllers;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bank.uob.model.Accounts;
import com.bank.uob.model.Transactions;
import com.bank.uob.repositories.AccRepo;
import com.bank.uob.repositories.TransactRepo;

@Controller
@RequestMapping("/transact")
public class TransController {

    @Autowired
    TransactRepo tr;

    @Autowired
    AccRepo ar;

    private static final Logger logger = LoggerFactory.getLogger(TransController.class);

    @RequestMapping("/add")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public String transAdd(Model model) {

        List<Accounts> accs = ar.findAll();
        model.addAttribute("accs", accs);

        return "transadd";
    }

    @RequestMapping("/view")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public String transView(Model model) {
        List<Transactions> trans = tr.findAllDesc();
        List<Accounts> accs = ar.findAll();
        model.addAttribute("trans", trans);
        model.addAttribute("accs", accs);
        return "transview";
    }

    @RequestMapping("/view/{acc_id}")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public String transFilterView(@PathVariable("acc_id") int acc_id, Model model) {
        List<Transactions> trans = tr.findByAcc_id(acc_id);
        List<Accounts> accs = ar.findAll();
        System.out.println(trans.toString());
        model.addAttribute("trans", trans);
        model.addAttribute("accs", accs);
        return "transview";
    }

    @RequestMapping("/save")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public String saveTran(
            @RequestParam(value = "acc_id") Integer acc_id,
            @RequestParam(value = "type") String type,
            @RequestParam(value = "amt") double amt) {
        System.out.println("in save");

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String u = ((UserDetails) principal).getUsername();
        Optional<Accounts> account = ar.findById(acc_id);
        Accounts acc = account.get();
        Transactions t = new Transactions(type, amt, u, acc);
        tr.save(t);

        double newBal = type.equals("Deposit") ? acc.getAcc_bal() + amt : acc.getAcc_bal() - amt;
        acc.setAcc_bal(newBal);
        ar.save(acc);

        logger.info(
                String.format("New transaction saved by %s: $%.2f %s to %s account - %d", u, amt, type,
                        acc.getAcc_name(),
                        acc_id));
        logger.info("Updated " + acc.getAcc_name() + " account balance: $" + newBal);

        return "redirect:/transact/view";
    }

    @RequestMapping("/edit/{tran}")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public String showEdit(@PathVariable("tran") int id, Model model) {
        // Add/Deduct back the amount
        Optional<Transactions> tran = tr.findById(id);
        model.addAttribute("tran", tran.get());
        return "transedit";
    }

    @RequestMapping("/save_edit")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public String saveEditTran(
            @RequestParam("id") int id,
            @RequestParam("acc_id") Integer acc_id,
            @RequestParam("prevType") String prevType,
            @RequestParam("newType") String newType,
            @RequestParam("prevAmt") double prevAmt,
            @RequestParam("newAmt") double newAmt) {
        System.out.println("in save");

        Optional<Accounts> account = ar.findById(acc_id);
        Accounts acc = account.get();

        if (!prevType.equals(newType)) {
            double sumAmt = prevAmt + newAmt;
            double newBal = newType.equals("Deposit") ? acc.getAcc_bal() + sumAmt : acc.getAcc_bal() - sumAmt;
            acc.setAcc_bal(newBal);
            ar.save(acc);

            System.out.println(!prevType.equals(newType));
            System.out.println(prevAmt + newAmt);
            System.out.println(newBal);
        } else {
            double sumAmt = Math.abs(prevAmt - newAmt);
            double newBal = newType.equals("Deposit") ? acc.getAcc_bal() + sumAmt : acc.getAcc_bal() - sumAmt;
            acc.setAcc_bal(newBal);
            ar.save(acc);

            System.out.println(prevType != newType);
            System.out.println(prevAmt + newAmt);
            System.out.println(newBal);
        }

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String u = ((UserDetails) principal).getUsername();

        Optional<Transactions> tran = tr.findById(id);
        Transactions t = tran.get();
        t.setTrans_type(newType);
        t.setTrans_amt(newAmt);
        t.setTeller_name(u);
        tr.save(t);

        logger.info(
                String.format("Transaction id %d saved by %s: $%.2f %s to %s account - %d", id, u, newAmt, newType,
                        acc.getAcc_name(), acc_id));
        logger.info("Updated " + acc.getAcc_name() + " account balance: $" + acc.getAcc_bal());

        return "redirect:/transact/view";
    }

    @RequestMapping("/delete")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public String deleteTran(
            @RequestParam("id") int id,
            @RequestParam("type") String type,
            @RequestParam("amt") double amt,
            @RequestParam("acc_id") int acc_id,
            Model model) {

        tr.deleteById(id);

        Optional<Accounts> account = ar.findById(acc_id);
        Accounts acc = account.get();

        double newBal = type.equals("Deposit")
                ? acc.getAcc_bal() - amt
                : acc.getAcc_bal() + amt;
        acc.setAcc_bal(newBal);
        ar.save(acc);

        logger.info(String.format("Transaction id %d deleted: $%.2f %s from %s account - %d", id, amt,
                type,
                acc.getAcc_name(),
                acc_id));
        logger.info("Updated " + acc.getAcc_name() + " account balance: $" + acc.getAcc_bal());

        return "redirect:/transact/view";
    }
}
