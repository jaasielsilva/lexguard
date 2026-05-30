import { Component, OnInit } from '@angular/core';
import { forkJoin } from 'rxjs';
import { DashboardMetrics } from './models/dashboard.models';
import { ComplianceScore } from './models/compliance.models';
import { DashboardService } from './services/dashboard.service';
import { ComplianceService } from './services/compliance.service';

interface MetricCard {
    label: string;
    value: number | string;
    icon: string;
    color: string;
    description: string;
}

@Component({
    selector: 'app-dashboard',
    templateUrl: './dashboard.component.html',
    styleUrls: ['./dashboard.component.scss'],
})
export class DashboardComponent implements OnInit {
    metrics: DashboardMetrics | null = null;
    compliance: ComplianceScore | null = null;
    loading = true;
    error = '';

    cards: MetricCard[] = [];

    constructor(
        private dashboardService: DashboardService,
        private complianceService: ComplianceService,
    ) { }

    ngOnInit(): void {
        this.loadMetrics();
    }

    loadMetrics(): void {
        this.loading = true;
        this.error = '';

        forkJoin({
            metrics: this.dashboardService.getMetrics(),
            compliance: this.complianceService.getScore(),
        }).subscribe({
            next: ({ metrics, compliance }) => {
                this.metrics = metrics;
                this.compliance = compliance;
                this.buildCards(metrics);
                this.loading = false;
            },
            error: () => {
                this.error = 'Não foi possível carregar as métricas. Tente novamente.';
                this.loading = false;
            },
        });
    }

    get scoreColor(): string {
        if (!this.compliance) return 'gray';
        if (this.compliance.score >= 80) return '#22c55e';
        if (this.compliance.score >= 50) return '#f59e0b';
        return '#ef4444';
    }

    get scoreStatusClass(): string {
        if (!this.compliance) return '';
        if (this.compliance.score >= 80) return 'score--low';
        if (this.compliance.score >= 50) return 'score--medium';
        return 'score--critical';
    }

    private buildCards(m: DashboardMetrics): void {
        this.cards = [
            {
                label: 'Titulares Ativos',
                value: m.totalTitulares,
                icon: 'bi-people-fill',
                color: 'blue',
                description: 'Total de titulares cadastrados',
            },
            {
                label: 'Dados Sensíveis',
                value: m.totalDadosSensiveis,
                icon: 'bi-shield-lock-fill',
                color: 'red',
                description: 'Registros classificados como sensíveis',
            },
            {
                label: 'Consentimentos Ativos',
                value: m.consentimentosAtivos,
                icon: 'bi-file-earmark-check-fill',
                color: 'green',
                description: 'Consentimentos vigentes',
            },
            {
                label: 'Solicitações Pendentes',
                value: m.solicitacoesPendentes,
                icon: 'bi-inbox-fill',
                color: 'orange',
                description: 'Aguardando atendimento',
            },
            {
                label: 'Acessos Registrados',
                value: m.acessosRecentes,
                icon: 'bi-eye-fill',
                color: 'purple',
                description: 'Logs de acesso a dados',
            },
            {
                label: 'Status Compliance',
                value: m.complianceStatus,
                icon: 'bi-shield-fill-check',
                color: m.complianceStatus === 'SUFICIENTE' ? 'green' : 'orange',
                description: 'Avaliação geral de conformidade',
            },
        ];
    }
}
