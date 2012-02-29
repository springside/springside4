@echo off

cd %~dp0/../../
call mvn mybatis-generator:generate
if errorlevel 1 goto end

echo [INFO] Code had generated to <target/generated-sources/mybatis-generator>

:end
pause