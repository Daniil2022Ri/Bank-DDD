package com.bank.publicinfo.controller;

import com.bank.publicinfo.dto.BankDetailsDto;
import com.bank.publicinfo.service.BankDetailsService;
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
@RequestMapping("/banks")
@RequiredArgsConstructor
@Tag(name = "Bank details API", description = "Публичные реквизиты банка")
public class BankDetailsController {

    private final BankDetailsService bankDetailsService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BankDetailsDto create(@Valid @RequestBody BankDetailsDto bankDetailsDto) {
        return bankDetailsService.create(bankDetailsDto);
    }

    @GetMapping
    public List<BankDetailsDto> getAll() {
        return bankDetailsService.getAll();
    }

    @GetMapping("/{bankDetailsId}")
    public BankDetailsDto getById(@PathVariable Long bankDetailsId) {
        return bankDetailsService.getById(bankDetailsId);
    }

    @PutMapping("/{bankDetailsId}")
    public BankDetailsDto update(@PathVariable Long bankDetailsId,
                                 @Valid @RequestBody BankDetailsDto bankDetailsDto) {
        return bankDetailsService.update(bankDetailsId, bankDetailsDto);
    }

    @DeleteMapping("/{bankDetailsId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long bankDetailsId) {
        bankDetailsService.delete(bankDetailsId);
    }
}
