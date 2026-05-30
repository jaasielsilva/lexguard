import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../../../core/services/api.service';
import { RelatorioResponse } from '../models/relatorio.model';

@Injectable({ providedIn: 'root' })
export class RelatoriosService {
    constructor(private api: ApiService) { }

    list(): Observable<RelatorioResponse[]> {
        return this.api.get<RelatorioResponse[]>('/relatorios');
    }
}
