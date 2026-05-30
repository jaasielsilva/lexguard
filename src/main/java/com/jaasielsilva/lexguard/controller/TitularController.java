package com.jaasielsilva.lexguard.controller;

import com.jaasielsilva.lexguard.dto.titular.TitularRequest;
import com.jaasielsilva.lexguard.dto.titular.TitularResponse;
import com.jaasielsilva.lexguard.service.TitularService;
import com.jaasielsilva.lexguard.tenant.TenantContext;
import jakarta.validation.Valid;
import java.util.Set;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/titulares")
@Validated
public class TitularController {

    private final TitularService titularService;

    public TitularController(TitularService titularService) {
        this.titularService = titularService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('DATA_WRITE')")
    public ResponseEntity<TitularResponse> createTitular(
            @RequestHeader("X-Empresa-Id") Long empresaId,
            @RequestHeader("X-Usuario") String usuario,
            @Valid @RequestBody TitularRequest request) {
        TenantContext.setEmpresaId(empresaId);
        try {
            return ResponseEntity.ok(titularService.create(request, usuario));
        } finally {
            TenantContext.clear();
        }
    }

    @GetMapping
    @PreAuthorize("hasAuthority('DATA_READ')")
    public ResponseEntity<Set<TitularResponse>> listTitulares(@RequestHeader("X-Empresa-Id") Long empresaId) {
        TenantContext.setEmpresaId(empresaId);
        try {
            return ResponseEntity.ok(titularService.listAll());
        } finally {
            TenantContext.clear();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('DATA_READ')")
    public ResponseEntity<TitularResponse> getTitular(
            @RequestHeader("X-Empresa-Id") Long empresaId,
            @PathVariable Long id) {
        TenantContext.setEmpresaId(empresaId);
        try {
            return ResponseEntity.ok(titularService.getById(id));
        } finally {
            TenantContext.clear();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('DATA_WRITE')")
    public ResponseEntity<TitularResponse> updateTitular(
            @RequestHeader("X-Empresa-Id") Long empresaId,
            @RequestHeader("X-Usuario") String usuario,
            @PathVariable Long id,
            @Valid @RequestBody TitularRequest request) {
        TenantContext.setEmpresaId(empresaId);
        try {
            return ResponseEntity.ok(titularService.update(id, request, usuario));
        } finally {
            TenantContext.clear();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DATA_WRITE')")
    public ResponseEntity<Void> deleteTitular(
            @RequestHeader("X-Empresa-Id") Long empresaId,
            @RequestHeader("X-Usuario") String usuario,
            @PathVariable Long id) {
        TenantContext.setEmpresaId(empresaId);
        try {
            titularService.softDelete(id, usuario);
            return ResponseEntity.noContent().build();
        } finally {
            TenantContext.clear();
        }
    }
}
