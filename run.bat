@echo off
title AURA — Institutional Sign In
cd /d "%~dp0"
echo ========================================================
echo   Starting AURA Campus Governance Desktop Application
echo   Connected to: Supabase Cloud PostgreSQL
echo ========================================================
if exist "target\aura.jar" (
    java -jar target\aura.jar
) else (
    ".tools\apache-maven-3.9.9\bin\mvn.cmd" compile exec:java
)
pause
