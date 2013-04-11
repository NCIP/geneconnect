/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>ClassName: java com.dataminer.server.jobmanager.BaseBuilder</p> 
 */

package com.dataminer.server.jobmanager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import com.dataminer.server.database.DBManager;
import com.dataminer.server.database.DataLoadManager;
import com.dataminer.server.exception.ApplicationException;
import com.dataminer.server.exception.FatalException;
import com.dataminer.server.exception.NonFatalException;
import com.dataminer.server.ftp.FTP;
import com.dataminer.server.ftp.FileInfo;
import com.dataminer.server.globals.Constants;
import com.dataminer.server.globals.Utility;
import com.dataminer.server.globals.Variables;
import com.dataminer.server.io.FileOutput;
import com.dataminer.server.io.PropertiesFileHandeler;
import com.dataminer.server.log.Logger;
import com.dataminer.server.mail.mailsender;
import com.dataminer.server.parser.CommandFileParser;
import com.dataminer.server.parser.CreateSchemaParser;
import com.dataminer.server.parser.OrganismTaxonomyParser;
import com.dataminer.server.parser.ParserManager;
import com.dataminer.server.parser.XMLCommandFileParser;
import com.dataminer.server.postwork.PostWorkManager;

/**
 * This Abstract class provides the default implementation of 
 * instantiating Download Manager, Parser Managers, Data Load Manager to 
 * create threads, each of which will start multiple threads to carry out the 
 * respective functionality of downloading, parsing and loading data from data sources. 
 * It also stores instance of DBManager which will be used to establish connection with 
 * the database and execute different queries.
 * This class also provides the abstract methods where the application using Annotation PArser 
 * Library wil implement application specific logic.
 *   
 * @author       Sachin Lale
 * @version      1.0
 */
public abstract class BaseBuilder 
{
	protected PropertiesFileHandeler pfh;
	
	/** CommandFileParser instance which is used for parsing the command file to get 
	 * information about the datasources from where data is to be downloaded. The 
	 * records in command file are converted to FileInfo objects */
	protected CommandFileParser cmdParser;
	
	/** Database manager instance */
	/** Single Database manager instance is used for connecting to the database based 
	 * on the information specified in the command line. Methods on this class are 
	 * called to execute queries, create cursors, creates status mail body by 
	 * populating database related information */
	protected DBManager dbInterface = DBManager.getInstance();
	
	
	/** vector to store fileInfo objects obtained from command file parser. This vector 
	 * is converted into filelist which has one file per fileInfo Object */
	protected Vector files;
	
	/** File containing the body of the status mail with the statistics for current 
	 * run if mail sending fails */
	protected FileOutput fileOutput;
	
	/** ftp client instance which is used to call checkDate using fileInfo object which 
	 * will decide whether file needs to be downloaded and parsed or no change has 
	 * occured at data source since last update*/
	protected FTP ftpInstance;
	

	/** Constructor Method 
	 * The default constructor performs the following fuctions :
	 * 1. Loading server.properties,
	 * 2. Loading File info object by parsing Command File
	 * 3. Set up Database connecttion
	 * 4. Process taxonomy data. 
	 * */
	public BaseBuilder()
	{
		/** Load Values from the server.properties File.*/
		loadProperties();
		
		/** based on the parameters provided on the command line call connect on dbInterface to 
		 * set up connection object*/
		try 
		{
			dbInterface.connect(Variables.driverName, Variables.dbURL,
					Variables.dbUserId, Variables.dbUserPsswd);
		} 
		catch (FatalException fatal) 
		{
			/** If database connection can not be established successfully it will throw 
			 * FatalException which will cause the program to terminate  */
			Logger.log("Fatal Exception occured while connecting to database",Logger.FATAL);
			Logger.log("Reason : " + fatal.getMessage(),Logger.FATAL);
			fatal.printStackTrace();
			fatal.printException();
			handleFatalException();
		}
		
		/** If Update or Add Chip mode is selected then parse the command file to get 
		 * the information about sources from where data is to be downloaded and parsed */
		if(false == Variables.createDBSchema)
		{
			/** Create an instance of Command file parser by passing it the file which 
			 * is to be parsed 
			 * The XMLCommandFileParser is added to parse te XML format of Command File.  
			 * */
			if(Variables.CommandFile.endsWith(".xml"))
				cmdParser = new XMLCommandFileParser(Variables.CommandFile);
			else
				cmdParser = new CommandFileParser(Variables.CommandFile);
			
			Logger.log("command file parser created",Logger.INFO);

			try 
			{
				/** This function reads command file and parses it to extract information 
				 * like site url, user name,password, file name, proxy settings etc from 
				 * the records in the file and populate the vector of FileInfo objects 
				 * present with the Command file parser */
				cmdParser.readCmdFile();
				Logger.log("read cmd file done",Logger.INFO);
			}
			catch (FileNotFoundException fnfe) 
			{
				/** In case of Update and Add Chip modes the entire information about 
				 * which data sources to process is stored in the command file, hence 
				 * if that file is not found in the working directory then execution is terminated */
				Logger.log("FTP Command File: "	+ Variables.CommandFile + " does not exist",Logger.FATAL);
				System.out.println("FTP Command File: "	+ Variables.CommandFile + " does not exist");
				System.exit(1);
			}
			catch (IOException ioex) 
			{
				Logger.log("Error while processing command file. Hence exiting from Application", Logger.FATAL);
				Logger.log(ioex.getMessage(),Logger.FATAL);
				System.out.println("Error while processing command file. Hence exiting from Application");
				System.exit(1);
			}
			/** Get the list of FileInfo objects containing the information from 
			 * command file and initialise the file vector */ 
			files = cmdParser.getFileInfo();
			createFileInfoList();
			Logger.log("GetFileInfo called to get files vector",Logger.INFO);
			Logger.log("Total number of files to process are : " + files.size(),Logger.INFO);
		}
		
		/**update the organism taxonomy map tables.*/
		processTaxonomy();
	}
	
