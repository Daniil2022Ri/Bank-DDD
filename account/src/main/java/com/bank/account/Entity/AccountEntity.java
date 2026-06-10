package com.bank.account.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.bouncycastle.asn1.cms.TimeStampedData;

@Entity
@Table(name = "Account_User" , schema = "Account")
@Data
@AllArgsConstructor
public class AccountEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column(name = "profile_id")
    private Long profileId;

    @Column(name = "account_Number" , nullable = false)
    private int accountNumber;

    @Column(name = "balance_Account")
    private double balanceAccount;

    @Column(name = "currency_Account")
    private Currency currencyAccount;

    @Column(name = "status_Account")
    private Status statusAccount;

    @Column(name = "status_Type_Account")
    private Type statusTypeAccount;

    @Column(name = "create_At" , nullable = false)
    private TimeStampedData createAt;

    @Column(name = "update_At")
    private TimeStampedData updateAt;




}
