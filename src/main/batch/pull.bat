@echo off

cls
call git status
pause

pause git pull origin %*

