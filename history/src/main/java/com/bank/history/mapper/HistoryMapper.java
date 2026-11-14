package com.bank.history.mapper;

import com.bank.history.dto.HistoryDto;
import com.bank.history.entity.History;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HistoryMapper {

    HistoryDto toDto(History history);

    List<HistoryDto> toDtoList(List<History> histories);

    @Mapping(target = "id", ignore = true)
    History toEntity(HistoryDto dto);
}
