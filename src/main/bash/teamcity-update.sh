#!/bin/bash

# -----------------------------------------
# Remember!
# - teamcity.startup.maintenance=false
# http://goo.gl/n0jmV
# -----------------------------------------

set -e
set -o pipefail

echo --------------------------------------
echo Updating TeamCity     : [$TeamCityUrl]
echo Updating Tomcat       : [$tomcat]
echo Saving old version in : [$backup]
echo --------------------------------------

echo  ========== Downloading ==========
echo   "##teamcity[progressMessage 'Downloading ..']"

rm -rf teamcity
mkdir  teamcity
cd     teamcity
wget   -nv ftp://anonymous@ftp.intellij.net/pub/.teamcity/nightly/8.0.x/*.war

# Both new and old builds are of form "TeamCity-23361"
oldBuild="TeamCity-`curl $TeamCityUrl/app/rest/server/build?guest=1`"
newBuild="`ls *.war | cut -f 1 -d '.'`"


if [ "$oldBuild" = "$newBuild" ];
then
    rm -rf teamcity
    echo  "##teamcity[buildStatus status='SUCCESS' text='|[$newBuild|] is already installed']"
    exit
else
    echo  "##teamcity[progressMessage 'Updating |[$oldBuild|] to |[$newBuild|]']"
fi


echo  ========== Unpacking ==========

unzip *.war
rm    *.war
cd    ..

echo  ========== Stopping Tomcat ==========

$tomcat/bin/shutdown.sh

sleep 30

rm -rf $tomcat/logs $tomcat/temp $tomcat/work
mkdir  $tomcat/logs $tomcat/temp $tomcat/work


echo  ========== Moving TeamCity ==========

rm    -rf                   $backup/teamcity
mkdir -p                    $backup
mv $tomcat/webapps/teamcity $backup
mv teamcity                 $tomcat/webapps

echo  ========== Killing remaining Tomcat processes ==========

echo "Tomcat processes before:"
echo [`ps -Af | grep java | grep org.apache.catalina.startup.Bootstrap`]
set +e

ps -Af | grep java | grep org.apache.catalina.startup.Bootstrap | awk '{print $2}' | while read pid;
do 
    echo "kill $pid"
    kill $pid

    sleep 10

    echo "kill -9 $pid"
    kill -9 $pid
done

set -e
echo "Tomcat processes after:"
echo [`ps -Af | grep java | grep org.apache.catalina.startup.Bootstrap`]

echo  ========== Starting Tomcat ==========

$tomcat/bin/startup.sh

echo  ========== Tomcat started, sleeping for 2 minutes ==========

sleep 120

echo  ========== Listing [$tomcat/logs/catalina.out] ==========

cat $tomcat/logs/catalina.out

echo  ========== Listing [$tomcat/logs/teamcity-server.log] ==========

cat $tomcat/logs/teamcity-server.log

echo  ========== Listing memory and disk usage ==========

echo -------
echo Memory:
echo -------

free -lt

echo -----
echo Disk:
echo -----

df -h .

echo  ========== Done! Updated to [`curl $TeamCityUrl/app/rest/server/version?guest=1`] ==========
echo  "##teamcity[buildStatus status='SUCCESS' text='Updated to |[$newBuild|]']"
