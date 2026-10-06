package org.app.bank.service;

import java.util.*;

import org.app.bank.entity.Account;
import org.app.bank.entity.Transaction;
import org.app.bank.exception.AccountNotFoundException;
import org.app.bank.exception.InsufficientFundsException;
import org.app.bank.repository.AccountRepository;
import org.app.bank.repository.TransactionRepository;
import org.app.bank.service.MakeTransactionRequest;
import org.app.bank.service.MakeTransactionService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MakeTransactionTest {

    // mocked databases
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private MakeTransactionService makeTransactionService;

    Account accountOne;
    Account accountTwo;
    Account accountThree;

    @Test
    void testMakeValidTransaction() {
        MakeTransactionRequest request = new MakeTransactionRequest(accountOne.getId(), accountTwo.getId(), 5.0);

        try {
            makeTransactionService.makeTransaction(request);
        }
        catch (RuntimeException re) {
            fail(String.format("Runtime Exception Thrown: %s", re.getMessage()));
        }

        // verify that the correct transaction object was created, and that the account objects were modified
        List<Transaction> transactions = transactionRepository.findAllByFromAccount(accountOne.getId());
        assertEquals(1, transactions.size());
        assertEquals(accountOne.getId(), transactions.get(0).getFromAccount());
        assertEquals(accountTwo.getId(), transactions.get(0).getToAccount());
        assertEquals(5.0, transactions.get(0).getAmount());

        Account accountOne = accountRepository.findById(1L).orElse(null);
        assertNotNull(accountOne);
        assertEquals(5.0, accountOne.getBalance());

        Account accountTwo = accountRepository.findById(2L).orElse(null);
        assertNotNull(accountTwo);
        assertEquals(25.0, accountTwo.getBalance());

        // check no other changes were made to the database
        assertEquals(3, accountRepository.findAll().size());
        assertEquals(4, transactionRepository.findAll().size());
    }

    @Test
    void testMakeInvalidTransactionToUserNotExists() {
        // check we get the correct exception from invalid
        MakeTransactionRequest request = new MakeTransactionRequest(5L, accountOne.getId(), 1.0);
        assertThrows(AccountNotFoundException.class, 
                     () -> makeTransactionService.makeTransaction(request));

        // check that no changes made to the database
        assertEquals(3, accountRepository.findAll().size());
        assertEquals(3, transactionRepository.findAll().size());
    }

    @Test
    void testMakeInvalidTransactionFromUserNotExists() {
        // check we get the correct exception to invalid
        MakeTransactionRequest request = new MakeTransactionRequest(accountOne.getId(), 5L, 1.0);
        assertThrows(AccountNotFoundException.class, 
                     () -> makeTransactionService.makeTransaction(request));

        // check that no changes made to the database
        assertEquals(3, accountRepository.findAll().size());
        assertEquals(3, transactionRepository.findAll().size());
    }

    @Test
    void testMakeInvalidTransactionIsufficientBalance() {
        // check we get the correct exception
        MakeTransactionRequest request = new MakeTransactionRequest(accountOne.getId(), accountTwo.getId(), 20.0);
        assertThrows(InsufficientFundsException.class,
                     () -> makeTransactionService.makeTransaction(request));

        // check that no changes made to the database
        assertEquals(3, accountRepository.findAll().size());
        assertEquals(3, transactionRepository.findAll().size());
    }

    @BeforeEach
    void initializeDatabase() {
        Account accountOne = new Account("one", 10.0);
        Account accountTwo = new Account("two", 20.0);
        Account accountThree = new Account("three", 5.0);

        Transaction createOne = new Transaction(0L, accountOne.getId(), 10.0);
        Transaction createTwo = new Transaction(0L, accountTwo.getId(), 20.0);
        Transaction createThree = new Transaction(0L, accountThree.getId(), 5.0);

        accountRepository.save(accountOne);
        transactionRepository.save(createOne);
        accountRepository.save(accountTwo);
        transactionRepository.save(createTwo);
        accountRepository.save(accountThree);
        transactionRepository.save(createThree);
    }

    @AfterEach
    void teardownDatabase() {
        accountRepository.deleteAll();
        transactionRepository.deleteAll();
    }
}