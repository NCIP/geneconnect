/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>ClassName: java com.dataminer.server.database.ServerStatus</p> 
 */
package com.dataminer.server.database;

import com.dataminer.server.globals.Constants;
import com.dataminer.server.log.Logger;
import com.dataminer.server.globals.Variables;
import com.dataminer.server.database.DBManager;
import com.dataminer.server.record.Record;
import com.dataminer.server.exception.FatalException;
import com.dataminer.server.ftp.FileInfo;
import com.dataminer.server.mail.mailsender;
import com.dataminer.server.io.FileOutput;
import com.dataminer.server.exception.ApplicationException;

import java.util.Date;
import java.util.TimeZone;
import java.util.Calendar;
import java.util.Locale;
import java.sql.SQLException;
import java.net.InetAddress;
import java.util.Enumeration;
import java.text.SimpleDateFormat;
import java.util.Vector;
import java.util.Iterator;
import java.lang.StringBuffer;
import java.util.StringTokenizer;

/**
 * Class to stores the Server status data in the database. It will store 
 * metadata information about from which m/c server was run, server run mode, 
 * errors, time taken in server_status table. And the information about which 
 * files were downloaded and parsed in the server_file_status table 
 * @author       Anuj Tiwari
 * @version      1.0
 */

public class ServerStatus 
{
	/** Used to join the 2 Server status tables to get the requisite information.*/
	private static long m_id = 0;
	/** Date the Server was run*/
	private static String m_executionDate;
	/** Total time taken for Server run. (in milliseconds)*/
	private static long m_totalTime = 0;
	/** Contains date in the String format.*/
	private static String m_date;
	/** Record containing server_status table data*/
	private Record m_serverStatusRec;
	/** Record containing server_file_status table data*/
	private Record m_serverFileStatusRec;
	/** Object of the DBManager class used for queries*/
	private DBManager dbInterface = DBManager.getInstance();
	
	/** ServerStatus is a singleton class.*/
	private static ServerStatus instance;
	/** File containing which files are being modified and in what mode*/
	public static FileOutput m_fileOutput;
	
	/**
	 * This function creates an instance of the singleton ServerStatus class
	 * It initializes certain basic class variables e.g. date,soem fileds of the
	 * Server status tables.
	 *@return ServerStatus An object of class ServerStatus.
	 */
	public static ServerStatus getInstance() 
	{
		if(instance == null)
		{
			/** create new instance only once for all classes.*/
			instance = new ServerStatus();
			TimeZone tz = TimeZone.getTimeZone("America/Los_Angeles");
			Locale l = new Locale("en","USA");
			Calendar cal = Calendar.getInstance(tz,l);
			int month = cal.get(Calendar.MONTH);
			month += 1;
			int year =  cal.get(Calendar.YEAR);
			int day =  cal.get(Calendar.DAY_OF_MONTH);
			String date = new String(month + "/" + day + "/" + year);
			/** Set execution date to current date */
			ServerStatus.m_date = date;

			try
			{
				Variables.machineName = InetAddress.getLocalHost().getHostName();
				String cwd = System.getProperty("user.dir");
				m_fileOutput = new FileOutput(cwd + Constants.statusFileName,true);
				Logger.log(Constants.statusFileName + " opened...",Logger.INFO);
			}
			catch(ApplicationException ae)
			{
				Logger.log("Error creating FileOutput object ",Logger.INFO);
			}
			catch(java.net.UnknownHostException uhe)
			{
				Logger.log("Error detecting HOST IP ",Logger.INFO);
			}
		}
		return instance;
	}
	
	/** 
	 * Method to set execution date 
	 */
	public static void setExecutionDate()
	{
		/** Based on Oracle/MySQL the date format is initialised in the begining*/
		SimpleDateFormat sdf = new SimpleDateFormat(Variables.dateString);
		m_executionDate = sdf.format(new Date());
		Logger.log("Execution date " + m_executionDate,Logger.INFO);
	}

	/**
	 * Method to set total time taken by server
	 * @param totTime Total time taken by server
	 */
	private void setTotalTime()
	{
		Long elapsedTime = new Long (Variables.endJobTime - Variables.startJobTime);
		m_totalTime = elapsedTime.longValue();
		Logger.log("Total time taken for this job = " + elapsedTime,Logger.INFO);
	}
	
