#!/bin/bash

branch=''
mark=''

status=`git status 2> /dev/null`
regex="nothing to commit, working directory clean$"

if [ "$status" != "" ];
then
    branch=`echo $status | head -1 | awk '{print $4}'`
    if [[ ! $status =~ $regex ]]; 
    then
        mark='!'    
    fi
    echo "<$branch$mark>"
else
    echo ""
fi
