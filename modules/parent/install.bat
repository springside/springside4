@echo off
echo [INFO] Install pom.xml to local repository.

cd %~dp0
call mvn clean install
pause