	/**
	 * This function generates Server_File_Status table data. It iterates over the vector of
	 * fileInfo objects that have been downloaded and parsed. It captures relevant information
	 * and stores it into a map against the file name. Later on this map is used to 
	 * populate the server_file_status table.
	 * @param files list of the files for which  
	 */
	public void setServerFileStatus(Vector files)
	{
		FileInfo fileInfo;
		String dat = new String();
		StringBuffer fileData;
		Iterator itr = files.iterator();
		try
		{
			Logger.log("Inside the SetServerFileStatus () ",Logger.INFO);
			/** Iterate over the list of fileInfo objects from which information is to be extracted
			 * for populating the fileData String for each file. All such strings are having information 
			 * separated by + signs which will be loaded in the server_status table
			 */
			while (itr.hasNext())
			{
				fileInfo = (FileInfo)itr.next();
				for (int i=0; i< fileInfo.getFiles().size(); i++) 
				{
					String filename = (String) fileInfo.getFiles().elementAt(i);
					String type;
					fileData=new StringBuffer();
					
					if(fileInfo.localFile)
					{
						Logger.log(filename,Logger.DEBUG);
						dat = (String)Variables.localFiles.get(filename.trim());
						Logger.log("last modifed date " + dat,Logger.INFO);
						/** Date modified appended to fileData*/
						fileData.append(dat + "+");
						if(fileInfo.getDatabaseType().equalsIgnoreCase(Constants.CHIPINFORMATION) || fileInfo.getDatabaseType().equalsIgnoreCase(Constants.CAARRAY))
						{
							type =(String)fileInfo.getInputFormat();
							Logger.log("Writing to FileStatus (ChipLibrary) " + filename,Logger.INFO);
							setDatabaseTransferType(filename,fileInfo.getDatabaseType(),type);
							fileData.append(type + "+");
						}
						else
						{
							type = "LOCAL";
							Logger.log("Writing to FileStatus (Local)" + filename,Logger.INFO);
							setDatabaseTransferType(filename,fileInfo.getDatabaseType(),type);
							fileData.append(type + "+");
						}
						/** Add the filename to fileData.*/
						fileData.append(filename + "+");
						/** Database Name appended to fileData*/
						fileData.append(fileInfo.getDatabaseType() + "+");
						/**Path of the File appended to fileData*/
						fileData.append(filename + "+");
					}
					else if(fileInfo.getType().equalsIgnoreCase(Constants.FTP))
					{
						/**get ftp modify date from Variables.ftpFiles hashtable.*/
						dat = Variables.ftpFiles.get(filename).toString();
						Logger.log("last modifed date " + dat,Logger.INFO);
						fileData.append(dat + "+");
						/** file type appended to fileData*/
						type="FTP";
						Logger.log("Writing to FileStatus (FTP) " + filename,Logger.INFO);
						setDatabaseTransferType(filename,fileInfo.getDatabaseType(),type);
						fileData.append(type + "+");
						/** Add the filename to fileData.*/
						fileData.append(filename + "+");
						/** Database Name appended to fileData*/
						fileData.append(fileInfo.getDatabaseType() + "+");
						/** Path of the File appended to fileData*/
						fileData.append(fileInfo.getBaseDir());
					}
					else if(fileInfo.getType().equalsIgnoreCase(Constants.HTTP))
					{
						/** get ftp modify date from Variables.httpFiles hashtable.*/        
						dat = (String)Variables.httpFiles.get(filename);
						Logger.log("last modifed date " + dat,Logger.INFO);
						fileData.append(dat + "+");
						/** file type appended to fileData*/
						type="HTTP";
						Logger.log("Writing to FileStatus (HTTP) " + filename,Logger.INFO);
						setDatabaseTransferType(filename,fileInfo.getDatabaseType(),type);
						
						fileData.append(type + "+");
						/** Add the filename to fileData.*/
						fileData.append(filename + "+");
						/** Database Name appended to fileData*/
						fileData.append(fileInfo.getDatabaseType() + "+");
						/** Path of the File appended to fileData*/
						fileData.append(fileInfo.getSite());
					}
					/** Now append revision information if it is there for the file else use last
					 * modified date as revision information */
					String revisionHistory = (String)Variables.fileRevisionHistory.get(filename);
					if(null == revisionHistory)
					{
						/** When the source file is not providing information about revision history
						 * then we use last modified date to populate that field in server_file_status table */
						revisionHistory = dat;
					}
					fileData.append("+" + revisionHistory);
					
					Variables.filesData.put(filename,fileData);
				}
			}
		}
		catch(Exception e)
		{
			Logger.log("Exception in the setServerFileStatus Func() "+e.getMessage(),Logger.WARNING);
		}
	}
	/**
	 * Method to get server status identifer 
	 * @return
	 */
	public long  getId()
	{
		return ServerStatus.m_id;
	}
	
