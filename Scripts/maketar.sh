#!/bin/sh

TMPDIR=tmp.$$

mkdir -p ${TMPDIR}/mediknight2
./makejar.sh

cp mediknight.jar ${TMPDIR}/mediknight2
cp mediknight.sh ${TMPDIR}/mediknight2/mediknight
cp stammdaten.sh ${TMPDIR}/mediknight2/stammdaten
cp remove-locks.sh ${TMPDIR}/mediknight2/remove-locks
cp mediknight.properties ${TMPDIR}/mediknight2
cp -r app/xml app/xsd ${TMPDIR}/mediknight2

chmod 755 ${TMPDIR}/mediknight2/mediknight
chmod 755 ${TMPDIR}/mediknight2/stammdaten
chmod 755 ${TMPDIR}/mediknight2/remove-locks
chmod 644 ${TMPDIR}/mediknight2/mediknight.properties
chmod 644 ${TMPDIR}/mediknight2/mediknight.jar

find ${TMPDIR}/mediknight2/xml ${TMPDIR}/mediknight2/xsd -type f | xargs chmod 644
find ${TMPDIR}/mediknight2/xml ${TMPDIR}/mediknight2/xsd -type d | xargs chmod 755

cd ${TMPDIR}
tar cf - mediknight2 | gzip > ../mediknight2.tar.gz

cd ..

rm -rf ${TMPDIR}

