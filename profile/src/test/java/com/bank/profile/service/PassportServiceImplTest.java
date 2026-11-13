package com.bank.profile.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bank.profile.dto.PassportDto;
import com.bank.profile.entity.Gender;
import com.bank.profile.entity.Passport;
import com.bank.profile.entity.Registration;
import com.bank.profile.exception.EntityNotFoundException;
import com.bank.profile.mapper.PassportMapperImpl;
import com.bank.profile.repository.PassportRepository;
import com.bank.profile.repository.RegistrationRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PassportServiceImplTest {

    private static final Long PASSPORT_ID = 1L;
    private static final Long REGISTRATION_ID = 2L;

    @Mock
    private PassportRepository passportRepository;

    @Mock
    private RegistrationRepository registrationRepository;

    private PassportServiceImpl passportService;

    @BeforeEach
    void setUp() {
        passportService = new PassportServiceImpl(passportRepository, registrationRepository,
                new PassportMapperImpl());
    }

    private PassportDto passportDto() {
        return PassportDto.builder()
                .series(4509)
                .number(123456L)
                .lastName("Иванов")
                .firstName("Иван")
                .middleName("Иванович")
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(1990, 1, 1))
                .birthPlace("Москва")
                .issuedBy("ОВД")
                .dateOfIssue(LocalDate.of(2010, 5, 20))
                .divisionCode(770001)
                .registrationId(REGISTRATION_ID)
                .build();
    }

    private Registration registration() {
        final Registration registration = new Registration();
        registration.setId(REGISTRATION_ID);
        registration.setCountry("Россия");
        registration.setIndex(101000L);
        return registration;
    }

    @Test
    @DisplayName("create: сохраняет паспорт с регистрацией")
    void createShouldSavePassport() {
        when(registrationRepository.findById(REGISTRATION_ID)).thenReturn(Optional.of(registration()));
        when(passportRepository.save(any(Passport.class))).thenAnswer(invocation -> {
            final Passport passport = invocation.getArgument(0);
            passport.setId(PASSPORT_ID);
            return passport;
        });

        final PassportDto result = passportService.create(passportDto());

        assertThat(result.getId()).isEqualTo(PASSPORT_ID);
        assertThat(result.getRegistrationId()).isEqualTo(REGISTRATION_ID);
        assertThat(result.getGender()).isEqualTo(Gender.MALE);
    }

    @Test
    @DisplayName("create: бросает EntityNotFoundException для неизвестной регистрации")
    void createShouldThrowWhenRegistrationMissing() {
        when(registrationRepository.findById(REGISTRATION_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> passportService.create(passportDto()))
                .isInstanceOf(EntityNotFoundException.class);
        verify(passportRepository, never()).save(any(Passport.class));
    }

    @Test
    @DisplayName("getById: возвращает паспорт")
    void getByIdShouldReturnPassport() {
        when(passportRepository.findById(PASSPORT_ID)).thenReturn(Optional.of(Passport.builder()
                .id(PASSPORT_ID)
                .gender(Gender.FEMALE)
                .registration(registration())
                .build()));

        final PassportDto result = passportService.getById(PASSPORT_ID);

        assertThat(result.getRegistrationId()).isEqualTo(REGISTRATION_ID);
        assertThat(result.getGender()).isEqualTo(Gender.FEMALE);
    }

    @Test
    @DisplayName("getById: бросает EntityNotFoundException")
    void getByIdShouldThrowWhenMissing() {
        when(passportRepository.findById(PASSPORT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> passportService.getById(PASSPORT_ID))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("getAll: возвращает список паспортов")
    void getAllShouldReturnPassports() {
        when(passportRepository.findAll()).thenReturn(List.of(Passport.builder()
                .id(PASSPORT_ID)
                .registration(registration())
                .build()));

        assertThat(passportService.getAll()).hasSize(1);
    }

    @Test
    @DisplayName("update: обновляет паспорт")
    void updateShouldModifyPassport() {
        final Passport existing = Passport.builder()
                .id(PASSPORT_ID)
                .gender(Gender.MALE)
                .registration(registration())
                .build();
        when(passportRepository.findById(PASSPORT_ID)).thenReturn(Optional.of(existing));
        when(registrationRepository.findById(REGISTRATION_ID)).thenReturn(Optional.of(registration()));
        when(passportRepository.save(existing)).thenReturn(existing);

        final PassportDto request = passportDto();
        request.setLastName("Петров");
        final PassportDto result = passportService.update(PASSPORT_ID, request);

        assertThat(result.getLastName()).isEqualTo("Петров");
    }

    @Test
    @DisplayName("delete: удаляет паспорт")
    void deleteShouldRemovePassport() {
        final Passport existing = Passport.builder().id(PASSPORT_ID).build();
        when(passportRepository.findById(PASSPORT_ID)).thenReturn(Optional.of(existing));

        passportService.delete(PASSPORT_ID);

        verify(passportRepository).delete(existing);
    }
}
