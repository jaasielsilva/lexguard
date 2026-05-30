import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ROLE_LABELS } from '../../../../core/constants/permissions';
import { getApiErrorMessage } from '../../../../core/utils/api-error.util';
import { RoleResponse } from '../../models/role.model';
import { UserCreateRequest, UserResponse } from '../../models/usuario.model';
import { RolesService } from '../../services/roles.service';
import { UsuariosService } from '../../services/usuarios.service';

@Component({
    selector: 'app-usuarios-list',
    templateUrl: './usuarios-list.component.html',
    styleUrls: ['./usuarios-list.component.scss'],
})
export class UsuariosListComponent implements OnInit {

    usuarios: UserResponse[] = [];
    filtered: UserResponse[] = [];
    loading = true;
    error = '';
    success = '';
    search = '';

    formVisivel = false;
    formError = '';
    saving = false;
    form: FormGroup;
    assignableRoles: RoleResponse[] = [];
    readonly roleLabels = ROLE_LABELS;

    constructor(
        private fb: FormBuilder,
        private service: UsuariosService,
        private rolesService: RolesService,
    ) {
        this.form = this.fb.group({
            nome: ['', [Validators.required, Validators.minLength(3)]],
            username: ['', Validators.required],
            email: ['', [Validators.required, Validators.email]],
            password: ['', [Validators.required, Validators.minLength(6)]],
            roleName: ['VIEWER', Validators.required],
        });
    }

    ngOnInit(): void {
        this.load();
        this.loadAssignableRoles();
    }

    loadAssignableRoles(): void {
        this.rolesService.list().subscribe({
            next: roles => {
                this.assignableRoles = roles.filter(r => r.assignable);
            },
        });
    }

    roleLabel(name: string): string {
        return this.roleLabels[name] ?? name;
    }

    load(): void {
        this.loading = true;

        this.service.list().subscribe({
            next: data => {
                this.usuarios = Array.isArray(data) ? data : Object.values(data);
                this.applyFilter();
                this.loading = false;
            },
            error: () => {
                this.error = 'Erro ao carregar usuários.';
                this.loading = false;
            },
        });
    }

    applyFilter(): void {
        const q = this.search.toLowerCase();

        this.filtered = this.usuarios.filter(u =>
            u.nome.toLowerCase().includes(q) ||
            u.username.toLowerCase().includes(q) ||
            u.email.toLowerCase().includes(q)
        );
    }

    abrirFormulario(): void {
        this.form.reset();
        this.formError = '';
        this.success = '';
        this.formVisivel = true;
    }

    cancelarFormulario(): void {
        if (this.saving) return;
        this.formVisivel = false;
        this.form.reset();
        this.formError = '';
    }

    onSubmit(): void {
        if (this.form.invalid) {
            this.form.markAllAsTouched();
            return;
        }

        this.saving = true;
        this.formError = '';

        const { roleName, ...rest } = this.form.value;
        const payload: UserCreateRequest = {
            ...rest,
            roles: [roleName],
        };

        this.service.create(payload).subscribe({
            next: () => {
                this.saving = false;
                this.formVisivel = false;
                this.form.reset();
                this.success = 'Usuário criado com sucesso!';
                this.load();
            },
            error: err => {
                this.saving = false;
                this.formError = getApiErrorMessage(err, 'Erro ao criar usuário.');
            },
        });
    }

    get f() { return this.form.controls; }
}
