export interface EmpresaResponse {
    id: number;
    nome: string;
    cnpj: string;
    contatoEmail: string;
    ativo: boolean;
    createdAt: string;
    updatedAt: string;
}

export interface EmpresaRequest {
    nome: string;
    cnpj: string;
    contatoEmail: string;
}
