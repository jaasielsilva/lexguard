package com.jaasielsilva.lexguard.config;

import com.jaasielsilva.lexguard.model.Empresa;
import com.jaasielsilva.lexguard.model.Permission;
import com.jaasielsilva.lexguard.model.Role;
import com.jaasielsilva.lexguard.model.Usuario;
import com.jaasielsilva.lexguard.repository.EmpresaRepository;
import com.jaasielsilva.lexguard.repository.RoleRepository;
import com.jaasielsilva.lexguard.repository.UsuarioRepository;
import java.util.EnumSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Executa uma única vez na inicialização da aplicação.
 * Cria a empresa raiz "LexGuard" e o usuário super admin caso ainda não
 * existam.
 */
@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    // CNPJ fictício reservado para a empresa raiz da plataforma
    private static final String LEXGUARD_CNPJ = "00.000.000/0001-00";
    private static final String SUPER_ADMIN_ROLE = "SUPER_ADMIN";

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

    public DataInitializer(
            EmpresaRepository empresaRepository,
            RoleRepository roleRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        this.empresaRepository = empresaRepository;
        this.roleRepository = roleRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Empresa empresa = provisionEmpresa();
        Role role = provisionRole(empresa.getId());
        provisionSuperAdmin(empresa.getId(), role);
    }

    // ─── Empresa raiz ────────────────────────────────────────────────────────

    private Empresa provisionEmpresa() {
        return empresaRepository.findByCnpj(LEXGUARD_CNPJ).orElseGet(() -> {
            Empresa e = new Empresa();
            e.setNome("LexGuard");
            e.setCnpj(LEXGUARD_CNPJ);
            e.setContatoEmail(superAdminEmail);
            e.setAtivo(true);
            // empresa raiz usa empresaId = 0 (sentinel — ela é a própria plataforma)
            e.setEmpresaId(0L);
            Empresa saved = empresaRepository.save(e);
            log.info("✅  Empresa raiz criada: LexGuard (id={})", saved.getId());
            return saved;
        });
    }

    // ─── Role SUPER_ADMIN ────────────────────────────────────────────────────

    private Role provisionRole(Long empresaId) {
        return roleRepository.findByNameAndEmpresaId(SUPER_ADMIN_ROLE, empresaId)
                .map(existing -> {
                    if (!existing.getPermissions().containsAll(EnumSet.allOf(Permission.class))) {
                        existing.setPermissions(EnumSet.allOf(Permission.class));
                        Role saved = roleRepository.save(existing);
                        log.info("✅  Role '{}' sincronizada com todas as permissões (empresaId={})", SUPER_ADMIN_ROLE,
                                empresaId);
                        return saved;
                    }
                    return existing;
                })
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setEmpresaId(empresaId);
                    r.setName(SUPER_ADMIN_ROLE);
                    r.setPermissions(EnumSet.allOf(Permission.class));
                    Role saved = roleRepository.save(r);
                    log.info("✅  Role '{}' criada com todas as permissões (empresaId={})", SUPER_ADMIN_ROLE,
                            empresaId);
                    return saved;
                });
    }

    // ─── Usuário super admin ─────────────────────────────────────────────────

    private void provisionSuperAdmin(Long empresaId, Role role) {
        usuarioRepository.findByUsernameAndEmpresaId(superAdminUsername, empresaId).ifPresentOrElse(
                u -> {
                    if (u.getRoles().stream().noneMatch(r -> SUPER_ADMIN_ROLE.equals(r.getName()))) {
                        u.getRoles().add(role);
                        usuarioRepository.save(u);
                        log.info("✅  Role '{}' vinculada ao super admin '{}'", SUPER_ADMIN_ROLE, u.getUsername());
                    } else {
                        log.info("ℹ️   Super admin '{}' já existe — nenhuma alteração feita.", u.getUsername());
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
                    log.info("✅  Super admin criado: username='{}' | empresaId={}", superAdminUsername, empresaId);
                    log.warn("⚠️   Altere a senha padrão do super admin em produção!");
                });
    }
}
