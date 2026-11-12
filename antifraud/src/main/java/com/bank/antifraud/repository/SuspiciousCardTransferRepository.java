package com.bank.antifraud.repository;

import com.bank.antifraud.entity.SuspiciousCardTransfer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SuspiciousCardTransferRepository extends JpaRepository<SuspiciousCardTransfer, Long> {

    boolean existsByTransferId(Long transferId);
}
