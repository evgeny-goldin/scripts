#!/bin/bash

set -e
set -o pipefail
scp -r bu@176.9.56.7:/home/backup/tm-backups/latest ~/Dropbox/Backup/data/trademob
