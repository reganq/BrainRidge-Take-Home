package org.app.bank.service;

import java.util.List;

// The expected output format for transaction history is a pair of the other account ID, and the amount transferred (negative if outgoing)
public record GetTransactionHistoryResponse(List<GetTransactionHistoryResponseInner> transactions) {}