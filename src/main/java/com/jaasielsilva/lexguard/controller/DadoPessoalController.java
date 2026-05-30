package com.jaasielsilva.lexguard.controller;

import com.jaasielsilva.lexguard.dto.dados.DadoPessoalRequest;
import com.jaasielsilva.lexguard.dto.dados.DadoPessoalResponse;
import com.jaasielsilva.lexguard.service.DadoPessoalService;
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
@RequestMapping("/api/dados")
@Validated
public class DadoPessoalController {

    private final DadoPessoalService dadoPessoalService;

    public DadoPessoalController(DadoPessoalService dadoPessoalService) {
        this.dadoPessoalService = dadoPessoalService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('DATA_WRITE')")
    public ResponseEntity<DadoPessoalResponse> createDado(
            @RequestHeader("X-Empresa-Id") Long empresaId,
            @RequestHeader("X-Usuario") String usuario,
            @Valid @RequestBody DadoPessoalRequest request) {
        TenantContext.setEmpresaId(empresaId);
        try {
            return ResponseEntity.ok(dadoPessoalService.create(request, usuario));
        } finally {
            TenantContext.clear();
        }
    }

    @GetMapping("/titular/{titularId}")
    @PreAuthorize("hasAuthority('DATA_READ')")
    public ResponseEntity<Set<DadoPessoalResponse>> listDados(
            @RequestHeader("X-Empresa-Id") Long empresaId,
            @RequestHeader("X-Usuario") String usuario,
            @PathVariable Long titularId) {
        TenantContext.setEmpresaId(empresaId);
        try {
            return ResponseEntity.ok(dadoPessoalService.listByTitular(titularId, usuario));
        } finally {
            TenantContext.clear();
        }
    }
}
