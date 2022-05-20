#!/usr/bin/env bash
#function:  dz h5 到预发布环境
#parmeter:  
#author: 千峰 
#version: 1.0 
#Test: CentOS 6 


scp  -P 22  -i /mnt/forgitpublish/ubqdxkh5/id_rsa  /mnt/forgitpublish/ubqdxkh5/forpre/xkh5.tar.gz   forpubuse@172.16.12.243:/mnt/forgitpublish/qdykh5/pre/

ssh forpubuse@172.16.12.243 -p 22 -i /mnt/forgitpublish/ubqdxkh5/id_rsa  -C "sh /mnt/forgitpublish/qdykh5/publishtopre.sh"

rm -f /mnt/forgitpublish/ubqdxkh5/forpre/xkh5.tar.gz 

/usr/bin/python  /mnt/forgitpublish/ddsendmsg/lcCMS.pyc  xkh5 pre 10

