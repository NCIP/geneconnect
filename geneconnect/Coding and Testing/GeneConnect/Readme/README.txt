==============================================================================
                 Guide for Configuring and Running GeneConnect Server.
==============================================================================

------------------------------------------------------------------------------
Index
------------------------------------------------------------------------------
1. Introduction

2. About GeneConnect Server
    
3. Pre-installation tasks : Setup Requirements 
    3.1. Database Installation
    3.2. Network Connectivity 
    3.3. Operating Systems
    3.4. Minimum Disk Space and RAM Requirements
    3.5. JAVA Installation
    3.6. PERL Installation and Integrating BioPerl modules
    3.7. BLAST Installation
    3.8. Ensembl API Installation
    3.9. Unzip and gzip utilities Installation
    3.10 Ant Installation

4. Folder Structure
    4.1. Contents of base directory and the subdirectories

5. Configuration Instructions
    5.1. Configurations required for running GeneConnect Server
    5.2. Configuring server.properties
    5.3. Configuring Command File 
    5.4. Configuring ApplicationConfig.properties File 
    5.5  Configuring build.properties File
    5.6  Configuring GCGraph.txt File

6. Configuring Scripts for GeneConnect Server run

7. Creating DataBase Tables

8. Running the Server
 
9. Building the server jar files with the updated code

10. Using GeneConnect server build script

11. Appendix

Appendix A : Oracle Database installation details

Appendix B : General Checks before starting with GeneConnect Server Run



------------------------------------------------------------------------------
1. Introduction
------------------------------------------------------------------------------

This document describes steps to set up the GeneConnect server, configuration of properties files, running the server executable through command prompt. Finally, it lists few important checks before running the server.


------------------------------------------------------------------------------
2. About GeneConnect Server
------------------------------------------------------------------------------

GeneConnect is the mapping service that will facilitate interoperability by interlinking approved genomic identifiers.


------------------------------------------------------------------------------
3. Pre-installation tasks : Setup Requirements 
------------------------------------------------------------------------------

3.1. Database Installation
--------------------------
	 
	 This release of GeneConnect Server supports Oracle 9i, 10g.

         - Oracle database server should be installed. Oracle instance should be running on it. 
	 
	 - Oracle database client should be installed on the machine, where the GeneConnect server is to be installed. 

	 - TNS name should be configured , pointing to the installed Oracle database server. For this, add an entry into TNSNAMES.ORA file in ORACLE_HOME\NETWORK\admin directory.

	 - See Appendix A for database installation details specific to GeneConnect. These steps need to be executed before running the GeneConnect server.


3.2. Network Connectivity 
-------------------------
Depending on how many data sources the server is to be run in one run, the continuous network connectivity requirements differ. 

If network is down during the server run then it will result into failure in database connectivity or data download. 


3.3. Operating Systems
----------------------

The GeneConnect can be installed on any of the following operating systems :
- Linux

The other operating Systems such as Windows 2000, NT, XP will be supported in the final release.


3.4. Minimum Disk Space and RAM Requirements
--------------------------------------------

Disk Space: 50 GB 
RAM: 2 GB


3.5. JAVA Installation
----------------------

Install JRE 1.4.2 version on your machine.
Installable can be downloaded from http://java.sun.com/j2se/1.4.2/download.html. 



3.6. PERL Installation and integrating BioPerl modules
------------------------------------------------------
Download ActivePerl-5.8.7 installer from http://www.activestate.com/Products/ActivePerl/ and install it on your machine.
DownLoad bioperl-1.4.zip from http://www.bioperl.org/Core/Latest/index.shtml and extract it. Follow the instructions in install file to integrate it with the perl.
(For Windows) Copy Bio directory from bioperl-1.4 to PERL_HOME\site\lib directory of your perl installation.



3.7. BLAST Installation
-----------------------
DownLoad BLAST installable from http://www.ncbi.nlm.nih.gov/BLAST/download.shtml and install it on your machine.


3.8. Ensembl API Installation
-----------------------------
a. Ensembl-API installation steps available at:
   http://www.ensembl.org/info/software/api_installation.html
   Install the Ensembl Core Perl API for version same as the version of mysql dump.

