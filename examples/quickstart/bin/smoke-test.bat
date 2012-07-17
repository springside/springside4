@echo off
echo [INFO] run smoking functional test.

cd %~dp0
cd ..

set MAVEN_OPTS=%MAVEN_OPTS% -XX:MaxPermSize=128m
call mvn clean test -Psmoke-test

cd bin
pause