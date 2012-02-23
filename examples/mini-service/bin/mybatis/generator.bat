@echo off

cd %~dp0/../../
call mvn mybatis-generator:generate
if errorlevel 1 goto end

echo [INFO] 代码已生成到target/generated-sources/mybatis-generator目录下.

:end
pause