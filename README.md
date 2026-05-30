# LexGuard

Plataforma SaaS multi-tenant de **Compliance e Proteção de Dados (LGPD)**.

| Camada    | Tecnologia                          |
|-----------|-------------------------------------|
| Backend   | Spring Boot 3 · Java 17 · MySQL 8   |
| Frontend  | Angular 16 · SCSS · Bootstrap Icons |
| Auth      | JWT (HS512) · Spring Security       |

---

## Primeira vez no projeto (leia isto antes de rodar comandos)

Depois do `git clone`, você precisa de **três coisas** no seu PC, nesta ordem:

1. **MySQL** rodando, com o banco `lexguard` criado.
2. **Arquivo de segredos locais** (`application-local.properties`) — senha do MySQL, chave JWT e senha do login `superadmin`. Esse arquivo **não vem no Git** (por segurança); o script `setup-local.ps1` cria ele para você.
3. **Backend** (API na porta 8080) e **frontend** (tela na porta 4200) em terminais separados.

### O que é cada arquivo importante?

| Arquivo | Para que serve |
|---------|----------------|
| `scripts/create-database.sql` | Cria só o banco `lexguard` no MySQL. Rode **uma vez** antes de subir o backend. |
| `scripts/setup-local.ps1` (Windows) ou `setup-local.sh` (Linux/macOS) | Pergunta suas senhas e gera `src/main/resources/application-local.properties` com segredos locais. |
| `src/main/resources/application-local.properties` | **Seus segredos** (senha MySQL, JWT, senha do superadmin). Criado pelo script ou copiado do `.example`. **Nunca commite.** |
| `src/main/resources/application.properties` | Config pública (URL do banco, porta, etc.) — já vem no repositório. |
| `application-local.properties.example` | Modelo vazio; use se preferir configurar manualmente em vez do script. |

---

## Guia passo a passo — Windows (PowerShell)

Abra o terminal na **pasta raiz do projeto** (onde está o `pom.xml`), por exemplo `C:\Users\...\lexguard`.

### Passo 0 — Pré-requisitos

Instale e confira:

| Ferramenta | Versão mínima | Como verificar |
|------------|---------------|----------------|
| Java JDK   | 17            | `java -version`  |
| Node.js    | 18+           | `node -version`  |
| npm        | 9+            | `npm -version`   |
| MySQL      | 8.0+          | Serviço MySQL **iniciado** (Workbench ou `mysql` no PATH) |

O projeto traz **Maven embutido** (`mvnw.cmd`); não é obrigatório instalar Maven.

### Passo 1 — Clonar

```powershell
git clone https://github.com/jaasielsilva/lexguard.git
cd lexguard
```

### Passo 2 — Criar o banco no MySQL

O backend espera um banco chamado `lexguard` em `localhost:3306`.

No PowerShell (na raiz do projeto), use **um** dos jeitos abaixo:

```powershell
# Opção A — se o comando mysql está no PATH:
Get-Content .\scripts\create-database.sql | mysql -u root -p

# Opção B — caminho completo do cliente MySQL (ajuste a versão/pasta):
Get-Content .\scripts\create-database.sql | & "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p
```

Digite a senha do usuário `root` quando pedir. Isso executa o SQL que só faz `CREATE DATABASE lexguard`.

> No PowerShell, `mysql ... < arquivo.sql` **não funciona** como no Linux; use `Get-Content` como acima ou cole o SQL no MySQL Workbench.

### Passo 3 — Criar o arquivo de segredos (obrigatório)

```powershell
.\scripts\setup-local.ps1
```

O script vai pedir:

| Pergunta | O que colocar |
|----------|----------------|
| Senha do MySQL | A mesma do usuário `root` que você usa no passo 2 |
| Senha do super admin | Senha do login inicial na aplicação (usuário `superadmin`). Enter = padrão `ChangeMe@2025!` |

Resultado: arquivo `src\main\resources\application-local.properties` com três linhas (senha MySQL, chave JWT gerada automaticamente, senha do admin).

Se o arquivo **já existir**, o script pergunta se quer sobrescrever — digite `s` e Enter para sim, ou só Enter para não.

### Passo 4 — Subir o backend

Ainda na raiz do projeto:

```powershell
.\mvnw.cmd spring-boot:run
```

Aguarde até ver a aplicação rodando. API: **http://localhost:8080/api**

Na primeira vez, o sistema cria tabelas, empresa padrão e usuário `superadmin`.

### Passo 5 — Subir o frontend (outro terminal)

```powershell
cd frontend
npm install
npm start
```

Interface: **http://localhost:4200**

### Passo 6 — Entrar no sistema

- **URL:** http://localhost:4200  
- **Usuário:** `superadmin`  
- **Senha:** a que você definiu no passo 3 (`app.superadmin.password`)

---

## Guia rápido — Linux / macOS

```bash
git clone https://github.com/jaasielsilva/lexguard.git
cd lexguard

mysql -u root -p < scripts/create-database.sql

chmod +x scripts/setup-local.sh
./scripts/setup-local.sh

./mvnw spring-boot:run
```

Em outro terminal: `cd frontend && npm install && npm start`

---

## Pré-requisitos (referência)

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
| `s/N` não é reconhecido ao rodar `setup-local.ps1` | Bug em versões antigas do script (PowerShell interpretava `(s/N)` como comando) | Atualize o repositório (`git pull`); o script atual já está corrigido |
| `key byte array is 0 bits` / JWT HMAC no login | `jwt.secret` vazio em `application-local.properties` (openssl no Windows sem saída) | Rode `.\scripts\repair-jwt-secret.ps1`, reinicie o backend e tente login de novo |
| `Could not resolve placeholder 'jwt.secret'` | `application-local.properties` não existe ou está vazio | Rode `.\scripts\setup-local.ps1` ou copie do `.example` e preencha |
| Erro de conexão MySQL | Banco não criado, MySQL parado ou senha errada no arquivo local | Crie o banco (passo 2), confira o serviço MySQL e `spring.datasource.password` |
| `mysql` não encontrado no PowerShell | Cliente MySQL fora do PATH | Use caminho completo do `mysql.exe` ou MySQL Workbench |
| CORS / login falha | Backend não está rodando | Inicie `.\mvnw.cmd spring-boot:run` antes do frontend |
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
