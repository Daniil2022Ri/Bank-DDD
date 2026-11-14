package com.bank.publicinfo.service;

import com.bank.publicinfo.dto.BranchDto;
import com.bank.publicinfo.entity.Branch;
import com.bank.publicinfo.exception.EntityNotFoundException;
import com.bank.publicinfo.mapper.PublicInfoMapper;
import com.bank.publicinfo.repository.BranchRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BranchServiceImpl implements BranchService {

    private static final String BRANCH_NOT_FOUND = "Branch with id %d not found";

    private final BranchRepository branchRepository;
    private final PublicInfoMapper publicInfoMapper;

    @Override
    public BranchDto create(BranchDto branchDto) {
        final Branch branch = publicInfoMapper.toBranchEntity(branchDto);
        return publicInfoMapper.toBranchDto(branchRepository.save(branch));
    }

    @Override
    @Transactional(readOnly = true)
    public BranchDto getById(Long branchId) {
        return publicInfoMapper.toBranchDto(findBranch(branchId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchDto> getAll() {
        return publicInfoMapper.toBranchDtoList(branchRepository.findAll());
    }

    @Override
    public BranchDto update(Long branchId, BranchDto branchDto) {
        final Branch branch = findBranch(branchId);
        publicInfoMapper.updateBranch(branchDto, branch);
        return publicInfoMapper.toBranchDto(branchRepository.save(branch));
    }

    @Override
    public void delete(Long branchId) {
        branchRepository.delete(findBranch(branchId));
    }

    private Branch findBranch(Long branchId) {
        return branchRepository.findById(branchId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(BRANCH_NOT_FOUND, branchId)));
    }
}
