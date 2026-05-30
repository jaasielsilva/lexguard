import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { FormsModule } from '@angular/forms';

import { AuditoriaListComponent } from './pages/list/auditoria-list.component';

const routes: Routes = [
    { path: '', component: AuditoriaListComponent },
];

@NgModule({
    declarations: [AuditoriaListComponent],
    imports: [CommonModule, FormsModule, RouterModule.forChild(routes)],
})
export class AuditoriaModule { }
