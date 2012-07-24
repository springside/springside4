@echo off

cd ../../modules/parent
call mvn versions:display-property-updates
pause