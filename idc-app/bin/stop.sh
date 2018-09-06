#!/bin/bash

cd `dirname $0`
cd ..


pidfile=./.app.pid;
if [ ! -f $pidfile ];
then
        echo pid file $pidfile not found.;
        exit 0;
fi
pids=`cat $pidfile`;
for pid in $pids
do
 kill -15 $pid;
done
rm -rf $pidfile;
echo "stop success";
exit 0;
