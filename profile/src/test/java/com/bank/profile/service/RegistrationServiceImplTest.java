package com.bank.profile.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bank.profile.dto.RegistrationDto;
import com.bank.profile.entity.Registration;
import com.bank.profile.exception.EntityNotFoundException;
import com.bank.profile.mapper.RegistrationMapperImpl;
import com.bank.profile.repository.RegistrationRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceImplTest {

    private static final Long REGISTRATION_ID = 1L;

    @Mock
    private RegistrationRepository registrationRepository;

    private RegistrationServiceImpl registrationService;

    @BeforeEach
    void setUp() {
        registrationService = new RegistrationServiceImpl(registrationRepository, new RegistrationMapperImpl());
    }

    private RegistrationDto registrationDto() {
        return RegistrationDto.builder()
                .country("Россия")
                .region("Москва")
                .city("Москва")
                .index(101000L)
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
    @DisplayName("create: сохраняет регистрацию")
    void createShouldSaveRegistration() {
        when(registrationRepository.save(any(Registration.class))).thenAnswer(invocation -> {
            final Registration registration = invocation.getArgument(0);
            registration.setId(REGISTRATION_ID);
            return registration;
        });

        final RegistrationDto result = registrationService.create(registrationDto());

        assertThat(result.getId()).isEqualTo(REGISTRATION_ID);
        assertThat(result.getIndex()).isEqualTo(101000L);
    }

    @Test
    @DisplayName("update: обновляет регистрацию")
    void updateShouldModifyRegistration() {
        final Registration existing = registration();
        when(registrationRepository.findById(REGISTRATION_ID)).thenReturn(Optional.of(existing));
        when(registrationRepository.save(existing)).thenReturn(existing);

        final RegistrationDto request = registrationDto();
        request.setCity("Санкт-Петербург");
        final RegistrationDto result = registrationService.update(REGISTRATION_ID, request);

        assertThat(result.getCity()).isEqualTo("Санкт-Петербург");
    }

    @Test
    @DisplayName("getById: бросает EntityNotFoundException")
    void getByIdShouldThrowWhenMissing() {
        when(registrationRepository.findById(REGISTRATION_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> registrationService.getById(REGISTRATION_ID))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("delete: удаляет регистрацию")
    void deleteShouldRemoveRegistration() {
        final Registration existing = registration();
        when(registrationRepository.findById(REGISTRATION_ID)).thenReturn(Optional.of(existing));

        registrationService.delete(REGISTRATION_ID);

        verify(registrationRepository).delete(existing);
    }
}
