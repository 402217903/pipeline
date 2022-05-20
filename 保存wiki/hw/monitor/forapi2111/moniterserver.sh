#!/usr/bin/env bash
#function: 监控 app server 程序运行
#parmeter:
#author: 千峰
#version:
#Test:


function checksubject(){
curl -i  -s  http://127.0.0.1:23000 >>/opt/dzzyxk10/checksubject
curl -i  -s  http://127.0.0.1:23000 |grep 'HTTP/1.1 200'
if [ $? -eq 0 ]
then 


    if [ -e /opt/dzzyxk10/subjectok ]
    then
    
        exit 90
    
    fi


else

    if [ -e /opt/dzzyxk10/subjectok ]
    then
    
        rm -rf /opt/dzzyxk10/subjectok
    fi
    date >>/opt/dzzyxk10/checksubject
    

curl 'https://oapi.dingtalk.com/robot/send?access_token=895629004ba138c0a8ecc89da63f2d040ee7adf766e614247d8f79eb5d919702' \
-H 'Content-Type: application/json' \
-d '
{"msgtype": "text", 
  "text": {
      "content": "致愿选科 s2 致愿选科模块出现问题，将进行重启"
   }
}'



cd /opt/dzzyxk10/
ps -ef|grep subject-server-1.0.0-SNAPSHOT.jar |grep -v grep|awk '{print $2}'|xargs kill -s 9 
sh /opt/dzzyxk10/subject-server.sh

touch /opt/dzzyxk10/subjectok


fi

}


checksubject

