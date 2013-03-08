/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java com.dataminer.server.jobmanager.BaseBuilder</p>
 */

package edu.wustl.geneconnect.builder;

import com.dataminer.server.exception.ApplicationException;
import com.dataminer.server.globals.Constants;
import com.dataminer.server.globals.Utility;
import com.dataminer.server.globals.Variables;
import com.dataminer.server.io.PropertiesFileHandeler;
import com.dataminer.server.jobmanager.BaseBuilder;
import com.dataminer.server.log.Logger;

import edu.wustl.geneconnect.GeneConnectServerConstants;
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
		if (Variables.calculateSummary)
		{
			// rename the base _U tables
			dbInterface.executeScriptFile("." + Variables.fileSep + GeneConnectServerConstants.SCRIPTS_FOLDER_NAME + Variables.fileSep
					+ GeneConnectServerConstants.BASE_U_TABLES_RENAME_SCRIPT_FILENAME );
			Logger.log("Base _U tables dropped successfully.", Logger.INFO);
			// create again _U tables
			dbInterface.executeScriptFile("." + Variables.fileSep + GeneConnectServerConstants.SCRIPTS_FOLDER_NAME + Variables.fileSep
					+ GeneConnectServerConstants.BASE_U_TABLES_CREATION_SCRIPT_FILENAME );
			Logger.log("Base _U tables dropped successfully.", Logger.INFO);
			SummaryCalculator summaryCalculator = new SummaryCalculator();
			summaryCalculator.calculateSummary();
		}
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
		super.loadProperties();
		try
		{
			/**Load actual properties now*/
			String fileSep = System.getProperty("file.separator");
			String fileName = Variables.currentDir + fileSep + "Config" + fileSep + Constants.serverPropertiesFile;
			PropertiesFileHandeler pfh = new PropertiesFileHandeler(fileName);
			
			/** set the mode of operation for server as update mode**/
			Variables.updateMode = true;

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
			
			String calculateSummary = pfh.getValue(Constants.CALCULATE_SUMMARY).trim();
			if (calculateSummary != null)
			{
				Variables.calculateSummary = Utility.toBoolean(calculateSummary);
			}
			
			Logger.log("Loaded Properties from Properties File"+Variables.subject,Logger.INFO);
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
