# LexGuard

Plataforma SaaS multi-tenant de **Compliance e Proteção de Dados (LGPD)**.

| Camada    | Tecnologia                          |
|-----------|-------------------------------------|
| Backend   | Spring Boot 3 · Java 17 · MySQL 8   |
| Frontend  | Angular 16 · SCSS · Bootstrap Icons |
| Auth      | JWT (HS512) · Spring Security       |

---

## Pré-requisitos

Instale antes de começar:

| Ferramenta | Versão mínima | Verificar        |
|------------|---------------|------------------|
| Java JDK   | 17            | `java -version`  |
| Maven      | 3.8+ (ou use `./mvnw`) | `mvn -version` |
| Node.js    | 18+           | `node -version`  |
| npm        | 9+            | `npm -version`   |
| MySQL      | 8.0+          | servidor rodando |

---

## 1. Clonar o repositório

```bash
git clone https://github.com/jaasielsilva/lexguard.git
cd lexguard
```

**Setup rápido (após clonar):**
```powershell
# Windows
.\scripts\setup-local.ps1
mysql -u root -p < scripts\create-database.sql
.\mvnw.cmd spring-boot:run
```
```bash
# Linux / macOS
./scripts/setup-local.sh
mysql -u root -p < scripts/create-database.sql
./mvnw spring-boot:run
```

---

## 2. Banco de dados MySQL

Crie o banco de dados (ajuste usuário/senha conforme seu ambiente):

```sql
CREATE DATABASE lexguard CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

O Hibernate cria/atualiza as tabelas automaticamente (`ddl-auto=update`) na primeira execução.

---

## 3. Configurar segredos (obrigatório)

As credenciais **não ficam no repositório**. Escolha uma das opções:

### Opção A — Script automático (recomendado)

**Windows (PowerShell):**
```powershell
.\scripts\setup-local.ps1
```

**Linux / macOS:**
```bash
chmod +x scripts/setup-local.sh
./scripts/setup-local.sh
```

O script irá:
- Gerar automaticamente uma chave JWT segura
- Solicitar a senha do MySQL e do super admin
- Criar `src/main/resources/application-local.properties` (gitignored)

### Opção B — Manual

```bash
cp src/main/resources/application-local.properties.example src/main/resources/application-local.properties
```

Edite `application-local.properties` e preencha:

| Propriedade | Descrição |
|-------------|-----------|
| `spring.datasource.password` | Senha do MySQL |
| `jwt.secret` | Chave JWT em Base64 (mín. 64 bytes) |
| `app.superadmin.password` | Senha do usuário admin inicial |

Gere uma chave JWT segura:

```bash
openssl rand -base64 64
```

### Opção C — Variáveis de ambiente

Defina as variáveis (veja `.env.example`):

```bash
export SPRING_DATASOURCE_PASSWORD=sua_senha_mysql
export JWT_SECRET=sua_chave_base64
export APP_SUPERADMIN_PASSWORD=SuaSenha@2025!
```

> **Importante:** `application-local.properties` e `.env` estão no `.gitignore`. Nunca commite segredos.

---

## 4. Subir o backend

Na raiz do projeto:

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

API disponível em: **http://localhost:8080/api**

Na primeira inicialização o sistema cria automaticamente:

- Empresa raiz **LexGuard**
- Role **SUPER_ADMIN** com todas as permissões
- Usuário **superadmin** (senha definida em `application-local.properties`)

---

## 5. Subir o frontend

Em outro terminal:

```bash
cd frontend
npm install
npm start
```

Interface disponível em: **http://localhost:4200**

---

## 6. Acessar o sistema

1. Abra http://localhost:4200
2. Faça login com:
   - **Usuário:** `superadmin`
   - **Senha:** a definida em `app.superadmin.password`

---

## Estrutura do projeto

```
lexguard/
├── src/main/java/          # Backend Spring Boot
│   └── com/jaasielsilva/lexguard/
│       ├── controller/     # REST API
│       ├── service/        # Regras de negócio
│       ├── model/          # Entidades JPA
│       ├── security/       # JWT + Spring Security
│       └── tenant/         # Multi-tenant (X-Empresa-Id)
├── src/main/resources/
│   ├── application.properties              # Config pública
│   └── application-local.properties.example # Template de segredos
├── frontend/               # Angular 16
│   └── src/app/
│       ├── auth/           # Login + guards
│       ├── dashboard/      # Shell + métricas
│       └── modules/        # Módulos LGPD (8 telas)
├── pom.xml
├── scripts/
│   ├── setup-local.ps1     # Setup credenciais (Windows)
│   ├── setup-local.sh      # Setup credenciais (Linux/macOS)
│   └── create-database.sql # SQL do banco local
└── README.md
```

---

## Módulos da aplicação

| Módulo          | Rota frontend              | Permissão      |
|-----------------|----------------------------|----------------|
| Dashboard       | `/dashboard`               | autenticado    |
| Titulares       | `/dashboard/titulares`     | DATA_READ/WRITE |
| Dados Pessoais  | `/dashboard/dados-pessoais`| DATA_READ/WRITE |
| Consentimentos  | `/dashboard/consentimentos`| CONSENT_MANAGE |
| Solicitações    | `/dashboard/solicitacoes`  | REQUEST_MANAGE |
| Relatórios      | `/dashboard/relatorios`    | REPORT_READ    |
| Auditoria       | `/dashboard/auditoria`     | AUDIT_READ     |
| Usuários        | `/dashboard/usuarios`      | USER_MANAGE    |

---

## Headers HTTP (rotas protegidas)

```
Authorization: Bearer <accessToken>
X-Empresa-Id: <empresaId>
X-Usuario: <username>        ← apenas em POST/PUT/DELETE
```

---

## Scripts úteis

### Backend

```bash
./mvnw test                  # Rodar testes
./mvnw spring-boot:run       # Desenvolvimento
./mvnw clean package        # Build JAR
```

### Frontend

```bash
npm start                    # Dev server (porta 4200)
npm run build                # Build produção
npm test                     # Testes unitários
```

---

## Solução de problemas

| Problema | Possível causa | Solução |
|----------|----------------|---------|
| `Could not resolve placeholder 'jwt.secret'` | Segredos não configurados | Copie e preencha `application-local.properties` |
| Erro de conexão MySQL | Banco não criado ou senha errada | Verifique MySQL e `spring.datasource.password` |
| CORS / login falha | Backend não está rodando | Inicie o backend na porta 8080 |
| `401` após login | Token expirado | Faça logout e login novamente |
| Frontend não conecta | URL da API | Padrão: `http://localhost:8080/api` em `frontend/src/app/core/services/api.service.ts` |

---

## Segurança em produção

- Altere a senha do `superadmin` após o primeiro acesso
- Use segredos fortes e únicos para `JWT_SECRET`
- Não exponha `application-local.properties` nem `.env`
- Configure HTTPS e variáveis de ambiente no servidor
- Desative `spring.jpa.show-sql` em produção

---

## Licença

Projeto educacional / portfolio — consulte o autor para uso comercial.
