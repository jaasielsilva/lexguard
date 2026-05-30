export type DataClassification = 'PESSOAL' | 'SENSIVEL';

export interface TitularRequest {
    nome: string;
    cpf: string;
    email: string;
    telefone?: string;
    classificacao: DataClassification;
}

export interface TitularResponse {
    id: number;
    nome: string;
    cpf: string;
    email: string;
    telefone: string;
    classificacao: DataClassification;
    ativo: boolean;
    softDeleted: boolean;
    createdAt: string;
    updatedAt: string;
}

export interface TitularSearchPage {
    items: TitularResponse[];
    totalElements: number;
    page: number;
    size: number;
    hasMore: boolean;
}
