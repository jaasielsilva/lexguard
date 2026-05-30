package com.jaasielsilva.lexguard.security;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jaasielsilva.lexguard.model.Permission;
import com.jaasielsilva.lexguard.model.Role;
import java.util.EnumSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

class UserAuthorityBuilderTest {

    @Test
    void superAdminRoleIncludesUserManagePermission() {
        Role role = new Role();
        role.setName("SUPER_ADMIN");
        role.setPermissions(EnumSet.allOf(Permission.class));

        Set<GrantedAuthority> authorities = UserAuthorityBuilder.fromRoles(Set.of(role));

        assertTrue(authorities.stream().anyMatch(a -> "USER_MANAGE".equals(a.getAuthority())));
        assertTrue(authorities.stream().anyMatch(a -> "ROLE_SUPER_ADMIN".equals(a.getAuthority())));
    }
}
