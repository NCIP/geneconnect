@echo off
echo REFSEQ PARSER....
cd ParserScripts
perl entrez_refseq.pl %1 %2
cd..