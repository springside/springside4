@echo off
echo [INFO] Install jar to local repository.

cd %~dp0
call mvn clean install
pause