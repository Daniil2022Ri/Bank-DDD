package com.bank.publicinfo.controller;

import com.bank.publicinfo.dto.AtmDto;
import com.bank.publicinfo.service.AtmService;
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
@RequestMapping("/atms")
@RequiredArgsConstructor
@Tag(name = "Atms API", description = "Банкоматы")
public class AtmController {

    private final AtmService atmService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AtmDto create(@Valid @RequestBody AtmDto atmDto) {
        return atmService.create(atmDto);
    }

    @GetMapping
    public List<AtmDto> getAll() {
        return atmService.getAll();
    }

    @GetMapping("/{atmId}")
    public AtmDto getById(@PathVariable Long atmId) {
        return atmService.getById(atmId);
    }

    @PutMapping("/{atmId}")
    public AtmDto update(@PathVariable Long atmId, @Valid @RequestBody AtmDto atmDto) {
        return atmService.update(atmId, atmDto);
    }

    @DeleteMapping("/{atmId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long atmId) {
        atmService.delete(atmId);
    }
}
