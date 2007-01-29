cd ParserScripts
echo "Executing sequence alignment run UniProtKB-EnsemblProtein...."
nohup perl gc_seqaln_client.pl -f $2 -p properties_seqaln_UniProtKB_EnsemblProtein.txt

