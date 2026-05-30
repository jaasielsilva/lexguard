import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';

import { DashboardComponent } from './dashboard.component';
import { ShellComponent } from './shell/shell.component';

const routes: Routes = [
    {
        path: '',
        component: ShellComponent,
        children: [
            { path: '', component: DashboardComponent },
            {
                path: 'titulares',
                loadChildren: () => import('../modules/titulares/titulares.module').then(m => m.TitularesModule),
            },
            {
                path: 'dados-pessoais',
                loadChildren: () => import('../modules/dados-pessoais/dados-pessoais.module').then(m => m.DadosPessoaisModule),
            },
            {
                path: 'consentimentos',
                loadChildren: () => import('../modules/consentimentos/consentimentos.module').then(m => m.ConsentimentosModule),
            },
            {
                path: 'solicitacoes',
                loadChildren: () => import('../modules/solicitacoes/solicitacoes.module').then(m => m.SolicitacoesModule),
            },
            {
                path: 'relatorios',
                loadChildren: () => import('../modules/relatorios/relatorios.module').then(m => m.RelatoriosModule),
            },
            {
                path: 'auditoria',
                loadChildren: () => import('../modules/auditoria/auditoria.module').then(m => m.AuditoriaModule),
            },
            {
                path: 'usuarios',
                loadChildren: () => import('../modules/usuarios/usuarios.module').then(m => m.UsuariosModule),
            },
            {
                path: 'empresas',
                loadChildren: () => import('../modules/empresas/empresas.module').then(m => m.EmpresasModule),
            },
        ],
    },
];

@NgModule({
    declarations: [DashboardComponent, ShellComponent],
    imports: [CommonModule, ReactiveFormsModule, FormsModule, RouterModule.forChild(routes)],
})
export class DashboardModule { }
