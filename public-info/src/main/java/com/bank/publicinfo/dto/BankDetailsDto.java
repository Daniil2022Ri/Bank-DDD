package com.bank.publicinfo.dto;

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
public class BankDetailsDto {

    private Long id;

    @NotNull(message = "bik is required")
    @Positive(message = "bik must be positive")
    private Long bik;

    @NotNull(message = "inn is required")
    @Positive(message = "inn must be positive")
    private Long inn;

    @NotNull(message = "kpp is required")
    @Positive(message = "kpp must be positive")
    private Long kpp;

    @NotNull(message = "corAccount is required")
    @Positive(message = "corAccount must be positive")
    private Integer corAccount;

    @NotBlank(message = "city is required")
    private String city;

    @NotBlank(message = "jointStockCompany is required")
    private String jointStockCompany;

    @NotBlank(message = "name is required")
    private String name;
}
