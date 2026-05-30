# LexGuard — configura credenciais locais (Windows PowerShell)
$ErrorActionPreference = "Stop"

$RootDir = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
$ResourcesDir = Join-Path $RootDir "src\main\resources"
$LocalFile = Join-Path $ResourcesDir "application-local.properties"

Write-Host ""
Write-Host "========================================"
Write-Host "  LexGuard — Setup local"
Write-Host "========================================"
Write-Host ""

if (Test-Path $LocalFile) {
    $overwrite = Read-Host "application-local.properties ja existe. Sobrescrever? (s/N)"
    if ($overwrite -notmatch "^[sS]$") {
        Write-Host "Cancelado. Nenhuma alteracao feita."
        exit 0
    }
}

# ── JWT secret ────────────────────────────────────────────────────────────────
function New-JwtSecret {
    if (Get-Command openssl -ErrorAction SilentlyContinue) {
        return (openssl rand -base64 64).Trim()
    }
    $bytes = New-Object byte[] 64
    [System.Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($bytes)
    return [Convert]::ToBase64String($bytes)
}

$jwtSecret = New-JwtSecret
Write-Host "OK Chave JWT gerada automaticamente" -ForegroundColor Green

# ── Senha MySQL ───────────────────────────────────────────────────────────────
$dbPassword = Read-Host "Senha do MySQL (usuario root)" -AsSecureString
$dbPasswordPlain = [Runtime.InteropServices.Marshal]::PtrToStringAuto(
    [Runtime.InteropServices.Marshal]::SecureStringToBSTR($dbPassword)
)
if ([string]::IsNullOrWhiteSpace($dbPasswordPlain)) {
    Write-Host "Erro: senha do MySQL nao pode ser vazia." -ForegroundColor Red
    exit 1
}

# ── Senha super admin ─────────────────────────────────────────────────────────
$adminInput = Read-Host "Senha do super admin [padrao: ChangeMe@2025!]" -AsSecureString
$adminPlain = [Runtime.InteropServices.Marshal]::PtrToStringAuto(
    [Runtime.InteropServices.Marshal]::SecureStringToBSTR($adminInput)
)
if ([string]::IsNullOrWhiteSpace($adminPlain)) {
    $adminPlain = "ChangeMe@2025!"
}

# ── Gravar arquivo ────────────────────────────────────────────────────────────
$content = @"
# Gerado por scripts/setup-local.ps1 — NAO commitar
spring.datasource.password=$dbPasswordPlain
jwt.secret=$jwtSecret
app.superadmin.password=$adminPlain
"@

Set-Content -Path $LocalFile -Value $content -Encoding UTF8 -NoNewline
Add-Content -Path $LocalFile -Value "" -Encoding UTF8

Write-Host ""
Write-Host "OK Arquivo criado: src\main\resources\application-local.properties" -ForegroundColor Green
Write-Host ""
Write-Host "Proximos passos:"
Write-Host ""
Write-Host "  1. Criar banco MySQL:"
Write-Host "     mysql -u root -p < scripts\create-database.sql"
Write-Host ""
Write-Host "  2. Subir backend:"
Write-Host "     .\mvnw.cmd spring-boot:run"
Write-Host ""
Write-Host "  3. Subir frontend (outro terminal):"
Write-Host "     cd frontend; npm install; npm start"
Write-Host ""
Write-Host "  4. Acessar http://localhost:4200"
Write-Host "     Login: superadmin / (senha definida acima)"
Write-Host ""
