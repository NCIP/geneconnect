@echo off
echo REFSEQ PARSER....
cd ParserScripts
perl Entrez_Refseq.pl %1 %2
cd..