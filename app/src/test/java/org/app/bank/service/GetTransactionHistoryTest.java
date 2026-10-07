package org.app.bank.service;

import java.util.*;

import org.app.bank.entity.Account;
import org.app.bank.entity.Transaction;
import org.app.bank.exception.AccountNotFoundException;
import org.app.bank.repository.AccountRepository;
import org.app.bank.repository.TransactionRepository;
import org.app.bank.service.GetTransactionHistoryRequest;
import org.app.bank.service.GetTransactionHistoryResponse;
import org.app.bank.service.GetTransactionHistoryService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.lenient;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class GetTransactionHistoryTest {

    // mocked databases
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private GetTransactionHistoryService getTransactionHistoryService;

    Account accountOne;
    Account accountTwo;
    Account accountThree;

    // mocked account repository
    private long nextAccountId = 1L;
    private Map<Long, Account> accountMap = new HashMap<>();

    // mocked transaction repository
    private long nextTransactionId = 1L;
    private Map<Long, Transaction> transactionMap = new HashMap<>();

    @Test
    void testGetTransactionsValidUser() {
        GetTransactionHistoryRequest request = new GetTransactionHistoryRequest(accountOne.getId());

        GetTransactionHistoryResponse response = null;
        try {
            response = getTransactionHistoryService.getTransactionHistory(request);
        }
        catch (RuntimeException re) {
            fail(String.format("Runtime Exception Thrown: %s", re.getMessage()));
        }

        assertNotNull(response);

        // check that the correct number of transactions were found, and that they are output in correct format
        assertEquals(3, response.transactions().size());

        assertEquals(0L, response.transactions().get(0).getKey());
        assertEquals(10.0, response.transactions().get(0).getValue());

        assertEquals(accountTwo.getId(), response.transactions().get(1).getKey());
        assertEquals(15.0, response.transactions().get(1).getValue());
        
        assertEquals(accountThree.getId(), response.transactions().get(2).getKey());
        assertEquals(-10.0, response.transactions().get(2).getValue());
    }

    @Test
    void testGetTransactionInvalidUser() {
        GetTransactionHistoryRequest request = new GetTransactionHistoryRequest(5L);

        // check we get the correct exception
        assertThrows(AccountNotFoundException.class, 
                     () -> getTransactionHistoryService.getTransactionHistory(request));
    }

    @BeforeEach
    void initializeDatabase() {
        // mocking account repository
        when(accountRepository.saveAndFlush(any(Account.class))).thenAnswer(invocation -> {
            Account account = invocation.getArgument(0);

            if (account.getId() == null) {
                account.setId(nextAccountId++);
            }

            accountMap.put(account.getId(), account);

            return account;
        });

        when(accountRepository.findById(any(Long.class))).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);

            return (accountMap.containsKey(id)) ? Optional.of(accountMap.get(id)) 
                                                : Optional.empty();
        });

        // mocking transaction repository
        when(transactionRepository.saveAndFlush(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction transaction = invocation.getArgument(0);

            if (transaction.getId() == null) {
                transaction.setId(nextTransactionId++);
            }

            transactionMap.put(transaction.getId(), transaction);

            return transaction;
        });

        lenient().when(transactionRepository.findAllByFromAccount(any(Long.class))).thenAnswer(invocation -> {
            Long fromAccountId = invocation.getArgument(0);

            List<Transaction> output = new ArrayList<>();

            for (Transaction transaction : transactionMap.values()) {
                if (transaction.getFromAccount() == fromAccountId) {
                    output.add(transaction);
                }
            }

            return output;
        });

        lenient().when(transactionRepository.findAllByToAccount(any(Long.class))).thenAnswer(invocation -> {
            Long toAccountId = invocation.getArgument(0);

            List<Transaction> output = new ArrayList<>();

            for (Transaction transaction : transactionMap.values()) {
                if (transaction.getToAccount() == toAccountId) {
                    output.add(transaction);
                }
            }

            return output;
        });

        accountOne = new Account("one", 10.0);
        accountRepository.saveAndFlush(accountOne);

        Transaction createOne = new Transaction(accountOne.getId(), 10.0);
        transactionRepository.saveAndFlush(createOne);

        accountTwo = new Account("two", 20.0);
        accountRepository.saveAndFlush(accountTwo);

        Transaction createTwo = new Transaction(accountTwo.getId(), 20.0);
        transactionRepository.saveAndFlush(createTwo);

        accountThree = new Account("three", 5.0);
        accountRepository.saveAndFlush(accountThree);

        Transaction createThree = new Transaction(accountThree.getId(), 5.0);
        transactionRepository.saveAndFlush(createThree);

        Transaction transactionOne = new Transaction(accountTwo.getId(), accountOne.getId(), 15.0);
        transactionRepository.saveAndFlush(transactionOne);
        Transaction transactionTwo = new Transaction(accountOne.getId(), accountThree.getId(), 10.0);
        transactionRepository.saveAndFlush(transactionTwo);
    }

    @AfterEach
    void teardownDatabase() {
        accountRepository.deleteAll();
        transactionRepository.deleteAll();
    }
}