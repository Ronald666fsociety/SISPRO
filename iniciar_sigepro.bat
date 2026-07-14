@echo off
title SIGEPRO - Sistema de Gestion de Proyectos
cd /d "%~dp0"

color 0B
echo ============================================
echo   SIGEPRO - Inicio Rapido
echo ============================================
echo.

:: Verificar Java
where java >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Java no encontrado. Instale JDK 17+ desde:
    echo         https://adoptium.net
    pause
    exit /b 1
)
for /f "tokens=3" %%i in ('java -version 2^>^&1 ^| findstr /i "version"') do set JAVAVER=%%i
echo [OK] Java %JAVAVER%

:: Verificar Node
where node >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Node.js no encontrado. Instale desde:
    echo         https://nodejs.org
    pause
    exit /b 1
)
echo [OK] Node.js
where npm >nul 2>&1 && echo [OK] NPM

:: Verificar MySQL
where mysql >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] MySQL CLI encontrado
) else (
    echo [INFO] MySQL CLI no encontrado en PATH
    echo        Asegurese de que MySQL este corriendo en localhost:3306
    echo.
)

echo.
echo ============================================
echo  Elija una opcion:
echo ============================================
echo  1. INICIAR TODO (Backend + Frontend)
echo  2. Solo configurar BD
echo  3. Salir
echo ============================================
set /p opcion="Opcion: "

if "%opcion%"=="1" goto iniciar
if "%opcion%"=="2" goto bd
if "%opcion%"=="3" exit /b 0

:bd
echo.
echo Configurando base de datos...
echo.

:: Preguntar credenciales MySQL
set /p mysqluser="Usuario MySQL [root]: "
if "%mysqluser%"=="" set mysqluser=root
set /p mysqlpass="Password MySQL []: "

:: Crear BD
mysql -u %mysqluser% -p%mysqlpass% -e "CREATE DATABASE IF NOT EXISTS sigepro;" 2>nul
if %errorlevel% neq 0 (
    echo [ERROR] No se pudo conectar a MySQL
    pause
    exit /b 1
)
echo [OK] Base de datos creada

:: Cargar schema
if exist "sigepro-backend\src\main\resources\schema.sql" (
    mysql -u %mysqluser% -p%mysqlpass% sigepro < "sigepro-backend\src\main\resources\schema.sql"
    echo [OK] Schema cargado
)

:: Cargar datos de prueba
if exist "sigepro-backend\src\main\resources\datos_prueba.sql" (
    mysql -u %mysqluser% -p%mysqlpass% sigepro < "sigepro-backend\src\main\resources\datos_prueba.sql"
    echo [OK] Datos de prueba cargados
)

echo.
if "%opcion%"=="2" (
    echo [OK] Base de datos configurada
    pause
    exit /b 0
)

:iniciar
echo.
echo ============================================
echo  INICIANDO SISTEMA
echo ============================================
echo.

:: Compilar Backend
echo [1/3] Compilando backend...
cd sigepro-backend
set JAVA_HOME=C:\Program Files\Microsoft\jdk-21.0.10.7-hotspot
call .\mvnw.cmd package -DskipTests -q
if %errorlevel% neq 0 (
    echo [ERROR] Error al compilar backend
    pause
    exit /b 1
)
echo [OK] Backend compilado
cd ..

:: Instalar Frontend
echo [2/3] Instalando dependencias del frontend...
cd sigepro-frontend
call npm install --silent 2>nul
echo [OK] Frontend listo
cd ..

:: Iniciar ambos
echo [3/3] Iniciando servicios...
echo.

start "SIGEPRO-Backend" cmd /k "cd /d %CD%\sigepro-backend && set JAVA_HOME=C:\Program Files\Microsoft\jdk-21.0.10.7-hotspot && .\mvnw.cmd spring-boot:run"
echo [..] Backend iniciando en: http://localhost:8080/api
echo      Esperar a que aparezca "Started SigeproApplication"
timeout /t 10 /nobreak >nul

start "SIGEPRO-Frontend" cmd /k "cd /d %CD%\sigepro-frontend && npm run dev"
echo [..] Frontend iniciando en: http://localhost:5173

echo.
echo ============================================
echo  SISTEMA INICIADO
echo ============================================
echo.
echo  Frontend: http://localhost:5173
echo  Backend:  http://localhost:8080/api
echo  Swagger:  http://localhost:8080/api/swagger-ui.html
echo.
echo  Usuario: admin@transandina.com
echo  Password: 123456
echo.
echo  IMPORTANTE:
echo  - Espere 30-60 segundos a que el backend termine de iniciar
echo  - Si hay error de BD, configure MySQL y ejecute opcion 2
echo.
pause
