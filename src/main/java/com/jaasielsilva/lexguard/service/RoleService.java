package com.jaasielsilva.lexguard.service;

import com.jaasielsilva.lexguard.dto.role.RolePermissionsUpdateRequest;
import com.jaasielsilva.lexguard.dto.role.RoleResponse;
import com.jaasielsilva.lexguard.exception.BadRequestException;
import com.jaasielsilva.lexguard.exception.ResourceNotFoundException;
import com.jaasielsilva.lexguard.model.Permission;
import com.jaasielsilva.lexguard.model.Role;
import com.jaasielsilva.lexguard.repository.RoleRepository;
import com.jaasielsilva.lexguard.security.StandardRoleTemplates;
import com.jaasielsilva.lexguard.tenant.TenantContext;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final StandardRolesProvisioner standardRolesProvisioner;

    public RoleService(RoleRepository roleRepository, StandardRolesProvisioner standardRolesProvisioner) {
        this.roleRepository = roleRepository;
        this.standardRolesProvisioner = standardRolesProvisioner;
    }

    public List<RoleResponse> listAll() {
        Long empresaId = requireEmpresaId();
        standardRolesProvisioner.ensureRolesExist(empresaId);
        return roleRepository.findAllByEmpresaIdOrderByNameAsc(empresaId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public RoleResponse getById(Long id) {
        Long empresaId = requireEmpresaId();
        Role role = findRole(id, empresaId);
        return toResponse(role);
    }

    @Transactional
    public RoleResponse updatePermissions(Long id, RolePermissionsUpdateRequest request) {
        Long empresaId = requireEmpresaId();
        Role role = findRole(id, empresaId);

        if (StandardRoleTemplates.SUPER_ADMIN.equals(role.getName())) {
            throw new BadRequestException("O perfil SUPER_ADMIN nao pode ser alterado pela API");
        }

        role.getPermissions().clear();
        role.getPermissions().addAll(request.getPermissions());
        role = roleRepository.saveAndFlush(role);
        return toResponse(role);
    }

    public Role requireAssignableRole(String roleName, Long empresaId) {
        standardRolesProvisioner.ensureRolesExist(empresaId);
        if (StandardRoleTemplates.isNonAssignable(roleName)) {
            throw new BadRequestException("Perfil nao pode ser atribuido: " + roleName);
        }
        if (!StandardRoleTemplates.isAssignable(roleName)) {
            throw new BadRequestException("Perfil invalido: " + roleName);
        }
        return roleRepository.findByNameAndEmpresaId(roleName, empresaId)
                .orElseThrow(() -> new BadRequestException("Perfil nao encontrado: " + roleName));
    }

    private Role findRole(Long id, Long empresaId) {
        return roleRepository.findById(id)
                .filter(r -> r.getEmpresaId().equals(empresaId))
                .orElseThrow(() -> new ResourceNotFoundException("Perfil nao encontrado"));
    }

    private RoleResponse toResponse(Role role) {
        boolean assignable = StandardRoleTemplates.isAssignable(role.getName());
        boolean systemRole = StandardRoleTemplates.all().containsKey(role.getName());
        return new RoleResponse(
                role.getId(),
                role.getName(),
                Set.copyOf(role.getPermissions()),
                assignable,
                systemRole);
    }

    private Long requireEmpresaId() {
        Long empresaId = TenantContext.getEmpresaId();
        if (empresaId == null) {
            throw new BadRequestException("Empresa nao informada");
        }
        return empresaId;
    }
}
