==============================================================================
         Guide for Configuring and Running GeneConnect caCore-Like System.
==============================================================================

------------------------------------------------------------------------------
Index
------------------------------------------------------------------------------
1. Introduction

2. About GeneConnect Server
    
3. Pre-installation tasks : Setup Requirements 
    3.1. Operating Systems
    3.2. JAVA Installation
    3.3. Apache Tomcat 5.x Installation
    3.4. caCore SDK 1-1-1 Installation  

4. Folder Structure
    4.1. Contents of base directory and the subdirectories

5. Configuration Instructions
    5.1. Configurations required to generate GeneConnect caCore-Like System.

6. Running the Server and Client Application
 
7. Appendix

Appendix A : General Checks before starting with GeneConnect caCore Like system



------------------------------------------------------------------------------
1. Introduction
------------------------------------------------------------------------------

This document describes steps to set up the GeneConnect caCore-Like system, configuration of properties files, running the server and client executable through command prompt.


------------------------------------------------------------------------------
2. About GeneConnect Server
------------------------------------------------------------------------------

GeneConnect is the mapping service that will facilitate interoperability by interlinking approved genomic identifiers.


------------------------------------------------------------------------------
3. Pre-installation tasks : Setup Requirements 
------------------------------------------------------------------------------

3.1. Operating Systems
----------------------

The GeneConnect caCore-Like system can be installed on any of the following operating systems :
- Windows XP,Linux 

The other operating Systems such as Windows 2000, NT will be supported in the final release.


3.2. JAVA Installation
----------------------

Install JRE 5.0 version on your machine.
Installable can be downloaded from http://java.sun.com/javase/downloads/index.jsp. 


3.3. Apache Tomcat 5.x Installation
-------------------------------------------------
	 
Install Apache Tomcat 5.0 version on your machine.
Installable can be downloaded from http://tomcat.apache.org/download-55.cgi


3.4. caCore SDK 3.1 Installation
-------------------------------------------------
	 
Install caCore SDK 3.1 version on your machine.
Installable can be downloaded from http://ncicb.nci.nih.gov/download/downloadcacoresdk.jsp


------------------------------------------------------------------------------
4. Folder Structure
------------------------------------------------------------------------------
a. Download the caCore SDK Generated code for GeneConnect from cabig CVS folder 'geneconnect/caCore SDK Generated Code'
   in the target directory will create the following folder hierarchy.

   caCore SDK Generated Code	
   -->src
   -->edu
      -->wustl
	 -->geneconnect
	    -->bizlogic
	    -->utility
	    -->domain
	       -->impl
	       -->ws
	    -->impl
	    -->ws
   -->gov
      -->nih
	 -->nci
	    -->system
	       -->applicationservice
		  -->impl
	       -->dao
		  -->impl
		     -->orm 

   Here onwards 'base directory' will refer to GeneConnect_caCore directory. 

b. Download the object model EAP file from caBIG CVS folder 'geneconnect\Final UML Model\GeneConnectObjectModel_Preapproval.eap'


4.1. Contents of base directory and the subdirectories will be as follows
-------------------------------------------------------------------------
Folder Name				Contents
-----------				--------
caCore SDK Generated Code		GC_caCore_SchemaCreation

caCore SDK Generated Code		log4j.properties
					
src					GCTestClient.java

edu/wustl/geneconnect			GenomicIdentifierSolution.java
					GenomicIdentifierSolution.hbm.xml

edu/wustl/geneconnect/impl		GenomicIdentifierSolutionImpl.java
					GenomicIdentifierSolutionImpl.hbm.xml

edu/wustl/geneconnect/ws		GenomicIdentifierSolution.java
					GenomicIdentifierSolutionImpl.java

edu/wustl/geneconnect/domain		DataSource.java
					LinkType.java
					Gene.java
					MessengerRNA.java
					Protein.java
					GenomicIdentifierSet.java
					OrderOfNodeTraversal.java
					ConsensusIdentifierData.java
					GenomicIdentifier.java
					DataSource.hbm.xml
					LinkType.hbm.xml
					Gene.hbm.xml
					MessengerRNA.hbm.xml
					Protein.hbm.xml
					GenomicIdentifierSet.hbm.xml
					OrderOfNodeTraversal.hbm.xml
					ConsensusIdentifierData.hbm.xml
					GenomicIdentifier.hbm.xml

