import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../../auth/services/auth.service';

@Injectable({ providedIn: 'root' })
export class ApiService {
    readonly BASE = 'http://localhost:8080/api';

    constructor(private http: HttpClient, private auth: AuthService) { }

    private headers(withUsuario?: string): HttpHeaders {
        let h = new HttpHeaders({
            'Authorization': `Bearer ${this.auth.getToken()}`,
            'X-Empresa-Id': String(this.auth.getEmpresaId()),
        });
        if (withUsuario) h = h.set('X-Usuario', withUsuario);
        return h;
    }

    get<T>(path: string, params?: HttpParams, withUsuario = true): Observable<T> {
        const usuario = withUsuario ? this.getUsername() : undefined;
        return this.http.get<T>(`${this.BASE}${path}`, {
            headers: this.headers(usuario),
            params,
        });
    }

    post<T>(path: string, body: unknown, usuario?: string): Observable<T> {
        return this.http.post<T>(`${this.BASE}${path}`, body, { headers: this.headers(usuario) });
    }

    put<T>(path: string, body: unknown, usuario?: string): Observable<T> {
        return this.http.put<T>(`${this.BASE}${path}`, body, { headers: this.headers(usuario) });
    }

    patch<T>(path: string, body: unknown, usuario?: string): Observable<T> {
        return this.http.patch<T>(`${this.BASE}${path}`, body, { headers: this.headers(usuario) });
    }

    delete<T>(path: string, usuario?: string): Observable<T> {
        return this.http.delete<T>(`${this.BASE}${path}`, { headers: this.headers(usuario) });
    }

    getUsername(): string {
        return localStorage.getItem('username') ?? 'sistema';
    }
}
