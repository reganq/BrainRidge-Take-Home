package org.app.bank.exception;

public class NegativeBalanceException extends RuntimeException {
    public NegativeBalanceException(Double request) {
        super(String.format("Initial balance cannot be negative: %f", request));
    }
}