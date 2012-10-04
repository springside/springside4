@echo off

echo [INFO] Generating project in ./generate dir

cd generate
call mvn archetype:generate -DarchetypeCatalog=local
pause