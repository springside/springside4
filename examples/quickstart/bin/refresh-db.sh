#!/bin/bash

echo "[INFO] Re-create the schema and provision the sample data."

cd ..

mvn antrun:run -Prefresh-db

cd bin
