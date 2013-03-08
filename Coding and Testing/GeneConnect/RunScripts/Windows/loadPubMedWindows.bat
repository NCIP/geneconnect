set PATH=d:\j2sdk1.4.2_06\bin;C:\oracle\ora92\bin;C:\oracle\ora92\lib;%PATH%;
set CLASSPATH=C:\oracle\ora92\jdbc\lib\classes12.zip;.\Lib\PubMed.jar;.\Lib\mysql-connector-java-3.1.10-bin.jar;%CLASSPATH%;

java -Xmx512M com.dataminer.pubmed.PubMedManager
