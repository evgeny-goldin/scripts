@echo off

cls
e:

echo "=========> gcommons"
cd \projects\gcommons
call update
call merge

echo "=========> maven-plugins"
cd \projects\maven-plugins
call update
call merge

echo "=========> scripts"
cd \projects\scripts
call git status
call git pull origin master
call git push origin master

echo "=========> maven-plugins-test"
cd \projects\maven-plugins-test
call git status
call git pull origin master
call git push origin master
