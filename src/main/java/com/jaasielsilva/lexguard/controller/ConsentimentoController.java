package com.jaasielsilva.lexguard.controller;

import com.jaasielsilva.lexguard.dto.consentimento.ConsentimentoRequest;
import com.jaasielsilva.lexguard.dto.consentimento.ConsentimentoResponse;
import com.jaasielsilva.lexguard.service.ConsentimentoService;
import com.jaasielsilva.lexguard.tenant.TenantContext;
import jakarta.validation.Valid;
import java.util.Set;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/consentimentos")
@Validated
public class ConsentimentoController {

    private final ConsentimentoService consentimentoService;

    public ConsentimentoController(ConsentimentoService consentimentoService) {
        this.consentimentoService = consentimentoService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CONSENT_MANAGE')")
    public ResponseEntity<ConsentimentoResponse> registerConsent(
            @RequestHeader("X-Empresa-Id") Long empresaId,
            @RequestHeader("X-Usuario") String usuario,
            @Valid @RequestBody ConsentimentoRequest request) {
        TenantContext.setEmpresaId(empresaId);
        try {
            return ResponseEntity.ok(consentimentoService.registerConsent(request, usuario));
        } finally {
            TenantContext.clear();
        }
    }

    @GetMapping("/titular/{titularId}")
    @PreAuthorize("hasAuthority('CONSENT_MANAGE')")
    public ResponseEntity<Set<ConsentimentoResponse>> listConsentimentos(
            @RequestHeader("X-Empresa-Id") Long empresaId,
            @PathVariable Long titularId) {
        TenantContext.setEmpresaId(empresaId);
        try {
            return ResponseEntity.ok(consentimentoService.listByTitular(titularId));
        } finally {
            TenantContext.clear();
        }
    }

    @PostMapping("/{id}/revoke")
    @PreAuthorize("hasAuthority('CONSENT_MANAGE')")
    public ResponseEntity<ConsentimentoResponse> revokeConsent(
            @RequestHeader("X-Empresa-Id") Long empresaId,
            @RequestHeader("X-Usuario") String usuario,
            @PathVariable Long id) {
        TenantContext.setEmpresaId(empresaId);
        try {
            return ResponseEntity.ok(consentimentoService.revokeConsent(id, usuario));
        } finally {
            TenantContext.clear();
        }
    }
}
