import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { PermissionService } from '../../core/services/permission.service';
import { AuthResponse, LoginRequest } from '../models/auth.models';

@Injectable({ providedIn: 'root' })
export class AuthService {
    private readonly API = 'http://localhost:8080/api/auth';

    constructor(
        private http: HttpClient,
        private permissionService: PermissionService,
    ) { }

    login(payload: LoginRequest): Observable<AuthResponse> {
        return this.http.post<AuthResponse>(`${this.API}/login`, payload).pipe(
            tap((res) => this.persistSession(res, payload.username)),
        );
    }

    logout(): void {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('empresaId');
        localStorage.removeItem('username');
        this.permissionService.clear();
    }

    isAuthenticated(): boolean {
        return !!localStorage.getItem('accessToken');
    }

    getToken(): string | null {
        return localStorage.getItem('accessToken');
    }

    getEmpresaId(): number | null {
        const id = localStorage.getItem('empresaId');
        return id ? Number(id) : null;
    }

    getUsername(): string {
        return localStorage.getItem('username') ?? '';
    }

    private persistSession(res: AuthResponse, username: string): void {
        if (!res.accessToken) {
            return;
        }
        localStorage.setItem('accessToken', res.accessToken);
        localStorage.setItem('refreshToken', res.refreshToken);
        localStorage.setItem('empresaId', String(res.empresaId));
        localStorage.setItem('username', username);
        this.permissionService.setSession(res.roles ?? [], res.permissions ?? []);
    }
}
