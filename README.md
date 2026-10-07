# BrainRidge-Take-Home
A simple banking transactions RESTful API, implemented with Spring Boot in Java

# Requirements
To run this project, you will need the following:
- Gradle v9.8.0
- Java 21

# Running the Program
You can run the program by executing ```./gradlew build``` and then ```./gradlew bootRun``` from the root directory. This automatically runs the test suite. If you'd prefer to build the project without running the tests, execute ```./gradlew assemble``` instead of the build command.

## The API
### POST /createAccount
Creates an account with a given initial balance. Records account creation as a transaction.

#### Request Parameters
- name (string): The name for the account-holder. Cannot be an empty string.
- balance (floating-point): The initial balance in the account. Cannot be negative.

#### Response
```
{
  "Created account 1 for Bob with initial balance 20.000000"
}
```

#### Error Handling
Error Code | Description
--- | ---
400 | Bad request - entered an empty string, a negative balance, or did not include a parameter.

### POST /makeTransaction
Makes a transaction between two accounts, removing the amount from one and adding it to the other's balance. Records the operation as a transaction.

#### Request Parameters
- fromAccount (long): The ID for the sender account.
- toAccount (long): The ID for the receiver account.
- balance (floating-point): The amount to be sent. The sending account must have at least this balance.

#### Response
```
{
  "Transferred 15.000000 from account 2 to account 1"
}
```

#### Error Handling
Error Code | Description
--- | ---
400 | Bad request - Tried to send more than sender account contains, or did not include a parameter.
404 | Not found - Either the sender or receiver account does not exist.

### GET /getTransactionHistory
Returns the transaction history for a given account.

#### Request Parameters
- account (long): The ID of the account to get the history for.

#### Response
```
{
  [
    "INITIAL BALANCE = 30.000000",
    "TRANSFER OUT 15.000000 TO 1, NEW BALANCE = 15.000000",
    "TRANSFER IN 14.000000 FROM 3, NEW BALANCE = 29.000000"
  ]
}
```

#### Error Handling
Error Code | Description
--- | ---
400 | Bad request - Did not include the account parameter
404 | Not found - The requested account does not exist.

# Assumptions
- I assumed that requests come in one-at-a-time. This is not a realistic assumption. In production, we would likely have concurrent requests. This could lead to synchronization bugs (e.g. two transactions transferring out of the same account at once). If this assumption didn't hold, I'd need to add synchronization such as locking when accessing any shared resources like accounts.
- I assumed that the volume of data being processed is small enough to comfortably fit into memory. Once again, this is not realistic and we would likely see so much data that memory would be overwhelmed. If this assumption didn't hold, I'd need to use a more resilient database (e.g. MongoDB, SQL)
