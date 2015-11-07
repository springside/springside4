#!/bin/bash

echo "[Pre-Requirement] Makesure install JDK 7.0+ and set the JAVA_HOME."
echo "[Pre-Requirement] Makesure install Maven 3.0.3+ and set the PATH."
	
set MAVEN_OPTS=$MAVEN_OPTS -XX:MaxPermSize=128m

echo "[Step 1] Install all springside modules to local maven repository."
cd modules
mvn clean install