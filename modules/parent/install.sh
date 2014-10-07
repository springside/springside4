#!/bin/bash

echo "[INFO] Install parent pom.xml to local repository."

mvn clean install -Dmaven.test.skip=true
