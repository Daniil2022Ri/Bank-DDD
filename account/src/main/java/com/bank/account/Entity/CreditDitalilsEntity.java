package com.bank.account.Entity;

import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "Credit_ditails", schema = "account_db")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreditDitalilsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @Column(name  = "Credit_account_Id")
    private Long accountId;

    @Column(name  = "Credit_limit" , nullable = false)
    private double creditLimit;

    @Column(name  = "Credit_Interest", nullable = false)
    private double interestRate;

    @Column(name  = "Credit_date")
    private LocalDateTime date;
}

