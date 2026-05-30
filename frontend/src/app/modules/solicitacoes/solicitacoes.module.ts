import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { TitularSearchPanelComponent } from '../../core/components/titular-search-panel/titular-search-panel.component';
import { SolicitacoesListComponent } from './pages/list/solicitacoes-list.component';

const routes: Routes = [
    { path: '', component: SolicitacoesListComponent },
];

@NgModule({
    declarations: [SolicitacoesListComponent],
    imports: [
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        RouterModule.forChild(routes),
        TitularSearchPanelComponent,
    ],
})
export class SolicitacoesModule { }