b. After this the EnsemblApi folder must be copied in the folder
   where the perl script is present 
   OR
   Add the path to the PERL5LIB environment variable.

For running Ensemble Parser, we need DBI modules 

DownLoad them from http://search.cpan.org/~timb/DBI-1.51/DBI.pm and integrate those with the PERL installation on your machine.




3.9. Unzip and gzip utilities Installation
------------------------------------------
For Windows :
- Download gzip from http://www.gzip.org/#exe and install it on your machine. 
- Update the System PATH to include gzip exe.

Similarly install unzip utility and update the system path.


3.10 Ant Installation
----------------------
Download ant from http://ant.apache.org/.
- To install Ant, choose a directory and copy the distribution file there. This directory will be known as ANT_HOME.
- Add the bin directory to your path.
- Set the ANT_HOME environment variable to the directory where you installed Ant.


------------------------------------------------------------------------------
4. Folder Structure
------------------------------------------------------------------------------
Unzipping the caFEServer.zip file from "Stable Code\Release" folder in the target directory will create the following folder hierarchy.

GeneConnectServer
-->Config
-->Lib
-->Logs
-->ParserScripts
-->Scripts
-->RunScripts
    -->Linux
    -->Solaris
    -->Windows

Here onwards 'base directory' will refer to GeneConnectServer directory. 

4.1. Contents of base directory and the subdirectories will be as follows
-------------------------------------------------------------------------
Folder Name                     Contents
-----------                     --------
GeneConnectServer               build.xml
				DataSync.sql
				delnodes.dmp
				division.dmp
				(*) EnsemblProteinHumanTestFile.dat
				(*) EnsemblTranscriptHumanTestFile.dat
				(*) entrez_refseqTestFile.dat
				entrez_unigeneTestFile.xml
				gbankmrnaTestFile.dat
				(*) gbankproteinTestfile.dat
				gc.prt
				gencode.dmp
				gene2xml.exe
				java.policy
				merged.dmp
				names.dmp
				NCBI_BioSource.mod
				NCBI_Entity.mod
				NCBI_Entrezgene.dtd
				NCBI_Entrezgene.mod
				NCBI_Gene.mod
				NCBI_General.mod
				NCBI_Protein.mod
				NCBI_Pub.mod
				NCBI_RNA.mod
				NCBI_SeqCode.mod
				NCBI_Seqloc.mod
				nodes.dmp
				(*) RefseqmrnaHumanTestFile.dat
				(*) RefseqproteinHumanTestFile.dat
				testFile.txt
				unigeneTestFile.dat.gz
				(*) uniprot_genbankTestFile.dat
				(*) uniprot_refseqTestFile.dat
				(*) uniprot_sprot_TestFile.dat
				(*) uniprot_sprot_TestFile.fasta
				(*) uniprot_trembl_TestFile.dat
				(*) uniprot_trembl_TestFile.fasta

(*)  These are the test input data files to the various parsers.		

Logs                            ErrorLog.txt
                                FileStatus.txt
                                (These files are generated by GeneConnect Server)

Lib                             activation.jar
				aspectjrt.jar
				caarray-client.jar
				caBIO.jar
				commons-collections-3.1.jar
				commons-lang.jar
				commons-pool-1.2.jar
				CorrelationRanges.jar
				ftp.jar
				GeneRanges.jar
				jakarta-oro-2.0.2-dev-2.jar
				jakarta-regexp-1.3.jar
				jaxen-1.1-beta-7-SNAPSHOT.jar
				jdom.jar
				jdom-contrib.jar
				log4j-1.2.5.jar
				mageom-client.jar
				mail.jar
				mysql-connector-java-3.1.10-bin.jar
				nciData.jar
				netcdfAll.jar
				smtp.jar
				tar.jar
				xercesImpl.jar
				xml-apis.jar 
                                GeneConnectServer.jar (jar that has been compiled from 
                                the source code)

