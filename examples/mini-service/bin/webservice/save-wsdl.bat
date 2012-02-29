@echo off
echo [INFO] Make sure the local WebService appllication started

cd %~dp0
call ant
if errorlevel 1 goto end

echo [INFO] WSDL had saved to webapp/wsdl/mini-service.wsdl.

:end
pause