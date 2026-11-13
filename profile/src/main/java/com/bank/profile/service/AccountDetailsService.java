package com.bank.profile.service;

import com.bank.profile.dto.AccountDetailsDto;
import java.util.List;

public interface AccountDetailsService {

    AccountDetailsDto create(AccountDetailsDto accountDetailsDto);

    AccountDetailsDto getById(Long accountDetailsId);

    List<AccountDetailsDto> getAll();

    AccountDetailsDto update(Long accountDetailsId, AccountDetailsDto accountDetailsDto);

    void delete(Long accountDetailsId);
}
