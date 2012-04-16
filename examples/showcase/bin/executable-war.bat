@echo off
echo [INFO] Packaging a executable war.

cd %~dp0
cd ..

set MAVEN_OPTS=%MAVEN_OPTS%
call mvn clean package -Pstandalone

echo [INFO] Executable war had been packaged as target/showcase-4.x.x.standalone.war


cd bin
pause