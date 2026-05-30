import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { TitularSearchPanelComponent } from '../../core/components/titular-search-panel/titular-search-panel.component';
import { DadosPessoaisListComponent } from './pages/list/dados-pessoais-list.component';

const routes: Routes = [
    { path: '', component: DadosPessoaisListComponent },
];

@NgModule({
    declarations: [DadosPessoaisListComponent],
    imports: [
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        RouterModule.forChild(routes),
        TitularSearchPanelComponent,
    ],
})
export class DadosPessoaisModule { }
