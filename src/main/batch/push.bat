@echo off

cls
call git status
pause

call git push origin %*


