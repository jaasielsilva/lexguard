package com.jaasielsilva.lexguard.controller;

import com.jaasielsilva.lexguard.dto.user.UserCreateRequest;
import com.jaasielsilva.lexguard.dto.user.UserResponse;
import com.jaasielsilva.lexguard.service.UsuarioService;
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
@RequestMapping("/api/users")
@Validated
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USER_MANAGE') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<UserResponse> createUser(
            @RequestHeader("X-Empresa-Id") Long empresaId,
            @Valid @RequestBody UserCreateRequest request) {
        TenantContext.setEmpresaId(empresaId);
        try {
            return ResponseEntity.ok(usuarioService.create(request));
        } finally {
            TenantContext.clear();
        }
    }

    @GetMapping
    @PreAuthorize("hasAuthority('USER_MANAGE') or hasAuthority('TENANT_ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Set<UserResponse>> listUsers(@RequestHeader("X-Empresa-Id") Long empresaId) {
        TenantContext.setEmpresaId(empresaId);
        try {
            return ResponseEntity.ok(usuarioService.listAll());
        } finally {
            TenantContext.clear();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_MANAGE') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<UserResponse> getUser(
            @RequestHeader("X-Empresa-Id") Long empresaId,
            @PathVariable Long id) {
        TenantContext.setEmpresaId(empresaId);
        try {
            return ResponseEntity.ok(usuarioService.getById(id));
        } finally {
            TenantContext.clear();
        }
    }
}
