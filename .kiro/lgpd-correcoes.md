# Plano de Correção LGPD — LexGuard

**Gerado em:** Maio/2026  
**Score atual:** 59/100 — ATENÇÃO  
**Objetivo:** ≥ 80/100 — BAIXO RISCO

---

## Fase 1 — Emergencial (1–2 dias)

### CRÍTICO-3 · `ConsentimentoService.listByTitular()` faz `findAll()` cross-tenant

- [ ] Criar query no `ConsentimentoRepository`:
  ```java
  @Query("SELECT c FROM Consentimento c WHERE c.empresaId = :empresaId AND c.titular.id = :titularId")
  Set<Consentimento> findByEmpresaIdAndTitularId(@Param("empresaId") Long empresaId, @Param("titularId") Long titularId);
  ```
- [ ] Substituir o `findAll().stream().filter(...)` em `ConsentimentoService.listByTitular()` pela nova query

### CRÍTICO-4 · `DadoPessoalService.listByTitular()` faz `findAll()` cross-tenant

- [ ] Criar query no `DadoPessoalRepository`:
  ```java
  @Query("SELECT d FROM DadoPessoal d WHERE d.empresaId = :empresaId AND d.titular.id = :titularId AND d.ativo = true")
  Set<DadoPessoal> findByEmpresaIdAndTitularIdAndAtivoTrue(@Param("empresaId") Long empresaId, @Param("titularId") Long titularId);
  ```
- [ ] Substituir o `findAll().stream().filter(...)` em `DadoPessoalService.listByTitular()` pela nova query

### CRÍTICO-5 · `/actuator/**` exposto sem autenticação

- [ ] Alterar `SecurityConfig.java`:
  ```java
  // Antes
  .requestMatchers("/actuator/**").permitAll()

  // Depois
  .requestMatchers("/actuator/health").permitAll()
  .requestMatchers("/actuator/**").hasRole("SUPER_ADMIN")
  ```

### CRÍTICO-6 · `show-sql=true` em produção vaza dados pessoais nos logs

- [ ] Remover de `application.properties`:
  ```properties
  spring.jpa.show-sql=true
  spring.jpa.properties.hibernate.format_sql=true
  ```
- [ ] Criar `application-dev.properties` e mover as duas linhas para lá
- [ ] Confirmar que o profile `dev` não está ativo em produção

---

## Fase 2 — Curto prazo (1 semana)

### CRÍTICO-1 · CPF exibido sem mascaramento no frontend

- [ ] Criar `CpfMaskPipe` em `frontend/src/app/core/pipes/cpf-mask.pipe.ts`:
  ```typescript
  @Pipe({ name: 'cpfMask' })
  export class CpfMaskPipe implements PipeTransform {
    transform(cpf: string): string {
      if (!cpf) return '';
      const digits = cpf.replace(/\D/g, '');
      if (digits.length !== 11) return '***.***.***-**';
      return `***.${digits.slice(3, 6)}.${digits.slice(6, 9)}-**`;
    }
  }
  ```
- [ ] Declarar o pipe no `CoreModule` (ou `SharedModule`)
- [ ] Aplicar em `titulares-list.component.html`:
  ```html
  <td class="mono">{{ t.cpf | cpfMask }}</td>
  ```
- [ ] Verificar se CPF aparece em outros templates (busca, modais, solicitações) e aplicar o pipe

### CRÍTICO-2 · `dados_pessoais.valor` exibido sem mascaramento

- [ ] Alterar `dados-pessoais-list.component.html` — substituir exibição direta por toggle:
  ```html
  <td>
    <span *ngIf="!d._revelado">{{ d.valor | slice:0:3 }}***</span>
    <span *ngIf="d._revelado">{{ d.valor }}</span>
    <button type="button" class="btn-icon" (click)="revelarValor(d)" *ngIf="!d._revelado" title="Revelar valor">
      <i class="bi bi-eye"></i>
    </button>
  </td>
  ```
- [ ] Adicionar `_revelado: boolean` ao modelo local no componente
- [ ] No método `revelarValor(d)`, chamar o serviço para registrar `DATA_ACCESS` no audit log
- [ ] Adicionar endpoint `POST /api/dados/{id}/acesso` no backend para registrar o acesso explícito

### Rate limiting no login

- [ ] Adicionar dependência `bucket4j-spring-boot-starter` no `pom.xml`
- [ ] Configurar limite de 5 tentativas por IP em 1 minuto no `AuthController`
- [ ] Retornar HTTP 429 com mensagem genérica ao exceder o limite

---

## Fase 3 — Médio prazo (2–4 semanas)

### Migrar tokens para HttpOnly cookies

