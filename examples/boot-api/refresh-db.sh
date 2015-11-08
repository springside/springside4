#!/bin/bash

echo "init db for production mode"

mvn antrun:run -Prefresh-db