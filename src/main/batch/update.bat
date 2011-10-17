@echo off

cls
call git remote update
call git checkout dev
call git status
pause

call git pull origin dev
call git checkout master
call git status
call git pull origin master
call git checkout dev

