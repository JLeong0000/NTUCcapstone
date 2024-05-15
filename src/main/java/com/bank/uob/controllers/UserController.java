package com.bank.uob.controllers;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bank.uob.auth.UserService;
import com.bank.uob.model.Users;
import com.bank.uob.repositories.AccRepo;
import com.bank.uob.repositories.TransactRepo;
import com.bank.uob.repositories.UserRepo;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserRepo ur;

    @Autowired
    UserService us;

    @Autowired
    AccRepo ar;

    @Autowired
    TransactRepo tr;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @RequestMapping("/add")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public String userAdd() {
        return "useradd";
    }

    @RequestMapping("/view")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public String userView(Model model) {
        List<Users> users = ur.findAll();
        model.addAttribute("users", users);
        return "userview";
    }

    @RequestMapping("/edit/{usr}")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public String showEdit(@PathVariable("usr") int id, Model model) {
        Optional<Users> user = ur.findById(id);
        model.addAttribute("user", user.get());
        return "useredit";
    }

    @RequestMapping("/save")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public String saveUser(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "pwd") String pwd,
            @RequestParam(value = "role") String role) {
        System.out.println("in save");

        Optional<Users> user = ur.findByName(name);

        if (user.isPresent()) {
            return "redirect:/users/add?error";

        } else {
            us.addUser(new Users(name, pwd, role));

            logger.info(String.format("New user saved: %s - %s", name, role));
        }

        return "redirect:/users/view";
    }

    @RequestMapping("/save_edit")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public String saveEditUser(
            @RequestParam(value = "id") int id,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "pwd") String pwd,
            @RequestParam(value = "role") String role) {
        System.out.println("in save");

        Optional<Users> user = ur.findById(id);
        Users u = user.get();

        u.setName(name);
        u.setPass(pwd);
        u.setRole(role);
        ur.save(u);

        logger.info(String.format("User id %d saved: %s - %s", u.getId(), name, role));

        return "redirect:/users/view";
    }

    @RequestMapping("/delete/{usr}")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public String deleteUser(@PathVariable("usr") int id, Model model) {
        Optional<Users> user = ur.findById(id);
        Users u = user.get();
        ur.deleteById(id);

        logger.info(String.format("User id %d deleted: %s - %s", u.getId(), u.getName(), u.getRole()));
        return "redirect:/users/view";
    }
}
