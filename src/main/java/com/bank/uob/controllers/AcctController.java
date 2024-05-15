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
import com.bank.uob.repositories.AccRepo;
import com.bank.uob.repositories.TransactRepo;

@Controller
@RequestMapping("/acct")
public class AcctController {

    @Autowired
    AccRepo ar;

    @Autowired
    TransactRepo tr;

    private static final Logger logger = LoggerFactory.getLogger(AcctController.class);

    @RequestMapping("/add")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public String acctAdd() {

        return "acctadd";
    }

    @RequestMapping("/view")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public String acctView(Model model) {

        List<Accounts> accs = ar.findAll();
        model.addAttribute("accs", accs);

        return "acctview";
    }

    @RequestMapping("/edit/{acc}")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public String showEdit(@PathVariable("acc") int id, Model model) {
        Optional<Accounts> acc = ar.findById(id);
        model.addAttribute("acc", acc.get());
        return "acctedit";
    }

    @RequestMapping("/save")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public String saveUser(
            @RequestParam(value = "id") int id,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "bal") double bal) {
        System.out.println("in save");

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Accounts> accs = ar.findById(id);

        if (accs.isPresent()) {
            return "redirect:/acct/add?error";
        } else {
            String u = ((UserDetails) principal).getUsername();
            Accounts a = new Accounts(id, name, bal, u);
            ar.save(a);

            logger.info(String.format("Account id %d saved by %s: %s - $%.2f", id, u, name, bal));
        }
        return "redirect:/acct/view";
    }

    @RequestMapping("/save_edit")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public String saveEditUser(
            @RequestParam(value = "id") int id,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "bal") double bal) {
        System.out.println("in save");

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String u = ((UserDetails) principal).getUsername();
        Accounts a = new Accounts(id, name, bal, u);
        ar.save(a);

        logger.info(String.format("Account id %d edited by %s: %s - $%.2f", id, u, name, bal));

        return "redirect:/acct/view";
    }

    @RequestMapping("/delete/{acc_id}")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public String deleteUser(@PathVariable("acc_id") int acc_id, Model model) {
        Optional<Accounts> acc = ar.findById(acc_id);
        Accounts a = acc.get();

        tr.deleteByAcc_id(acc_id);
        ar.deleteById(acc_id);

        logger.info(String.format("Account id %d deleted by %s: %s - $%.2f", a.getAcc_id(), a.getTeller_name(),
                a.getAcc_name(),
                a.getAcc_bal()));

        return "redirect:/acct/view";
    }
}
