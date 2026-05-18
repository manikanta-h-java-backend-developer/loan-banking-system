package com.mani.loan.banking.system.exception;

import lombok.Getter;

@Getter
public class DuplicateResourceException extends RuntimeException {
    private final String code;

    public DuplicateResourceException(String message) {
        super(message);
        this.code = "DUPLICATE_RESOURCE";
    }

}