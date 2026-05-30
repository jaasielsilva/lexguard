import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { getApiErrorMessage } from '../../../../core/utils/api-error.util';
import { EmpresaRequest, EmpresaResponse } from '../../models/empresa.model';
import { EmpresasService } from '../../services/empresas.service';

@Component({
    selector: 'app-empresas-list',
    templateUrl: './empresas-list.component.html',
    styleUrls: ['./empresas-list.component.scss'],
})
export class EmpresasListComponent implements OnInit {

    empresas: EmpresaResponse[] = [];
    filtered: EmpresaResponse[] = [];
    loading = true;
    error = '';
    success = '';
    search = '';

    // Modal de criação/edição
    formVisivel = false;
    editando: EmpresaResponse | null = null;
    formError = '';
    saving = false;
    form: FormGroup;

    // Modal de confirmação toggle
    confirmVisivel = false;
    confirmTarget: EmpresaResponse | null = null;
    confirmLoading = false;

    constructor(
        private fb: FormBuilder,
        private service: EmpresasService,
    ) {
        this.form = this.fb.group({
            nome: ['', [Validators.required, Validators.minLength(3)]],
            cnpj: ['', [Validators.required, Validators.pattern(/^\d{14}$|^\d{2}\.\d{3}\.\d{3}\/\d{4}-\d{2}$/)]],
            contatoEmail: ['', [Validators.required, Validators.email]],
        });
    }

    ngOnInit(): void {
        this.load();
    }

    load(): void {
        this.loading = true;
        this.error = '';
        this.service.list().subscribe({
            next: data => {
                this.empresas = data;
                this.applyFilter();
                this.loading = false;
            },
            error: () => {
                this.error = 'Erro ao carregar empresas.';
                this.loading = false;
            },
        });
    }

    applyFilter(): void {
        const q = this.search.toLowerCase();
        this.filtered = this.empresas.filter(e =>
            e.nome.toLowerCase().includes(q) ||
            e.cnpj.includes(q) ||
            e.contatoEmail.toLowerCase().includes(q),
        );
    }

    // ── Formulário ────────────────────────────────────────────────────────────

    abrirNovo(): void {
        this.editando = null;
        this.form.reset();
        this.formError = '';
        this.success = '';
        this.formVisivel = true;
    }

    abrirEdicao(empresa: EmpresaResponse): void {
        this.editando = empresa;
        this.form.patchValue({
            nome: empresa.nome,
            cnpj: empresa.cnpj,
            contatoEmail: empresa.contatoEmail,
        });
        this.formError = '';
        this.success = '';
        this.formVisivel = true;
    }

    cancelarFormulario(): void {
        if (this.saving) return;
        this.formVisivel = false;
        this.editando = null;
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

        const payload: EmpresaRequest = this.form.value;
        const op = this.editando
            ? this.service.update(this.editando.id, payload)
            : this.service.create(payload);

        op.subscribe({
            next: () => {
                this.saving = false;
                this.success = this.editando
                    ? 'Empresa atualizada com sucesso!'
                    : 'Empresa cadastrada com sucesso!';
                this.formVisivel = false;
                this.editando = null;
                this.form.reset();
                this.load();
            },
            error: err => {
                this.saving = false;
                this.formError = getApiErrorMessage(err, 'Erro ao salvar empresa.');
            },
        });
    }

    // ── Toggle ativo ──────────────────────────────────────────────────────────

    abrirConfirm(empresa: EmpresaResponse): void {
        this.confirmTarget = empresa;
        this.confirmVisivel = true;
    }

    cancelarConfirm(): void {
        if (this.confirmLoading) return;
        this.confirmVisivel = false;
        this.confirmTarget = null;
    }

    confirmarToggle(): void {
        if (!this.confirmTarget) return;
        this.confirmLoading = true;
        this.service.toggleAtivo(this.confirmTarget.id).subscribe({
            next: () => {
                this.confirmLoading = false;
                this.confirmVisivel = false;
                this.confirmTarget = null;
                this.load();
            },
            error: () => {
                this.confirmLoading = false;
                this.error = 'Erro ao alterar status da empresa.';
                this.confirmVisivel = false;
            },
        });
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    formatCnpj(cnpj: string): string {
        const d = cnpj.replace(/\D/g, '');
        if (d.length !== 14) return cnpj;
        return `${d.slice(0, 2)}.${d.slice(2, 5)}.${d.slice(5, 8)}/${d.slice(8, 12)}-${d.slice(12)}`;
    }

    get f() { return this.form.controls; }
}
