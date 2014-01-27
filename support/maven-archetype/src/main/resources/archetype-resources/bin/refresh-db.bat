@echo off
echo [INFO] Re-create the schema and provision the sample data.

cd %~dp0
cd ..

call mvn antrun:run -Prefresh-db

cd bin
pause