@echo off
echo [INFO] Create schema by sql, and import default data from src/test/resources/data/sample-data.xml by dbunit.

cd %~dp0
call ant init-db
pause