	/**
	 * This method instantiates the Builder class,the class name has to be configured as 
	 * Key = Value pair in ApplicationConfig.properties in ../Config folder.
	 * if classname is not configure this will instantiate the BaseBuider with empty
	 * implementation of abstract methods.   
	 * @param builderClassName
	 * @return BaseBuilder instance of given classname or default/empty implementation of BaseBuilder
	 */
	public static BaseBuilder getInstance(String builderClassName)
	{
		BaseBuilder builder = null;
		if(builderClassName!=null)
		{
			try
			{
				builder = (BaseBuilder )Class.forName(builderClassName).newInstance();
			}
			catch(Exception e)
			{
				Logger.log(e.getMessage(),Logger.FATAL);
				System.exit(1);
			}
		}
		else
		{
			builder = new BaseBuilder(){
				public  void preProcessing(){
					
				}
				public void postProcessing(){
					
				}
				public void caCoreProcessing(){
					
				}
			};
		}
		return builder;
	}
	
	public void setApplicationPropertyHandler(PropertiesFileHandeler pfh)
	{
		this.pfh =pfh;
	}
	
	/**
	 * The Application must implement this method  to perform applciation specific 
	 * function before download and parse data.Such as creatation of '_U' tables, variables 
	 * initialisation 
	 *
	 */
	public  abstract void preProcessing();
	
	/**
	 * The Application using annotaion parser library must implement this method  to 
	 * perform applciation specific function after download and parse data. 
	 * Such as renaming of '_U' tables etc. 
	 *
	 */
	public abstract void postProcessing();
	
	/**
	 * The Application using annotaion parser library must implement this method  to 
	 * perform applciation specific caCore system function after download and parse data. 
	 * Such as renaming creation of caCore tables, post processing for caCore tables etc. 
	 *
	 */
	public abstract void caCoreProcessing();
	
	/**
	 * This function default implementation which will create postwork manager
	 * and it will call method on it to postwork.
	 * @return This method returns success/failure in carrying out postwork
	 */
	public boolean postWork()
	{
		Logger.log("Builder::postwork called",Logger.DEBUG);
		//create _U tables for post work
		PostWorkManager pwManager = new PostWorkManager(dbInterface);
		
		boolean isSuccess = pwManager.postWork();
			
		return isSuccess;
	}
	
