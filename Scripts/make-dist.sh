#!/bin/sh

ROOT=/opt/mediknight2
JAVA=/opt/java/j2sdk1.3.1
DATABASE="jdbc:mysql://hardy:3306/mediknight"

DIST_PROPERTIES=Distribution/mediknight.properties.in
DIST_START_SCRIPT=Distribution/mediknight.sh.in
DIST_STAMMDATEN_SCRIPT=Distribution/stammdaten.sh.in
DIST_LOCKS_SCRIPT=Distribution/remove-locks.sh.in

DESTDIR=`pwd`/dist.$$${ROOT}

XML=projects/app/xml
XSD=projects/app/xsd

FILES="$DIST_PROPERTIES $DIST_START_SCRIPT $DIST_STAMMDATEN_SCRIPT $DIST_LOCKS_SCRIPT"

DIRS="$XML $XSD"

#
# Sanity checks...
#
for f in $FILES ; do
    if [ ! -f "$f" ]; then
        echo "Cannot find file '$f'. Please change to the project root." 1>&2
        exit 1
    fi
done

for d in $DIRS ; do
    if [ ! -d "$d" ]; then
        echo "Cannot find directory '$d'. Please change to the project root." 1>&2
        exit 1
    fi
done


#
# Process command line parameters...
#
while [ -n "$*" ]; do
    case $1 in
    --root*)
        shift
        ROOT="$1"
        ;;

    --java*)
        shift
        JAVA="$1"
        ;;

    --db*)
	shift
	DATABASE="$1"
	;;

    *)
	break
	;;

    esac
    shift
done


mkdir -p "$DESTDIR"

Scripts/makejar.sh "$DESTDIR"

cp -r "$XML" "$XSD" "$DESTDIR"

sed -e "s#@ROOT@#${ROOT}#" \
    -e "s#@DB@#${DATABASE}#" \
    -e "s#@JAVA@#${JAVA}#" < $DIST_START_SCRIPT > ${DESTDIR}/mediknight

sed -e "s#@ROOT@#${ROOT}#" \
    -e "s#@DB@#${DATABASE}#" \
    -e "s#@JAVA@#${JAVA}#" < $DIST_PROPERTIES > ${DESTDIR}/mediknight.properties

sed -e "s#@ROOT@#${ROOT}#" \
    -e "s#@DB@#${DATABASE}#" \
    -e "s#@JAVA@#${JAVA}#" < $DIST_STAMMDATEN_SCRIPT > ${DESTDIR}/stammdaten

sed -e "s#@ROOT@#${ROOT}#" \
    -e "s#@DB@#${DATABASE}#" \
    -e "s#@JAVA@#${JAVA}#" < $DIST_LOCKS_SCRIPT > ${DESTDIR}/remove-locks

chmod 755 ${DESTDIR}/{stammdaten,mediknight,remove-locks}

