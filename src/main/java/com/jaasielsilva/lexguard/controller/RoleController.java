package com.jaasielsilva.lexguard.controller;

import com.jaasielsilva.lexguard.dto.role.RolePermissionsUpdateRequest;
import com.jaasielsilva.lexguard.dto.role.RoleResponse;
import com.jaasielsilva.lexguard.service.RoleService;
import com.jaasielsilva.lexguard.tenant.TenantContext;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/roles")
@Validated
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('USER_MANAGE') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<RoleResponse>> list(
            @RequestHeader("X-Empresa-Id") Long empresaId) {
        TenantContext.setEmpresaId(empresaId);
        try {
            return ResponseEntity.ok(roleService.listAll());
        } finally {
            TenantContext.clear();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_MANAGE') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<RoleResponse> get(
            @RequestHeader("X-Empresa-Id") Long empresaId,
            @PathVariable Long id) {
        TenantContext.setEmpresaId(empresaId);
        try {
            return ResponseEntity.ok(roleService.getById(id));
        } finally {
            TenantContext.clear();
        }
    }

    @PutMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('USER_MANAGE') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<RoleResponse> updatePermissions(
            @RequestHeader("X-Empresa-Id") Long empresaId,
            @PathVariable Long id,
            @Valid @RequestBody RolePermissionsUpdateRequest request) {
        TenantContext.setEmpresaId(empresaId);
        try {
            return ResponseEntity.ok(roleService.updatePermissions(id, request));
        } finally {
            TenantContext.clear();
        }
    }
}
