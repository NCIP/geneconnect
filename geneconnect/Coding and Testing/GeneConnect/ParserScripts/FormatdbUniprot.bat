@echo off
echo UNIPROT DESTINATION FASTA PARSER....
cd ParserScripts
perl FormatdbUniprot.pl %1 %2
cd..