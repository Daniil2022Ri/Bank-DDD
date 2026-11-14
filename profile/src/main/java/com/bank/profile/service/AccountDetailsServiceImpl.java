package com.bank.profile.service;

import com.bank.profile.dto.AccountDetailsDto;
import com.bank.profile.entity.AccountDetails;
import com.bank.profile.entity.Profile;
import com.bank.profile.exception.EntityNotFoundException;
import com.bank.profile.mapper.AccountDetailsMapper;
import com.bank.profile.repository.AccountDetailsRepository;
import com.bank.profile.repository.ProfileRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountDetailsServiceImpl implements AccountDetailsService {

    private static final String ACCOUNT_DETAILS_NOT_FOUND = "Account details with id %d not found";
    private static final String PROFILE_NOT_FOUND = "Profile with id %d not found";

    private final AccountDetailsRepository accountDetailsRepository;
    private final ProfileRepository profileRepository;
    private final AccountDetailsMapper accountDetailsMapper;

    @Override
    public AccountDetailsDto create(AccountDetailsDto accountDetailsDto) {
        final Profile profile = findProfile(accountDetailsDto.getProfileId());
        final AccountDetails accountDetails = accountDetailsMapper.toEntity(accountDetailsDto);
        accountDetails.setProfile(profile);
        return accountDetailsMapper.toDto(accountDetailsRepository.save(accountDetails));
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDetailsDto getById(Long accountDetailsId) {
        return accountDetailsMapper.toDto(findAccountDetails(accountDetailsId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDetailsDto> getAll() {
        return accountDetailsMapper.toDtoList(accountDetailsRepository.findAll());
    }

    @Override
    public AccountDetailsDto update(Long accountDetailsId, AccountDetailsDto accountDetailsDto) {
        final AccountDetails accountDetails = findAccountDetails(accountDetailsId);
        accountDetailsMapper.updateEntity(accountDetailsDto, accountDetails);
        if (accountDetailsDto.getProfileId() != null) {
            accountDetails.setProfile(findProfile(accountDetailsDto.getProfileId()));
        }
        return accountDetailsMapper.toDto(accountDetailsRepository.save(accountDetails));
    }

    @Override
    public void delete(Long accountDetailsId) {
        accountDetailsRepository.delete(findAccountDetails(accountDetailsId));
    }

    private AccountDetails findAccountDetails(Long accountDetailsId) {
        return accountDetailsRepository.findById(accountDetailsId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(ACCOUNT_DETAILS_NOT_FOUND, accountDetailsId)));
    }

    private Profile findProfile(Long profileId) {
        return profileRepository.findById(profileId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(PROFILE_NOT_FOUND, profileId)));
    }
}
