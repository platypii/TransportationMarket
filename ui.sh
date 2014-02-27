#!/bin/bash



# Hackity hack hack (in case script is called from another directory)
cd `dirname $0`

## If no arguments, use default testing setup:
if [ $# -eq 0 ]
then
java -ea -classpath bin TMarket.ui.UI -nodebug
#java -Dsun.java2d.opengl=true -ea -classpath bin TMarket.ui.UI
#java -ea TMarket.ui.UI resources/la.sim
else
java -ea -classpath bin TMarket.ui.UI $@
fi

# -classpath bin