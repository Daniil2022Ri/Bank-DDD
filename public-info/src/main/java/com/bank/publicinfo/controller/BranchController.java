package com.bank.publicinfo.controller;

import com.bank.publicinfo.dto.BranchDto;
import com.bank.publicinfo.service.BranchService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/branches")
@RequiredArgsConstructor
@Tag(name = "Branches API", description = "Отделения банка")
public class BranchController {

    private final BranchService branchService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BranchDto create(@Valid @RequestBody BranchDto branchDto) {
        return branchService.create(branchDto);
    }

    @GetMapping
    public List<BranchDto> getAll() {
        return branchService.getAll();
    }

    @GetMapping("/{branchId}")
    public BranchDto getById(@PathVariable Long branchId) {
        return branchService.getById(branchId);
    }

    @PutMapping("/{branchId}")
    public BranchDto update(@PathVariable Long branchId, @Valid @RequestBody BranchDto branchDto) {
        return branchService.update(branchId, branchDto);
    }

    @DeleteMapping("/{branchId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long branchId) {
        branchService.delete(branchId);
    }
}
