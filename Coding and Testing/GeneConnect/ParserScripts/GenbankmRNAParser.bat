@echo off
echo GeneBank mRNA FASTA PARSER....
cd ParserScripts
perl GenbankmRNAParser.pl %1 %2
cd..