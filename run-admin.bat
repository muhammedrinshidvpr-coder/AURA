@echo off
title AURA — Admin Governance Console
cd /d "%~dp0"
echo ========================================================
echo   Launching AURA Admin Governance Console (Slide 22)
echo   Logged in as: Campus Maintenance (Administrator)
echo   Connected to: Supabase Cloud PostgreSQL
echo ========================================================
if exist "target\aura.jar" (
    java -jar target\aura.jar --admin
) else (
    ".tools\apache-maven-3.9.9\bin\mvn.cmd" compile exec:java -Dexec.args="--admin"
)
pause
