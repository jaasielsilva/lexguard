import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ALL_PERMISSIONS, PERMISSION_LABELS, PermissionCode, ROLE_LABELS } from '../../../../core/constants/permissions';
import { getApiErrorMessage } from '../../../../core/utils/api-error.util';
import { RoleResponse } from '../../models/role.model';
import { RolesService } from '../../services/roles.service';

@Component({
    selector: 'app-perfil-edit',
    templateUrl: './perfil-edit.component.html',
    styleUrls: ['./perfil-edit.component.scss'],
})
export class PerfilEditComponent implements OnInit {
    role: RoleResponse | null = null;
    selected = new Set<PermissionCode>();
    loading = true;
    saving = false;
    error = '';
    success = '';

    readonly allPermissions = ALL_PERMISSIONS;
    readonly permissionLabels = PERMISSION_LABELS;
    readonly roleLabels = ROLE_LABELS;

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private service: RolesService,
    ) { }

    ngOnInit(): void {
        const id = Number(this.route.snapshot.paramMap.get('id'));
        this.service.get(id).subscribe({
            next: role => {
                this.role = role;
                this.selected = new Set(role.permissions);
                this.loading = false;
            },
            error: () => {
                this.error = 'Perfil nao encontrado.';
                this.loading = false;
            },
        });
    }

    toggle(code: PermissionCode, checked: boolean): void {
        if (checked) {
            this.selected.add(code);
        } else {
            this.selected.delete(code);
        }
    }

    isChecked(code: PermissionCode): boolean {
        return this.selected.has(code);
    }

    salvar(): void {
        if (!this.role || this.selected.size === 0) {
            this.error = 'Selecione ao menos uma permissao.';
            return;
        }
        this.saving = true;
        this.error = '';
        this.service.updatePermissions(this.role.id, { permissions: [...this.selected] }).subscribe({
            next: (updated) => {
                this.saving = false;
                this.role = updated;
                this.selected = new Set(updated.permissions);
                this.success = 'Permissoes atualizadas. Usuarios com este perfil precisam fazer login novamente.';
            },
            error: err => {
                this.saving = false;
                this.error = getApiErrorMessage(err, 'Erro ao salvar permissoes.');
            },
        });
    }

    voltar(): void {
        this.router.navigate(['/dashboard/usuarios/perfis']);
    }

    label(name: string): string {
        return this.roleLabels[name] ?? name;
    }
}
