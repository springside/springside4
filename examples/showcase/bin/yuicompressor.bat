@echo off
echo [PreRequirment] Download yuicompressor-2.4.7.jar from http://yuilibrary.com/downloads/yuicompressor and put it here.

cd %~dp0
java -jar yuicompressor-2.4.7.jar -o ..\src\main\webapp\static\styles\main.min.css ..\src\main\webapp\static\styles\main.css

pause