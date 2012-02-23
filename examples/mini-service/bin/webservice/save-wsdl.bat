@echo off
echo [INFO] 确保设置path系统变量含ANT1.7以上版本的bin目录.
echo [INFO] 确保本地WebService应用已启动.

cd %~dp0
call ant
if errorlevel 1 goto end

echo [INFO] WSDL已保存到webapp/wsdl/mini-service.wsdl.

:end
pause