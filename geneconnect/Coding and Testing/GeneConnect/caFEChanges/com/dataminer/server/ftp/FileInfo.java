/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java com.dataminer.server.ftp.FileInfo</p> 
 */

package com.dataminer.server.ftp;

import com.dataminer.server.globals.Constants;
import com.dataminer.server.log.Logger;
import java.util.Vector;
import java.util.Enumeration;

/**
 * Provides a record structure to store the ftp information
 * @author Anuj Tiwari
 * @version 1.0
 * ----- FTP info file format -------------
 * SITE_URL	pathbox.wustl.edu
 * USER_ID 	phataka
 * PASSWD		pwd
 * BASE_DIR	<base dir>
 * FILE		<file1>
 * FILE		<file2>
 * ...         ...
 * FILE        <file n>
 */

public class FileInfo 
{
	private String siteURL="";
	private String userID="";
	private String passwd="";
	private String baseDir="";
	private String databaseType="";
	private String inpFormat=""; //for chipinformation input data
	public boolean localFile = false;
	private Vector files;
	
	//New Member Variables Added for Http Files
	private String type="";
	private boolean proxy = false;
	private String proxyHost = "";
	private String proxyPort = ""; 
	
	// New member varaibles added for external parser 
	private boolean externalParser=false;
	private boolean writeToDB=false;
	private String externalParserCommanFile="";
	private Vector exterParserArgs;
	/**
	 * Constructor method
	 */
	public FileInfo() 
	{
		files = new Vector();
		exterParserArgs = new Vector();
	}
	/**
	 * Method to set FTP url site
	 * @param site URL of ftp site
	 */
	public void addSite(String site) 
	{
		siteURL = site;
	}
	/**
	 * Sets login name of the ftp server
	 * @param user User name to login to ftp site
	 */
	public void addUser(String user) 
	{
		userID = user;
	}
	/**
	 * Sets password for given user of FTP server
	 * @param pwd
	 */
	public void addPasswd(String pwd) 
	{
		passwd = pwd;
	}
	/**
	 * Set base directory
	 * @param base Name of the base directory
	 */
	public void addBaseDir(String base) 
	{
		baseDir = base;
	}
	/**
	 * Set type of the database
	 * @param type Database Type string
	 */
	public void addDatabaseType(String type) 
	{
		databaseType = type;
	}
	/**
	 * Add file name to the list
	 * @param file Name of the file 
	 */
	public void addFile(String file) 
	{
		files.addElement(file);
	}
	/**
	 * Set input format  
	 * @param format Input format string
	 */
	public void addInputFormat(String format) 
	{
		inpFormat = format;
	}
	/**
	 * Set type
	 * @param type Type string
	 */
	public void setType(String type)
	{
		this.type = type;
	}
	/**
	 * Set proxy host url
	 * @param proxyHost proxy host string
	 */
	public void setProxyHost(String proxyHost)
	{
		this.proxyHost = proxyHost;
	}
	/**
	 * Set proxy host port number
	 * @param proxyPort Port number
	 */
	public void setProxyPort(String proxyPort)
	{
		this.proxyPort = proxyPort;
	}
	/**
	 * Set proxy settings
	 * @param proxy Proxy setting string
	 */
	public void setProxy(boolean proxy)
	{
		this.proxy = proxy;
	}
	
	
	/**
	 * This function removes the required filename from the files vector.
	 * @param fileName Name of the file to be removed from vector
	 */
	public void remFile(String fileName)
	{
		if(files.size()!=0 && files.contains(fileName))
		{
			files.removeElement(fileName);
		}
	}
	
	/**
	 * Method to Check if parser for file is External Command 
	 * @return
	 */
	public void setIsExternalParser(boolean isExternalParser)
	{
		this.externalParser=isExternalParser;
	}
	
	/**
	 * Method to Check if external parser writes parsed data directly to database
	 * @return
	 */
	public void setIsWriteToDB(boolean isWriteToDB)
	{
		this.writeToDB=isWriteToDB;
	}
	/**
	 * Method to Set external parser's command fileto execute
	 * @return
	 */
	public void setExternalParserCommanFile(String externalParserCommanFile)
	{
		this.externalParserCommanFile=externalParserCommanFile;
	}
	
	public void addExternalParserArg(String arg)
	{
		this.exterParserArgs.add(arg);
	}
	
