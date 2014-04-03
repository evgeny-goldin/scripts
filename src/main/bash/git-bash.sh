#!/bin/bash

branch=''
mark=''

status=`git status 2> /dev/null`
noBranchRegex1="Not currently on any branch"
noBranchRegex2="HEAD detached at"
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
        branch=`echo $status | head -1 | awk '{print $3}'`
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

    # https://blogs.atlassian.com/2013/07/git-upstreams-forks/

    curr_branch=$(git rev-parse --abbrev-ref HEAD);
    curr_remote=$(git config branch.$curr_branch.remote);
    curr_merge_branch=$(git config branch.$curr_branch.merge | cut -d / -f 3);

    if [ "$curr_remote" == "" ] || [ "$curr_merge_branch" == "" ] ;
    then
        numbers=`git rev-list --left-right --count $curr_branch...origin/master | tr -s '\t' '|'`;
    else
        numbers=`git rev-list --left-right --count $curr_branch...$curr_remote/$curr_merge_branch | tr -s '\t' '|'`;
    fi    

    echo "<$branch$mark>[$numbers]"
else
    echo ""
fi
