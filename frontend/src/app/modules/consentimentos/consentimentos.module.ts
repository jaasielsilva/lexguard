import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { FormsModule } from '@angular/forms';

import { ConsentimentosListComponent } from './pages/list/consentimentos-list.component';

const routes: Routes = [
    { path: '', component: ConsentimentosListComponent },
];

@NgModule({
    declarations: [ConsentimentosListComponent],
    imports: [CommonModule, FormsModule, RouterModule.forChild(routes)],
})
export class ConsentimentosModule { }
