package org.app.bank.service;

import java.util.*;

import org.app.bank.entity.Account;
import org.app.bank.entity.Transaction;
import org.app.bank.exception.EmptyNameException;
import org.app.bank.exception.NegativeBalanceException;
import org.app.bank.repository.AccountRepository;
import org.app.bank.repository.TransactionRepository;
import org.app.bank.service.CreateAccountRequest;
import org.app.bank.service.CreateAccountResponse;
import org.app.bank.service.CreateAccountService;

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
public class CreateAccountTest {

    // mocked databases
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private CreateAccountService createAccountService;

    // mocked account repository
    private long nextAccountId = 1L;
    private Map<Long, Account> accountMap = new HashMap<>();

    // mocked transaction repository
    private long nextTransactionId = 1L;
    private Map<Long, Transaction> transactionMap = new HashMap<>();

    @Test
    void testCreateValidUser() {
        CreateAccountRequest request = new CreateAccountRequest("Regan", 10.5);

        CreateAccountResponse response = null;
        try {
            response = createAccountService.createAccount(request);
        }
        catch (RuntimeException re) {
            fail(String.format("Runtime Exception Thrown: %s", re.getMessage()));
        }

        assertNotNull(response);
        assertEquals(1L, response.id());

        // verify that the correct objects (account, transaction) were created
        Account newAccount = accountRepository.findById(response.id()).orElse(null);
        assertNotNull(newAccount);
        assertEquals("Regan", newAccount.getName());
        assertEquals(10.5, newAccount.getBalance());
        assertEquals(response.id(), newAccount.getId());

        List<Transaction> allTransactions = transactionRepository.findAll();
        assertEquals(1, allTransactions.size());

        Transaction transaction = allTransactions.get(0);
        assertEquals(response.id(), transaction.getToAccount());
        assertEquals(10.5, transaction.getAmount());

        // check that no other changes made to the database
        assertEquals(1, accountRepository.findAll().size());
        assertEquals(1, transactionRepository.findAll().size());
    }

    @Test
    void testCreateInvalidUserNoName() {
        CreateAccountRequest request = new CreateAccountRequest("", 1.0);

        // check we get the correct exception
        assertThrows(EmptyNameException.class,
                     () -> createAccountService.createAccount(request));

        // check that no changes made to the database
        assertEquals(0, accountRepository.findAll().size());
        assertEquals(0, transactionRepository.findAll().size());
    }

    @Test
    void testCreateInvalidUserNegativeBalance() {
        CreateAccountRequest request = new CreateAccountRequest("Regan", -1.0);

        // check we get the correct exception
        assertThrows(NegativeBalanceException.class,
                     () -> createAccountService.createAccount(request));

        // check that no changes made to the database
        assertEquals(0, accountRepository.findAll().size());
        assertEquals(0, transactionRepository.findAll().size());
    }

    // used to setup IDs in the non-failing tests
    @BeforeEach
    void mockDatabase() {
        // mocking account repository
        lenient().when(accountRepository.saveAndFlush(any(Account.class))).thenAnswer(invocation -> {
            Account account = invocation.getArgument(0);

            if (account.getId() == null) {
                account.setId(nextAccountId++);
            }

            accountMap.put(account.getId(), account);

            return account;
        });

        lenient().when(accountRepository.findById(any(Long.class))).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);

            return (accountMap.containsKey(id)) ? Optional.of(accountMap.get(id)) 
                                                : Optional.empty();
        });

        when(accountRepository.findAll()).thenAnswer(invocation -> {
            return new ArrayList<>(accountMap.values());
        });

        // mocking transaction repository
        lenient().when(transactionRepository.saveAndFlush(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction transaction = invocation.getArgument(0);

            if (transaction.getId() == null) {
                transaction.setId(nextTransactionId++);
            }

            transactionMap.put(transaction.getId(), transaction);

            return transaction;
        });

        when(transactionRepository.findAll()).thenAnswer(invocation -> {
            return new ArrayList<>(transactionMap.values());
        });
    }
}