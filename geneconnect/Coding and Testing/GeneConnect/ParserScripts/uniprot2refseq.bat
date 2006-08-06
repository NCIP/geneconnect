@echo off
echo UNIPROT REFSEQ PARSER....
cd ParserScripts
perl uniprot_refseq.pl %1 %2
cd..