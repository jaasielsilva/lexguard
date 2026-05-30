import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../../../core/services/api.service';
import { ConsentimentoRequest, ConsentimentoResponse } from '../models/consentimento.model';

@Injectable({ providedIn: 'root' })
export class ConsentimentosService {
    constructor(private api: ApiService) { }

    listByTitular(titularId: number): Observable<ConsentimentoResponse[]> {
        return this.api.get<ConsentimentoResponse[]>(`/consentimentos/titular/${titularId}`);
    }

    create(body: ConsentimentoRequest): Observable<ConsentimentoResponse> {
        return this.api.post<ConsentimentoResponse>('/consentimentos', body, this.api.getUsername());
    }
}
