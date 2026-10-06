package org.app.bank.service;

import java.util.List;
import org.apache.commons.lang3.tuple.Pair;

// The expected output format for transaction history is a pair of the other account ID, and the amount transferred (negative if outgoing)
public record GetTransactionHistoryResponse(List<Pair<Long, Double>> transactions) {}