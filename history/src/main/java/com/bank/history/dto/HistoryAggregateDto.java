package com.bank.history.dto;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryAggregateDto {

    private long total;
    private Map<String, Long> counts;
    private List<HistoryDto> entries;
}
