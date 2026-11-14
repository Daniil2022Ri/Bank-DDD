package com.bank.profile.exception;

public class DuplicateProfileException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DuplicateProfileException(String message) {
        super(message);
    }
}
