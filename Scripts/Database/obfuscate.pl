#!/usr/bin/perl
 
while ( chomp($line=<STDIN>) ) {
    @words = split ' ', $line;
    if ( $words[2] eq 'patient' ) {
        $expr = '';
        for ( $i=4; $words[$i]; $i++ ) {
            $expr .= $words[$i]
        }
        @words = split ',', $expr;
        print "INSERT INTO patient (id,name) values ";
        print "$words[0],\'Name".$pc++."\');\n";
    }
    else {
        print "@words\n";
    }
}
