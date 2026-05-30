import { Component, OnInit } from '@angular/core';
import { AuditLogResponse } from '../../models/audit-log.model';
import { AuditoriaService } from '../../services/auditoria.service';

@Component({
    selector: 'app-auditoria-list',
    templateUrl: './auditoria-list.component.html',
    styleUrls: ['./auditoria-list.component.scss'],
})
export class AuditoriaListComponent implements OnInit {
    logs: AuditLogResponse[] = [];
    filtered: AuditLogResponse[] = [];
    loading = true;
    error = '';
    search = '';

    constructor(private service: AuditoriaService) { }

    ngOnInit(): void { this.load(); }

    load(): void {
        this.loading = true;
        this.service.list().subscribe({
            next: data => {
                this.logs = Array.isArray(data) ? data : Object.values(data);
                this.applyFilter();
                this.loading = false;
            },
            error: () => { this.error = 'Erro ao carregar logs de auditoria.'; this.loading = false; },
        });
    }

    applyFilter(): void {
        const q = this.search.toLowerCase();
        this.filtered = this.logs.filter(l =>
            l.action.toLowerCase().includes(q) ||
            l.usuario.toLowerCase().includes(q) ||
            l.descricao.toLowerCase().includes(q)
        );
    }
}
