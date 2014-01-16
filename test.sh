#!/bin/bash
########################################
#Usage for future me                   #
#Leave IP_ADDR & PORT alone            #
#BASENAME is just well the basename    #
#...of the client                      #
#CHILDREN is the number of subprocesses#
#...that will be made                  #
#run does nothing but read from a file #
#and do noting with it untill its out  #
#of lines to read                      #
#this is because BoggleClient takes its#
#stdin from a file(given by FILE)      #
#Basically change children to test load#
#and alter the test[0-9].txt to change #
#the input to the server               #
########################################

IP_ADDR=`ip addr show wlan0 | sed -n 's/.*\(192[0-9.]*\)\/.*/\1/p'`
PORT=58839
BASENAME=client
CHILDREN=5
run(){
  $JAVA
  while read;do
    sleep 0.05
  done
}
for i in `seq 1 $CHILDREN`; do
 NAME="$BASENAME$i"
 FILE=test$i.txt
 JAVA="java cs671.BoggleClient $NAME $IP_ADDR $PORT $FILE"	
 (cat $FILE | run ) &>temp$i.txt &
done
wait
rm temp[0-9]*.txt