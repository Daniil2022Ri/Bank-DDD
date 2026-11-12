package com.bank.antifraud.controller;

import com.bank.antifraud.dto.SuspiciousAccountTransferDto;
import com.bank.antifraud.dto.SuspiciousCardTransferDto;
import com.bank.antifraud.dto.SuspiciousPhoneTransferDto;
import com.bank.antifraud.service.SuspiciousTransferService;
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
@RequestMapping("/suspicious-transfers")
@RequiredArgsConstructor
@Tag(name = "Suspicious transfers API", description = "Управление подозрительными переводами")
public class SuspiciousTransferController {

    private final SuspiciousTransferService suspiciousTransferService;

    @PostMapping("/cards")
    @ResponseStatus(HttpStatus.CREATED)
    public SuspiciousCardTransferDto createCard(@Valid @RequestBody SuspiciousCardTransferDto transferDto) {
        return suspiciousTransferService.createCard(transferDto);
    }

    @GetMapping("/cards")
    public List<SuspiciousCardTransferDto> getAllCards() {
        return suspiciousTransferService.getAllCards();
    }

    @GetMapping("/cards/{transferId}")
    public SuspiciousCardTransferDto getCardById(@PathVariable Long transferId) {
        return suspiciousTransferService.getCardById(transferId);
    }

    @PutMapping("/cards/{transferId}")
    public SuspiciousCardTransferDto updateCard(@PathVariable Long transferId,
                                                @Valid @RequestBody SuspiciousCardTransferDto transferDto) {
        return suspiciousTransferService.updateCard(transferId, transferDto);
    }

    @DeleteMapping("/cards/{transferId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCard(@PathVariable Long transferId) {
        suspiciousTransferService.deleteCard(transferId);
    }

    @PostMapping("/phones")
    @ResponseStatus(HttpStatus.CREATED)
    public SuspiciousPhoneTransferDto createPhone(@Valid @RequestBody SuspiciousPhoneTransferDto transferDto) {
        return suspiciousTransferService.createPhone(transferDto);
    }

    @GetMapping("/phones")
    public List<SuspiciousPhoneTransferDto> getAllPhones() {
        return suspiciousTransferService.getAllPhones();
    }

    @GetMapping("/phones/{transferId}")
    public SuspiciousPhoneTransferDto getPhoneById(@PathVariable Long transferId) {
        return suspiciousTransferService.getPhoneById(transferId);
    }

    @PutMapping("/phones/{transferId}")
    public SuspiciousPhoneTransferDto updatePhone(@PathVariable Long transferId,
                                                  @Valid @RequestBody SuspiciousPhoneTransferDto transferDto) {
        return suspiciousTransferService.updatePhone(transferId, transferDto);
    }

    @DeleteMapping("/phones/{transferId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePhone(@PathVariable Long transferId) {
        suspiciousTransferService.deletePhone(transferId);
    }

    @PostMapping("/accounts")
    @ResponseStatus(HttpStatus.CREATED)
    public SuspiciousAccountTransferDto createAccount(@Valid @RequestBody SuspiciousAccountTransferDto transferDto) {
        return suspiciousTransferService.createAccount(transferDto);
    }

    @GetMapping("/accounts")
    public List<SuspiciousAccountTransferDto> getAllAccounts() {
        return suspiciousTransferService.getAllAccounts();
    }

    @GetMapping("/accounts/{transferId}")
    public SuspiciousAccountTransferDto getAccountById(@PathVariable Long transferId) {
        return suspiciousTransferService.getAccountById(transferId);
    }

    @PutMapping("/accounts/{transferId}")
    public SuspiciousAccountTransferDto updateAccount(@PathVariable Long transferId,
                                                      @Valid @RequestBody SuspiciousAccountTransferDto transferDto) {
        return suspiciousTransferService.updateAccount(transferId, transferDto);
    }

    @DeleteMapping("/accounts/{transferId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(@PathVariable Long transferId) {
        suspiciousTransferService.deleteAccount(transferId);
    }
}
