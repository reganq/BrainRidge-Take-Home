package org.app.bank.exception;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(Long id, Double request) {
        super(String.format("Account %d has less balance than %f", id, request));
    }
}