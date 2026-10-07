package org.app.bank.service;

import org.springframework.stereotype.Service;

import org.app.bank.entity.Account;
import org.app.bank.entity.Transaction;
import org.app.bank.repository.AccountRepository;
import org.app.bank.repository.TransactionRepository;
import org.app.bank.exception.NegativeBalanceException;
import org.app.bank.exception.EmptyNameException;

@Service
public class CreateAccountService implements CreateAccount {

    AccountRepository accountRepository;
    TransactionRepository transactionRepository;

    public CreateAccountService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public CreateAccountResponse createAccount(CreateAccountRequest request) {
        // error checking
        if (request.initialBalance() < 0) {
            throw new NegativeBalanceException(request.initialBalance());
        }

        if (request.name() == null || request.name().isEmpty()) {
            throw new EmptyNameException();
        }

        // create and save new account
        Account newAccount = new Account(request.name(), request.initialBalance());
        accountRepository.saveAndFlush(newAccount);
        
        // create and save the initial balance transaction
        Transaction initialBalanceTransaction = new Transaction(newAccount.getId(), request.initialBalance());
        transactionRepository.saveAndFlush(initialBalanceTransaction);

        return new CreateAccountResponse(newAccount.getId());
    }
}