Config                          ApplicationConfig.properties
				CmdFullFile.txt
				CommandFile.dtd
				CommandFile.xml(Lists the data sources from 
                                where data is to be downloaded for server run)
				OrganismTaxonomyConfig.txt
				PostWorkExecutionSequence.txt
				server.properties
				Time.txt
				tmpCmdFullFile.txt
				UnigeneOrganismAbbreviations.txt

ParserScripts			auto (Contains Perl modules used by Parser scripts)
				AutoLoader.pm
				Config (Contains Perl modules used by Parser scripts)
				ensembl.bat
				ensemblparser.pl
				entrez_refseq.pl
				entreztorefseq.bat
				entreztorefseq.sh
				FetchParams.pm
				FormatdbSinglefile.bat
				FormatdbSinglefile.sh
				FormatdbUniprot.bat
				FormatdbUniprot.pl
				FormatdbUniprot.sh
				FormatSinglefile.pl
				gbankprotein.pl
				GenbankmRNAParser.bat
				GenbankmRNAParser.pl
				GenbankmRNAParser.sh
				GenbankProteinParser.bat
				GenbankProteinParser.sh
				java.policy
				(+) properties_blastdestnrefmrna.txt
				(+) properties_blastDestnSingleFile.txt
				(+) properties_blastdestnuniprot.txt
				(+) properties_blastsourcegenbank.txt
				(+) properties_blastsourcegenbankmrna.txt
				(+) properties_blastsourcegenbankprotein.txt
				samplemap.txt
				Simple.pm
				UnigeneParser.bat
				UnigeneParser.sh
				uniprot_genbank.pl
				uniprot_refseq.pl
				uniprot2genbank.bat
				uniprot2genbank.sh
				uniprot2refseq.bat
				uniprot2refseq.sh

(+) These are the property files for the BLAST parsers. 

Scripts                         caCoreTables_createSchemaMysql.xml
				caCoreTables_createSchemaOracle.xml
				caCoreTables_DropConstraintsMysql.xml
				caCoreTables_DropConstraintsOracle.xml
				caCoreTables_RefConstraintsMysql.xml
				caCoreTables_RefConstraintsOracle.xml
				CreateGCSchema_sql_Mysql.xml
				CreateGCSchema_sql_Oracle.xml
				DropConstraintsMysql.xml
				DropConstraintsOracle.xml
				GCDataDump.sql
				GCSchemaCreation.sql
				ReferenceConstraints_sql.xml

RunScripts                      MySQLInit.sql
				OracleInit.sql
				GCOracleInit.sql
				Linux 
                                Solaris
                                Windows
                                (Above three folders contain OS specific 
                                scripts which will be used to run ca FEServer)



------------------------------------------------------------------------------
5. Configuration Instructions
------------------------------------------------------------------------------

Go to 'RunScripts' folder under base directory and copy all files from the subfolder corresponding to the server machine's OS to the base directory. For instance, if you want to run GeneConnect on a Linux machine, copy the script 'UpdateLinux.sh' into the base directory.


5.1. Configurations required for running GeneConnect 
----------------------------------------------------------------

a. server.properties file 
b. CommandFile.xml
c. ApplicationConfig.properties 
d. build.properties


5.2. Configuring Server.properties
----------------------------------

Before we run GeneConnect Server we need to make some changes in the server.properties file. This property file is present in 'Config' directory.

-----------------------------Start of File------------------------------------
### Main Properties file for GeneConnect Server

### Server Status Mail related parameters
toAddress   = <to email address>
host        = <mail server ip/domain name>
fromAddress = <from email address>
mailAccountpassword = <Password of the mail account from which mail needs to be sent>
signature   = <signature for the mail>
subject     = <email subject>

# Database connection properties

### multithread settings
noofthreads = <Number of threads to be spawned by GeneConnect server for downloading, parsing and loading the data>

# Name of Command file specifying the data sources to parse in the current run
commandFileName=Config/CommandFile.xml  

( For GeneConnect Server , it is a xml file. See section 5.3 for details.
  For Information regarding command file for caFE Server read caFEServerReadme.txt )

-----------------------------End of File--------------------------------------



5.3. Configuring Command File 
-----------------------------

