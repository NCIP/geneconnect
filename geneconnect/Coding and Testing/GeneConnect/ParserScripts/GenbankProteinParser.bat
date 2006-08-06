@echo off
echo GeneBank Protein FASTA PARSER....
cd ParserScripts
perl gbankprotein.pl %1 %2
cd..