package com.bank.account.Entity;

import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Account_User" , schema = "account_bd")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name = "profile_id" , nullable = false)
    private Long profileId;

    @Column(name = "account_Number" , nullable = false , length = 20)
    private int accountNumber;

    @Column(name = "balance_Account")
    private BigDecimal balanceAccount;

    @Column(name = "currency_Account")
    private Currency currencyAccount;

    @Column(name = "status_Account")
    private Status statusAccount;

    @Column(name = "status_Type_Account")
    private Type statusTypeAccount;

    @Column(name = "create_At" , nullable = false)
    private LocalDateTime createAt;

    @Column(name = "update_At")
    private LocalDateTime updateAt;



}
