package org.app.bank.exception;

public class EmptyNameException extends RuntimeException {
    public EmptyNameException() {
        super("Account holder name must be present");
    }
}