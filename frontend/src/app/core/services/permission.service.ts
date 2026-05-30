import { Injectable } from '@angular/core';
import { PermissionCode } from '../constants/permissions';

@Injectable({ providedIn: 'root' })
export class PermissionService {

    getPermissions(): PermissionCode[] {
        const raw = localStorage.getItem('permissions');
        if (!raw) {
            return [];
        }
        try {
            return JSON.parse(raw) as PermissionCode[];
        } catch {
            return [];
        }
    }

    getRoles(): string[] {
        const raw = localStorage.getItem('roles');
        if (!raw) {
            return [];
        }
        try {
            return JSON.parse(raw) as string[];
        } catch {
            return [];
        }
    }

    hasPermission(code: PermissionCode | string): boolean {
        if (this.getRoles().includes('SUPER_ADMIN')) {
            return true;
        }
        return this.getPermissions().includes(code as PermissionCode);
    }

    hasAnyPermission(...codes: string[]): boolean {
        if (codes.length === 0) {
            return true;
        }
        return codes.some(c => this.hasPermission(c));
    }

    setSession(roles: string[], permissions: string[]): void {
        localStorage.setItem('roles', JSON.stringify(roles ?? []));
        localStorage.setItem('permissions', JSON.stringify(permissions ?? []));
    }

    clear(): void {
        localStorage.removeItem('roles');
        localStorage.removeItem('permissions');
    }
}
