package com.jaasielsilva.lexguard.security;

import com.jaasielsilva.lexguard.dto.auth.AuthResponse;
import com.jaasielsilva.lexguard.dto.auth.LoginRequest;
import com.jaasielsilva.lexguard.dto.auth.TokenRefreshRequest;
import com.jaasielsilva.lexguard.model.TokenBlacklist;
import com.jaasielsilva.lexguard.repository.TokenBlacklistRepository;
import com.jaasielsilva.lexguard.repository.UsuarioRepository;
import com.jaasielsilva.lexguard.tenant.TenantContext;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.Set;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtTokenProvider tokenProvider;
    private final UsuarioRepository usuarioRepository;
    private final TokenBlacklistRepository tokenBlacklistRepository;

    public AuthController(AuthenticationManager authenticationManager,
            CustomUserDetailsService userDetailsService,
            JwtTokenProvider tokenProvider,
            UsuarioRepository usuarioRepository,
            TokenBlacklistRepository tokenBlacklistRepository) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.tokenProvider = tokenProvider;
        this.usuarioRepository = usuarioRepository;
        this.tokenBlacklistRepository = tokenBlacklistRepository;
    }

    /**
     * Login sem necessidade de informar o ID da empresa.
     * O empresaId é resolvido internamente a partir do username.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@Valid @RequestBody LoginRequest request) {
        // 1. Busca o usuário pelo username para descobrir o empresaId
        var usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElse(null);

        if (usuario == null) {
            return ResponseEntity.status(401).body(AuthResponse.error("Credenciais inválidas"));
        }

        Long empresaId = usuario.getEmpresaId();
        TenantContext.setEmpresaId(empresaId);

        try {
            // 2. Autentica com Spring Security (valida a senha via BCrypt)
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            var userDetails = userDetailsService.loadUserByUsername(request.getUsername());

            String accessToken = tokenProvider.generateAccessToken(userDetails, empresaId);
            String refreshToken = tokenProvider.generateRefreshToken(userDetails, empresaId);

            return ResponseEntity.ok(buildSuccessResponse(
                    accessToken, refreshToken, empresaId, "Autenticado com sucesso", authentication.getAuthorities()));

        } catch (AuthenticationException ex) {
            return ResponseEntity.status(401).body(AuthResponse.error("Credenciais inválidas"));
        } finally {
            TenantContext.clear();
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        if (!tokenProvider.validateToken(request.getRefreshToken())) {
            return ResponseEntity.badRequest().body(AuthResponse.error("Refresh token inválido"));
        }

        Long empresaId = tokenProvider.getEmpresaId(request.getRefreshToken());
        String username = tokenProvider.getUsername(request.getRefreshToken());

        if (tokenBlacklistRepository.findByTokenAndEmpresaId(request.getRefreshToken(), empresaId).isPresent()) {
            return ResponseEntity.status(401).body(AuthResponse.error("Refresh token bloqueado"));
        }

        TenantContext.setEmpresaId(empresaId);
        try {
            var userDetails = userDetailsService.loadUserByUsername(username);
            String token = tokenProvider.generateAccessToken(userDetails, empresaId);
            return ResponseEntity.ok(buildSuccessResponse(
                    token,
                    request.getRefreshToken(),
                    empresaId,
                    "Token renovado",
                    userDetails.getAuthorities()));
        } finally {
            TenantContext.clear();
        }
    }

    private AuthResponse buildSuccessResponse(
            String accessToken,
            String refreshToken,
            Long empresaId,
            String message,
            java.util.Collection<? extends GrantedAuthority> authorities) {
        Set<String> roles = AuthorityUtils.extractRoleNames(authorities);
        Set<String> permissions = AuthorityUtils.extractPermissions(authorities);
        return AuthResponse.success(accessToken, refreshToken, empresaId, message, roles, permissions);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("X-Empresa-Id") Long empresaId,
            @Valid @RequestBody TokenRefreshRequest request) {

        if (!tokenProvider.validateToken(request.getRefreshToken())) {
            return ResponseEntity.badRequest().build();
        }

        TokenBlacklist blacklist = new TokenBlacklist();
        blacklist.setEmpresaId(empresaId);
        blacklist.setToken(request.getRefreshToken());
        blacklist.setRevokedAt(Instant.now());
        tokenBlacklistRepository.save(blacklist);

        return ResponseEntity.noContent().build();
    }
}
