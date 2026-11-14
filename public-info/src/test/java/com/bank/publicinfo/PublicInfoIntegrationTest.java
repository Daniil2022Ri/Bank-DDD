package com.bank.publicinfo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bank.publicinfo.dto.AtmDto;
import com.bank.publicinfo.dto.BankDetailsDto;
import com.bank.publicinfo.dto.BranchDto;
import com.bank.publicinfo.dto.CertificateDto;
import com.bank.publicinfo.dto.LicenseDto;
import com.bank.publicinfo.entity.Audit;
import com.bank.publicinfo.entity.OperationType;
import com.bank.publicinfo.repository.AtmRepository;
import com.bank.publicinfo.repository.AuditRepository;
import com.bank.publicinfo.repository.BankDetailsRepository;
import com.bank.publicinfo.repository.BranchRepository;
import com.bank.publicinfo.repository.CertificateRepository;
import com.bank.publicinfo.repository.LicenseRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.OffsetTime;
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
class PublicInfoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuditRepository auditRepository;

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private LicenseRepository licenseRepository;

    @Autowired
    private AtmRepository atmRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private BankDetailsRepository bankDetailsRepository;

    @BeforeEach
    void cleanUp() {
        auditRepository.deleteAll();
        certificateRepository.deleteAll();
        licenseRepository.deleteAll();
        atmRepository.deleteAll();
        branchRepository.deleteAll();
        bankDetailsRepository.deleteAll();
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

    private BankDetailsDto bankDetailsDto() {
        return BankDetailsDto.builder()
                .bik(444525002L)
                .inn(7707083893L)
                .kpp(773601001L)
                .corAccount(30101810)
                .city("Москва")
                .jointStockCompany("ПАО")
                .name("Сбербанк")
                .build();
    }

    private BranchDto branchDto() {
        return BranchDto.builder()
                .address("ул. Тверская, 1")
                .phoneNumber(79990000000L)
                .city("Москва")
                .startOfWork(OffsetTime.parse("09:00:00+03:00"))
                .endOfWork(OffsetTime.parse("18:00:00+03:00"))
                .build();
    }

    @Test
    @DisplayName("Полный сценарий: банк, отделение, банкомат, сертификат, лицензия + аудит")
    void shouldPerformFullFlow() throws Exception {
        final long bankId = create("/banks", bankDetailsDto());
        final long branchId = create("/branches", branchDto());
        final long atmId = create("/atms", AtmDto.builder()
                .address("ул. Тверская, 1")
                .allHours(true)
                .branchId(branchId)
                .build());
        create("/certificates", CertificateDto.builder()
                .photo(new byte[]{1, 2, 3})
                .bankDetailsId(bankId)
                .build());
        create("/licenses", LicenseDto.builder()
                .photo(new byte[]{4, 5})
                .bankDetailsId(bankId)
                .build());

        mockMvc.perform(get("/atms/{id}", atmId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.branchId").value(branchId));

        mockMvc.perform(put("/banks/{id}", bankId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(BankDetailsDto.builder()
                                .bik(444525002L)
                                .inn(7707083893L)
                                .kpp(773601001L)
                                .corAccount(30101810)
                                .city("Москва")
                                .jointStockCompany("ПАО")
                                .name("Сбер")
                                .build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Сбер"));

        final List<Audit> auditRecords = auditRepository.findAll();
        assertThat(auditRecords).hasSize(6);
        assertThat(auditRecords).filteredOn(audit -> audit.getOperationType() == OperationType.CREATE)
                .hasSize(5);
        assertThat(auditRecords.get(auditRecords.size() - 1).getOperationType())
                .isEqualTo(OperationType.UPDATE);
    }

    @Test
    @DisplayName("Дубликат БИК возвращает 409")
    void shouldRejectDuplicateBik() throws Exception {
        create("/banks", bankDetailsDto());

        mockMvc.perform(post("/banks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bankDetailsDto())))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Удаление и 404")
    void shouldDeleteAndReturnNotFound() throws Exception {
        final long licenseId = create("/licenses", LicenseDto.builder()
                .photo(new byte[]{7})
                .bankDetailsId(create("/banks", bankDetailsDto()))
                .build());

        mockMvc.perform(delete("/licenses/{id}", licenseId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/licenses/{id}", licenseId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Невалидные данные возвращают 400")
    void shouldRejectInvalidData() throws Exception {
        mockMvc.perform(post("/branches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"address\":\"ул. Тверская, 1\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
