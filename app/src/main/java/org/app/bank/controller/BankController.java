package org.app.bank.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

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
            response.getId(), name, balance);
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
    public ResponseEntity<List<String>> getTransactionHistory(@RequestParam Long id) {
        GetTransactionHistoryRequest request = new GetTransactionHistoryRequest(id);
        GetTransactionHistoryResponse response = getTransactionHistoryService.getTransactionHistory(request);

        List<String> resultList = new ArrayList<>();
        Double balance = 0.0;
        for (Pair<Long, Double> transaction : response.getTransactions()) {
            if (transaction == null || transaction.getLeft() == null || transaction.getRight() == null) {
                continue;
            }

            if (transaction.getLeft() == 0) {
                // use 0 account ID as placeholder for initial balance
                balance = transaction.getRight();
                resultList.add(String.format("INITIAL BALANCE = %f", balance));
            }
            else if (transaction.getRight() < 0) {
                balance += transaction.getRight();
                resultList.add(String.format("TRANSFER OUT %f TO %d, NEW BALANCE = %f", 
                    -transaction.getRight(), transaction.getLeft(), balance));
            }
            else {
                balance += transaction.getRight();
                resultList.add(String.format("TRANSFER IN %f FROM %d, NEW BALANCE = %f", 
                    transaction.getRight(), transaction.getLeft(), balance));
            }
        }

        return ResponseEntity.status(HttoStatus.OK)
                             .body(resultList);
    }

}