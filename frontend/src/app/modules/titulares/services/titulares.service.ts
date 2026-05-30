import { Injectable } from '@angular/core';
import { HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiService } from '../../../core/services/api.service';
import { TitularRequest, TitularResponse, TitularSearchPage } from '../models/titular.model';

@Injectable({ providedIn: 'root' })
export class TitularesService {
    constructor(private api: ApiService) { }

    list(): Observable<TitularResponse[]> {
        return this.api.get<TitularResponse[]>('/titulares');
    }

    /** Busca paginada no servidor — adequada a grandes volumes de titulares. */
    search(query: string, page = 0, size = 15): Observable<TitularSearchPage> {
        const params = new HttpParams()
            .set('q', query.trim())
            .set('page', String(page))
            .set('size', String(size));
        return this.api.get<TitularSearchPage>('/titulares/search', params);
    }

    getById(id: number): Observable<TitularResponse> {
        return this.api.get<TitularResponse>(`/titulares/${id}`);
    }

    create(data: TitularRequest): Observable<TitularResponse> {
        return this.api.post<TitularResponse>('/titulares', data, this.api.getUsername());
    }

    update(id: number, data: TitularRequest): Observable<TitularResponse> {
        return this.api.put<TitularResponse>(`/titulares/${id}`, data, this.api.getUsername());
    }

    delete(id: number): Observable<void> {
        return this.api.delete<void>(`/titulares/${id}`, this.api.getUsername());
    }
}
