package com.mani.loan.banking.system.exception;

public class LoanNotFoundException extends RuntimeException {
    public LoanNotFoundException(String message) {
        super(message);
    }
}
