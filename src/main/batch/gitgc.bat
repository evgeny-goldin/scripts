@echo off
call git reflog expire --expire=1.minute refs/heads/master
call git fsck --unreachable --strict
call git prune
call git gc
