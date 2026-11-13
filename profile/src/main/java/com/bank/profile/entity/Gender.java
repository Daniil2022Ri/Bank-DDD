package com.bank.profile.entity;

public enum Gender {
    MALE("МУЖ"),
    FEMALE("ЖЕН");

    private final String code;

    Gender(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static Gender fromCode(String code) {
        for (final Gender gender : values()) {
            if (gender.code.equals(code)) {
                return gender;
            }
        }
        throw new IllegalArgumentException("Unknown gender code: " + code);
    }
}
