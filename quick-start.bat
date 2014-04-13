@echo off
echo [Pre-Requirement] Makesure install JDK 6.0+ and set the JAVA_HOME.
echo [Pre-Requirement] Makesure install Maven 3.0.3+ and set the PATH.

set MVN=mvn
set MAVEN_OPTS=%MAVEN_OPTS% -XX:MaxPermSize=128m

echo [Step 1] Install all springside modules and archetype to local maven repository.
cd modules
call %MVN% clean install
if errorlevel 1 goto error

cd ..\support\maven-archetype 
call %MVN% clean install
if errorlevel 1 goto error
cd ..\..

echo [Step 2] Initialize schema and data for all example projects.
cd examples
call %MVN% antrun:run -Prefresh-db
if errorlevel 1 goto error
cd ..\

echo [Step 3] Start all example projects.
cd examples\quickstart
start "QuickStart" %MVN% clean jetty:run 
if errorlevel 1 goto error
cd ..\showcase
start "Showcase" %MVN% clean jetty:run -Djetty.port=8081
if errorlevel 1 goto error
cd ..\..\

echo [INFO] Please wait a moment. When you see "[INFO] Started Jetty Server" in both 2 popup consoles, you can access below demo sites:
echo [INFO] http://localhost:8080/quickstart
echo [INFO] http://localhost:8081/showcase

goto end
:error
echo Error Happen!!
:end
pause