package com.bank.transfer.mapper;

import com.bank.transfer.dto.AccountTransferDto;
import com.bank.transfer.dto.CardTransferDto;
import com.bank.transfer.dto.PhoneTransferDto;
import com.bank.transfer.entity.AccountTransfer;
import com.bank.transfer.entity.CardTransfer;
import com.bank.transfer.entity.PhoneTransfer;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TransferMapper {

    AccountTransferDto toAccountDto(AccountTransfer entity);

    List<AccountTransferDto> toAccountDtoList(List<AccountTransfer> entities);

    @Mapping(target = "id", ignore = true)
    AccountTransfer toAccountEntity(AccountTransferDto dto);

    @Mapping(target = "id", ignore = true)
    void updateAccount(AccountTransferDto dto, @MappingTarget AccountTransfer entity);

    CardTransferDto toCardDto(CardTransfer entity);

    List<CardTransferDto> toCardDtoList(List<CardTransfer> entities);

    @Mapping(target = "id", ignore = true)
    CardTransfer toCardEntity(CardTransferDto dto);

    @Mapping(target = "id", ignore = true)
    void updateCard(CardTransferDto dto, @MappingTarget CardTransfer entity);

    PhoneTransferDto toPhoneDto(PhoneTransfer entity);

    List<PhoneTransferDto> toPhoneDtoList(List<PhoneTransfer> entities);

    @Mapping(target = "id", ignore = true)
    PhoneTransfer toPhoneEntity(PhoneTransferDto dto);

    @Mapping(target = "id", ignore = true)
    void updatePhone(PhoneTransferDto dto, @MappingTarget PhoneTransfer entity);
}
