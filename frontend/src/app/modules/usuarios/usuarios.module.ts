import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { UsuariosListComponent } from './pages/list/usuarios-list.component';
import { PerfisListComponent } from './pages/perfis/perfis-list.component';
import { PerfilEditComponent } from './pages/perfis/perfil-edit.component';

const routes: Routes = [
    { path: '', component: UsuariosListComponent },
    { path: 'perfis', component: PerfisListComponent },
    { path: 'perfis/:id', component: PerfilEditComponent },
];

@NgModule({
    declarations: [UsuariosListComponent, PerfisListComponent, PerfilEditComponent],
    imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule.forChild(routes)],
})
export class UsuariosModule { }
