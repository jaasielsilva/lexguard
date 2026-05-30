import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { FormsModule } from '@angular/forms';

import { SolicitacoesListComponent } from './pages/list/solicitacoes-list.component';

const routes: Routes = [
    { path: '', component: SolicitacoesListComponent },
];

@NgModule({
    declarations: [SolicitacoesListComponent],
    imports: [CommonModule, FormsModule, RouterModule.forChild(routes)],
})
export class SolicitacoesModule { }
