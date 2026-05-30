import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subject, Subscription, debounceTime, distinctUntilChanged } from 'rxjs';
import {
    AuditActionGroup,
    AuditLogAction,
    AuditLogResponse,
} from '../../models/audit-log.model';
import { AuditoriaService } from '../../services/auditoria.service';

type ActionGroupChip = { id: AuditActionGroup | null; label: string };

@Component({
    selector: 'app-auditoria-list',
    templateUrl: './auditoria-list.component.html',
    styleUrls: ['./auditoria-list.component.scss'],
})
export class AuditoriaListComponent implements OnInit, OnDestroy {
    logs: AuditLogResponse[] = [];
    loading = true;
    loadingMore = false;
    error = '';
    search = '';
    actionGroup: AuditActionGroup | null = null;
    totalElements = 0;
    currentPage = 0;
    hasMore = false;
    private readonly pageSize = 25;

    private readonly search$ = new Subject<string>();
    private searchSub?: Subscription;

    readonly actionGroupChips: ActionGroupChip[] = [
        { id: null, label: 'Todas' },
        { id: 'AUTH', label: 'Autenticação' },
        { id: 'TITULAR', label: 'Titulares' },
        { id: 'CONSENT', label: 'Consentimentos' },
        { id: 'DATA', label: 'Dados' },
        { id: 'REQUEST', label: 'Solicitações' },
        { id: 'REPORT', label: 'Relatórios' },
    ];

    constructor(private service: AuditoriaService) { }

    ngOnInit(): void {
        this.searchSub = this.search$
            .pipe(debounceTime(400), distinctUntilChanged())
            .subscribe(() => this.loadPage(false));
        this.loadPage(false);
    }

    ngOnDestroy(): void {
        this.searchSub?.unsubscribe();
    }

    get filtrosAtivos(): boolean {
        return this.actionGroup != null || this.search.trim().length > 0;
    }

    onSearchChange(): void {
        this.search$.next(this.search);
    }

    setActionGroup(group: AuditActionGroup | null): void {
        this.actionGroup = group;
        this.loadPage(false);
    }

    limparFiltros(): void {
        this.search = '';
        this.actionGroup = null;
        this.loadPage(false);
    }

    loadPage(append: boolean): void {
        if (append) {
            if (!this.hasMore || this.loadingMore) return;
            this.loadingMore = true;
        } else {
            this.loading = true;
            this.currentPage = 0;
            this.logs = [];
        }
        this.error = '';

        const page = append ? this.currentPage + 1 : 0;
        this.service.search(this.search, page, this.pageSize, this.actionGroup).subscribe({
            next: res => {
                this.currentPage = res.page;
                this.totalElements = res.totalElements;
                this.hasMore = res.hasMore;
                this.logs = append ? [...this.logs, ...res.items] : [...res.items];
                this.loading = false;
                this.loadingMore = false;
            },
            error: () => {
                this.error = 'Erro ao carregar logs de auditoria.';
                this.loading = false;
                this.loadingMore = false;
            },
        });
    }

    loadMore(): void {
        this.loadPage(true);
    }

    actionLabel(action: AuditLogAction): string {
        const labels: Record<AuditLogAction, string> = {
            LOGIN: 'Login',
            LOGOUT: 'Logout',
            CREATE_TITULAR: 'Criar titular',
            UPDATE_TITULAR: 'Atualizar titular',
            DELETE_TITULAR: 'Excluir titular',
            REGISTER_CONSENT: 'Registrar consentimento',
            REVOKE_CONSENT: 'Revogar consentimento',
            DATA_ACCESS: 'Acesso a dados',
            DATA_EXPORT: 'Exportação de dados',
            REQUEST_SUBMITTED: 'Solicitação registrada',
            REQUEST_HANDLED: 'Solicitação tratada',
            REPORT_GENERATED: 'Relatório gerado',
        };
        return labels[action] ?? action;
    }

    actionBadgeClass(action: AuditLogAction): string {
        if (action === 'LOGIN' || action === 'LOGOUT') return 'badge-blue';
        if (action.includes('TITULAR')) return 'badge-blue';
        if (action.includes('CONSENT')) return 'badge-green';
        if (action.startsWith('DATA_')) return 'badge-amber';
        if (action.startsWith('REQUEST_')) return 'badge-amber';
        if (action === 'REPORT_GENERATED') return 'badge-gray';
        return 'badge-gray';
    }
}
