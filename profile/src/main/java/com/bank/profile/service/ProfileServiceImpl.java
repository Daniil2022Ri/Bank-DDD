package com.bank.profile.service;

import com.bank.profile.dto.ProfileDto;
import com.bank.profile.entity.ActualRegistration;
import com.bank.profile.entity.Passport;
import com.bank.profile.entity.Profile;
import com.bank.profile.exception.DuplicateProfileException;
import com.bank.profile.exception.EntityNotFoundException;
import com.bank.profile.mapper.ProfileMapper;
import com.bank.profile.repository.ActualRegistrationRepository;
import com.bank.profile.repository.PassportRepository;
import com.bank.profile.repository.ProfileRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileServiceImpl implements ProfileService {

    private static final String PROFILE_NOT_FOUND = "Profile with id %d not found";
    private static final String PASSPORT_NOT_FOUND = "Passport with id %d not found";
    private static final String ACTUAL_REGISTRATION_NOT_FOUND = "Actual registration with id %d not found";
    private static final String INN_EXISTS = "Profile with inn %d already exists";
    private static final String SNILS_EXISTS = "Profile with snils %d already exists";

    private final ProfileRepository profileRepository;
    private final PassportRepository passportRepository;
    private final ActualRegistrationRepository actualRegistrationRepository;
    private final ProfileMapper profileMapper;

    @Override
    public ProfileDto create(ProfileDto profileDto) {
        ensureUnique(profileDto, null);
        final Passport passport = findPassport(profileDto.getPassportId());
        final ActualRegistration actualRegistration = resolveActualRegistration(profileDto.getActualRegistrationId());
        final Profile profile = profileMapper.toEntity(profileDto);
        profile.setPassport(passport);
        profile.setActualRegistration(actualRegistration);
        return profileMapper.toDto(profileRepository.save(profile));
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileDto getById(Long profileId) {
        return profileMapper.toDto(findProfile(profileId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfileDto> getAll() {
        return profileMapper.toDtoList(profileRepository.findAll());
    }

    @Override
    public ProfileDto update(Long profileId, ProfileDto profileDto) {
        final Profile profile = findProfile(profileId);
        ensureUnique(profileDto, profileId);
        profileMapper.updateEntity(profileDto, profile);
        if (profileDto.getPassportId() != null) {
            profile.setPassport(findPassport(profileDto.getPassportId()));
        }
        if (profileDto.getActualRegistrationId() != null) {
            profile.setActualRegistration(resolveActualRegistration(profileDto.getActualRegistrationId()));
        }
        return profileMapper.toDto(profileRepository.save(profile));
    }

    @Override
    public void delete(Long profileId) {
        profileRepository.delete(findProfile(profileId));
    }

    private Profile findProfile(Long profileId) {
        return profileRepository.findById(profileId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(PROFILE_NOT_FOUND, profileId)));
    }

    private Passport findPassport(Long passportId) {
        return passportRepository.findById(passportId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(PASSPORT_NOT_FOUND, passportId)));
    }

    private ActualRegistration resolveActualRegistration(Long actualRegistrationId) {
        if (actualRegistrationId == null) {
            return null;
        }
        return actualRegistrationRepository.findById(actualRegistrationId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(ACTUAL_REGISTRATION_NOT_FOUND, actualRegistrationId)));
    }

    private void ensureUnique(ProfileDto profileDto, Long profileId) {
        final Long inn = profileDto.getInn();
        if (inn != null && isInnTaken(inn, profileId)) {
            throw new DuplicateProfileException(String.format(INN_EXISTS, inn));
        }
        final Long snils = profileDto.getSnils();
        if (snils != null && isSnilsTaken(snils, profileId)) {
            throw new DuplicateProfileException(String.format(SNILS_EXISTS, snils));
        }
    }

    private boolean isInnTaken(Long inn, Long profileId) {
        if (profileId == null) {
            return profileRepository.existsByInn(inn);
        }
        return profileRepository.existsByInnAndIdNot(inn, profileId);
    }

    private boolean isSnilsTaken(Long snils, Long profileId) {
        if (profileId == null) {
            return profileRepository.existsBySnils(snils);
        }
        return profileRepository.existsBySnilsAndIdNot(snils, profileId);
    }
}
