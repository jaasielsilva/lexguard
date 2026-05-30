#!/usr/bin/env bash
# LexGuard — configura credenciais locais (Linux / macOS)
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
RESOURCES_DIR="$ROOT_DIR/src/main/resources"
LOCAL_FILE="$RESOURCES_DIR/application-local.properties"
EXAMPLE_FILE="$RESOURCES_DIR/application-local.properties.example"

echo ""
echo "========================================"
echo "  LexGuard — Setup local"
echo "========================================"
echo ""

if [[ -f "$LOCAL_FILE" ]]; then
  read -r -p "application-local.properties já existe. Sobrescrever? (s/N): " OVERWRITE
  if [[ ! "$OVERWRITE" =~ ^[sS]$ ]]; then
    echo "Cancelado. Nenhuma alteração feita."
    exit 0
  fi
fi

# ── JWT secret ────────────────────────────────────────────────────────────────
if command -v openssl &>/dev/null; then
  JWT_SECRET="$(openssl rand -base64 64 | tr -d '\n')"
else
  JWT_SECRET="$(head -c 64 /dev/urandom | base64 | tr -d '\n')"
fi
echo "✓ Chave JWT gerada automaticamente"

# ── Senha MySQL ───────────────────────────────────────────────────────────────
read -r -s -p "Senha do MySQL (usuário root): " DB_PASSWORD
echo ""
if [[ -z "$DB_PASSWORD" ]]; then
  echo "Erro: senha do MySQL não pode ser vazia."
  exit 1
fi

# ── Senha super admin ─────────────────────────────────────────────────────────
read -r -s -p "Senha do super admin [padrão: ChangeMe@2025!]: " ADMIN_PASSWORD
echo ""
ADMIN_PASSWORD="${ADMIN_PASSWORD:-ChangeMe@2025!}"

# ── Gravar arquivo ────────────────────────────────────────────────────────────
cat > "$LOCAL_FILE" <<EOF
# Gerado por scripts/setup-local.sh — NÃO commitar
spring.datasource.password=${DB_PASSWORD}
jwt.secret=${JWT_SECRET}
app.superadmin.password=${ADMIN_PASSWORD}
EOF

echo ""
echo "✓ Arquivo criado: src/main/resources/application-local.properties"
echo ""
echo "Próximos passos:"
echo ""
echo "  1. Criar banco MySQL:"
echo "     mysql -u root -p < scripts/create-database.sql"
echo ""
echo "  2. Subir backend:"
echo "     ./mvnw spring-boot:run"
echo ""
echo "  3. Subir frontend (outro terminal):"
echo "     cd frontend && npm install && npm start"
echo ""
echo "  4. Acessar http://localhost:4200"
echo "     Login: superadmin / (senha definida acima)"
echo ""
