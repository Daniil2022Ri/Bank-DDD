package com.bank.publicinfo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bank_details")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bik", nullable = false, unique = true)
    private Long bik;

    @Column(name = "inn", nullable = false, unique = true)
    private Long inn;

    @Column(name = "kpp", nullable = false, unique = true)
    private Long kpp;

    @Column(name = "cor_account", nullable = false, unique = true)
    private Integer corAccount;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "joint_stock_company", nullable = false)
    private String jointStockCompany;

    @Column(name = "name", nullable = false)
    private String name;
}
