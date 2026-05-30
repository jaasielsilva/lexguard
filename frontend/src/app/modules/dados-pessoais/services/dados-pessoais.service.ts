import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../../../core/services/api.service';
import { DadoPessoalResponse } from '../models/dado-pessoal.model';

@Injectable({ providedIn: 'root' })
export class DadosPessoaisService {
    constructor(private api: ApiService) { }

    listByTitular(titularId: number): Observable<DadoPessoalResponse[]> {
        return this.api.get<DadoPessoalResponse[]>(`/dados/titular/${titularId}`);
    }
}
