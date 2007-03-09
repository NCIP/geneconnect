cd ParserScripts
echo "Executing sequence alignment run GenBankmRNA-EnsemblTranscript...."
perl gc_seqaln_client.pl -f %2 -p properties_seqaln_GenBankmRNA_EnsemblTranscript.txt
echo "Executing sequence alignment run GenBankmRNA-UniProtKB...."
perl gc_seqaln_client.pl -f %2 -p properties_seqaln_GenBankProtein_UniProtKB.txt