package com.bank.profile.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.bank.profile.dto.AccountDetailsDto;
import com.bank.profile.entity.AccountDetails;
import com.bank.profile.entity.Profile;
import com.bank.profile.exception.EntityNotFoundException;
import com.bank.profile.mapper.AccountDetailsMapperImpl;
import com.bank.profile.repository.AccountDetailsRepository;
import com.bank.profile.repository.ProfileRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountDetailsServiceImplTest {

    private static final Long ACCOUNT_DETAILS_ID = 1L;
    private static final Long ACCOUNT_ID = 55L;
    private static final Long PROFILE_ID = 10L;

    @Mock
    private AccountDetailsRepository accountDetailsRepository;

    @Mock
    private ProfileRepository profileRepository;

    private AccountDetailsServiceImpl accountDetailsService;

    @BeforeEach
    void setUp() {
        accountDetailsService = new AccountDetailsServiceImpl(accountDetailsRepository, profileRepository,
                new AccountDetailsMapperImpl());
    }

    private AccountDetailsDto accountDetailsDto() {
        return AccountDetailsDto.builder()
                .accountId(ACCOUNT_ID)
                .profileId(PROFILE_ID)
                .build();
    }

    @Test
    @DisplayName("create: связывает счёт с профилем")
    void createShouldLinkAccountToProfile() {
        when(profileRepository.findById(PROFILE_ID)).thenReturn(Optional.of(
                Profile.builder().id(PROFILE_ID).build()));
        when(accountDetailsRepository.save(any(AccountDetails.class))).thenAnswer(invocation -> {
            final AccountDetails accountDetails = invocation.getArgument(0);
            accountDetails.setId(ACCOUNT_DETAILS_ID);
            return accountDetails;
        });

        final AccountDetailsDto result = accountDetailsService.create(accountDetailsDto());

        assertThat(result.getId()).isEqualTo(ACCOUNT_DETAILS_ID);
        assertThat(result.getAccountId()).isEqualTo(ACCOUNT_ID);
        assertThat(result.getProfileId()).isEqualTo(PROFILE_ID);
    }

    @Test
    @DisplayName("create: бросает EntityNotFoundException для неизвестного профиля")
    void createShouldThrowWhenProfileMissing() {
        when(profileRepository.findById(PROFILE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountDetailsService.create(accountDetailsDto()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("getById: бросает EntityNotFoundException")
    void getByIdShouldThrowWhenMissing() {
        when(accountDetailsRepository.findById(ACCOUNT_DETAILS_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountDetailsService.getById(ACCOUNT_DETAILS_ID))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
