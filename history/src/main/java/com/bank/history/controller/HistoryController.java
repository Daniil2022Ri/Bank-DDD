package com.bank.history.controller;

import com.bank.history.dto.HistoryAggregateDto;
import com.bank.history.dto.HistoryDto;
import com.bank.history.service.HistoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "History API", description = "История изменений и агрегация аудита микросервисов")
public class HistoryController {

    private final HistoryService historyService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HistoryDto create(@Valid @RequestBody HistoryDto historyDto) {
        return historyService.create(historyDto);
    }

    @GetMapping
    public List<HistoryDto> getAll() {
        return historyService.getAll();
    }

    @GetMapping("/aggregate")
    public HistoryAggregateDto aggregate() {
        return historyService.aggregate();
    }

    @GetMapping("/id/{historyId}")
    public HistoryDto getById(@PathVariable Long historyId) {
        return historyService.getById(historyId);
    }

    @GetMapping("/{microservice}")
    public List<HistoryDto> getByMicroservice(@PathVariable String microservice) {
        return historyService.getByMicroservice(microservice);
    }
}
