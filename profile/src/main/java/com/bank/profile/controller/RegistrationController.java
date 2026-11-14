package com.bank.profile.controller;

import com.bank.profile.dto.RegistrationDto;
import com.bank.profile.service.RegistrationService;
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
@RequestMapping("/registrations")
@RequiredArgsConstructor
@Tag(name = "Registrations API", description = "Управление регистрационными данными")
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RegistrationDto create(@Valid @RequestBody RegistrationDto registrationDto) {
        return registrationService.create(registrationDto);
    }

    @GetMapping
    public List<RegistrationDto> getAll() {
        return registrationService.getAll();
    }

    @GetMapping("/{registrationId}")
    public RegistrationDto getById(@PathVariable Long registrationId) {
        return registrationService.getById(registrationId);
    }

    @PutMapping("/{registrationId}")
    public RegistrationDto update(@PathVariable Long registrationId,
                                  @Valid @RequestBody RegistrationDto registrationDto) {
        return registrationService.update(registrationId, registrationDto);
    }

    @DeleteMapping("/{registrationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long registrationId) {
        registrationService.delete(registrationId);
    }
}
