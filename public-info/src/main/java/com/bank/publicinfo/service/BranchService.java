package com.bank.publicinfo.service;

import com.bank.publicinfo.dto.BranchDto;
import java.util.List;

public interface BranchService {

    BranchDto create(BranchDto branchDto);

    BranchDto getById(Long branchId);

    List<BranchDto> getAll();

    BranchDto update(Long branchId, BranchDto branchDto);

    void delete(Long branchId);
}
