@echo off
echo [Pre-Requirement] Makesure install JDK 7.0+ and set the JAVA_HOME.
echo [Pre-Requirement] Makesure install Maven 3.0.3+ and set the PATH.

set MVN=mvn
set MAVEN_OPTS=%MAVEN_OPTS% -XX:MaxPermSize=128M

echo [Step 1] Install all springside modules to local maven repository.
cd modules
call %MVN% clean install
if errorlevel 1 goto error

echo [Step 2] run boot-api project in dev mode.
cd ..\examples\boot-api
call %MVN% spring-boot:run
if errorlevel 1 goto error


goto end
:error
echo Error Happen!!
:end
pause