package com.jaasielsilva.lexguard.controller;

import com.jaasielsilva.lexguard.dto.solicitacao.SolicitacaoTitularRequest;
import com.jaasielsilva.lexguard.dto.solicitacao.SolicitacaoTitularResponse;
import com.jaasielsilva.lexguard.model.RequestStatus;
import com.jaasielsilva.lexguard.service.SolicitacaoTitularService;
import com.jaasielsilva.lexguard.tenant.TenantContext;
import jakarta.validation.Valid;
import java.util.Set;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/solicitacoes")
@Validated
public class SolicitacaoTitularController {

    private final SolicitacaoTitularService solicitacaoService;

    public SolicitacaoTitularController(SolicitacaoTitularService solicitacaoService) {
        this.solicitacaoService = solicitacaoService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('REQUEST_MANAGE')")
    public ResponseEntity<SolicitacaoTitularResponse> createSolicitacao(
            @RequestHeader("X-Empresa-Id") Long empresaId,
            @RequestHeader("X-Usuario") String usuario,
            @Valid @RequestBody SolicitacaoTitularRequest request) {
        TenantContext.setEmpresaId(empresaId);
        try {
            return ResponseEntity.ok(solicitacaoService.createRequest(request, usuario));
        } finally {
            TenantContext.clear();
        }
    }

    @GetMapping
    @PreAuthorize("hasAuthority('REQUEST_MANAGE')")
    public ResponseEntity<Set<SolicitacaoTitularResponse>> listSolicitacoes(
            @RequestHeader("X-Empresa-Id") Long empresaId) {
        TenantContext.setEmpresaId(empresaId);
        try {
            return ResponseEntity.ok(solicitacaoService.listAll());
        } finally {
            TenantContext.clear();
        }
    }

    @PutMapping("/{id}/respond")
    @PreAuthorize("hasAuthority('REQUEST_MANAGE')")
    public ResponseEntity<SolicitacaoTitularResponse> respondSolicitacao(
            @RequestHeader("X-Empresa-Id") Long empresaId,
            @RequestHeader("X-Usuario") String usuario,
            @PathVariable Long id,
            @RequestBody SolicitacaoTitularResponse request) {
        TenantContext.setEmpresaId(empresaId);
        try {
            return ResponseEntity.ok(solicitacaoService.respondToRequest(
                    id, request.getResposta(), usuario, RequestStatus.ATENDIDO, usuario));
        } finally {
            TenantContext.clear();
        }
    }
}
