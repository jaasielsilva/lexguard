import { Injectable } from '@angular/core';
import { HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiService } from '../../../core/services/api.service';
import { AuditActionGroup, AuditLogSearchPage } from '../models/audit-log.model';

@Injectable({ providedIn: 'root' })
export class AuditoriaService {
    constructor(private api: ApiService) { }

    search(
        query: string,
        page = 0,
        size = 25,
        actionGroup?: AuditActionGroup | null,
    ): Observable<AuditLogSearchPage> {
        let params = new HttpParams()
            .set('page', String(page))
            .set('size', String(size));
        const term = query.trim();
        if (term.length > 0) {
            params = params.set('q', term);
        }
        if (actionGroup) {
            params = params.set('actionGroup', actionGroup);
        }
        return this.api.get<AuditLogSearchPage>('/audit', params);
    }
}
