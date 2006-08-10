echo Starting GeneConnect Server....

#********** GeneConnect Application Base Directory ***********
export GC_HOME=/opt1/geneconnect/GeneConnectServer

# *********** Java home DIrectory *****************
export JAVA_HOME=/usr/bin/j2sdk1.4.2_12

#*********** Oracle Home DIrectory ***************
export ORACLE_HOME=/opt/oracle/920

#********* Perl Home Directory *****************
export PERL_HOME=/usr

#********* Blast Home Directory *****************
export BLAST_HOME=/home/washu/data_for_geneconnect/blast-2.2.13

# ******* Set PATH Variable ********************
export PATH=$PATH:$JAVA_HOME/bin:$ORACLE_HOME/bin:$BLAST_HOME/bin:$PERL_HOME/bin

# ****** Set CLASSPATH Variable **************
export CLASSPATH=./Lib/FEServer.jar:./Lib/ftp.jar:./Lib/caBIO.jar:./Lib/mail.jar:./Lib/smtp.jar:./Lib/activation.jar:$ORACLE_HOME/jdbc/lib/classes12.zip:./Lib/jakarta-regexp-1.3.jar:./Lib/jaxen-1.1-beta-7-SNAPSHOT.jar:./Lib/jdom.jar:./Lib/jdom-contrib.jar:./Lib/commons-pool-1.2.jar:./Lib/commons-collections-3.1.jar:./Lib/mysql-connector-java-3.1.10-bin.jar:./Lib/aspectjrt.jar:./Lib/caarray-client.jar:./Lib/log4j-1.2.5.jar:./Lib/mageom-client.jar:./Lib/nciData.jar:./Lib/netcdfAll.jar:./Lib/xercesImpl.jar:./Lib/xml-apis.jar
export LD_LIBRARY_PATH=$ORACLE_HOME/lib
echo $CLASSPATH

#********** Execute GeneConnect Server *************
java  -Xmx512M  -DentityExpansionLimit=5000000 -Djava.security.manager -Djava.security.policy=java.policy com.dataminer.server.jobmanager.JobManager