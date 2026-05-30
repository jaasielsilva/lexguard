export type LegalBasis = 'CONSENTIMENTO' | 'OBRIGACAO_LEGAL' | 'EXECUCAO_CONTRATO' | 'INTERESSE_LEGITIMO' | 'PROTECAO_VIDA' | 'SERVICO_PUBLICO' | 'EXERCICIO_DIREITO';

export interface ConsentimentoRequest {
    titularId: number;
    finalidade: string;
    baseLegal: LegalBasis;
    versaoTermo: string;
}

export interface ConsentimentoResponse {
    id: number;
    titularId: number;
    finalidade: string;
    baseLegal: LegalBasis;
    versaoTermo: string;
    aceiteEm: string;
    revogadoEm: string | null;
    ativo: boolean;
}
