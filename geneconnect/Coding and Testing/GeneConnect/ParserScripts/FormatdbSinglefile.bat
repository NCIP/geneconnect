@echo off
echo BLAST SINGLE FILE DESTINATION PARSER....
cd ParserScripts
perl FormatSinglefile.pl %1 %2
cd..