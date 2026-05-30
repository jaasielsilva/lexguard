package com.jaasielsilva.lexguard.security;

import com.jaasielsilva.lexguard.model.Usuario;
import com.jaasielsilva.lexguard.repository.UsuarioRepository;
import com.jaasielsilva.lexguard.tenant.TenantContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Long empresaId = TenantContext.getEmpresaId();
        if (empresaId == null) {
            throw new UsernameNotFoundException("Empresa id não informado");
        }
        return loadUserByUsernameAndEmpresaId(username, empresaId);
    }

    public UserDetails loadUserByUsernameAndEmpresaId(String username, Long empresaId) {
        Usuario usuario = usuarioRepository.findByUsernameAndEmpresaId(username, empresaId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
        return new org.springframework.security.core.userdetails.User(
                usuario.getUsername(),
                usuario.getPassword(),
                usuario.isAtivo(),
                true,
                true,
                true,
                UserAuthorityBuilder.fromRoles(usuario.getRoles()));
    }
}
