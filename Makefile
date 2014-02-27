# Makefile to build the Transportation Market




all:
	mkdir -p bin
#	javac -d bin src/TMarket/*.java src/TMarket/*/*.java
#	javac -Xlint -d bin src/TMarket/*/*.java
	javac -Xlint -d bin `find src -name *.java`
	mkdir -p bin/TMarket/resources
	cp -R src/TMarket/resources/*.* bin/TMarket/resources/


hprofile:
#	time java -Xrunhprof:cpu=times,depth=30,file=batch.hprof -classpath bin TMarket.ui.Batch
#	time java -Xrunhprof:cpu=samples,depth=30,file=batch.hprof -classpath bin TMarket.ui.Batch
#	time java -Xrunhprof:cpu=samples,interval=1,depth=40,file=batch.hprof -classpath bin TMarket.ui.Batch
#	time java -Xrunhprof:cpu=samples,interval=1,depth=40,file=ui.hprof -classpath bin TMarket.ui.UI
	time java -Xrunhprof:heap=sites,depth=30,file=batch.hprof -classpath bin TMarket.ui.UI

jrat:
#	time java -javaagent:shiftone-jrat.jar -classpath bin TMarket.ui.Batch
	time java -javaagent:shiftone-jrat.jar -classpath bin TMarket.ui.UI

time:
	time java -classpath bin TMarket.ui.Batch

jar: all
#	jar cvfm tm.jar manifest.mft -C bin .
	jar cfe tm.jar TMarket.ui.UI -C bin .

fairsharejar: all
	jar cfe fairshare.jar TMarket.fairshare.FairShareUI -C bin .

zip: clean
	zip -r bitcloud.zip *

clean:
	rm -rf bin
#	rm bin/TMarket/*.class bin/TMarket/*/*.class



