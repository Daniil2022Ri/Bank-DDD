package com.bank.profile.mapper;

import com.bank.profile.dto.PassportDto;
import com.bank.profile.entity.Passport;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PassportMapper {

    @Mapping(source = "registration.id", target = "registrationId")
    PassportDto toDto(Passport passport);

    @Mapping(source = "registration.id", target = "registrationId")
    List<PassportDto> toDtoList(List<Passport> passports);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "registration", ignore = true)
    Passport toEntity(PassportDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "registration", ignore = true)
    void updateEntity(PassportDto dto, @MappingTarget Passport passport);
}
