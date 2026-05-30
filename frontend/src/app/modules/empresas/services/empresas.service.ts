import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../../../core/services/api.service';
import { EmpresaRequest, EmpresaResponse } from '../models/empresa.model';

@Injectable({ providedIn: 'root' })
export class EmpresasService {

    private readonly PATH = '/empresas';

    constructor(private api: ApiService) { }

    list(): Observable<EmpresaResponse[]> {
        return this.api.get<EmpresaResponse[]>(this.PATH);
    }

    getById(id: number): Observable<EmpresaResponse> {
        return this.api.get<EmpresaResponse>(`${this.PATH}/${id}`);
    }

    create(request: EmpresaRequest): Observable<EmpresaResponse> {
        return this.api.post<EmpresaResponse>(this.PATH, request);
    }

    update(id: number, request: EmpresaRequest): Observable<EmpresaResponse> {
        return this.api.put<EmpresaResponse>(`${this.PATH}/${id}`, request);
    }

    toggleAtivo(id: number): Observable<EmpresaResponse> {
        return this.api.patch<EmpresaResponse>(`${this.PATH}/${id}/toggle-ativo`, {});
    }
}
