echo  $CLASSPATH
java -Xmx512M -DentityExpansionLimit=5000000 -Djava.security.manager -Djava.security.policy=java.policy edu.wustl.geneconnect.parser.UniGeneParser $1 $2