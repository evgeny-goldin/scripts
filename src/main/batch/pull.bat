@echo off

cls
call git status
pause

call git pull origin %*

