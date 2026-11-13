package com.bank.profile.service;

import com.bank.profile.dto.PassportDto;
import com.bank.profile.entity.Passport;
import com.bank.profile.entity.Registration;
import com.bank.profile.exception.EntityNotFoundException;
import com.bank.profile.mapper.PassportMapper;
import com.bank.profile.repository.PassportRepository;
import com.bank.profile.repository.RegistrationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PassportServiceImpl implements PassportService {

    private static final String PASSPORT_NOT_FOUND = "Passport with id %d not found";
    private static final String REGISTRATION_NOT_FOUND = "Registration with id %d not found";

    private final PassportRepository passportRepository;
    private final RegistrationRepository registrationRepository;
    private final PassportMapper passportMapper;

    @Override
    public PassportDto create(PassportDto passportDto) {
        final Registration registration = findRegistration(passportDto.getRegistrationId());
        final Passport passport = passportMapper.toEntity(passportDto);
        passport.setRegistration(registration);
        return passportMapper.toDto(passportRepository.save(passport));
    }

    @Override
    @Transactional(readOnly = true)
    public PassportDto getById(Long passportId) {
        return passportMapper.toDto(findPassport(passportId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PassportDto> getAll() {
        return passportMapper.toDtoList(passportRepository.findAll());
    }

    @Override
    public PassportDto update(Long passportId, PassportDto passportDto) {
        final Passport passport = findPassport(passportId);
        passportMapper.updateEntity(passportDto, passport);
        if (passportDto.getRegistrationId() != null) {
            passport.setRegistration(findRegistration(passportDto.getRegistrationId()));
        }
        return passportMapper.toDto(passportRepository.save(passport));
    }

    @Override
    public void delete(Long passportId) {
        passportRepository.delete(findPassport(passportId));
    }

    private Passport findPassport(Long passportId) {
        return passportRepository.findById(passportId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(PASSPORT_NOT_FOUND, passportId)));
    }

    private Registration findRegistration(Long registrationId) {
        return registrationRepository.findById(registrationId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(REGISTRATION_NOT_FOUND, registrationId)));
    }
}
