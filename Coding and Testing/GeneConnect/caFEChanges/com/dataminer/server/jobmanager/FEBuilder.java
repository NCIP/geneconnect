/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>ClassName: java com.dataminer.server.jobmanager.Builder</p> 
 */

package com.dataminer.server.jobmanager;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

import com.dataminer.server.exception.ApplicationException;
import com.dataminer.server.exception.FatalException;
import com.dataminer.server.exception.NonFatalException;
import com.dataminer.server.ftp.FTP;
import com.dataminer.server.ftp.FileInfo;
import com.dataminer.server.globals.Constants;
import com.dataminer.server.globals.Variables;
import com.dataminer.server.io.FileOutput;
import com.dataminer.server.io.PropertiesFileHandeler;
import com.dataminer.server.log.Logger;
import com.dataminer.server.mail.mailsender;
import com.dataminer.server.parser.CreateSchemaParser;
import com.dataminer.server.postwork.ManageCaCoreSystemTables;

/**
 * This class instantiates Download Manager, Parser Managers, Data Load Manager to 
 * create threads, each of which will start multiple threads to carry out the 
 * respective functionality of downloading, parsing and loading data from data sources. 
 * It also stores instance of DBManager which will be used to establish connection with 
 * the database and execute different queries as well as perform specific functions 
 * like create _U tables, drop original tables, rename _U tables etc. it will be used 
 * mainly for updating the database through different queries. 
 * @author       Meghana Chitale
 * @version      1.0
 */
public class FEBuilder extends BaseBuilder
{
		
	/** Instance of caCoreTable Manager which will populate caCore tables after data 
	 * loading in main tables and postwork is over */ 
	private ManageCaCoreSystemTables m_caCorePwManager;
	
	/** Constructor Method */
	public FEBuilder() 
	{
		/** Initialise the instance of caCoreSystem Table Manager by passing it the 
		 * reference of dbInterface */
		m_caCorePwManager = new ManageCaCoreSystemTables(dbInterface);
	}
	/**
	 * Implementation of Abstract method of BaseBuilder.
	 * 
	 */
	public void preProcessing()
	{
		/** In case of Update and Add Chip mode create organismTaxonomyMap parser and 
		 * Update taxonomy information Also for Add Chip and update mode read the 
		 * postwork dependency sequence and store it in map. Also set names of summary 
		 * tables to _U based on what tables are going to get modified during postwork */
		if(false == Variables.createDBSchema)
		{		
			String fileName = Variables.currentDir + Constants.fileSep + "Config"
			+ Constants.fileSep +Constants.postWorkThreadSequence;
			/**function below will intialise global map storing the postwork dependency sequence 
			 * and will store info about what postwork is to be calculated and in what 
			 * dependency order.*/
			ReadPostWorkExecutionSequence(fileName);
			
			/** This function initialises the table names which are to be calculated in 
			 * postwork. This will be used for creation and droping of _U and original 
			 * tables It will give postwork to be configured based on the configuration 
			 * file even though user will not use it every time. Advanced user can use it 
			 * or else it will be useful in postwork recovery mode.*/
			setSummaryTableNames();
		}
		
		/** If update mode is selcted then final FileList is to be generatedalong with 
		 * initialising the tables names to _U for post work based on which summary 
		 * tables are to be modified	 */
		if(Variables.updateMode)
		{
			/** This function sets the basetable names in the Variables file depending 
			 * on the Command file.*/
			setBaseTableNames();
		}
		/** In case of Add Chip mode the entire vector of fileInfo objects needs to 
		 * be added into the list of objects which are to be considered for parsing 
		 * and downloading. In case of Update mode some data sources which are not 
		 * modified since last update need not be processed again and hence all 
		 * FileInfo objeects may not be added to the list	 */ 
		else if(Variables.addChip)
		{
			Variables.fileInfoList.addAll(files);
		}
		
		/** In case of Oracle the iformation about table spaces before updating data 
		 * is stored in HashTable*/ 
		if(Variables.dbIdentifier.equalsIgnoreCase(Constants.ORACLE))
		{
			dbInterface.fillTableSpaceData(true);
		}
		validateConfigurationProperties();
		createTables();
	}
	/**
	 * This function will check whether all the required property values are set in the
	 * server.properties file for the data sources which will be parsed in the current run
	 */
	public void validateConfigurationProperties()
	{
		if(true == Variables.goupdate)
		{
			if((null == Variables.serverProperties.get(Constants.XML_ROOT)) || 
					(null == Variables.serverProperties.get(Constants.PARENT_TERMID)) 
					|| (null == Variables.serverProperties.get(Constants.PARENT_TERMNAME)))
			{
				Logger.log("GO configuration parameters incorrectly specified in server.properties file", Logger.FATAL);
				System.exit(1);
			}
		}
		if(true == Variables.hmlgupdate)
		{
			if(null == Variables.serverProperties.get(Constants.CUT_OFF))
			{
				Logger.log("HomoloGene configuration parameters incorrectly specified in server.properties file", Logger.FATAL);
				System.exit(1);
			}
		}
		if(true == Variables.addChip)
		{
			if((null == Variables.serverProperties.get(Constants.caArrayUserName)) || 
					(null == Variables.serverProperties.get(Constants.caArraypassword)) 
					|| (null == Variables.serverProperties.get(Constants.caArrayDatabaseUrl)))
			{
				Logger.log("caArray configuration parameters incorrectly specified in server.properties file", Logger.FATAL);
				System.exit(1);
			}
		}
	}
	
