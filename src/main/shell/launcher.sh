#!/bin/sh
# resolve links - $0 may be a softlink
PRG="$0"
while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done
SERVICE_NAME="NEWSERVICE"
TS=`date "+%Y%m%d%H%M%S"`
PRGNAME=`basename $PRG`
PRGDIR=`dirname "$PRG"`
PRG_HOME=`cd $PRGDIR/..;pwd`
JAVA_HOME=$CDDS_HOME/jre
LIB_DIR=$PRG_HOME/lib
CFG_DIR=$PRG_HOME/config
RT_CP=".:$PRG_HOME:$CFG_DIR"
LOG_DIR="$PRG_HOME/log"
LOG_FILE="$LOG_DIR/${PRGNAME}_$TS.log"
WORKING_DIR=$PRG_HOME/work

if [ "x$LD_LIBRARY_PATH" = "x" ]; then
	LD_LIBRARY_PATH=$LIB_DIR/native
else
	LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$LIB_DIR/native
fi
export LD_LIBRARY_PATH

JARS=`ls -t $LIB_DIR/*.jar`
for JAR in $JARS
do
RT_CP="$RT_CP:$JAR"
done

HADOOP_CLIENT_LIB="/usr/lib/hadoop/client"
HDFSJARS=`ls -t $HADOOP_CLIENT_LIB/*.jar | grep -v slf4j | grep -v log4j | grep -v jetty`
for JAR in $HDFSJARS
do
RT_CP="$RT_CP:$JAR"
done

ACTION=$1;
shift

MAINCLASS=""
cd "$WORKING_DIR"
echo "#########################################################################################"  >$LOG_DIR/${PRGNAME}_${TS}_STARTUP.log
"$JAVA_HOME/bin/java" -version			 >>$LOG_DIR/${PRGNAME}_${TS}_STARTUP.log  2>&1
echo "#########################################################################################"   >>$LOG_DIR/${PRGNAME}_${TS}_STARTUP.log
echo "$RT_CP"  >>$LOG_DIR/${PRGNAME}_${TS}_STARTUP.log
echo "#########################################################################################"   >>$LOG_DIR/${PRGNAME}_${TS}_STARTUP.log
JAVA_OPTS="-Xms128m -Xmx2048m -XX:+UseParallelOldGC -Dlog.file=$LOG_FILE"
echo "$JAVA_HOME/bin/java" $JAVA_OPTS  "$MAINCLASS" -service $SERVICE_NAME  "$@"  >>$LOG_DIR/${PRGNAME}_${TS}_STARTUP.log
export CLASSPATH="$RT_CP"		


export LD_LIBRARY_PATH
case "$ACTION" in
        start)
            if [ -e "$WORKING_DIR/$PRGNAME.pid" ]; then
				PID=`cat "$WORKING_DIR/$PRGNAME.pid"`
				echo "$PRGNAME is currently running with pid $PID"
			else
				nohup "$JAVA_HOME/bin/java" $JAVA_OPTS  "$MAINCLASS" -service $SERVICE_NAME  "$@" >>$LOG_DIR/${PRGNAME}_${TS}_STARTUP.log 2>&1 &
				#"$JAVA_HOME/bin/java" $JAVA_OPTS  "$MAINCLASS" -service $SERVICE_NAME 
				PID=$!
				echo $PID >"$WORKING_DIR"/"$PRGNAME".pid
				echo "$PRGNAME start at $PID"
			fi		
            ;;
        stop)
           if [ -e "$WORKING_DIR/$PRGNAME.pid" ]; then
				PID=`cat "$WORKING_DIR/$PRGNAME.pid"`
				echo "Stopping $PRGNAME at pid $PID"
				kill $PID
				sleep 3		
				rm -f "$WORKING_DIR/$PRGNAME.pid"				
			else
				LINE=`ps -ef | grep -P "$JAVA_HOME/bin/java" | grep "-service $SERVICE_NAME"`
				if [  "x" = "x$LINE" ]; then
					echo "$PRGNAME is not running."
				else
					COLS=( $LINE )
					PID=${COLS[1]}
					echo "Stopping $PRGNAME at pid $PID"
					kill $PID
					sleep 3	
				fi					
			fi
            ;;
        status)			
			LINE=`ps -ef | grep  "$JAVA_HOME/bin/java" | grep "-service $SERVICE_NAME"`
			if [  "x" = "x$LINE" ]; then
				echo "$JAVA_HOME/bin/java" "-service $SERVICE_NAME"
				echo "$PRGNAME is not running."
			else
				COLS=( $LINE )
				PID=${COLS[1]}
				echo "$PRGNAME is currently running with pid $PID"
			fi			
            ;;
		cleanlog)			
			rm -f $LOG_DIR/${PRGNAME}*.log
            ;;
        restart)
           if [ -e "$WORKING_DIR/$PRGNAME.pid" ]; then
				PID=`cat "$WORKING_DIR/$PRGNAME.pid"`
				kill $PID
				sleep 3		
				rm -f "$WORKING_DIR/$PRGNAME.pid"				
			else
				LINE=`ps -ef | grep -P "$JAVA_HOME/bin/java" | grep "-service $SERVICE_NAME"`
				COLS=( $LINE )
				PID=${COLS[1]}
				kill $PID
				sleep 3	
			fi	
			nohup "$JAVA_HOME/bin/java" $JAVA_OPTS  "$MAINCLASS" -service $SERVICE_NAME  "$@" >>$LOG_DIR/${PRGNAME}_${TS}_STARTUP.log 2>&1 &
			#"$JAVA_HOME/bin/java" $JAVA_OPTS  "$MAINCLASS" -service $SERVICE_NAME 
			PID=$!
			echo $PID >"$WORKING_DIR"/"$PRGNAME".pid
			echo "$PRGNAME start at $PID"
            ;;             
        *)
            echo $"Usage: $0 {start|stop|restart|status}"
            exit 1
 
esac














