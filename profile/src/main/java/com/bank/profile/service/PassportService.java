package com.bank.profile.service;

import com.bank.profile.dto.PassportDto;
import java.util.List;

public interface PassportService {

    PassportDto create(PassportDto passportDto);

    PassportDto getById(Long passportId);

    List<PassportDto> getAll();

    PassportDto update(Long passportId, PassportDto passportDto);

    void delete(Long passportId);
}
