package com.jaasielsilva.lexguard.controller;

import com.jaasielsilva.lexguard.dto.audit.AuditLogResponse;
import com.jaasielsilva.lexguard.service.AuditLogService;
import com.jaasielsilva.lexguard.tenant.TenantContext;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audit")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('AUDIT_READ')")
    public ResponseEntity<List<AuditLogResponse>> listAuditLogs(
            @RequestHeader("X-Empresa-Id") Long empresaId) {
        TenantContext.setEmpresaId(empresaId);
        try {
            return ResponseEntity.ok(auditLogService.listAll());
        } finally {
            TenantContext.clear();
        }
    }
}