	/**
	 * This function sets the fields necessary for the Server_Status table
	 * And inserts data into it.
	 * @exception FatalException thrown by functions of dbInterface.
	 * @exception SQLException thrown by functions of dbInterface.
	 */
	public void fillServerStatusTable() throws FatalException,SQLException
	{
		/** Mode in which the Server was run.(U=Update,C=Create,A=Add chip mode)*/
		char executionMode;
		/** IP of the machine from which the Server was run*/
		String machineName = "";
		try
		{
			String IP = InetAddress.getLocalHost().getHostAddress();
			machineName = IP;
		}
		catch(java.net.UnknownHostException uhe)
		{
			Logger.log("Error detecting HOST IP ",Logger.INFO);
		}

		/** This method will set total processing time including time required for parsing and postwork */
		setTotalTime();
		Logger.log(" Server status set total time done",Logger.DEBUG);
		char mode = Constants.UPDATE_MODE_CHAR;
		if(Variables.updateMode)
		{
			mode = Constants.UPDATE_MODE_CHAR;
		}
		else if(Variables.addChip)
		{
			mode = Constants.ADD_CHIP_MODE_CHAR;
		}
		else if(Variables.createDBSchema)
		{
			mode = Constants.CREATE_DATABASE_MODE_CHAR;
		}
		executionMode = mode;
		
		dbInterface.initTable(Constants.serverStatusTableName);
		m_serverStatusRec = new Record(dbInterface.noOfColumns(Constants.serverStatusTableName),
				dbInterface.getPrecision(Constants.serverStatusTableName));
		
		/** This function call returns the maximm id in the Server_Status Table.*/
		int max=dbInterface.execQuery(Constants.getMaxId);
		
		max += 1;
		ServerStatus.m_id = max; 
		m_serverStatusRec.fields[0].append(max);
		m_serverStatusRec.fields[1].append(m_executionDate);
		m_serverStatusRec.fields[2].append(executionMode);
		m_serverStatusRec.fields[3].append(machineName);
		m_serverStatusRec.fields[4].append(Variables.totalPrsingTime);
		m_serverStatusRec.fields[5].append(m_totalTime);
		m_serverStatusRec.fields[6].append(Variables.errorCount);
		m_serverStatusRec.fields[7].append(Variables.postWorkErrorCount);
		dbInterface.insertRow(Constants.serverStatusTableName,m_serverStatusRec.fields);
		Logger.log("Data Inserted into SERVER_STATUS table successfully. ",Logger.INFO);
		Logger.log("Errors Occuring till basic PostWork got Over =  " + Variables.postWorkErrorCount,Logger.INFO);
		if(0 == Variables.postWorkErrorCount)
		{
			/** This function sets the fields of the Server_file_status table.*/
			instance.setServerFileStatus(Variables.fileInfoList);
			/** This function inserts data into server_file_status table.*/
			fillServerFileStatus();
		}
		dbInterface.commit();
		
		fillRevisionHistoryTable();
		dbInterface.commit();
	}
	
