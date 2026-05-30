/** Alinhado ao enum Permission do backend */
export type PermissionCode =
    | 'DATA_READ'
    | 'DATA_WRITE'
    | 'CONSENT_MANAGE'
    | 'AUDIT_READ'
    | 'REQUEST_MANAGE'
    | 'REPORT_READ'
    | 'USER_MANAGE'
    | 'TENANT_ADMIN';

export const ALL_PERMISSIONS: PermissionCode[] = [
    'DATA_READ',
    'DATA_WRITE',
    'CONSENT_MANAGE',
    'AUDIT_READ',
    'REQUEST_MANAGE',
    'REPORT_READ',
    'USER_MANAGE',
    'TENANT_ADMIN',
];

export const PERMISSION_LABELS: Record<PermissionCode, string> = {
    DATA_READ: 'Ler dados (titulares e dados pessoais)',
    DATA_WRITE: 'Criar e editar dados',
    CONSENT_MANAGE: 'Gerenciar consentimentos',
    AUDIT_READ: 'Consultar auditoria',
    REQUEST_MANAGE: 'Gerenciar solicitacoes do titular',
    REPORT_READ: 'Relatorios',
    USER_MANAGE: 'Gerenciar usuarios e perfis',
    TENANT_ADMIN: 'Administracao do tenant',
};

export const ROLE_LABELS: Record<string, string> = {
    SUPER_ADMIN: 'Super administrador',
    ADMIN: 'Administrador',
    ANALYST: 'Analista',
    VIEWER: 'Somente leitura',
    DPO: 'Encarregado (DPO)',
    USER: 'Usuario (legado)',
};
