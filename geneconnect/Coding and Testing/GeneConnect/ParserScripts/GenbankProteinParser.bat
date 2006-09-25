@echo off
echo GeneBank Protein FASTA PARSER....
cd ParserScripts
perl GenbankProteinParser.pl %1 %2
cd..