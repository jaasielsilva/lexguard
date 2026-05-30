import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';

import { TitularesListComponent } from './pages/list/titulares-list.component';
import { TitularFormComponent } from './pages/form/titular-form.component';

const routes: Routes = [
    { path: '', component: TitularesListComponent },
    { path: 'novo', component: TitularFormComponent },
    { path: 'editar/:id', component: TitularFormComponent },
];

@NgModule({
    declarations: [TitularesListComponent, TitularFormComponent],
    imports: [CommonModule, ReactiveFormsModule, FormsModule, RouterModule.forChild(routes)],
})
export class TitularesModule { }