edu/wustl/geneconnect/domain/impl	DataSourceImpl.java
					LinkTypeImpl.java
					GeneImpl.java
					MessengerRNAImpl.java
					ProteinImpl.java
					GenomicIdentifierSetImpl.java
					OrderOfNodeTraversalImpl.java
					ConsensusIdentifierDataImpl.java
					GenomicIdentifierImpl.java
					DataSourceImpl.hbm.xml
					LinkTypeImpl.hbm.xml
					GeneImpl.hbm.xml
					MessengerRNAImpl.hbm.xml
					ProteinImpl.hbm.xml
					GenomicIdentifierSetImpl.hbm.xml
					OrderOfNodeTraversalImpl.hbm.xml
					ConsensusIdentifierDataImpl.hbm.xml
					GenomicIdentifierImpl.hbm.xml


edu/wustl/geneconnect/domain/ws		DataSource.java
					LinkType.java
					Gene.java
					MessengerRNA.java
					Protein.java
					GenomicIdentifierSet.java
					OrderOfNodeTraversal.java
					ConsensusIdentifierData.java
					GenomicIdentifier.java
					DataSourceImpl.java
					LinkTypeImpl.java
					GeneImpl.java
					MessengerRNAImpl.java
					ProteinImpl.java
					GenomicIdentifierSetImpl.java
					OrderOfNodeTraversalImpl.java
					ConsensusIdentifierDataImpl.java
					GenomicIdentifierImpl.java

edu/wustl/geneconnect/bizlogic		GCBizLogic.java
					GCCriteria.java
					ResultProcessor.java	

edu/wustl/geneconnect/utility		Constants.java
					Utility.java
					MetadataManager.java

gov/nih/nci/system/applicationservice/impl ApplicationServiceBusinessImpl.java

gov/nih/nci/system/dao/impl/orm		ORMDAOImpl.java


------------------------------------------------------------------------------
5. Configuration Instructions
------------------------------------------------------------------------------

5.1. Configurations required to generate GeneConnect caCore-Like System
-----------------------------------------------------------------------

a. Create folder 'geneconnect' under 'caCoreSDK base directory/cacoretoolkit/custom' folder.
b. From GeneConnect_caCore directory ('src') copy subfolder 'edu' to the 
   'caCoreSDK base directory/cacoretoolkit/custom/geneconnect' and also to 'caCoreSDK base directory/cacoretoolkit/src'.
c. From GeneConnect_caCore directory ('src') copy gov/nih/nci/system/applicationservice/impl/ApplicationServiceBusinessImpl.java
   and gov/nih/nci/system/dao/impl/orm/ORMDAOImpl.java to the 'caCoreSDK base directory/cacoretoolkit/src' directory to appropriate 
   package structure.
d. From GeneConnect_caCore directory copy 'log4j.properties' file to  'caCoreSDK base directory/cacoretoolkit/conf/'.
e. Generate XMI Annotaions of Object Model using Enterprise Architect.
f. Configure caCore SDK property/configuration files given the XMI file name(generated in step 'd').
   (For more information on configuring caCore SDK please refer to caCore SDK Technical manual).	
g.. Execute ant script of caCore SDK.


------------------------------------------------------------------------------
6. Running the Server
------------------------------------------------------------------------------
Before running GeneConnect Server make sure that you go through the check list in Appendix B and then run the script configured according to your requirements. 

a. Start the Apache Tomcat Server.

------------------------------------------------------------------------------
7. Appendix
------------------------------------------------------------------------------

Appendix A : General Checks before starting with GeneConnect caCore Like system 
-------------------------------------------------------------------------------
1. Check if Web Application Archieve (WAR file)generated by caCore SDK deployed in Apache Tomcat location.
2. While executing GCTestClient check if Required jar files are present in the CLASSPATH.
3. caCore SDk buil;d script requires MySQL databse installed. If your machine does not have 
   MySQL installed then remove task of target start-mysql from 'caCoreSDK base directory/cacoretoolkit/os.windows.xml'
4. Comment out the statement <exclude name="log4j-1.2.8.jar"/> in 
   'caCoreSDK base directory/cacoretoolkit/build.xml' located at ANT target '-package-server.main'.
5. Check hibernate properties for oracle of caCore SDK is correctly configured.

------------------------------------------------------------------------------
Incase of any query/error please mail the logs back to 
mailto:help@mga.wustl.edu
------------------------------------------------------------------------------
