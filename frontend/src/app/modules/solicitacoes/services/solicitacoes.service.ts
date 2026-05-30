import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../../../core/services/api.service';
import { SolicitacaoResponse } from '../models/solicitacao.model';

@Injectable({ providedIn: 'root' })
export class SolicitacoesService {
    constructor(private api: ApiService) { }

    list(): Observable<SolicitacaoResponse[]> {
        return this.api.get<SolicitacaoResponse[]>('/solicitacoes');
    }
}
