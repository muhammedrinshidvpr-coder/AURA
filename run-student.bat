@echo off
title AURA — Student Portal
cd /d "%~dp0"
echo ========================================================
echo   Launching AURA Student Portal (Slide 21 Mockup)
echo   Logged in as: Muhammed Rinshid VP (Lead Student)
echo   Connected to: Supabase Cloud PostgreSQL
echo ========================================================
if exist "target\aura.jar" (
    java -jar target\aura.jar --student
) else (
    ".tools\apache-maven-3.9.9\bin\mvn.cmd" compile exec:java -Dexec.args="--student"
)
pause
