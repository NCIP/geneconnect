

#export

export CLASSPATH=./Lib/FEServer.jar:./Lib/ftp.jar:./Lib/caBIO.jar:./Lib/mail.jar:./Lib/smtp.jar:./Lib/activation.jar:$ORACLE_HOME/jdbc/lib/classes12.zip:./Lib/jakarta-regexp-1.3.jar:./Lib/jaxen-1.1-beta-7-SNAPSHOT.jar:./Lib/jdom.jar:./Lib/jdom-contrib.jar:./Lib/commons-pool-1.2.jar:./Lib/commons-collections-3.1.jar:./Lib/mysql-connector-java-3.1.10-bin.jar:./Lib/aspectjrt.jar:./Lib/caarray-client.jar:./Lib/log4j-1.2.5.jar:./Lib/mageom-client.jar:./Lib/nciData.jar:./Lib/netcdfAll.jar:./Lib/xercesImpl.jar:./Lib/xml-apis.jar

echo Starting UniGene Parser ...

java -Xmx512M -DentityExpansionLimit=5000000 -Djava.security.manager -Djava.security.policy=java.policy edu.wustl.geneconnect.parser.UniGeneParser $1 $2
