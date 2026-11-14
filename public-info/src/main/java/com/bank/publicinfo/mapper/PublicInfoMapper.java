package com.bank.publicinfo.mapper;

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
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PublicInfoMapper {

    BankDetailsDto toBankDetailsDto(BankDetails entity);

    List<BankDetailsDto> toBankDetailsDtoList(List<BankDetails> entities);

    @Mapping(target = "id", ignore = true)
    BankDetails toBankDetailsEntity(BankDetailsDto dto);

    @Mapping(target = "id", ignore = true)
    void updateBankDetails(BankDetailsDto dto, @MappingTarget BankDetails entity);

    BranchDto toBranchDto(Branch entity);

    List<BranchDto> toBranchDtoList(List<Branch> entities);

    @Mapping(target = "id", ignore = true)
    Branch toBranchEntity(BranchDto dto);

    @Mapping(target = "id", ignore = true)
    void updateBranch(BranchDto dto, @MappingTarget Branch entity);

    @Mapping(source = "branch.id", target = "branchId")
    AtmDto toAtmDto(Atm entity);

    @Mapping(source = "branch.id", target = "branchId")
    List<AtmDto> toAtmDtoList(List<Atm> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "branch", ignore = true)
    Atm toAtmEntity(AtmDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "branch", ignore = true)
    void updateAtm(AtmDto dto, @MappingTarget Atm entity);

    @Mapping(source = "bankDetails.id", target = "bankDetailsId")
    CertificateDto toCertificateDto(Certificate entity);

    @Mapping(source = "bankDetails.id", target = "bankDetailsId")
    List<CertificateDto> toCertificateDtoList(List<Certificate> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bankDetails", ignore = true)
    Certificate toCertificateEntity(CertificateDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bankDetails", ignore = true)
    void updateCertificate(CertificateDto dto, @MappingTarget Certificate entity);

    @Mapping(source = "bankDetails.id", target = "bankDetailsId")
    LicenseDto toLicenseDto(License entity);

    @Mapping(source = "bankDetails.id", target = "bankDetailsId")
    List<LicenseDto> toLicenseDtoList(List<License> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bankDetails", ignore = true)
    License toLicenseEntity(LicenseDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bankDetails", ignore = true)
    void updateLicense(LicenseDto dto, @MappingTarget License entity);
}
