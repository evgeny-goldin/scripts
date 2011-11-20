#!/bin/bash

SCRIPTS_ROOT=`pwd`
GROOVY='groovy -Dgroovy.grape.report.downloads=true'
echo Root is [$SCRIPTS_ROOT]
echo Groovy is [$GROOVY]

echo Removing old files
rm -rf $SCRIPTS_ROOT/tests ~/.groovy

echo Cloning projects
git clone    git://github.com/evgeny-goldin/wiki.git    $SCRIPTS_ROOT/tests/wiki
svn checkout http://gmaps4jsf.googlecode.com/svn/trunk/ $SCRIPTS_ROOT/tests/gmaps4jsf
svn checkout http://svg-edit.googlecode.com/svn/trunk/  $SCRIPTS_ROOT/tests/svg-edit

cd $SCRIPTS_ROOT/src/main/groovy

echo --== Running links.groovy ==--
$GROOVY links.groovy $SCRIPTS_ROOT/tests/wiki "**/*.txt"

echo --== Running mvnOp.groovy ==--
$GROOVY mvnOp.groovy $SCRIPTS_ROOT/tests

echo --== Running svnOp.groovy ==--
$GROOVY svnOp.groovy $SCRIPTS_ROOT/tests

cd $SCRIPTS_ROOT/src/test/groovy

echo --== Running y2m.groovy ==--
$GROOVY y2m.groovy

echo Removing old files
rm -rf $SCRIPTS_ROOT/tests
