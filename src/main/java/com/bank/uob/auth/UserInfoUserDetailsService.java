package com.bank.uob.auth;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.bank.uob.model.Users;
import com.bank.uob.repositories.UserRepo;

@Component
public class UserInfoUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo ur;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Users> user = ur.findByName(username);
        return user.map(UserInfoDetails::new).orElseThrow();
    }
}
