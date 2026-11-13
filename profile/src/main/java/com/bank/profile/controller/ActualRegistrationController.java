package com.bank.profile.controller;

import com.bank.profile.dto.ActualRegistrationDto;
import com.bank.profile.service.ActualRegistrationService;
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
@RequestMapping("/actual-registrations")
@RequiredArgsConstructor
@Tag(name = "Actual registrations API", description = "Управление фактическим местом проживания")
public class ActualRegistrationController {

    private final ActualRegistrationService actualRegistrationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ActualRegistrationDto create(@Valid @RequestBody ActualRegistrationDto registrationDto) {
        return actualRegistrationService.create(registrationDto);
    }

    @GetMapping
    public List<ActualRegistrationDto> getAll() {
        return actualRegistrationService.getAll();
    }

    @GetMapping("/{registrationId}")
    public ActualRegistrationDto getById(@PathVariable Long registrationId) {
        return actualRegistrationService.getById(registrationId);
    }

    @PutMapping("/{registrationId}")
    public ActualRegistrationDto update(@PathVariable Long registrationId,
                                        @Valid @RequestBody ActualRegistrationDto registrationDto) {
        return actualRegistrationService.update(registrationId, registrationDto);
    }

    @DeleteMapping("/{registrationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long registrationId) {
        actualRegistrationService.delete(registrationId);
    }
}
