@echo off

cls
call git status
echo Are you sure you want to PUSH?! You won't be able to amend or rebase your commits!
pause

call git push origin %*
