package com.bank.profile.mapper;

import com.bank.profile.dto.ProfileDto;
import com.bank.profile.entity.Profile;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    @Mapping(source = "passport.id", target = "passportId")
    @Mapping(source = "actualRegistration.id", target = "actualRegistrationId")
    ProfileDto toDto(Profile profile);

    @Mapping(source = "passport.id", target = "passportId")
    @Mapping(source = "actualRegistration.id", target = "actualRegistrationId")
    List<ProfileDto> toDtoList(List<Profile> profiles);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passport", ignore = true)
    @Mapping(target = "actualRegistration", ignore = true)
    Profile toEntity(ProfileDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passport", ignore = true)
    @Mapping(target = "actualRegistration", ignore = true)
    void updateEntity(ProfileDto dto, @MappingTarget Profile profile);
}
