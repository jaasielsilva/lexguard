import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../../core/services/api.service';
import { ComplianceScore } from '../models/compliance.models';

@Injectable({ providedIn: 'root' })
export class ComplianceService {

    constructor(private api: ApiService) { }

    getScore(): Observable<ComplianceScore> {
        return this.api.get<ComplianceScore>('/compliance/score');
    }
}
