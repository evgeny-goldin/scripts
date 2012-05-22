echo  ========== [1] - Downloading ==========

rm -rf teamcity
mkdir  teamcity
cd     teamcity
wget   -nv ftp://ftp.intellij.net/pub/.teamcity/nightly/*.war
echo   "##teamcity[buildStatus status='`ls *.war | cut -f 1 -d '.'`']"

echo  ========== [2] - Unpacking ==========

unzip *.war
rm    *.war
cd    ..

echo  ========== [3] - Stopping Tomcat ==========

~/java/tomcat/bin/shutdown.sh
sleep 20
~/cleanup-tomcat.sh

echo  ========== [4] - Moving TeamCity ==========

rm -rf                            ~/download/old/teamcity
mkdir                             ~/download 
mkdir                             ~/download/old 
mv ~/java/tomcat/webapps/teamcity ~/download/old
mv teamcity                       ~/java/tomcat/webapps
rm -rf                            teamcity

echo  ========== [5] - Killing remaining Tomcat process ==========

kill `ps | grep java | grep org.apache.catalina.startup.Bootstrap | cut -f 3 -d ' '`

echo  ========== [6] - Starting Tomcat ==========

~/cleanup-tomcat.sh
~/java/tomcat/bin/startup.sh

echo  ========== [6] - Tomcat started, sleeping for 2 minutes ==========

sleep 120
free

echo  ========== [7] - Listing Tomcat log file ==========

cat ~/java/tomcat/logs/catalina.out
