@echo off
echo [INFO] Use maven tomcat7-plugin run the project.

cd %~dp0
cd ..

set MAVEN_OPTS=%MAVEN_OPTS% -XX:MaxPermSize=128m
call mvn tomcat7:run

cd bin
pause 