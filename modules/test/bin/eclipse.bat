@echo off
echo [INFO] Use maven eclipse plugin download jar and generate eclipse project files.
echo [INFO] Please add "-Declipse.workspace=<path-to-eclipse-workspace>" at end of mvn command.

cd %~dp0
cd ..
call mvn eclipse:clean eclipse:eclipse
cd bin
pause