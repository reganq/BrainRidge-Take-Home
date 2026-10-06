package org.app.bank.repository;

import java.util.*;

import org.app.bank.entity.Transaction;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByFromAccount(Long fromAccount);
    List<Transaction> findAllByToAccount(Long toAccount);
}