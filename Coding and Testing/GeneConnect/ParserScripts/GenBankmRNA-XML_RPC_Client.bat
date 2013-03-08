cd ParserScripts
echo "Executing sequence alignment run GenBankProtein-EnsemblProtein...."
perl gc_seqaln_client.pl -f %2 -p properties_seqaln_GenBankProtein_EnsemblProtein.txt
echo "Executing sequence alignment run GenBankProtein-RefSeqProtein...."
perl gc_seqaln_client.pl -f %2 -p properties_seqaln_GenBankProtein_RefSeqProtein.txt
echo "Executing sequence alignment run GenBankProtein-UniProtKB...."
perl gc_seqaln_client.pl -f %2 -p properties_seqaln_GenBankProtein_UniProtKB.txt
