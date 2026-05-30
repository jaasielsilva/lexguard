package com.jaasielsilva.lexguard.controller;

import com.jaasielsilva.lexguard.dto.compliance.ComplianceScoreResponse;
import com.jaasielsilva.lexguard.service.ComplianceScoreService;
import com.jaasielsilva.lexguard.tenant.TenantContext;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/compliance")
public class ComplianceController {

    private final ComplianceScoreService complianceScoreService;

    public ComplianceController(ComplianceScoreService complianceScoreService) {
        this.complianceScoreService = complianceScoreService;
    }

    /**
     * GET /api/compliance/score
     * Retorna o LGPD Compliance Score calculado em tempo real para o tenant.
     * Requer permissão REPORT_READ.
     */
    @GetMapping("/score")
    @PreAuthorize("hasAuthority('REPORT_READ')")
    public ResponseEntity<ComplianceScoreResponse> getScore(
            @RequestHeader("X-Empresa-Id") Long empresaId) {
        TenantContext.setEmpresaId(empresaId);
        try {
            return ResponseEntity.ok(complianceScoreService.calculate());
        } finally {
            TenantContext.clear();
        }
    }
}
