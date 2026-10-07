package org.app.bank.controller;

import java.util.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import org.app.bank.service.CreateAccount;
import org.app.bank.service.CreateAccountRequest;
import org.app.bank.service.CreateAccountResponse;
import org.app.bank.service.CreateAccountService;
import org.app.bank.service.GetTransactionHistory;
import org.app.bank.service.GetTransactionHistoryRequest;
import org.app.bank.service.GetTransactionHistoryResponse;
import org.app.bank.service.GetTransactionHistoryResponseInner;
import org.app.bank.service.GetTransactionHistoryService;
import org.app.bank.service.MakeTransaction;
import org.app.bank.service.MakeTransactionRequest;
import org.app.bank.service.MakeTransactionService;

@RestController
public class BankController {

    private final CreateAccount createAccountService;
    private final GetTransactionHistory getTransactionHistoryService;
    private final MakeTransaction makeTransactionService;

    public BankController(CreateAccount createAccountService, GetTransactionHistory getTransactionHistoryService,
        MakeTransaction makeTransactionService) {

        this.createAccountService = createAccountService;
        this.getTransactionHistoryService = getTransactionHistoryService;
        this.makeTransactionService = makeTransactionService;
    }

    @PostMapping("/createAccount")
    public ResponseEntity<String> createAccount(@RequestParam String name, 
        @RequestParam Double balance) {

        CreateAccountRequest request = new CreateAccountRequest(name, balance);
        CreateAccountResponse response = createAccountService.createAccount(request);

        String outputMessage = String.format("Created account %d for %s with initial balance %f", 
            response.id(), name, balance);
        return ResponseEntity.status(HttpStatus.OK)
                             .body(outputMessage);
    }

    @PostMapping("/makeTransaction")
    public ResponseEntity<String> makeTransaction(@RequestParam Long fromAccount, 
        @RequestParam Long toAccount, @RequestParam Double amount) {

        MakeTransactionRequest request = new MakeTransactionRequest(fromAccount, toAccount, amount);
        makeTransactionService.makeTransaction(request);

        String outputMessage = String.format("Transferred %f from account %d to account %d", 
            amount, fromAccount, toAccount);
        return ResponseEntity.status(HttpStatus.OK)
                             .body(outputMessage);
    }

    @GetMapping("/getTransactionHistory")
    public ResponseEntity<List<String>> getTransactionHistory(@RequestParam Long account) {
        GetTransactionHistoryRequest request = new GetTransactionHistoryRequest(account);
        GetTransactionHistoryResponse response = getTransactionHistoryService.getTransactionHistory(request);

        List<String> resultList = new ArrayList<>();
        Double balance = 0.0;
        for (GetTransactionHistoryResponseInner transaction : response.transactions()) {
            if (transaction == null) {
                continue;
            }

            if (transaction.isInit()) {
                // use 0 account ID as placeholder for initial balance
                balance = transaction.amount();
                resultList.add(String.format("INITIAL BALANCE = %f", balance));
            }
            else if (transaction.isOutgoing()) {
                balance -= transaction.amount();
                resultList.add(String.format("TRANSFER OUT %f TO %d, NEW BALANCE = %f", 
                    transaction.amount(), transaction.otherAccount(), balance));
            }
            else {
                balance += transaction.amount();
                resultList.add(String.format("TRANSFER IN %f FROM %d, NEW BALANCE = %f", 
                    transaction.amount(), transaction.otherAccount(), balance));
            }
        }

        return ResponseEntity.status(HttpStatus.OK)
                             .body(resultList);
    }

}