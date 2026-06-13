package com.bank.account.Dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreditDetailsDto {

    private Long id;
    private Long accountId;
    private double creditLimit;
    private double interestRate;
    private LocalDateTime date;

}

