package com.bank.account.Mapper;

import com.bank.account.Dto.CreditDetailsDto;
import com.bank.account.Entity.CreditDetailsEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MapperCreditDitails {

    CreditDetailsEntity creditDitalsEntityToDto(CreditDetailsDto creditDetailsDto);
    CreditDetailsDto creditDitalsDtoToEntity(CreditDetailsEntity creditDetailsEntity);

}
