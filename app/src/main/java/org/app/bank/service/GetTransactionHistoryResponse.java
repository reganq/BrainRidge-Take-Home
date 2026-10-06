package org.app.bank.service;

import java.util.List;
import org.apache.commons.lang3.tuple.Pair;

public record GetTransactionHistoryResponse(List<Pair<Long, Double>> transactions) {}