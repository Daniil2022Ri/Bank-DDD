package com.bank.transfer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "phone_transfer")
@Getter
@Setter
@NoArgsConstructor
public class PhoneTransfer extends AbstractTransfer {

    @Column(name = "phone_number", nullable = false)
    private Long phoneNumber;
}
