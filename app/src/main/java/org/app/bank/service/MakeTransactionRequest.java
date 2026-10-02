package org.app.bank.service;

public record MakeTransactionRequest(Long fromAccount, Long toAccount, Double amount) {}