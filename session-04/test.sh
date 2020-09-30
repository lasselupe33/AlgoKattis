#!/bin/bash
javac Solution.java
if [ "$1" ]
then
  IN="$1"
  OUT="${IN%.*}"
  SOL="$(cat $OUT.ans)"
  RES="$(java Solution < $IN)"
  echo $IN
  echo $RES $SOL
else
  for IN in ./data/*.in
  do
    OUT="${IN%.*}"
    SOL="$(cat $OUT.ans)"
    RES="$(java Solution < $IN)"
    echo $IN
    echo $RES $SOL
    echo
  done
fi
