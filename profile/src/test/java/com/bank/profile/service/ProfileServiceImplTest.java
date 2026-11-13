package com.bank.profile.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bank.profile.dto.ProfileDto;
import com.bank.profile.entity.ActualRegistration;
import com.bank.profile.entity.Passport;
import com.bank.profile.entity.Profile;
import com.bank.profile.exception.DuplicateProfileException;
import com.bank.profile.exception.EntityNotFoundException;
import com.bank.profile.mapper.ProfileMapperImpl;
import com.bank.profile.repository.ActualRegistrationRepository;
import com.bank.profile.repository.PassportRepository;
import com.bank.profile.repository.ProfileRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProfileServiceImplTest {

    private static final Long PROFILE_ID = 10L;
    private static final Long PASSPORT_ID = 1L;
    private static final Long ACTUAL_REGISTRATION_ID = 2L;
    private static final Long PHONE_NUMBER = 79990000000L;
    private static final Long INN = 123456789012L;
    private static final Long SNILS = 12345678901L;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private PassportRepository passportRepository;

    @Mock
    private ActualRegistrationRepository actualRegistrationRepository;

    private ProfileServiceImpl profileService;

    @BeforeEach
    void setUp() {
        profileService = new ProfileServiceImpl(profileRepository, passportRepository,
                actualRegistrationRepository, new ProfileMapperImpl());
    }

    private ProfileDto profileDto() {
        return ProfileDto.builder()
                .phoneNumber(PHONE_NUMBER)
                .email("user@mail.ru")
                .nameOnCard("IVAN IVANOV")
                .passportId(PASSPORT_ID)
                .build();
    }

    private Passport passport() {
        return Passport.builder().id(PASSPORT_ID).build();
    }

    private ActualRegistration actualRegistration() {
        final ActualRegistration registration = new ActualRegistration();
        registration.setId(ACTUAL_REGISTRATION_ID);
        registration.setCountry("Россия");
        registration.setIndex(101000L);
        return registration;
    }

    @Test
    @DisplayName("create: сохраняет профиль с паспортом и фактической регистрацией")
    void createShouldSaveProfile() {
        final ProfileDto request = profileDto();
        request.setActualRegistrationId(ACTUAL_REGISTRATION_ID);
        when(passportRepository.findById(PASSPORT_ID)).thenReturn(Optional.of(passport()));
        when(actualRegistrationRepository.findById(ACTUAL_REGISTRATION_ID))
                .thenReturn(Optional.of(actualRegistration()));
        when(profileRepository.save(any(Profile.class))).thenAnswer(invocation -> {
            final Profile profile = invocation.getArgument(0);
            profile.setId(PROFILE_ID);
            return profile;
        });

        final ProfileDto result = profileService.create(request);

        assertThat(result.getId()).isEqualTo(PROFILE_ID);
        assertThat(result.getPassportId()).isEqualTo(PASSPORT_ID);
        assertThat(result.getActualRegistrationId()).isEqualTo(ACTUAL_REGISTRATION_ID);
    }

    @Test
    @DisplayName("create: сохраняет профиль без фактической регистрации")
    void createShouldSaveProfileWithoutActualRegistration() {
        when(passportRepository.findById(PASSPORT_ID)).thenReturn(Optional.of(passport()));
        when(profileRepository.save(any(Profile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final ProfileDto result = profileService.create(profileDto());

        assertThat(result.getActualRegistrationId()).isNull();
    }

    @Test
    @DisplayName("create: бросает EntityNotFoundException для неизвестного паспорта")
    void createShouldThrowWhenPassportMissing() {
        when(passportRepository.findById(PASSPORT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> profileService.create(profileDto()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(String.valueOf(PASSPORT_ID));
        verify(profileRepository, never()).save(any(Profile.class));
    }

    @Test
    @DisplayName("create: запрещает дубликат ИНН")
    void createShouldRejectDuplicateInn() {
        final ProfileDto request = profileDto();
        request.setInn(INN);
        when(profileRepository.existsByInn(INN)).thenReturn(true);

        assertThatThrownBy(() -> profileService.create(request))
                .isInstanceOf(DuplicateProfileException.class)
                .hasMessageContaining(String.valueOf(INN));
    }

    @Test
    @DisplayName("create: запрещает дубликат СНИЛС")
    void createShouldRejectDuplicateSnils() {
        final ProfileDto request = profileDto();
        request.setSnils(SNILS);
        when(profileRepository.existsBySnils(SNILS)).thenReturn(true);

        assertThatThrownBy(() -> profileService.create(request))
                .isInstanceOf(DuplicateProfileException.class)
                .hasMessageContaining(String.valueOf(SNILS));
    }

    @Test
    @DisplayName("getById: возвращает профиль")
    void getByIdShouldReturnProfile() {
        when(profileRepository.findById(PROFILE_ID)).thenReturn(Optional.of(Profile.builder()
                .id(PROFILE_ID)
                .phoneNumber(PHONE_NUMBER)
                .passport(passport())
                .build()));

        final ProfileDto result = profileService.getById(PROFILE_ID);

        assertThat(result.getId()).isEqualTo(PROFILE_ID);
        assertThat(result.getPassportId()).isEqualTo(PASSPORT_ID);
    }

    @Test
    @DisplayName("getById: бросает EntityNotFoundException")
    void getByIdShouldThrowWhenMissing() {
        when(profileRepository.findById(PROFILE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> profileService.getById(PROFILE_ID))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("getAll: возвращает список профилей")
    void getAllShouldReturnProfiles() {
        when(profileRepository.findAll()).thenReturn(List.of(Profile.builder()
                .id(PROFILE_ID)
                .phoneNumber(PHONE_NUMBER)
                .passport(passport())
                .build()));

        assertThat(profileService.getAll()).hasSize(1);
    }

    @Test
    @DisplayName("update: обновляет профиль")
    void updateShouldModifyProfile() {
        final Profile existing = Profile.builder()
                .id(PROFILE_ID)
                .phoneNumber(PHONE_NUMBER)
                .passport(passport())
                .build();
        when(profileRepository.findById(PROFILE_ID)).thenReturn(Optional.of(existing));
        when(passportRepository.findById(PASSPORT_ID)).thenReturn(Optional.of(passport()));
        when(profileRepository.save(existing)).thenReturn(existing);

        final ProfileDto request = profileDto();
        request.setPhoneNumber(79991112233L);
        final ProfileDto result = profileService.update(PROFILE_ID, request);

        assertThat(result.getPhoneNumber()).isEqualTo(79991112233L);
    }

    @Test
    @DisplayName("delete: удаляет профиль")
    void deleteShouldRemoveProfile() {
        final Profile existing = Profile.builder().id(PROFILE_ID).build();
        when(profileRepository.findById(PROFILE_ID)).thenReturn(Optional.of(existing));

        profileService.delete(PROFILE_ID);

        verify(profileRepository).delete(existing);
    }
}
