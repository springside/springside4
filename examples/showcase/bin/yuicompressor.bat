@echo off
echo Please download the yuicompressor from http://yuilibrary.com/downloads/yuicompressor and put the jar file here.

cd %~dp0
java -jar yuicompressor-2.4.7.jar -o ..\src\main\webapp\static\showcase.min.css ..\src\main\webapp\static\showcase.css

pause