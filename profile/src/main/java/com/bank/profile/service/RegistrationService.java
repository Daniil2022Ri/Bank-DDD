package com.bank.profile.service;

import com.bank.profile.dto.RegistrationDto;
import java.util.List;

public interface RegistrationService {

    RegistrationDto create(RegistrationDto registrationDto);

    RegistrationDto getById(Long registrationId);

    List<RegistrationDto> getAll();

    RegistrationDto update(Long registrationId, RegistrationDto registrationDto);

    void delete(Long registrationId);
}
