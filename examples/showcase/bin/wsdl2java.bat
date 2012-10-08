@echo off
echo Before run this script, please save 
echo http://localhost:8080/showcase/soap/accountservice?wsdl to target/wsdl/accountservice.wsdl

cd %~dp0/../
call mvn cxf-codegen:wsdl2java
if errorlevel 1 goto end

echo [INFO] Code had generated to <target/generated-sources/cxf>
pause

:end
pause