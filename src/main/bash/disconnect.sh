#!/bin/bash

# set -x
set -e

echo '==================='
date
echo '==================='

result_file=~/Temp/whoer.txt

if [ "$(\ps -Af | grep pia_manager | grep -v grep | grep -v grep | wc -l | awk '{print $1}')" == "0" ]; then
  echo '===> PIA Manager is not running'
else  
  echo '===> PIA Manager is running'
  rm -f "$result_file"
  wget --timeout=5 -nv http://whoer.net/ -O "$result_file"
  if [ "$(grep Comcast "$result_file" | wc -l | awk '{print $1}')" == "0" ]; then
    echo "===> Comcast not found in '$result_file'"  
  else
    echo "===> Comcast found in '$result_file'! Turning network off"  
    /usr/sbin/scselect Off
  fi
fi