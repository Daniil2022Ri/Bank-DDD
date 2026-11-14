package com.bank.history.service;

import com.bank.history.dto.HistoryAggregateDto;
import com.bank.history.dto.HistoryDto;
import java.util.List;

public interface HistoryService {

    HistoryDto create(HistoryDto historyDto);

    HistoryDto getById(Long historyId);

    List<HistoryDto> getAll();

    List<HistoryDto> getByMicroservice(String microservice);

    HistoryAggregateDto aggregate();
}
