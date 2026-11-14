package com.bank.publicinfo.service;

import com.bank.publicinfo.dto.LicenseDto;
import java.util.List;

public interface LicenseService {

    LicenseDto create(LicenseDto licenseDto);

    LicenseDto getById(Long licenseId);

    List<LicenseDto> getAll();

    LicenseDto update(Long licenseId, LicenseDto licenseDto);

    void delete(Long licenseId);
}
