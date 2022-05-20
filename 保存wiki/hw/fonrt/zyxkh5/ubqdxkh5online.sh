#!/usr/bin/env bash
#function:  dz h5 到线上环境
#parmeter:  
#author: 千峰 
#version: 1.0 
#Test: CentOS 6 


scp  -P 22  -i /mnt/forgitpublish/ubqdxkh5/id_rsa  /mnt/forgitpublish/ubqdxkh5/foronline/xkh5.tar.gz   forpubuse@172.16.12.243:/mnt/forgitpublish/qdykh5/online/

ssh forpubuse@172.16.12.243 -p 22 -i /mnt/forgitpublish/ubqdxkh5/id_rsa  -C "sh /mnt/forgitpublish/qdykh5/publishtoonline.sh"


rm -f /mnt/forgitpublish/ubqdxkh5/foronline/xkh5.tar.gz 



/usr/bin/python  /mnt/forgitpublish/ddsendmsg/lcCMS.pyc  xkh5 online 10

