#!/bin/bash

# -----------------------------------------
# http://buildserver/guestAuth/repository/download/bt24/lastSuccessful/teamcity-ivy.xml
# -----------------------------------------

ideaProcess=`tasklist | grep idea.exe`

if [ "$ideaProcess" != "" ]; 
then 
    echo 'IDEA is running'
    exit
fi


url=http://buildserver/guestAuth/repository/download/bt24/lastSuccessful
artifactName="`curl $url/teamcity-ivy.xml | grep ideaIU | grep zip | grep -v teamcity | cut -d '"' -f 2`.zip"

rm -rf   idea
mkdir    idea
cd       idea
wget     "$url/$artifactName"
unzip    "$artifactName"
rm       "$artifactName"
cd       ..
rm -rf   C:/Winny/java/idea
mv       idea C:/Winny/java
