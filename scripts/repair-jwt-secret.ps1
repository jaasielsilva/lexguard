# Repara jwt.secret vazio em application-local.properties (sem alterar outras senhas).
$ErrorActionPreference = "Stop"

$RootDir = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
$LocalFile = Join-Path $RootDir "src\main\resources\application-local.properties"

if (-not (Test-Path $LocalFile)) {
    Write-Host "Arquivo nao encontrado. Rode primeiro: .\scripts\setup-local.ps1" -ForegroundColor Red
    exit 1
}

$bytes = New-Object byte[] 64
[System.Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($bytes)
$jwtSecret = [Convert]::ToBase64String($bytes)

$lines = Get-Content -Path $LocalFile -Encoding UTF8
$found = $false
$out = foreach ($line in $lines) {
    if ($line -match '^\s*jwt\.secret\s*=') {
        $found = $true
        "jwt.secret=$jwtSecret"
    } else {
        $line
    }
}
if (-not $found) {
    $out = @($out) + "jwt.secret=$jwtSecret"
}

Set-Content -Path $LocalFile -Value $out -Encoding UTF8
Write-Host "OK jwt.secret atualizado em application-local.properties" -ForegroundColor Green
Write-Host "Reinicie o backend (Ctrl+C e .\mvnw.cmd spring-boot:run) e tente o login de novo."
