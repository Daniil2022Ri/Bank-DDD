package com.bank.account.Repository;

import com.bank.account.Entity.CreditDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditDitailsRepository extends JpaRepository<CreditDetailsEntity, Long > {
}
