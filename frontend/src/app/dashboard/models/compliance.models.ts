export interface ComplianceScore {
    score: number;
    status: 'BAIXO RISCO' | 'ATENÇÃO' | 'CRÍTICO';
    alertas: string[];
    riscoSeguranca: number;
    riscoConsentimento: number;
    riscoAuditoria: number;
    riscoLegal: number;
}
