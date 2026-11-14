package com.bank.transfer.dto;

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
public class AccountTransferDto {

    private static final int MONEY_INTEGER_DIGITS = 18;
    private static final int MONEY_FRACTION_DIGITS = 2;

    private Long id;

    @NotNull(message = "accountNumber is required")
    @Positive(message = "accountNumber must be positive")
    private Long accountNumber;

    @NotNull(message = "amount is required")
    @Positive(message = "amount must be positive")
    @Digits(integer = MONEY_INTEGER_DIGITS, fraction = MONEY_FRACTION_DIGITS)
    private BigDecimal amount;

    private String purpose;

    @NotNull(message = "accountDetailsId is required")
    @Positive(message = "accountDetailsId must be positive")
    private Long accountDetailsId;
}
