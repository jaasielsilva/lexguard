# LexGuard — Arquitetura e Documentação Técnica

## Visão Geral
Plataforma SaaS multi-tenant de Compliance e Proteção de Dados (LGPD).
- **Backend**: Spring Boot 3.x · Java 21 · MySQL 8 · JWT (HS512)
- **Frontend**: Angular 16 · Bootstrap Icons · SCSS

---

## Segurança

### Fluxo de Autenticação
1. `POST /api/auth/login` → backend resolve `empresaId` pelo `username` → retorna `accessToken` + `refreshToken`
2. Todas as rotas protegidas exigem `Authorization: Bearer <token>` + `X-Empresa-Id: <id>`
3. `TenantFilter` lê o header e seta `TenantContext` (ThreadLocal)
4. `JwtAuthenticationFilter` valida o token e popula o `SecurityContext`
5. `@PreAuthorize` verifica a role/permissão no método

### Roles e Permissões
| Role | Permissões |
|------|-----------|
| SUPER_ADMIN | Todas (DATA_READ, DATA_WRITE, CONSENT_MANAGE, AUDIT_READ, REQUEST_MANAGE, REPORT_READ, USER_MANAGE, TENANT_ADMIN) |
| ADMIN | DATA_READ, DATA_WRITE, CONSENT_MANAGE, REQUEST_MANAGE, REPORT_READ, USER_MANAGE |
| ANALYST | DATA_READ, CONSENT_MANAGE, REQUEST_MANAGE, REPORT_READ |
| VIEWER | DATA_READ, REPORT_READ |

### Mapeamento @PreAuthorize por Endpoint
| Controller | Método | Permissão |
|-----------|--------|-----------|
| DashboardController | GET / | autenticado |
| TitularController | GET | DATA_READ |
| TitularController | POST/PUT/DELETE | DATA_WRITE |
| DadoPessoalController | GET | DATA_READ |
| DadoPessoalController | POST | DATA_WRITE |
| ConsentimentoController | GET/POST/revoke | CONSENT_MANAGE |
| SolicitacaoController | GET/POST/respond | REQUEST_MANAGE |
| RelatorioController | GET/POST | REPORT_READ |
| AuditLogController | GET | AUDIT_READ |
| UsuarioController | GET/POST | USER_MANAGE |

---

## 8 Módulos

| # | Módulo | Rota Frontend | Rota Backend | Permissão |
|---|--------|--------------|-------------|-----------|
| 1 | Dashboard | /dashboard | GET /api/dashboard | autenticado |
| 2 | Titulares | /dashboard/titulares | /api/titulares | DATA_READ / DATA_WRITE |
| 3 | Dados Pessoais | /dashboard/dados-pessoais | /api/dados | DATA_READ / DATA_WRITE |
| 4 | Consentimentos | /dashboard/consentimentos | /api/consentimentos | CONSENT_MANAGE |
| 5 | Solicitações | /dashboard/solicitacoes | /api/solicitacoes | REQUEST_MANAGE |
| 6 | Relatórios | /dashboard/relatorios | /api/relatorios | REPORT_READ |
| 7 | Auditoria | /dashboard/auditoria | /api/audit | AUDIT_READ |
| 8 | Usuários | /dashboard/usuarios | /api/users | USER_MANAGE |

---

## Padrão de Headers HTTP (rotas protegidas)
```
Authorization: Bearer <accessToken>
X-Empresa-Id: <empresaId>
X-Usuario: <username>   ← apenas em POST/PUT/DELETE
```

---

## Enums do Domínio
- **DataClassification**: PESSOAL, SENSIVEL
- **LegalBasis**: CONSENTIMENTO, OBRIGACAO_LEGAL, EXECUCAO_CONTRATO, INTERESSE_LEGITIMO, PROTECAO_VIDA, SERVICO_PUBLICO, EXERCICIO_DIREITO
- **RequestStatus**: PENDENTE, EM_PROCESSAMENTO, ATENDIDO, RECUSADO
- **RightRequestType**: (ver model)
- **AuditLogAction**: LOGIN, LOGOUT, CREATE_TITULAR, UPDATE_TITULAR, DELETE_TITULAR, REGISTER_CONSENT, REVOKE_CONSENT, DATA_ACCESS, DATA_EXPORT, REQUEST_SUBMITTED, REQUEST_HANDLED, REPORT_GENERATED
- **ReportType**: (ver model)
- **RiskLevel**: (ver model)

---

## Padrão Frontend (Angular)
- Cada módulo tem: `models/`, `services/`, `pages/list/`, `pages/form/`
- `ApiService` base injeta automaticamente `Authorization` e `X-Empresa-Id`
- `AuthGuard` protege todas as rotas filhas de `/dashboard`
- Lazy loading em todos os módulos
