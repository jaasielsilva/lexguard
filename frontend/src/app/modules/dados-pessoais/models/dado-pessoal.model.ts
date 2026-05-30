export type DataClassification = 'PESSOAL' | 'SENSIVEL';
export type LegalBasis = 'CONSENTIMENTO' | 'OBRIGACAO_LEGAL' | 'EXECUCAO_CONTRATO' | 'INTERESSE_LEGITIMO' | 'PROTECAO_VIDA' | 'SERVICO_PUBLICO' | 'EXERCICIO_DIREITO';
export type RiskLevel = 'BAIXO' | 'MEDIO' | 'ALTO' | 'CRITICO';

export interface DadoPessoalRequest {
    titularId: number;
    nomeCampo: string;
    valor: string;
    classificacao: DataClassification;
    baseLegal: LegalBasis;
    finalidade: string;
    localArmazenamento?: string;
    risco: RiskLevel;
}

export interface DadoPessoalResponse {
    id: number;
    titularId: number;
    nomeCampo: string;
    valor: string;
    classificacao: DataClassification;
    baseLegal: LegalBasis;
    finalidade: string;
    localArmazenamento: string;
    risco: RiskLevel;
    ativo: boolean;
    ultimaVezAcessado: string;
    createdAt: string;
    updatedAt: string;
}
