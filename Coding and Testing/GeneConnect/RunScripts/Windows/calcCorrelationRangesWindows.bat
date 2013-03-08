set JAVA_HOME=<Java home directory>
set ORACLE_HOME=<Oracle home directory>

set PATH=%JAVA_HOME%\bin;%ORACLE_HOME%\bin;%ORACLE_HOME%\ora92\lib;%PATH%
set CLASSPATH=%ORACLE_HOME%\jdbc\lib\classes12.zip;.\Lib\CorrelationRanges.jar;.\Lib\mysql-connector-java-3.1.10-bin.jar;%CLASSPATH%;

java PrintCorrGeneRange <noOfMachines: Integer> <database user> <database password> <database driver> <database URL> <database type (Oracle/Mysql)>