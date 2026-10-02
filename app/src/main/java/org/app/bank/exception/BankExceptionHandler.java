package org.app.bank.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/*
 * Class responsible for sending error responses to clients
 */
@RestControllerAdvice
public class BankExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFound(AccountNotFoundException exception, 
        HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(response);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientFunds(InsufficientFundsException exception, 
        HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(response);
    }

    @ExceptionHandler(NegativeBalanceException.class)
    public ResponseEntity<ErrorResponse> handleNegativeBalance(NegativeBalanceException exception, 
        HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(response);
    }
}