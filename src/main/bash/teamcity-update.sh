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
