#!/bin/bash
cd `dirname $0`
BIN_DIR=`pwd`
cd ..
DEPLOY_DIR=`pwd`
CONF_DIR=$DEPLOY_DIR/conf

SERVER_NAME=DDC_SERVER
SERVER_PORT=40000
LOGS_DIR=$DEPLOY_DIR/logs
JAVA_COMMAND=/usr/java/jdk1.8.0_74/bin/java

PIDS=`ps aux | grep java | grep "$CONF_DIR" |awk '{print $2}'`
if [ -n "$PIDS" ]; then
    echo "ERROR: The $SERVER_NAME already started!"
    echo "PID: $PIDS"
    exit 1
fi

if [ -n "$SERVER_PORT" ]; then
    SERVER_PORT_COUNT=`netstat -tln | grep $SERVER_PORT | wc -l`
    if [ $SERVER_PORT_COUNT -gt 0 ]; then
        echo "ERROR: The $SERVER_NAME port $SERVER_PORT already used!"
        exit 1
    fi
fi

if [ ! -d $LOGS_DIR ]; then
    mkdir -p $LOGS_DIR
fi

STDOUT_FILE=$LOGS_DIR/stdout.log

LIB_DIR=$DEPLOY_DIR/lib
LIB_JARS=`ls $LIB_DIR|grep .jar|awk '{print "'$LIB_DIR'/"$0}'|tr "\n" ":"`

######### change the jvm args on here ##########
JAVA_OPTS=" -server -Xms1536M -Xmx1536M -Xmn512M -Xss256k -XX:PermSize=192M -XX:MaxPermSize=192M -XX:MaxDirectMemorySize=256M -XX:+UseParallelGC -XX:+UseParallelOldGC -XX:GCTimeRatio=49 -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:$LOGS_DIR/gc.log -XX:+HeapDumpOnOutOfMemoryError -XX:ErrorFile=$LOGS_DIR/jvm_error.log -XX:HeapDumpPath=$LOGS_DIR/jvm_dump.hprof "
################################################

JAVA_DEBUG_OPTS=""
if [ "$1" = "debug" ]; then
    JAVA_DEBUG_OPTS=" -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n "
fi

JAVA_JMX_OPTS=""
if [ "$1" = "jmx" ]; then
    JAVA_JMX_OPTS=" -Dcom.sun.management.jmxremote.port=1099 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false "
fi

echo -e "Starting the $SERVER_NAME ...\c"
nohup $JAVA_COMMAND $JAVA_OPTS $JAVA_DEBUG_OPTS $JAVA_JMX_OPTS -classpath $CONF_DIR:$LIB_JARS com.dmall.dispatcher.server.ServerLauncher > $STDOUT_FILE 2>&1 &

COUNT=0
while [ $COUNT -lt 1 ]; do
    echo -e ".\c"
    sleep 1 
    if [ -n "$SERVER_PORT" ]; then
		COUNT=`netstat -an | grep $SERVER_PORT | wc -l`
    else
    	COUNT=`ps aux | grep java | grep "$DEPLOY_DIR" | awk '{print $2}' | wc -l`
    fi
    if [ $COUNT -gt 0 ]; then
        break
    fi
done

echo "OK!"
PIDS=`ps aux | grep java | grep "$CONF_DIR" | awk '{print $2}'`
echo "PID: $PIDS"
echo "STDOUT: $STDOUT_FILE"
