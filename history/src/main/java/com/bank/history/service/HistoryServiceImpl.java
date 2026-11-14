package com.bank.history.service;

import com.bank.history.dto.HistoryAggregateDto;
import com.bank.history.dto.HistoryDto;
import com.bank.history.entity.History;
import com.bank.history.entity.HistorySource;
import com.bank.history.exception.EntityNotFoundException;
import com.bank.history.mapper.HistoryMapper;
import com.bank.history.repository.HistoryRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class HistoryServiceImpl implements HistoryService {

    private static final String HISTORY_NOT_FOUND = "History entry with id %d not found";

    private final HistoryRepository historyRepository;
    private final HistoryMapper historyMapper;

    @Override
    public HistoryDto create(HistoryDto historyDto) {
        final History history = historyMapper.toEntity(historyDto);
        return historyMapper.toDto(historyRepository.save(history));
    }

    @Override
    @Transactional(readOnly = true)
    public HistoryDto getById(Long historyId) {
        return historyMapper.toDto(historyRepository.findById(historyId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(HISTORY_NOT_FOUND, historyId))));
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistoryDto> getAll() {
        return historyMapper.toDtoList(historyRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistoryDto> getByMicroservice(String microservice) {
        final HistorySource source = HistorySource.fromCode(microservice);
        return historyRepository.findAll().stream()
                .filter(history -> hasAudit(history, source))
                .map(historyMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public HistoryAggregateDto aggregate() {
        final List<History> histories = historyRepository.findAll();
        final Map<String, Long> counts = new LinkedHashMap<>();
        for (final HistorySource source : HistorySource.values()) {
            final long count = histories.stream().filter(history -> hasAudit(history, source)).count();
            counts.put(source.getCode(), count);
        }
        return HistoryAggregateDto.builder()
                .total(histories.size())
                .counts(counts)
                .entries(historyMapper.toDtoList(histories))
                .build();
    }

    private boolean hasAudit(History history, HistorySource source) {
        return switch (source) {
            case TRANSFER -> history.getTransferAuditId() != null;
            case PROFILE -> history.getProfileAuditId() != null;
            case ACCOUNT -> history.getAccountAuditId() != null;
            case ANTI_FRAUD -> history.getAntiFraudAuditId() != null;
            case PUBLIC_BANK_INFO -> history.getPublicBankInfoAuditId() != null;
            case AUTHORIZATION -> history.getAuthorizationAuditId() != null;
            default -> false;
        };
    }
}
