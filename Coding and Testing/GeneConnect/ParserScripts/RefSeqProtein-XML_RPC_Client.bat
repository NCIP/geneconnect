cd ParserScripts
echo "Executing sequence alignment run RefSeqProtein-EnsemblProtein...."
perl gc_seqaln_client.pl -f %2 -p properties_seqaln_RefSeqProtein_EnsemblProtein.txt
echo "Executing sequence alignment run RefSeqProtein-UniProtKB...."
perl gc_seqaln_client.pl -f %2 -p properties_seqaln_RefSeqProtein_UniProtKB.txt
