@echo off
echo [PreRequirment] Download yuicompressor-2.4.7.jar from http://yuilibrary.com/downloads/yuicompressor and put it here.

cd %~dp0
java -jar yuicompressor-2.4.7.jar -o ..\src\main\webapp\static\styles\default.min.css ..\src\main\webapp\static\styles\default.css

pause