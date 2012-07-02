#!/bin/bash

# -----------------------------------------
# http://buildserver/guestAuth/repository/download/bt457/lastSuccessful/teamcity-ivy.xml
# -----------------------------------------

tcProcess=`tasklist | grep tomcat7.exe`

if [ "$tcProcess" != "" ]; 
then 
    echo 'TeamCity is running'
    exit
fi


url=http://buildserver/guestAuth/repository/download/bt457/lastSuccessful
artifactName="`curl $url/teamcity-ivy.xml | grep war | cut -d '"' -f 2`.war"

rm -rf   bs
mkdir    bs
cd       bs
wget     "$url/$artifactName"
unzip    "$artifactName"
rm       "$artifactName"
cd       ..
rm -rf   c:/Winny/java/TeamCity/webapps/bs
mv       bs c:/Winny/java/TeamCity/webapps
