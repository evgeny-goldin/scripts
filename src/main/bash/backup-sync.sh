#!/bin/bash

set -e
set -o pipefail
set -x

dropbox=/Users/evgenyg/Dropbox
data=/Volumes/Data
backup=/Volumes/Backup

if [ -d "$dropbox" -a -d "$data" -a -d "$backup" ]; then
    rm    -rf "$dropbox/.dropbox.cache"
    rsync -rv "$dropbox"         "$backup"
    rsync -rv "$data/Books"      "$backup"
    rsync -rv "$data/Tr/TheBest" "$backup/Tr"
else
    echo Not all folders are available
    echo $dropbox
    echo $data
    echo $backup
fi
