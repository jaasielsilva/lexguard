export type ReportType = 'AUDITORIA' | 'ACESSO_DADOS' | 'CONSENTIMENTO' | 'RISCOS' | 'DASHBOARD';

export interface RelatorioResponse {
    id: number;
    titulo: string;
    descricao: string;
    tipo: ReportType;
    conteudo: string;
    geradoEm: string;
    geradoPor: string;
}

export interface RelatorioRequest {
    titulo: string;
    descricao?: string;
    tipo: ReportType;
    conteudo: string;
}
