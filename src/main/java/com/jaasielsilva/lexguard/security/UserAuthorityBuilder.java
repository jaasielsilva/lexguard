package com.jaasielsilva.lexguard.security;

import com.jaasielsilva.lexguard.model.Permission;
import com.jaasielsilva.lexguard.model.Role;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public final class UserAuthorityBuilder {

    private UserAuthorityBuilder() {
    }

    public static Set<GrantedAuthority> fromRoles(Collection<Role> roles) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
            if (role.getPermissions() != null) {
                for (Permission permission : role.getPermissions()) {
                    authorities.add(new SimpleGrantedAuthority(permission.name()));
                }
            }
        }
        return authorities;
    }
}
