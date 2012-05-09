@echo off
echo [Pre-Requirement] Makesure install JDK 6.0+ and set the JAVA_HOME.
echo [Pre-Requirement] Makesure install Maven 3.0.4+ and set the PATH.

set MVN=mvn
set MAVEN_OPTS=%MAVEN_OPTS% -XX:MaxPermSize=128m

echo [Step 1] Install all springside modules to local maven repository.
cd modules
call %MVN% clean install -Dmaven.test.skip=true
if errorlevel 1 goto error
cd ..\

echo [Step 2] Generate Eclipse project files for all projects
call %MVN% eclipse:clean eclipse:eclipse
if errorlevel 1 goto error

echo [Step 3] Init schema and data for all example projects.
cd examples
call %MVN% antrun:run -Prefresh-db
if errorlevel 1 goto error
cd ..\

echo [Step 4] Start all example projects.
cd examples\mini-service
start "Mini-Service" %MVN% clean jetty:run -Djetty.port=8082
if errorlevel 1 goto error
cd ..\mini-web
start "Mini-Web" %MVN% clean jetty:run -Djetty.port=8081
if errorlevel 1 goto error
cd ..\showcase
start "Showcase" %MVN% clean jetty:run
if errorlevel 1 goto error

cd ..\..\

echo [INFO] Please wait a moment then access below demo sites:
echo [INFO] http://localhost:8082/mini-service
echo [INFO] http://localhost:8081/mini-web
echo [INFO] http://localhost:8080/showcase

goto end
:error
echo Error Happen!!!
:end
pause