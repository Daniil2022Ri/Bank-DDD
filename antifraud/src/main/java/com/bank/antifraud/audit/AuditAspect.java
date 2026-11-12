package com.bank.antifraud.audit;

import com.bank.antifraud.entity.EntityType;
import com.bank.antifraud.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private static final String SERVICE = "com.bank.antifraud.service.SuspiciousTransferServiceImpl";

    private final AuditService auditService;

    @AfterReturning(pointcut = "execution(* " + SERVICE + ".createCard(..))", returning = "result")
    public void auditCreateCard(Object result) {
        auditService.logCreate(EntityType.CARD_TRANSFER, result);
    }

    @AfterReturning(pointcut = "execution(* " + SERVICE + ".createPhone(..))", returning = "result")
    public void auditCreatePhone(Object result) {
        auditService.logCreate(EntityType.PHONE_TRANSFER, result);
    }

    @AfterReturning(pointcut = "execution(* " + SERVICE + ".createAccount(..))", returning = "result")
    public void auditCreateAccount(Object result) {
        auditService.logCreate(EntityType.ACCOUNT_TRANSFER, result);
    }

    @Around("execution(* " + SERVICE + ".updateCard(..)) && args(transferId, ..)")
    public Object auditUpdateCard(ProceedingJoinPoint joinPoint, Long transferId) throws Throwable {
        final String beforeJson = auditService.currentStateJson(EntityType.CARD_TRANSFER, transferId);
        final Object result = joinPoint.proceed();
        auditService.logUpdate(EntityType.CARD_TRANSFER, beforeJson, result);
        return result;
    }

    @Around("execution(* " + SERVICE + ".updatePhone(..)) && args(transferId, ..)")
    public Object auditUpdatePhone(ProceedingJoinPoint joinPoint, Long transferId) throws Throwable {
        final String beforeJson = auditService.currentStateJson(EntityType.PHONE_TRANSFER, transferId);
        final Object result = joinPoint.proceed();
        auditService.logUpdate(EntityType.PHONE_TRANSFER, beforeJson, result);
        return result;
    }

    @Around("execution(* " + SERVICE + ".updateAccount(..)) && args(transferId, ..)")
    public Object auditUpdateAccount(ProceedingJoinPoint joinPoint, Long transferId) throws Throwable {
        final String beforeJson = auditService.currentStateJson(EntityType.ACCOUNT_TRANSFER, transferId);
        final Object result = joinPoint.proceed();
        auditService.logUpdate(EntityType.ACCOUNT_TRANSFER, beforeJson, result);
        return result;
    }
}
