package com.bank.antifraud.mapper;

import com.bank.antifraud.dto.SuspiciousAccountTransferDto;
import com.bank.antifraud.dto.SuspiciousCardTransferDto;
import com.bank.antifraud.dto.SuspiciousPhoneTransferDto;
import com.bank.antifraud.entity.SuspiciousAccountTransfer;
import com.bank.antifraud.entity.SuspiciousCardTransfer;
import com.bank.antifraud.entity.SuspiciousPhoneTransfer;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SuspiciousTransferMapper {

    SuspiciousCardTransferDto toCardDto(SuspiciousCardTransfer entity);

    List<SuspiciousCardTransferDto> toCardDtoList(List<SuspiciousCardTransfer> entities);

    @Mapping(target = "id", ignore = true)
    SuspiciousCardTransfer toCardEntity(SuspiciousCardTransferDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transferId", ignore = true)
    void updateCard(SuspiciousCardTransferDto dto, @MappingTarget SuspiciousCardTransfer entity);

    SuspiciousPhoneTransferDto toPhoneDto(SuspiciousPhoneTransfer entity);

    List<SuspiciousPhoneTransferDto> toPhoneDtoList(List<SuspiciousPhoneTransfer> entities);

    @Mapping(target = "id", ignore = true)
    SuspiciousPhoneTransfer toPhoneEntity(SuspiciousPhoneTransferDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transferId", ignore = true)
    void updatePhone(SuspiciousPhoneTransferDto dto, @MappingTarget SuspiciousPhoneTransfer entity);

    SuspiciousAccountTransferDto toAccountDto(SuspiciousAccountTransfer entity);

    List<SuspiciousAccountTransferDto> toAccountDtoList(List<SuspiciousAccountTransfer> entities);

    @Mapping(target = "id", ignore = true)
    SuspiciousAccountTransfer toAccountEntity(SuspiciousAccountTransferDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transferId", ignore = true)
    void updateAccount(SuspiciousAccountTransferDto dto, @MappingTarget SuspiciousAccountTransfer entity);
}
