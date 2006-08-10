export ORACLE_HOME=<Oracle base directory>
export JAVA_HOME=<Java home directory>
export PATH=$ORACLE_HOME/bin:$JAVA_HOME/bin:$PATH
export CLASSPATH=.:Lib/FEServer.jar:Lib/mysql-connector-java-3.1.10-bin.jar:$ORACLE_HOME/jdbc/lib/classes12.zip:$CLASSPATH
export LD_LIBRARY_PATH=$ORACLE_HOME/lib
#echo $CLASSPATH
#echo $LD_LIBRARY_PATH

# The last three parameters in the command below are to indicate if you want to run DataSynchronization, Fetch PFIDS and
# calculate correlation respectively. Using these parameters, these functions can be controlled individually.
java -Xmx512M com.dataminer.pubmed.PubMedManager
