cd  ./ParserScripts
echo "Executing sequence alignment run GenBankmRNA-EnsemblTranscript...."
nohup perl gc_seqaln_client.pl -f $2 -p properties_seqaln_GenBankmRNA_EnsemblTranscript.txt
echo "Executing sequence alignment run GenBankmRNA-UniProtKB...."
nohup perl gc_seqaln_client.pl -f $2 -p properties_seqaln_GenBankProtein_UniProtKB.txt