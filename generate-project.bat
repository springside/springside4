@echo off
echo [INFO] Generating project in ./generated-projects

if not exist  generated-projects mkdir generated-projects
cd generated-projects
call mvn archetype:generate -DarchetypeGroupId=org.springside.examples -DarchetypeArtifactId=quickstart-archetype -DarchetypeVersion=4.2.3-GA

pause