The Command File (/Config/CommandFile.xml) contains the details about the annotation data sources to download and parse. For each data source, user can configure details like protocol to be used for download, location of the data source, name of files to download and so forth.

The table below lists the details of parameters to be specified for each data source in the command file.

Field			Description
-----			-----------

DataSource		
	name		Name of the Data source.
	type		Type of Protocol to be used.
	siteurl		URL of FTP/HTTP server.
	username	Username to access FTP data files.
	password	Password of the user to access remote data files.

	ExternalParser
	   writeToDB	Specifies whether the parser directly writes the data into the database or not.
	   commandfile  Name of the external command file to be invoked in order to execute the parser.

	BaseDirectory
	   dir		Directory on FTP/HTTP server which holds the data files
	   islocal	Used to specify that data file are already present on the same machine.

	File
  	   name		The data file name.


Please refer to the sample config file provided with the installation.

The command file should be a valid xml file. So please check for validity of the file before running the server.

For running the parsers on the test data (provided with the installation) , use /Config/CommandFile.xml as it is.

The detailed list of parsers and their appropriate test input files can be found in GCExternalParserDetails.xls.


5.4. Configuring ApplicationConfig.properties File 
--------------------------------------------------

Field				Description
-----				-----------
BUILDER_CLASS			edu.wustl.geneconnect.builder.GCBuilder   (for GeneConnect Server)
				com.dataminer.server.jobmanager.FEBuilder (for caFE Server)

TABLE_CREATION_FILE_ORACLE	
TABLE_CREATION_FILE_MYSQL	
CONSTRAINT_DROP_FILE_ORACLE	
CONSTRAINT_DROP_FILE_MYSQL	
CONSTRAINT_CREATION_FILE	


5.5  Configuring build.properties File
--------------------------------------
Before runing GeneConnect server build script, Specify the following properties in build.properties file :
GC_HOME = GeneConnect Server home directory
JAVA_HOME = Java base Directory
ORACLE_HOME = Oracle home directory
PERL_HOME = Perl home directory
BLAST_HOME = BLAST home directory
CVS_PACKAGE = Name of the CVS package/module to check out\update
CVS_CHECKOUT = Decides which CVS operation to perform. It should be true for first time to do checkout operation
and false later on , to get the updates from CVS. 
CVS_LOCATION = Directory where the checked out files should be placed. OR the CVS location which has to be updated. 
CVS_MODULE_LOCATION = Directory from the CVS which should be copied to current repository. 
   CVS_LOCATION + CVS_MODULE_LOCATION should point to GeneConnctServer (GeneConnect Base Directory) folder in CVS.


5.6  Configuring GCGraph.txt File
---------------------------------
Various datasource and links available among them are configured in GCGraph.txt. After addition or removal of any data source 
GCGraph.txt needs to be modified accordingly. After any modification in this cofig file metaData and summary of base tables 
(genomic links calculation) needs to be calculated. This can be achieved by running calculateMetaData and calculateSummary target of
GeneConnect server build script. Refer "Section 10. Using GeneConnect server build script" for more details.

Syntax of every line in GCGraph.txt is as follows : (Every item in the list is separated by ',')
1) List of data source names.
2) List of Genomic identifier class name
3) List of following value pairs : <Data type of values of the Data source> "-"  <Class Name> "-" <Attribute Name>
4) List of following value pairs : <Table Name> "-" <Column Name>
5) List of row no. in which data source needs to displayed in GeneConect graph.
6) List of column no. in which data source needs to displayed in GeneConect graph.
7) List of output attribute name for the data source from genomic class.
8) Line 8 and onwards specifies adjacency matrix for GeneConnect graph.

Each entry in the matrix represents the link types between two Data Sources. 

Possible values and their significance in adjacency matrix is as follows :
0 – No Link
1 – Direct Annotation
2 – Inferred Annotation
4 – Identity
8 - Alignment
n – Addition of 1, 2, 4 or 8 to represent multiple link types (e.g. 3 means Direct and Inferred Annotation, 6 means Inferred and Identity, 15 means all link types)

