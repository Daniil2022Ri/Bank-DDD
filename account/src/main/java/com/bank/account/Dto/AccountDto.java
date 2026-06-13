package com.bank.account.Dto;

import com.bank.account.Entity.Currency;
import com.bank.account.Entity.Status;
import com.bank.account.Entity.Type;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {


    private Long id;
    private Long profileId;
    private int accountNumber;
    private double balanceAccount;
    private Currency currencyAccount;
    private Status statusAccount;
    private Type statusTypeAccount;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;





}

