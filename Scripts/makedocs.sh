#!/bin/sh

PWD=`pwd`

srcdir=$PWD/app/src
docdir=$HOME/jbproject/doc
jdkurl=file:///home/java/jdk1.3.0/docs/api

mkdir -p $docdir

classpath=$srcdir
for lib in $PWD/lib/*.jar ; do
    classpath=$classpath:$lib
done

pcks=`find $srcdir -name "*.java" -exec dirname {} \; | sort -u | sed "s!$srcdir/!!" | sed 'y!/!\.!'`

javadoc -classpath $classpath -d $docdir -version -private -author -link $jdkurl -sourcepath $srcdir $pcks
