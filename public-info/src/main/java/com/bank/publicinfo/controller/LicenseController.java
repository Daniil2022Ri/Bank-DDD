package com.bank.publicinfo.controller;

import com.bank.publicinfo.dto.LicenseDto;
import com.bank.publicinfo.service.LicenseService;
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
@RequestMapping("/licenses")
@RequiredArgsConstructor
@Tag(name = "Licenses API", description = "Лицензии банка")
public class LicenseController {

    private final LicenseService licenseService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LicenseDto create(@Valid @RequestBody LicenseDto licenseDto) {
        return licenseService.create(licenseDto);
    }

    @GetMapping
    public List<LicenseDto> getAll() {
        return licenseService.getAll();
    }

    @GetMapping("/{licenseId}")
    public LicenseDto getById(@PathVariable Long licenseId) {
        return licenseService.getById(licenseId);
    }

    @PutMapping("/{licenseId}")
    public LicenseDto update(@PathVariable Long licenseId, @Valid @RequestBody LicenseDto licenseDto) {
        return licenseService.update(licenseId, licenseDto);
    }

    @DeleteMapping("/{licenseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long licenseId) {
        licenseService.delete(licenseId);
    }
}
