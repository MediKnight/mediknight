#!/bin/sh

if [ -z "$1" ]; then
    echo 2>&1 "Usage: makejar.sh destination-dir"
    exit 1
fi


CURRENT_DIR=`pwd`
JARCHIV=${1}/mediknight.jar
MANIFEST=${CURRENT_DIR}/mainclass.mf.$$
CLASSDIR=bin
TMPDIR=tmp.$$

echo "Building java archive $JARCHIV"

rm -f $JARCHIV
touch $JARCHIV

echo "Processing $CLASSDIR ..."

cd $CLASSDIR
find . -name "*.class" -o \
       -name "*.properties" -o \
       -name "*.jpg" -o \
       -name "*.png" -o \
       -name "*.gif" | xargs jar uf $JARCHIV

cd $CURRENT_DIR

mkdir -p $TMPDIR
cd $TMPDIR
for LIB in ${CURRENT_DIR}/projects/lib/*.jar ; do
    echo "Extracting $LIB in $TMPDIR ..."
    jar xf $LIB
done

echo "Recreating ..."
jar uf $JARCHIV *

cd ..
rm -rf $TMPDIR

echo "Main-Class: de.bo.mediknight.MainFrame" > $MANIFEST
jar umf $MANIFEST $JARCHIV

rm -f $MANIFEST
