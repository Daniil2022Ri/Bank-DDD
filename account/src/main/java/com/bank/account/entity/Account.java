package com.bank.account.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "account_details")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    private static final int MONEY_PRECISION = 20;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "passport_id", nullable = false)
    private Long passportId;

    @Column(name = "account_number", nullable = false, unique = true)
    private Long accountNumber;

    @Column(name = "bank_details_id", nullable = false, unique = true)
    private Long bankDetailsId;

    @Column(name = "money", nullable = false, precision = MONEY_PRECISION, scale = 2)
    private BigDecimal money;

    @Column(name = "negative_balance", nullable = false)
    private Boolean negativeBalance;

    @Column(name = "profile_id", nullable = false)
    private Long profileId;
}
