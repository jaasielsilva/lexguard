package com.jaasielsilva.lexguard.controller;

import com.jaasielsilva.lexguard.dto.relatorio.RelatorioResponse;
import com.jaasielsilva.lexguard.model.ReportType;
import com.jaasielsilva.lexguard.service.RelatorioService;
import com.jaasielsilva.lexguard.tenant.TenantContext;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/relatorios")
@Validated
public class RelatorioController {

    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('REPORT_READ')")
    public ResponseEntity<RelatorioResponse> generateReport(
            @RequestHeader("X-Empresa-Id") Long empresaId,
            @RequestHeader("X-Usuario") String usuario,
            @RequestParam @NotBlank String titulo,
            @RequestParam(required = false) String descricao,
            @RequestParam @NotNull ReportType tipo,
            @RequestParam @NotBlank String conteudo) {
        TenantContext.setEmpresaId(empresaId);
        try {
            return ResponseEntity.ok(relatorioService.generateReport(titulo, descricao, tipo, conteudo, usuario));
        } finally {
            TenantContext.clear();
        }
    }

    @GetMapping
    @PreAuthorize("hasAuthority('REPORT_READ')")
    public ResponseEntity<Set<RelatorioResponse>> listReports(@RequestHeader("X-Empresa-Id") Long empresaId) {
        TenantContext.setEmpresaId(empresaId);
        try {
            return ResponseEntity.ok(relatorioService.listReports());
        } finally {
            TenantContext.clear();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('REPORT_READ')")
    public ResponseEntity<RelatorioResponse> getReport(
            @RequestHeader("X-Empresa-Id") Long empresaId,
            @PathVariable Long id) {
        TenantContext.setEmpresaId(empresaId);
        try {
            return ResponseEntity.ok(relatorioService.getReport(id));
        } finally {
            TenantContext.clear();
        }
    }
}
