import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../auth/services/auth.service';

interface NavItem {
    label: string;
    icon: string;
    route: string;
}

@Component({
    selector: 'app-shell',
    templateUrl: './shell.component.html',
    styleUrls: ['./shell.component.scss'],
})
export class ShellComponent {
    sidebarOpen = true;

    navItems: NavItem[] = [
        { label: 'Dashboard', icon: 'bi-speedometer2', route: '/dashboard' },
        { label: 'Titulares', icon: 'bi-people', route: '/dashboard/titulares' },
        { label: 'Dados Pessoais', icon: 'bi-database-lock', route: '/dashboard/dados-pessoais' },
        { label: 'Consentimentos', icon: 'bi-file-earmark-check', route: '/dashboard/consentimentos' },
        { label: 'Solicitações', icon: 'bi-inbox', route: '/dashboard/solicitacoes' },
        { label: 'Relatórios', icon: 'bi-bar-chart-line', route: '/dashboard/relatorios' },
        { label: 'Auditoria', icon: 'bi-shield-check', route: '/dashboard/auditoria' },
        { label: 'Usuários', icon: 'bi-person-gear', route: '/dashboard/usuarios' },
    ];

    constructor(private auth: AuthService, private router: Router) { }

    toggleSidebar(): void {
        this.sidebarOpen = !this.sidebarOpen;
    }

    logout(): void {
        this.auth.logout();
        this.router.navigate(['/login']);
    }
}
