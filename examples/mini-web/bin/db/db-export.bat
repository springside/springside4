@echo off
echo [INFO] Export data  to src/main/resources/data/export-data.xml by dbunit.

cd %~dp0
call ant exp-db
pause