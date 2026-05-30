import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { TitularSearchPanelComponent } from '../../../../core/components/titular-search-panel/titular-search-panel.component';
import { PermissionService } from '../../../../core/services/permission.service';
import { getApiErrorMessage } from '../../../../core/utils/api-error.util';
import { TitularResponse } from '../../../titulares/models/titular.model';
import {
    RequestStatus,
    RightRequestType,
    SolicitacaoRequest,
    SolicitacaoResponse,
} from '../../models/solicitacao.model';
import { SolicitacoesService } from '../../services/solicitacoes.service';

@Component({
    selector: 'app-solicitacoes-list',
    templateUrl: './solicitacoes-list.component.html',
    styleUrls: ['./solicitacoes-list.component.scss'],
})
export class SolicitacoesListComponent implements OnInit {
    @ViewChild('filtroTitularPanel') filtroTitularPanel?: TitularSearchPanelComponent;
    @ViewChild('modalTitularPanel') modalTitularPanel?: TitularSearchPanelComponent;

    solicitacoes: SolicitacaoResponse[] = [];
    filtered: SolicitacaoResponse[] = [];
    loading = true;
    error = '';
    success = '';
    search = '';
    statusFiltro: RequestStatus | 'TODOS' = 'TODOS';

    readonly statusChips: { id: RequestStatus | 'TODOS'; label: string }[] = [
        { id: 'TODOS', label: 'Todas' },
        { id: 'PENDENTE', label: 'Pendentes' },
        { id: 'EM_PROCESSAMENTO', label: 'Em processamento' },
        { id: 'ATENDIDO', label: 'Atendidas' },
        { id: 'RECUSADO', label: 'Recusadas' },
    ];
    formVisivel = false;
    formError = '';
    saving = false;
    form: FormGroup;
    canManage = false;

    /** Filtro opcional da listagem (fila global quando null). */
    titularFiltro: TitularResponse | null = null;
    /** Titular escolhido no modal de nova solicitação. */
    titularFormulario: TitularResponse | null = null;

    atenderTarget: SolicitacaoResponse | null = null;
    respostaAtendimento = '';
    atendendo = false;

    readonly tipos: RightRequestType[] = ['ACESSO', 'CORRECAO', 'EXCLUSAO', 'PORTABILIDADE'];

    constructor(
        private fb: FormBuilder,
        private service: SolicitacoesService,
        private permissions: PermissionService,
    ) {
        this.canManage = this.permissions.hasPermission('REQUEST_MANAGE');
        this.form = this.fb.group({
            titularId: [null, Validators.required],
            tipo: ['EXCLUSAO', Validators.required],
            descricao: ['', [Validators.required, Validators.minLength(5)]],
        });
    }

    ngOnInit(): void {
        this.load();
    }

    get filtroTitularAtivo(): boolean {
        return this.titularFiltro != null;
    }

    get filtrosAtivos(): boolean {
        return this.filtroTitularAtivo || this.statusFiltro !== 'TODOS' || this.search.trim().length > 0;
    }

    load(): void {
        this.loading = true;
        this.error = '';
        const request$ = this.titularFiltro
            ? this.service.listByTitular(this.titularFiltro.id)
            : this.service.list();

        request$.subscribe({
            next: data => {
                const rows = Array.isArray(data) ? data : Object.values(data as Record<string, SolicitacaoResponse>);
                this.solicitacoes = rows.sort(
                    (a, b) => new Date(b.solicitadoEm).getTime() - new Date(a.solicitadoEm).getTime(),
                );
                this.applyFilter();
                this.loading = false;
            },
            error: () => {
                this.error = 'Erro ao carregar solicitações.';
                this.loading = false;
            },
        });
    }

    applyFilter(): void {
        const q = this.search.toLowerCase().trim();
        this.filtered = this.solicitacoes
            .filter(s => {
                if (this.statusFiltro !== 'TODOS' && s.status !== this.statusFiltro) {
                    return false;
                }
                if (!q) {
                    return true;
                }
                return (
                    s.tipo.toLowerCase().includes(q) ||
                    s.descricao.toLowerCase().includes(q) ||
                    this.tipoLabel(s.tipo).toLowerCase().includes(q)
                );
            })
            .sort((a, b) => new Date(b.solicitadoEm).getTime() - new Date(a.solicitadoEm).getTime());
    }

    setStatusFiltro(status: RequestStatus | 'TODOS'): void {
        this.statusFiltro = status;
        this.applyFilter();
    }

