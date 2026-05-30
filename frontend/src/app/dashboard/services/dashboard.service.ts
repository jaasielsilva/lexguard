import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DashboardMetrics } from '../models/dashboard.models';
import { AuthService } from '../../auth/services/auth.service';

@Injectable({ providedIn: 'root' })
export class DashboardService {
    private readonly API = 'http://localhost:8080/api/dashboard';

    constructor(private http: HttpClient, private auth: AuthService) { }

    getMetrics(): Observable<DashboardMetrics> {
        const empresaId = this.auth.getEmpresaId();
        const headers = new HttpHeaders({
            'Authorization': `Bearer ${this.auth.getToken()}`,
            'X-Empresa-Id': String(empresaId),
        });
        return this.http.get<DashboardMetrics>(this.API, { headers });
    }
}