	/**
	 * This is the main function of builder class which will create parser, downloader 
	 * and loader managers. It will call start methods on them and pass the file info 
	 * list to downloader. The DPQueue's which are used have synchronised Downloader 
	 * will populate the fileDownloaded DPQueue which will be passed to parser which 
	 * will populate filesParsed DPQueue which will be passed to loader. Thus all 
	 * three of them will create specified number of threads in parallel and will 
	 * perform the producer - consumer actions 
	 */
	public void downloadParseAndLoadData()
	{

		/**
		 * Create a List of Files to be downloaded which has a syncronized 
		 * method for multiple threads to access the objetcs.
		 */
		FileList filesToDownLoad = new FileList(files);
		
		/** 
		 * Create queues that will hold the downloaded and parsed files from 
		 * which threads can pick up the objects.
		 */
		Logger.log("Queue max size set to 100",Logger.INFO);
		DPQueue filesDownLoaded = new DPQueue(100);
		DPQueue filesParsed = new DPQueue(100);
		
		/**
		 * create the DownLoad, Parser and Loader managers that will spawn the threads
		 * and start downloading,parsing and loading of the data. 
		 */
		DownLoadManager downLoadManager = new DownLoadManager(filesDownLoaded,Variables.noOfThreads);
		ParserManager parserManager = new ParserManager(Variables.noOfThreads,filesParsed);
		DataLoadManager dataLoadmanager = new DataLoadManager(Variables.noOfThreads);
		
		downLoadManager.start(filesToDownLoad);
		parserManager.start(filesDownLoaded);
		dataLoadmanager.start(filesParsed);
		
		try
		{
			/**
			 *  Wait for downloader, parser and data loader threads to get over
			 */
			downLoadManager.join();
			parserManager.join();
			dataLoadmanager.join();
		}
		catch(InterruptedException e)
		{
			Logger.log("Error in Execution of Threads" + e.getMessage(), Logger.WARNING);
			System.out.println("Exception in DownloadParse And LoadData:" +e.getMessage());
			System.exit(1);
		}
			
	}
	/**
	 * update the organism taxonomy map tables.
	 *
	 */
	protected void processTaxonomy()
	{
		OrganismTaxonomyParser orgTaxParser = new OrganismTaxonomyParser(dbInterface);
		
		/**Process if processTaxonomy is set as 'true' in server.properties file*/
		if(false == Variables.createDBSchema&&Variables.processTaxonomy)
		{
			/** update the organism taxonomy map tables.*/
			Logger.log("using organism taxonomy parser",Logger.INFO);
			
			/** In the constructor method of OrganismTaxonomyMap parser latest taxonomy 
			 * file will be downloaded parsed and related tables will be populated with 
			 * latest taxonomy ids	 */
			
			orgTaxParser.loadtaxonomyDetails();
			Logger.log("updates organism taxonomy details",Logger.INFO);
		}	
		/** These functions will read the organism taxonomymap table in the database to 
		 * get the taxonomy ids,organism names and corresponding local taxonomy ids. 
		 * They will be kept in memory for the entire execution time. The global maps 
		 * populated by these tables will be used by parsers to convert the organism names or
		 * taxonomy ids into local taxids before storing them in the database. */
	
		orgTaxParser.populateOrgaismTaxonomyMap();
		orgTaxParser.populateAbbreviatedOrganismMap();	
			
	}
	
	/**
	 *This function creates the final fileInfo List and determines which
	 *databases are to be downloaded (ftp/local).
	 */
	
	public void createFileInfoList()
	{
		/** Here we must use the files Vector obtained by parsing command file and check which files are to be 
		 * downloaded. If all the files from a particular data source have not been modified since they were last
		 * updated, then there is no need to bring in the data from that data source. But even if single file has been
		 * modified we need to bring in all files from that data source. This is so because we don not append the newly
		 * parsed data to existing tables but we replace old tables completely with new tables.*/
		
		FileInfo fileInfo;
		boolean checkVal = true;
		Iterator itr = files.iterator();
		while (true == itr.hasNext())
		{
			fileInfo = (FileInfo)itr.next();
			Logger.log(" Take out fileinfo object for = "+fileInfo.getDatabaseType(),Logger.INFO);
			/** If file is of type local file then it needs to be processed in current run irrespective
			 * of time when it was modified But if it is FTPed or HTTPed then we need to check its
			 * last modified date to decide whether it needs to be downloaded or not*/
			if( false == fileInfo.localFile )//this function checks if the files for this DB are to be ftp'd or not.
			{
				/** For FTPed file call method on FTP to check date. It will return true if file needs
				 * to be downloaded in current run
				 */
				if(fileInfo.getType().equalsIgnoreCase(Constants.FTP))
				{
					checkVal = FTP.checkDate(fileInfo);
					//System.out.println(checkVal + "--" + fileInfo.getBaseDir());
				}
				/** For HTTPed file call method on HTTP to check date. It will return true if file needs
				 * to be downloaded in current run
				 */
				else if(fileInfo.getType().equalsIgnoreCase(Constants.HTTP))
				{
					checkVal = HttpDownLoader.checkDate(fileInfo);
				
				}
			}
			else
			{
				/** The following will be incase of a local file.Because we always want them to be added fileInfoList*/
				checkVal=true;
			}
			if(checkVal==false)
			{
				itr.remove();
			}
		}

		/** Add the names of fileInfo objects obtained above into the Hashtable fileInfoList.*/
		Variables.fileInfoList.addAll(files);
		for(int i =0;i<files.size();i++)
		{
			FileInfo fi = (FileInfo)files.get(i);
			//System.out.println(fi.getBaseDir());
		}
		
	}
	
