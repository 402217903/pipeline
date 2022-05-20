#!/usr/bin/env bash
#function: dz server  自动发布
#parmeter:
#author: 千峰
#version: 1.0
#Test: CentOS 7 
cd /opt/dzzyxk10/

ps -ef|grep profession-server-0.0.1-SNAPSHOT.jar  |grep -v grep|awk '{print $2}'|xargs kill -s 9 

nohup java -Xms512m -Xmx2048m -jar profession-server-0.0.1-SNAPSHOT.jar --spring.profiles.active=formal   >/dev/null 2>&1 &
