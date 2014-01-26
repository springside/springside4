@echo off
echo [INFO] run smoking functional test.

cd %~dp0
cd ..

set MAVEN_OPTS=%MAVEN_OPTS% -XX:MaxPermSize=128m
call mvn clean test -Pfunctional-test,run-smoke

cd bin
pause