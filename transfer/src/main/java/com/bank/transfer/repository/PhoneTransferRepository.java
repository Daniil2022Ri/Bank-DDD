package com.bank.transfer.repository;

import com.bank.transfer.entity.PhoneTransfer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneTransferRepository extends JpaRepository<PhoneTransfer, Long> {
}
