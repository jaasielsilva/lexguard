package com.jaasielsilva.lexguard.service;

import com.jaasielsilva.lexguard.model.Permission;
import com.jaasielsilva.lexguard.model.Role;
import com.jaasielsilva.lexguard.repository.RoleRepository;
import com.jaasielsilva.lexguard.security.StandardRoleTemplates;
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

    @Transactional
    public void ensureForEmpresa(Long empresaId) {
        for (Map.Entry<String, Set<Permission>> entry : StandardRoleTemplates.all().entrySet()) {
            syncRole(empresaId, entry.getKey(), entry.getValue());
        }
    }

    private void syncRole(Long empresaId, String name, Set<Permission> expected) {
        roleRepository.findByNameAndEmpresaId(name, empresaId)
                .ifPresentOrElse(
                        existing -> {
                            if (!existing.getPermissions().containsAll(expected)
                                    || !expected.containsAll(existing.getPermissions())) {
                                existing.setPermissions(new HashSet<>(expected));
                                roleRepository.save(existing);
                                log.info("Perfil '{}' sincronizado (empresaId={})", name, empresaId);
                            }
                        },
                        () -> {
                            Role role = new Role();
                            role.setEmpresaId(empresaId);
                            role.setName(name);
                            role.setPermissions(new HashSet<>(expected));
                            roleRepository.save(role);
                            log.info("Perfil '{}' criado (empresaId={})", name, empresaId);
                        });
    }
}
