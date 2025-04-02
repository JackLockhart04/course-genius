@echo off
cd /d %~dp0
mvn clean package -Pproduction
pause