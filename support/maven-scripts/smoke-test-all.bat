@echo off

cd ../../examples
call mvn clean test -Pfunctional-test,run-smoke
pause