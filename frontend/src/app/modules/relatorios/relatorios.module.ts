import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { FormsModule } from '@angular/forms';

import { RelatoriosListComponent } from './pages/list/relatorios-list.component';

const routes: Routes = [
    { path: '', component: RelatoriosListComponent },
];

@NgModule({
    declarations: [RelatoriosListComponent],
    imports: [CommonModule, FormsModule, RouterModule.forChild(routes)],
})
export class RelatoriosModule { }
