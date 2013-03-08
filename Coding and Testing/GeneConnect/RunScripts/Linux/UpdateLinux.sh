echo Starting GeneConnect Server....

#********** GeneConnect Application Base Directory ***********
GC_HOME=/opt1/geneconnect/GeneConnectServer
export GC_HOME

# *********** Java home DIrectory *****************
JAVA_HOME=/usr/bin/j2sdk1.4.2_12
export JAVA_HOME

#*********** Oracle Home DIrectory ***************
ORACLE_HOME=/opt/oracle/920
export ORACLE_HOME

#********* Perl Home Directory *****************
PERL_HOME=/usr
export PERL_HOME

#********* Blast Home Directory *****************
BLAST_HOME=/home/washu/data_for_geneconnect/blast-2.2.13
export BLAST_HOME

# ******* Set PATH Variable ********************
PATH=$PATH:$JAVA_HOME/bin:$ORACLE_HOME/bin:$BLAST_HOME/bin:$PERL_HOME/bin
export PATH

# ****** Set CLASSPATH Variable **************
CLASSPATH=./Lib/GeneConnectServer.jar:./Lib/ftp.jar:./Lib/caBIO.jar:./Lib/mail.jar:./Lib/smtp.jar:./Lib/activation.jar:$ORACLE_HOME/jdbc/lib/classes12.zip:./Lib/jakarta-regexp-1.3.jar:./Lib/jaxen-1.1-beta-7-SNAPSHOT.jar:./Lib/jdom.jar:./Lib/jdom-contrib.jar:./Lib/commons-pool-1.2.jar:./Lib/commons-collections-3.1.jar:./Lib/mysql-connector-java-3.1.10-bin.jar:./Lib/aspectjrt.jar:./Lib/caarray-client.jar:./Lib/log4j-1.2.5.jar:./Lib/mageom-client.jar:./Lib/nciData.jar:./Lib/netcdfAll.jar:./Lib/xercesImpl.jar:./Lib/xml-apis.jar:./Lib/commons-collections-3.2.jar:./Lib/geneconnectcaCore-client.jar

export CLASSPATH

LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$ORACLE_HOME/lib
export LD_LIBRARY_PATH


# ***** Copy Perl modules to the perl libraries *******
mkdir $PERL_HOME/site/lib/auto/Config
mkdir $PERL_HOME/site/lib/auto/Config/Simple
cp $GC_HOME/ParserScripts/auto/Config/Simple/*.* $PERL_HOME/site/lib/auto/Config/Simple

mkdir $PERL_HOME/site/lib/Config
cp $GC_HOME/ParserScripts/Config/*.* $PERL_HOME/site/lib/Config
# *****************************************************


#********** Execute GeneConnect Server *************
java  -Xmx1800M  -DentityExpansionLimit=5000000 -Djava.security.manager -Djava.security.policy=java.policy com.dataminer.server.jobmanager.JobManager