	/**
	 * This method creates the database schemas used by application.
	 * The schema defination should be in ROOT_DIR/Scripts/file_name.xml
	 * And the schena defination 'file_name' should be the value of follwing KEYS in ApplicationComfig.properties
	 * 	TABLE_CREATION_FILE_ORACLE
	 * 	TABLE_CREATION_FILE_MYSQL
	 * 
	 * @param 
	 * @return 
	 */
	public void createTables()
	{
		if(true == Variables.createDBSchema)
		{
			try
			{ 
				Logger.log("createTables() called ",Logger.INFO);
				/** Create Schema parser which will parse database creation files and 
				 * store information about the tables and their definitions in the maps 
				 * which will be used later to create tables  */
				CreateSchemaParser csp = new CreateSchemaParser();
				
				HashMap hmTableCreation = null;
				/** Select appropriate file names having create database scripts for 
				 * Oracle as well as MySQL */
				if(Variables.dbIdentifier.equals(Constants.ORACLE))
				{
					String fileName = Variables.currentDir + Constants.fileSep + "Scripts"
					+ Constants.fileSep +pfh.getValue(Constants.TABLE_CREATION_FILE_ORACLE);
					csp.parseFile(fileName);
				}
				else if(Variables.dbIdentifier.equals(Constants.MYSQL))
				{
					String fileName = Variables.currentDir + Constants.fileSep + "Scripts"
					+ Constants.fileSep +pfh.getValue(Constants.TABLE_CREATION_FILE_MYSQL);
					csp.parseFile(fileName);
				}
				
				/** From the create schema parser get the table definition map which will
				 * store information about all
				 * the tables and their creation scripts */
				hmTableCreation = csp.getTableDefinitionMap();
				
				/** If create Database schema mode is used then create all the base and 
				 * summary tables based on the scripts */
			
				
					String IP = "HostNameUnknown";
					/** Iterate over the table creation map and read each of the DDL 
					 * statements and execute them to create respective tables */
					for (Iterator iter = hmTableCreation.entrySet().iterator(); iter.hasNext();)
					{ 
						Map.Entry entry = (Map.Entry) iter.next();
						String tabName = (String)entry.getKey();
						String colDef = (String)entry.getValue();
						dbInterface.executeUpdate("CREATE TABLE " + tabName + colDef);
					}
					/** The data base creation script also has the insert queries to 
					 * insert some default entries in
					 * the database after creation of tables */
					Vector vecInsertQueries = null;
					/** Get the vector storing insert queries read from the create Database scripts */
					vecInsertQueries = csp.getInsertQueryVector();
					/** Execute the insert queries read from the DB create scripts to 
					 * populate tables with default values*/
					for (Iterator iter = vecInsertQueries.iterator(); iter.hasNext();) 
					{
						String insertQuery = (String) iter.next();
						dbInterface.executeUpdate(insertQuery);						
					}
					
					/** Put foreign key constraint on server_file status table to refer to 
					 * server_status table for server run ID */
					dbInterface.executeUpdate(Constants.queryServerStatusFK);
				
					Logger.log("schema creation over", Logger.INFO);
					
					/** Create mailSender object which will be used to send mail to specified 
					 * address with specified parameters and body text	 */
					mailsender mailsend = new mailsender();
					
					SimpleDateFormat sdf = new SimpleDateFormat(Variables.dateString);
					String executionDate=sdf.format(new Date());
					/** get maximum server run ID from server_status table which will be 
					 * used to set next ID for storing the statistics of current run */
					int max=dbInterface.execQuery(Constants.getMaxId);
					max++;
					try
					{
						Variables.machineName = InetAddress.getLocalHost().getHostName();
						IP = InetAddress.getLocalHost().getHostAddress();
					}
					catch(java.net.UnknownHostException uhe)
					{
						Logger.log("Error detecting HOST IP in builder ",Logger.INFO);
					}
					Variables.endJobTime = System.currentTimeMillis();
					Long elapsedTime = new Long (Variables.endJobTime - Variables.startJobTime);

					String query[] = {"INSERT INTO " + Constants.serverStatusTableName + 
					" VALUES (" + max + "," + "'" + executionDate + "'" + ",'C'," + "'" + IP + 
					"'" + ",0," + elapsedTime + "," +  Variables.errorCount + ",0)"};
					
					dbInterface.insertRowsForStatus(query);
					dbInterface.commit();
					
					String subject = Variables.subject + " " + Variables.machineName;
					String body = new String(dbInterface.createMailBody(max,executionDate) );
					/** mailsend is an object of class mail sender and it would actually send the mail.*/
					boolean send = mailsend.sendmail(Variables.toAddress,Variables.fromAddress,Variables.password
					        , Variables.host,subject,body);
					
					if(false == send)
					{
						try
						{
							Logger.log("Mail could not be Sent to " + Variables.toAddress,Logger.INFO);
							fileOutput = new FileOutput(Constants.StatusMailBody,false);
							Logger.log("StatusMailBody.txt opened...",Logger.INFO);
							fileOutput.writeln(body);
						}
						catch(ApplicationException ae)
						{
							Logger.log("Error creating FileOutput object for Statusmail",Logger.INFO);
						}
					}
					else
					{
						Logger.log("Mail Sent to " + Variables.toAddress,Logger.INFO);
					}
				} 
				catch(FatalException fatal)
				{
					Logger.log("Fatal Exception in create tables function ",Logger.FATAL);
					Logger.log(fatal.getMessage(),Logger.DEBUG);
				}
				catch(ApplicationException ae)
				{
					Logger.log("Application Error Loading Properties from Properties File " + ae.getMessage(),Logger.WARNING);
					System.out.println("Exception in Create Tables:" + ae.getMessage());
					System.exit(1);
				}
				catch (Exception exp) 
				{
					Logger.log("Error while executing exception" + exp.getMessage(), Logger.INFO);
				}
		}
}	
	
	
	/**
	 * Load properties from server.properties file
	 * The properties read are: to and from addresses for status mail,mail server,mail subject,
	 * no of threads to be spawned for downloader and parser, flags indicating whether postwork 
	 * and caCore table generation should be done or not, GO configuration, Homologene Cut off,
	 * caArray configuration parameters and organism taxonomy configuration.
	 */
	protected void loadProperties()
	{
		try
		{
			String fileSep = System.getProperty("file.separator");
			String fileName = Variables.currentDir + fileSep + "Config" + fileSep + Constants.serverPropertiesFile;
			PropertiesFileHandeler pfh = new PropertiesFileHandeler(fileName);
			/**Load actual properties now*/
			
			//FE specific code moved to fe builder
			/** get the mode of operation for server as first command line argument**/
			/*int serverRunMode = Integer.parseInt(pfh.getValue(Constants.EXECUTION_MODE).trim());
			Logger.log("Mode in which server will run: 1:Update 2:Add Chip 3:Create Database Schema = " + serverRunMode,Logger.INFO);
			/** Based on the mode selected set appropriate variable */
			/*if(Constants.UPDATE_MODE == serverRunMode)
			{
				Variables.updateMode = true;
			}
			else if (Constants.ADD_CHIP_MODE == serverRunMode) 
			{
				Variables.addChip = true;
			} 
			else if (Constants.CREATE_DB_SCHEMA_MODE == serverRunMode) 
			{
				Variables.createDBSchema = true;
			}
			else
			{
				Logger.log("Invalid mode for server. Only modes 1,2,3 are allowed.",Logger.FATAL);
				System.out.println("Exceptin: Invalid mode for server. Only modes 1,2,3 are allowed");
				System.exit(1);
			}*/

			/** Read username and password for database from command line */
			Variables.dbUserId = pfh.getValue(Constants.DATABASE_USERNAME).trim();
			Variables.dbUserPsswd = pfh.getValue(Constants.DATABASE_PASSWORD).trim();

			/** Set the temperory directory to the user directory */
			Variables.tempDir = System.getProperty("user.dir");

			/** Set database configuration parameters from command lines into the global variables*/
			Variables.dbConnect =  pfh.getValue(Constants.DATABASE_CONNECT).trim();
			Variables.driverName = pfh.getValue(Constants.DATABASE_DRIVER).trim();
			Variables.dbURL = pfh.getValue(Constants.DATABASE_URL).trim();

			
			String dbType = pfh.getValue(Constants.DATABASE_TYPE).trim();
			
			/** If the database type is other than MySQL and Oracle then the execution will  terminate logging below message*/
			if((!(dbType.equalsIgnoreCase(Constants.ORACLE) || dbType.equalsIgnoreCase(Constants.MYSQL) )))
			{
				Logger.log("Invalid Data base identifier. Oracle and Mysql are only allowed.",Logger.FATAL);
				System.out.println("Exception: Invalid Data base identifier. Oracle and Mysql are only allowed.");
				System.exit(1);
			}

			/** This will avoid checking equalsIgnoreCase again and again for database identifiers */
			if(true == dbType.equalsIgnoreCase(Constants.ORACLE))
				Variables.dbIdentifier = Constants.ORACLE;
			else
				Variables.dbIdentifier = Constants.MYSQL;
			
			/** This function will set date format, date function and null character specific to the database being used */
			makeDBServerSpecificSettings();
			
			//FE Builder specific code moved to fe builder
			/** If Update or Add Chip modes are selected then their should be 8th parameter on the command line
			 * specifying the name of Command file have source information from which data is to be downloaded */
			/*if(false == Variables.createDBSchema)
			{
				/** If Command file is not specified for Update and Add Chip mode then ArrayOutOfBound Exception 
				 * will be thrown */
				/*Variables.CommandFile = pfh.getValue(Constants.COMMAND_FILE_NAME).trim();
				
				if(null == Variables.CommandFile)
				{
					/** If Command file name is not specified on command line in case of Update and Add Chip mode then 
					 * execution will terminate logging correct message */
					/*Logger.log("Command File path and name is required for modes other than CreateDBSchema mode",Logger.FATAL);
					System.out.println("Command File path and name is required for modes other than CreateDBSchema mode");
					System.exit(1);
				}
			}*/
			
			/** Following block will log all the options specified by user through command line */
			Logger.log("dbuser = " + Variables.dbUserId,Logger.INFO);
			Logger.log("Command file path = " + Variables.CommandFile,Logger.INFO);
			Logger.log("update mode = " + Variables.updateMode,Logger.INFO);
			Logger.log("adding new chips = " + Variables.addChip,Logger.INFO);
			Logger.log("driverName = " + Variables.driverName,Logger.INFO);
			Logger.log("dbURL = " + Variables.dbURL,Logger.INFO);
			Logger.log("Temp. working directory = " + Variables.tempDir,Logger.INFO);

			
			/** Properties for mail client and status mail parameters */
			Variables.toAddress = pfh.getValue(Constants.TOADDRESS).trim();
			Variables.host = pfh.getValue(Constants.HOST).trim();
			Variables.fromAddress = pfh.getValue(Constants.FROMADDRESS).trim();
			Variables.password = pfh.getValue(Constants.MAIL_PASSWORD);
			Variables.signature = pfh.getValue(Constants.SIGNATURE).trim();
			Variables.subject = pfh.getValue(Constants.SUBJECT).trim();
				
			/** Properties for Server*/
			Variables.noOfThreads = Integer.parseInt(pfh.getValue(Constants.NUMBER_OF_THREADS).trim());
			
			/** MySQL specific setting which requires host name to connect to the database */
			Variables.dbName = pfh.getValue(Constants.DBNAME).trim();
			
			/** Properties used to set whether postwork and caCore table population needs to be done or not */
			String postWork = pfh.getValue(Constants.POSTWORK).trim();
			
			/**if postwork flag is not set then default value true indicating perform postwork will be used.*/
			if(postWork != null)
			{	
				Variables.postWork = Utility.toBoolean(postWork);
				Logger.log("Variables.postWork = " + Variables.postWork,Logger.DEBUG);
			}
			
			/**if caCorePostwork flag is not set then default value true indicating perform caCorepostwork will be used.*/
			String caCoreSystemPostWork = pfh.getValue(Constants.CACORE_TABLE_CREATION).trim(); 
			if(caCoreSystemPostWork != null)
			{
				Variables.caCoreSystemPostWork = Utility.toBoolean(caCoreSystemPostWork);
				Logger.log("Variables.caCoreSystemPostWork = " + caCoreSystemPostWork,Logger.DEBUG);
			}
			String processTaxonomy= pfh.getValue(Constants.PROCESS_TAXONOMY).trim();
			/**if processTaxonomy flag is not set then default value true indicating perform processTaxonomy will be used.*/
			if(processTaxonomy != null)
			{	
				Variables.processTaxonomy = Utility.toBoolean(processTaxonomy);
				Logger.log("Variables.processTaxonomy = " + Variables.processTaxonomy,Logger.DEBUG);
			}
			if((null == pfh.getValue(Constants.orgNames).trim()) || (null == pfh.getValue(Constants.orgHistory).trim()))
			{
				Logger.log("HomoloGene configuration parameters not found in server.properties file", Logger.WARNING);
				Logger.log("Default values for the same will be used",Logger.WARNING);
			}
			Variables.serverProperties.put(Constants.orgNames,pfh.getValue(Constants.orgNames).trim());
			Variables.serverProperties.put(Constants.orgHistory,pfh.getValue(Constants.orgHistory).trim());
			
			Variables.serverTables = pfh.getValue(Constants.SERVER_TABLES);
			Variables.caCoreSystemTables = pfh.getValue(Constants.CACORE_SYSTEM_TABLES);
			
			/**Commented as this are caFE specific. Overridden in FEBuilder*/
			
			
//			/** Read GO configuration specific parameters from server.properties file*/
//			Variables.serverProperties.put(Constants.XML_ROOT,pfh.getValue(Constants.XML_ROOT).trim());
//			Variables.serverProperties.put(Constants.PARENT_TERMID,pfh.getValue(Constants.PARENT_TERMID).trim());
//			Variables.serverProperties.put(Constants.PARENT_TERMNAME,pfh.getValue(Constants.PARENT_TERMNAME).trim());
//			
//
//			/** Read Homologene specific parameters from server.properties file*/
//			Variables.serverProperties.put(Constants.CUT_OFF,pfh.getValue(Constants.CUT_OFF).trim());
//			
//			/** Read configuration parameters for taxdmp.zip file. The parameters will store the 
//			 * values of NAMES and HISTORY file which are to be parsed for reading taxonomy ids	 */
//			if((null == pfh.getValue(Constants.orgNames).trim()) || (null == pfh.getValue(Constants.orgHistory).trim()))
//			{
//				Logger.log("HomoloGene configuration parameters not found in server.properties file", Logger.WARNING);
//				Logger.log("Default values for the same will be used",Logger.WARNING);
//			}
//			Variables.serverProperties.put(Constants.orgNames,pfh.getValue(Constants.orgNames).trim());
//			Variables.serverProperties.put(Constants.orgHistory,pfh.getValue(Constants.orgHistory).trim());
//			
//			/** Read caArray database connection parameters*/
//			Variables.serverProperties.put(Constants.caArrayUserName,pfh.getValue(Constants.caArrayUserName).trim());
//			Variables.serverProperties.put(Constants.caArraypassword,pfh.getValue(Constants.caArraypassword).trim());
//			Variables.serverProperties.put(Constants.caArrayDatabaseUrl,pfh.getValue(Constants.caArrayDatabaseUrl).trim());

			/** deleteDownloadedFiles is by default false. It can be initialised to
			 * the parameter read from the server.properties file */
			if(pfh.getValue(Constants.deleteDownloadedFiles).trim() != null)
			{
				Variables.deleteDownloadedFiles = Utility.toBoolean(pfh.getValue(Constants.deleteDownloadedFiles).trim());
				Logger.log("deleteDownloadedFiles = " + Variables.deleteDownloadedFiles,Logger.DEBUG);
			}
			
			
			
			Logger.log("Loaded Properties from Properties File " + fileName, Logger.INFO);
		}
		catch(NonFatalException nfe)
		{
			Logger.log("Non Fatal Error Loading Properties from Properties File " + nfe.getMessage(),Logger.WARNING);
		}
		catch(ApplicationException ae)
		{
			Logger.log("Application Error Loading Properties from Properties File " + ae.getMessage(),Logger.WARNING);
		}
		catch(Exception e)
		{
			Logger.log("Exception while Loading Properties from Properties File " + e.getMessage(),Logger.WARNING);
		}
		
	}
	
