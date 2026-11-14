package com.bank.account.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {

    private static final int MONEY_INTEGER_DIGITS = 18;
    private static final int MONEY_FRACTION_DIGITS = 2;

    private Long id;

    @NotNull(message = "passportId is required")
    @Positive(message = "passportId must be positive")
    private Long passportId;

    @NotNull(message = "accountNumber is required")
    @Positive(message = "accountNumber must be positive")
    private Long accountNumber;

    @NotNull(message = "bankDetailsId is required")
    @Positive(message = "bankDetailsId must be positive")
    private Long bankDetailsId;

    @NotNull(message = "money is required")
    @Digits(integer = MONEY_INTEGER_DIGITS, fraction = MONEY_FRACTION_DIGITS)
    private BigDecimal money;

    private Boolean negativeBalance;

    @NotNull(message = "profileId is required")
    @Positive(message = "profileId must be positive")
    private Long profileId;
}
