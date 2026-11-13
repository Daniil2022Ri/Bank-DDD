package com.bank.profile.service;

import com.bank.profile.dto.ActualRegistrationDto;
import com.bank.profile.entity.ActualRegistration;
import com.bank.profile.exception.EntityNotFoundException;
import com.bank.profile.mapper.ActualRegistrationMapper;
import com.bank.profile.repository.ActualRegistrationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ActualRegistrationServiceImpl implements ActualRegistrationService {

    private static final String ACTUAL_REGISTRATION_NOT_FOUND = "Actual registration with id %d not found";

    private final ActualRegistrationRepository actualRegistrationRepository;
    private final ActualRegistrationMapper actualRegistrationMapper;

    @Override
    public ActualRegistrationDto create(ActualRegistrationDto registrationDto) {
        final ActualRegistration registration = actualRegistrationMapper.toEntity(registrationDto);
        return actualRegistrationMapper.toDto(actualRegistrationRepository.save(registration));
    }

    @Override
    @Transactional(readOnly = true)
    public ActualRegistrationDto getById(Long registrationId) {
        return actualRegistrationMapper.toDto(findRegistration(registrationId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActualRegistrationDto> getAll() {
        return actualRegistrationMapper.toDtoList(actualRegistrationRepository.findAll());
    }

    @Override
    public ActualRegistrationDto update(Long registrationId, ActualRegistrationDto registrationDto) {
        final ActualRegistration registration = findRegistration(registrationId);
        actualRegistrationMapper.updateEntity(registrationDto, registration);
        return actualRegistrationMapper.toDto(actualRegistrationRepository.save(registration));
    }

    @Override
    public void delete(Long registrationId) {
        actualRegistrationRepository.delete(findRegistration(registrationId));
    }

    private ActualRegistration findRegistration(Long registrationId) {
        return actualRegistrationRepository.findById(registrationId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(ACTUAL_REGISTRATION_NOT_FOUND, registrationId)));
    }
}