	/**
	 * Method to make database server specific settings This is important because MySQL and oracle have different 
	 * date formats and functions for reading and writing date from the queries. Also null character to be specified 
	 * in the files to be loaded by mysqlimport and sqlloader are dirrerent for mysqlloader it is '\N' and for 
	 * Oracle sqlloader it is blank. 
	 */
	protected static void makeDBServerSpecificSettings()
	{
		if(true == Variables.dbIdentifier.equals(Constants.ORACLE))
		{
			/** This variable is used by simpleDateFormat function to format the date when it is to be used for 
			 * writing in the query sent to the database */
			Variables.dateString= "dd-MMM-yyyy";
			/** This variable sets the null character to be written to the file which is to be given to the database
			 * loader for loading into the database	 */
			Variables.dbSpecificNullCharacter = "";
			/** For to_date function in Oracle the query needs to have the format string specifying the format in 
			 * which date is present in the query. This is stored in the variables below */
			Variables.dateFormat = "mm/dd/yyyy";
			/** When database is to be queries for dates which are stored in string format in the code we need to 
			 * give database specific date conversion functio in the query which is specified in the below variable */
			Variables.dateFunction = "to_date";
		}
		else if (true == Variables.dbIdentifier.equals(Constants.MYSQL))
		{
			/** This variable is used by simpleDateFormat function to format the date when it is to be used for 
			 * writing in the query sent to the database */
			Variables.dateString = "yyyy-MM-dd";
			/** This variable sets the null character to be written to the file which is to be given to the database
			 * loader for loading into the database	 */
			Variables.dbSpecificNullCharacter = "\\N";
			/** For format_date function in MySQL the query needs to have the format string specifying the format in 
			 * which date is present in the query. This is stored in the variables below */
			Variables.dateFormat = "%Y/%m/%e";
			/** When database is to be queries for dates which are stored in string format in the code we need to 
			 * give database specific date conversion function in the query which is specified in the below variable */
			Variables.dateFunction = "date_format";
		}
	}
	
