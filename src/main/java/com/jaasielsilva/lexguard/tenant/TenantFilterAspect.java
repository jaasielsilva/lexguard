package com.jaasielsilva.lexguard.tenant;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class TenantFilterAspect {

    private static final Logger logger = LoggerFactory.getLogger(TenantFilterAspect.class);

    private final EntityManager entityManager;

    @Around("execution(* com.jaasielsilva.lexguard.service..*(..))")
    public Object applyTenantFilter(ProceedingJoinPoint joinPoint) throws Throwable {
        Long empresaId = TenantContext.getEmpresaId();
        if (empresaId != null) {
            try {
                Session session = entityManager.unwrap(Session.class);
                if (session.getEnabledFilter("tenantFilter") == null) {
                    session.enableFilter("tenantFilter").setParameter("empresaId", empresaId);
                }
            } catch (Exception ex) {
                logger.debug("Unable to apply tenant filter: {}", ex.getMessage());
            }
        }
        return joinPoint.proceed();
    }
}