	/**
	 * This function extracts data from the filesData hashtable and inserts records
	 * into the ServerFileStatus table.
	 * @exception FatalException thrown by functions of dbInterface.
	 * @exception SQLException thrown by functions of dbInterface.
	 */
	public void fillServerFileStatus() throws FatalException,SQLException
	{
		try
		{
			dbInterface.initTable(Constants.serverFileStatusTableName);
			m_serverFileStatusRec = new Record(dbInterface.noOfColumns(Constants.serverFileStatusTableName),
					dbInterface.getPrecision(Constants.serverFileStatusTableName));
			String date = new String();
			String type = new String();
			String path = new String();
			String dataBase = new String();
			String revisionHistory = new String();
			Enumeration files = Variables.filesData.keys();
			/** files thus now would contain all the file names local or Ftp.*/
			Logger.log("Trying to insert data into Server_File_Status table. ",Logger.INFO);
			while(files.hasMoreElements())
			{
				String file = files.nextElement().toString();
				/** StringBuffer filedata would henceforth contain all the file related data.
				 *namely date type database path e.t.c.*/
				StringBuffer fileData = new StringBuffer();
				fileData.append(Variables.filesData.get(file));
				StringTokenizer st = new StringTokenizer(fileData.toString(),"+");
				int i = 0;
				while(st.hasMoreElements())
				{
					if(0 == i)
					{
						date = st.nextToken();
					}
					if(1 == i)
					{
						type = st.nextToken();
					}
					if(2 == i)
					{
						st.nextToken();
					}
					if(3 == i)
					{
						dataBase = st.nextToken();
					}
					if(4 == i)
					{
						path = st.nextToken();
					}
					if(5 == i)
					{
						revisionHistory = st.nextToken();
					}
					i++;
				}
				/** set fileds of the table Server_File_Status.*/
				m_serverFileStatusRec.fields[0].append(m_id);
				m_serverFileStatusRec.fields[1].append(date);
				m_serverFileStatusRec.fields[2].append(file);
				m_serverFileStatusRec.fields[3].append(type);
				m_serverFileStatusRec.fields[4].append(dataBase);
				m_serverFileStatusRec.fields[5].append(path);
				m_serverFileStatusRec.fields[6].append(revisionHistory);
				/** insert into ServerFileStatus table.*/
				dbInterface.insertRow(Constants.serverFileStatusTableName,m_serverFileStatusRec.fields);
				m_serverFileStatusRec.resetAllFields();
				Logger.log("Data Inserted into SERVER_FILE_STATUS table successfully. ",Logger.INFO);
			}
		}
		catch(Exception e )
		{
			Logger.log("Exception while inserting data into server_file_status table " + e.getMessage(),Logger.INFO);
		}
		
	}
	
	/** This method is used to set the revision history table values for each of the
	 * organism. The table contains the revsion history for each of the organism
	 * whose information has been parsed by one or more data source. Each of the
	 * data source may provide revision number which can be same for all organisms
	 * or it can be different for diff organisms. This is because source generally
	 * provide info as 1. All info single file 2. Same org info in multiple files
	 * 3. Diff org infor in diff file. Hence we handle each data source differently
	 * and ultimately populate revision number for each of the organism in the 
	 * revision_history table based on the information about that source*/
	private void fillRevisionHistoryTable()
	{
		/** Here all those taxids which are newly added in the organism taxonomy map will be
		 * added to the revision_history table. Later on against these taxids based on the parsing done
		 * revision history will be added for different data sources.
		 */
		dbInterface.executeUpdate("INSERT INTO " + Variables.revisionHistoryTableName + "(RVH_LOCAL_TAXID) (SELECT "
				+ " OTM_LOCAL_TAXID FROM " + Variables.organismTaxonomyMapTableName + " WHERE "
				+ "NOT EXISTS (SELECT RVH_LOCAL_TAXID FROM " + Variables.revisionHistoryTableName + " WHERE " +
						"OTM_LOCAL_TAXID = RVH_LOCAL_TAXID))");
		dbInterface.commit();
		/**update the values of data source revision numbers based on below values*/
		
		if(true == Variables.llupdate)
		{
			/** Update Entrezgene related version information*/
			Enumeration enumLocalTaxids = Variables.entrezGeneRevisionHistory.keys();
			while(enumLocalTaxids.hasMoreElements()) 
			{
				String localTaxid = (String)enumLocalTaxids.nextElement();
				String revHistory = (String)Variables.entrezGeneRevisionHistory.get(localTaxid);
				dbInterface.executeUpdate("UPDATE " + Variables.revisionHistoryTableName + " SET RVH_ENTREZ_VERSION = '"
						+ revHistory + "' WHERE RVH_LOCAL_TAXID = " + localTaxid);
			}
		}
		if(true == Variables.ugupdate)
		{
			Enumeration enumLocalTaxids = Variables.UniGeneRevisionHistory.keys();
			while(enumLocalTaxids.hasMoreElements()) 
			{
				String localTaxid = (String)enumLocalTaxids.nextElement();
				String revHistory = (String)Variables.UniGeneRevisionHistory.get(localTaxid);
				dbInterface.executeUpdate("UPDATE " + Variables.revisionHistoryTableName + " SET RVH_UNIGENE_VERSION = '"
						+ revHistory + "' WHERE RVH_LOCAL_TAXID = " + localTaxid);
			}
		}
		
		if(true == Variables.ustupdate)
		{
			dbInterface.executeUpdate("UPDATE " + Variables.revisionHistoryTableName + " SET RVH_UNI_STS_VERSION = '"
					+ Variables.UniSTSRevisionHistory + "'");
		}
		
		if(true == Variables.goupdate)
		{
			dbInterface.executeUpdate("UPDATE " + Variables.revisionHistoryTableName + " SET RVH_GO_VERSION = '"
					+ Variables.goRevisionHistory + "'");
		}
		
		if(true == Variables.hmlgupdate)
		{
			dbInterface.executeUpdate("UPDATE " + Variables.revisionHistoryTableName + " SET RVH_HML_VERSION = '"
					+ Variables.homologeneRevisionHistory + "'");
		}
		if(true == Variables.dbsnpupdate)
		{
			Enumeration enumLocalTaxids = Variables.dbSNPRevisionHistory.keys();
			while(enumLocalTaxids.hasMoreElements()) 
			{
				String localTaxid = (String)enumLocalTaxids.nextElement();
				String revHistory = (String)Variables.dbSNPRevisionHistory.get(localTaxid);
				dbInterface.executeUpdate("UPDATE " + Variables.revisionHistoryTableName + " SET RVH_DBSNP_VERSION = '"
					+ revHistory + "' WHERE RVH_LOCAL_TAXID = " + localTaxid);
			}
		}
		if(true == Variables.taxdmpUpdate)
		{
			dbInterface.executeUpdate("UPDATE " + Variables.revisionHistoryTableName + " SET RVH_TAX_VERSION = '"
					+ Variables.taxonomyRevisionHistory + "'");
		}
		
	}
	
