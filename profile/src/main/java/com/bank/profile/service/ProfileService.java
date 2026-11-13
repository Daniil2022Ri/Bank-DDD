package com.bank.profile.service;

import com.bank.profile.dto.ProfileDto;
import java.util.List;

public interface ProfileService {

    ProfileDto create(ProfileDto profileDto);

    ProfileDto getById(Long profileId);

    List<ProfileDto> getAll();

    ProfileDto update(Long profileId, ProfileDto profileDto);

    void delete(Long profileId);
}
