#!/bin/bash



# Hackity hack hack (in case script is called from another directory)
cd `dirname $0`

## If no arguments, use default testing setup:
if [ $# -eq 0 ]
then
java -ea -classpath bin TMarket.fairshare.FairShareUI
else
java -ea -classpath bin TMarket.fairshare.FairShareUI $@
fi

# -classpath bin