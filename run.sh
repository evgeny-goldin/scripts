#!/bin/bash

echo Validating MediaWiki links
rm -rf wiki
git clone git://github.com/evgeny-goldin/wiki.git
groovy src/main/groovy/links.groovy wiki "**/*.txt"

echo Running y2m tests
cd src/test/groovy
groovy y2m.groovy
