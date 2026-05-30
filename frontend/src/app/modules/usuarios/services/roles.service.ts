import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../../../core/services/api.service';
import { RolePermissionsUpdateRequest, RoleResponse } from '../models/role.model';

@Injectable({ providedIn: 'root' })
export class RolesService {
    constructor(private api: ApiService) { }

    list(): Observable<RoleResponse[]> {
        return this.api.get<RoleResponse[]>('/roles');
    }

    get(id: number): Observable<RoleResponse> {
        return this.api.get<RoleResponse>(`/roles/${id}`);
    }

    updatePermissions(id: number, body: RolePermissionsUpdateRequest): Observable<RoleResponse> {
        return this.api.put<RoleResponse>(`/roles/${id}/permissions`, body, this.api.getUsername());
    }
}
