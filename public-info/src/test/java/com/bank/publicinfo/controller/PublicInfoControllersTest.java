package com.bank.publicinfo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bank.publicinfo.dto.AtmDto;
import com.bank.publicinfo.dto.BankDetailsDto;
import com.bank.publicinfo.dto.BranchDto;
import com.bank.publicinfo.dto.CertificateDto;
import com.bank.publicinfo.dto.LicenseDto;
import com.bank.publicinfo.exception.EntityNotFoundException;
import com.bank.publicinfo.service.AtmService;
import com.bank.publicinfo.service.BankDetailsService;
import com.bank.publicinfo.service.BranchService;
import com.bank.publicinfo.service.CertificateService;
import com.bank.publicinfo.service.LicenseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.OffsetTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {BankDetailsController.class, BranchController.class, AtmController.class,
        CertificateController.class, LicenseController.class},
        properties = "server.servlet.context-path=")
@ActiveProfiles("test")
class PublicInfoControllersTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BankDetailsService bankDetailsService;

    @MockBean
    private BranchService branchService;

    @MockBean
    private AtmService atmService;

    @MockBean
    private CertificateService certificateService;

    @MockBean
    private LicenseService licenseService;

    private BankDetailsDto bankDetailsDto() {
        return BankDetailsDto.builder()
                .id(1L)
                .bik(444525002L)
                .inn(7707083893L)
                .kpp(773601001L)
                .corAccount(30101810)
                .city("Москва")
                .jointStockCompany("ПАО")
                .name("Сбербанк")
                .build();
    }

    @Test
    @DisplayName("POST /banks: создаёт реквизиты банка")
    void shouldCreateBankDetails() throws Exception {
        given(bankDetailsService.create(any(BankDetailsDto.class))).willReturn(bankDetailsDto());

        mockMvc.perform(post("/banks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bankDetailsDto())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Сбербанк"));
    }

    @Test
    @DisplayName("GET /banks/{id}: возвращает 404")
    void shouldReturnNotFoundForBank() throws Exception {
        given(bankDetailsService.getById(99L))
                .willThrow(new EntityNotFoundException("Bank details with id 99 not found"));

        mockMvc.perform(get("/banks/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /banks: возвращает 400 при невалидном теле")
    void shouldRejectInvalidBank() throws Exception {
        mockMvc.perform(post("/banks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bik\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @DisplayName("POST /branches: создаёт отделение")
    void shouldCreateBranch() throws Exception {
        final BranchDto response = BranchDto.builder()
                .id(2L)
                .address("ул. Тверская, 1")
                .phoneNumber(79990000000L)
                .city("Москва")
                .startOfWork(OffsetTime.parse("09:00:00+03:00"))
                .endOfWork(OffsetTime.parse("18:00:00+03:00"))
                .build();
        given(branchService.create(any(BranchDto.class))).willReturn(response);

        mockMvc.perform(post("/branches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(response)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    @DisplayName("POST /atms: создаёт банкомат")
    void shouldCreateAtm() throws Exception {
        final AtmDto response = AtmDto.builder()
                .id(3L)
                .address("ул. Тверская, 1")
                .allHours(true)
                .branchId(2L)
                .build();
        given(atmService.create(any(AtmDto.class))).willReturn(response);

        mockMvc.perform(post("/atms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(response)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.branchId").value(2));
    }

    @Test
    @DisplayName("POST /certificates: создаёт сертификат")
    void shouldCreateCertificate() throws Exception {
        final CertificateDto response = CertificateDto.builder()
                .id(4L)
                .photo(new byte[]{1, 2, 3})
                .bankDetailsId(1L)
                .build();
        given(certificateService.create(any(CertificateDto.class))).willReturn(response);

        mockMvc.perform(post("/certificates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(response)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bankDetailsId").value(1));
    }

    @Test
    @DisplayName("POST /licenses: создаёт лицензию")
    void shouldCreateLicense() throws Exception {
        final LicenseDto response = LicenseDto.builder()
                .id(5L)
                .photo(new byte[]{4, 5})
                .bankDetailsId(1L)
                .build();
        given(licenseService.create(any(LicenseDto.class))).willReturn(response);

        mockMvc.perform(post("/licenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(response)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    @DisplayName("POST /banks: возвращает 400 на битый JSON")
    void shouldReturnBadRequestOnMalformedJson() throws Exception {
        mockMvc.perform(post("/banks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bik\":"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Malformed JSON request"));
    }
}
