@echo off
echo [INFO] Install archetype to local repository.

cd %~dp0
call mvn clean install
pause