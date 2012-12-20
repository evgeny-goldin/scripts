#!/bin/bash

branch=''
mark=''

status=`git status 2> /dev/null`
pushRegex="# Your branch is ahead of"
cleanRegex="nothing to commit, working directory clean$"

if [ "$status" != "" ];
then
    branch=`echo $status | head -1 | awk '{print $4}'`
    
    if [[ $status =~ $pushRegex ]]; 
    then
        mark="$mark^"
    fi

    if [[ ! $status =~ $cleanRegex ]]; 
    then
        mark="$mark!"
    fi

    echo "<$branch$mark>"
else
    echo ""
fi
