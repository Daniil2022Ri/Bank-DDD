package com.bank.profile.controller;

import com.bank.profile.dto.AccountDetailsDto;
import com.bank.profile.service.AccountDetailsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account-details")
@RequiredArgsConstructor
@Tag(name = "Account details API", description = "Связь профилей с банковскими счетами")
public class AccountDetailsController {

    private final AccountDetailsService accountDetailsService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountDetailsDto create(@Valid @RequestBody AccountDetailsDto accountDetailsDto) {
        return accountDetailsService.create(accountDetailsDto);
    }

    @GetMapping
    public List<AccountDetailsDto> getAll() {
        return accountDetailsService.getAll();
    }

    @GetMapping("/{accountDetailsId}")
    public AccountDetailsDto getById(@PathVariable Long accountDetailsId) {
        return accountDetailsService.getById(accountDetailsId);
    }

    @PutMapping("/{accountDetailsId}")
    public AccountDetailsDto update(@PathVariable Long accountDetailsId,
                                    @Valid @RequestBody AccountDetailsDto accountDetailsDto) {
        return accountDetailsService.update(accountDetailsId, accountDetailsDto);
    }

    @DeleteMapping("/{accountDetailsId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long accountDetailsId) {
        accountDetailsService.delete(accountDetailsId);
    }
}
