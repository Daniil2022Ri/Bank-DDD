package com.bank.account.Dto;

import com.bank.account.Entity.Currency;
import com.bank.account.Entity.Status;
import com.bank.account.Entity.Type;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bouncycastle.asn1.cms.TimeStampedData;

@Data
@AllArgsConstructor
public class AccountDto {


    private Long id;
    private Long profileId;
    private int accountNumber;
    private double balanceAccount;
    private Currency currencyAccount;
    private Status statusAccount;
    private Type statusTypeAccount;
    private TimeStampedData createAt;
    private TimeStampedData updateAt;





}