	/**
	 * This method is used to make the check whether all required configuration files,scripts and utilities are 
	 * present in correct directories or not.This is important so that all required files are checked for 
	 * exisistance and permissions ensuring that later on during run they will be guaranteed to be found at required 
	 * locations.In case of missing files the process will exit by logging appropriate message in logger and 
	 * displaying on screen.
	 */
	public void setupCheckBeforeRun()
	{
		try
		{
		/** Check if all script files are there in the directory and can be read */
		String fileName = "";
		if(true == Variables.dbIdentifier.equalsIgnoreCase(Constants.ORACLE))
		{
			/** Check scripts specific to Oracle */
			fileName=Variables.currentDir + Constants.fileSep + "Scripts"
			+ Constants.fileSep +pfh.getValue(Constants.TABLE_CREATION_FILE_ORACLE);
			Utility.checkFile(fileName);
			
			fileName=Variables.currentDir + Constants.fileSep + "Scripts"
			+ Constants.fileSep +pfh.getValue(Constants.CONSTRAINT_DROP_FILE_ORACLE);
			Utility.checkFile(fileName);
		}
		else if(true == Variables.dbIdentifier.equalsIgnoreCase(Constants.MYSQL))
		{
			/** Check scripts specific to MySQL */
			fileName=Variables.currentDir + Constants.fileSep + "Scripts"
			+ Constants.fileSep +pfh.getValue(Constants.TABLE_CREATION_FILE_MYSQL);
			Utility.checkFile(fileName);
			
			fileName=Variables.currentDir + Constants.fileSep + "Scripts"
			+ Constants.fileSep +pfh.getValue(Constants.CONSTRAINT_DROP_FILE_MYSQL);
			Utility.checkFile(fileName);
		}
		/** Check whether comman scripts are present and have read permissions */
		fileName=Variables.currentDir + Constants.fileSep + "Scripts"
		+ Constants.fileSep +pfh.getValue(Constants.CONSTRAINT_CREATION_FILE);
		Utility.checkFile(fileName);
		
		/** If mode is Update mode then check if all configuration files required for parsers are present at 
		 * correct locations */
		if(true == Variables.updateMode)
		{
			Utility.checkFile(Variables.currentDir + Variables.fileSep + "Config" + Variables.fileSep + Variables.taxonomyCmdFile);
			Utility.checkFile(Variables.currentDir + Variables.fileSep + "Config" + Variables.fileSep + "server.properties");
			Utility.checkFile(Variables.currentDir + Variables.fileSep + "Config" + Variables.fileSep + Constants.unigeneAbbreviatedOrgFile);
			
			/** Check if all DTDs required for entrezgene datasource's datafile are present */
			Utility.checkFile(Variables.currentDir + Variables.fileSep + "NCBI_BioSource.mod");
			Utility.checkFile(Variables.currentDir + Variables.fileSep + "NCBI_Entity.mod");
			Utility.checkFile(Variables.currentDir + Variables.fileSep + "NCBI_Entrezgene.dtd");
			Utility.checkFile(Variables.currentDir + Variables.fileSep + "NCBI_Entrezgene.mod");
			Utility.checkFile(Variables.currentDir + Variables.fileSep + "NCBI_Gene.mod");
			Utility.checkFile(Variables.currentDir + Variables.fileSep + "NCBI_General.mod");
			Utility.checkFile(Variables.currentDir + Variables.fileSep + "NCBI_Protein.mod");
			Utility.checkFile(Variables.currentDir + Variables.fileSep + "NCBI_Pub.mod");
			Utility.checkFile(Variables.currentDir + Variables.fileSep + "NCBI_RNA.mod");
			Utility.checkFile(Variables.currentDir + Variables.fileSep + "NCBI_SeqCode.mod");
			Utility.checkFile(Variables.currentDir + Variables.fileSep + "NCBI_Seqloc.mod");
			
			File fCheck = new File(Variables.currentDir );
			String fileList[];
			String conFileName = null;
			fileList = fCheck.list();
			/** As specified in the deployment guide server administrator is supposed to copy the correct versio of 
			 * gene2xml utility from RunScripts folder into the current folder from where server will run. Below is
			 * the code which checks whether any file begining with "gene2xm" is there in local directory. There is 
			 * no specific check for file name as they differ based on the user where server is being run.
			 */
			for (int i = 0; i < fileList.length; i++) 
			{
				if(fileList[i].startsWith(Constants.GENE2XML))
				{
					conFileName = fileList[i];
					break;
				}
			}
			/** In update mode if gene2xml utility is not present in current directory execution will terminate*/
			if(null == conFileName)
			{
				Logger.log("gene2xml conversion utility missing in user directory, exiting run",Logger.FATAL);
				System.out.println("gene2xml conversion utility missing in user directory, exiting run");
				System.exit(1);
			}

		}	
		
		/** If mode is Update or Add Chip then check if command file is found in the directory and the directory 
		 * has permissions to create and write to new file */
		if((true == Variables.updateMode) || (true ==  Variables.addChip))
		{
			/** check if command file is present and can be read */
			Utility.checkFile(Variables.CommandFile);
			fileName = Variables.currentDir + Variables.fileSep + "testFile.txt";
			
			/** Check if locally file can be created and written in current directory */
			try
			{
				/** Check permission for creating file writer*/
				FileWriter fw = new FileWriter(fileName);
				fw.close();
				File testfile = new File(fileName);
				testfile.delete();
			}
			catch(IOException e)
			{
				/** If file can not be created in the current directory then it is not possible to go ahead with the 
				 * server execution. Server writes parsed data to local files which is loaded into database using loaders */
				Logger.log("Failed to create file writer, exiting run",Logger.FATAL);
				System.out.println("Failed to create a file writer in current directory, exiting run" + e.getMessage());
				System.exit(1);
			}
		}
		Logger.log("setupCheckBeforeRun successfully completed",Logger.DEBUG);
		}
		catch(ApplicationException ae)
		{
			System.out.println("Exception in Setup Check: " +ae.getMessage());
			System.exit(1);
		}
	}
	
	/** 
	 * handles any fatal exception and exits after doing the clean up 
	 */
	public void handleFatalException() 
	{
		Logger.log("Fatal exception occured in Server run. terminating the execution",Logger.DEBUG);
		/** clean up consists of calling disconnect on the dbManager's connection object*/
		try
		{
			dbInterface.disconnect();
		} 
		catch (FatalException fe)
		{
			fe.printException();
		}
		Variables.endJobTime = System.currentTimeMillis();
		Long elapsedTime = new Long (Variables.endJobTime-Variables.startJobTime);
		Logger.log("Total time taken for this job = " + elapsedTime,Logger.INFO);
		System.exit(1);
	}
	
}