    contagemStatus(status: RequestStatus | 'TODOS'): number {
        if (status === 'TODOS') {
            return this.solicitacoes.length;
        }
        return this.solicitacoes.filter(s => s.status === status).length;
    }

    limparFiltros(): void {
        this.search = '';
        this.statusFiltro = 'TODOS';
        if (this.filtroTitularAtivo) {
            this.verTodasSolicitacoes();
        } else {
            this.applyFilter();
        }
    }

    tipoLabel(tipo: RightRequestType): string {
        const labels: Record<RightRequestType, string> = {
            ACESSO: 'Acesso',
            CORRECAO: 'Correção',
            EXCLUSAO: 'Exclusão',
            PORTABILIDADE: 'Portabilidade',
        };
        return labels[tipo] ?? tipo;
    }

    onTitularFiltroChange(titular: TitularResponse | null): void {
        this.titularFiltro = titular;
        this.success = '';
        this.load();
    }

    verTodasSolicitacoes(): void {
        this.filtroTitularPanel?.applySelection(null);
    }

    titularNome(s: SolicitacaoResponse): string {
        return s.titularNome ?? `ID ${s.titularId}`;
    }

    statusBadgeClass(status: RequestStatus): string {
        switch (status) {
            case 'PENDENTE':
                return 'badge-amber';
            case 'EM_PROCESSAMENTO':
                return 'badge-blue';
            case 'ATENDIDO':
                return 'badge-green';
            case 'RECUSADO':
                return 'badge-red';
            default:
                return 'badge-gray';
        }
    }

    statusLabel(status: RequestStatus): string {
        switch (status) {
            case 'PENDENTE':
                return 'Pendente';
            case 'EM_PROCESSAMENTO':
                return 'Em processamento';
            case 'ATENDIDO':
                return 'Atendida';
            case 'RECUSADO':
                return 'Recusada';
            default:
                return status;
        }
    }

    onTitularFormChange(titular: TitularResponse | null): void {
        this.titularFormulario = titular;
        this.form.patchValue({ titularId: titular?.id ?? null });
        if (titular) {
            this.form.get('titularId')?.markAsTouched();
        }
    }

    abrirFormulario(): void {
        this.formError = '';
        this.success = '';
        this.titularFormulario = null;
        this.form.reset({ titularId: null, tipo: 'EXCLUSAO', descricao: '' });
        this.formVisivel = true;
        if (this.titularFiltro) {
            setTimeout(() => this.modalTitularPanel?.applySelection(this.titularFiltro!), 0);
        }
    }

    cancelarFormulario(): void {
        if (this.saving) return;
        this.formVisivel = false;
        this.modalTitularPanel?.applySelection(null);
    }

    onSubmit(): void {
        if (!this.form.get('titularId')?.value) {
            this.formError = 'Selecione um titular na busca.';
            return;
        }
        if (this.form.invalid) {
            this.form.markAllAsTouched();
            return;
        }
        this.saving = true;
        this.formError = '';
        const payload: SolicitacaoRequest = this.form.value;
        this.service.create(payload).subscribe({
            next: () => {
                this.saving = false;
                this.formVisivel = false;
                this.modalTitularPanel?.applySelection(null);
                this.success = 'Solicitação registrada.';
                this.load();
            },
            error: err => {
                this.saving = false;
                this.formError = getApiErrorMessage(err, 'Erro ao criar solicitação.');
            },
        });
    }

    emProcessamento(s: SolicitacaoResponse): void {
        this.error = '';
        this.service.markInProgress(s.id).subscribe({
            next: () => {
                this.success = 'Status atualizado para em processamento.';
                this.load();
            },
            error: err => {
                this.error = getApiErrorMessage(err, 'Erro ao atualizar status.');
            },
        });
    }

    abrirAtender(s: SolicitacaoResponse): void {
        this.respostaAtendimento = 'Solicitação atendida conforme pedido do titular.';
        this.atenderTarget = s;
    }

    cancelarAtender(): void {
        if (this.atendendo) return;
        this.atenderTarget = null;
        this.respostaAtendimento = '';
    }

    confirmarAtender(): void {
        if (!this.atenderTarget || !this.respostaAtendimento.trim()) return;
        this.atendendo = true;
        this.error = '';
        this.service.markAttended(this.atenderTarget.id, this.respostaAtendimento.trim()).subscribe({
            next: () => {
                this.atendendo = false;
                this.atenderTarget = null;
                this.respostaAtendimento = '';
                this.success = 'Solicitação atendida.';
                this.load();
            },
            error: err => {
                this.atendendo = false;
                this.error = getApiErrorMessage(err, 'Erro ao concluir solicitação.');
            },
        });
    }
}
