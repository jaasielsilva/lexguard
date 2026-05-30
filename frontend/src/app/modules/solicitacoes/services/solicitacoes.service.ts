import { Injectable } from '@angular/core';
import { HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiService } from '../../../core/services/api.service';
import { RequestStatus, SolicitacaoRequest, SolicitacaoResponse } from '../models/solicitacao.model';

@Injectable({ providedIn: 'root' })
export class SolicitacoesService {
    constructor(private api: ApiService) { }

    list(): Observable<SolicitacaoResponse[]> {
        return this.api.get<SolicitacaoResponse[]>('/solicitacoes');
    }

    listByTitular(titularId: number): Observable<SolicitacaoResponse[]> {
        const params = new HttpParams().set('titularId', String(titularId));
        return this.api.get<SolicitacaoResponse[]>('/solicitacoes', params);
    }

    create(body: SolicitacaoRequest): Observable<SolicitacaoResponse> {
        return this.api.post<SolicitacaoResponse>('/solicitacoes', body, this.api.getUsername());
    }

    markInProgress(id: number): Observable<SolicitacaoResponse> {
        return this.api.patch<SolicitacaoResponse>(`/solicitacoes/${id}/status`, { status: 'EM_PROCESSAMENTO' });
    }

    markAttended(id: number, resposta: string): Observable<SolicitacaoResponse> {
        return this.api.put<SolicitacaoResponse>(`/solicitacoes/${id}/respond`, { resposta });
    }
}
