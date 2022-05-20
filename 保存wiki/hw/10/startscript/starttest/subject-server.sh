
#!/usr/bin/env bash
#function: dz server  自动发布
#parmeter:
#author: 千峰
#version: 1.0
#Test: CentOS 7 

cd /opt/dzzyxk10/

ps -ef|grep subject-server-1.0.0-SNAPSHOT.jar |grep -v grep|awk '{print $2}'|xargs kill -s 9 

nohup java -Xms512m -Xmx512m -jar subject-server-1.0.0-SNAPSHOT.jar --spring.data.mongodb.custom.authentication-database=admin --spring.profiles.active=test    >/dev/null 2>&1 &
