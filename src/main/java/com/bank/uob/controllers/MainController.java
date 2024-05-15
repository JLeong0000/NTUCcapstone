package com.bank.uob.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.bank.uob.model.Accounts;
import com.bank.uob.model.Transactions;
import com.bank.uob.repositories.AccRepo;
import com.bank.uob.repositories.TransactRepo;

@Controller
public class MainController {

    @Autowired
    AccRepo ar;

    @Autowired
    TransactRepo tr;

    @GetMapping("/")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")

    public String showMain(Model model) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        List<Accounts> accs = ar.findAll();
        List<Transactions> trans = tr.findRecentTransacts();

        model.addAttribute("user", username);
        model.addAttribute("accs", accs);
        model.addAttribute("trans", trans);

        return "home";
    }

}
