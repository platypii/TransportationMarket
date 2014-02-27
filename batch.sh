#!/bin/bash



# Hackity hack hack (in case script is called from another directory)
cd `dirname $0`


## If no arguments, use default testing setup:
if [ $# -eq 0 ]
then
#echo java -ea -classpath bin TMarket.auctionserver.AuctionServer resources/grid16x10.cty &
#java -ea -classpath bin TMarket.auctionserver.AuctionServer resources/grid16x10.cty &
echo java -ea -classpath bin TMarket.ui.Batch resources/test.sim
java -ea -classpath bin TMarket.ui.Batch resources/test.sim
else
echo java -ea -classpath bin TMarket.ui.Batch $@
java -ea -classpath bin TMarket.ui.Batch $@
fi




