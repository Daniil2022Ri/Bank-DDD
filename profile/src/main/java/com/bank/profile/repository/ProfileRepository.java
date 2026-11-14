package com.bank.profile.repository;

import com.bank.profile.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    boolean existsByInn(Long inn);

    boolean existsBySnils(Long snils);

    boolean existsByInnAndIdNot(Long inn, Long id);

    boolean existsBySnilsAndIdNot(Long snils, Long id);
}
