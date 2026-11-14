package com.bank.publicinfo.service;

import com.bank.publicinfo.dto.BankDetailsDto;
import java.util.List;

public interface BankDetailsService {

    BankDetailsDto create(BankDetailsDto bankDetailsDto);

    BankDetailsDto getById(Long bankDetailsId);

    List<BankDetailsDto> getAll();

    BankDetailsDto update(Long bankDetailsId, BankDetailsDto bankDetailsDto);

    void delete(Long bankDetailsId);
}
