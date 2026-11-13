package com.bank.profile.service;

import com.bank.profile.dto.RegistrationDto;
import com.bank.profile.entity.Registration;
import com.bank.profile.exception.EntityNotFoundException;
import com.bank.profile.mapper.RegistrationMapper;
import com.bank.profile.repository.RegistrationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RegistrationServiceImpl implements RegistrationService {

    private static final String REGISTRATION_NOT_FOUND = "Registration with id %d not found";

    private final RegistrationRepository registrationRepository;
    private final RegistrationMapper registrationMapper;

    @Override
    public RegistrationDto create(RegistrationDto registrationDto) {
        final Registration registration = registrationMapper.toEntity(registrationDto);
        return registrationMapper.toDto(registrationRepository.save(registration));
    }

    @Override
    @Transactional(readOnly = true)
    public RegistrationDto getById(Long registrationId) {
        return registrationMapper.toDto(findRegistration(registrationId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistrationDto> getAll() {
        return registrationMapper.toDtoList(registrationRepository.findAll());
    }

    @Override
    public RegistrationDto update(Long registrationId, RegistrationDto registrationDto) {
        final Registration registration = findRegistration(registrationId);
        registrationMapper.updateEntity(registrationDto, registration);
        return registrationMapper.toDto(registrationRepository.save(registration));
    }

    @Override
    public void delete(Long registrationId) {
        registrationRepository.delete(findRegistration(registrationId));
    }

    private Registration findRegistration(Long registrationId) {
        return registrationRepository.findById(registrationId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(REGISTRATION_NOT_FOUND, registrationId)));
    }
}
