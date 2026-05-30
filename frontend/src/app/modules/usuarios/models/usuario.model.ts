export interface UserCreateRequest {
    username: string;
    password: string;
    nome: string;
    email: string;
    roles: string[];
}

export interface UserResponse {
    id: number;
    username: string;
    nome: string;
    email: string;
    ativo: boolean;
    roles: string[];
    createdAt: string;
    updatedAt: string;
}
