#!/bin/bash

CURL="curl -v"
if [[ -z "$1" ]]; then
	BASE_URL='http://localhost:9635'
else
	BASE_URL="$1"
fi
FILE_DIR=`dirname $0`
COOKIEJAR="$FILE_DIR/cookiejar.txt"

USERNAME='abdul'
PASSWORD='abdul'

rm $COOKIEJAR

$CURL -X POST -c $COOKIEJAR "$BASE_URL/login?username=$USERNAME&password=$PASSWORD"

TYPES="agents
collections
activities
services"

for t in $TYPES
do
	for f in `find $FILE_DIR -path "*/$t/*.atom"`
	do
		URL="$BASE_URL/$t"
		$CURL -X POST -H 'Content-Type: application/atom+xml' -b @$COOKIEJAR --data-binary @$f $URL
	done
done

$CURL -X POST -b @$COOKIEJAR "$BASE_URL/solr/dataimport?command=full-import"
