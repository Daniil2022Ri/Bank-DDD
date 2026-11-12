package com.bank.account.repository;

import com.bank.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByAccountNumber(Long accountNumber);

    boolean existsByBankDetailsId(Long bankDetailsId);

    boolean existsByAccountNumberAndIdNot(Long accountNumber, Long id);

    boolean existsByBankDetailsIdAndIdNot(Long bankDetailsId, Long id);
}
