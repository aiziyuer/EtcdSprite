#!/usr/bin/env bash

# mvn deploy:deploy-file \
#     -DgroupId=com.aiziyuer \
#     -DartifactId=com.aiziyuer.lombok \
#     -Dversion=1.16.12 \
#     -Dfile=com.aiziyuer.lombok-1.16.12.jar \
#     -Dsources=com.aiziyuer.lombok-1.16.12-sources.jar \
#     -Durl=file:../repo


files=`ls | grep 'lipse.core.databinding.beans-1.2.100.I20100824-0800.jar'`

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
		    -DgroupId=$groupId \
		    -DartifactId=$artifactId \
		    -Dversion=$version \
		    -Dfile=${file}.jar \
		    -Dsources=${file}-sources.jar \
		    -Durl=file:../repo
		"""

		echo $command && eval $command

    else
        echo "$f doesn't match" >&2 # this could get noisy if there are a lot of non-matching files
    fi
done