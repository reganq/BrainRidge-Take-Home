package org.app.bank.service;

import org.springframework.stereotype.Service;

import org.app.bank.entity.Account;
import org.app.bank.entity.Transaction;
import org.app.bank.repository.AccountRepository;
import org.app.bank.repository.TransactionRepository;
import org.app.bank.exception.AccountNotFoundException;
import org.app.bank.exception.InsufficientFundsException;

@Service
public class MakeTransactionService implements MakeTransaction {
    AccountRepository accountRepository;
    TransactionRepository transactionRepository;

    public MakeTransactionService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public void makeTransaction(MakeTransactionRequest request) {
        // error checking: do both accounts exist
        Account fromAccount = accountRepository.findById(request.fromAccount()).orElse(null);
        if (fromAccount == null) {
            throw new AccountNotFoundException(request.fromAccount());
        }

        Account toAccount = accountRepository.findById(request.toAccount()).orElse(null);
        if (toAccount == null) {
            throw new AccountNotFoundException(request.toAccount());
        }

        // error checking: does sender have sufficient balance
        if (fromAccount.getBalance() < request.amount()) {
            throw new InsufficientFundsException(request.fromAccount(), request.amount());
        }

        // make the entity change - should be autosaved
        fromAccount.setBalance(fromAccount.getBalance() - request.amount());
        toAccount.setBalance(toAccount.getBalance() + request.amount());

        // save a new transaction
        Transaction newTransaction = new Transaction(request.fromAccount(), request.toAccount(), request.amount());
        transactionRepository.saveAndFlush(newTransaction);
    }
}