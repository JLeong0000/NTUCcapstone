package com.bank.uob.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bank.uob.model.Users;
import com.bank.uob.repositories.UserRepo;

@Service
public class UserService {
    @Autowired
    private UserRepo ur;
    @Autowired
    private PasswordEncoder pe;

    public String addUser(Users user) {
        user.setPass(pe.encode(user.getPass()));
        ur.save(user);
        return "User Added";
    }

}
