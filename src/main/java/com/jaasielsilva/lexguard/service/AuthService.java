package com.jaasielsilva.lexguard.service;

import com.jaasielsilva.lexguard.dto.auth.AuthResponse;
import com.jaasielsilva.lexguard.exception.ResourceNotFoundException;
import com.jaasielsilva.lexguard.model.TokenBlacklist;
import com.jaasielsilva.lexguard.model.Usuario;
import com.jaasielsilva.lexguard.repository.TokenBlacklistRepository;
import com.jaasielsilva.lexguard.repository.UsuarioRepository;
import com.jaasielsilva.lexguard.security.JwtTokenProvider;
import com.jaasielsilva.lexguard.security.UserAuthorityBuilder;
import com.jaasielsilva.lexguard.tenant.TenantContext;
import java.time.Instant;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UsuarioRepository usuarioRepository;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager,
            JwtTokenProvider tokenProvider,
            UsuarioRepository usuarioRepository,
            TokenBlacklistRepository tokenBlacklistRepository,
            PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.usuarioRepository = usuarioRepository;
        this.tokenBlacklistRepository = tokenBlacklistRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse authenticate(Long empresaId, String username, String password) {
        TenantContext.setEmpresaId(empresaId);
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            Usuario usuario = usuarioRepository.findByUsernameAndEmpresaId(username, empresaId)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
            User principal = new User(
                    usuario.getUsername(),
                    usuario.getPassword(),
                    usuario.isAtivo(),
                    true,
                    true,
                    true,
                    UserAuthorityBuilder.fromRoles(usuario.getRoles()));
            String accessToken = tokenProvider.generateAccessToken(principal, empresaId);
            String refreshToken = tokenProvider.generateRefreshToken(principal, empresaId);
            return new AuthResponse(accessToken, refreshToken, empresaId, "Autenticado com sucesso");
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Credenciais inválidas");
        } finally {
            TenantContext.clear();
        }
    }

    public AuthResponse refreshToken(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new BadCredentialsException("Refresh token inválido");
        }
        Long empresaId = tokenProvider.getEmpresaId(refreshToken);
        if (tokenBlacklistRepository.findByTokenAndEmpresaId(refreshToken, empresaId).isPresent()) {
            throw new BadCredentialsException("Refresh token bloqueado");
        }
        String username = tokenProvider.getUsername(refreshToken);
        Usuario usuario = usuarioRepository.findByUsernameAndEmpresaId(username, empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        User principal = new User(
                usuario.getUsername(),
                usuario.getPassword(),
                usuario.isAtivo(),
                true,
                true,
                true,
                UserAuthorityBuilder.fromRoles(usuario.getRoles()));
        String accessToken = tokenProvider.generateAccessToken(principal, empresaId);
        TokenBlacklist blacklist = new TokenBlacklist();
        blacklist.setEmpresaId(empresaId);
        blacklist.setToken(refreshToken);
        blacklist.setRevokedAt(Instant.now());
        tokenBlacklistRepository.save(blacklist);
        return new AuthResponse(accessToken, refreshToken, empresaId, "Refresh token validado com sucesso");
    }
}
