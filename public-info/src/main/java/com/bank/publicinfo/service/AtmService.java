package com.bank.publicinfo.service;

import com.bank.publicinfo.dto.AtmDto;
import java.util.List;

public interface AtmService {

    AtmDto create(AtmDto atmDto);

    AtmDto getById(Long atmId);

    List<AtmDto> getAll();

    AtmDto update(Long atmId, AtmDto atmDto);

    void delete(Long atmId);
}
