#!/bin/bash

set -e

echo '==================='
date
echo '==================='

result_file=~/Temp/whoer.txt

if [ "$(\ps -Af | grep -i torrent | grep -v grep | wc -l | awk '{print $1}')" == "0" ]; then
  echo '===> Torrents are not running'
else  
  echo '===> Torrents are running'
  rm -f "$result_file"
  wget --timeout=5 -nv http://whoer.net/ -O "$result_file"
  if [ "$(grep Comcast "$result_file" | wc -l | awk '{print $1}')" == "0" ]; then
    echo "===> Comcast not found in '$result_file'"  
  else
    echo "===> !!! Comcast found in '$result_file', turning the network off"
    /usr/sbin/scselect Off
  fi
fi
