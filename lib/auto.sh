#!/usr/bin/env bash

files=` ls | grep jar | grep -v sources.jar `

regex="(([a-z]+.[a-z]+).([^-]+)-(.*)).jar"

for f in $files
do
    if [[ $f =~ $regex ]]
    then
        file="${BASH_REMATCH[1]}"
        groupId="${BASH_REMATCH[2]}"
        artifactId="${groupId}.${BASH_REMATCH[3]}"
        version="${BASH_REMATCH[4]}"

		command="""
		mvn deploy:deploy-file \
		    -DgroupId=com.aiziyuer \
		    -DartifactId=$artifactId \
		    -Dversion=$version \
		    -Dfile=${file}.jar \
		    -Dsources=${file}-sources.jar \
		    -Durl=file:../repo
		"""

		echo $command && eval $command

    else
        echo "$f doesn't match" >&2 
    fi
done