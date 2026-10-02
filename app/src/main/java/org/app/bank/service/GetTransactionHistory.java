package org.app.bank.service;

import GetTransactionHistoryRequest;
import GetTransactionHistoryResponse;

public interface GetTransactionHistory {
    public GetTransactionHistoryResponse getTransactionHistory(GetTransactionHistoryRequest request);
}