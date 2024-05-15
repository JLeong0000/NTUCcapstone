package com.bank.uob.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@SuppressWarnings("removal")
public class SecurityConfig {

    @Bean
    UserDetailsService userDetailsService() {
        return new UserInfoUserDetailsService();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests()
                .requestMatchers(
                        "/",
                        "/users/add",
                        "/users/view",
                        "/users/edit",
                        "/users/edit/*",
                        "/users/delete/*",
                        "/users/delete/",
                        "/users/save",
                        "/users/save_edit",
                        "/acct/add",
                        "/acct/view",
                        "/acct/edit",
                        "/acct/edit/*",
                        "/acct/delete/*",
                        "/acct/delete/",
                        "/acct/save",
                        "/acct/save_edit",
                        "/transact/add",
                        "/transact/view",
                        "/transact/view/*",
                        "/transact/edit",
                        "/transact/edit/*",
                        "/transact/delete",
                        "/transact/save",
                        "/transact/save_edit")
                .authenticated()
                .and()
                .authorizeHttpRequests()
                .requestMatchers(
                        "/css/*",
                        "/login",
                        "/logout")
                .permitAll()
                .and()
                .formLogin(login -> login
                        .loginPage("/login")
                        .permitAll()
                        .defaultSuccessUrl("/"))
                .logout(logout -> logout
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll())
                .build();
    }

    @Bean
    AuthenticationProvider authProvider() {
        DaoAuthenticationProvider dap = new DaoAuthenticationProvider();
        dap.setUserDetailsService(userDetailsService());
        dap.setPasswordEncoder(passwordEncoder());
        return dap;
    }
}
