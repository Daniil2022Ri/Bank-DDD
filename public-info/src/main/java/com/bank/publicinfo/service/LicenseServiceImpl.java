package com.bank.publicinfo.service;

import com.bank.publicinfo.dto.LicenseDto;
import com.bank.publicinfo.entity.BankDetails;
import com.bank.publicinfo.entity.License;
import com.bank.publicinfo.exception.EntityNotFoundException;
import com.bank.publicinfo.mapper.PublicInfoMapper;
import com.bank.publicinfo.repository.BankDetailsRepository;
import com.bank.publicinfo.repository.LicenseRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LicenseServiceImpl implements LicenseService {

    private static final String LICENSE_NOT_FOUND = "License with id %d not found";
    private static final String BANK_DETAILS_NOT_FOUND = "Bank details with id %d not found";

    private final LicenseRepository licenseRepository;
    private final BankDetailsRepository bankDetailsRepository;
    private final PublicInfoMapper publicInfoMapper;

    @Override
    public LicenseDto create(LicenseDto licenseDto) {
        final License license = publicInfoMapper.toLicenseEntity(licenseDto);
        license.setBankDetails(findBankDetails(licenseDto.getBankDetailsId()));
        return publicInfoMapper.toLicenseDto(licenseRepository.save(license));
    }

    @Override
    @Transactional(readOnly = true)
    public LicenseDto getById(Long licenseId) {
        return publicInfoMapper.toLicenseDto(findLicense(licenseId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LicenseDto> getAll() {
        return publicInfoMapper.toLicenseDtoList(licenseRepository.findAll());
    }

    @Override
    public LicenseDto update(Long licenseId, LicenseDto licenseDto) {
        final License license = findLicense(licenseId);
        publicInfoMapper.updateLicense(licenseDto, license);
        license.setBankDetails(findBankDetails(licenseDto.getBankDetailsId()));
        return publicInfoMapper.toLicenseDto(licenseRepository.save(license));
    }

    @Override
    public void delete(Long licenseId) {
        licenseRepository.delete(findLicense(licenseId));
    }

    private License findLicense(Long licenseId) {
        return licenseRepository.findById(licenseId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(LICENSE_NOT_FOUND, licenseId)));
    }

    private BankDetails findBankDetails(Long bankDetailsId) {
        return bankDetailsRepository.findById(bankDetailsId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(BANK_DETAILS_NOT_FOUND, bankDetailsId)));
    }
}