E.g.  If we are calculating the matrix value for Ensembl Protein to UniprotKB, then 
Link types = Alignment based + Inferred = 8 + 4 + 2 = 14.
Thus there should be value 14 for in the adjacency matrix for the directional link from Ensembl Protein to UniprotKB.

All properties of the data sources should be specified in the same order of data sources (specified on first line).



------------------------------------------------------------------------------
6. Configuring Scripts for GeneConnect Server run
------------------------------------------------------------------------------

To execute GeneConnect server, perform the following steps:

a. Copy the Update<OS> script (e.g. UpdateLinux.sh) from the RunScripts/<OS> folder to the 
   base directory. 
b. Update the script to configure the system variables to correct paths and 
   command line parameters to desired values. 

c. Specify correct values for JAVA_HOME, ORACLE_HOME, PERL_HOME and BLAST_HOME, etc variables in the following files :
	i.  Update<OS> script file.
	ii. All .bat and .sh  files under /ParserScripts.



------------------------------------------------------------------------------
7. Creating DataBase Tables
------------------------------------------------------------------------------

To create the database tables required by GeneConnect Server, run the script "GCSchemaCreation.sql" in the /Scripts directory.

OR Run the "createTables" target of GeneConnect build script. (Refer Section 10 for more details)



------------------------------------------------------------------------------
8. Running the Server
------------------------------------------------------------------------------
Before running GeneConnect Server make sure that you go through the check list in Appendix B and then run the script configured according to your requirements. 

To run the server from command prompt, execute the below command from the base directory.

Linux/Solaris:
$ nohup ./update.sh &

Windows:
> update.bat 

OR Run the "run" target of GeneConnect build script. (Refer Section 10 for more details)

--------------------------------------------------------
9. Building the server jar files with the updated code
--------------------------------------------------------
This section describes the procedure that should be followed for building GeneConnectServer.jar from .java files.

a. Add the path of ant utility in PATH variable.

b. Execute ant command from base directory of the GeneConnectServer (directory which contains "build.xml" file) to build GeneConnectServer.jar

           ant -emacs buildjar

c. Ant file "build.xml" present in the base directory will be used for generating GeneConnectServer.jar which will be placed in the Lib directory under the base directory. 

Run the "buildjar" target of GeneConnect build script. (Refer Section 10 for more details).


------------------------------------------------------------------------------
10. Using GeneConnect server build script
------------------------------------------------------------------------------
The various targets in the GeneConnect Server build script and their usages are as follows :

a) clean: Clean ups all temporary and log files.
b) buildjar: Create the application JAR - GeneConnectServer.jar.
c) checkConfigFiles: Checks server.properties and build.properties files for mandatory and correct property values.
After specifying values in all config files , run this target to check for correctness.
d) replaceConfigParameters: Replaces configuration parameters such as JAVA_HOME, ORACLE_HOME, etc. in run scripts. 
	You just need to specify all such configuration parameters in single property file (build.properties) 
	and run this task. It will copy them to appropriate run scripts.
e) createDataBaseSpaceAndUser: Create a database schema and user.
f) createTables: Create a database tables - Base as well as metadata tables.
g) copyRunScripts: Copies run scripts to the GeneConnect base directory.
h) run: Runs the GeneConnect server.
i) calculateMetaData: Calculates GeneConnect MetaData as per the configuartions done /Config/GCGraph.txt file.
j) calculateSummary: Calculates genomis identifier links.
k) GetLatestAndDeply: Retrieves latest contents from CVS (it can be caBIG CVS or any other CVS , needs to configured in build.properties file) and deploys the GeneConnect server.
l) GetLatestAndBuildJar: Retrieves latest contents from CVS (it can be caBIG CVS or any other CVS , needs to configured in build.properties file) and builds GeneConnectServer.jar.
m) deploy: Deploys GeneConnect server. The steps followed are :
	1> Checks server.properties and build.properties files for mandatory and correct property values
	2> Replaces configuration parameters such as JAVA_HOME, ORACLE_HOME, etc. in run scripts.
	3> Create a database schema and user
	4> Create a database tables - Base as well as metadata tables.
	5> Copies run scripts to the GeneConnect base directory.
	6> Calculates GeneConnect MetaData as per the configuartions done /Config/GCGraph.txt file.
	7> Sends the mail for successful deployment of GeneConnect Server.