	/**
	 * This function sets the mail body for Update/ Add chip mode and 
	 * calls the mail sending utility by configuring proper parameters based 
	 * on the values set from server.properties.
	 */
	public void callSendMail()
	{
		String to = Variables.toAddress;
		String from = Variables.fromAddress;
		String host = Variables.host;
		String subject = Variables.subject + " " + Variables.machineName;
		int max = dbInterface.execQuery(Constants.getMaxId);
		/** This function createMailBody(max,date) creates the body of the message to
		 * be sent depending on data in the ServerStatus tables.*/
		String body = new String(dbInterface.createMailBody(max,m_date));
		m_fileOutput.close();
		Logger.log(Constants.statusFileName + " closed and ready to be attached to mail",Logger.INFO);
		/** mailsend is an object of class mail sender and it would actually send the mail.*/
		mailsender mailsend = new mailsender();
		
		Logger.log("To address : " + to,Logger.INFO);
		Logger.log("From address : " + from,Logger.INFO);
		Logger.log("Host address : " + host,Logger.INFO);
		Logger.log("Subject address : " + subject,Logger.INFO);
		Logger.log("Body address : " + body,Logger.INFO);
		
		boolean send = mailsend.sendmail(to,from,Variables.password,host,subject,body);
		Logger.log("Mail Sent to Alias " + to,Logger.INFO);
		/** If mail send operation fails then the the mail body which was generated for 
		 * sending will be stored in StatusMailBosy.txt file in Logs directory
		 */
		if(false == send)
		{
			try
			{
				Logger.log("Mail could not be Sent to " + to,Logger.INFO);
				m_fileOutput = new FileOutput(Constants.StatusMailBody,false);
				Logger.log("StatusMailBody.txt opened...",Logger.INFO);
				m_fileOutput.writeln(body);
				m_fileOutput.close();
			}
			catch(ApplicationException ae)
			{
				Logger.log("Error creating FileOutput object for Statusmail",Logger.INFO);
			}
		}
		else
		{
			Logger.log("Mail Sent to " + to,Logger.INFO);
		}
	}
	
	
	/**
	 * This function sets the Variables file to ensure that type of dataTransfer for each database
	 * is stored and added into the database.
	 * @param fileName Name of the file
	 * @param dataBase Data source name
	 * @param type type of the data
	 */
	public void setDatabaseTransferType(String fileName,String dataBase,String type)
	{
		String line = dataBase + "\t" + fileName + "\t" + type;
		try
		{
			m_fileOutput.writeln(line);
		}
		catch(ApplicationException ae)
		{
			Logger.log("Error creating FileOutput object ",Logger.INFO);
		}
	}
}

