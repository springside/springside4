@echo off
echo [INFO] Use maven eclipse plugin download jar and generate eclipse project files.
cd ../..
call mvn eclipse:clean eclipse:eclipse
pause