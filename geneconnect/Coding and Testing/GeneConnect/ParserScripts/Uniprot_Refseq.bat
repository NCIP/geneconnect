@echo off
echo UNIPROT REFSEQ PARSER....
cd ParserScripts
perl Uniprot_Refseq.pl %1 %2
cd..