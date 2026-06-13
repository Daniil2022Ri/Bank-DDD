package com.bank.account.Controller;

import com.bank.account.Dto.AccountDto;
import com.bank.account.Service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;


    @PostMapping
    public ResponseEntity<AccountDto> openAccount(@RequestBody AccountDto dto) {
        return ResponseEntity.ok(accountService.openAccount(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getAccount(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @GetMapping("/by-profile/{profileId}")
    public ResponseEntity<List<AccountDto>> getByProfile(@PathVariable Long profileId) {
        return ResponseEntity.ok(accountService.getAccountsByProfile(profileId));
    }

    @PutMapping("/{id}/block")
    public ResponseEntity<AccountDto> blockAccount(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.blockAccount(id));
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<AccountDto> closeAccount(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.closeAccount(id));
    }

    @PutMapping("/{id}/balance")
    public ResponseEntity<AccountDto> updateBalance(
            @PathVariable Long id,
            @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(accountService.updateBalance(id, amount));
    }

}

