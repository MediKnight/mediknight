#!/bin/sh

TMPDIR=CD.$$

mkdir -p $TMPDIR

./maketar.sh

cp mediknight2.tar.gz \
   ../database/import-vette/alter-struct.sql \
   $TMPDIR

