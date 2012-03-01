@echo off
echo [Pre-Requirement] Makesure install JDK 6.0+ and set the JAVA_HOME.
echo [Pre-Requirement] Makesure install Maven 3.0+

set MVN=mvn
set MAVEN_OPTS=%MAVEN_OPTS% -XX:MaxPermSize=128m

echo [Step 1] Install all springside modules to local maven repository, and generate eclipse files to all projects.
call %MVN% clean install -Pmodules -Dmaven.test.skip=true
if errorlevel 1 goto error
call %MVN% -Pall eclipse:clean eclipse:eclipse
if errorlevel 1 goto error

echo [Step 2] Start H2 Standalone database.
cd support/h2
start "H2 Database" %MVN% exec:java -PwithoutBrowser
cd ..\..\

echo [Step 3] Mini-Service:init database data and start jetty.
cd examples\mini-service
call %MVN% antrun:run -Prefreshdb
if errorlevel 1 goto error
start "Mini-Service" %MVN% clean jetty:run -Djetty.port=8082
cd ..\..\

echo [Step 4] Mini-Web:init database data and start jetty.
cd examples\mini-web
call %MVN% antrun:run -Prefreshdb
if errorlevel 1 goto error
start "Mini-Web" %MVN% jetty:run -Djetty.port=8081
cd ..\..\

echo [Step 5] Showcase:init database data and start jetty.
cd examples\showcase
call %MVN% antrun:run -Prefreshdb
if errorlevel 1 goto error
start "Showcase" %MVN% jetty:run
cd ..\..\

echo [INFO] SpringSide4.0 Quick Start finished.
echo [INFO] Access below demo sites:
echo [INFO] http://localhost:8082/mini-service
echo [INFO] http://localhost:8081/mini-web
echo [INFO] http://localhost:8080/showcase

goto end
:error
echo Error Happen!!!
:end
pause