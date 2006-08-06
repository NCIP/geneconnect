package com.dataminer.server.globals;

import java.io.File;

import com.dataminer.server.log.Logger;

public class Utility 
{

	public Utility()
	{
			
	}
	/** Accept the string and convert to Boolean . This is used to set the properties based 
	 * on the values read from server.properties */
	public static boolean toBoolean(String str)
	{
		if(str.equalsIgnoreCase("TRUE"))
			return true;
		else 
			return false;
	} 
	
	/**
	 * This function checks whether input file exists and has read permission,if not then it
	 * will log error message and exit with status 1. 
	 * @param fileName Name of the file which is to be checked for exisistance and read permission in current directory
	 */
	public static void checkFile(String fileName)
	{
		File fCheck = new File(fileName);
		if(false == fCheck.exists())
		{
			Logger.log("File " + fileName + " missing, exiting run",Logger.FATAL);
			System.out.println("File " + fileName + " missing, exiting run");
			System.exit(1);
		}
		if(false == fCheck.canRead())
		{
			Logger.log("File " + fileName + " not having read permission, exiting run",Logger.FATAL);
			System.out.println("File " + fileName + " not having read permission, exiting run");
			System.exit(1);
		}
	}
	/**
	 * Delete File
	 * @param fileName
	 */
	public static void deleteFile(String fileName)
	{
		String gzFileName = fileName;
		File f = new File(gzFileName);
		try
		{
			boolean flag=f.delete();
			Logger.log(gzFileName+" deleted successfully."+flag,Logger.INFO);
		}
		catch(SecurityException se )
		{
			Logger.log(gzFileName+" Delete Exception "+se.getMessage(),Logger.INFO);
		}
		catch(Exception ex)
		{
			Logger.log(gzFileName+" Delete Exception*** "+ex.getMessage(),Logger.INFO);
		}
	}
		
}
