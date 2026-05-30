export type RightRequestType = 'ACESSO' | 'CORRECAO' | 'EXCLUSAO' | 'PORTABILIDADE';
export type RequestStatus = 'PENDENTE' | 'EM_PROCESSAMENTO' | 'ATENDIDO' | 'RECUSADO';

export interface SolicitacaoRequest {
    titularId: number;
    tipo: RightRequestType;
    descricao: string;
}

export interface SolicitacaoResponse {
    id: number;
    titularId: number;
    tipo: RightRequestType;
    descricao: string;
    status: RequestStatus;
    resposta: string | null;
    solicitadoEm: string;
    atendidoEm: string | null;
    concluidoPor: string | null;
}
