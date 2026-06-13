package com.bank.account.Repository;

import com.bank.account.Entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<AccountEntity , Long> {

    @Override
    Optional<AccountEntity> findById(Long aLong);

    List<AccountEntity> findByProfileId(Long profileId);
}
