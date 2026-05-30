package com.jaasielsilva.lexguard.config;

import com.jaasielsilva.lexguard.model.Empresa;
import com.jaasielsilva.lexguard.model.Role;
import com.jaasielsilva.lexguard.model.Usuario;
import com.jaasielsilva.lexguard.repository.EmpresaRepository;
import com.jaasielsilva.lexguard.repository.RoleRepository;
import com.jaasielsilva.lexguard.repository.UsuarioRepository;
import com.jaasielsilva.lexguard.security.StandardRoleTemplates;
import com.jaasielsilva.lexguard.service.StandardRolesProvisioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Executa na inicializacao: empresa raiz, perfis padrao e super admin.
 */
@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private static final String LEXGUARD_CNPJ = "00.000.000/0001-00";

    @Value("${app.superadmin.username}")
    private String superAdminUsername;

    @Value("${app.superadmin.password}")
    private String superAdminPassword;

    @Value("${app.superadmin.nome}")
    private String superAdminNome;

    @Value("${app.superadmin.email}")
    private String superAdminEmail;

    private final EmpresaRepository empresaRepository;
    private final RoleRepository roleRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final StandardRolesProvisioner standardRolesProvisioner;

    public DataInitializer(
            EmpresaRepository empresaRepository,
            RoleRepository roleRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            StandardRolesProvisioner standardRolesProvisioner) {
        this.empresaRepository = empresaRepository;
        this.roleRepository = roleRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.standardRolesProvisioner = standardRolesProvisioner;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Empresa empresa = provisionEmpresa();
        standardRolesProvisioner.ensureForEmpresa(empresa.getId());
        Role superAdminRole = roleRepository
                .findByNameAndEmpresaId(StandardRoleTemplates.SUPER_ADMIN, empresa.getId())
                .orElseThrow();
        provisionSuperAdmin(empresa.getId(), superAdminRole);
    }

    private Empresa provisionEmpresa() {
        return empresaRepository.findByCnpj(LEXGUARD_CNPJ).orElseGet(() -> {
            Empresa e = new Empresa();
            e.setNome("LexGuard");
            e.setCnpj(LEXGUARD_CNPJ);
            e.setContatoEmail(superAdminEmail);
            e.setAtivo(true);
            e.setEmpresaId(0L);
            Empresa saved = empresaRepository.save(e);
            log.info("Empresa raiz criada: LexGuard (id={})", saved.getId());
            return saved;
        });
    }

    private void provisionSuperAdmin(Long empresaId, Role role) {
        usuarioRepository.findByUsernameAndEmpresaId(superAdminUsername, empresaId).ifPresentOrElse(
                u -> {
                    if (u.getRoles().stream().noneMatch(r -> StandardRoleTemplates.SUPER_ADMIN.equals(r.getName()))) {
                        u.getRoles().add(role);
                        usuarioRepository.save(u);
                        log.info("Perfil SUPER_ADMIN vinculado ao super admin '{}'", u.getUsername());
                    } else {
                        log.info("Super admin '{}' ja existe.", u.getUsername());
                    }
                },
                () -> {
                    Usuario u = new Usuario();
                    u.setEmpresaId(empresaId);
                    u.setUsername(superAdminUsername);
                    u.setPassword(passwordEncoder.encode(superAdminPassword));
                    u.setNome(superAdminNome);
                    u.setEmail(superAdminEmail);
                    u.setAtivo(true);
                    u.getRoles().add(role);
                    usuarioRepository.save(u);
                    log.info("Super admin criado: username='{}' | empresaId={}", superAdminUsername, empresaId);
                    log.warn("Altere a senha padrao do super admin em producao!");
                });
    }
}
