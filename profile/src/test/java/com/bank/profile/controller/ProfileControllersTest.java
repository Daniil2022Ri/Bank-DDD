package com.bank.profile.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bank.profile.dto.AccountDetailsDto;
import com.bank.profile.dto.ActualRegistrationDto;
import com.bank.profile.dto.PassportDto;
import com.bank.profile.dto.ProfileDto;
import com.bank.profile.dto.RegistrationDto;
import com.bank.profile.entity.Gender;
import com.bank.profile.exception.EntityNotFoundException;
import com.bank.profile.service.AccountDetailsService;
import com.bank.profile.service.ActualRegistrationService;
import com.bank.profile.service.PassportService;
import com.bank.profile.service.ProfileService;
import com.bank.profile.service.RegistrationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {ProfileController.class, PassportController.class, RegistrationController.class,
        ActualRegistrationController.class, AccountDetailsController.class},
        properties = "server.servlet.context-path=")
@ActiveProfiles("test")
class ProfileControllersTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProfileService profileService;

    @MockBean
    private PassportService passportService;

    @MockBean
    private RegistrationService registrationService;

    @MockBean
    private ActualRegistrationService actualRegistrationService;

    @MockBean
    private AccountDetailsService accountDetailsService;

    private ProfileDto profileDto() {
        return ProfileDto.builder()
                .id(10L)
                .phoneNumber(79990000000L)
                .email("user@mail.ru")
                .nameOnCard("IVAN IVANOV")
                .inn(123456789012L)
                .snils(12345678901L)
                .passportId(1L)
                .actualRegistrationId(2L)
                .build();
    }

    @Test
    @DisplayName("POST /profiles: создаёт профиль")
    void shouldCreateProfile() throws Exception {
        given(profileService.create(any(ProfileDto.class))).willReturn(profileDto());

        mockMvc.perform(post("/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileDto())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.passportId").value(1));
    }

    @Test
    @DisplayName("POST /profiles: возвращает 400 при невалидном теле")
    void shouldRejectInvalidProfile() throws Exception {
        mockMvc.perform(post("/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"phoneNumber\":-1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @DisplayName("GET /profiles/{id}: возвращает 404")
    void shouldReturnNotFoundForProfile() throws Exception {
        given(profileService.getById(99L)).willThrow(new EntityNotFoundException("Profile with id 99 not found"));

        mockMvc.perform(get("/profiles/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /profiles/{id}: возвращает 204")
    void shouldDeleteProfile() throws Exception {
        mockMvc.perform(delete("/profiles/10"))
                .andExpect(status().isNoContent());

        verify(profileService).delete(10L);
    }

    @Test
    @DisplayName("POST /passports: создаёт паспорт")
    void shouldCreatePassport() throws Exception {
        final PassportDto response = PassportDto.builder()
                .id(1L)
                .series(4509)
                .number(123456L)
                .lastName("Иванов")
                .firstName("Иван")
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(1990, 1, 1))
                .birthPlace("Москва")
                .issuedBy("ОВД")
                .dateOfIssue(LocalDate.of(2010, 5, 20))
                .divisionCode(770001)
                .registrationId(1L)
                .build();
        given(passportService.create(any(PassportDto.class))).willReturn(response);

        mockMvc.perform(post("/passports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(response)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.gender").value("MALE"))
                .andExpect(jsonPath("$.registrationId").value(1));
    }

    @Test
    @DisplayName("POST /registrations: создаёт регистрацию")
    void shouldCreateRegistration() throws Exception {
        final RegistrationDto response = RegistrationDto.builder()
                .id(1L)
                .country("Россия")
                .index(101000L)
                .build();
        given(registrationService.create(any(RegistrationDto.class))).willReturn(response);

        mockMvc.perform(post("/registrations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(response)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.country").value("Россия"));
    }

    @Test
    @DisplayName("POST /actual-registrations: создаёт фактическую регистрацию")
    void shouldCreateActualRegistration() throws Exception {
        final ActualRegistrationDto response = ActualRegistrationDto.builder()
                .id(1L)
                .country("Россия")
                .index(101000L)
                .build();
        given(actualRegistrationService.create(any(ActualRegistrationDto.class))).willReturn(response);

        mockMvc.perform(post("/actual-registrations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(response)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /account-details: связывает счёт с профилем")
    void shouldCreateAccountDetails() throws Exception {
        final AccountDetailsDto response = AccountDetailsDto.builder()
                .id(1L)
                .accountId(55L)
                .profileId(10L)
                .build();
        given(accountDetailsService.create(any(AccountDetailsDto.class))).willReturn(response);

        mockMvc.perform(post("/account-details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(response)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountId").value(55));
    }

    @Test
    @DisplayName("PUT /profiles/{id}: обновляет профиль")
    void shouldUpdateProfile() throws Exception {
        given(profileService.update(eq(10L), any(ProfileDto.class))).willReturn(profileDto());

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/profiles/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileDto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nameOnCard").value("IVAN IVANOV"));
    }

    @Test
    @DisplayName("POST /profiles: возвращает 400 на битый JSON")
    void shouldReturnBadRequestOnMalformedJson() throws Exception {
        mockMvc.perform(post("/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"phoneNumber\":"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Malformed JSON request"));
    }
}
