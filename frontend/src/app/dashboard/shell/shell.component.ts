import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { PermissionCode } from '../../core/constants/permissions';
import { PermissionService } from '../../core/services/permission.service';
import { AuthService } from '../../auth/services/auth.service';
import { ROLE_LABELS } from '../../core/constants/permissions';

interface NavItem {
    label: string;
    icon: string;
    route: string;
    /** Permissoes necessarias (qualquer uma). Vazio = so autenticado. */
    permissions?: PermissionCode[];
    /** Visível apenas para SUPER_ADMIN */
    superAdminOnly?: boolean;
}

@Component({
    selector: 'app-shell',
    templateUrl: './shell.component.html',
    styleUrls: ['./shell.component.scss'],
})
export class ShellComponent implements OnInit {
    sidebarOpen = true;

    private readonly allNavItems: NavItem[] = [
        { label: 'Dashboard', icon: 'bi-speedometer2', route: '/dashboard' },
        { label: 'Titulares', icon: 'bi-people', route: '/dashboard/titulares', permissions: ['DATA_READ'] },
        { label: 'Dados Pessoais', icon: 'bi-database-lock', route: '/dashboard/dados-pessoais', permissions: ['DATA_READ'] },
        { label: 'Consentimentos', icon: 'bi-file-earmark-check', route: '/dashboard/consentimentos', permissions: ['CONSENT_MANAGE'] },
        { label: 'Solicitações', icon: 'bi-inbox', route: '/dashboard/solicitacoes', permissions: ['REQUEST_MANAGE'] },
        { label: 'Relatórios', icon: 'bi-bar-chart-line', route: '/dashboard/relatorios', permissions: ['REPORT_READ'] },
        { label: 'Auditoria', icon: 'bi-shield-check', route: '/dashboard/auditoria', permissions: ['AUDIT_READ'] },
        { label: 'Usuários', icon: 'bi-person-gear', route: '/dashboard/usuarios', permissions: ['USER_MANAGE'] },
        { label: 'Perfis', icon: 'bi-key', route: '/dashboard/usuarios/perfis', permissions: ['USER_MANAGE'] },
        { label: 'Empresas', icon: 'bi-building', route: '/dashboard/empresas', superAdminOnly: true },
    ];

    navItems: NavItem[] = [];
    displayRole = '';

    constructor(
        private auth: AuthService,
        private permissions: PermissionService,
        private router: Router,
    ) { }

    ngOnInit(): void {
        this.navItems = this.allNavItems.filter(item => {
            if (item.superAdminOnly) {
                return this.permissions.getRoles().includes('SUPER_ADMIN');
            }
            return !item.permissions || this.permissions.hasAnyPermission(...item.permissions);
        });
        const roles = this.permissions.getRoles();
        const primary = roles.find(r => r !== 'USER') ?? roles[0];
        this.displayRole = primary ? (ROLE_LABELS[primary] ?? primary) : '';
    }

    toggleSidebar(): void {
        this.sidebarOpen = !this.sidebarOpen;
    }

    logout(): void {
        this.auth.logout();
        this.router.navigate(['/login']);
    }
}
