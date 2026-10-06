package org.app.bank.service;

import java.util.*;

import org.apache.commons.lang3.tuple.Pair;

import org.springframework.stereotype.Service;

import org.app.bank.entity.Account;
import org.app.bank.entity.Transaction;
import org.app.bank.entity.TimestampComparator;
import org.app.bank.repository.AccountRepository;
import org.app.bank.repository.TransactionRepository;
import org.app.bank.exception.AccountNotFoundException;

@Service
public class GetTransactionHistoryService implements GetTransactionHistory {
    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;

    // timestamp-based comparator for transactions
    private TimestampComparator comparator = new TimestampComparator();

    public GetTransactionHistoryService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public GetTransactionHistoryResponse getTransactionHistory(GetTransactionHistoryRequest request) {
        // ensure the account exists
        Account account = accountRepository.findById(request.account()).orElse(null);
        if (account == null) {
            throw new AccountNotFoundException(request.account());
        }

        // get all transactions into the account
        List<Transaction> transactions = transactionRepository.findAllByToAccount(request.account());

        // get all transactions out of the account
        transactions.addAll(transactionRepository.findAllByFromAccount(request.account()));
        
        // sort all transactions by timestamp
        transactions.sort(comparator);

        // add the transactions to the output list, in output format
        List<Pair<Long, Double>> outputList = new ArrayList<>();
        for (Transaction transaction : transactions) {
            Double amount;
            Long otherAccount;
            if (transaction.getToAccount() == account.getId()) {
                amount = transaction.getAmount();
                otherAccount = transaction.getFromAccount();
            }
            else {
                amount = -transaction.getAmount();
                otherAccount = transaction.getToAccount();
            }

            outputList.add(Pair.of(otherAccount, amount));
        }

        return new GetTransactionHistoryResponse(outputList);
    }
}