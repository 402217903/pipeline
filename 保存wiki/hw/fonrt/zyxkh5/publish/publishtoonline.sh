#!/usr/bin/env bash
#function: 发布前端 xkh5 到正式环境
#parmeter:
#author: 千峰
#version:1.0 
#Test: CentOS 6 


cd  /mnt/forgitpublish/qdykh5/online/
tar -zxf xkh5.tar.gz  -C  /mnt/forgitpublish/qdykh5/online/forpub/
sudo chown -R  forpubuse:forpubuse  /opt/java/ubzy/h5/xk/10
chmod 777 -R  /opt/java/ubzy/h5/xk/10

if [ -f xkh5.tar.gzdel ]
then
    
    \rm  -rf  /mnt/forgitpublish/qdykh5/online/xkh5.tar.gzdel
    
fi


if [ -f /mnt/forgitpublish/qdykh5/online/xkh5.tar.gzsecond ]
then
    
    mv /mnt/forgitpublish/qdykh5/online/xkh5.tar.gzsecond  /mnt/forgitpublish/qdykh5/online/xkh5.tar.gzdel
    
fi


if [ -f /mnt/forgitpublish/qdykh5/online/xkh5.tar.gzfirst ]
then
    
    mv /mnt/forgitpublish/qdykh5/online/xkh5.tar.gzfirst  /mnt/forgitpublish/qdykh5/online/xkh5.tar.gzsecond
    
fi


if [ -f /mnt/forgitpublish/qdykh5/online/xkh5.tar.gz ]
then
    
    mv /mnt/forgitpublish/qdykh5/online/xkh5.tar.gz  /mnt/forgitpublish/qdykh5/online/xkh5.tar.gzfirst
    
fi


rm -rf /opt/java/ubzy/h5/xk/10/*


mv /mnt/forgitpublish/qdykh5/online/forpub/*  /opt/java/ubzy/h5/xk/10/

cd /opt/java/ubzy/h5/xk/10/

sudo chown -R  root_html:root_html /opt/java/ubzy/h5/xk/10/
