export JAVA_HOME=<Java home directory>
export ORACLE_HOME=<Oracle home directory>
export PATH=$ORACLE_HOME/bin:$JAVA_HOME/bin:$PATH
export CLASSPATH=.:Lib/CorrelationRanges.jar:$ORACLE_HOME/jdbc/lib/classes12.zip:\Lib\mysql-connector-java-3.1.10-bin.jar:$CLASSPATH



java PrintCorrGeneRange <noOfMachines: Integer> <database user> <database password> <database driver> <database URL> <database type (Oracle/Mysql)>
