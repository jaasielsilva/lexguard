import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../../../core/services/api.service';
import { UserCreateRequest, UserResponse } from '../models/usuario.model';

@Injectable({ providedIn: 'root' })
export class UsuariosService {
    constructor(private api: ApiService) { }

    list(): Observable<UserResponse[]> {
        return this.api.get<UserResponse[]>('/users');
    }

    create(user: UserCreateRequest): Observable<UserResponse> {
        return this.api.post<UserResponse>('/users', user, this.api.getUsername());
    }
}
