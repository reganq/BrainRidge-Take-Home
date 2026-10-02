package org.app.bank.service;

public record GetTransactionHistoryResponse(List<Pair<Long, Double>> transactions) {}