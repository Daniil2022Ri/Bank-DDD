package com.bank.account.Mapper;

import com.bank.account.Dto.CreditDetailsDto;
import com.bank.account.Entity.CreditDitalilsEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MapperCreditDitails {

    CreditDitalilsEntity creditDitalsEntityToDto(CreditDetailsDto creditDetailsDto);
    CreditDetailsDto creditDitalsDtoToEntity(CreditDitalilsEntity creditDitalilsEntity);

}
