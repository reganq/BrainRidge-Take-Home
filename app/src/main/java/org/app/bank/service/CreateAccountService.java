package org.app.bank.service;

import org.springframework.stereotype.Service;

@Service
public class CreateAccountService implements CreateAccount {
    @Override
    public CreateAccountResponse createAccount(CreateAccountRequest request) {
        if (request.getInitialBalance() < 0) {
            throw new NegativeBalanceException(request.getInitialBalance());
        }
    }
}