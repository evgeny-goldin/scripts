# http://renesto.blogspot.de/2013/07/exporting-skype-chat-using-shell-script.html

sqlite3 -batch "$HOME/Library/Application Support/Skype/$1/main.db" <<EOF
.mode csv
.output skype-contacts.csv
select * from Contacts;
.output stdout
.exit
EOF
echo "'skype-contacts.csv' is exported"
