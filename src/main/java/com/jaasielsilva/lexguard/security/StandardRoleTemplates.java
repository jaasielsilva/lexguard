package com.jaasielsilva.lexguard.security;

import com.jaasielsilva.lexguard.model.Permission;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Perfis padrao por empresa (tenant). Permissoes sempre via Role, nunca direto no usuario.
 */
public final class StandardRoleTemplates {

    public static final String SUPER_ADMIN = "SUPER_ADMIN";
    public static final String ADMIN = "ADMIN";
    public static final String ANALYST = "ANALYST";
    public static final String VIEWER = "VIEWER";
    public static final String DPO = "DPO";
    /** Legado: sincronizado com VIEWER na inicializacao. */
    public static final String USER = "USER";

    private static final Set<String> NON_ASSIGNABLE = Set.of(SUPER_ADMIN);

    private StandardRoleTemplates() {
    }

    public static Map<String, Set<Permission>> all() {
        Map<String, Set<Permission>> roles = new LinkedHashMap<>();
        roles.put(SUPER_ADMIN, EnumSet.allOf(Permission.class));
        roles.put(ADMIN, EnumSet.of(
                Permission.DATA_READ,
                Permission.DATA_WRITE,
                Permission.CONSENT_MANAGE,
                Permission.REQUEST_MANAGE,
                Permission.REPORT_READ,
                Permission.USER_MANAGE));
        roles.put(ANALYST, EnumSet.of(
                Permission.DATA_READ,
                Permission.CONSENT_MANAGE,
                Permission.REQUEST_MANAGE,
                Permission.REPORT_READ));
        roles.put(VIEWER, EnumSet.of(
                Permission.DATA_READ,
                Permission.REPORT_READ));
        roles.put(DPO, EnumSet.of(
                Permission.DATA_READ,
                Permission.CONSENT_MANAGE,
                Permission.REQUEST_MANAGE,
                Permission.REPORT_READ,
                Permission.AUDIT_READ));
        roles.put(USER, EnumSet.copyOf(roles.get(VIEWER)));
        return roles;
    }

    public static Set<String> assignableRoleNames() {
        return Set.of(ADMIN, ANALYST, VIEWER, DPO);
    }

    public static boolean isAssignable(String roleName) {
        return roleName != null && assignableRoleNames().contains(roleName);
    }

    public static boolean isNonAssignable(String roleName) {
        return roleName != null && NON_ASSIGNABLE.contains(roleName);
    }
}
