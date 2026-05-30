import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../../../core/services/api.service';
import { DadoPessoalRequest, DadoPessoalResponse } from '../models/dado-pessoal.model';

@Injectable({ providedIn: 'root' })
export class DadosPessoaisService {
    constructor(private api: ApiService) { }

    listByTitular(titularId: number): Observable<DadoPessoalResponse[]> {
        return this.api.get<DadoPessoalResponse[]>(`/dados/titular/${titularId}`);
    }

    create(body: DadoPessoalRequest): Observable<DadoPessoalResponse> {
        return this.api.post<DadoPessoalResponse>('/dados', body, this.api.getUsername());
    }
}
