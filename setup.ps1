<#
.SYNOPSIS
    Script de instalacion y configuracion de SIGEPRO
.DESCRIPTION
    Verifica/instala dependencias, configura la BD e inicia el proyecto
#>

$ErrorActionPreference = "Continue"
$ROOT = $PSScriptRoot

Write-Host "============================================" -ForegroundColor Cyan
Write-Host "  SIGEPRO - Setup Automatizado" -ForegroundColor Cyan
Write-Host "  Sistema de Gestion de Proyectos" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# ──────────────────────────────────────────
# 1. Verificar herramientas base
# ──────────────────────────────────────────
Write-Host "[1/5] Verificando herramientas base..." -ForegroundColor Yellow

$tools = @{
    "Java 17+" = @{ cmd = "java"; req = "17"; test = { java -version 2>&1 | Select-String "version" } }
    "Node.js 18+" = @{ cmd = "node"; req = "18"; test = { node --version } }
    "NPM" = @{ cmd = "npm"; req = "8"; test = { npm --version } }
    "Git" = @{ cmd = "git"; req = "2"; test = { git --version } }
}

$allOk = $true
foreach ($t in $tools.Keys) {
    $info = $tools[$t]
    $found = Get-Command $info.cmd -ErrorAction SilentlyContinue
    if ($found) {
        Write-Host "  [OK] $t - $(& $info.test)" -ForegroundColor Green
    } else {
        Write-Host "  [FAIL] $t - NO INSTALADO" -ForegroundColor Red
        $allOk = $false
    }
}

if (-not $allOk) {
    Write-Host "`nInstale las herramientas faltantes desde:" -ForegroundColor Yellow
    Write-Host "  Java: https://adoptium.net" -ForegroundColor Cyan
    Write-Host "  Node.js: https://nodejs.org" -ForegroundColor Cyan
    Write-Host "  Git: https://git-scm.com" -ForegroundColor Cyan
    exit 1
}

# ──────────────────────────────────────────
# 2. MySQL
# ──────────────────────────────────────────
Write-Host "`n[2/5] Verificando MySQL..." -ForegroundColor Yellow

$mysqlFound = Get-Command mysql -ErrorAction SilentlyContinue
$mysqlInstalled = $false

if ($mysqlFound) {
    Write-Host "  [OK] MySQL CLI encontrado" -ForegroundColor Green
    $mysqlInstalled = $true
} else {
    Write-Host "  [INFO] MySQL CLI no encontrado en PATH" -ForegroundColor Yellow

    # Buscar instalacion existente
    $mysqlDirs = @(
        "C:\Program Files\MySQL\MySQL Server 8.*",
        "C:\Program Files\MySQL\MySQL Server 9.*",
        "C:\xampp\mysql\bin",
        "$env:ProgramFiles\MySQL\MySQL Server 8.*",
        "${env:ProgramFiles(x86)}\MySQL\MySQL Server 8.*"
    )

    foreach ($dir in $mysqlDirs) {
        $exe = Resolve-Path "$dir\bin\mysql.exe" -ErrorAction SilentlyContinue
        if ($exe) {
            Write-Host "  [OK] MySQL encontrado en: $($exe.Path)" -ForegroundColor Green
            $env:Path += ";$(Split-Path $exe.Path -Parent)"
            $mysqlInstalled = $true
            break
        }
    }
}

if (-not $mysqlInstalled) {
    Write-Host "  [ACCION] Instalando MySQL via winget..." -ForegroundColor Yellow

    # Intentar con winget
    $wingetResult = & winget install "Oracle.MySQL" --accept-package-agreements --accept-source-agreements 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "  [OK] MySQL instalado. Agregando al PATH..." -ForegroundColor Green
        $env:Path += ";C:\Program Files\MySQL\MySQL Server 8.0\bin"
        $mysqlInstalled = $true
    } else {
        Write-Host "  [WARN] No se pudo instalar MySQL automaticamente." -ForegroundColor Red
        Write-Host "  Instale MySQL manualmente desde: https://dev.mysql.com/downloads/installer/" -ForegroundColor Cyan
        Write-Host "  O instale XAMPP (mas simple): https://www.apachefriends.org/" -ForegroundColor Cyan
        Write-Host "`n  Luego ejecute este script nuevamente." -ForegroundColor Yellow
        exit 1
    }
}

# ──────────────────────────────────────────
# 3. Configurar BD
# ──────────────────────────────────────────
Write-Host "`n[3/5] Configurando base de datos..." -ForegroundColor Yellow

