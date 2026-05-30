package com.jaasielsilva.lexguard.controller;

import com.jaasielsilva.lexguard.dto.audit.AuditLogSearchPageResponse;
import com.jaasielsilva.lexguard.service.AuditLogService;
import com.jaasielsilva.lexguard.tenant.TenantContext;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ResponseEntity<AuditLogSearchPageResponse> listAuditLogs(
            @RequestHeader("X-Empresa-Id") Long empresaId,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String actionGroup,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size) {
        TenantContext.setEmpresaId(empresaId);
        try {
            return ResponseEntity.ok(auditLogService.search(q, actionGroup, page, size));
        } finally {
            TenantContext.clear();
        }
    }
}
