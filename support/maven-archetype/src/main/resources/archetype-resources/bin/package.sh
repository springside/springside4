#!/bin/bash

echo "[INFO] Package the war in target dir."

cd ..

mvn clean package -Dmaven.test.skip=true

cd bin
