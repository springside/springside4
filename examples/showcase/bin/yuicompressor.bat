@echo off
echo [PreRequirment] Download last version of yuicompressor from https://github.com/yui/yuicompressor/releases and put it here.

cd %~dp0
java -jar yuicompressor-*.jar -o ..\src\main\webapp\static\styles\default.min.css ..\src\main\webapp\static\styles\default.css

pause