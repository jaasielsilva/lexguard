package com.jaasielsilva.lexguard.security;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;

public final class AuthorityUtils {

    private static final String ROLE_PREFIX = "ROLE_";

    private AuthorityUtils() {
    }

    public static Set<String> extractRoleNames(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith(ROLE_PREFIX))
                .map(a -> a.substring(ROLE_PREFIX.length()))
                .collect(Collectors.toCollection(TreeSet::new));
    }

    public static Set<String> extractPermissions(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> !a.startsWith(ROLE_PREFIX))
                .collect(Collectors.toCollection(TreeSet::new));
    }
}
