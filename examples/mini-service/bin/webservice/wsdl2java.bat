@echo off

cd %~dp0/../../
call mvn cxf-codegen:wsdl2java
if errorlevel 1 goto end

echo [INFO] Code had generated to <target/generated-sources/cxf>

:end
pause