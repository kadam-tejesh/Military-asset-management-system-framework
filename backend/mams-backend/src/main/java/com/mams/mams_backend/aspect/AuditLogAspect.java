package com.mams.mams_backend.aspect;

import com.mams.mams_backend.dto.HasId;
import com.mams.mams_backend.entity.AuditLog;
import com.mams.mams_backend.repository.AuditLogRepository;
import com.mams.mams_backend.security.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogAspect {

    private final AuditLogRepository auditLogRepository;
    private final SecurityUtil securityUtil;

    @AfterReturning(pointcut = "@annotation(audited)", returning = "result")
    public void log(JoinPoint jp, Audited audited, Object result) {
        try {
            AuditLog entry = new AuditLog();
            entry.setUser(securityUtil.currentUser());
            entry.setAction(audited.action());
            entry.setEntityType(audited.entityType());
            if (result instanceof HasId h) {
                entry.setEntityId(h.id());
            }
            entry.setDetails(Arrays.toString(jp.getArgs()));

            if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs) {
                HttpServletRequest req = attrs.getRequest();
                String fwd = req.getHeader("X-Forwarded-For");
                entry.setIpAddress(fwd != null ? fwd.split(",")[0].trim() : req.getRemoteAddr());
            }
            auditLogRepository.save(entry);
        } catch (Exception e) {
            // auditing must never break the business operation
            System.err.println("Audit logging failed: " + e.getMessage());
        }
    }
}
