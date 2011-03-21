@echo off

call git checkout dev
call git status
call git pull origin dev
call git checkout master
call git status
call git pull origin master
call git checkout dev

