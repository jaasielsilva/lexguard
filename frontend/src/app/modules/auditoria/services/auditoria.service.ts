import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../../../core/services/api.service';
import { AuditLogResponse } from '../models/audit-log.model';

@Injectable({ providedIn: 'root' })
export class AuditoriaService {
    constructor(private api: ApiService) { }

    list(): Observable<AuditLogResponse[]> {
        return this.api.get<AuditLogResponse[]>('/audit');
    }
}
