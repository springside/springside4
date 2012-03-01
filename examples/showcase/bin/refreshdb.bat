@echo off
echo [INFO] Re-create the schema and provision the sample data.

cd %~dp0
cd ..

set MAVEN_OPTS=%MAVEN_OPTS%
call mvn antrun:run -Prefreshdb

cd bin
pause