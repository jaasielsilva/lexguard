import { Component, HostBinding, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ApiService } from '../../../core/services/api.service';

export interface ComplianceScoreResponse {
    score: number;
    status: string;
    alertas: string[];
    riscoSeguranca: number;
    riscoConsentimento: number;
    riscoAuditoria: number;
    riscoLegal: number;
}

@Component({
    selector: 'app-lgpd-health-score',
    templateUrl: './lgpd-health-score.component.html',
    styleUrls: ['./lgpd-health-score.component.scss'],
})
export class LgpdHealthScoreComponent implements OnInit {
    score: ComplianceScoreResponse | null = null;
    loading = true;
    error = '';
    animatedScore = 0;

    @HostBinding('class.ok') get isOk() { return !!this.score && this.score.score >= 80; }
    @HostBinding('class.atencao') get isAtencao() { return !!this.score && this.score.score >= 50 && this.score.score < 80; }
    @HostBinding('class.critico') get isCritico() { return !!this.score && this.score.score < 50; }

    constructor(private api: ApiService, private router: Router) { }

    ngOnInit(): void {
        this.load();
    }

    load(): void {
        this.loading = true;
        this.error = '';
        this.api.get<ComplianceScoreResponse>('/compliance/score').subscribe({
            next: (data) => {
                this.score = data;
                this.loading = false;
                this.animateScore(data.score);
            },
            error: () => {
                this.error = 'Não foi possível carregar o score.';
                this.loading = false;
            },
        });
    }

    private animateScore(target: number): void {
        const steps = 40;
        const interval = 800 / steps;
        const increment = target / steps;
        let current = 0;
        const timer = setInterval(() => {
            current = Math.min(current + increment, target);
            this.animatedScore = Math.round(current);
            if (current >= target) clearInterval(timer);
        }, interval);
    }

    get statusLabel(): string {
        if (!this.score) return '';
        if (this.score.score >= 80) return 'OK';
        if (this.score.score >= 50) return 'ATENÇÃO';
        return 'CRÍTICO';
    }

    get statusClass(): string {
        if (!this.score) return '';
        if (this.score.score >= 80) return 'status--ok';
        if (this.score.score >= 50) return 'status--atencao';
        return 'status--critico';
    }

    get progressClass(): string {
        if (!this.score) return '';
        if (this.score.score >= 80) return 'progress--green';
        if (this.score.score >= 50) return 'progress--yellow';
        return 'progress--red';
    }

    get topRiscos(): { label: string; valor: number; classe: string }[] {
        if (!this.score) return [];
        return [
            { label: 'Segurança técnica', valor: this.score.riscoSeguranca, classe: this.riscoClass(this.score.riscoSeguranca) },
            { label: 'Consentimentos', valor: this.score.riscoConsentimento, classe: this.riscoClass(this.score.riscoConsentimento) },
            { label: 'Auditoria', valor: this.score.riscoAuditoria, classe: this.riscoClass(this.score.riscoAuditoria) },
            { label: 'Base legal', valor: this.score.riscoLegal, classe: this.riscoClass(this.score.riscoLegal) },
        ]
            .filter(r => r.valor > 0)
            .sort((a, b) => b.valor - a.valor)
            .slice(0, 3);
    }

    private riscoClass(valor: number): string {
        if (valor >= 30) return 'risco--alto';
        if (valor >= 15) return 'risco--medio';
        return 'risco--baixo';
    }

    verRelatorio(): void {
        this.router.navigate(['/dashboard/relatorios']);
    }
}
