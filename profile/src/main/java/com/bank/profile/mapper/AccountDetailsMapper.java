package com.bank.profile.mapper;

import com.bank.profile.dto.AccountDetailsDto;
import com.bank.profile.entity.AccountDetails;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AccountDetailsMapper {

    @Mapping(source = "profile.id", target = "profileId")
    AccountDetailsDto toDto(AccountDetails accountDetails);

    @Mapping(source = "profile.id", target = "profileId")
    List<AccountDetailsDto> toDtoList(List<AccountDetails> accountDetails);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "profile", ignore = true)
    AccountDetails toEntity(AccountDetailsDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "profile", ignore = true)
    void updateEntity(AccountDetailsDto dto, @MappingTarget AccountDetails accountDetails);
}
