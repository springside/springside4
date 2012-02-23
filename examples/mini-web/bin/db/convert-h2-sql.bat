@echo off
echo [INFO] Convert production sql to H2 sql for testing.

cd %~dp0
call ant convert.mysql.to.h2
pause