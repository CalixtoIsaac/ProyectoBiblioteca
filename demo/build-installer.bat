@echo off
cd /d "%~dp0"
setlocal

set APP_NAME=SistemaBiblioteca
set APP_VERSION=1.0
set VENDOR=Mi Biblioteca
set DESCRIPTION=Sistema de Gestion de Biblioteca
set MAIN_CLASS=com.biblioteca_1.BibliotecaFXApp
set JAR_NAME=ProyectoBibliotecaFX-1.0.jar
set ICON_PATH=src\main\resources\com\biblioteca_1\images\icono.ico
set OUTPUT_DIR=installer
set RUNTIME_DIR=custom-runtime

if not exist "pom.xml" ( echo [ERROR] No se encontro pom.xml & pause & exit /b 1 )
if "%JAVA_HOME%"=="" ( echo [ERROR] JAVA_HOME no configurado & pause & exit /b 1 )
if not exist "%JAVA_HOME%\bin\jpackage.exe" ( echo [ERROR] jpackage no encontrado & pause & exit /b 1 )

echo ============================================================
echo   SISTEMA BIBLIOTECA - Generador de Instalador
echo   Directorio: %CD%
echo ============================================================
echo.

echo Cerrando procesos Java previos...
taskkill /F /IM java.exe  >nul 2>&1
taskkill /F /IM javaw.exe >nul 2>&1
timeout /t 2 /nobreak >nul

echo Limpiando carpeta target y runtime previo...
if exist "target" (
    takeown /f "target" /r /d y >nul 2>&1
    icacls "target" /grant administrators:F /t >nul 2>&1
    rmdir /s /q "target" >nul 2>&1
)
if exist "%RUNTIME_DIR%" rmdir /s /q "%RUNTIME_DIR%"

echo Eliminando desktop.ini...
for /r "src" %%f in (desktop.ini) do del /f /q "%%f" >nul 2>&1
echo.

echo [1/4] Compilando y empaquetando...
call mvn package -q
if errorlevel 1 ( echo [ERROR] Compilacion fallida. & pause & exit /b 1 )
if not exist "target\%JAR_NAME%" ( echo [ERROR] No se genero el JAR & dir target\ & pause & exit /b 1 )
if not exist "target\libs" ( echo [ERROR] No se genero target\libs & pause & exit /b 1 )
echo       OK
echo.

echo [2/4] Construyendo runtime con JavaFX incluido (jlink)...
echo       Esto puede tardar 1-2 minutos...

:: jlink construye un JRE minimo que INCLUYE los modulos de JavaFX
:: --module-path: donde estan los modulos (JDK + JARs de JavaFX)
:: --add-modules: modulos que queremos incluir en el runtime
"%JAVA_HOME%\bin\jlink.exe" ^
    --module-path "%JAVA_HOME%\jmods;target\libs" ^
    --add-modules java.base,java.desktop,java.logging,java.sql,java.naming,java.xml,java.security.jgss,java.instrument,java.management,javafx.controls,javafx.fxml,javafx.base,javafx.graphics,javafx.media ^
    --output "%RUNTIME_DIR%" ^
    --compress=2 ^
    --no-header-files ^
    --no-man-pages

if errorlevel 1 (
    echo.
    echo [ERROR] jlink fallo. 
    echo Los JARs de JavaFX en target\libs pueden no tener modulos compatibles.
    echo Verifica que los javafx-*-win.jar esten en target\libs\
    dir target\libs\ | findstr /i "javafx"
    pause & exit /b 1
)
echo       OK - Runtime con JavaFX creado en %RUNTIME_DIR%\
echo.

echo [3/4] Preparando carpeta de salida...
if exist "%OUTPUT_DIR%" rmdir /s /q "%OUTPUT_DIR%"
mkdir "%OUTPUT_DIR%"
echo       OK
echo.

echo [4/4] Generando instalador .exe con jpackage...
echo       (1-2 minutos, por favor espera)
echo.

set ICON_ARG=
if exist "%ICON_PATH%" (
    set "ICON_ARG=--icon "%ICON_PATH%""
    echo       Icono: %ICON_PATH%
) else (
    echo       AVISO: icono.ico no encontrado.
)

:: --runtime-image le dice a jpackage que use el runtime que
:: construimos con jlink (que ya incluye JavaFX dentro)
"%JAVA_HOME%\bin\jpackage.exe" ^
    --type exe ^
    --name "%APP_NAME%" ^
    --app-version "%APP_VERSION%" ^
    --vendor "%VENDOR%" ^
    --description "%DESCRIPTION%" ^
    --runtime-image "%RUNTIME_DIR%" ^
    --input target ^
    --main-jar %JAR_NAME% ^
    --main-class %MAIN_CLASS% ^
    --dest "%OUTPUT_DIR%" ^
    --win-shortcut ^
    --win-menu ^
    --win-dir-chooser ^
    --java-options "-Dfile.encoding=UTF-8" ^
    --java-options "--add-opens=javafx.graphics/com.sun.javafx.application=ALL-UNNAMED" ^
    --java-options "--add-opens=java.base/java.lang=ALL-UNNAMED" ^
    %ICON_ARG%

if errorlevel 1 (
    echo.
    echo [ERROR] jpackage fallo.
    echo   Instala WiX Toolset v3: https://github.com/wixtoolset/wix3/releases
    echo.
    pause & exit /b 1
)

echo.
echo ============================================================
echo   EXITO! Instalador listo en:
echo   %CD%\%OUTPUT_DIR%\%APP_NAME%-%APP_VERSION%.exe
echo ============================================================
echo.
pause
