/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java com.dataminer.server.jobmanager.JobManager</p> 
 */
package com.dataminer.server.jobmanager;

import com.dataminer.server.globals.Variables;
import com.dataminer.server.io.PropertiesFileHandeler;
import com.dataminer.server.log.Logger;
import com.dataminer.server.database.ServerStatus;
import com.dataminer.server.exception.ApplicationException;
import com.dataminer.server.exception.FatalException;
import com.dataminer.server.exception.NonFatalException;

import java.sql.SQLException;


/** 
 * JobManager is the driver class of the server program. JobManager holds main working modules like Builder and
 * ServerStatus instances. Builder which also instantiates DBManager is used to get data from the specified data 
 * sources, load it into the database and perform some post processing to precalculate some results. ServerStatus 
 * module is used for sending mail regarding the execution statistics.
 * creating and sending 
 * @author 		Meghana Chitale
 * @version 	1.0
 */
public class JobManager 
{
	/** Builder instance which will be used for creating tables, downloading and parsing data from the specified
	 * data sources */
	private static BaseBuilder builder;
	
	/**
	 * Property File HAndler to read ApplicationConfig.property file.
	 * This static refernce used throughout application. 
	 */
	private static PropertiesFileHandeler pfh;
	
	/** Single ServerStatus instance is used for setting up the performance statistics in the SERVER_STATUS table
	 * and sending mail informing about the same statustics*/
	private static ServerStatus  servStat = ServerStatus.getInstance();
	
	/**
	 * Default constructor 
	 */
	public JobManager()
	{
	}

	/**
	 * This is the entry point for caFE Server application. Based on the parameters provided in Configuration files,
	 * it calls methods to validate the parameters and then calls Build class method to download,parses and load the annotation data.
	 * @param args
	 */
	public static void main(String args[]) 
	{
	    System.out.println("Job Manager Started...");
	    System.out.println("See ./Logs/ErroLog.txt for details.");
	    /** Set the job start time in global variables */
		Variables.startJobTime = System.currentTimeMillis();
		String fileSep = System.getProperty("file.separator");
		String fileName = Variables.currentDir + fileSep + "Config" + fileSep + "ApplicationConfig.properties";
				
		/**
		 * Get value of BUILDER_CLASS key from ApplicationConfig.property. 
		 */
		try
		{
			pfh = new PropertiesFileHandeler(fileName);
		}	
		catch(NonFatalException nfe)
		{
			Logger.log("Non Fatal Error Loading Properties from Properties File " + nfe.getMessage(),Logger.WARNING);
			System.exit(1);
		}
		try
		{
			/**
			 * Instantiate BUILDER_CLASS as specified in ApplicationConfig 
			 * and set property file handler 
			 */	
			
			builder = BaseBuilder.getInstance(pfh.getValue("BUILDER_CLASS"));
			builder.setApplicationPropertyHandler(pfh);
		}
		catch(ApplicationException ae)
		{
			Logger.log("Application Error Loading Properties from Properties File " + ae.getMessage(),Logger.WARNING);
			System.exit(1);
		}
		
		/** This function will check in the working directory whether all the files are present and have correct 
		 * permissions. If required conditions are not satisfied then execution will be terminated by logging 
		 * appropriate message in the logger and also showing the same on the console */
		builder.setupCheckBeforeRun();
		
		
		/**
		 * Perform the task to be done BEFORE downloading and parsing starts
		 * this method has to implement by application to do specific funcions.
		 */
		builder.preProcessing();
		
		
		
		ServerStatus.setExecutionDate();
		
		/** This condition checks if mode of execution is not createDBSchema, if so then it will call the correct
		 * Builder class method to spawn multiple threads to download, parse and load data.
		 */
		if(Variables.createDBSchema == false)
		{
			/**
			 * This function download data from specified data sources, parse it and then load the data into the 
			 * database. it will spawn multiple threads for doing the same.
			 */
			builder.downloadParseAndLoadData();
			Logger.log("Completed loadDataWithMultipleThreads",Logger.INFO);
			Logger.log("Error count before starting postwork " + Variables.errorCount,Logger.INFO);
			
			/**
			 * If there were no errors when downloading,parsing and loading data then the execution will go ahead with 
			 * post work.
			 */
			if(0 == Variables.errorCount)
			{
				Logger.log("Do postwork = " + Variables.postWork,Logger.DEBUG);
				boolean isSuccess = true;
				/** If variable.postWork is set to true indicating that postwork should be performed for current run 
				 * then it will be done. This Variable is by default true because of which postwork will always follow
				 * the parser run. But its value can also be configured from outside by setting in server.properties file
				 */
				if(true == Variables.postWork)
				{
					/**
					 * This function will initialise postWorkManager which will ultimately spawn required threads
					 * and perform postwork to created different summary tables. It will return true/false based on
					 * whether postwork was carried out without any errors or not.
					 */
					isSuccess = builder.postWork(); 
				}
				/**
				 * If parsing and postwork were done successfully then isSuccess will be true and it will cause 
				 * postProcessing to take place.
				 */
				if(true == isSuccess)
				{
					/**
					 * Perform the task to be done AFTER downloading and parsing starts
					 * this method has to implement by application to do specific funcions.
					 */
					/**
					 * In this function original base and summary tables having old data will be dropped. The_U tables 
					 * which have new data will be renamed to original tables.
					 */
					builder.postProcessing();
				}
			}
			
			/** If Variables.caCoreSystemPostWork is set to true in server.properties file then only the tables used
			 * in caCore like system will be populated. 
			 */
			if(true == Variables.caCoreSystemPostWork)
			{
				/** This functioN will call controller class to populate the tables used by caCore System*/
				builder.caCoreProcessing();
			}
		}
		
		Variables.endJobTime = System.currentTimeMillis();
		try
		{
			/**
			 * If mode is createDBSchema mode then the status mail will be sent in the call to createTables function 
			 * itself since the body of the mail bears just error count along with machine IP,date etc. But in case 
			 * of Update or AddChip mode the mail contains details about the table data count and tablespace. This 
			 * processing is done below by using servStat class.
			 */
			if(false == Variables.createDBSchema)
			{
				/** This function will add appropriate information to SERVER_STATUS table having information about 
				 * files that were parsed and other statistical info like machine used, time takesn, errors etc. */
				servStat.fillServerStatusTable();
				Logger.log("Server status information added in table",Logger.INFO);
				/** This function will use the statistical information about the current run and */
				servStat.callSendMail();
				Logger.log("Server status callsendmail done",Logger.INFO);
			}
		}
		catch(FatalException fe)
		{
			Logger.log("Error in Put method of ServerStatus. " + fe.getMessage(),Logger.FATAL);
		}
		catch(SQLException sqle)
		{
			Logger.log("Error in Put method of ServerStatus " + sqle.getMessage(),Logger.FATAL);
		}
	}
}

