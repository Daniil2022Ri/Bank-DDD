package com.bank.account.Controller;

import com.bank.account.Dto.AccountDto;
import com.bank.account.Entity.AccountEntity;
import com.bank.account.Mapper.MapperAccount;
import com.bank.account.Repository.AccountRepository;
import com.bank.account.Service.AccountService;
import com.bank.account.ServiceImp.AccountServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/accountEntity") //base need fix
public class AccountController {



    private final AccountService accountService;
    private final MapperAccount mapperAccount;
    private final AccountRepository accountRepository;

    @GetMapping("/{UserID}")
    public ResponseEntity<AccountDto> getAccountBalance(@PathVariable Long userId){
        AccountDto accountDto = accountService.findBalanceAccountUser(userId);

        return ResponseEntity.ok(accountDto);
    }
}
