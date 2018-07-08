#!/bin/bash

if [ ! -d "output" ]; then
mkdir output
#mkdir output/n
#mkdir output/r
#mkdir output/m
mkdir output/LZW
mkdir output/7z
mkdir output/diff
#mkdir output/diff/example_files
#mkdir output/diff/n
#mkdir output/diff/r
#mkdir output/diff/m
for i in example_files/* ; do
	extension="${i##*.}"
	if [ "$extension" != "txt" ]; then
		name="${i%.*}"
		xxd example_files/"$(basename $i)" output/diff/example_files/"$(basename $i)".hex
	fi
	done
fi

javac LZW.java
#javac MyLZW.java

for i in example_files/* ; do
	file=$(basename $i)
	extension="${file##*.}"
	name="${file%.*}"
	if [ "$file" == "all.tar" ]; then
		echo "Skipping $i takes way to long"
	else
		echo " "
		echo "Compressing $file in \"n\" mode"
		java MyLZW - n < example_files/$file > output/n/"$name"_n.lzw
		echo "Compressing $file in \"r\" mode"
		java MyLZW - r < example_files/$file > output/r/"$name"_r.lzw
		echo "Compressing $file in \"m\" mode"
		java MyLZW - m < example_files/$file > output/m/"$name"_m.lzw
		echo "Extracting $file from \"n\" mode"
		java MyLZW + < output/n/"$name"_n.lzw > output/n/$file
		echo "Extracting $file from \"r\" mode"
		java MyLZW + < output/r/"$name"_r.lzw > output/r/$file
		echo "Extracting $file from \"m\" mode"
		java MyLZW + < output/m/"$name"_m.lzw > output/m/$file
		echo "Comparing differences"
		echo " "
		echo "Compressing $file in \"n\" mode"
		java LZW - < example_files/$file > output/"$name".lzw


		

		
	fi
done

echo " "
echo "Cleaning up \".class\" files"
rm *.class

echo " "
echo "All differencet stored in output/diff/[n/r/m]/ "
echo "< denotes original file"
echo "> denotes my uncompressed file"
echo " "
