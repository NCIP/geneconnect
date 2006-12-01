/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java com.dataminer.server.jobmanager.BaseBuilder</p>
 */

package edu.wustl.geneconnect.builder;

import com.dataminer.server.exception.ApplicationException;
import com.dataminer.server.exception.NonFatalException;
import com.dataminer.server.globals.Constants;
import com.dataminer.server.globals.Utility;
import com.dataminer.server.globals.Variables;
import com.dataminer.server.io.PropertiesFileHandeler;
import com.dataminer.server.jobmanager.BaseBuilder;
import com.dataminer.server.log.Logger;

import edu.wustl.geneconnect.postwork.SummaryCalculator;

/**
 * This  class extends the BaseBuilder and implements GenConnect specific logic
 * for pre-process nad post process.
 * Use default implementaion of downloading ,parsing and loading data
 * provided by BaseBuilder.
 *
 * @author       Sachin Lale
 * @version      1.0
 */
public  class GCBuilder extends BaseBuilder
{
	public void preProcessing()
	{
		createTables();
		//System.exit(0);
		// TODO process blast for GenBank
	}

	/**
	 * The Application using annotaion parser library must implement this method  to
	 * perform applciation specific function after download and parse data.
	 * Such as renaming of '_U' tables etc.
	 *
	 */
	public void postProcessing()
	{
		// dropEntrezTables();
		
		//Invoke Summary table calculator to calculate all-to-all genomic links 
		SummaryCalculator summaryCalculator = new SummaryCalculator();
		summaryCalculator.calculateSummary();
	}

	/**
	 * The Application using annotation parser library must implement this method  to
	 * perform applciation specific caCore system function after download and parse data.
	 * Such as renaming creation of caCore tables, post processing for caCore tables etc.
	 *
	 */
	public void caCoreProcessing()
	{

	}
	/*public void createTables()
		{
			try
			{
				Logger.log("createTables() called ",Logger.INFO);
				*//** Create Schema parser which will parse database creation files and
				 * store information about the tables and their definitions in the maps
				 * which will be used later to create tables  *//*
				CreateSchemaParser csp = new CreateSchemaParser();

				HashMap hmTableCreation = null;
				*//** Select appropriate file names having create database scripts for
				 * Oracle as well as MySQL *//*
				if(Variables.dbIdentifier.equals(Constants.ORACLE))
				{
					csp.parseFile(Constants.feCreationFileOracle);
				}
				else if(Variables.dbIdentifier.equals(Constants.MYSQL))
				{
					csp.parseFile(Constants.feCreationFileMysql);
				}

				*//** From the create schema parser get the table definition map which will
				 * store information about all
				 * the tables and their creation scripts *//*
				hmTableCreation = csp.getTableDefinitionMap();


				*//** droping _u tables should be done intellegiently when going ahead with recovery mode*//*
				if(false == Variables.createDBSchema)
				{
					dbInterface.drop_UTables();
				}

				*//** In case of Update mode we need to create _U tables for the data sources
				 * which are to be modified But for add chip mode we append data to existing
				 * chipinformation table and hence we do not need to create _U table for chipinformation
				 *//*
				if(true == Variables.updateMode)
				{
					Logger.log("create tables for update entered ",Logger.INFO);
					dbInterface.prepareTables(hmTableCreation);
				}


			}
			catch(FatalException fatal)
			{
				Logger.log("Fatal Exception in create tables function ",Logger.FATAL);
				Logger.log(fatal.getMessage(),Logger.DEBUG);
			}
		}*/


	private void dropEntrezTables()
	{
		String[] entrezTableNames = {"entrez_fly","entrez_genenames","entrez_goid",
				"entrez_map","entrez_omim","entrez_phenotype",
				"entrez_pmids","entrez_sts","system_termdata","system_termtree"};
		StringBuffer dropQuery = new StringBuffer();
		for(int i=0;i<entrezTableNames.length;i++)
		{
			dropQuery.append("DROP TABLE ");
			dropQuery.append(entrezTableNames[i]);
			dbInterface.executeUpdate(dropQuery.toString());
			Logger.log("Table dropped: "+entrezTableNames[i],Logger.INFO);
			dropQuery.setLength(0);
		}
	}

	protected void loadProperties()
	{
		try
		{
			String fileSep = System.getProperty("file.separator");
			String fileName = Variables.currentDir + fileSep + "Config" + fileSep + Constants.serverPropertiesFile;
			PropertiesFileHandeler pfh = new PropertiesFileHandeler(fileName);
			/**Load actual properties now*/

			/** set the mode of operation for server as update mode**/
			Variables.updateMode = true;


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

			/** Get the name of Command file have source information from which data is to be downloaded
			 *  If Command file is not specified for Update and Add Chip mode then ArrayOutOfBound Exception
			 *  will be thrown
			 **/

			Variables.CommandFile = pfh.getValue(Constants.COMMAND_FILE_NAME).trim();
			if(null == Variables.CommandFile)
			{
				/** If Command file name is not specified on command line in case of Update and Add Chip mode then
				 * execution will terminate logging correct message */
				Logger.log("Command File path and name is required for modes other than CreateDBSchema mode",Logger.FATAL);
				System.out.println("Command File path and name is required for modes other than CreateDBSchema mode");
				System.exit(1);
			}
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
			if(pfh.getValue(Constants.DBNAME)!=null)
			{
				Variables.dbName = pfh.getValue(Constants.DBNAME).trim();
			}

//			/** Properties used to set whether postwork and caCore table population needs to be done or not */
//			String postWork = pfh.getValue(Constants.POSTWORK).trim();
//			/**if postwork flag is not set then default value true indicating perform postwork will be used.*/
//			if(postWork != null)
//			{
//				Variables.postWork = Utility.toBoolean(postWork);
//				Logger.log("Variables.postWork = " + Variables.postWork,Logger.DEBUG);
//			}
//			/**if caCorePostwork flag is not set then default value true indicating perform caCorepostwork will be used.*/
//			String caCoreSystemPostWork = pfh.getValue(Constants.CACORE_TABLE_CREATION).trim();
//			if(caCoreSystemPostWork != null)
//			{
//				Variables.caCoreSystemPostWork = Utility.toBoolean(caCoreSystemPostWork);
//				Logger.log("Variables.caCoreSystemPostWork = " + caCoreSystemPostWork,Logger.DEBUG);
//			}
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


			/** deleteDownloadedFiles is by default false. It can be initialised to
			 * the parameter read from the server.properties file */
			if(pfh.getValue(Constants.deleteDownloadedFiles).trim() != null)
			{
				Variables.deleteDownloadedFiles = Utility.toBoolean(pfh.getValue(Constants.deleteDownloadedFiles).trim());
				Logger.log("deleteDownloadedFiles = " + Variables.deleteDownloadedFiles,Logger.DEBUG);
			}
			Logger.log("Loaded Properties from Properties File"+Variables.subject,Logger.INFO);
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
