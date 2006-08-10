export JAVA_HOME=<Java home directory>
export ORACLE_HOME=<Oracle home directory>
export PATH=$ORACLE_HOME/bin:$JAVA_HOME/bin:$PATH
export CLASSPATH=.:Lib/GeneRanges.jar:$ORACLE_HOME/jdbc/lib/classes12.zip:\Lib\mysql-connector-java-3.1.10-bin.jar:$CLASSPATH
export LD_LIBRARY_PATH=$ORACLE_HOME/lib
#echo $CLASSPATH
#echo $LD_LIBRARY_PATH

# The last three parameters in the command below are to indicate if you want to run DataSynchronization, Fetch PFIDS and
# calculate correlation respectively. Using these parameters, these functions can be controlled individually.
java PrintGeneIdRange <noOfMachines: Integer> <database user> <database password> <database driver> <database URL> <database type (Oracle/Mysql)>
