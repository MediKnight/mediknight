#!/bin/sh

VM=/opt/java/j2sdk/bin/java
PWD=`pwd`
 
cd `dirname $0`
$VM -jar mediknight.jar
cd $PWD
