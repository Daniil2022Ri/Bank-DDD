package com.bank.publicinfo.service;

import com.bank.publicinfo.dto.BankDetailsDto;
import com.bank.publicinfo.entity.BankDetails;
import com.bank.publicinfo.exception.EntityNotFoundException;
import com.bank.publicinfo.mapper.PublicInfoMapper;
import com.bank.publicinfo.repository.BankDetailsRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BankDetailsServiceImpl implements BankDetailsService {

    private static final String BANK_DETAILS_NOT_FOUND = "Bank details with id %d not found";

    private final BankDetailsRepository bankDetailsRepository;
    private final PublicInfoMapper publicInfoMapper;

    @Override
    public BankDetailsDto create(BankDetailsDto bankDetailsDto) {
        final BankDetails bankDetails = publicInfoMapper.toBankDetailsEntity(bankDetailsDto);
        return publicInfoMapper.toBankDetailsDto(bankDetailsRepository.save(bankDetails));
    }

    @Override
    @Transactional(readOnly = true)
    public BankDetailsDto getById(Long bankDetailsId) {
        return publicInfoMapper.toBankDetailsDto(findBankDetails(bankDetailsId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BankDetailsDto> getAll() {
        return publicInfoMapper.toBankDetailsDtoList(bankDetailsRepository.findAll());
    }

    @Override
    public BankDetailsDto update(Long bankDetailsId, BankDetailsDto bankDetailsDto) {
        final BankDetails bankDetails = findBankDetails(bankDetailsId);
        publicInfoMapper.updateBankDetails(bankDetailsDto, bankDetails);
        return publicInfoMapper.toBankDetailsDto(bankDetailsRepository.save(bankDetails));
    }

    @Override
    public void delete(Long bankDetailsId) {
        bankDetailsRepository.delete(findBankDetails(bankDetailsId));
    }

    private BankDetails findBankDetails(Long bankDetailsId) {
        return bankDetailsRepository.findById(bankDetailsId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(BANK_DETAILS_NOT_FOUND, bankDetailsId)));
    }
}
