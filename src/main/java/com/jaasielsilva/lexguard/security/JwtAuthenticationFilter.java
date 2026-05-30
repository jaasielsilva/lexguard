package com.jaasielsilva.lexguard.security;

import com.jaasielsilva.lexguard.tenant.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider,
            CustomUserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = resolveToken(request);
            if (token != null && jwtTokenProvider.validateToken(token)) {
                String username = jwtTokenProvider.getUsername(token);
                Long empresaId = resolveEmpresaId(token);
                if (empresaId == null) {
                    logger.warn("JWT válido, mas empresaId ausente (informe X-Empresa-Id ou claim empresaId no token)");
                } else {
                    try {
                        UserDetails userDetails = userDetailsService.loadUserByUsernameAndEmpresaId(username, empresaId);
                        var principal = new JwtUserDetails(username, empresaId, userDetails.getAuthorities());
                        var authentication = new UsernamePasswordAuthenticationToken(
                                principal, token, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } catch (UsernameNotFoundException ex) {
                        logger.warn("JWT válido, mas usuário '{}' não encontrado no tenant {}: {}",
                                username, empresaId, ex.getMessage());
                    }
                }
            }
        } catch (Exception ex) {
            logger.warn("JWT authentication failed: {}", ex.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    private Long resolveEmpresaId(String token) {
        Long headerEmpresaId = TenantContext.getEmpresaId();
        Long tokenEmpresaId = jwtTokenProvider.getEmpresaId(token);
        if (headerEmpresaId != null && tokenEmpresaId != null && !headerEmpresaId.equals(tokenEmpresaId)) {
            logger.warn("X-Empresa-Id ({}) difere do empresaId do token ({})", headerEmpresaId, tokenEmpresaId);
        }
        return headerEmpresaId != null ? headerEmpresaId : tokenEmpresaId;
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
