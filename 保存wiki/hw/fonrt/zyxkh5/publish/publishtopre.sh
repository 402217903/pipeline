#!/usr/bin/env bash
#function: 发布前端 xkh5 到预发布环境
#parmeter:
#author: 千峰
#version:1.0 
#Test: CentOS 6 

cd  /mnt/forgitpublish/qdykh5/pre/
tar -zxf xkh5.tar.gz  -C  /mnt/forgitpublish/qdykh5/pre/
sudo chown -R  forpubuse:forpubuse  /opt/java/ubzy/h5_pre/xk/10
chmod 777 -R /opt/java/ubzy/h5_pre/xk/10

rm -f /mnt/forgitpublish/qdykh5/pre/xkh5.tar.gz
rm -rf /opt/java/ubzy/h5_pre/xk/10*


mv /mnt/forgitpublish/qdykh5/pre/*  /opt/java/ubzy/h5_pre/xk/10

cd /opt/java/ubzy/h5_pre/xk/10

sudo chown -R  root_html:root_html /opt/java/ubzy/h5_pre/xk/10
