import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { PermissionService } from '../../../../core/services/permission.service';
import { getApiErrorMessage } from '../../../../core/utils/api-error.util';
import { TitularResponse } from '../../../titulares/models/titular.model';
import { ConsentimentoRequest, ConsentimentoResponse, LegalBasis } from '../../models/consentimento.model';
import { ConsentimentosService } from '../../services/consentimentos.service';

@Component({
    selector: 'app-consentimentos-list',
    templateUrl: './consentimentos-list.component.html',
    styleUrls: ['./consentimentos-list.component.scss'],
})
export class ConsentimentosListComponent {
    titularSelecionado: TitularResponse | null = null;
    titularId: number | null = null;
    consentimentos: ConsentimentoResponse[] = [];
    loading = false;
    error = '';
    success = '';
    formVisivel = false;
    formError = '';
    saving = false;
    form: FormGroup;
    canCreate = false;

    readonly basesLegais: LegalBasis[] = [
        'CONSENTIMENTO', 'OBRIGACAO_LEGAL', 'EXECUCAO_CONTRATO', 'INTERESSE_LEGITIMO',
        'PROTECAO_VIDA', 'SERVICO_PUBLICO', 'EXERCICIO_DIREITO',
    ];

    constructor(
        private fb: FormBuilder,
        private service: ConsentimentosService,
        private permissions: PermissionService,
    ) {
        this.canCreate = this.permissions.hasPermission('CONSENT_MANAGE');
        this.form = this.fb.group({
            finalidade: ['', Validators.required],
            baseLegal: ['CONSENTIMENTO', Validators.required],
            versaoTermo: ['', Validators.required],
        });
    }

    onTitularChange(titular: TitularResponse | null): void {
        this.titularSelecionado = titular;
        this.titularId = titular?.id ?? null;
        this.success = '';
        if (this.titularId) {
            this.load();
        } else {
            this.consentimentos = [];
        }
    }

    load(): void {
        if (!this.titularId) return;
        this.loading = true;
        this.error = '';
        this.service.listByTitular(this.titularId).subscribe({
            next: data => {
                this.consentimentos = Array.isArray(data) ? data : Object.values(data);
                this.loading = false;
            },
            error: () => {
                this.error = 'Erro ao carregar consentimentos.';
                this.loading = false;
            },
        });
    }

    abrirFormulario(): void {
        if (!this.titularId) {
            this.error = 'Localize e selecione um titular antes de registrar consentimento.';
            return;
        }
        this.form.reset({
            finalidade: '',
            baseLegal: 'CONSENTIMENTO',
            versaoTermo: '',
        });
        this.formError = '';
        this.success = '';
        this.formVisivel = true;
    }

    cancelarFormulario(): void {
        if (this.saving) return;
        this.formVisivel = false;
        this.formError = '';
    }

    onSubmit(): void {
        if (!this.titularId || this.form.invalid) {
            this.form.markAllAsTouched();
            return;
        }
        this.saving = true;
        this.formError = '';
        const payload: ConsentimentoRequest = {
            titularId: this.titularId,
            ...this.form.value,
        };
        this.service.create(payload).subscribe({
            next: () => {
                this.saving = false;
                this.formVisivel = false;
                this.success = 'Consentimento registrado com sucesso.';
                this.load();
            },
            error: err => {
                this.saving = false;
                this.formError = getApiErrorMessage(err, 'Erro ao registrar consentimento.');
            },
        });
    }
}
