export ORACLE_BASE=<Oracle base directory>
export JAVA_HOME=<Java home directory>
export ORACLE_HOME=<Oracle home directory>
export ORACLE_SID=<database sid>
export ORA_NLS33=$ORACLE_HOME/ocommon/nls/admin/data
export PATH=$ORACLE_HOME/bin:$JAVA_HOME/bin:$PATH
export CLASSPATH=./Lib/GeneConnectServer.jar:./Lib/ftp.jar:./Lib/caBIO.jar:./Lib/mail.jar:./Lib/smtp.jar:./Lib/activation.jar:$ORACLE_HOME/jdbc/lib/classes12.zip:./Lib/jakarta-regexp-1.3.jar:./Lib/jaxen-1.1-beta-7-SNAPSHOT.jar:./Lib/jdom.jar:./Lib/jdom-contrib.jar:./Lib/commons-pool-1.2.jar:./Lib/commons-collections-3.1.jar:./Lib/mysql-connector-java-3.1.10-bin.jar:./Lib/aspectjrt.jar:./Lib/caarray-client.jar:./Lib/log4j-1.2.5.jar:./Lib/mageom-client.jar:./Lib/nciData.jar:./Lib/netcdfAll.jar:./Lib/xercesImpl.jar:./Lib/xml-apis.jar
export LD_LIBRARY_PATH=$ORACLE_HOME/lib
#echo $CLASSPATH
#echo $LD_LIBRARY_PATH

#If a machine has Fedora Core 4 with JDK 1.4.2 and if operating system is using IPV6 protocol, then it may give IOException #while connecting to a FTP URL. In that case set -Djava.net.preferIPv4Stack=true in the script.

java  -Xmx512M  -DentityExpansionLimit=5000000 -Djava.security.manager  -Djava.security.policy=java.policy com.dataminer.server.jobmanager.JobManager
