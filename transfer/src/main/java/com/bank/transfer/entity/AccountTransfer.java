package com.bank.transfer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "account_transfer")
@Getter
@Setter
@NoArgsConstructor
public class AccountTransfer extends AbstractTransfer {

    @Column(name = "account_number", nullable = false, unique = true)
    private Long accountNumber;
}
