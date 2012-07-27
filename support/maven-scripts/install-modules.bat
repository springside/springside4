@echo off

cd ../../modules
call mvn clean install -Dmaven.test.skip=true
pause