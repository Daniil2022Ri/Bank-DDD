package com.bank.history.entity;

public enum HistorySource {
    TRANSFER("transfer"),
    PROFILE("profile"),
    ACCOUNT("account"),
    ANTI_FRAUD("anti_fraud"),
    PUBLIC_BANK_INFO("public_bank_info"),
    AUTHORIZATION("authorization");

    private final String code;

    HistorySource(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static HistorySource fromCode(String code) {
        for (final HistorySource source : values()) {
            if (source.code.equalsIgnoreCase(code)) {
                return source;
            }
        }
        throw new IllegalArgumentException("Unknown microservice: " + code);
    }
}