	/**
	 * Sets the names of the base tables In the Variables file the base table names are
	 * set depending on the Command file.
	 */
	private void setBaseTableNames()
	{
		String update="_U";
		
		/** If any of the unigene files is there in the list of FileInfo objects to 
		 * e processed then Variables.ugupdate will be true and the names of 
		 * corresponding tables used by this data source will be set to _U postfix */ 
		if(Variables.ugupdate) 
		{
			Logger.log("Unignene table names set to _U",Logger.INFO);
			Variables.ugBaseTableName 		+= update;
			Variables.ugExpressTableName 	+= update;
			Variables.ugProtsimTableName 	+= update;
			Variables.ugSequenceTableName 	+= update;
			Variables.ugHistoryTableName 	+= update;   
		}
		/** If any of the files from EntrezGene data source is to be parsed then the 
		 * corresponding tables names will be set with _U postfix. Here onwards these 
		 * new _U tables are created and used to pupulate data. Later on when entire 
		 * processing completes successfully then these _U tables are renamed in place 
		 * of original tables */
		if(Variables.llupdate)  
		{
			Variables.locusBaseTableName 	+= update;
			Variables.llGeneNamesTableName 	+= update;
			Variables.llGoidTableName 		+= update;
			Variables.llMapTableName 		+= update;
			Variables.llOmimTableName 		+= update;
			Variables.llPhenotypeTableName 	+= update;
			Variables.llPmidTableName 		+= update;
			Variables.llUgTableName 		+= update;
			Variables.locusStsTableName 	+= update;
			Variables.locusFlyTableName 	+= update;
			Variables.llHistoryTableName 	+= update;
		}
		/** If HomoloGene data source is getting updated then initialise all the 
		 * corresponding tables to _U */
		if(Variables.hmlgupdate) 
		{
			Variables.homologeneTempTableName	+= update;
			Variables.homologeneXMLTableName	+= update;
			Variables.orthologTableName			+= update;
			Variables.orthologStartGeneName		+= update;
		}
		/** If UniSTS data source is getting updated then set all the corresponding 
		 * table names to _U */
		if(Variables.ustupdate) 
		{
			Variables.uniStsBaseTableName		+= update;
			Variables.uniStsAliasTableName		+= update;
			Variables.uniStsAccessionTableName	+= update;
		}
		/** If dbSNP data source is getting updated then set all the corresponding 
		 * table names to _U */
		if(Variables.dbsnpupdate)
		{
			Variables.dbSnpBaseTableName	+= update;
			Variables.dbSnpContigTableName	+= update;
			Variables.dbSnpFxnTableName		+= update;
			Variables.dbSnpContigMapTableName	+= update;
			Variables.dbSnpLocusTableName	+= update;
		}
		/** System term table names are initialised to _U alays since they are 
		 * populated by multiple data sources. Each time except the terms which 
		 * will be repopulated by current data sources all the other terms are copied
		 * in _U at the time of their creation. 
		 */ 
		Variables.termTableName	+= update;
		Variables.treeTableName	+= update;
		Logger.log("Renamed the Base tables to _U ",Logger.INFO);
	}
	
