package com.bank.uob.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.bank.uob.model.Transactions;

public interface TransactRepo extends ListCrudRepository<Transactions, Integer> {

    @Query(value = "SELECT * FROM uob_transactions ORDER BY trans_id DESC LIMIT 3", nativeQuery = true)
    public List<Transactions> findRecentTransacts();

    @Query(value = "SELECT * FROM uob_transactions ORDER BY trans_id DESC", nativeQuery = true)
    public List<Transactions> findAllDesc();

    @Query(value = "SELECT * FROM uob_transactions WHERE acc_id = ?1 ORDER BY trans_id DESC", nativeQuery = true)
    List<Transactions> findByAcc_id(int acc_id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM uob_transactions WHERE acc_id = ?1", nativeQuery = true)
    int deleteByAcc_id(int acc_id);

}
