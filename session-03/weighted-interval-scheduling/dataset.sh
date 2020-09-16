#!/bin/bash
filebase=./data/"$1"

touch "$filebase".in
echo "$1" > "$filebase".in

MAX=1000000000

counter=1
while [ $counter -le "$1" ]
do
  FMAX=($MAX - 1)
  S=$(jot -r 1 0 "$FMAX")
  E=$(jot -r 1 "$S" "$MAX")
  W=$(jot -r 1 1 "$FMAX")
  echo $S $E $W
  ((counter++))
done >> "$filebase".in

touch "$filebase".out
java Solution < "$filebase".in > "$filebase".out