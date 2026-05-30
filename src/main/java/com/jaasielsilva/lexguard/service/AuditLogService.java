package com.jaasielsilva.lexguard.service;

import com.jaasielsilva.lexguard.dto.audit.AuditLogResponse;
import com.jaasielsilva.lexguard.repository.AuditLogRepository;
import com.jaasielsilva.lexguard.tenant.TenantContext;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public List<AuditLogResponse> listAll() {
        Long empresaId = TenantContext.getEmpresaId();
        return auditLogRepository.findByEmpresaId(empresaId).stream()
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .map(log -> new AuditLogResponse(
                        log.getId(),
                        log.getAction(),
                        log.getUsuario(),
                        log.getResource(),
                        log.getDescricao(),
                        log.getFinalidade(),
                        log.getBaseLegal(),
                        log.getTimestamp()))
                .collect(Collectors.toList());
    }
}
