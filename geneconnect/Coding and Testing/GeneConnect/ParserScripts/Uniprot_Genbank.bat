@echo off
echo UNIPROT GENEBANK PARSER....
cd ParserScripts
perl Uniprot_Genbank.pl %1 %2
cd..