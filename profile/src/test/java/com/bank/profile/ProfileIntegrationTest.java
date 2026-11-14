package com.bank.profile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bank.profile.dto.AccountDetailsDto;
import com.bank.profile.dto.ActualRegistrationDto;
import com.bank.profile.dto.PassportDto;
import com.bank.profile.dto.ProfileDto;
import com.bank.profile.dto.RegistrationDto;
import com.bank.profile.entity.Audit;
import com.bank.profile.entity.Gender;
import com.bank.profile.entity.OperationType;
import com.bank.profile.repository.AccountDetailsRepository;
import com.bank.profile.repository.ActualRegistrationRepository;
import com.bank.profile.repository.AuditRepository;
import com.bank.profile.repository.PassportRepository;
import com.bank.profile.repository.ProfileRepository;
import com.bank.profile.repository.RegistrationRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProfileIntegrationTest {

    private static final Long INN = 123456789012L;
    private static final Long SNILS = 12345678901L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuditRepository auditRepository;

    @Autowired
    private AccountDetailsRepository accountDetailsRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private PassportRepository passportRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private ActualRegistrationRepository actualRegistrationRepository;

    @BeforeEach
    void cleanUp() {
        auditRepository.deleteAll();
        accountDetailsRepository.deleteAll();
        profileRepository.deleteAll();
        passportRepository.deleteAll();
        registrationRepository.deleteAll();
        actualRegistrationRepository.deleteAll();
    }

    private long create(String path, Object body) throws Exception {
        final MvcResult result = mockMvc.perform(post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andReturn();
        final JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("id").asLong();
    }

    private RegistrationDto registrationDto() {
        return RegistrationDto.builder()
                .country("Россия")
                .region("Москва")
                .city("Москва")
                .street("Тверская")
                .houseNumber("1")
                .index(101000L)
                .build();
    }

    private PassportDto passportDto(long registrationId, long number) {
        return PassportDto.builder()
                .series(4509)
                .number(number)
                .lastName("Иванов")
                .firstName("Иван")
                .middleName("Иванович")
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(1990, 1, 1))
                .birthPlace("Москва")
                .issuedBy("ОВД Москвы")
                .dateOfIssue(LocalDate.of(2010, 5, 20))
                .divisionCode(770001)
                .registrationId(registrationId)
                .build();
    }

    private ProfileDto profileDto(long passportId, Long actualRegistrationId, Long inn) {
        return ProfileDto.builder()
                .phoneNumber(79990000000L)
                .email("user@mail.ru")
                .nameOnCard("IVAN IVANOV")
                .inn(inn)
                .snils(SNILS)
                .passportId(passportId)
                .actualRegistrationId(actualRegistrationId)
                .build();
    }

    @Test
    @DisplayName("Полный сценарий: регистрация, паспорт, профиль, счёт, аудит")
    void shouldPerformFullProfileFlow() throws Exception {
        final long registrationId = create("/registrations", registrationDto());
        final long passportId = create("/passports", passportDto(registrationId, 123456L));
        final long actualRegistrationId = create("/actual-registrations", ActualRegistrationDto.builder()
                .country("Россия")
                .city("Москва")
                .index(101000L)
                .build());
        final long profileId = create("/profiles", profileDto(passportId, actualRegistrationId, INN));

        mockMvc.perform(get("/profiles/{id}", profileId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.passportId").value(passportId))
                .andExpect(jsonPath("$.actualRegistrationId").value(actualRegistrationId))
                .andExpect(jsonPath("$.inn").value(INN));

        mockMvc.perform(put("/profiles/{id}", profileId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileDto(passportId, actualRegistrationId, INN))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phoneNumber").value(79990000000L));

        final List<Audit> auditRecords = auditRepository.findAll();
        assertThat(auditRecords).hasSize(2);
        assertThat(auditRecords.get(0).getOperationType()).isEqualTo(OperationType.CREATE);
        assertThat(auditRecords.get(0).getEntityJson()).contains("\"phoneNumber\":79990000000");
        assertThat(auditRecords.get(1).getOperationType()).isEqualTo(OperationType.UPDATE);

        final long accountDetailsId = create("/account-details", AccountDetailsDto.builder()
                .accountId(55L)
                .profileId(profileId)
                .build());
        mockMvc.perform(get("/account-details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        mockMvc.perform(delete("/account-details/{id}", accountDetailsId))
                .andExpect(status().isNoContent());
        mockMvc.perform(delete("/profiles/{id}", profileId))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/profiles/{id}", profileId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Дубликат ИНН возвращает 409")
    void shouldRejectDuplicateInn() throws Exception {
        final long registrationId = create("/registrations", registrationDto());
        final long passportId = create("/passports", passportDto(registrationId, 123456L));
        create("/profiles", profileDto(passportId, null, INN));

        final long secondPassportId = create("/passports", passportDto(registrationId, 654321L));
        mockMvc.perform(post("/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                profileDto(secondPassportId, null, INN))))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Паспорт с неизвестной регистрацией возвращает 404")
    void shouldRejectUnknownRegistration() throws Exception {
        mockMvc.perform(post("/passports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passportDto(999L, 123456L))))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Невалидный профиль возвращает 400")
    void shouldRejectInvalidProfile() throws Exception {
        mockMvc.perform(post("/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"not-an-email\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
