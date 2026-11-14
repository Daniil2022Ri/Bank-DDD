package com.bank.account.exception;

public class AuditSerializationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AuditSerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
