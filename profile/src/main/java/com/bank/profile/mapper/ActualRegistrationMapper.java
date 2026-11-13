package com.bank.profile.mapper;

import com.bank.profile.dto.ActualRegistrationDto;
import com.bank.profile.entity.ActualRegistration;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ActualRegistrationMapper {

    ActualRegistrationDto toDto(ActualRegistration entity);

    List<ActualRegistrationDto> toDtoList(List<ActualRegistration> entities);

    @Mapping(target = "id", ignore = true)
    ActualRegistration toEntity(ActualRegistrationDto dto);

    @Mapping(target = "id", ignore = true)
    void updateEntity(ActualRegistrationDto dto, @MappingTarget ActualRegistration entity);
}
