#!/bin/bash
cd `dirname $0`

./dump.sh
./stop.sh
./start.sh
