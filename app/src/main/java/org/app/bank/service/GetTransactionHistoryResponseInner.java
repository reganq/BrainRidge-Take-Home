package org.app.bank.service;

// The inner record for transaction history
public record GetTransactionHistoryResponseInner(Boolean isInit, Boolean isOutgoing, Long otherAccount, Double amount) {}