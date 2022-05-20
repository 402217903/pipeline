#!/usr/bin/env bash
#function: dz server  自动发布
#parmeter:
#author: 千峰
#version: 1.0
#Test: CentOS 7 

function deploysubject()
{
echo "10s后将要更新 subject 致愿选科正式程序"
sleep 10


cd /opt/dzzyxk10/

ps -ef|grep subject-server-1.0.0-SNAPSHOT.jar |grep -v grep|awk '{print $2}'|xargs kill -s 9 


if [ -e /opt/dzzyxk10/subject-server-1.0.0-SNAPSHOT.jarbak ]
then

rm -rf /opt/dzzyxk10/subject-server-1.0.0-SNAPSHOT.jarbak

fi

mv /opt/dzzyxk10/subject-server-1.0.0-SNAPSHOT.jar /opt/dzzyxk10/subject-server-1.0.0-SNAPSHOT.jarbak

\cp /opt/publish/dzxk/subject-server-1.0.0-SNAPSHOT.jar   /opt/dzzyxk10/

sh subject-server.sh

sleep 3

ps -ef|grep subject-server-1.0.0-SNAPSHOT.jar 


}

function deployprofession()
{
echo "10s后将要更新 profession 致愿选科正式程序"
sleep 10


cd /opt/dzzyxk10/

ps -ef|grep profession-server-0.0.1-SNAPSHOT.jar |grep -v grep|awk '{print $2}'|xargs kill -s 9 


if [ -e /opt/dzzyxk10/profession-server-0.0.1-SNAPSHOT.jarbak ]
then

rm -rf /opt/dzzyxk10/profession-server-0.0.1-SNAPSHOT.jarbak

fi

mv /opt/dzzyxk10/profession-server-0.0.1-SNAPSHOT.jar /opt/dzzyxk10/profession-server-0.0.1-SNAPSHOT.jarbak

\cp /opt/publish/dzxk/profession-server-0.0.1-SNAPSHOT.jar   /opt/dzzyxk10/

sh profession-server.sh

sleep 3

ps -ef|grep profession-server-0.0.1-SNAPSHOT.jar 


}




while :
do
cat <</
    1)deploysubject     
/
echo  "请选择对应选项"
    read choose
    case $choose in
    1)
     deploysubject;;
    2)
     deployprofession;;
    esac
done


