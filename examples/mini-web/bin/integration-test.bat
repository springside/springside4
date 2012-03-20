@echo off
echo [INFO] run all functional test.

cd %~dp0
cd ..

set MAVEN_OPTS=%MAVEN_OPTS% -XX:MaxPermSize=128m
call mvn clean integration-test -Pintegration-test

cd bin
pause