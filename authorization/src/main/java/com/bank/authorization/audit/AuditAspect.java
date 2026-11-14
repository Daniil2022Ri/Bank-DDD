package com.bank.authorization.audit;

import com.bank.authorization.service.AuditService;
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

    private static final String USER_SERVICE = "com.bank.authorization.service.UserServiceImpl";

    private final AuditService auditService;

    @AfterReturning(pointcut = "execution(* " + USER_SERVICE + ".create(..))", returning = "result")
    public void auditCreate(Object result) {
        auditService.logCreate(result);
    }

    @Around("execution(* " + USER_SERVICE + ".update(..)) && args(userId, ..)")
    public Object auditUpdate(ProceedingJoinPoint joinPoint, Long userId) throws Throwable {
        final String beforeJson = auditService.currentStateJson(userId);
        final Object result = joinPoint.proceed();
        auditService.logUpdate(beforeJson, result);
        return result;
    }
}
