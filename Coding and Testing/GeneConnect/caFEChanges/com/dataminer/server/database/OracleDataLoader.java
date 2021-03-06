/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>ClassName: java com.dataminer.server.database.OracleDataLoader</p> 
 */

package com.dataminer.server.database;

import com.dataminer.server.globals.Variables;
import com.dataminer.server.jobmanager.DPQueue;
import com.dataminer.server.log.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
/**
 * Class to load data files into Oracle database using data loader utility
 * @author Meghana Chitale
 * @version 1.0
 */
public class OracleDataLoader extends DataLoader
{
	/**
	 * Constructor method
	 */
	public OracleDataLoader()
	{
		parameters = new String[9];
		parameters[0] = "sqlldr";
		parameters[1] = Variables.dbUserId + "/" + Variables.dbUserPsswd + "@" + Variables.dbConnect;
		parameters[5] = "rows=100000000";
		parameters[6] = "multithreading=true";
		parameters[7] = "errors=100000000";
		parameters[8] = "direct=true";
		//parameters[9] = "UNRECOVERABLE";
		
		/** there is no option to set the number of errors to be ignored to infinity
		 * hence it has been set to some large number.*/
		parameterSize=9;
		/** it is required to set parametersize so that the comnmand is formed correcly.*/
	}
	/**
	 * Method to load data files into Oracle database using loader utility
	 * @param filesParsed Queue that holds list of files that are parsed and
	 * ready for upload into a database
	 */
	public void loadData(DPQueue filesParsed)
	{
		Logger.log("Load data started ",Logger.INFO);
		
		String fileName = null;
		
		try
		{
			Logger.log("Entered load data for oracle files ",Logger.INFO);
			while ((fileName = (String)filesParsed.consume())!= null)
			{
				Logger.log("loading file : " + fileName,Logger.INFO);
				loadFile(fileName);
			}
			Logger.log("Data Loading Complete",Logger.INFO);
		}
		catch(IOException ioExp)
		{
			Logger.log("Error while loading data into database" + ioExp.getMessage(),Logger.WARNING);
		}
		catch(InterruptedException intrExp)
		{
			Logger.log("Error while loading data into database" + intrExp.getMessage(),Logger.WARNING);
		}
	}
	/**
	 * Method to load given data file into database
	 * @param fileName Name of the file to load
	 * @throws IOException Throws exception if error while loading data file
	 * @throws InterruptedException Throws exception if error while loading data file
	 */
	private void loadFile(String fileName) throws InterruptedException,IOException
	{
		Runtime run = Runtime.getRuntime();
		parameters[2] = fileName; 
		parameters[3] = "log="+ fileName + ".log";
		parameters[4] = "bad="+ fileName + ".bad";
		
		//Perform Direct load, if the file name contains "_direct". 
		//This is the temporary fix. Later on this needs to changed. 
		//Loader should accept Structure containing filename and various load parameteres instead of filename only. 
		if (!fileName.contains("_direct"))
			parameterSize = parameterSize - 1; 
			
		/**parameter array is converted to string and then passed to exec.This is 
		 * done since the size of parameters is diff for diff execs.And it throws
		 * null ptr exception if some parameters of array passed to exc are null.*/
		StringBuffer cmd = new StringBuffer();
		for (int i = 0; i < parameterSize; i++) 
		{
			cmd.append(parameters[i]);
			cmd.append(" ");
		}
		Logger.log("Executing : " + cmd.toString(),Logger.INFO);
		Process sqlldr =  run.exec(cmd.toString());
		
		System.out.println("Executing command : " + cmd.toString());
		/** Close the Stream of The Process.If the Stream is not closed 
		 * then it will not allow next process to create new Stream.*/
		sqlldr.getInputStream().close();
		
		sqlldr.waitFor();                               
		System.out.println("Loaded File " + fileName);
		Logger.log("Process exit status value " + sqlldr.exitValue(),Logger.INFO);
		Logger.log("Loaded file " + fileName,Logger.INFO);
		/** check the output log file generated for failures in 
		 * loading the records and take action accordingly.*/
		//deleteLoadedFile(fileName);
		
		boolean success = readLogFile(fileName + ".log",fileName + ".bad");
		if(true == success)
		{
			Logger.log("Sqlloader successfully loaded file " + fileName,Logger.INFO);
		}
		else
		{
			Logger.log("Some Error while loading file : " + fileName + " See log for details" ,Logger.DEBUG);
		}
		
	}
	/**
	 * Method to load file into Oracle database
	 * @param fileName File name 
	 */
	public void load(String fileName)
	{
		try
		{
			Logger.log("loading file : " + fileName,Logger.INFO);
			loadFile(fileName);
			Logger.log("Data Loading Complete",Logger.INFO);
		}
		catch(IOException ioExp)
		{
			Logger.log("Error in loader " + ioExp.getMessage(), Logger.WARNING);
		}
		catch(InterruptedException intrExp)
		{
			Logger.log("Error in loader " + intrExp.getMessage(), Logger.WARNING);
		}
	}
	/**
	 * Method to read log files generated by SQLLoader
	 * @param logFile Name of the log file
	 * @param badFileName Name of bad file
	 * @return true if no error in log files
	 */
	private boolean readLogFile(String logFile, String badFileName)
	{
		
		/** flag for errors. Set if errors. Default is unset.*/
		boolean error=false;
		File logFileHandle = new File(logFile);
		try
		{
			BufferedReader logFileReader = new BufferedReader
			(new FileReader(logFileHandle));
			String line = null;
			
			/** read all lines one by one.*/
			while((line=logFileReader.readLine()) != null)
			{
				
				/** This line in log file denotes the error in
				 * username or password.*/
				if(line.startsWith("SQL*Loader-128:"))
				{
					Logger.log("DB1001:",Logger.WARNING);
					error = true;
				}
				
				/** This line in log file denotes the error in
				 * database name.*/
				else if(line.startsWith("SQL*Loader-704:"))
				{
					Logger.log("DB1002:",Logger.WARNING);
					error = true;
				}
				else if(line.startsWith("SQL*Loader-908:"))
				{
					Logger.log("DB1004: ",Logger.WARNING);
					Logger.log("File was: "+logFile,Logger.WARNING);
					error = true;
				}
				else if((line.startsWith("ORA-02371:"))&&(error != true))
				{
					Logger.log("DB1004: ",Logger.WARNING);
					Logger.log("File was: "+logFile,Logger.WARNING);
					error = true;
				}
				else if(line.startsWith("Error checking path homogeneity"))
				{
					Logger.log("DB1004: ",Logger.WARNING);
					Logger.log("File was: "+logFile,Logger.WARNING);
					error = true;
				}
				else if(line.startsWith("SQL*Loader-951:"))
				{
					Logger.log("Error calling once/load initialization",Logger.WARNING);
					Logger.log("File was: "+logFile,Logger.WARNING);
					error = true;
				}
				
				/** Check how many records were rejected.*/
				else if(line.startsWith("Total logical records rejected:"))
				{
					StringTokenizer tokens = new StringTokenizer(line);
					int numberOfTokens = tokens.countTokens();
					while(numberOfTokens > 1)
					{
						tokens.nextToken();
						numberOfTokens-- ;
					}
					int rejectedRecords =0;
					try
					{
						rejectedRecords = Integer.parseInt
						(tokens.nextToken());
					}
					catch(NumberFormatException nfe)
					{
						Logger.log("AB0006:",Logger.WARNING);
						Logger.log("Exception= "+nfe.getMessage(),Logger.WARNING);
						Logger.log("Inside readLogFile in OracleSqlLoader",Logger.DEBUG);
					}
					
					/** if rejected records are greater than zero, then report error.*/
					if(rejectedRecords > 0)
					{
						Logger.log("DB1003: Check File: "+badFileName,Logger.WARNING);
						error = true;
					}
				}
			}
		}
		catch(java.io.FileNotFoundException fnfe)
		{
			Logger.log(logFile+" not found for checking errors during SQL LOADER",Logger.WARNING);
			Logger.log("IO0001:, Log File: "+logFile,Logger.WARNING);
			Logger.log("Exception= "+fnfe.getMessage(),Logger.WARNING);
			
			error = true;
		}
		catch(java.io.IOException ioe)
		{
			Logger.log(logFile+" not found for checking errors during SQL LOADER",Logger.WARNING);
			Logger.log("IO0004:, log file"+logFile,Logger.WARNING);
			Logger.log("Exception= "+ioe.getMessage(),Logger.WARNING);
			error = true;
		}
		
		/**negate error to reflect answer apropriatly*/
		return !error;
	}
	
	
}
