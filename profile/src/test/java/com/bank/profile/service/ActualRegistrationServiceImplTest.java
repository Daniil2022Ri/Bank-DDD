package com.bank.profile.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bank.profile.dto.ActualRegistrationDto;
import com.bank.profile.entity.ActualRegistration;
import com.bank.profile.exception.EntityNotFoundException;
import com.bank.profile.mapper.ActualRegistrationMapperImpl;
import com.bank.profile.repository.ActualRegistrationRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ActualRegistrationServiceImplTest {

    private static final Long REGISTRATION_ID = 1L;

    @Mock
    private ActualRegistrationRepository actualRegistrationRepository;

    private ActualRegistrationServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ActualRegistrationServiceImpl(actualRegistrationRepository,
                new ActualRegistrationMapperImpl());
    }

    private ActualRegistration registration() {
        final ActualRegistration registration = new ActualRegistration();
        registration.setId(REGISTRATION_ID);
        registration.setCountry("Россия");
        registration.setIndex(101000L);
        return registration;
    }

    @Test
    @DisplayName("create: сохраняет фактическую регистрацию")
    void createShouldSaveRegistration() {
        when(actualRegistrationRepository.save(any(ActualRegistration.class))).thenAnswer(invocation -> {
            final ActualRegistration registration = invocation.getArgument(0);
            registration.setId(REGISTRATION_ID);
            return registration;
        });
        final ActualRegistrationDto request = ActualRegistrationDto.builder()
                .country("Россия")
                .index(101000L)
                .build();

        final ActualRegistrationDto result = service.create(request);

        assertThat(result.getId()).isEqualTo(REGISTRATION_ID);
    }

    @Test
    @DisplayName("getAll: возвращает список")
    void getAllShouldReturnRegistrations() {
        when(actualRegistrationRepository.findAll()).thenReturn(List.of(registration()));

        assertThat(service.getAll()).hasSize(1);
    }

    @Test
    @DisplayName("getById: бросает EntityNotFoundException")
    void getByIdShouldThrowWhenMissing() {
        when(actualRegistrationRepository.findById(REGISTRATION_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(REGISTRATION_ID))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("delete: удаляет фактическую регистрацию")
    void deleteShouldRemoveRegistration() {
        final ActualRegistration existing = registration();
        when(actualRegistrationRepository.findById(REGISTRATION_ID)).thenReturn(Optional.of(existing));

        service.delete(REGISTRATION_ID);

        verify(actualRegistrationRepository).delete(existing);
    }
}
