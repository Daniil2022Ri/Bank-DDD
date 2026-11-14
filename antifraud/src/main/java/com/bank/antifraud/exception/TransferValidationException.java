package com.bank.antifraud.exception;

public class TransferValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TransferValidationException(String message) {
        super(message);
    }
}
