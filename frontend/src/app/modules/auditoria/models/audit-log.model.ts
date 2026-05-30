export type AuditLogAction =
    | 'LOGIN' | 'LOGOUT'
    | 'CREATE_TITULAR' | 'UPDATE_TITULAR' | 'DELETE_TITULAR'
    | 'REGISTER_CONSENT' | 'REVOKE_CONSENT'
    | 'DATA_ACCESS' | 'DATA_EXPORT'
    | 'REQUEST_SUBMITTED' | 'REQUEST_HANDLED'
    | 'REPORT_GENERATED';

export type LegalBasis = 'CONSENTIMENTO' | 'OBRIGACAO_LEGAL' | 'EXECUCAO_CONTRATO' | 'INTERESSE_LEGITIMO' | 'PROTECAO_VIDA' | 'SERVICO_PUBLICO' | 'EXERCICIO_DIREITO';

export interface AuditLogResponse {
    id: number;
    action: AuditLogAction;
    usuario: string;
    resource: string;
    descricao: string;
    finalidade: string;
    baseLegal: LegalBasis;
    timestamp: string;
}
