package com.bank.account.controller;

import com.bank.account.dto.AccountDto;
import com.bank.account.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Tag(name = "Account API", description = "CRUD операции над банковскими счетами")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать банковский счёт")
    public AccountDto create(@Valid @RequestBody AccountDto accountDto) {
        return accountService.create(accountDto);
    }

    @GetMapping("/{accountId}")
    @Operation(summary = "Получить счёт по идентификатору")
    public AccountDto getById(@PathVariable Long accountId) {
        return accountService.getById(accountId);
    }

    @GetMapping
    @Operation(summary = "Получить список всех счетов")
    public List<AccountDto> getAll() {
        return accountService.getAll();
    }

    @PutMapping("/{accountId}")
    @Operation(summary = "Обновить данные счёта")
    public AccountDto update(@PathVariable Long accountId, @Valid @RequestBody AccountDto accountDto) {
        return accountService.update(accountId, accountDto);
    }

    @DeleteMapping("/{accountId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить счёт")
    public void delete(@PathVariable Long accountId) {
        accountService.delete(accountId);
    }
}
