@echo off

cls
e:

echo "=========> gcommons"
cd \projects\gcommons
call update
call gr ideaModule
call merge

echo "=========> maven-plugins"
cd \projects\maven-plugins
call update
call merge

echo "=========> scripts"
cd \projects\scripts
call pull master
call push master

echo "=========> maven-plugins-test"
cd \projects\maven-plugins-test
call pull master
call push master
