package com.bank.account.mapper;

import com.bank.account.dto.AccountDto;
import com.bank.account.entity.Account;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountDto toDto(Account account);

    List<AccountDto> toDtoList(List<Account> accounts);

    @Mapping(target = "id", ignore = true)
    Account toEntity(AccountDto accountDto);

    @Mapping(target = "id", ignore = true)
    void updateEntity(AccountDto accountDto, @MappingTarget Account account);
}
