#!/bin/bash

echo "[Pre-Requirement] Makesure install JDK 7.0+ and set the JAVA_HOME."
echo "[Pre-Requirement] Makesure install Maven 3.0.3+ and set the PATH."
	
export MAVEN_OPTS="$MAVEN_OPTS -Xmx1024m -XX:MaxPermSize=128M -Djava.security.egd=file:/dev/./urandom"

echo "[Step 1] Install all springside modules to local maven repository."
cd modules
mvn clean install
if [ $? -ne 0 ];then
  echo "Quit  because maven install fail"
  exit -1
fi

echo "[Step 2] run boot-api project in dev mode."
cd ../examples/boot-api
mvn spring-boot:run



