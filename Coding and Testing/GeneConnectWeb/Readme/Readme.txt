==============================================================================
         Guide for Deploying GeneConnect web application.
==============================================================================

------------------------------------------------------------------------------
Index
------------------------------------------------------------------------------
1. Introduction

2. About GeneConnect
    
3. Pre-installation tasks : Setup Requirements 
    3.1. Operating Systems
    3.2. Minimum Disk Space and RAM Requirements
    3.3. Database Installation
    3.4. JAVA Installation
    3.5. Apache Tomcat 5.x Installation
    3.6. Apache ANT Installation
    3.7. GeneConnect caCore-Like System

4. Folder Structure

5. Configuration Instructions
    5.1. Configurations required to deploy GeneConnect web application.

6. Running the Server.
 
7. Appendix

Appendix A : General Checks before starting with GeneConnect web application



------------------------------------------------------------------------------
1. Introduction
------------------------------------------------------------------------------

This document describes steps to configure and deploy GeneConnect web application.


------------------------------------------------------------------------------
2. About GeneConnect
------------------------------------------------------------------------------

GeneConnect is the mapping service that will facilitate interoperability by interlinking approved genomic identifiers.


------------------------------------------------------------------------------
3. Pre-installation tasks : Setup Requirements 
------------------------------------------------------------------------------

3.1. Operating Systems
----------------------

The GeneConnect web application can be installed on any of the following operating systems :
- Windows XP,Linux 

The other operating Systems such as Windows 2000, NT will be supported in the final release.


3.2. Minimum Disk Space and RAM Requirements
--------------------------------------------

Disk Space: 100 MB
RAM: <<<1>>> GB


3.3. Database Installation
--------------------------
	 
	 This release of GeneConnect Server supports Oracle 9i.

         - Oracle database server should be installed. Oracle instance should be running on it. 
	 
	 - Oracle database client should be installed on the machine, where the GeneConnect server is to be installed. 

	 - TNS name should be configured , pointing to the installed Oracle database server. For this, add an entry into TNSNAMES.ORA file in ORACLE_HOME\NETWORK\admin directory.

	 - See Appendix A for database installation details specific to GeneConnect. These steps need to be executed before running the GeneConnect server.

3.4. JAVA Installation
----------------------

Install JRE 5.0 version on your machine.
Installable can be downloaded from http://java.sun.com/javase/downloads/index.jsp. 


3.5. Apache Tomcat 5.x Installation
-------------------------------------------------
	 
Install Apache Tomcat 5.0 version on your machine.
Installable can be downloaded from http://tomcat.apache.org/download-55.cgi

3.6 Apache ANT Installation
-------------------------------------------------
Download the latest ANT and install it to a desired location e.g. C:\ANT. 
Installable can be downloaded from http://ant.apache.org/

3.7. GeneConnect caCore-Like system
-------------------------------------------------
To install GeneConnect caCore-Like system please refer to 'Readme.txt' localted at 'geneconnect\caCore SDK Generated Code'.

------------------------------------------------------------------------------
4. Folder Structure
------------------------------------------------------------------------------
a. Download the Geneconnect release zip file from cabig CVS folder 'geneconnect/Coding and Testing/GeneConnectWeb/Release/GeneConnect_Web_Installable.zip'.


------------------------------------------------------------------------------
5. Configuration Instructions
------------------------------------------------------------------------------

5.1. Configurations required to deploy GeneConnect web application GeneConnect caCore-Like System
---------------------------------------------------------------------------------------------------

a. Modify the configuration file 'build.properties' according to the your system setup.
   The file is located at 'geneconnect/Coding and Testing/GeneConnectWeb'.
      Following  is the list of properies to set and their description:

   1. tomcat.home.dir        : Should be set to CATALINA_HOME. The path should be separated by '/'
   2. tomcat.server.port     : Port number on which Apache Tomcat server is running.
   3. geneconnect.cacore.url : The URL to access/query on GeneConnect caCore-like system.
   4. database.type          : Database type used in the application.Currently  set as 'oracle'.
   5. database.host          : Hostname or IP address of the machine on which the database server is running.
   6. database.port          : Port number to connect with the database server.
   7. database.name          : The name of the database. Specify the same name that you have specified while creating the database.
   8. database.username      : The username used to connect to the database. 
   9. database.password      : The password used to authenticate the database user.

b. Open command prompt.

c. Change directory location to where you downloaded/checked out the web app source code. 'geneconnect/Coding and Testing/GeneConnectWeb'.

d. Run the ANT task to deploy the application:
	$ ant deploy_app

	deploy_app target: Configure the Web applciation of GeneConnect web application 
	according to properties set in 'build.properties' file. And deploy the  application on Apache Tomcat server location.

e. After deploying appliction on tomcat server copy the 'geneconnect/Coding and Testing/GeneConnectWeb/log4j.xml' at 
   'CATALINA_HOME/common/classes'. If file is already exists at destination folder then modify the 'CATALINA_HOME/common/classes/log4j.xml' file as follows.

   (i) Add following <appender> section in side <log4j:configuration> tag.		
	 <appender name="CATISSUECORE_LOGGER" class="org.apache.log4j.FileAppender">
         	<param name="File" value="${catalina.home}/logs/geneconnect.log" />
         	<param name="Append" value="true" />
         	<layout class="org.apache.log4j.PatternLayout">
         		<param name="ConversionPattern" value="%d{dd MMM yyy HH:mm:ss}, %-6p %C:%M:%L %m %n" />
         	</layout>
	 </appender>
   (ii) Add following <category> section
	<category name="catissuecore.logger">
         	<priority value="DEBUG" />
         	<appender-ref ref="CATISSUECORE_LOGGER" />
	</category>	

------------------------------------------------------------------------------
6. Running the Server
------------------------------------------------------------------------------
Before stating the Apache Tomcat server make sure that you go through the check list in Appendix B and then run the script configured according to your requirements. 

a. Start the Apache Tomcat Server.

b. Open the browser and enter the following URl to access teh application:
	http://machine_name:machine_port/geneconnect/Home.do

   where, machine_name : : This specifies the host name or IP address of the machine running the Tomcat server.
          machine_port : This specifies the port number on which Tomcat server is listening to the HTTP request.

   example of above URL: http://localhost:8080/geneconnect/Home.do

c. The same aaplication is deployed on testserver. The URL to access application is
   http://128.252.178.298:9092/geneconnect/Home.do	

------------------------------------------------------------------------------
7. Appendix
------------------------------------------------------------------------------

Appendix A : General Checks before starting with Tomcat server
-------------------------------------------------------------------------------
1. Check if Web Application Archieve 'geneconnectcaCore.war' (WAR file) is deployed and running.
2. Check if GeneConnect Web Application Archieve 'geneconnect.war' (WAR file)is deployed in Apache Tomcat location.
3. 'log4j.xml' file is properly set as described in section 5.1.
4. The database is setuped. Please refer to GeneConnect server Readme.txt located at 'geneconnect/Coding and Testing/GeneConnect/Readme'

------------------------------------------------------------------------------
Incase of any query/error please mail the logs back to 
mailto:geneconnect_bugs@mga.wustl.edu
------------------------------------------------------------------------------