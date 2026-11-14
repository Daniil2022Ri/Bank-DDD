package com.bank.publicinfo.service;

import com.bank.publicinfo.dto.CertificateDto;
import com.bank.publicinfo.entity.BankDetails;
import com.bank.publicinfo.entity.Certificate;
import com.bank.publicinfo.exception.EntityNotFoundException;
import com.bank.publicinfo.mapper.PublicInfoMapper;
import com.bank.publicinfo.repository.BankDetailsRepository;
import com.bank.publicinfo.repository.CertificateRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CertificateServiceImpl implements CertificateService {

    private static final String CERTIFICATE_NOT_FOUND = "Certificate with id %d not found";
    private static final String BANK_DETAILS_NOT_FOUND = "Bank details with id %d not found";

    private final CertificateRepository certificateRepository;
    private final BankDetailsRepository bankDetailsRepository;
    private final PublicInfoMapper publicInfoMapper;

    @Override
    public CertificateDto create(CertificateDto certificateDto) {
        final Certificate certificate = publicInfoMapper.toCertificateEntity(certificateDto);
        certificate.setBankDetails(findBankDetails(certificateDto.getBankDetailsId()));
        return publicInfoMapper.toCertificateDto(certificateRepository.save(certificate));
    }

    @Override
    @Transactional(readOnly = true)
    public CertificateDto getById(Long certificateId) {
        return publicInfoMapper.toCertificateDto(findCertificate(certificateId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CertificateDto> getAll() {
        return publicInfoMapper.toCertificateDtoList(certificateRepository.findAll());
    }

    @Override
    public CertificateDto update(Long certificateId, CertificateDto certificateDto) {
        final Certificate certificate = findCertificate(certificateId);
        publicInfoMapper.updateCertificate(certificateDto, certificate);
        certificate.setBankDetails(findBankDetails(certificateDto.getBankDetailsId()));
        return publicInfoMapper.toCertificateDto(certificateRepository.save(certificate));
    }

    @Override
    public void delete(Long certificateId) {
        certificateRepository.delete(findCertificate(certificateId));
    }

    private Certificate findCertificate(Long certificateId) {
        return certificateRepository.findById(certificateId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(CERTIFICATE_NOT_FOUND, certificateId)));
    }

    private BankDetails findBankDetails(Long bankDetailsId) {
        return bankDetailsRepository.findById(bankDetailsId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(BANK_DETAILS_NOT_FOUND, bankDetailsId)));
    }
}
