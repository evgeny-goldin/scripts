#!/bin/bash

# -----------------------------------------
# Things to remember:
# - teamcity.startup.maintenance=false
# - Backup
# -----------------------------------------

set -e
set -o pipefail

url=http://buildserver/guestAuth/repository/download/bt24/lastSuccessful
artifactName="`curl $url/teamcity-ivy.xml | grep '.win' | cut -f 2 -d '"'`.zip"
wget "$url/$artifactName"
