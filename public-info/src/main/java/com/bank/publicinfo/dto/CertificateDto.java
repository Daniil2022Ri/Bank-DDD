package com.bank.publicinfo.dto;

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
public class CertificateDto {

    private Long id;

    @NotNull(message = "photo is required")
    private byte[] photo;

    @NotNull(message = "bankDetailsId is required")
    @Positive(message = "bankDetailsId must be positive")
    private Long bankDetailsId;
}
