#/bin/bash -e

for i in {0..99};
do

x=$(printf '1%3s' "$i" | tr ' ' '0')
echo "$x"
curl -s http://localhost:8080/sleep/$x | jq &

done