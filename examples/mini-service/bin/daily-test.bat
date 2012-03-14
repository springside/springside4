@echo off
echo [INFO] run daily functional test.

cd %~dp0
cd ..

set MAVEN_OPTS=%MAVEN_OPTS% -XX:MaxPermSize=128m
call mvn clean integration-test

cd bin
pause