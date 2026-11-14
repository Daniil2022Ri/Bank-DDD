package com.bank.profile.audit;

import com.bank.profile.service.AuditService;
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

    private static final String PROFILE_SERVICE = "com.bank.profile.service.ProfileServiceImpl";

    private final AuditService auditService;

    @AfterReturning(pointcut = "execution(* " + PROFILE_SERVICE + ".create(..))", returning = "result")
    public void auditCreate(Object result) {
        auditService.logCreate(result);
    }

    @Around("execution(* " + PROFILE_SERVICE + ".update(..)) && args(profileId, ..)")
    public Object auditUpdate(ProceedingJoinPoint joinPoint, Long profileId) throws Throwable {
        final String beforeJson = auditService.currentStateJson(profileId);
        final Object result = joinPoint.proceed();
        auditService.logUpdate(beforeJson, result);
        return result;
    }
}
