echo  ========== [1] - downloading TeamCity ==========

rm -rf teamcity
mkdir  teamcity
cd     teamcity
wget   -nv ftp://ftp.intellij.net/pub/.teamcity/nightly/*.war

echo  ========== [2] - done, unpacking ==========

unzip *.war
rm    *.war
cd    ..

echo  ========== [3] - done, stopping Tomcat ==========

~/java/tomcat/bin/shutdown.sh
sleep 20
~/cleanup-tomcat.sh

echo  ========== [4] - done, moving TeamCity ==========

rm -rf                            ~/download/old/teamcity
mkdir                             ~/download 
mkdir                             ~/download/old 
mv ~/java/tomcat/webapps/teamcity ~/download/old
mv teamcity                       ~/java/tomcat/webapps
rm -rf                            teamcity

echo  ========== [5] - done, killing remaining Tomcat process ==========

kill `ps | grep java | grep org.apache.catalina.startup.Bootstrap | cut -f 3 -d ' '`

echo  ========== [6] - done, starting Tomcat ==========

~/cleanup-tomcat.sh
~/java/tomcat/bin/startup.sh

echo  ========== [6] - done, Tomcat started! ==========

sleep 60
free

echo  ========== [7] - listing Tomcat log file ==========

cat ~/java/tomcat/logs/catalina.out
