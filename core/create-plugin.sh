#!/bin/sh
MVN=/home/vpc/data-vpc/Programs/apache-maven-3.5.2/bin/mvn
HERE=$1
shift
groupId=$1
artifactId=$2
archetypeVersion=$3

if [ N$groupId == N ] ; then 
    groupId=test.exampleplugin
fi

if [ N$artifactId == N ] ; then 
    artifactId=my-example-plugin
fi

if [ N$archetypeVersion == N ] ; then 
    archetypeVersion=1.0.0
fi

#groupId=test.machin
#artifactId=my-test
#archetypeVersion=1.0.0

#$MVN archetype:generate -DarchetypeGroupId=net.vpc.app.vain-ruling.core -DarchetypeArtifactId=vr-sample-plugin-archetype  -DarchetypeVersion=1.0  -DgroupId=$groupId -DartifactId=$artifactId
$MVN archetype:generate -DarchetypeGroupId=net.vpc.app.vain-ruling.core -DarchetypeArtifactId=vr-sample-plugin-archetype $*

