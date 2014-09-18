#!/bin/bash

echo "[INFO] run smoking functional test."

cd ..

set MAVEN_OPTS=%MAVEN_OPTS% -XX:MaxPermSize=128m

mvn clean test -Pfunctional-test,run-smoke

cd bin
