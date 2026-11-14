package com.bank.profile.dto;

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
public class AccountDetailsDto {

    private Long id;

    @NotNull(message = "accountId is required")
    @Positive(message = "accountId must be positive")
    private Long accountId;

    @NotNull(message = "profileId is required")
    @Positive(message = "profileId must be positive")
    private Long profileId;
}
