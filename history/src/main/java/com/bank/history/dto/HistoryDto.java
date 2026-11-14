package com.bank.history.dto;

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
public class HistoryDto {

    private Long id;

    @Positive(message = "transferAuditId must be positive")
    private Long transferAuditId;

    @Positive(message = "profileAuditId must be positive")
    private Long profileAuditId;

    @Positive(message = "accountAuditId must be positive")
    private Long accountAuditId;

    @Positive(message = "antiFraudAuditId must be positive")
    private Long antiFraudAuditId;

    @Positive(message = "publicBankInfoAuditId must be positive")
    private Long publicBankInfoAuditId;

    @Positive(message = "authorizationAuditId must be positive")
    private Long authorizationAuditId;
}
