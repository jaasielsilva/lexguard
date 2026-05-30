import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ROLE_LABELS } from '../../../../core/constants/permissions';
import { RoleResponse } from '../../models/role.model';
import { RolesService } from '../../services/roles.service';

@Component({
    selector: 'app-perfis-list',
    templateUrl: './perfis-list.component.html',
    styleUrls: ['./perfis-list.component.scss'],
})
export class PerfisListComponent implements OnInit {
    roles: RoleResponse[] = [];
    loading = true;
    error = '';

    readonly roleLabels = ROLE_LABELS;

    constructor(private service: RolesService, private router: Router) { }

    ngOnInit(): void {
        this.load();
    }

    load(): void {
        this.loading = true;
        this.service.list().subscribe({
            next: data => {
                this.roles = data;
                this.loading = false;
            },
            error: () => {
                this.error = 'Erro ao carregar perfis.';
                this.loading = false;
            },
        });
    }

    editar(role: RoleResponse): void {
        this.router.navigate(['/dashboard/usuarios/perfis', role.id]);
    }

    label(name: string): string {
        return this.roleLabels[name] ?? name;
    }
}
