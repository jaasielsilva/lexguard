import { Component, OnInit } from '@angular/core';
import { ConsentimentoResponse } from '../../models/consentimento.model';
import { ConsentimentosService } from '../../services/consentimentos.service';
import { TitularesService } from '../../../titulares/services/titulares.service';
import { TitularResponse } from '../../../titulares/models/titular.model';

@Component({
    selector: 'app-consentimentos-list',
    templateUrl: './consentimentos-list.component.html',
    styleUrls: ['./consentimentos-list.component.scss'],
})
export class ConsentimentosListComponent implements OnInit {
    titulares: TitularResponse[] = [];
    titularId: number | null = null;
    consentimentos: ConsentimentoResponse[] = [];
    loading = false;
    error = '';

    constructor(
        private service: ConsentimentosService,
        private titularesService: TitularesService
    ) { }

    ngOnInit(): void {
        this.titularesService.list().subscribe({
            next: data => {
                this.titulares = Array.isArray(data) ? data : Object.values(data);
                if (this.titulares.length > 0) {
                    this.titularId = this.titulares[0].id;
                    this.load();
                }
            },
            error: () => { this.error = 'Erro ao carregar titulares.'; },
        });
    }

    load(): void {
        if (!this.titularId) return;
        this.loading = true;
        this.error = '';
        this.service.listByTitular(this.titularId).subscribe({
            next: data => {
                this.consentimentos = Array.isArray(data) ? data : Object.values(data);
                this.loading = false;
            },
            error: () => { this.error = 'Erro ao carregar consentimentos.'; this.loading = false; },
        });
    }
}
