@echo off
echo [INFO] Use maven jetty-plugin run the project.

cd %~dp0
cd ..

set MAVEN_OPTS=%MAVEN_OPTS% -XX:MaxPermSize=128m -Xms1024m -Xmx1024m -XX:NewSize=512m -server
call mvn jetty:run -Djetty.port=8080

cd bin
pause 