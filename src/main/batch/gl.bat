@echo off
git log --format=format:[%%h]-[%%cn]-[%%cr]-[%%s] -10 %*

