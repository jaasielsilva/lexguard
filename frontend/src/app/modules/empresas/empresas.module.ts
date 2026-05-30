import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { EmpresasListComponent } from './pages/list/empresas-list.component';

const routes: Routes = [
    { path: '', component: EmpresasListComponent },
];

@NgModule({
    declarations: [EmpresasListComponent],
    imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule.forChild(routes)],
})
export class EmpresasModule { }
