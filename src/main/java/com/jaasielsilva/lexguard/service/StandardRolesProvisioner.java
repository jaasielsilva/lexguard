package com.jaasielsilva.lexguard.service;

import com.jaasielsilva.lexguard.model.Permission;
import com.jaasielsilva.lexguard.model.Role;
import com.jaasielsilva.lexguard.repository.RoleRepository;
import com.jaasielsilva.lexguard.security.StandardRoleTemplates;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StandardRolesProvisioner {

    private static final Logger log = LoggerFactory.getLogger(StandardRolesProvisioner.class);

    private final RoleRepository roleRepository;

    public StandardRolesProvisioner(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Garante que os perfis padrao existam. Nao sobrescreve permissoes ja gravadas
     * (exceto SUPER_ADMIN, que sempre recebe todas as permissoes).
     */
    @Transactional
    public void ensureRolesExist(Long empresaId) {
        for (Map.Entry<String, Set<Permission>> entry : StandardRoleTemplates.all().entrySet()) {
            ensureRoleExists(empresaId, entry.getKey(), entry.getValue());
        }
    }

    private void ensureRoleExists(Long empresaId, String name, Set<Permission> defaultPermissions) {
        roleRepository.findByNameAndEmpresaId(name, empresaId)
                .ifPresentOrElse(
                        existing -> syncSuperAdminOnly(existing),
                        () -> {
                            Role role = new Role();
                            role.setEmpresaId(empresaId);
                            role.setName(name);
                            role.setPermissions(new HashSet<>(defaultPermissions));
                            roleRepository.save(role);
                            log.info("Perfil '{}' criado (empresaId={})", name, empresaId);
                        });
    }

    private void syncSuperAdminOnly(Role existing) {
        if (!StandardRoleTemplates.SUPER_ADMIN.equals(existing.getName())) {
            return;
        }
        Set<Permission> all = EnumSet.allOf(Permission.class);
        if (!existing.getPermissions().containsAll(all)) {
            replacePermissions(existing, all);
            roleRepository.save(existing);
            log.info("Perfil SUPER_ADMIN sincronizado com todas as permissoes (empresaId={})",
                    existing.getEmpresaId());
        }
    }

    private static void replacePermissions(Role role, Set<Permission> permissions) {
        role.getPermissions().clear();
        role.getPermissions().addAll(permissions);
    }
}
