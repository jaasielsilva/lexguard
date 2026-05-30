package com.jaasielsilva.lexguard.controller;

import com.jaasielsilva.lexguard.dto.empresa.EmpresaRequest;
import com.jaasielsilva.lexguard.dto.empresa.EmpresaResponse;
import com.jaasielsilva.lexguard.service.EmpresaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/empresas")
@Validated
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<EmpresaResponse>> listAll() {
        return ResponseEntity.ok(empresaService.listAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<EmpresaResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(empresaService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<EmpresaResponse> create(@Valid @RequestBody EmpresaRequest request) {
        return ResponseEntity.ok(empresaService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<EmpresaResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody EmpresaRequest request) {
        return ResponseEntity.ok(empresaService.update(id, request));
    }

    @PatchMapping("/{id}/toggle-ativo")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<EmpresaResponse> toggleAtivo(@PathVariable Long id) {
        return ResponseEntity.ok(empresaService.toggleAtivo(id));
    }
}
