package com.jaasielsilva.lexguard.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    /** Usuario autenticado (JWT). Usar em leituras; escritas podem aceitar X-Usuario ou este valor. */
    public static String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) {
            return "sistema";
        }
        return auth.getName();
    }
}
