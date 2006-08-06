@echo off
echo UNIPROT GENEBANK PARSER....
cd ParserScripts
perl uniprot_genbank.pl %1 %2
cd..