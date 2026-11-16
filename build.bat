@echo off
REM Builds the mod into a .jar (Windows)
cd /d "%~dp0"

where java >nul 2>nul
if errorlevel 1 (
  echo ERROR: Java 17 is not installed.
  exit /b 1
)

if not exist gradlew.bat (
  where gradle >nul 2>nul
  if errorlevel 1 (
    echo ERROR: no gradlew.bat and no 'gradle' installed. Install Gradle or copy the wrapper from the Forge 1.20.1 MDK.
    exit /b 1
  )
  echo No wrapper found, generating one with Gradle 8.1.1...
  call gradle wrapper --gradle-version 8.1.1
)

call gradlew.bat clean build
if errorlevel 1 exit /b 1

echo.
echo Done. Your jar is in build\libs\
dir /b build\libs\*.jar
pause
