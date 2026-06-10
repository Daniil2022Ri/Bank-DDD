package com.bank.account.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

import java.text.DateFormat;

@Entity
@Table(name = "Credit_ditails", schema = "Credit_Account_Details")
public class CreditDitalilsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name  = "Credit_account_Id")
    private Long accountId;

    @Column(name  = "Credit_limit" , nullable = false)
    private double creditLimit;

    @Column(name  = "Credit_Interest", nullable = false)
    private double interestRate;

    @Column(name  = "Credit_date")
    private DateFormat date;
}