$MYSQL_USER = "root"
$MYSQL_PASS = "root"

# Intentar conectar
$mysqlCmd = "mysql -u $MYSQL_USER"
if ($MYSQL_PASS) { $mysqlCmd += " -p$MYSQL_PASS" }

try {
    # Crear BD
    $createDb = "$mysqlCmd -e ""CREATE DATABASE IF NOT EXISTS sigepro;"""
    Invoke-Expression $createDb 2>&1 | Out-Null
    Write-Host "  [OK] Base de datos 'sigepro' creada/verificada" -ForegroundColor Green

    # Cargar schema
    $schemaFile = "$ROOT\sigepro-backend\src\main\resources\schema.sql"
    if (Test-Path $schemaFile) {
        $loadSchema = "$mysqlCmd sigepro < `"$schemaFile`""
        Invoke-Expression $loadSchema 2>&1 | Out-Null
        Write-Host "  [OK] Schema cargado" -ForegroundColor Green
    }

    # Cargar datos de prueba
    $dataFile = "$ROOT\sigepro-backend\src\main\resources\datos_prueba.sql"
    if (Test-Path $dataFile) {
        $loadData = "$mysqlCmd sigepro < `"$dataFile`""
        Invoke-Expression $loadData 2>&1 | Out-Null
        Write-Host "  [OK] Datos de prueba cargados" -ForegroundColor Green
    }
} catch {
    Write-Host "  [WARN] No se pudo conectar a MySQL. Configure manualmente." -ForegroundColor Yellow
    Write-Host "  Error: $_" -ForegroundColor Red
}

# ──────────────────────────────────────────
# 4. Backend - Compilar
# ──────────────────────────────────────────
Write-Host "`n[4/5] Compilando backend..." -ForegroundColor Yellow

$backendDir = "$ROOT\sigepro-backend"
if (Test-Path "$backendDir\pom.xml") {
    Push-Location $backendDir
    $env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-21.0.10.7-hotspot"
    & ".\mvnw.cmd" clean package -q -DskipTests 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "  [OK] Backend compilado correctamente" -ForegroundColor Green
    } else {
        Write-Host "  [FAIL] Error al compilar backend" -ForegroundColor Red
    }
    Pop-Location
}

# ──────────────────────────────────────────
# 5. Frontend - Instalar dependencias
# ──────────────────────────────────────────
Write-Host "`n[5/5] Instalando dependencias del frontend..." -ForegroundColor Yellow

$frontendDir = "$ROOT\sigepro-frontend"
if (Test-Path "$frontendDir\package.json") {
    Push-Location $frontendDir
    npm install 2>&1 | Out-Null
    if ($LASTEXITCODE -eq 0) {
        Write-Host "  [OK] Dependencias del frontend instaladas" -ForegroundColor Green
    } else {
        Write-Host "  [FAIL] Error al instalar dependencias" -ForegroundColor Red
    }
    Pop-Location
}

# ──────────────────────────────────────────
# INSTRUCCIONES FINALES
# ──────────────────────────────────────────
Write-Host ""
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "  INSTALACION COMPLETADA" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Para iniciar el sistema, abra DOS terminales:" -ForegroundColor White
Write-Host ""
Write-Host "TERMINAL 1 - BACKEND (Spring Boot):" -ForegroundColor Green
Write-Host "  cd $ROOT\sigepro-backend" -ForegroundColor Gray
Write-Host '  $env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-21.0.10.7-hotspot"' -ForegroundColor Gray
Write-Host "  .\mvnw.cmd spring-boot:run" -ForegroundColor Gray
Write-Host ""
Write-Host "  Esperar a que aparezca: Started SigeproApplication in X.XX seconds" -ForegroundColor Yellow
Write-Host ""
Write-Host "TERMINAL 2 - FRONTEND (React + Vite):" -ForegroundColor Green
Write-Host "  cd $ROOT\sigepro-frontend" -ForegroundColor Gray
Write-Host "  npm run dev" -ForegroundColor Gray
Write-Host ""
Write-Host "NAVEGADOR:" -ForegroundColor Green
Write-Host "  Abrir: http://localhost:5173" -ForegroundColor Gray
Write-Host "  Email: admin@transandina.com" -ForegroundColor Gray
Write-Host "  Password: 123456" -ForegroundColor Gray
Write-Host ""
Write-Host "Swagger UI (documentacion API):" -ForegroundColor Green
Write-Host "  http://localhost:8080/api/swagger-ui.html" -ForegroundColor Gray
Write-Host ""
