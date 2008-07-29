#!/bin/sh

echo $0 | grep '^/' >/dev/null 2>&1

if [ $? -eq 0 ]; then
	PROJECT_DIR=`dirname $0`/..
else
	PROJECT_DIR=`pwd`/`dirname $0`/..
fi
BOPRINT_DIR=$PROJECT_DIR/../BOprint2

TARGET_DIR=/tmp/mediknight.$$
JARCHIV=$TARGET_DIR/mediknight.jar
MANIFEST=$TARGET_DIR/mainclass.mf.$$
TMPDIR=/tmp/tmp.$$

mkdir -p $TMPDIR
mkdir -p $TARGET_DIR

echo "Building java archive $JARCHIV"

rm -f $JARCHIV
touch $JARCHIV

echo "Processing $CLASSDIR ..."

cp $PROJECT_DIR/Distribution/mediknight.properties $PROJECT_DIR/bin/de/bo/mediknight/resources
cp $PROJECT_DIR/src/de/bo/mediknight/resources/logo.gif $PROJECT_DIR/bin/de/bo/mediknight/resources
cd $PROJECT_DIR/bin
find . -name "*.class" -o \
       -name "*.properties" -o \
       -name "*.jpg" -o \
       -name "*.png" -o \
       -name "*.gif" | xargs jar uf $JARCHIV


cd $TMPDIR
for LIB in $PROJECT_DIR/lib/*.jar ; do
    echo "Extracting $LIB in $TMPDIR ..."
    jar xf $LIB
done
echo "Recreating ..."
jar uf $JARCHIV com hsqlServlet.class org de javax
cd $PROJECT_DIR

cd $BOPRINT_DIR/bin
find . -name "*.class" -o \
       -name "*.properties" -o \
       -name "*.jpg" -o \
       -name "*.gif" | xargs jar uvf $JARCHIV

cd $TARGET_DIR

echo "Main-Class: de.bo.mediknight.MainFrame" > $MANIFEST
jar umf $MANIFEST $JARCHIV

rm -f $MANIFEST

# Kopiere die Dateien, die nicht ins JAR kommen:
cd $TARGET_DIR
cp -r $PROJECT_DIR/Distribution/xml $PROJECT_DIR/Distribution/xsd $TARGET_DIR
mkdir -p $TARGET_DIR/de/bo/mediknight/properties
cp $PROJECT_DIR/Distribution/mediknight.properties $TARGET_DIR/de/bo/mediknight/properties
ln -s $TARGET_DIR/de/bo/mediknight/properties/mediknight.properties .

cd $TARGET_DIR
tar cf $TMPDIR/mediknight.tar .

