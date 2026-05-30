package com.jaasielsilva.lexguard.tenant;

public class TenantContext {

    private static final ThreadLocal<Long> TENANT = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void setEmpresaId(Long empresaId) {
        TENANT.set(empresaId);
    }

    public static Long getEmpresaId() {
        return TENANT.get();
    }

    public static void clear() {
        TENANT.remove();
    }
}
