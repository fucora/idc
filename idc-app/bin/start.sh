#!/bin/sh

cd `dirname $0`
cd ..

PID_FILE=./.app.pid;

if [ -f $PID_FILE ];
then
        old_pid=`cat $PID_FILE`;
        pids=`ps aux | grep java | awk '{print $2;}'`;
        for pid in $pids
        do
                if [ $pid -eq $old_pid ];
                then
                        echo "server is running as $pid, please stop it first.";
                        exit 0;
                fi
        done
fi

nohup java -cp "./conf:lib/*" -Dspring.profiles.active=default com.iwellmass.idc.app.IDCApplication -Xms1024m -Xmx1024m  &
pid=$!
echo $pid > $PID_FILE;
echo "start success"
    