	public Vector getExternalParserArg()
	{
		return this.exterParserArgs;
	}
	/**
	 * Get URL site 
	 * @return URL site
	 */
	public String getSite() 
	{
		return siteURL;
	}
	/**
	 * Get user name
	 * @return user name
	 */
	public String getUser() 
	{
		return userID;
	}
	/**
	 * Get password of this ftp site
	 * @return password string
	 */
	public String getPasswd() 
	{
		return passwd;
	}
	/**
	 * get base directory
	 * @return base directory string
	 */
	public String getBaseDir() 
	{
		return baseDir;
	}
	/**
	 * Get database type
	 * @return database type
	 */
	public String getDatabaseType() 
	{
		return databaseType;
	}
	/**
	 * Get vector of files
	 * @return vector of files
	 */
	public Vector getFiles() 
	{
		return files;
	}
	/**
	 * Return input format
	 * @return input format string
	 */
	public String getInputFormat() 
	{
		return inpFormat;
	}
	/**
	 * Get number of files
	 * @return number of files
	 */
	public int getNo_Of_Files()
	{
		return files.size();
	}
	
	/**
	 * Method to check if proxy setting required
	 * @return true if proxy else false
	 */
	public boolean isProxy()
	{
		return proxy;
	}
	/**
	 * Get name/url of proxy host
	 * @return proxy host string
	 */
	public String getProxyHost()
	{
		return proxyHost;
	}
	/**
	 * Get proxy port string
	 * @return proxy port string
	 */
	public String getProxyPort()
	{
		return proxyPort;
	}
	/**
	 * Get type of data
	 * @return type of data
	 */
	public String getType()
	{
		return type;
	}
	
	/**
	 * Method to Check if parser for file is External Command 
	 * @return
	 */
	public boolean IsExternalParser()
	{
		return externalParser;
	}
	
	/**
	 * Method to Check if external parser writes parsed data directly to database
	 * @return
	 */
	public boolean IsWriteToDB()
	{
		return writeToDB;
	}
	/**
	 * Method to Set external parser's command fileto execute
	 * @return
	 */
	public String getExternalParserCommanFile()
	{
		return externalParserCommanFile;
	}
	
	
	/** 
	 * check record for completeness
	 * @return true if record is complete, false otherwise.
	 */
	public boolean checkRecord() 
	{
		//database type should be defined
		if (false==databaseType.equalsIgnoreCase("")) 
		{
			//are the files to be ftp'ed
			if(type.equalsIgnoreCase("FTP"))
			{
				if ((false==siteURL.equalsIgnoreCase("")) && (false==userID.equalsIgnoreCase("")) && (false==passwd.equalsIgnoreCase("")) && (false==baseDir.equalsIgnoreCase("")) ) 
				{
					if ( files.size() > 0) 
					{
						/*one of more file is present */
						return true;
					}
					else 
					{
						Logger.log("Nothing to download: " + siteURL + userID,Logger.INFO);
					}
				}
			}
			else if (type.equalsIgnoreCase("HTTP") && (siteURL.equalsIgnoreCase("")==false)) 
			{
				if((proxy == true) && (proxyHost.equalsIgnoreCase("") == false) && (proxyPort.equalsIgnoreCase("") == false))
				{
					//URL Present without proxy 
					return true;
				}
				else if(proxy == false)
				{
					return true;
				}
				else 
				{
					Logger.log("No Proxy Settings Defined " + databaseType + " file.",Logger.INFO);
				}
			}
			else if (localFile == true) 
			{
				if ( files.size() > 0) 
				{
					/*one of more file is present */
					return true;
				}
				else
				{
					Logger.log("No local " + databaseType + " file.",Logger.INFO);
				}
			}
		}
		return false;
	}
	/** 
	 * resets the member variables 
	 */
	
	public void resetRecord() 
	{
		type = "";
		proxy = false;
		proxyHost = "";
		proxyPort = "";
		siteURL = "";
		userID = "";
		passwd = "";
		baseDir = "";
		inpFormat = "";
		databaseType="";
		externalParser=false;
		writeToDB=false;
		externalParserCommanFile="";
		files.clear();
	}
	/**
	 *  Method yo print record details
	 */
	public void printRecord() 
	{
		System.out.println("New Record");
		System.out.println("site url = " + siteURL);
		System.out.println("user id = " + userID);
		System.out.println("passwd = " + passwd);
		if (inpFormat != null) 
		{
			System.out.println("Input Format = " + inpFormat);
		}
		if (baseDir.length() > 0) 
		{
			System.out.println("base dir = " + baseDir);
		}
		Enumeration enumVar = files.elements();
		int i=1;
		while (enumVar.hasMoreElements()) 
		{
			System.out.println("file " + i++ + " = " + (String)enumVar.nextElement());
		}
	}
	
	/**
	 * This function forms a list of all file names in the File Info object seperated by delimiter. 
	 * @return list of file names seperated by the delimiter
	 */
	public String getFileNames()
	{
        String fileNames = "";
        for (int i = 0; i < this.getFiles().size() ; i++) 
        {
            if (i == 0) 
            {
                fileNames = (String) this.getFiles().get(i);
            }
            else 
            {
                fileNames = fileNames + Constants.DELIMITER + (String) this.getFiles().get(i);
            }
        }   
        return fileNames;
	}
}