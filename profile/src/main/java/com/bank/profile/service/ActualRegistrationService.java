package com.bank.profile.service;

import com.bank.profile.dto.ActualRegistrationDto;
import java.util.List;

public interface ActualRegistrationService {

    ActualRegistrationDto create(ActualRegistrationDto registrationDto);

    ActualRegistrationDto getById(Long registrationId);

    List<ActualRegistrationDto> getAll();

    ActualRegistrationDto update(Long registrationId, ActualRegistrationDto registrationDto);

    void delete(Long registrationId);
}
