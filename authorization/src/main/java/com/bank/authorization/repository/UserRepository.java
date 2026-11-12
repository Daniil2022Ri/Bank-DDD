package com.bank.authorization.repository;

import com.bank.authorization.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByProfileId(Long profileId);

    boolean existsByProfileId(Long profileId);
}
