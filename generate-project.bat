@echo off
echo [INFO] Generating project in ./generated-projects

mkdir generated-projects
cd generated-projects
call mvn archetype:generate -DarchetypeCatalog=local

pause


