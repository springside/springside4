@echo off
echo [INFO] run BDD functional test,please start the showcase server first.

cd %~dp0
cd ..

set MAVEN_OPTS=%MAVEN_OPTS% -XX:MaxPermSize=128m
call mvn test -Pbdd-test

cd bin
pause