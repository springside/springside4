#!/bin/bash

echo "[INFO] Generating project in ./generated-projects"

if [[ ! -d "generated-projects" ]]; then
    mkdir "generated-projects"
fi

cd generated-projects

mvn archetype:generate -DarchetypeGroupId=org.springside.examples -DarchetypeArtifactId=quickstart-archetype -DarchetypeVersion=4.1.1-SNAPSHOT