- [ ] Backend — alterar `AuthController` para retornar `accessToken` via `Set-Cookie: HttpOnly; Secure; SameSite=Strict`
- [ ] Backend — criar endpoint `POST /api/auth/refresh` que lê o cookie (não o body)
- [ ] Frontend — remover `localStorage.setItem('accessToken', ...)` do `auth.service.ts`
- [ ] Frontend — remover `Authorization: Bearer` do `api.service.ts` (o cookie é enviado automaticamente)
- [ ] Frontend — manter apenas `empresaId` e `username` no `localStorage` (não são segredos)
- [ ] Testar CORS com `allowCredentials: true` (já configurado no `SecurityConfig`)

### Auditoria nos GETs de dados sensíveis

- [ ] `TitularController.listTitulares()` — chamar `auditService.logAction(..., DATA_ACCESS, ...)`
- [ ] `TitularController.getTitular()` — idem
- [ ] `DadoPessoalController.listDados()` — já chama `auditService.logDataAccess()` ✅ — verificar se está registrando corretamente
- [ ] Criar constante de finalidade padrão para evitar strings soltas nos services

### Índices no banco de dados

- [ ] Criar migration (Flyway) com os índices:
  ```sql
  CREATE INDEX idx_titulares_empresa_cpf    ON titulares(empresa_id, cpf);
  CREATE INDEX idx_titulares_empresa_ativo  ON titulares(empresa_id, ativo, soft_deleted);
  CREATE INDEX idx_audit_empresa_ts         ON audit_logs(empresa_id, timestamp DESC);
  CREATE INDEX idx_dados_empresa_titular    ON dados_pessoais(empresa_id, titular_id, ativo);
  CREATE INDEX idx_consent_empresa_titular  ON consentimentos(empresa_id, titular_id, ativo);
  ```

### Migrar para Flyway

- [ ] Adicionar `flyway-core` ao `pom.xml`
- [ ] Alterar `ddl-auto` de `update` para `validate`
- [ ] Criar `src/main/resources/db/migration/V1__baseline.sql` com o schema atual
- [ ] Testar migração em ambiente de desenvolvimento antes de aplicar em produção

---

## Fase 4 — Estrutural (1–2 meses)

### Criptografia em repouso para `dados_pessoais.valor` (classificação SENSÍVEL)

- [ ] Avaliar estratégia: criptografia na camada de aplicação (AES-256 via `javax.crypto`) ou colunas criptografadas no MySQL
- [ ] Criar `EncryptionService` no backend com `encrypt(String)` / `decrypt(String)`
- [ ] Alterar `DadoPessoal.valor` para armazenar o valor criptografado quando `classificacao = SENSIVEL`
- [ ] Alterar `DadoPessoalService.create()` para criptografar antes de salvar
- [ ] Alterar `DadoPessoalService.mapToResponse()` para descriptografar antes de retornar
- [ ] Garantir que a chave de criptografia **não** fique em `application.properties` — usar variável de ambiente ou AWS Secrets Manager
- [ ] Criar migration para re-criptografar dados existentes

### Política de retenção de logs

- [ ] Definir prazo de retenção (recomendado: 5 anos para fins de responsabilização — art. 37 LGPD)
- [ ] Criar `@Scheduled` no Spring para arquivar/deletar `audit_logs` com `timestamp < NOW() - 5 years`
- [ ] Criar tabela `audit_logs_archive` para registros arquivados (não deletar diretamente)
- [ ] Documentar a política de retenção no DPA (Data Processing Agreement) da empresa

### Testes de penetração — foco em cross-tenant isolation

- [ ] Testar se um token de `empresa_id=1` consegue acessar dados de `empresa_id=2` via manipulação do header `X-Empresa-Id`
- [ ] Testar se o `TenantFilter` rejeita corretamente headers inválidos ou ausentes
- [ ] Testar os endpoints de busca (`/api/titulares/search`) com `empresaId` de outro tenant
- [ ] Verificar se o `@Filter(name = "tenantFilter")` do Hibernate está ativo em todas as queries (requer `Session.enableFilter()`)
- [ ] Contratar pentest externo ou usar OWASP ZAP automatizado no pipeline CI/CD

---

## Checklist de Conformidade LGPD (resumo)

| Requisito LGPD | Status | Fase |
|----------------|--------|------|
| Base legal registrada para cada dado | ✅ Implementado | — |
| Consentimento com versão de termo | ✅ Implementado | — |
| Direito de acesso do titular | ✅ Solicitações implementadas | — |
| Direito de correção | ✅ PUT /titulares/{id} | — |
| Direito de exclusão (anonimização) | ⚠️ Soft delete apenas | Fase 4 |
| Direito de portabilidade | ⚠️ Sem exportação implementada | Fase 3 |
| Registro de atividades de tratamento | ⚠️ Parcial (GET sem log) | Fase 3 |
| Segurança dos dados (art. 46) | ⚠️ findAll() cross-tenant | Fase 1 |
| Minimização de dados no frontend | ❌ CPF/valor expostos | Fase 2 |
| Criptografia de dados sensíveis | ❌ Não implementada | Fase 4 |
| Política de retenção | ❌ Não definida | Fase 4 |
| Notificação de incidentes (art. 48) | ❌ Sem mecanismo | Backlog |
