@echo off
cls
echo Starting GeneConnect Server...

rem ************ Application base directory **********
set GC_HOME=E:\GeneConnectServer

rem ************** Java base Directory ***************
set JAVA_HOME=D:\MyInstalls\J2SE1.4.2_02

rem ************* Oracle home directory **************
set ORACLE_HOME=D:\Oracle92

rem ************** Perl home directory ***************
rem ***** Perl Installation should have BioPerl modules installed *******
set PERL_HOME=D:\Perl

rem ************* BLAST home directory **************
set BLAST_HOME=D:\blast-2.2.14-ia32-win32

set OLD_PATH=%PATH%

set PATH=%JAVA_HOME%/bin;%ORACLE_HOME%/bin;%PERL_HOME%/bin;%BLAST_HOME%/bin;%PATH%

set OLD_CLASSPATH=%CLASSPATH%

set CLASSPATH=%GC_HOME%/lib/FEServer.jar;%GC_HOME%/Lib/caBIO.jar;%GC_HOME%/Lib/commons-collections-3.1.jar;%GC_HOME%/Lib/commons-pool-1.2.jar;%GC_HOME%/Lib/tar.jar;%GC_HOME%/Lib/ftp.jar;%GC_HOME%/Lib/jakarta-regexp-1.3.jar;%GC_HOME%/Lib/jaxen-1.1-beta-7-SNAPSHOT.jar;%GC_HOME%/Lib/jdom.jar;%GC_HOME%/Lib/jdom-contrib.jar;%GC_HOME%/Lib/mail.jar;%GC_HOME%/Lib/activation.jar;%GC_HOME%/Lib/mysql-connector-java-3.1.10-bin.jar;%GC_HOME%/Lib/smtp.jar;%GC_HOME%/Lib/aspectjrt.jar;%GC_HOME%/Lib/caarray-client.jar;%GC_HOME%/Lib/log4j-1.2.5.jar;%GC_HOME%/Lib/mageom-client.jar;%GC_HOME%/Lib/nciData.jar;%GC_HOME%/Lib/netcdfAll.jar;%GC_HOME%/Lib/xercesImpl.jar;%GC_HOME%/Lib/xml-apis.jar;%ORACLE_HOME%/jdbc/Lib/classes12.zip

rem ***** Copy .op files from the config folder to the root folder of the project ****
rem *** this is for test data and can be removed once file deletion problem has been solved ******
copy %GC_HOME%\Config\*.op %GC_HOME%

rem ***** Copy Perl modules to the perl libraries *******
mkdir %PERL_HOME%\site\lib\auto\Config
mkdir %PERL_HOME%\site\lib\auto\Config\Simple
copy %GC_HOME%\ParserScripts\auto\Config\Simple\*.* %PERL_HOME%\site\lib\auto\Config\Simple

mkdir %PERL_HOME%\site\lib\Config
copy %GC_HOME%\ParserScripts\Config\*.* %PERL_HOME%\site\lib\Config
rem *****************************************************

java  -Xmx512M  -DentityExpansionLimit=5000000 -Djava.security.manager  -Djava.security.policy=java.policy com.dataminer.server.jobmanager.JobManager

set CLASSPATH=%OLD_CLASSPATH%

set PATH=%OLD_PATH%