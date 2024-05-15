package com.bank.uob.repositories;

import org.springframework.data.repository.ListCrudRepository;

import com.bank.uob.model.Accounts;

public interface AccRepo extends ListCrudRepository<Accounts, Integer> {

}
