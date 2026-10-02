package org.app.bank.service;

import MakeTransaction;
import MakeTransactionRequest;
import MakeTransactionResponse;

public class MakeTransactionService implements MakeTransaction {
    public MakeTransactionResponse makeTransaction(MakeTransactionRequest request) {
        return new MakeTransactionResponse();
    }
}