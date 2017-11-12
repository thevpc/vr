#!/bin/sh
MVN=/home/vpc/data-vpc/Programs/apache-maven-3.5.2/bin/mvn
HERE=`pwd`
cd vr-plugin-base-archetype
$MVN archetype:create-from-project 
cd target/generated-sources/archetype/
$MVN archetype:create-from-project 
$MVN install
cd $HERE

rm -Rf vr-plugin-archetype/pom.xml
rm -Rf vr-plugin-archetype/src
cp -R vr-plugin-base-archetype/target/generated-sources/archetype/* vr-plugin-archetype


