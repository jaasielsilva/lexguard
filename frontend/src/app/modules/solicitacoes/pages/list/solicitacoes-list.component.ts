import { Component, OnInit } from '@angular/core';
import { SolicitacaoResponse } from '../../models/solicitacao.model';
import { SolicitacoesService } from '../../services/solicitacoes.service';

@Component({
    selector: 'app-solicitacoes-list',
    templateUrl: './solicitacoes-list.component.html',
    styleUrls: ['./solicitacoes-list.component.scss'],
})
export class SolicitacoesListComponent implements OnInit {
    solicitacoes: SolicitacaoResponse[] = [];
    filtered: SolicitacaoResponse[] = [];
    loading = true;
    error = '';
    search = '';

    constructor(private service: SolicitacoesService) { }

    ngOnInit(): void { this.load(); }

    load(): void {
        this.loading = true;
        this.service.list().subscribe({
            next: data => {
                this.solicitacoes = Array.isArray(data) ? data : Object.values(data);
                this.applyFilter();
                this.loading = false;
            },
            error: () => { this.error = 'Erro ao carregar solicitações.'; this.loading = false; },
        });
    }

    applyFilter(): void {
        const q = this.search.toLowerCase();
        this.filtered = this.solicitacoes.filter(s =>
            s.tipo.toLowerCase().includes(q) ||
            s.status.toLowerCase().includes(q) ||
            s.descricao.toLowerCase().includes(q)
        );
    }
}
