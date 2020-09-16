while IFS= read -r line
do
  echo "$line"
done < "$2" | java "$1"
