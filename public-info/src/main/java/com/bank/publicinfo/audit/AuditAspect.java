package com.bank.publicinfo.audit;

import com.bank.publicinfo.entity.EntityType;
import com.bank.publicinfo.service.AuditService;
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

    private static final String BANK_SERVICE = "com.bank.publicinfo.service.BankDetailsServiceImpl";
    private static final String BRANCH_SERVICE = "com.bank.publicinfo.service.BranchServiceImpl";
    private static final String ATM_SERVICE = "com.bank.publicinfo.service.AtmServiceImpl";
    private static final String CERTIFICATE_SERVICE = "com.bank.publicinfo.service.CertificateServiceImpl";
    private static final String LICENSE_SERVICE = "com.bank.publicinfo.service.LicenseServiceImpl";

    private final AuditService auditService;

    @AfterReturning(pointcut = "execution(* " + BANK_SERVICE + ".create(..))", returning = "result")
    public void auditCreateBankDetails(Object result) {
        auditService.logCreate(EntityType.BANK_DETAILS, result);
    }

    @Around("execution(* " + BANK_SERVICE + ".update(..)) && args(entityId, ..)")
    public Object auditUpdateBankDetails(ProceedingJoinPoint joinPoint, Long entityId) throws Throwable {
        return auditUpdate(joinPoint, entityId, EntityType.BANK_DETAILS);
    }

    @AfterReturning(pointcut = "execution(* " + BRANCH_SERVICE + ".create(..))", returning = "result")
    public void auditCreateBranch(Object result) {
        auditService.logCreate(EntityType.BRANCH, result);
    }

    @Around("execution(* " + BRANCH_SERVICE + ".update(..)) && args(entityId, ..)")
    public Object auditUpdateBranch(ProceedingJoinPoint joinPoint, Long entityId) throws Throwable {
        return auditUpdate(joinPoint, entityId, EntityType.BRANCH);
    }

    @AfterReturning(pointcut = "execution(* " + ATM_SERVICE + ".create(..))", returning = "result")
    public void auditCreateAtm(Object result) {
        auditService.logCreate(EntityType.ATM, result);
    }

    @Around("execution(* " + ATM_SERVICE + ".update(..)) && args(entityId, ..)")
    public Object auditUpdateAtm(ProceedingJoinPoint joinPoint, Long entityId) throws Throwable {
        return auditUpdate(joinPoint, entityId, EntityType.ATM);
    }

    @AfterReturning(pointcut = "execution(* " + CERTIFICATE_SERVICE + ".create(..))", returning = "result")
    public void auditCreateCertificate(Object result) {
        auditService.logCreate(EntityType.CERTIFICATE, result);
    }

    @Around("execution(* " + CERTIFICATE_SERVICE + ".update(..)) && args(entityId, ..)")
    public Object auditUpdateCertificate(ProceedingJoinPoint joinPoint, Long entityId) throws Throwable {
        return auditUpdate(joinPoint, entityId, EntityType.CERTIFICATE);
    }

    @AfterReturning(pointcut = "execution(* " + LICENSE_SERVICE + ".create(..))", returning = "result")
    public void auditCreateLicense(Object result) {
        auditService.logCreate(EntityType.LICENSE, result);
    }

    @Around("execution(* " + LICENSE_SERVICE + ".update(..)) && args(entityId, ..)")
    public Object auditUpdateLicense(ProceedingJoinPoint joinPoint, Long entityId) throws Throwable {
        return auditUpdate(joinPoint, entityId, EntityType.LICENSE);
    }

    private Object auditUpdate(ProceedingJoinPoint joinPoint, Long entityId, EntityType entityType)
            throws Throwable {
        final String beforeJson = auditService.currentStateJson(entityType, entityId);
        final Object result = joinPoint.proceed();
        auditService.logUpdate(entityType, beforeJson, result);
        return result;
    }
}
