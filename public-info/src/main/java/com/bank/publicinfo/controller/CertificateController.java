package com.bank.publicinfo.controller;

import com.bank.publicinfo.dto.CertificateDto;
import com.bank.publicinfo.service.CertificateService;
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
@RequestMapping("/certificates")
@RequiredArgsConstructor
@Tag(name = "Certificates API", description = "Сертификаты банка")
public class CertificateController {

    private final CertificateService certificateService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CertificateDto create(@Valid @RequestBody CertificateDto certificateDto) {
        return certificateService.create(certificateDto);
    }

    @GetMapping
    public List<CertificateDto> getAll() {
        return certificateService.getAll();
    }

    @GetMapping("/{certificateId}")
    public CertificateDto getById(@PathVariable Long certificateId) {
        return certificateService.getById(certificateId);
    }

    @PutMapping("/{certificateId}")
    public CertificateDto update(@PathVariable Long certificateId,
                                 @Valid @RequestBody CertificateDto certificateDto) {
        return certificateService.update(certificateId, certificateDto);
    }

    @DeleteMapping("/{certificateId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long certificateId) {
        certificateService.delete(certificateId);
    }
}
