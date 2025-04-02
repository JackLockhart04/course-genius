@echo off
cd /d %~dp0
mvn exec:java -Dexec.mainClass="nf.free.coursegenius.LocalRun" -Plocal
pause