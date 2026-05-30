import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { FormsModule } from '@angular/forms';

import { DadosPessoaisListComponent } from './pages/list/dados-pessoais-list.component';

const routes: Routes = [
    { path: '', component: DadosPessoaisListComponent },
];

@NgModule({
    declarations: [DadosPessoaisListComponent],
    imports: [CommonModule, FormsModule, RouterModule.forChild(routes)],
})
export class DadosPessoaisModule { }
