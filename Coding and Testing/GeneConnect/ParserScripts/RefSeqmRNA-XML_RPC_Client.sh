cd  ./ParserScripts
echo "Executing sequence alignment run RefseqmRNA-EnsemblTranscript...."
nohup perl gc_seqaln_client.pl -f $2 -p properties_seqaln_RefseqmRNA_EnsemblTranscript.txt