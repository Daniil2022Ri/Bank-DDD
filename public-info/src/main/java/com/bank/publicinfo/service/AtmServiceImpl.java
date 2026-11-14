package com.bank.publicinfo.service;

import com.bank.publicinfo.dto.AtmDto;
import com.bank.publicinfo.entity.Atm;
import com.bank.publicinfo.entity.Branch;
import com.bank.publicinfo.exception.EntityNotFoundException;
import com.bank.publicinfo.mapper.PublicInfoMapper;
import com.bank.publicinfo.repository.AtmRepository;
import com.bank.publicinfo.repository.BranchRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AtmServiceImpl implements AtmService {

    private static final String ATM_NOT_FOUND = "Atm with id %d not found";
    private static final String BRANCH_NOT_FOUND = "Branch with id %d not found";

    private final AtmRepository atmRepository;
    private final BranchRepository branchRepository;
    private final PublicInfoMapper publicInfoMapper;

    @Override
    public AtmDto create(AtmDto atmDto) {
        final Atm atm = publicInfoMapper.toAtmEntity(atmDto);
        atm.setBranch(resolveBranch(atmDto.getBranchId()));
        return publicInfoMapper.toAtmDto(atmRepository.save(atm));
    }

    @Override
    @Transactional(readOnly = true)
    public AtmDto getById(Long atmId) {
        return publicInfoMapper.toAtmDto(findAtm(atmId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AtmDto> getAll() {
        return publicInfoMapper.toAtmDtoList(atmRepository.findAll());
    }

    @Override
    public AtmDto update(Long atmId, AtmDto atmDto) {
        final Atm atm = findAtm(atmId);
        publicInfoMapper.updateAtm(atmDto, atm);
        if (atmDto.getBranchId() != null) {
            atm.setBranch(resolveBranch(atmDto.getBranchId()));
        }
        return publicInfoMapper.toAtmDto(atmRepository.save(atm));
    }

    @Override
    public void delete(Long atmId) {
        atmRepository.delete(findAtm(atmId));
    }

    private Atm findAtm(Long atmId) {
        return atmRepository.findById(atmId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(ATM_NOT_FOUND, atmId)));
    }

    private Branch resolveBranch(Long branchId) {
        if (branchId == null) {
            return null;
        }
        return branchRepository.findById(branchId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(BRANCH_NOT_FOUND, branchId)));
    }
}
