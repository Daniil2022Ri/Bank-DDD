package com.bank.account.Mapper;

import com.bank.account.Dto.AccountDto;
import com.bank.account.Entity.AccountEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MapperAccount {

    AccountEntity accountDtoToEntity(AccountDto accountDto);
    AccountDto accountEntityToDto(AccountEntity accountEntity);

}
