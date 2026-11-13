package com.bank.profile.controller;

import com.bank.profile.dto.ProfileDto;
import com.bank.profile.service.ProfileService;
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
@RequestMapping("/profiles")
@RequiredArgsConstructor
@Tag(name = "Profiles API", description = "Управление банковскими профилями")
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileDto create(@Valid @RequestBody ProfileDto profileDto) {
        return profileService.create(profileDto);
    }

    @GetMapping
    public List<ProfileDto> getAll() {
        return profileService.getAll();
    }

    @GetMapping("/{profileId}")
    public ProfileDto getById(@PathVariable Long profileId) {
        return profileService.getById(profileId);
    }

    @PutMapping("/{profileId}")
    public ProfileDto update(@PathVariable Long profileId, @Valid @RequestBody ProfileDto profileDto) {
        return profileService.update(profileId, profileDto);
    }

    @DeleteMapping("/{profileId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long profileId) {
        profileService.delete(profileId);
    }
}
