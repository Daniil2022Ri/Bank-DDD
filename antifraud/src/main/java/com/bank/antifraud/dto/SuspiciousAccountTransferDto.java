package com.bank.antifraud.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class SuspiciousAccountTransferDto {

    private Long id;

    @NotNull(message = "transferId is required")
    @Positive(message = "transferId must be positive")
    private Long transferId;

    @NotNull(message = "blocked is required")
    private Boolean blocked;

    @NotNull(message = "suspicious is required")
    private Boolean suspicious;

    private String blockedReason;

    @NotBlank(message = "suspiciousReason is required")
    private String suspiciousReason;
}
