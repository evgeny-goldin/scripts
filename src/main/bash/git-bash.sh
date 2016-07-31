#!/bin/bash
set +x

branch=''
mark=''

status=$(git status 2> /dev/null)
noBranchRegex1="Not currently on any branch"
noBranchRegex2="HEAD detached at"
divergedRegex="have diverged"
pushRegex="# Your branch is ahead of"
cleanRegex="nothing to commit, working tree clean"
main_branch="origin/master"

if [ "$status" != "" ];
then

    if [[ $status =~ $noBranchRegex1 ]] || [[ $status =~ $noBranchRegex2 ]];
    then
        branch="[$(git describe --all --tags --long)]"
    else
        branch=$(git branch | grep \* | awk '{print $2}')
    fi
    commit=$(git log -1 --format=format:%h)

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

    curr_branch=$(git rev-parse --abbrev-ref HEAD 2>&1);

    for br in $(git branch); do
      if [[ "$br" == "mainline" ]]; then
        main_branch="origin/mainline"
      fi
    done

    if [[ "$curr_branch" =~ "fatal: " ]]; then
      echo "<not initialized yet>"
      exit
    fi

    curr_remote=$(git config branch.$curr_branch.remote);
    curr_merge_branch=$(git config branch.$curr_branch.merge | sed 's|refs/heads/||');

    if [ "$curr_remote" == "" ] || [ "$curr_merge_branch" == "" ] ;
    then
        numbers=$(git rev-list --left-right --count $curr_branch...$main_branch 2>&1 | tr -s '\t' '|');
    else
        numbers=$(git rev-list --left-right --count $curr_branch...$curr_remote/$curr_merge_branch 2>&1 | tr -s '\t' '|');
    fi

    if [[ "$numbers" =~ "fatal: " ]]; then
      echo "<not initialized yet>"
    else
      echo "<$branch$mark>[$commit][$numbers]"
    fi
else
    echo ""
fi