	/** Set the summary table names to _U if the tables are to be modified in the 
	 * current run by doing postwork 
	 * on them*/
	private void setSummaryTableNames()
	{
		String update="_U";
		if(Variables.geneinfoPostWork)
		{
			Variables.geneinfoTableName	+= update;
		}
		if(Variables.geneinfo_summaryPostWork)
		{
			Variables.geneinfo_summaryTableName	+= update;
		}
		if(Variables.geneinfo_marray_summaryPostWork)
		{
			Variables.geneinfo_marray_summaryTableName += update;
		}
		if(Variables.chipdescriptionPostWork)
		{
			Variables.chipdescriptionTableName += update;
		}
		if(Variables.chipinfo_homoloPostWork)
		{
			Variables.chipinfoHomoloTableName += update;
		}
		if(Variables.chipinfo_omimPostWork)
		{
			Variables.chipinfoOmimTableName	+= update;
		}
		if(Variables.chipinfo_termPostWork)
		{
			Variables.chipinfoTermTableName	+= update;
			Variables.chipinfoTermWithDuplicatesTableName += update;
		}
		if(Variables.homologenePostWork)
		{
			Variables.homologeneTableName += update;;
		}
		
		Logger.log("Initialised Summary table names to _U",Logger.INFO);
	}
	
	
	/**
	 * Function to create Tables if the Server is in createDBSchema mode. It
	 * will also call initialize tables to initialize the database tables.
	 * If the server is in update mode then it will truncate the required
	 * tables.
	 */
	public void createTables()
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
				csp.parseFile(Constants.feCreationFileOracle);
			}
			else if(Variables.dbIdentifier.equals(Constants.MYSQL))
			{
				csp.parseFile(Constants.feCreationFileMysql);
			}
			
			/** From the create schema parser get the table definition map which will
			 * store information about all
			 * the tables and their creation scripts */
			hmTableCreation = csp.getTableDefinitionMap();
			
			/** If create Database schema mode is used then create all the base and 
			 * summary tables based on the scripts */
			if(true == Variables.createDBSchema)
			{
				try
				{
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
				catch (Exception exp) 
				{
					Logger.log("Error while executing exception" + exp.getMessage(), Logger.INFO);
				}
			}
			
			/** droping _u tables should be done intellegiently when going ahead with recovery mode*/
			if(false == Variables.createDBSchema)
			{
				dbInterface.drop_UTables();
			}
			
			/** In case of Update mode we need to create _U tables for the data sources 
			 * which are to be modified But for add chip mode we append data to existing 
			 * chipinformation table and hence we do not need to create _U table for chipinformation
			 */ 
			if(true == Variables.updateMode)
			{
				Logger.log("create tables for update entered ",Logger.INFO);
				dbInterface.prepareTables(hmTableCreation);
			}
			
			/** If it is Add chip or update mode then we check whether postwork flag is set 
			 * to true or not. If so then we create _U tables for the postwork based on the 
			 * postwork dependency structure file. This file decides postwork for which 
			 * tables is to be carried
			 */
			if(false == Variables.createDBSchema)
			{
				if(Variables.postWork)
				{
					Logger.log("Variables.postwork " + Variables.postWork,Logger.DEBUG);
					/** This function will use table definitions from script XML and will 
					 * create _U tables for postwork as required in the current run.
					 */
					dbInterface.preparePostworktables(hmTableCreation);
				}
			}
		} 
		catch(FatalException fatal)
		{
			Logger.log("Fatal Exception in create tables function ",Logger.FATAL);
			Logger.log(fatal.getMessage(),Logger.DEBUG);
		}
	}
	
	


	/**
	 * 
	 * Implementation of Abstract method of BaseBuilder.
	 * This function will be called after download parsing, loading of base tables
	 * and postwork summary table computation is over. It will drop original tables
	 * and rename the _U tables to original ones. If postwork is done in current
	 * run then the above will be done for summary tables as well.  
	 */
	public void postProcessing()
	{
		
		/**drop constraints and indices. drop original tables and rename _U tables.*/
		dbInterface.baseTableUpdate();

		/**If postwork is performed during current run then _U table remaning needs to be carried
		 * for those tables also
		 */
		if(true == Variables.postWork)
		{
			/** drop constraints and original table. Rename _U tables of Postwork to original tables*/
			dbInterface.summaryTableUpdate();
		}
		/** Use constraint definitions from script xmls and put constraints on the base 
		 * tables which have been renamed as original tables
		 */
		putConstraintsOnTables();

	}

	/** This function will drop original caCORE  tables and it will rename the new caCORE _U 
	 * tables as originals. Also it will put constrainst on the caCORE tables.
	 */
	private void postProcessingForcaCoreTables()
	{
		/**drop constraints and indices. drop original tables and rename _U tables.*/
		dbInterface.caCoreSystemTableUpdate();
		/** Read constraints from XML script and put them on caCORE tables which have been newly created*/
		m_caCorePwManager.putConstraintsOncaCoreSystemTables();
	}

	
	
	/**
	 * This method will initialise the caCorePostWork Manager which will populate the
	 * caCore tables on initialisation.
	 * @return It returns whether caCore tables were successfully populated using the
	 * current data fro base and summary tables
	 */
	private boolean createCaCoreSystemTables()
	{
		boolean isSuccess = false;
		isSuccess = m_caCorePwManager.populateCaCoreSystemTables();
		Logger.log("isSuccess value for populateCaCoreSystemTables is " + isSuccess,Logger.DEBUG);
		return isSuccess;
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
		String dbName;
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
			Logger.log("checkVal Value for " + fileInfo.getDatabaseType().toString() + " = " + checkVal,Logger.INFO);
			/** checkVal=true---> We need to ftp files of the database while
			 *  checkVal=false---> We don't need to ftp files of the database.*/
			
			dbName = fileInfo.getDatabaseType();
			if (dbName.equalsIgnoreCase(Constants.UNIGENE) && (true == checkVal))
			{
				Variables.ugupdate=true;
			}
			else if (dbName.equalsIgnoreCase(Constants.ENTREZGENE)&& (true == checkVal))
			{
				Variables.llupdate=true;
			} 	
			else if (dbName.equalsIgnoreCase(Constants.HOMOLOGENE)&& (true == checkVal))
			{
				Variables.hmlgupdate=true;
			} 
			else if (dbName.equalsIgnoreCase(Constants.GO)&& (true == checkVal))
			{
				Variables.goupdate=true;
			}
			else if (dbName.equalsIgnoreCase(Constants.UNISTS)&& (true == checkVal))
			{
				Variables.ustupdate=true;
			}
			else if (dbName.equalsIgnoreCase(Constants.DBSNP)&& (true == checkVal))
			{
				Variables.dbsnpupdate=true;
			}
		}
		
		/** Now if all files in a particular data source are not modified since last run then they all can be 
		 * removed from the list of files to be processed
		 */
		itr = files.iterator();
		while (itr.hasNext())
		{
			fileInfo = (FileInfo)itr.next();
			/** If no file from UNIGENE data source has updated since last run then the Variables.ugupdate
			 * flag will be false indicating that the UNIGENE resource need not be downloaded in current
			 * run. Henec we can navigate the fileInfo list and remove the files which are from UNIGENE
			 * source. Same checking will be done for other sources also and the files from that data
			 * source will be removed if it is not to be updated */
			if((false == Variables.ugupdate) && fileInfo.getDatabaseType().equalsIgnoreCase(Constants.UNIGENE))
			{
				itr.remove();
			}
			else if ((false == Variables.llupdate) && fileInfo.getDatabaseType().equalsIgnoreCase(Constants.ENTREZGENE))
			{
				itr.remove();
			}
			else if ((false == Variables.hmlgupdate) && fileInfo.getDatabaseType().equalsIgnoreCase(Constants.HOMOLOGENE))
			{
				itr.remove();
			}
			else if ((false == Variables.goupdate) && fileInfo.getDatabaseType().equalsIgnoreCase(Constants.GO))
			{
				itr.remove();
			}
			else if ((false == Variables.ustupdate) && fileInfo.getDatabaseType().equalsIgnoreCase(Constants.UNISTS))
			{
				itr.remove();	
			}
			else if ((false == Variables.dbsnpupdate) && fileInfo.getDatabaseType().equalsIgnoreCase(Constants.DBSNP))
			{
				itr.remove();	
			}
		}
		/** Add the names of fileInfo objects obtained above into the Hashtable fileInfoList.*/
		Variables.fileInfoList.addAll(files);
		
	}
	
	
	/**
	 * Method to add constaints on tables which have been populated in Update mode. 
	 * Constraints wil be read from script files and then they will be set
	 */
	private void putConstraintsOnTables()
	{
		try
		{
			Logger.log("putConstraintsOntables() called ",Logger.INFO);
			/** Create schema parser to parse the constrainst xml*/
			CreateSchemaParser csp = new CreateSchemaParser();
			HashMap hmTableConstraints = null;
			/** Call parse method on schema parser will correct file name to parse*/
			csp.parseFile(Constants.feConstraintCreationFile);
			hmTableConstraints = csp.getDataSourceConstraints();
			
			/** If update mode is true then base tables relating to each of the data sources
			 * get modified. After renaming the _U tables to originals put constrainst on base tables
			 * The foreign key constraints are put per source basis i.e. tables in a datasource refer to 
			 * the main/key table like UNIGENE/ENTREZGENE in that particular data source */
			if(Variables.updateMode)
			{
				/** If UNIGENE data source has been modified in current run then put constrains on it.*/
				if(Variables.ugupdate)
				{
					/** From the constraint map get the constraints for UNIGENE data source*/
					Vector vecConstraint = (Vector)hmTableConstraints.get("UNIGENE");
					if(vecConstraint!=null)
					{
						
						for (Iterator iter = vecConstraint.iterator(); iter.hasNext();) 
						{
							String element = (String) iter.next();
							dbInterface.executeUpdate(element);
						}
					}
				}
				/** If ENTREZGENE data source has been modified in current run then put constrains on it.*/
				if(Variables.llupdate)
				{
					/** From the constraint map get the constraints for UNIGENE data source*/
					Vector vecConstraint = (Vector)hmTableConstraints.get("ENTREZGENE");
					if(vecConstraint != null)
					{
						
						for (Iterator iter = vecConstraint.iterator(); iter.hasNext();) 
						{
							String element = (String) iter.next();
							dbInterface.executeUpdate(element);
						}
					}
				}
				/** If HOMOLOGENE data source has been modified in current run then put constrains on it.*/
				if(Variables.hmlgupdate)
				{
					/** From the constraint map get the constraints for UNIGENE data source*/
					Vector vecConstraint = (Vector)hmTableConstraints.get("HOMOLOGENE");
					if(vecConstraint!=null)
					{
						
						for (Iterator iter = vecConstraint.iterator(); iter.hasNext();) 
						{
							String element = (String) iter.next();
							dbInterface.executeUpdate(element);
						}
					}
				}
				/** If UNISTS data source has been modified in current run then put constrains on it.*/
				if(Variables.ustupdate)
				{
					/** From the constraint map get the constraints for UNIGENE data source*/
					Vector vecConstraint = (Vector)hmTableConstraints.get("UNISTS");
					if(vecConstraint!=null)
					{
						
						for (Iterator iter = vecConstraint.iterator(); iter.hasNext();) 
						{
							String element = (String) iter.next();
							dbInterface.executeUpdate(element);
						}
					}
				}
				/** If DBSNP data source has been modified in current run then put constrains on it.*/
				if(Variables.dbsnpupdate)
				{
					/** From the constraint map get the constraints for UNIGENE data source*/
					Vector vecConstraint = (Vector)hmTableConstraints.get("DBSNP");
					if(vecConstraint != null)
					{
						
						for (Iterator iter = vecConstraint.iterator(); iter.hasNext();) 
						{
							String element = (String) iter.next();
							dbInterface.executeUpdate(element);
						}
					}
				}
				/** Constrainst no system term tables are put and dropped even if you are running
				 * server for any data source*/
				Vector vecConstraint = (Vector)hmTableConstraints.get("TERMDATA");
				if(vecConstraint != null)
				{
					
					for (Iterator iter = vecConstraint.iterator(); iter.hasNext();) 
					{
						String element = (String) iter.next();
						dbInterface.executeUpdateSupressError(element);
					}
				}
			} 
		} 
		catch(FatalException fatal)
		{
			/** If any of the executeUpdate method above used to put constraints on tables fails
			 * throwing SQLException then the method executeUpdate throws FatalException which is caught
			 * here. Since that method increments error count by one before throwing fatal exception
			 * there is no need to do that here
			 */
			Logger.log("Falal Exception when putting constraints on base tables " + fatal.getMessage(),Logger.FATAL);
		}
	}
	
	
	/**
	 * This method reads input file and populates hash map to store postwork dependency
	 * structure. The structure of that file is:
	 * UNIT_ID (e.g.GENEINF)	"TAB SEPARATED LIST OF POST WORK UNIT ID WHOSE COMPLETION 
	 * 							TRIGEGRS EXECUTION OF UNIT ON LHS"
	 * The units which don't depend on any other postwork table to begin execution have
	 * nothing on RHS
	 * @param fileName :Name of file having postwork thread dependency structure
	 */
	private void ReadPostWorkExecutionSequence(String fileName)
	{
		try
		{
			BufferedReader fReader = new BufferedReader(new FileReader(fileName));
			
			String line;
			while((line = fReader.readLine()) != null)
			{
				StringTokenizer sTok = new StringTokenizer(line,"\t");
				try
				{
					/** There should be at least one token on the line indicating the 
					 * postwork unit identifier
					 */
					if(sTok.countTokens() >= 1)
					{
						String workThread = sTok.nextToken();
						Vector vecTriggers = new Vector();
						/** Each of the unit id on RHS will be added to the vector */
						while(sTok.hasMoreTokens())
						{
							String threadTrigger = sTok.nextToken();
							vecTriggers.add(threadTrigger);
						}
						/** The vector will be stored against the unit id on LHS. Thus the vector will
						 * give dependencies of the unit id on RHS
						 */
						Variables.hmPostWorkSeq.put(workThread,vecTriggers);
						setPostWorkFlags(workThread);
					}
				}
				catch(NoSuchElementException e)
				{
					Logger.log("Misiing token on line " + line + " in PostWork cofiguration file",Logger.FATAL);
				}
			}
		}
		catch(FileNotFoundException e)
		{
			Logger.log(Constants.unigeneAbbreviatedOrgFile + " file not found ",Logger.FATAL);
			Logger.log(e.getMessage(),Logger.WARNING);
			Variables.errorCount++;
		}
		catch(IOException e)
		{
			Logger.log("IO Exception while reading file " + Constants.unigeneAbbreviatedOrgFile ,Logger.FATAL);
			Logger.log(e.getMessage(),Logger.WARNING);
			Variables.errorCount++;
		}
		
	}

	/**
	 * This function is called each time when new thread Identifier is read from the postwork dependency structure
	 * file. Each thread in postworkDependencysequence represents one summary table which needs to be created by
	 * spawning one thread for the same. This function sets the flag corresponding to that summary table which 
	 * indicates whether that table has to computed under postwork in the current run.
	 * @param threadID It is current summary table identifier read from the postworkDependencyStructure
	 */
	private void setPostWorkFlags(String threadID)
	{
		if(threadID.equalsIgnoreCase(Constants.HOMOLOGENE))
		{
			Variables.homologenePostWork = true;
		}
		else if(threadID.equalsIgnoreCase(Constants.GENEINFO_SUMMARY))
		{
			Variables.geneinfo_summaryPostWork = true;
		}
		else if(threadID.equalsIgnoreCase(Constants.GENEINFO))
		{
			Variables.geneinfoPostWork = true;
		}
		else if(threadID.equalsIgnoreCase(Constants.CHIPDESCRIPTION))
		{
			Variables.chipdescriptionPostWork = true;
		}
		else if(threadID.equalsIgnoreCase(Constants.GENEINFO_MARRAY_SUMMARY))
		{
			Variables.geneinfo_marray_summaryPostWork = true;
		}
		else if(threadID.equalsIgnoreCase(Constants.CHIPINFO_HOMOLO))
		{
			Variables.chipinfo_homoloPostWork = true;
		}
		else if(threadID.equalsIgnoreCase(Constants.CHIPINFO_OMIM))
		{
			Variables.chipinfo_omimPostWork = true;
		}
		else if(threadID.equalsIgnoreCase(Constants.CHIPINFO_TERM))
		{
			Variables.chipinfo_termPostWork =true;
		}
	}
	
	/**
	 * This method will be called when builder class will go out of scope to call
	 * disconnect on the db connection.
	 */
	protected void finalize()
	{
		try
		{
			dbInterface.disconnect();
		} 
		catch (FatalException fe)
		{
			Logger.log("Error while calling disconnect on the dbInterface",Logger.FATAL);
		}
	}
	
	

	/** If Variables.caCoreSystemPostWork is set to true in server.properties file then only the tables used
	 * in caCore like system will be populated. 
	 */	

	public void caCoreProcessing()
	{
		boolean isSuccess = false;
			/** This functioN will call controller class to populate the tables used by caCore System*/
			isSuccess = createCaCoreSystemTables();
			/** If caCore like system's tables were created successfully then only rename original tables. */
			if(true == isSuccess)
			{
				/** This function will rename _U tables created for caCoreSystem will be renamed to original
				 * tables */
				postProcessingForcaCoreTables();
			}
	}
	protected void loadProperties()
	{
		super.loadProperties();
		try
		{
			/** get the mode of operation for server as first command line argument**/
			int serverRunMode = Integer.parseInt(pfh.getValue(Constants.EXECUTION_MODE).trim());
			Logger.log("Mode in which server will run: 1:Update 2:Add Chip 3:Create Database Schema = " + serverRunMode,Logger.INFO);
			/** Based on the mode selected set appropriate variable */
			if(Constants.UPDATE_MODE == serverRunMode)
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
			}
			
			/** If Update or Add Chip modes are selected then their should be 8th parameter on the command line
			 * specifying the name of Command file have source information from which data is to be downloaded */
			if(false == Variables.createDBSchema)
			{
				/** If Command file is not specified for Update and Add Chip mode then ArrayOutOfBound Exception 
				 * will be thrown */
				Variables.CommandFile = pfh.getValue(Constants.COMMAND_FILE_NAME).trim();
				
				if(null == Variables.CommandFile)
				{
					/** If Command file name is not specified on command line in case of Update and Add Chip mode then 
					 * execution will terminate logging correct message */
					Logger.log("Command File path and name is required for modes other than CreateDBSchema mode",Logger.FATAL);
					System.out.println("Command File path and name is required for modes other than CreateDBSchema mode");
					System.exit(1);
				}
			}
			
			String fileSep = System.getProperty("file.separator");
			String fileName = Variables.currentDir + fileSep + "Config" + fileSep + Constants.serverPropertiesFile;
			PropertiesFileHandeler pfh = new PropertiesFileHandeler(fileName);
			/** Read GO configuration specific parameters from server.properties file*/
			Variables.serverProperties.put(Constants.XML_ROOT,pfh.getValue(Constants.XML_ROOT).trim());
			Variables.serverProperties.put(Constants.PARENT_TERMID,pfh.getValue(Constants.PARENT_TERMID).trim());
			Variables.serverProperties.put(Constants.PARENT_TERMNAME,pfh.getValue(Constants.PARENT_TERMNAME).trim());
			

			/** Read Homologene specific parameters from server.properties file*/
			Variables.serverProperties.put(Constants.CUT_OFF,pfh.getValue(Constants.CUT_OFF).trim());
			
			/** Read configuration parameters for taxdmp.zip file. The parameters will store the 
			 * values of NAMES and HISTORY file which are to be parsed for reading taxonomy ids	 */
			if((null == pfh.getValue(Constants.orgNames).trim()) || (null == pfh.getValue(Constants.orgHistory).trim()))
			{
				Logger.log("HomoloGene configuration parameters not found in server.properties file", Logger.WARNING);
				Logger.log("Default values for the same will be used",Logger.WARNING);
			}
			Variables.serverProperties.put(Constants.orgNames,pfh.getValue(Constants.orgNames).trim());
			Variables.serverProperties.put(Constants.orgHistory,pfh.getValue(Constants.orgHistory).trim());
			
			/** Read caArray database connection parameters*/
			Variables.serverProperties.put(Constants.caArrayUserName,pfh.getValue(Constants.caArrayUserName).trim());
			Variables.serverProperties.put(Constants.caArraypassword,pfh.getValue(Constants.caArraypassword).trim());
			Variables.serverProperties.put(Constants.caArrayDatabaseUrl,pfh.getValue(Constants.caArrayDatabaseUrl).trim());
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
}
