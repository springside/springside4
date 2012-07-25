@echo off

cd ../../
call mvn versions:display-dependency-updates
pause