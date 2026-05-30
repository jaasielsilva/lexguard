# LexGuard - configura credenciais locais (Windows PowerShell)
# Use apenas caracteres ASCII neste arquivo (compativel com Windows PowerShell 5.1).
$ErrorActionPreference = "Stop"

$RootDir = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
$ResourcesDir = Join-Path $RootDir "src\main\resources"
$LocalFile = Join-Path $ResourcesDir "application-local.properties"

function New-JwtSecret {
    # Preferir .NET no Windows: openssl no PATH as vezes existe mas nao gera saida.
    $bytes = New-Object byte[] 64
    [System.Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($bytes)
    return [Convert]::ToBase64String($bytes)
}

function Test-JwtSecretValue {
    param([string]$Secret)
    if ([string]::IsNullOrWhiteSpace($Secret)) {
        return $false
    }
    try {
        $decoded = [Convert]::FromBase64String($Secret.Trim())
        return $decoded.Length -ge 32
    } catch {
        return $false
    }
}

Write-Host ""
Write-Host "========================================"
Write-Host "  LexGuard - Setup local"
Write-Host "========================================"
Write-Host ""

if (Test-Path $LocalFile) {
    $overwrite = Read-Host 'application-local.properties ja existe. Sobrescrever? (s/N)'
    if ($overwrite -notmatch '^[sS]$') {
        Write-Host "Cancelado. Nenhuma alteracao feita."
        exit 0
    }
}

$jwtSecret = New-JwtSecret
if (-not (Test-JwtSecretValue -Secret $jwtSecret)) {
    Write-Host "Erro: nao foi possivel gerar uma chave JWT valida." -ForegroundColor Red
    exit 1
}
Write-Host "OK Chave JWT gerada automaticamente" -ForegroundColor Green

$dbPassword = Read-Host "Senha do MySQL (usuario root)" -AsSecureString
$dbPasswordPlain = [Runtime.InteropServices.Marshal]::PtrToStringAuto(
    [Runtime.InteropServices.Marshal]::SecureStringToBSTR($dbPassword)
)
if ([string]::IsNullOrWhiteSpace($dbPasswordPlain)) {
    Write-Host "Erro: senha do MySQL nao pode ser vazia." -ForegroundColor Red
    exit 1
}

$adminInput = Read-Host "Senha do super admin [padrao: ChangeMe@2025!]" -AsSecureString
$adminPlain = [Runtime.InteropServices.Marshal]::PtrToStringAuto(
    [Runtime.InteropServices.Marshal]::SecureStringToBSTR($adminInput)
)
if ([string]::IsNullOrWhiteSpace($adminPlain)) {
    $adminPlain = "ChangeMe@2025!"
}

$lines = @(
    "# Gerado por scripts/setup-local.ps1 - NAO commitar"
    "spring.datasource.password=$dbPasswordPlain"
    "jwt.secret=$jwtSecret"
    "app.superadmin.password=$adminPlain"
    ""
)
Set-Content -Path $LocalFile -Value $lines -Encoding UTF8

Write-Host ""
Write-Host "OK Arquivo criado: src\main\resources\application-local.properties" -ForegroundColor Green
Write-Host ""
Write-Host "Proximos passos:"
Write-Host ""
Write-Host "  1. Criar banco MySQL:"
Write-Host "     Get-Content .\scripts\create-database.sql | mysql -u root -p"
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
