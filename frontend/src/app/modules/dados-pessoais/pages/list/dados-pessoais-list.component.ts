import { Component, OnInit } from '@angular/core';
import { DadoPessoalResponse } from '../../models/dado-pessoal.model';
import { DadosPessoaisService } from '../../services/dados-pessoais.service';
import { TitularesService } from '../../../titulares/services/titulares.service';
import { TitularResponse } from '../../../titulares/models/titular.model';

@Component({
    selector: 'app-dados-pessoais-list',
    templateUrl: './dados-pessoais-list.component.html',
    styleUrls: ['./dados-pessoais-list.component.scss'],
})
export class DadosPessoaisListComponent implements OnInit {
    titulares: TitularResponse[] = [];
    titularId: number | null = null;
    dados: DadoPessoalResponse[] = [];
    loading = false;
    error = '';

    constructor(
        private service: DadosPessoaisService,
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
                this.dados = Array.isArray(data) ? data : Object.values(data);
                this.loading = false;
            },
            error: () => { this.error = 'Erro ao carregar dados pessoais.'; this.loading = false; },
        });
    }
}
