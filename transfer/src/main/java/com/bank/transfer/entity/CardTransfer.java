package com.bank.transfer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "card_transfer")
@Getter
@Setter
@NoArgsConstructor
public class CardTransfer extends AbstractTransfer {

    @Column(name = "card_number", nullable = false, unique = true)
    private Long cardNumber;
}
