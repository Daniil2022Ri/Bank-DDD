package com.bank.account.audit;

import com.bank.account.dto.AccountDto;
import com.bank.account.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditService auditService;

    @Around("execution(* com.bank.account.service.AccountServiceImpl.create(..))")
    public Object auditCreate(ProceedingJoinPoint joinPoint) throws Throwable {
        final Object result = joinPoint.proceed();
        auditService.logCreate((AccountDto) result);
        return result;
    }

    @Around("execution(* com.bank.account.service.AccountServiceImpl.update(..)) && args(accountId, accountDto)")
    public Object auditUpdate(ProceedingJoinPoint joinPoint, Long accountId, AccountDto accountDto)
            throws Throwable {
        final String beforeJson = auditService.currentStateJson(accountId);
        final Object result = joinPoint.proceed();
        auditService.logUpdate(beforeJson, (AccountDto) result);
        return result;
    }
}