To execute any of the above tasks:
1> Make sure that Ant is installed on your system. Environment variable ANT_HOME has been set and PATH contains the location of bin directory of Ant. (Refer section 3.10)
2> Open a command prompt or shell terminal.
3> Go to the GeneConnect server base directory.
4> Run the command : ant <task_name>
    where <task_name> can be any of the item among a-m from the above list.


------------------------------------------------------------------------------
11. Appendix
------------------------------------------------------------------------------


Appendix A : Oracle Database installation details
--------------------------------------------------
GeneConnect server will require ~25 GB of disk space to run. Before running, complete the Oracle set-up as given below by logging in as an administrator.

Tablespace creation scripts
---------------------------
CREATE TABLESPACE "GENECONNECTDATA" LOGGING 
DATAFILE '$ORACLE_HOME/GENECONNECTDATA01.ora' SIZE 16384M 
EXTENT MANAGEMENT LOCAL SEGMENT SPACE MANAGEMENT AUTO;


User Creation Scripts
---------------------
This section provides query to create a database user 'GENECONNECTUSER' with some specific privileges. The following actions can also be performed by executing the script GCOracleInit.sql, present in the /RunScripts directory.
NOTE: In the below SQL replace <password> with an appropriate password string.

 
CREATE USER GENECONNECTUSER PROFILE DEFAULT IDENTIFIED BY <password> DEFAULT TABLESPACE USERS TEMPORARY TABLESPACE TEMP ACCOUNT UNLOCK;
 
GRANT CREATE ANY INDEX TO GENECONNECTUSER;
GRANT CREATE ANY TABLE TO GENECONNECTUSER;
GRANT UNLIMITED TABLESPACE TO GENECONNECTUSER;
GRANT CONNECT TO GENECONNECTUSER;



Appendix B : General Checks before starting with GeneConnect Server Run
-----------------------------------------------------------------------
1. Check ./Logs folder created.
2. Check gene2xml file present in base dir with execute permission.
3. Check gene2xml is selected based on the OS type of the machine where you are going to run the server. 
4. Check all dtd files starting with NCBI_ (11 in number) are present in base directory.
5. Before going for Update/AddChip mode check that database creation was successful and all tables were created.
6. Check that the server_file_status table does not have any entries which will indicate files are already FTP ed and not FTP them. If files for a particular source are already present there then it will not download and parse that source again.
7. Check if unzip and gzip utilities are installed on machines.
8. Check if the logged in user has permission to execute the unzip command on the downloaded file.
9. Check if correct oracle home and java home has been set.
10. Check if VM argument -Xmx256M is set in the script
11. Check if  -DentityExpansionLimit=5000000 parameter is set to indicate the maximum allowed nodes for entrez files.
12. If a machine has Fedora Core 4 with JDK 1.4.2 and if operating system is using IPV6 protocol, then it may give IOException while connecting to a FTP URL. In that case set -Djava.net.preferIPv4Stack=true in the script.
13. Set up proper SMTP host in the server.properties file. Also set correct sender and receiver addresses.
14. If some files are already present in the user directory where you are going to download the same files then it tries to overwrite the existing files. If the logged in user is not having permission to delete the existing files then new files will not get downloaded.
15. Check if enough driver space is there on the drive to download the files. 
16. Check if enough table space is available in case of Oracle database.
17. See if Required jar files are present in the Lib directory.
18. Check if java_path and oracle_home are correctly set in the script.
19. Check if each parameter of command line is set appropriately.
20. Make sure that code cleans up the local directory as far as possible so that enough space is available there.
21. Make sure that the folders in the base directory contain the required files.
22. Also make sure that there are no any unwanted files in the base directory other than the ones mentioned above in the setup.




------------------------------------------------------------------------------
Incase of any query/error please mail the logs back to 
mailto:help@mga.wustl.edu
------------------------------------------------------------------------------