package com.bank.uob.repositories;

import java.util.Optional;

import org.springframework.data.repository.ListCrudRepository;

import com.bank.uob.model.Users;

public interface UserRepo extends ListCrudRepository<Users, Integer> {
    Optional<Users> findByName(String name);

    Users findByNameAndPass(String name, String pass);
}
