package com.bank.profile.repository;

import com.bank.profile.entity.AccountDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountDetailsRepository extends JpaRepository<AccountDetails, Long> {
}
