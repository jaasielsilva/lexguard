import { Component, OnInit } from '@angular/core';
import { RelatorioResponse } from '../../models/relatorio.model';
import { RelatoriosService } from '../../services/relatorios.service';

@Component({
    selector: 'app-relatorios-list',
    templateUrl: './relatorios-list.component.html',
    styleUrls: ['./relatorios-list.component.scss'],
})
export class RelatoriosListComponent implements OnInit {
    relatorios: RelatorioResponse[] = [];
    filtered: RelatorioResponse[] = [];
    loading = true;
    error = '';
    search = '';

    constructor(private service: RelatoriosService) { }

    ngOnInit(): void { this.load(); }

    load(): void {
        this.loading = true;
        this.service.list().subscribe({
            next: data => {
                this.relatorios = Array.isArray(data) ? data : Object.values(data);
                this.applyFilter();
                this.loading = false;
            },
            error: () => { this.error = 'Erro ao carregar relatórios.'; this.loading = false; },
        });
    }

    applyFilter(): void {
        const q = this.search.toLowerCase();
        this.filtered = this.relatorios.filter(r =>
            r.titulo.toLowerCase().includes(q) ||
            r.tipo.toLowerCase().includes(q)
        );
    }
}
