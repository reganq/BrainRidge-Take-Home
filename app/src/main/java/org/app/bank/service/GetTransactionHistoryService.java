package org.app.bank.service;

import java.util.*;

public class GetTransactionHistoryService implements GetTransactionHistory {
    public GetTransactionHistoryResponse getTransactionHistory(GetTransactionHistoryRequest request) {
        return new GetTransactionHistoryResponse(new ArrayList<>());
    }
}