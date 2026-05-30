import { Component, OnInit } from '@angular/core';
import { DashboardMetrics } from './models/dashboard.models';
import { DashboardService } from './services/dashboard.service';

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
    loading = true;
    error = '';

    cards: MetricCard[] = [];

    constructor(private dashboardService: DashboardService) { }

    ngOnInit(): void {
        this.loadMetrics();
    }

    loadMetrics(): void {
        this.loading = true;
        this.error = '';

        this.dashboardService.getMetrics().subscribe({
            next: (data) => {
                this.metrics = data;
                this.buildCards(data);
                this.loading = false;
            },
            error: () => {
                this.error = 'Não foi possível carregar as métricas. Tente novamente.';
                this.loading = false;
            },
        });
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
                icon: 'bi-database-lock-fill',
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
