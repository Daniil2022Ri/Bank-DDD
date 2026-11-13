package com.bank.profile.controller;

import com.bank.profile.dto.PassportDto;
import com.bank.profile.service.PassportService;
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
@RequestMapping("/passports")
@RequiredArgsConstructor
@Tag(name = "Passports API", description = "Управление паспортными данными")
public class PassportController {

    private final PassportService passportService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PassportDto create(@Valid @RequestBody PassportDto passportDto) {
        return passportService.create(passportDto);
    }

    @GetMapping
    public List<PassportDto> getAll() {
        return passportService.getAll();
    }

    @GetMapping("/{passportId}")
    public PassportDto getById(@PathVariable Long passportId) {
        return passportService.getById(passportId);
    }

    @PutMapping("/{passportId}")
    public PassportDto update(@PathVariable Long passportId, @Valid @RequestBody PassportDto passportDto) {
        return passportService.update(passportId, passportDto);
    }

    @DeleteMapping("/{passportId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long passportId) {
        passportService.delete(passportId);
    }
}
