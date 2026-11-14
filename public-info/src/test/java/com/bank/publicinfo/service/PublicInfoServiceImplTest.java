package com.bank.publicinfo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bank.publicinfo.dto.AtmDto;
import com.bank.publicinfo.dto.BankDetailsDto;
import com.bank.publicinfo.dto.BranchDto;
import com.bank.publicinfo.dto.CertificateDto;
import com.bank.publicinfo.dto.LicenseDto;
import com.bank.publicinfo.entity.Atm;
import com.bank.publicinfo.entity.BankDetails;
import com.bank.publicinfo.entity.Branch;
import com.bank.publicinfo.entity.Certificate;
import com.bank.publicinfo.entity.License;
import com.bank.publicinfo.exception.EntityNotFoundException;
import com.bank.publicinfo.mapper.PublicInfoMapperImpl;
import com.bank.publicinfo.repository.AtmRepository;
import com.bank.publicinfo.repository.BankDetailsRepository;
import com.bank.publicinfo.repository.BranchRepository;
import com.bank.publicinfo.repository.CertificateRepository;
import com.bank.publicinfo.repository.LicenseRepository;
import java.time.OffsetTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PublicInfoServiceImplTest {

    private static final Long ID = 1L;
    private static final Long BANK_ID = 10L;
    private static final Long BRANCH_ID = 20L;

    @Mock
    private BankDetailsRepository bankDetailsRepository;

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private AtmRepository atmRepository;

    @Mock
    private CertificateRepository certificateRepository;

    @Mock
    private LicenseRepository licenseRepository;

    private PublicInfoMapperImpl mapper;
    private BankDetailsServiceImpl bankDetailsService;
    private BranchServiceImpl branchService;
    private AtmServiceImpl atmService;
    private CertificateServiceImpl certificateService;
    private LicenseServiceImpl licenseService;

    @BeforeEach
    void setUp() {
        mapper = new PublicInfoMapperImpl();
        bankDetailsService = new BankDetailsServiceImpl(bankDetailsRepository, mapper);
        branchService = new BranchServiceImpl(branchRepository, mapper);
        atmService = new AtmServiceImpl(atmRepository, branchRepository, mapper);
        certificateService = new CertificateServiceImpl(certificateRepository, bankDetailsRepository, mapper);
        licenseService = new LicenseServiceImpl(licenseRepository, bankDetailsRepository, mapper);
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

    private Branch branch() {
        return Branch.builder()
                .id(BRANCH_ID)
                .address("ул. Тверская, 1")
                .phoneNumber(79990000000L)
                .city("Москва")
                .startOfWork(OffsetTime.parse("09:00:00+03:00"))
                .endOfWork(OffsetTime.parse("18:00:00+03:00"))
                .build();
    }

    private BankDetails bankDetails() {
        return BankDetails.builder()
                .id(BANK_ID)
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
    @DisplayName("BankDetails: создаёт запись")
    void shouldCreateBankDetails() {
        when(bankDetailsRepository.save(any(BankDetails.class))).thenAnswer(invocation -> {
            final BankDetails entity = invocation.getArgument(0);
            entity.setId(ID);
            return entity;
        });

        final BankDetailsDto result = bankDetailsService.create(bankDetailsDto());

        assertThat(result.getId()).isEqualTo(ID);
        assertThat(result.getBik()).isEqualTo(444525002L);
    }

    @Test
    @DisplayName("BankDetails: бросает EntityNotFoundException")
    void shouldThrowWhenBankDetailsMissing() {
        when(bankDetailsRepository.findById(ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bankDetailsService.getById(ID))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(String.valueOf(ID));
    }

    @Test
    @DisplayName("BankDetails: обновляет и удаляет")
    void shouldUpdateAndDeleteBankDetails() {
        final BankDetails existing = bankDetails();
        when(bankDetailsRepository.findById(BANK_ID)).thenReturn(Optional.of(existing));
        when(bankDetailsRepository.save(existing)).thenReturn(existing);

        final BankDetailsDto request = bankDetailsDto();
        request.setName("Тинькофф");
        final BankDetailsDto updated = bankDetailsService.update(BANK_ID, request);
        assertThat(updated.getName()).isEqualTo("Тинькофф");

        bankDetailsService.delete(BANK_ID);
        verify(bankDetailsRepository).delete(existing);
    }

    @Test
    @DisplayName("Branch: создаёт отделение")
    void shouldCreateBranch() {
        when(branchRepository.save(any(Branch.class))).thenAnswer(invocation -> {
            final Branch entity = invocation.getArgument(0);
            entity.setId(ID);
            return entity;
        });

        final BranchDto result = branchService.create(branchDto());

        assertThat(result.getId()).isEqualTo(ID);
        assertThat(result.getPhoneNumber()).isEqualTo(79990000000L);
    }

    @Test
    @DisplayName("Atm: создаёт банкомат с отделением")
    void shouldCreateAtmWithBranch() {
        when(branchRepository.findById(BRANCH_ID)).thenReturn(Optional.of(branch()));
        when(atmRepository.save(any(Atm.class))).thenAnswer(invocation -> {
            final Atm entity = invocation.getArgument(0);
            entity.setId(ID);
            return entity;
        });
        final AtmDto request = AtmDto.builder()
                .address("ул. Тверская, 1")
                .allHours(true)
                .branchId(BRANCH_ID)
                .build();

        final AtmDto result = atmService.create(request);

        assertThat(result.getId()).isEqualTo(ID);
        assertThat(result.getBranchId()).isEqualTo(BRANCH_ID);
    }

    @Test
    @DisplayName("Atm: бросает EntityNotFoundException для неизвестного отделения")
    void shouldThrowWhenBranchMissing() {
        when(branchRepository.findById(BRANCH_ID)).thenReturn(Optional.empty());
        final AtmDto request = AtmDto.builder()
                .address("ул. Тверская, 1")
                .allHours(false)
                .branchId(BRANCH_ID)
                .build();

        assertThatThrownBy(() -> atmService.create(request))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Certificate: создаёт сертификат")
    void shouldCreateCertificate() {
        when(bankDetailsRepository.findById(BANK_ID)).thenReturn(Optional.of(bankDetails()));
        when(certificateRepository.save(any(Certificate.class))).thenAnswer(invocation -> {
            final Certificate entity = invocation.getArgument(0);
            entity.setId(ID);
            return entity;
        });
        final CertificateDto request = CertificateDto.builder()
                .photo(new byte[]{1, 2, 3})
                .bankDetailsId(BANK_ID)
                .build();

        final CertificateDto result = certificateService.create(request);

        assertThat(result.getId()).isEqualTo(ID);
        assertThat(result.getBankDetailsId()).isEqualTo(BANK_ID);
        assertThat(result.getPhoto()).containsExactly(1, 2, 3);
    }

    @Test
    @DisplayName("Certificate: бросает EntityNotFoundException для неизвестного банка")
    void shouldThrowWhenBankMissingForCertificate() {
        when(bankDetailsRepository.findById(BANK_ID)).thenReturn(Optional.empty());
        final CertificateDto request = CertificateDto.builder()
                .photo(new byte[]{1})
                .bankDetailsId(BANK_ID)
                .build();

        assertThatThrownBy(() -> certificateService.create(request))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("License: создаёт лицензию")
    void shouldCreateLicense() {
        when(bankDetailsRepository.findById(BANK_ID)).thenReturn(Optional.of(bankDetails()));
        when(licenseRepository.save(any(License.class))).thenAnswer(invocation -> {
            final License entity = invocation.getArgument(0);
            entity.setId(ID);
            return entity;
        });
        final LicenseDto request = LicenseDto.builder()
                .photo(new byte[]{4, 5})
                .bankDetailsId(BANK_ID)
                .build();

        final LicenseDto result = licenseService.create(request);

        assertThat(result.getId()).isEqualTo(ID);
        assertThat(result.getBankDetailsId()).isEqualTo(BANK_ID);
    }
}
