import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { UsuariosListComponent } from './pages/list/usuarios-list.component';

const routes: Routes = [
    { path: '', component: UsuariosListComponent },
];

@NgModule({
    declarations: [UsuariosListComponent],
    imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule.forChild(routes)],
})
export class UsuariosModule { }
