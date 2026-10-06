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
        assertEquals(-5.0, response.transactions().get(2).getValue());
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
        accountOne = new Account("one", 10.0);
        accountTwo = new Account("two", 20.0);
        accountThree = new Account("three", 5.0);

        Transaction createOne = new Transaction(0L, accountOne.getId(), 10.0);
        Transaction createTwo = new Transaction(0L, accountTwo.getId(), 20.0);
        Transaction createThree = new Transaction(0L, accountThree.getId(), 5.0);
        Transaction transactionOne = new Transaction(accountTwo.getId(), accountOne.getId(), 15.0);
        Transaction transactionTwo = new Transaction(accountOne.getId(), accountThree.getId(), 10.0);

        accountRepository.save(accountOne);
        transactionRepository.save(createOne);
        accountRepository.save(accountTwo);
        transactionRepository.save(createTwo);
        accountRepository.save(accountThree);
        transactionRepository.save(createThree);

        transactionRepository.save(transactionOne);
        transactionRepository.save(transactionTwo);
    }

    @AfterEach
    void teardownDatabase() {
        accountRepository.deleteAll();
        transactionRepository.deleteAll();
    }
}