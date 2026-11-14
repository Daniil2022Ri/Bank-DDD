package com.bank.profile.mapper;

import com.bank.profile.dto.RegistrationDto;
import com.bank.profile.entity.Registration;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RegistrationMapper {

    RegistrationDto toDto(Registration entity);

    List<RegistrationDto> toDtoList(List<Registration> entities);

    @Mapping(target = "id", ignore = true)
    Registration toEntity(RegistrationDto dto);

    @Mapping(target = "id", ignore = true)
    void updateEntity(RegistrationDto dto, @MappingTarget Registration entity);
}
