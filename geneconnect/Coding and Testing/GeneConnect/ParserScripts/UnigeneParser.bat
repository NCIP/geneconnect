@echo off
set GC_HOME=D:/Washu_CVS/GeneConnect/GeneConnectServer
set JAVA_HOME=C:/jdk1.4.2_04
set ORACLE_HOME=E:\oracle\product\10.1.0\db_1
rem set PATH=%JAVA_HOME%/bin;%PATH%
set CLASSPATH=%GC_HOME%/Lib/FEServer.jar;%GC_HOME%/Lib/caBIO.jar;%GC_HOME%/Lib/commons-collections-3.1.jar;%GC_HOME%/Lib/commons-pool-1.2.jar;%GC_HOME%/Lib/tar.jar;%GC_HOME%/Lib/ftp.jar;%GC_HOME%/Lib/jakarta-regexp-1.3.jar;%GC_HOME%/Lib/jaxen-1.1-beta-7-SNAPSHOT.jar;%GC_HOME%/Lib/jdom.jar;%GC_HOME%/Lib/jdom-contrib.jar;%GC_HOME%/Lib/mail.jar;%GC_HOME%/Lib/activation.jar;%GC_HOME%/Lib/mysql-connector-java-3.1.10-bin.jar;%GC_HOME%/Lib/smtp.jar;%GC_HOME%/Lib/aspectjrt.jar;%GC_HOME%/Lib/caarray-client.jar;%GC_HOME%/Lib/log4j-1.2.5.jar;%GC_HOME%/Lib/mageom-client.jar;%GC_HOME%/Lib/nciData.jar;%GC_HOME%/Lib/netcdfAll.jar;%GC_HOME%/Lib/xercesImpl.jar;%GC_HOME%/Lib/xml-apis.jar;%ORACLE_HOME%/jdbc/Lib/classes12.zip
echo Starting UniGene Parser ...
java -Xmx512M -DentityExpansionLimit=5000000 -Djava.security.manager -Djava.security.policy=java.policy edu.wustl.geneconnect.parser.UniGeneParser %1 %2 %3 %4 %5 %6 