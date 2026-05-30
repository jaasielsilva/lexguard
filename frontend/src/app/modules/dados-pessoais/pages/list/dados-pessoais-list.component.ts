import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { PermissionService } from '../../../../core/services/permission.service';
import { getApiErrorMessage } from '../../../../core/utils/api-error.util';
import {
    DadoPessoalRequest,
    DadoPessoalResponse,
    DataClassification,
    LegalBasis,
    RiskLevel,
} from '../../models/dado-pessoal.model';
import { DadosPessoaisService } from '../../services/dados-pessoais.service';
import { TitularResponse } from '../../../titulares/models/titular.model';

@Component({
    selector: 'app-dados-pessoais-list',
    templateUrl: './dados-pessoais-list.component.html',
    styleUrls: ['./dados-pessoais-list.component.scss'],
})
export class DadosPessoaisListComponent {
    titularSelecionado: TitularResponse | null = null;
    titularId: number | null = null;
    dados: DadoPessoalResponse[] = [];
    loading = false;
    error = '';
    success = '';
    formVisivel = false;
    formError = '';
    saving = false;
    form: FormGroup;
    canCreate = false;

    readonly classificacoes: DataClassification[] = ['PESSOAL', 'SENSIVEL'];
    readonly basesLegais: LegalBasis[] = [
        'CONSENTIMENTO', 'OBRIGACAO_LEGAL', 'EXECUCAO_CONTRATO', 'INTERESSE_LEGITIMO',
        'PROTECAO_VIDA', 'SERVICO_PUBLICO', 'EXERCICIO_DIREITO',
    ];
    readonly riscos: RiskLevel[] = ['BAIXO', 'MEDIO', 'ALTO', 'CRITICO'];

    constructor(
        private fb: FormBuilder,
        private service: DadosPessoaisService,
        private permissions: PermissionService,
    ) {
        this.canCreate = this.permissions.hasPermission('DATA_WRITE');
        this.form = this.fb.group({
            nomeCampo: ['', Validators.required],
            valor: ['', Validators.required],
            classificacao: ['PESSOAL', Validators.required],
            baseLegal: ['EXECUCAO_CONTRATO', Validators.required],
            finalidade: ['', Validators.required],
            localArmazenamento: [''],
            risco: ['MEDIO', Validators.required],
        });
    }

    onTitularChange(titular: TitularResponse | null): void {
        this.titularSelecionado = titular;
        this.titularId = titular?.id ?? null;
        this.success = '';
        if (this.titularId) {
            this.load();
        } else {
            this.dados = [];
        }
    }

    load(): void {
        if (!this.titularId) return;
        this.loading = true;
        this.error = '';
        this.service.listByTitular(this.titularId).subscribe({
            next: data => {
                this.dados = Array.isArray(data) ? data : Object.values(data);
                this.loading = false;
            },
            error: () => {
                this.error = 'Erro ao carregar dados pessoais.';
                this.loading = false;
            },
        });
    }

    abrirFormulario(): void {
        if (!this.titularId) {
            this.error = 'Localize e selecione um titular antes de registrar dados pessoais.';
            return;
        }
        this.form.reset({
            nomeCampo: '',
            valor: '',
            classificacao: 'PESSOAL',
            baseLegal: 'EXECUCAO_CONTRATO',
            finalidade: '',
            localArmazenamento: 'Sistema LexGuard',
            risco: 'MEDIO',
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
        const payload: DadoPessoalRequest = {
            titularId: this.titularId,
            ...this.form.value,
        };
        this.service.create(payload).subscribe({
            next: () => {
                this.saving = false;
                this.formVisivel = false;
                this.success = 'Dado pessoal cadastrado com sucesso.';
                this.load();
            },
            error: err => {
                this.saving = false;
                this.formError = getApiErrorMessage(err, 'Erro ao cadastrar dado pessoal.');
            },
        });
    }

    get f() { return this.form.controls; }
}
