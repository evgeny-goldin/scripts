#!/bin/bash

branch=''
mark=''

status=`git status 2> /dev/null`
noBranchRegex1="# Not currently on any branch"
noBranchRegex2="# HEAD detached at"
divergedRegex="have diverged"
pushRegex="# Your branch is ahead of"
cleanRegex="nothing to commit, working directory clean$"

if [ "$status" != "" ];
then
    
    if [[ $status =~ $noBranchRegex1 ]] || [[ $status =~ $noBranchRegex2 ]]; 
    then
        # [`git rev-parse --short --verify HEAD`] shows current commit
        branch="[`git describe --all --tags --long`]"
    else    
        branch=`echo $status | head -1 | awk '{print $4}'`
    fi
    
    if [[ $status =~ $divergedRegex ]]; 
    then
        mark="$mark?"
    fi

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
