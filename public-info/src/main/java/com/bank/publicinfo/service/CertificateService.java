package com.bank.publicinfo.service;

import com.bank.publicinfo.dto.CertificateDto;
import java.util.List;

public interface CertificateService {

    CertificateDto create(CertificateDto certificateDto);

    CertificateDto getById(Long certificateId);

    List<CertificateDto> getAll();

    CertificateDto update(Long certificateId, CertificateDto certificateDto);

    void delete(Long certificateId);
}
