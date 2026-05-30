import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../../../core/services/api.service';
import { TitularRequest, TitularResponse } from '../models/titular.model';

@Injectable({ providedIn: 'root' })
export class TitularesService {
    constructor(private api: ApiService) { }

    list(): Observable<TitularResponse[]> {
        return this.api.get<TitularResponse[]>('/titulares');
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
