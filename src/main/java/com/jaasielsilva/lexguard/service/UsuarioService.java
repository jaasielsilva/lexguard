package com.jaasielsilva.lexguard.service;

import com.jaasielsilva.lexguard.dto.user.UserCreateRequest;
import com.jaasielsilva.lexguard.dto.user.UserResponse;
import com.jaasielsilva.lexguard.exception.BadRequestException;
import com.jaasielsilva.lexguard.exception.ResourceNotFoundException;
import com.jaasielsilva.lexguard.model.Role;
import com.jaasielsilva.lexguard.model.Usuario;
import com.jaasielsilva.lexguard.repository.RoleRepository;
import com.jaasielsilva.lexguard.repository.UsuarioRepository;
import com.jaasielsilva.lexguard.tenant.TenantContext;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse create(UserCreateRequest request) {
        Long empresaId = TenantContext.getEmpresaId();
        if (empresaId == null) {
            throw new BadRequestException("Empresa não informada");
        }
        usuarioRepository.findByUsernameAndEmpresaId(request.getUsername(), empresaId)
                .ifPresent(u -> {
                    throw new BadRequestException("Usuário já existe");
                });

        Usuario usuario = new Usuario();
        usuario.setEmpresaId(empresaId);
        usuario.setUsername(request.getUsername());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setAtivo(true);

        Set<Role> roles = request.getRoles() == null ? Set.of()
                : request.getRoles().stream()
                        .map(roleName -> roleRepository.findByNameAndEmpresaId(roleName, empresaId)
                                .orElseGet(() -> {
                                    Role role = new Role();
                                    role.setEmpresaId(empresaId);
                                    role.setName(roleName);
                                    return roleRepository.save(role);
                                }))
                        .collect(Collectors.toSet());
        usuario.setRoles(roles);
        usuario = usuarioRepository.save(usuario);
        return mapToResponse(usuario);
    }

    public UserResponse getById(Long id) {
        Long empresaId = TenantContext.getEmpresaId();
        Usuario usuario = usuarioRepository.findById(id)
                .filter(u -> u.getEmpresaId().equals(empresaId))
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        return mapToResponse(usuario);
    }

    public Set<UserResponse> listAll() {
        Long empresaId = TenantContext.getEmpresaId();
        return usuarioRepository.findAll().stream()
                .filter(user -> user.getEmpresaId().equals(empresaId))
                .map(this::mapToResponse)
                .collect(Collectors.toSet());
    }

    private UserResponse mapToResponse(Usuario usuario) {
        return new UserResponse(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.isAtivo(),
                usuario.getRoles().stream().map(Role::getName).collect(Collectors.toSet()),
                usuario.getCreatedAt(),
                usuario.getUpdatedAt());
    }
}
