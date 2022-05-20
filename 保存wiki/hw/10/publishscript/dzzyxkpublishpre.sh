#!/usr/bin/env bash
#function:  下发全量包到服务器中
#parmeter:  
#author: 千峰 
#version: 1.0 
#Test: CentOS 6 



ansible all -i "172.16.3.20,"  --private-key /data/gitlabrunneruse/pubkey/gitpubusekey  -u appuse   -m copy -a "src=/mnt/forgitpublish/ubdzzyxk/pre/ dest=/opt/publish/dzxk/ group=appuse owner=appuse "

\rm /mnt/forgitpublish/ubdzzyxk/pre/*.jar 



curl 'https://oapi.dingtalk.com/robot/send?access_token=a4b0d8a997e6beb1c78ee51812848c9c89466ab8f98ec1fc497a072088b8ad0c' \
-H 'Content-Type: application/json' \
-d '
{"msgtype": "text", 
  "text": {
      "content": "大志 选科 华为云 预发布后端服务包准备完毕"
   }
}'


#/usr/bin/python  /mnt/forgitpublish/ddsendmsg/lcCMS.pyc  zyxk test 10


