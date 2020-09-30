#!/bin/bash

javac Solution.java

for IN in ./data/*.in
do
  OUT="${IN%.*}"
  SOL="$(cat $OUT.ans)"
  RES="$(java Solution < $IN)"
  echo $IN
	echo $RES $SOL
  echo
done
