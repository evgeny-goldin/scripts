@echo off

cls
call git checkout dev
call git status
pause

call git rebase master
call git checkout master
call git status
call git merge dev
call git push origin master
call git push origin dev
call git checkout dev
