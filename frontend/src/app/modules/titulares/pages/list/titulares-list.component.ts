import { Component, OnInit } from '@angular/core';
import { TitularResponse } from '../../models/titular.model';
import { TitularesService } from '../../services/titulares.service';

@Component({
    selector: 'app-titulares-list',
    templateUrl: './titulares-list.component.html',
    styleUrls: ['./titulares-list.component.scss'],
})
export class TitularesListComponent implements OnInit {
    titulares: TitularResponse[] = [];
    filtered: TitularResponse[] = [];
    loading = true;
    error = '';
    search = '';
    deleteId: number | null = null;
    deleting = false;

    constructor(private service: TitularesService) { }

    ngOnInit(): void { this.load(); }

    load(): void {
        this.loading = true;
        this.service.list().subscribe({
            next: data => {
                this.titulares = Array.isArray(data) ? data : Object.values(data);
                this.applyFilter();
                this.loading = false;
            },
            error: () => { this.error = 'Erro ao carregar titulares.'; this.loading = false; },
        });
    }

    applyFilter(): void {
        const q = this.search.toLowerCase();
        this.filtered = this.titulares.filter(t =>
            t.nome.toLowerCase().includes(q) ||
            t.cpf.includes(q) ||
            t.email.toLowerCase().includes(q)
        );
    }

    confirmDelete(id: number): void { this.deleteId = id; }
    cancelDelete(): void { this.deleteId = null; }

    doDelete(): void {
        if (!this.deleteId) return;
        this.deleting = true;
        this.service.delete(this.deleteId).subscribe({
            next: () => { this.deleteId = null; this.deleting = false; this.load(); },
            error: () => { this.deleting = false; this.error = 'Erro ao excluir titular.'; },
        });
    }
}
