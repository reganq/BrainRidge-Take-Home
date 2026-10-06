package org.app.bank.service;

import org.springframework.stereotype.Service;

import org.app.bank.entity.Account;
import org.app.bank.repository.AccountRepository;
import org.app.bank.exception.NegativeBalanceException;
import org.app.bank.exception.EmptyNameException;

@Service
public class CreateAccountService implements CreateAccount {

    AccountRepository repository;

    public CreateAccountService(AccountRepository repository) {
        this.repository = repository;
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

        repository.saveAndFlush(newAccount);

        return new CreateAccountResponse(newAccount.getId());
    }
}