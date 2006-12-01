/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java com.dataminer.server.database.DBManager</p> 
 */

package com.dataminer.server.database;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import com.dataminer.server.exception.ApplicationException;
import com.dataminer.server.exception.FatalException;
import com.dataminer.server.exception.NonFatalException;
import com.dataminer.server.globals.Constants;
import com.dataminer.server.globals.Variables;
import com.dataminer.server.io.PropertiesFileHandeler;
import com.dataminer.server.log.Logger;
import com.dataminer.server.parser.CreateSchemaParser;
import com.dataminer.server.record.Record;
/**
 * Provides interface to the database. It provides functions to inserts and 
 * query the database. Also it provides methods for getting database related
 * queries executed for specific 
 * @author Meghana Chitale
 * @version      1.0
 */
/** DBManager module manages database connection and provided methods for database interaction.*/
public class DBManager 
{
	/** connection to the database */
	private Connection conn   = null;
	/** for batch insertion */
	private int insertCount ;
	/** stores information about all db tables */
	private Hashtable tableInfoHTable = new Hashtable();
	
	/** DBManager as a singleton class */
	private static DBManager dbInstance = new DBManager();
	/** Method to return instance of this class
	 * @return DBManager Returns object of this class
	 */
	public static DBManager getInstance() 
	{
		return dbInstance;
	}
	
	/** 
	 * For MySQL database when cursor is created on any of the tables then its entire data is cached in
	 * the database even if fetch size is set to say 200. To overcome this problem the workaround is that
	 * set the fetch size to Interger.MIM_VALUE and create one ResultSet per connection. The connections created
	 * for such purpose are stored in below Vector and closed at the end
	 */
	private Vector m_streamingConnections = new Vector();
	
	/** The connection parameters are passed to connect method are used to establish the single connection used
	 * later for querying etc. But for creating the streaming connections for MySQL cursor creation problem we 
	 * need these connection parameters afterwards also. They are stored in the variables below. */
	private String m_driverName;
	private String m_dbURL;
	private String m_userName;
	private String m_passWord;
	
	/**
	 *  inner tableInfo class that stores all the information for a particular database table. When a 
	 *  particular table is to be used then all its meta information can be initialised with the database 
	 *  manager by setting up the values in the below class. tableInfoHTable is used for storing all the 
	 *  table information objects .
	 */
	public class TableInfo 
	{
		String tableName;
		int noOfColumns;
		/** names of the fields */
		String colNames[]; 
		/** SQL type of the fields */
		int colTypes[];    
		/** max. length of the fields */
		int colPrecision[];   
		PreparedStatement ps;
		int nullable[];
		TableInfo(int colCount) 
		{
			noOfColumns = colCount;
			colNames = new String[colCount];
			colTypes = new int[colCount];
			colPrecision = new int[colCount];
			nullable = new int[colCount];
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isConnected()
	{
	    return (conn != null);
	}
	
	/**
	 * Method to execute given query and return query result as integer. It is generally
	 * used for getting the table counts or maximum nubers from table
	 * @param query Query String to execute
	 * @return The query result is returned as integer
	 */
	public int execQuery(String query)
	{
	    int max = 0;
	    ResultSet rs = executeSQLQuery(query);
	    /** The function reads the value returned by the query and returns the integer conversion of the 
	     * result. The function is used for getting maximum number form the table or count of the table.*/
	    try 
	    {
	        while(rs.next())
	        {
	            max = rs.getInt(1);
	        }
	    } catch(SQLException sq) {
	        /** If query execution throws SQL Exception then 0 will be returned*/
	        Logger.log("error in execQuery(): "+ query  + "   " + sq.getMessage(),Logger.WARNING);
	        return 0;
	    }
	    return max;
	}
	
	/**
	 * Method to execute given query and return query result.
	 * @param query Query String to execute
	 * @return The query result 
	 */
	public ResultSet executeSQLQuery(String query)
	{
	    ResultSet resultSet = null;
	    Logger.log("Executing: " + query,Logger.INFO);
	    /** Execute the query*/
	    long startTime = System.currentTimeMillis();
	    try
	    {
	        //Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
	        Statement stmt = conn.createStatement();
	        resultSet = stmt.executeQuery(query);
	    } catch(SQLException sq)
	    {
	        /** If query execution throws SQL Exception then null will be returned*/
	        Logger.log("error in executeSQLQuery(): "+ query  + "   " + sq.getMessage(),Logger.WARNING);
	        return null;
	    } 
	    long endTime = System.currentTimeMillis();
	    long queryTime = endTime - startTime;
	    Logger.log("Query Time: " + queryTime,Logger.INFO);
	    Logger.log("Done ",Logger.INFO);
	    return resultSet;
	}
	
	public void executeScriptFile(String filename)
	{
		Logger.log("Executing sql script file " + filename, Logger.INFO);
		try
		{
			BufferedReader scriptFile = new BufferedReader(new FileReader(filename));
			String query = "";
			String currentLine = null;
			while ((currentLine = scriptFile.readLine()) != null)
			{
				if (currentLine.contains(";"))
				{
					if (currentLine.indexOf(';') != 0)
					{
						query = query + currentLine.substring(0, currentLine.indexOf(';'));
					}
					executeSQLQuery(query);
					query = "";
				}
				else
				{
					query = query + currentLine;
				}
			}
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Logger.log("Finished excuting sql script file " + filename, Logger.INFO);
	}
	
	/**
	 *Creates mail body for the summary mail to be sent by server after completing the run
	 *This function is used to send mails for Add chip and Update mode.
	 *@param id It represents id of the entry in Server_status table.
	 *@param date represents todays(current) date.
	 */
	public String createMailBody(int id,String date)
	{
		/** Here param id gives the id of the status entry in Server_status table which is to be 
		 * considered for the status information about the run
		 */
		String headerBody = "Hi All,\n\nThe Cron job for Server update on " + Variables.machineName + " on " 
		+ date + ".\n\n" +
		"Statistics of the Server run are as follows: \n\n";
		String createDbSchemaModeBody = "Hi All,\n\nThe Server was run in CreateDBSchema on " + 
		Variables.machineName + " on " + date + ".\n\n" + "Statistics of the Server db Creation are as follows: \n\n";
		/** Statement to be returned in case an exception occurs*/
		String tempBody = new String("There was problem generating the Automatically generated mail.\nPlease Check the database.");
		StringBuffer body;
		/** Initialise proper header in the status mail based on the mode in which server was run*/
		if(Variables.createDBSchema)
		{
			body = new StringBuffer(createDbSchemaModeBody);
		}
		else
		{
			body = new StringBuffer(headerBody);
		}
		
		/** Query used to get information about the server run from server_status table*/
		String getServerStatus ="SELECT * FROM " + Constants.serverStatusTableName + " where sst_id = " + id;
		try
		{
			String times;
			int time;
			int hr;
			int min;
			Statement stmt = conn.createStatement();
			Logger.log("Executing: " + getServerStatus,Logger.INFO);
			/** Execute the query to get server run information*/
			long startTime = System.currentTimeMillis();
			ResultSet rs = stmt.executeQuery(getServerStatus);
			long endTime = System.currentTimeMillis();
			long queryTime = endTime - startTime;
			while(rs.next())
			{
				body.append("Execution  date: "+date+".\n");
				body.append("IP from which Server was run: "+rs.getString("SST_MACHINENAME")+"\t("+Variables.machineName+")\n");
				String mode = rs.getString("SST_EXECUTION_MODE");
				/** Read the execution mode from server_status table and set the value of mode accordingly*/
				if(mode.equalsIgnoreCase("U"))
				{
					mode = "Update";
				}
				else if(mode.equalsIgnoreCase("A")) 
				{
					mode = "AddChip";
				}
				else if(mode.equalsIgnoreCase("C"))
				{
					mode = "CreateDBSchema";
				}
				body.append("Execution mode: " + mode + ".\n");
				/** If execution mode is add chip or update mode then add the server run time statistics 
				 * in the mail body	 */
				if(false == Variables.createDBSchema)
				{
					times = rs.getString("SST_PARSING_TIME");
					Integer temp = new Integer(times);
					/** Given below is the logic of converting milliseconds into format hr:sec*/
					time = temp.intValue()/60000;
					hr = time/60;
					min = time%60;
					/** e.g. 2160000 ms = 2160000/60000 =36 --> 0 hrs 36 mins.*/
					body.append("Parsing and Base table creation Time: " + temp.intValue() + " milliseconds (approx " + hr + " hrs " + min + " mins ).\n");
					Integer temp1 = new Integer(rs.getString("SST_TOTAL_TIME"));
					/** Convert milliseconds into hr:sec.*/
					time = temp1.intValue()/60000;
					hr = time/60;
					min =time%60;
					body.append("Total Time for Server run: " + temp1.intValue() + " milliseconds (approx " + hr + " hrs " + min + " mins ). \n");
					body.append("Parsing and Postwork Error count: " + rs.getString("SST_POSTWORK_ERROR_COUNT") + ".\n");
					body.append("Postwork Done: " + Variables.postWork + ".\n");
				}
			}
			body.append("Total Error count: " + Variables.errorCount + ".\n");
			Logger.log("Query Time: " + queryTime,Logger.INFO);
			Logger.log("Done ",Logger.INFO);
			Logger.log("Data inserted into Tables Server_Status,Server_File_Status. ",Logger.INFO);
			/** If mode is not create db mode then add the information about the postwork in the mail*/
			if(false == Variables.createDBSchema)
			{
				if(Variables.postWorkErrorCount != 0 )
				{
					body.append("\n There were " + Variables.errorCount + " Errors during the Server run.");
					body.append("\n The new tablename_u tables have not been renamed to original names.");
				}
				body.append("\n Organism_taxonomymap table has been updated");
				/** If type of database being used is Oracle then we need to add the tablecospace information
				 * in the mail telling the available and used tablespace size */
				if(Variables.dbIdentifier.equalsIgnoreCase(Constants.ORACLE))
				{
					body.append("\nThe status of the tablespace usage is as follows: \n");
					
					if(Variables.dbIdentifier.equalsIgnoreCase(Constants.ORACLE))
					{
						/**Add the information about tablespace after execution in the appropriate map. The 
						 * maps used to store the tablespace related information which are used change based
						 * on true/false passed which represents before drop= true/false */
						fillTableSpaceData(false);
					}
					body.append("\nTablespace Name\t%Used (Before dropping old tables)\t%Used (After dropping old tables)\n");
					Enumeration enumBefore = Variables.freeSpaceBefDrop.keys();
					/** Read information from table Variables.freeSpaceBefDrop and append the mail body*/
					while(enumBefore.hasMoreElements())
					{
						String key = enumBefore.nextElement().toString();
						Integer freeInt =  (Integer) Variables.freeSpaceBefDrop.get(key);
						Integer allocInt = (Integer) Variables.allocSpaceBefDrop.get(key);
						float free  = freeInt.floatValue();
						float alloc = allocInt.floatValue();
						float used  = alloc - free;
						float pctBefore   = (used / alloc)*100;
						/** The code above and below finds what % of space in tablespace is used and what% is free.*/
						freeInt =  (Integer) Variables.freeSpaceAftDrop.get(key);
						allocInt = (Integer) Variables.allocSpaceAftDrop.get(key);
						free  = freeInt.floatValue();
						alloc = allocInt.floatValue();
						used  = alloc-free;
						float pctAfter = (used / alloc)*100;
						
						if(key.equalsIgnoreCase("POSTDATA") || key.equalsIgnoreCase("PUBMEDDATA") || key.equalsIgnoreCase("CACOREDATA"))
						{
							body.append("\n" + key + "\t\t\t" + pctBefore + "%\t\t\t\t" + pctAfter + "%");
						}
						else if(key.equalsIgnoreCase("REFDATA"))
						{
							body.append("\n" + key + "\t\t\t\t" + pctBefore + "%\t\t\t\t" + pctAfter + "%");
						}
						
					}
					Logger.log("Data Regarding Tablespaces obtained. ",Logger.INFO);
				}
				/**Read the counts of all important tables of caFE Server, postwork and caCore and append
				 * that information in the mail body */
				body.append("\n\n Count of some important caFE Server Base Tables and Summary Tables:  \n");
				
				if(Variables.serverTables!=null)
				{
					StringTokenizer serverTables = new StringTokenizer(Variables.serverTables,",");
					
					while(serverTables.hasMoreTokens())
					{
						String table = serverTables.nextToken();
						try {
							body.append("\n " + table+" : " + retCount(table));
						} catch (SQLException sqlEx) {
							continue;
						}
					}
				}
				/** If caCore table creation is run in the current run then only add information about their count 
				 * in the mail. They are not created in createDB scripts but are later created when their creation
				 * is set to true after Update/ Add chip mode. Hence we add their information only when that
				 * flag indicating they are being processed is set to true */
				if(true == Variables.caCoreSystemPostWork)
				{
					body.append("\n\n Count of tables created for caCORE like System:  \n");
					
					if(Variables.caCoreSystemTables!=null)
					{
						StringTokenizer caCoreSystemTables = new StringTokenizer(Variables.caCoreSystemTables,",");
						
						while(caCoreSystemTables.hasMoreTokens())
						{
							String table = caCoreSystemTables.nextToken();
							try {
								body.append("\n " + table + " : " + retCount(table));
							} catch (SQLException sqlEx) {
								continue;
							}
						}
					}
				}
				Logger.log("Added count of Basic tables ",Logger.INFO);
			}
			body.append("\n\nRegards,\nServer Administrator");
			return body.toString();
		}
		catch(SQLException sqlEx)
		{
			Logger.log("error in createMailBody() " + sqlEx.getMessage(),Logger.WARNING);
			Variables.errorCount++;
		}
		catch(NumberFormatException nfe)
		{
			Logger.log("Error in sendmailInt conversion: " + nfe.getMessage(),Logger.WARNING);
		}
		return tempBody;
	}
	
	/**
	 * This function finds the current state of the tablespaces.
	 * As to what is the number of free blocks available currently.
	 * @param beforeDrop Boolean indicating calculate tablespace statisics before dropping 
	 * _u rables
	 */
	public void fillTableSpaceData(boolean beforeDrop)
	{
		try{
			Statement stmt = conn.createStatement();
			Logger.log("Executing: " + Constants.getfreeSpace,Logger.INFO);
			/** Execute the query*/
			long startTime = System.currentTimeMillis();
			ResultSet rs = stmt.executeQuery(Constants.getfreeSpace);
			long endTime = System.currentTimeMillis();
			long queryTime = endTime - startTime;
			if(beforeDrop)
			{
				while(rs.next())
				{
					/** 1 TABLESPACE_NAME  2 FREESPACE In MEGS.*/
					Variables.freeSpaceBefDrop.put(rs.getString(1),new Integer(rs.getInt(2)));
				}
			}
			else
			{
				while(rs.next())
				{
					/** 1 TABLESPACE_NAME  2 FREESPACE In MEGS.*/
					Variables.freeSpaceAftDrop.put(rs.getString(1),new Integer(rs.getInt(2)));
				}
			}
			Logger.log("Query Time: " + queryTime,Logger.INFO);
			Logger.log("Done ",Logger.INFO);
			Logger.log("Executing: " + Constants.getAllocatedSpace,Logger.INFO);
			/** Execute the query*/
			startTime = System.currentTimeMillis();
			rs = stmt.executeQuery(Constants.getAllocatedSpace);
			endTime = System.currentTimeMillis();
			queryTime = endTime - startTime;
			if(beforeDrop)
			{
				while(rs.next())
				{
					/** 1 TABLESPACE_NAME  2 FREESPACE In MEGS.*/
					Variables.allocSpaceBefDrop.put(rs.getString(1),new Integer(rs.getInt(2)));
				}
			}
			else
			{
				while(rs.next())
				{
					/** 1 TABLESPACE_NAME  2 FREESPACE In MEGS.*/
					Variables.allocSpaceAftDrop.put(rs.getString(1),new Integer(rs.getInt(2)));
				}
			}
			Logger.log("Query Time: "+queryTime,Logger.INFO);
			Logger.log("Done ",Logger.INFO);
		}
		catch(SQLException sqlEx)
		{
			Logger.log("error in fillTableSpaceData(boolean beforeDrop) " + sqlEx.getMessage(),Logger.WARNING);
			Variables.errorCount++;
		}
		
	}
	
	/**
	 * This Function Gets the Count of All the important tables.
	 * returns these to the function createMail Body.
	 * @return count of the number of elements of the table.
	 * @exception SQLEXception Throws exception if error during execution of query 
	 */
	public long retCount(String tableName) throws SQLException
	{
		long count = 0;
		Statement stmt = conn.createStatement();
		String countQuery = "Select count(*) from "+tableName;
		Logger.log("Executing: " + countQuery,Logger.DEBUG);
		/** Execute the query*/
		long startTime = System.currentTimeMillis();
		ResultSet rs = stmt.executeQuery(countQuery);
		while(rs.next())
		{
			count = rs.getLong(1);
		}
		long endTime = System.currentTimeMillis();
		long queryTime = endTime - startTime;
		Logger.log("Query Time: " + queryTime,Logger.INFO);
		Logger.log("Done ",Logger.INFO);
		Logger.log("Number of elements in " + tableName + " = " + count,Logger.INFO);
		return count;
	}
	
	/**
	 * This function makes sure that
	 * 1 All the old versions of the basetables created so far are dropped.
	 * 2 Rename all the new updated base tables to their original names.
	 * @exception SQLException Throws exception if error during execution of query
	 */
	public void baseTableUpdate()
	{
		try
		{
			Logger.log("base table update entered",Logger.INFO);
			/** Create schema parser to parse the drop constraint scripts*/
			CreateSchemaParser csp = new CreateSchemaParser();
			HashMap hmTableDropConstraints = null;
			String parseFileName = null;
			/** Based on type of database being used use specific scripts for droping the constraints 
			 * on FEServer tables		 */
			if(Variables.dbIdentifier.equalsIgnoreCase(Constants.ORACLE))
			{
				parseFileName = new String(Constants.feConstraintDropFileOracle);
			}
			else if(Variables.dbIdentifier.equalsIgnoreCase(Constants.MYSQL))
			{
				parseFileName = new String(Constants.feConstraintDropFileMysql);
			}
			Logger.log("parsing file " + parseFileName,Logger.INFO);
			csp.parseFile(parseFileName);
			/**Get the map of data source and corresponing constraints	 */
			hmTableDropConstraints = csp.getDataSourceConstraints();
			
			/** drop constraints on tables from respective data sources before droping the tables; This is 
			 * required so that later on while dropping the tables the constraints don't create problems*/
			if(Variables.updateMode)
			{
				/** If unigene data source is being updated in current run then put the constraints on those
				 * tables as read from the script files and corresponding to unigene data source */
				if(Variables.ugupdate)
				{
					Vector vecConstraint = (Vector)hmTableDropConstraints.get(Constants.UNIGENE);
					if(vecConstraint!=null)
					{
						for (Iterator iter = vecConstraint.iterator(); iter.hasNext();) 
						{
							String element = (String) iter.next();
							executeUpdateSupressError(element);
						}
					}
					/** Drop the tables corresponding to Unigene data source and then rename the _U tables 
					 * as original tables */
					dropTables(Constants.ugTables);
					reNameTables(Constants.ugTables);
				}
				/** If Entrezgene data source is being updated in current run then put the constraints on those
				 * tables as read from the script files and corresponding to Entrezgene data source */			
				if(Variables.llupdate)
				{
					Vector vecConstraint = (Vector)hmTableDropConstraints.get(Constants.ENTREZGENE);
					if(vecConstraint!=null)
					{
						for (Iterator iter = vecConstraint.iterator(); iter.hasNext();) 
						{
							String element = (String) iter.next();
							executeUpdateSupressError(element);
						}
					}
					/** Drop the tables corresponding to Entrezgene data source and then rename the _U tables 
					 * as original tables */
					dropTables(Constants.llTables);
					reNameTables(Constants.llTables);
				}
				/** If Homologene data source is being updated in current run then put the constraints on those
				 * tables as read from the script files and corresponding to Homologene data source */
				if(Variables.hmlgupdate)
				{
					Vector vecConstraint = (Vector)hmTableDropConstraints.get(Constants.HOMOLOGENE);
					if(vecConstraint!=null)
					{
						
						for (Iterator iter = vecConstraint.iterator(); iter.hasNext();) 
						{
							String element = (String) iter.next();
							executeUpdateSupressError(element);
						}
					}			
					/** Drop the tables corresponding to Homologene data source and then rename the _U tables 
					 * as original tables */
					dropTables(Constants.hmlgTables);
					reNameTables(Constants.hmlgTables);
				}
				/** If UniSTS data source is being updated in current run then put the constraints on those
				 * tables as read from the script files and corresponding to UniSTS data source */
				if(Variables.ustupdate)
				{
					Vector vecConstraint = (Vector)hmTableDropConstraints.get(Constants.UNISTS);
					if(vecConstraint!=null)
					{
						
						for (Iterator iter = vecConstraint.iterator(); iter.hasNext();) 
						{
							String element = (String) iter.next();
							executeUpdateSupressError(element);
						}
					}
					/** Drop the tables corresponding to UniSTS data source and then rename the _U tables 
					 * as original tables */
					dropTables(Constants.unistsTables);
					reNameTables(Constants.unistsTables);
				}
				/** If dbSNP data source is being updated in current run then put the constraints on those
				 * tables as read from the script files and corresponding to dbSNP data source */
				if(Variables.dbsnpupdate)
				{
					Vector vecConstraint = (Vector)hmTableDropConstraints.get(Constants.DBSNP);
					if(vecConstraint!=null)
					{
						
						for (Iterator iter = vecConstraint.iterator(); iter.hasNext();) 
						{
							String element = (String) iter.next();
							executeUpdateSupressError(element);
						}
					}		
					/** Drop the tables corresponding to dbSNP data source and then rename the _U tables 
					 * as original tables */
					dropTables(Constants.dbSnpTables);
					reNameTables(Constants.dbSnpTables);
				}
				/** System termtables are not deleted in any run but they are updated from the existing 
				 * tables. Henece we need to drop constraints on the original tables as they will be 
				 * dropped and recreated in each run */
				Vector vecConstraint = (Vector)hmTableDropConstraints.get(Constants.TERMDATA);
				if(vecConstraint != null)
				{
					
					for (Iterator iter = vecConstraint.iterator(); iter.hasNext();) 
					{
						String element = (String) iter.next();
						executeUpdateSupressError(element);
					}
				}		
				/** Drop System term tables then rename the _U tables as original tables */
				dropTables(Constants.sysCopyTables);
				reNameTables(Constants.sysCopyTables);
				
			}
		} catch(FatalException fatal)
		{
			Logger.log("Fatal exception in BaseTableUpdate " + fatal.getMessage(),Logger.FATAL);
		}
		
	}
	/**
	 * This function will drop all the original caCore tables and after that it will rename the _U tables
	 * as the original tables. This is donw after we are done populating the caCore tables without any error
	 */
	public void caCoreSystemTableUpdate()
	{
		dropConstraints();
		
		/** Drop original tables*/
		for(int i=0;i<Constants.caCoreSystemTables.length; i++)
		{
			try
			{
				String tabName = Constants.caCoreSystemTables[i];
				String Query = "DROP TABLE " + tabName ;
				executeUpdateException(Query);
			}
			catch(FatalException ex)
			{
				Logger.log("Fatalexception while dropping caCoreSystem tables" + ex.getMessage(),Logger.FATAL);
			}
		}
		
		/** rename _U tables*/
		for(int i=0;i<Constants.caCoreSystemTables.length; i++)
		{
			try
			{
				String tabName = Constants.caCoreSystemTables[i];
				String Query = "ALTER TABLE " + tabName + "_U RENAME TO " + tabName;
				executeUpdateException(Query);
			}
			catch(FatalException ex)
			{
				Logger.log("Fatalexception while renaming caCoreSystem tables" + ex.getMessage(),Logger.FATAL);
			}
		}
		if(Variables.dbIdentifier.equalsIgnoreCase(Constants.ORACLE))
		{
			/** if database is oracle then only sequeneces are present and need to be deleted. The sequences
			 * are created each time when caCore tables are being created as indicated in server.properties 
			 * file. Henece we need to make sure that the sequences are dropped and recreated along with the
			 * tables*/
			dropcaCoreSequences();
		}
	}
	/**
	 * This function is called each time after renaming the _U tables of caCore as original tables. This is 
	 * required so as to make sure that the sequences used to auto number caCore tables on Oracle are deleted
	 * and hence can be recreated during next run*/
	private void dropcaCoreSequences()
	{
		/** drop sequences*/
		for(int i=0;i<Constants.caCoreSequences.length; i++)
		{
			try
			{
				String seqName = Constants.caCoreSequences[i];
				String Query = "DROP SEQUENCE " + seqName ;
				executeUpdateException(Query);
			}
			catch(FatalException ex)
			{
				Logger.log("Fatalexception while dropping caCoreSystem sequences" + ex.getMessage(),Logger.FATAL);
			}
		}
		
	}
	
	/**
	 * This method is called for dropping constraints on caCore tables so that later when we drop the original tables
	 * we don't get errors. Theconstraint files for dropping constraints in Oracle and MySQL differ even though
	 * the syntax is same. This is because we need to create the primary keys on the suto numbered columns in 
	 * MySQL at the time of table creation and not later. So those queries are not there in MySQL scripts but
	 * are present in Oracle Scripts */
	private void dropConstraints()
	{
		/** drop constraints before droppiong the tables*/
		Logger.log("drop constraints before caCoreSystemTableUpdate ",Logger.DEBUG);
		try
		{
			CreateSchemaParser csp=new CreateSchemaParser();
			HashMap hmTableDropConstraints = null;
			String parseFileName = null;
			/** Select appropriate file to read the queries for dropping constraints on caCore tables*/
			if(Variables.dbIdentifier.equals(Constants.ORACLE))
			{
				parseFileName = new String(Constants.caCoreSystemTables_dropConstraintsOracle);
			}
			else if(Variables.dbIdentifier.equals(Constants.MYSQL))
			{
				parseFileName = new String(Constants.caCoreSystemTables_dropConstraintsMysql);
			}
			Logger.log("parsing file " + parseFileName,Logger.INFO);
			csp.parseFile(parseFileName);
			
			hmTableDropConstraints = csp.getDataSourceConstraints();
			
			/** drop constraints on tables from respective data sources before droping the tables;*/
			Vector vecConstraint = (Vector)hmTableDropConstraints.get(Constants.CACOREDATA);
			if(vecConstraint != null)
			{
				for (Iterator iter = vecConstraint.iterator(); iter.hasNext();) 
				{
					try
					{
						String element = (String) iter.next();
						executeUpdateException(element);
					}
					catch(FatalException ex)
					{
						Logger.log("Fatal exception in drop constraints on caCoreSystemTables " + ex.getMessage(),Logger.WARNING);
					}
				}
			}
		}
		catch(FatalException ex)
		{
			Logger.log("Fatal exception in create schema for dropping constraints",Logger.FATAL);
		}
		Logger.log("drop constraints before caCoreSystemTableUpdate completed",Logger.DEBUG);
		
	}
	
	/**
	 * This function will carry out update activity for the summary tables which included droppping of 
	 * original tables and renaming of _U tables as corresponding original tables.This method is called 
	 * after successful execution of postwork processing*/
	public void summaryTableUpdate()
	{
		Logger.log("postwork table update entered",Logger.INFO);
		/** If geneinfo postwork is being done in the current run as read from the postwork execution
		 * sequence file we will be dropping geneinfo old table and renaming the new geneinfo_u table
		 * as geneinfo*/
		if(Variables.geneinfoPostWork)
		{
			dropTables(Constants.geneinfoTables);
			reNameTables(Constants.geneinfoTables);
		}
		/** If geneinfo_summary postwork is being done in the current run as read from the postwork execution
		 * sequence file we will be dropping geneinfo old table and renaming the new geneinfo_summary_u table
		 * as geneinfo*/
		if(Variables.geneinfo_summaryPostWork)
		{
			dropTables(Constants.geneinfo_summaryTables);
			reNameTables(Constants.geneinfo_summaryTables);
		}
		/** If geneinfo_marray_summary postwork is being done in the current run as read from the postwork execution
		 * sequence file we will be dropping geneinfo old table and renaming the new geneinfo_marray_summary_u table
		 * as geneinfo*/
		if(Variables.geneinfo_marray_summaryPostWork)
		{
			dropTables(Constants.geneinfo_marray_summaryTables);
			reNameTables(Constants.geneinfo_marray_summaryTables);
		}
		/** If chipdescription postwork is being done in the current run as read from the postwork execution
		 * sequence file we will be dropping geneinfo old table and renaming the new chipdescription_u table
		 * as geneinfo*/
		if(Variables.chipdescriptionPostWork)
		{
			dropTables(Constants.chipdescriptionTables);
			reNameTables(Constants.chipdescriptionTables);
		}
		/** If chipinfo_homolo postwork is being done in the current run as read from the postwork execution
		 * sequence file we will be dropping geneinfo old table and renaming the new chipinfo_homolo_u table
		 * as geneinfo*/
		if(Variables.chipinfo_homoloPostWork)
		{
			dropTables(Constants.chipinfo_homoloTables);
			reNameTables(Constants.chipinfo_homoloTables);
		}
		/** If chipinfo_omim postwork is being done in the current run as read from the postwork execution
		 * sequence file we will be dropping geneinfo old table and renaming the new chipinfo_omim_u table
		 * as geneinfo*/
		if(Variables.chipinfo_omimPostWork)
		{
			dropTables(Constants.chipinfo_omimTables);
			reNameTables(Constants.chipinfo_omimTables);
		}
		/** If chipinfo_term postwork is being done in the current run as read from the postwork execution
		 * sequence file we will be dropping geneinfo old table and renaming the new chipinfo_term_u table
		 * as geneinfo*/
		if(Variables.chipinfo_termPostWork)
		{
			dropTables(Constants.chipinfo_termTables);
			reNameTables(Constants.chipinfo_termTables);
		}
		/** If Homologene postwork is being done in the current run as read from the postwork execution
		 * sequence file we will be dropping geneinfo old table and renaming the new homologene_u table
		 * as geneinfo*/
		if(Variables.homologenePostWork)
		{
			dropTables(Constants.homolo_postworkTables);
			reNameTables(Constants.homolo_postworkTables);
		}
	}
	
	/**
	 * This function renames tablelist[i]_UPDATE to tablelist[i].
	 * @param tableList[] The list of tables that needs to be renamed
	 */
	private void reNameTables(String tableList[]) 
	{
		/** Move through the list of tables passed and then rename _U of each of them to original name*/
		for (int i = 0; i < tableList.length; i++) 
		{
			String reNameStmt = null;
			if(Variables.dbIdentifier.equalsIgnoreCase(Constants.ORACLE))
			{
				reNameStmt = "rename " + tableList[i] + Constants.update + " to " + tableList[i];
			}
			else if (Variables.dbIdentifier.equalsIgnoreCase(Constants.MYSQL))
			{
				reNameStmt = "alter table " + tableList[i] + Constants.update + " rename to " + tableList[i];
			}
			Logger.log("Rename  table: " + tableList[i] + Constants.update + " to " + tableList[i],Logger.INFO);
			executeUpdate(reNameStmt);
		}
	}
	
	/**
	 * Method to create _u tables for update mode of Server
	 * @param hmTableCreation Map holding data-source to tables to create map   
	 * @throws FatalException Throws exception if error during execution of query
	 */
	public void prepareTables(HashMap hmTableCreation) throws FatalException
	{
		try
		{
			/** this function will create all the necessary _U tables which will be used for updation purpose*/
			Statement stmt = conn.createStatement();
			Logger.log("Inside PrepareTables for updation",Logger.DEBUG);
			String tabCopyString = " AS SELECT * FROM ";
			
			for(int i = 0; i < Constants.sysCopyTables.length; i++)
			{
				stmt.executeUpdate("CREATE TABLE " + Constants.sysCopyTables[i] + "_U" + 
						tabCopyString + Constants.sysCopyTables[i]);
				Logger.log("created table " + Constants.sysCopyTables[i] + "_U" ,Logger.INFO);
				Logger.log("table count " + Constants.sysCopyTables[i] + "_U" + retCount(Constants.sysCopyTables[i] + "_U"),Logger.DEBUG);
			}
			
			/** put primary key constraints on the system tables so that later on any inserts to 
			 * system termdata and termtree table will be following the constraints.*/
			try
			{
				Logger.log("Executing " + Constants.querySystemTermdataPK,Logger.INFO);
				stmt.executeUpdate(Constants.querySystemTermdataPK);
				Logger.log("Executing " + Constants.querysystemTermtreePK,Logger.INFO);
				stmt.executeUpdate(Constants.querysystemTermtreePK);
			}
			catch(SQLException sqlEx)
			{
				Logger.log("Error in set constraint on system _U tables " + sqlEx.getMessage(),Logger.FATAL);
				/** Here increasing error count does not make sense because: The constraints put on the _U
				 * system term tables are removed in post processing. It is possible that due to some error
				 * during server run the post processing to rename _U tables is not called so the constraints
				 * might preexist in the database on _U tables.
				 */
				//Variables.errorCount++;
			}
			
			/** store what records to delete from system_termtree*/
			Vector deleteList = new Vector();
			/** put a check if Variables.unigeneupdate is true.get all the Unigene table names*/
			if(Variables.ugupdate)
			{
				for(int i = 0 ; i < Constants.ugTables.length; i++)
				{
					String tabName = Constants.ugTables[i];
					String colData = (String) hmTableCreation.get(tabName);
					if(colData != null)
					{
						stmt.executeUpdate("CREATE TABLE " + Constants.ugTables[i] + "_U" + colData);
						Logger.log(" created table " + Constants.ugTables[i] + "_U",Logger.INFO);
					}
					else
					{
						Logger.log(" Table definition not found in script : " + Constants.ugTables[i],Logger.INFO);
					}
				}
				/** Add records to be deleted from termtable,termtree.*/
				deleteList.addElement("'TISSUE_%'");
			}
			if(Variables.llupdate)
			{
				for(int i = 0 ; i < Constants.llTables.length; i++)
				{
					String colData = (String) hmTableCreation.get(Constants.llTables[i]);
					if(colData != null)
					{
						stmt.executeUpdate("CREATE TABLE " + Constants.llTables[i] + "_U" + colData);
						Logger.log(" created table " + Constants.llTables[i] + "_U",Logger.INFO);
					}
				}
				/** Entrezgene contributes to Map & Annot data*/
				deleteList.addElement("'MAP_%'");
				deleteList.addElement("'ANNOT_%'");
			}
			if(Variables.hmlgupdate)
			{
				for(int i = 0 ; i < Constants.hmlgTables.length; i++)
				{
					String colData = (String) hmTableCreation.get(Constants.hmlgTables[i]);
					if(colData != null)
					{
						stmt.executeUpdate("CREATE TABLE " + Constants.hmlgTables[i] + "_U" + colData);
						Logger.log(" created table " + Constants.hmlgTables[i] + "_U",Logger.INFO);
					}
				}   			
			}
			if(Variables.goupdate)
			{
				/** currently there are *no* Go base tables GO contributes to GO data*/
				deleteList.addElement("'GO:%'");
			}
			if(Variables.ustupdate)
			{
				for(int i = 0 ; i < Constants.unistsTables.length; i++)
				{
					String colData = (String) hmTableCreation.get(Constants.unistsTables[i]);
					if(colData != null)
					{
						stmt.executeUpdate("CREATE TABLE " + Constants.unistsTables[i] + "_U" + colData);
						Logger.log(" created table " + Constants.unistsTables[i] + "_U",Logger.INFO);
					}
				}   			
			}
			if(Variables.dbsnpupdate)
			{
				for(int i = 0 ; i < Constants.dbSnpTables.length; i++)
				{
					String colData = (String) hmTableCreation.get(Constants.dbSnpTables[i]);
					if(colData != null)
					{
						stmt.executeUpdate("CREATE TABLE " + Constants.dbSnpTables[i] + "_U" + colData);
						Logger.log(" created table " + Constants.dbSnpTables[i] + "_U",Logger.INFO);
					}
				}   			
			}
			int count = deleteList.size();
			String []deleteRecordList = new String[count];
			for (int i=0; i< count; i++) 
			{
				Logger.log("Delete records like " + deleteRecordList[i],Logger.INFO);
				deleteRecordList[i] = (String)deleteList.elementAt(i);
			}
			/** Delete data from System_termtree_U and System_termdata_U*/
			deleteRecordsLike(deleteRecordList);
			deleteList.clear();
			
			conn.commit();
		}
		catch(SQLException sqlEx)
		{
			Logger.log("Error in prepareTables " + sqlEx.getMessage(),Logger.FATAL);
			Variables.errorCount++;
		}
	}
	
	/**
	 * This function will create _U tables for each of the postwork unit based on whether that postwork
	 * unit is to be processed in current run or not.
	 * @param hmTableCreation	Map having table names and definitions as read from the table creation scripts
	 * @throws FatalException It throws FatalException when query for any _U table creation fails
	 */
	public void preparePostworktables(HashMap hmTableCreation) throws FatalException
	{
		try
		{
			Logger.log("prepare tables for postwork entered",Logger.DEBUG);
			/**If as per postwork execution unit sequence geneinfo postwork unit is to be processed in the current
			 * run then call create table queries for the corrsponding tables. Constants.geneinfoTables list has the 
			 * names of tables which are populated by geneinfo postwork analysis */
			if(Variables.geneinfoPostWork)
			{
				Statement stmt = conn.createStatement();
				Logger.log("Inside PrepareTables for postwork",Logger.DEBUG);
				
				for(int i = 0 ; i < Constants.geneinfoTables.length; i++)
				{
					String tabName = Constants.geneinfoTables[i];
					String colData = (String) hmTableCreation.get(tabName);
					if(colData != null)
					{
						stmt.executeUpdate("CREATE TABLE " + Constants.geneinfoTables[i] + "_U" + colData);
						Logger.log(" created table " + Constants.geneinfoTables[i] + "_U",Logger.INFO);
					}
					else
					{
						Logger.log("Table definition not found in script : " + Constants.geneinfoTables[i],Logger.INFO);
					}
				}
			}
			if(Variables.geneinfo_summaryPostWork)
			{
				Statement stmt = conn.createStatement();
				Logger.log("Inside PrepareTables for postwork",Logger.DEBUG);
				
				for(int i = 0 ; i < Constants.geneinfo_summaryTables.length; i++)
				{
					String tabName = Constants.geneinfo_summaryTables[i];
					String colData = (String) hmTableCreation.get(tabName);
					if(colData != null)
					{
						stmt.executeUpdate("CREATE TABLE " + Constants.geneinfo_summaryTables[i] + "_U" + colData);
						Logger.log(" created table " + Constants.geneinfo_summaryTables[i] + "_U",Logger.INFO);
					}
					else
					{
						Logger.log("Table definition not found in script : " + Constants.geneinfo_summaryTables[i],Logger.INFO);
					}
				}
			}
			
			if(Variables.geneinfo_marray_summaryPostWork)
			{
				Statement stmt = conn.createStatement();
				Logger.log("Inside PrepareTables for postwork",Logger.DEBUG);
				
				for(int i = 0 ; i < Constants.geneinfo_marray_summaryTables.length; i++)
				{
					String tabName = Constants.geneinfo_marray_summaryTables[i];
					String colData = (String) hmTableCreation.get(tabName);
					if(colData != null)
					{
						stmt.executeUpdate("CREATE TABLE " + Constants.geneinfo_marray_summaryTables[i] + "_U" + colData);
						Logger.log(" created table " + Constants.geneinfo_marray_summaryTables[i] + "_U",Logger.INFO);
					}
					else
					{
						Logger.log("Table definition not found in script : " + Constants.geneinfo_marray_summaryTables[i],Logger.INFO);
					}
				}
			}
			
			if(Variables.chipdescriptionPostWork)
			{
				Statement stmt = conn.createStatement();
				Logger.log("Inside PrepareTables for postwork",Logger.DEBUG);
				
				for(int i = 0 ; i < Constants.chipdescriptionTables.length; i++)
				{
					String tabName = Constants.chipdescriptionTables[i];
					String colData = (String) hmTableCreation.get(tabName);
					if(colData != null)
					{
						stmt.executeUpdate("CREATE TABLE " + Constants.chipdescriptionTables[i] + "_U" + colData);
						Logger.log(" created table " + Constants.chipdescriptionTables[i] + "_U",Logger.INFO);
					}
					else
					{
						Logger.log("Table definition not found in script : " + Constants.chipdescriptionTables[i],Logger.INFO);
					}
				}
			}
			
			if(Variables.chipinfo_homoloPostWork)
			{
				Statement stmt = conn.createStatement();
				Logger.log("Inside PrepareTables for postwork",Logger.DEBUG);
				
				for(int i = 0 ; i < Constants.chipinfo_homoloTables.length; i++)
				{
					String tabName = Constants.chipinfo_homoloTables[i];
					String colData = (String) hmTableCreation.get(tabName);
					if(colData != null)
					{
						stmt.executeUpdate("CREATE TABLE " + Constants.chipinfo_homoloTables[i] + "_U" + colData);
						Logger.log(" created table " + Constants.chipinfo_homoloTables[i] + "_U",Logger.INFO);
					}
					else
					{
						Logger.log("Table definition not found in script : " + Constants.chipinfo_homoloTables[i],Logger.INFO);
					}
				}
			}
			
			if(Variables.chipinfo_omimPostWork)
			{
				Statement stmt = conn.createStatement();
				Logger.log("Inside PrepareTables for postwork",Logger.DEBUG);
				
				for(int i = 0 ; i < Constants.chipinfo_omimTables.length; i++)
				{
					String tabName = Constants.chipinfo_omimTables[i];
					String colData = (String) hmTableCreation.get(tabName);
					if(colData != null)
					{
						stmt.executeUpdate("CREATE TABLE " + Constants.chipinfo_omimTables[i] + "_U" + colData);
						Logger.log(" created table " + Constants.chipinfo_omimTables[i] + "_U",Logger.INFO);
					}
					else
					{
						Logger.log("Table definition not found in script : " + Constants.chipinfo_omimTables[i],Logger.INFO);
					}
				}
			}
			
			if(Variables.chipinfo_termPostWork)
			{
				Statement stmt = conn.createStatement();
				Logger.log("Inside PrepareTables for postwork",Logger.DEBUG);
				
				for(int i = 0 ; i < Constants.chipinfo_termTables.length; i++)
				{
					String tabName = Constants.chipinfo_termTables[i];
					String colData = (String) hmTableCreation.get(tabName);
					if(colData != null)
					{
						stmt.executeUpdate("CREATE TABLE " + Constants.chipinfo_termTables[i] + "_U" + colData);
						Logger.log(" created table " + Constants.chipinfo_termTables[i] + "_U",Logger.INFO);
					}
					else
					{
						Logger.log("Table definition not found in script : " + Constants.chipinfo_termTables[i],Logger.INFO);
					}
				}
			}
			if(Variables.homologenePostWork)
			{
				Statement stmt = conn.createStatement();
				Logger.log("Inside PrepareTables for Homologene postwork",Logger.DEBUG);
				
				for(int i = 0 ; i < Constants.homolo_postworkTables.length; i++)
				{
					String tabName = Constants.homolo_postworkTables[i];
					String colData = (String) hmTableCreation.get(tabName);
					if(colData != null)
					{
						stmt.executeUpdate("CREATE TABLE " + Constants.homolo_postworkTables[i] + "_U" + colData);
						Logger.log(" created table " + Constants.homolo_postworkTables[i] + "_U",Logger.INFO);
					}
					else
					{
						Logger.log("Table definition not found in script : " + Constants.homolo_postworkTables[i],Logger.INFO);
					}
				}
			}
		}
		catch(SQLException e)
		{
			Logger.log("SQL exception in prepare tables for postwork",Logger.WARNING);
			throw new FatalException(e.getMessage());
		}
	}
	
	/**
	 * Method to prepare tables for add chip library mode
	 * @param hmTableCreation Hashmap holding list of tables to prepare
	 * @throws FatalException Throws exception if error duing preparing tables
	 */
	public void prepareTablesForAddChip(HashMap hmTableCreation) throws FatalException
	{
		try
		{
			Statement stmt = conn.createStatement();
			Logger.log("Inside PrepareTables for updation",Logger.INFO);
			String tabName = Constants.chipTableName;
			String colData = (String) hmTableCreation.get(tabName);
			if(colData != null)
			{
				stmt.executeUpdate("CREATE TABLE " + Constants.chipTableName + "_U" + colData);
				Logger.log(" created table " + Constants.chipTableName + "_U",Logger.INFO);
			}
			else
			{
				Logger.log(" Table definition not found in script : " + Constants.chipTableName,Logger.INFO);
			}
			
		}
		catch(Exception Ex)
		{
			Logger.log("Error in prepareTablesForAddChip() "+Ex.getMessage(),Logger.FATAL);
			Variables.errorCount++;
		}
	}
	
	
	/**
	 * Function to drop tables.
	 * @param tableList list of tables to be truncated.
	 */
	private void dropTables(String tableList[]) 
	{
		for (int i = 0; i < tableList.length; i++) 
		{
			dropTable(tableList[i]);
		}
	}
	/**
	 * Function to drop specified table
	 * @param tabName Name of the table to drop
	 */
	
	private void dropTable(String tabName)
	{
		String dropStmt = "drop table " + tabName;
		executeUpdate(dropStmt);
		Logger.log("Dropped table: " + tabName,Logger.INFO);
	}
	
	/**
	 * Method to delete records like specified criteria from systemtree and
	 * system_termdata table 
	 * @param predicateList List of predicates to be deleted from tables
	 */
	private void deleteRecordsLike(String predicateList[]) 
	{
		String systermdataStmt = "delete from " + Variables.termTableName +
		" where std_termid like ";
		String systermtreeStmt = "delete from " + Variables.treeTableName +
		" where stt_child_termid like ";
		for (int i = 0; i < predicateList.length; i++) 
		{
			/** Delete from system_termdata*/
			String deleteStmt = systermtreeStmt + predicateList[i];
			executeUpdate(deleteStmt);
			
			deleteStmt = systermdataStmt + predicateList[i];
			executeUpdate(deleteStmt);
		}
	}
	
	
	/**
	 * Insert rows into a ServerStatus table for updating status of parsed data-sources
	 * @param tableList List of queries to execute
	 */
	public void insertRowsForStatus(String tableList[])
	{
		for (int i = 0; i < tableList.length; i++) 
			executeUpdate(tableList[i]);
	}
	
	/**
	 * instantiates a TableInfo object after retriving the table metadata from
	 * the database
	 * @param tableName Name of the table to initialize
	 * @exception FatalException Throws exception if error during initialization
	 */
	public synchronized void initTable(String tableName) throws FatalException 
	{
		try
		{
			Statement stmt = conn.createStatement();
			/** Check if tabel has been already initialised in that case skip init.*/
			if(null == tableInfoHTable.get(tableName.toUpperCase()))
			{
				Logger.log("Initialised table: " + tableName,Logger.INFO);
				ResultSet rSet =
					stmt.executeQuery("select * from " + tableName + " where 1=2");
				ResultSetMetaData mData = rSet.getMetaData();
				TableInfo table = new TableInfo(mData.getColumnCount());
				/** Get the table information*/
				for (int i=0; i<table.noOfColumns; i++)
				{
					table.colNames[i] = mData.getColumnName(i + 1);
					table.colTypes[i] = mData.getColumnType(i + 1);
					table.colPrecision[i] = mData.getPrecision(i + 1);
					table.nullable[i]	= mData.isNullable(i + 1);
				}
				/** Table information complete, store it in the hashtable*/
				tableInfoHTable.put(tableName.toUpperCase(), table);
				conn.commit();
				stmt.close();
			}
		} 
		catch (SQLException sqlEx)
		{
			Variables.errorCount++;
			throw new FatalException("Error in initTable: " + sqlEx.getMessage());
		}
	}
	/**
	 * Method to return column names for given table
	 * @param tableName Name of the table
	 * @return List of column names for given table name
	 * @throws FatalException Throws exception if error while getting column names
	 */
	public String[] getFieldNames(String tableName) throws FatalException 
	{
		try
		{
			Statement stmt = conn.createStatement();
			ResultSet rSet =
				stmt.executeQuery("select * from " + tableName + " where 1=2");
			ResultSetMetaData mData = rSet.getMetaData();
			int noOfFields = mData.getColumnCount();
			String [] fieldNames = new String[noOfFields];
			for (int i=0; i< noOfFields; i++) 
			{
				fieldNames[i] =  mData.getColumnName(i+1);
			}
			stmt.close();
			return fieldNames;
		} 
		catch (SQLException sqlEx) 
		{
			Variables.errorCount++;
			throw new FatalException("Error in getFieldNames: " + sqlEx.getMessage());
		}
	}
	
	/**
	 * Method to check if record to be loaded in the DB table is valid according
	 * to nullability constraint on the table columms.If column which should not 
	 * be null is null then the function returns false.
	 * @param rec the record to be written,tableName Name of the table
	 * @return true/false based on if record is valis/invalid according to the
	 * nullability constraints on table columns.
	 */
	public synchronized boolean checkNullability(Record rec, String tabName)
	{
		TableInfo table = (TableInfo)tableInfoHTable.get(tabName.toUpperCase());
		for (int i=0; i<table.noOfColumns; i++)
		{
			int nullability = table.nullable[i];
			if(nullability != ResultSetMetaData.columnNullableUnknown)
			{
				if(nullability == ResultSetMetaData.columnNoNulls)
				{
					/** since the column can't be null return false if it is null*/
					if(rec.fields[i].toString().equals("") || rec.fields[i].toString().equals("-") || rec.fields[i].toString().equalsIgnoreCase("null"))
					{
						return false;
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * Method to return list of column names and respective type for given table
	 * @param tableName Name of the table
	 * @return List of column names and respective type for given table name
	 * @throws FatalException Throws exception if error while getting column names
	 */
	public String[][] getFieldNamesANDType(String tableName) throws FatalException 
	{
		try 
		{
			Statement stmt = conn.createStatement();
			ResultSet rSet =
				stmt.executeQuery("select * from " + tableName + " where 1=2");
			ResultSetMetaData mData = rSet.getMetaData();
			int noOfFields = mData.getColumnCount();
			String [][] fieldNames = new String[noOfFields][3];
			for (int i=0; i< noOfFields; i++) 
			{
				fieldNames[i][0] =  mData.getColumnName(i + 1);
				fieldNames[i][1] = mData.getColumnTypeName(i + 1);
				fieldNames[i][2] = String.valueOf(mData.getPrecision(i + 1));
			}
			stmt.close();
			return fieldNames;
		} 
		catch (SQLException sqlEx) 
		{
			Variables.errorCount++;
			throw new FatalException("Error in getFieldNames: " + sqlEx.getMessage());
		}
	}
	
	/**
	 *  Fills the hashtable for homologene organism names and there taxonomy id's.
	 *  @exception FatalException if error while filling details 
	 */
	public void mapTaxonomyId() throws FatalException 
	{
		try 
		{
			Statement stmt = conn.createStatement();
			ResultSet rSet =
				stmt.executeQuery("select * from " + Variables.organismTaxonomyMapTableName);
			while(rSet.next())
			{
				Variables.orgtaxMap.put(rSet.getString(1),rSet.getString(2));
			}
			stmt.close();
		}
		catch (SQLException sqlEx) 
		{
			Variables.errorCount++;
			throw new FatalException("Error in mapTaxonomyId(): " + sqlEx.getMessage());
		}
	}
	
	
	/**
	 * returns the # of fields in the table
	 * @param tableName name of the table
	 * @return int no of fields in the table, 0 if the table is not created.
	 */
	public int noOfColumns(String tableName) 
	{
		TableInfo table = (TableInfo)tableInfoHTable.get(tableName.toUpperCase());
		if (table != null)
			return table.noOfColumns;
		else
			return 0;
	}
	
	/**
	 * returns the tableInfo object for the table
	 * @param tableName Name of the table
	 */
	public int[] getPrecision(String tableName) 
	{
		TableInfo table =  (TableInfo)tableInfoHTable.get(tableName.toUpperCase());
		if (table != null)
		{
			return table.colPrecision;
		}
		else
			return null;
	}
	
	
	/**
	 * connects to the database specified by the dbURL
	 * @param driverName Name of the database driver
	 * @param dbURL database url string
	 * @param userName database user name
	 * @param passWord database password
	 * @throws FatalException throws exception if error during connection
	 */
	public void connect(String driverName, String dbURL, String userName, String passWord)
			throws FatalException
	{
		m_driverName = driverName;
		m_dbURL = dbURL;
		m_userName = userName;
		m_passWord = passWord;
		/** load the driver, which also registers the driver*/
		try
		{
			Class.forName(driverName);
		}
		catch (ClassNotFoundException e)
		{
			Variables.errorCount++;
			throw new FatalException("driver " + driverName + " not found");
		}
		try
		{
			conn = DriverManager.getConnection(dbURL, userName, passWord);
			Logger.log("connection successful", Logger.INFO);
			conn.setAutoCommit(false);
		}
		catch (SQLException sqlEx)
		{
			/** Unable to establish a connection through the driver manager.*/
			Variables.errorCount++;
			throw new FatalException("SQLException: " + sqlEx.getMessage());
		}
	}
	
	/**
	 * Connects to the database using db propeties specified in server.peoperties.
	 * This method can be used when db manger is invoked out of server run and db properties are not loaded. 
	 * @throws FatalException
	 */
	public void connect() throws FatalException
	{
		//Check whether invoked out of server run and db properties are not loaded. 
		if (Variables.dbUserId == null ||  Variables.dbUserId.equals(""))
		{
			//Load db propeties specified in server.peoperties.
			loadProperties();
		}
		connect(Variables.driverName, Variables.dbURL, Variables.dbUserId,
				Variables.dbUserPsswd);
	}
		
	/**
	 * Loads database connection properties from server.properties file.
	 */
	private void loadProperties()
	{
		String fileSep = System.getProperty("file.separator");
		String fileName = Variables.currentDir + fileSep + "Config" + fileSep
				+ Constants.serverPropertiesFile;
		PropertiesFileHandeler pfh;
		try
		{
			pfh = new PropertiesFileHandeler(fileName);

			/** Read username and password for database from command line */
			Variables.dbUserId = pfh.getValue(Constants.DATABASE_USERNAME).trim();
			Variables.dbUserPsswd = pfh.getValue(Constants.DATABASE_PASSWORD).trim();

			/** Set the temperory directory to the user directory */
			Variables.tempDir = System.getProperty("user.dir");

			/** Set database configuration parameters from command lines into the global variables*/
			Variables.dbConnect = pfh.getValue(Constants.DATABASE_CONNECT).trim();
			Variables.driverName = pfh.getValue(Constants.DATABASE_DRIVER).trim();
			Variables.dbURL = pfh.getValue(Constants.DATABASE_URL).trim();

			String dbType = pfh.getValue(Constants.DATABASE_TYPE).trim();

			/** If the database type is other than MySQL and Oracle then the execution will  terminate logging below message*/
			if ((!(dbType.equalsIgnoreCase(Constants.ORACLE))))
			{
				Logger.log("Invalid Data base identifier. Only Oracle is allowed.", Logger.FATAL);
				System.out
						.println("Exception: Invalid Data base identifier. Only Oracle is allowed.");
				System.exit(1);
			}

			Variables.dbIdentifier = Constants.ORACLE;
		}
		catch (NonFatalException nfe)
		{
			Logger.log("Non Fatal Error Loading Properties from Properties File "
					+ nfe.getMessage(), Logger.WARNING);
		}
		catch (ApplicationException ae)
		{
			Logger.log("Application Error Loading Properties from Properties File "
					+ ae.getMessage(), Logger.WARNING);
		}
		catch (Exception e)
		{
			Logger.log("Exception while Loading Properties from Properties File " + e.getMessage(),
					Logger.WARNING);
		}
	}
	
	/**
	 * constructs the prepareStmt to insert a row in a table
	 * @param tableName name of the table where the row is to be inserted
	 * @param table Informtation about given table 
	 * @exception FatalException error if prepareStmt cannot be created
	 */
	private void prepareInsert(String tableName, TableInfo table) throws FatalException 
	{
		try 
		{
			String columns = "(";
			String places = "(";
			for (int i=0; i< table.noOfColumns -1; i++) 
			{
				columns += table.colNames[i] + ", ";
				places += "?, ";
			}
			/** Taking care of "," after the last column*/
			columns += table.colNames[table.noOfColumns-1] + ")";
			places += "? )";
			String insert = "insert into " + tableName + " " + columns
			+ " values " + places;
			table.ps = conn.prepareStatement(insert);
		}
		catch (SQLException sqlEx)
		{
			Variables.errorCount++;
			throw new FatalException("prepareInsert: " + sqlEx.getMessage());
		}
	}
	
	/**
	 * inserts a new row into the database table.
	 * @param tableName the name of the db table
	 * @param row the record to be inserted
	 * @exception FatalException if error while inserting rows into specified table
	 * @exception SQLException error in inserting the record
	 */
	public synchronized void insertRow(String tableName, StringBuffer row[])
	throws SQLException, FatalException
	{
		TableInfo table = (TableInfo)tableInfoHTable.get(tableName.toUpperCase());
		if (null == table.ps)
		{
			prepareInsert(tableName, table);
		}
		/** insert the row*/
		for (int i=0; i< row.length; i++) 
		{
			if (0 == row[i].length())
			{
				/** if data to be inserted is null*/
				table.ps.setNull(i+1, table.colTypes[i]);
			} 
			else
			{
				/** make sure that the row data is within limit*/
				if (row[i].length() > table.colPrecision[i]) 
				{
					if((table.colTypes[i] == java.sql.Types.VARCHAR) || (table.colTypes[i] == java.sql.Types.CHAR) )
					{
						row[i].setLength(table.colPrecision[i]);
					}
				}
				table.ps.setObject(i+1, row[i].toString());
			}
			
		}
		Logger.log("inserting values " + table.ps.toString(),Logger.INFO);
		table.ps.executeUpdate();
		insertCount++;
		if(0 == (insertCount % Variables.batchValue)) 
		{
			conn.commit();
			if(tableName.equalsIgnoreCase(Constants.serverStatusTableName)|| tableName.equalsIgnoreCase(Constants.serverFileStatusTableName))
				conn.commit();
		}
		
	}
	
	/** 
	 * Method to execute execute Update query
	 * @param query Query string to execute  
	 */
	public void executeUpdate(String query)
	{
		try
		{
			Statement stmt = conn.createStatement();
			Logger.log("Executing: " + query,Logger.INFO);
			long startTime = System.currentTimeMillis();
			stmt.executeUpdate(query);
			long endTime = System.currentTimeMillis();
			long queryTime = endTime - startTime;
			Logger.log("Query Time: " + queryTime,Logger.INFO);
			Logger.log("Done ",Logger.INFO);
			stmt.close();
		}
		catch (SQLException sqlEx)
		{
			Variables.errorCount++;
			Logger.log("error in executeUpdate: " + sqlEx.getMessage(),Logger.INFO);
		}
	}
	
	/** 
	 * Method to execute execute Update query
	 * @param query Query string to execute
	 * @exception FatalException thrown when SQL error occurs while executing query  
	 */
	public void executeUpdateException(String query) throws FatalException
	{
		try
		{
			Statement stmt = conn.createStatement();
			Logger.log("Executing: " + query,Logger.INFO);
			long startTime = System.currentTimeMillis();
			stmt.executeUpdate(query);
			long endTime = System.currentTimeMillis();
			long queryTime = endTime - startTime;
			Logger.log("Query Time: "+queryTime,Logger.INFO);
			Logger.log("Done ",Logger.INFO);
			stmt.close();
		}
		catch (SQLException sqlEx)
		{
			Logger.log("error in executeUpdate: " + sqlEx.getMessage(),Logger.INFO);
			throw new FatalException(sqlEx.getMessage());
		}
	}
	
	
	
	/**
	 * Method to execute update query. Also this method doesn't increment Variables.errorCount
	 * variable if error occurs while update table 
	 * @param query Query string to execute
	 */
	public void executeUpdateSupressError(String query) 
	{
		int cnt = Variables.errorCount;
		try 
		{
			Statement stmt = conn.createStatement();
			Logger.log("Executing: " + query,Logger.INFO);
			long startTime = System.currentTimeMillis();
			stmt.executeUpdate(query);
			long endTime = System.currentTimeMillis();
			long queryTime = endTime - startTime;
			Logger.log("Query Time: "+queryTime,Logger.INFO);
			Logger.log("Done ",Logger.INFO);
			stmt.close();
		}
		catch (SQLException sqlEx)
		{
			Variables.errorCount++;
			Logger.log("error in executeUpdate: " + sqlEx.getMessage(),Logger.WARNING);
		}
		Variables.errorCount = cnt;
	}
	
	/** 
	 * Method to execute Query and return result as a array of String 
	 * @param query Query string
	 * @return String[] Result of executing query
	 */
	public String[] executeQuery(String query) 
	{
		String [] resultCols = null;
		try
		{
			Statement stmt = conn.createStatement();
			Logger.log("Executing: " + query,Logger.DEBUG);
			/** Execute the query*/
			long startTime = System.currentTimeMillis();
			stmt.executeQuery(query);
			long endTime = System.currentTimeMillis();
			long queryTime = endTime - startTime;
			Logger.log("Query Time: "+queryTime,Logger.INFO);
			Logger.log("Done ",Logger.INFO);
			stmt.close();
		} 
		catch (SQLException sqlEx)
		{
			Variables.errorCount++;
			Logger.log("error in executeQuery: " + sqlEx.getMessage(),Logger.INFO);
		}
		return resultCols;
	}
	
	
	public ResultSet createCursor(String query,boolean isStreaming) throws FatalException 
	{
		ResultSet rs = null;
		try
		{
			Connection useConnection = conn;
			/** Fetch size is set by default to 200 which is number of rows fetched and kept in memory at a time
			 * for MySQL this will be set to Integer.MIN_VALUE so that we get one row fetched at a time.*/
			int intFetchSize = 200;
			/** if streaming cursor : cursor with one row each time you do next() is required for MySQL then
			 * we need to do workaround of setting the Fetch Size to Interger.MIM_VALUE and have one connection
			 * per result set. This is not required in case of Oracle.*/
			if(Variables.dbIdentifier.equals(Constants.MYSQL))
			{
				/**create streaming cursor with new connection in case of MySQL. If new connection is not created 
				 * then the existing one is used.*/
				if(true == isStreaming)
				{
					Connection mysqlConn;
					try 
					{
						Class.forName(m_driverName);
					} 
					catch (ClassNotFoundException e)
					{
						Variables.errorCount++;
						throw new FatalException("driver " + m_driverName + " not found");
					}
					try 
					{
						/** get connection to the specified database.*/
						mysqlConn = DriverManager.getConnection(m_dbURL, m_userName, m_passWord);
						Logger.log("connection successful",Logger.INFO);
						mysqlConn.setAutoCommit(false);
						m_streamingConnections.add(mysqlConn);
						useConnection = mysqlConn;
						intFetchSize = Integer.MIN_VALUE;
					}
					catch (SQLException sqlEx)
					{
						/** Unable to establish a connection through the driver manager.*/
						Variables.errorCount++;
						throw new FatalException("SQLException: " + sqlEx.getMessage());
					}
					
				}
			}
			Statement stmt = useConnection.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			stmt.setFetchSize(intFetchSize);
			Logger.log("Fetch size: " + stmt.getFetchSize(), Logger.INFO);
			Logger.log("Executing: " + query, Logger.INFO);
			long startTime = System.currentTimeMillis();
			rs = stmt.executeQuery(query);
			long endTime = System.currentTimeMillis();
			long queryTime = endTime - startTime;
			Logger.log("Query Time: " + queryTime, Logger.INFO);
		}
		catch (SQLException sqlEx)
		{
			Logger.log("error in executeQuery: " + sqlEx.getMessage(),Logger.INFO);
			throw new FatalException(sqlEx.getMessage());
			
		}
		Logger.log("created cursor for above qurty",Logger.INFO);
		return rs;
	}
	
	/** 
	 * Method to execute Query and return result as a array of String 
	 * @param query Query string
	 * @return String[] Result of executing query
	 */
	public String [] executeQuery2(String query) 
	{
		String [] resultCols = null;
		try
		{
			Statement stmt = conn.createStatement();
			Logger.log("Executing: " + query,Logger.INFO);
			long startTime = System.currentTimeMillis();
			/** execute the query*/
			ResultSet rs = stmt.executeQuery(query);
			long endTime = System.currentTimeMillis();
			long queryTime = endTime - startTime;
			Logger.log("Query Time: "+queryTime,Logger.INFO);
			/** get the result set metadata*/
			ResultSetMetaData rsmd = rs.getMetaData();
			int noOfResultCols = rsmd.getColumnCount();
			/** return the first row*/
			resultCols = new String [noOfResultCols];
			for (int i=1; i <= noOfResultCols; i++) 
			{
				resultCols[i] = rs.getString(i);
			}
			Logger.log("Done ",Logger.INFO);
			rs.close();
			stmt.close();
			
		} 
		catch (SQLException sqlEx) 
		{
			Variables.errorCount++;
			Logger.log("error in executeQuery: " + sqlEx.getMessage(),Logger.INFO);
		}
		return resultCols;
	}
	
	/**
	 * Method to update organism taxonomy map
	 * @param fOrgtaxMap FileWriter object for writing taxonomy details into a file
	 */
	public void updateOrganismTaxonomyMap(FileWriter fOrgtaxMap)
	{
		try
		{
			long MaxCnt = retCount(Variables.organismTaxonomyMapTableName);
			Statement stmt = conn.createStatement();
			String query = Constants.queryOrganismTaxonomyUpdate;
			Logger.log("Executing: " + query,Logger.INFO);
			long startTime = System.currentTimeMillis();
			//execute the query
			ResultSet rs = stmt.executeQuery(query);
			long endTime = System.currentTimeMillis();
			long queryTime = endTime - startTime;
			Logger.log("Query Time: "+queryTime,Logger.INFO);
			
			long i = MaxCnt+1;
			Logger.log("No of records in OrgTaxmap before update " + i,Logger.INFO);
			while(rs.next())
			{
				/** insert each entry in the organism_taxonomymap table.*/
				fOrgtaxMap.write(i + Constants.columnSeparator + rs.getString(1) + Constants.columnSeparator + 
						rs.getString(2) + Constants.columnSeparator);
				fOrgtaxMap.write("\n");
				i++;
			}
			Logger.log("No of records in OrgTaxmap after update " + i,Logger.INFO);
			rs.close();
			stmt.close();
		}
		catch(SQLException e)
		{
			Logger.log("SQL Exception in organismTaxonomymap ",Logger.FATAL);
			Logger.log(e.getMessage(),Logger.DEBUG);
		}
		catch(IOException e)
		{
			Logger.log("Error in writing o/p file for organism taxonomy update",Logger.INFO);
		}
	}
	/**
	 * Method to synchronize organism taxonomy map
	 */
	public void synchroniseOrganismTaxonomyMap()
	{
		try
		{
			Statement stmt = conn.createStatement();
			String query = Constants.queryFindModifiedTaxids;
			Logger.log("Executing: " + query,Logger.INFO);
			long startTime = System.currentTimeMillis();
			/** execute the query to get the records which have their taxids changed
			 * to new ones from the history table.*/
			ResultSet rs = stmt.executeQuery(query);
			long endTime = System.currentTimeMillis();
			long queryTime = endTime - startTime;
			Logger.log("Query Time: "+queryTime,Logger.INFO);
			
			while(rs.next())
			{
				String newTaxid = rs.getString(1);
				String oldTaxid = rs.getString(2);
				query = "UPDATE ORGANISM_TAXONOMYMAP SET OTM_TAXID=" + newTaxid + " WHERE OTM_TAXID=" + oldTaxid;
				executeUpdate(query);
			}
			rs.close();
			stmt.close();
		}
		catch(SQLException e)
		{
			Logger.log("SQL Exception in organismTaxonomymap ",Logger.FATAL);
			Logger.log(e.getMessage(),Logger.DEBUG);
		}
	}
	/**
	 * Method to populate organism taxonomy map
	 */
	public void populateOrgaismTaxonomyMap()
	{
		try
		{
			Statement stmt = conn.createStatement();
			String query = Constants.queryReadTaxonomyMap;
			Logger.log("Executing: " + query,Logger.INFO);
			long startTime = System.currentTimeMillis();
			/** execute the query*/
			ResultSet rs = stmt.executeQuery(query);
			long endTime = System.currentTimeMillis();
			long queryTime = endTime - startTime;
			Logger.log("Query Time: "+queryTime,Logger.INFO);
			
			int i=0;
			while(rs.next())
			{
				/** OTM_LOCAL_TAXID,OTM_TAXID,OTM_OTM_ORGNAME*/
				Variables.hmOrganismLocalId.put(rs.getString(3).trim(),rs.getString(1).trim());
				Variables.hmTaxidLocalId.put(rs.getString(2).trim(),rs.getString(1).trim());
				i++;
			}
			Logger.log("Added " + i + " records in maps for organism taxonomy",Logger.INFO);
			rs.close();
			stmt.close();
		}
		catch(SQLException e)
		{
			Logger.log("SQL Exception in organismTaxonomymap ",Logger.FATAL);
			Logger.log(e.getMessage(),Logger.DEBUG);
		}
	}

	
	/**
	 *This function trims the date object and returns a String
	 * in the required order.
	 * @param date The modify date as received from the ftp site.
	 */
	public String dateTrimmer(Date date)
	{
		String tempDate=new String(date.toString());
		/** This method should use standard date function for manipulating the date instead of
		 * following crude method */
		HashMap hmMonths = new HashMap();
		hmMonths.put("Jan","01");
		hmMonths.put("Feb","02");
		hmMonths.put("Mar","03");
		hmMonths.put("Apr","04");
		hmMonths.put("May","05");
		hmMonths.put("June","06");
		hmMonths.put("July","07");
		hmMonths.put("Aug","08");
		hmMonths.put("Sep","09");
		hmMonths.put("Oct","10");
		hmMonths.put("Nov","11");
		hmMonths.put("Dec","12");
		
		StringTokenizer st=new StringTokenizer(tempDate," ");
		StringBuffer finDate = new StringBuffer();
		String month=null;
		String day = null;
		String year = null;
		int i =1;
		while (st.hasMoreTokens() && i<13) 
		{
			tempDate=st.nextToken();
			/** 2nd,3rd,6th token to be added inorder to obtain date in MM/DD/YY format.*/
			if((i == 2)||(i == 3)||(i == 6))
			{
				if(2 == i)
				{
					month = tempDate;
				}
				else if(3 == i)
				{
					day = tempDate;
				}
				else if(6 == i)
				{
					year = tempDate;
				}
				finDate.append(tempDate);
				if(i != 6)
				{
					finDate.append("/");
				}
			}
			i++;
			/** mysql yyyy/mm/dd*/
		}
		if(Variables.dbIdentifier.equalsIgnoreCase(Constants.MYSQL))
		{
			String monNumeric = (String)hmMonths.get(month);
			finDate.setLength(0);
			finDate.append(year + "/");
			finDate.append(monNumeric + "/");
			finDate.append(day);
		}
		return finDate.toString();
	}
	
	
	/**
	 * This Function returns true if file being modified
	 * is of a date after the lastupdaet date.
	 * @param date represents current modify date of the file at ftp site.
	 * @param fileName filename whose modify date is to be checked with DB.
	 * @return boolean true if file is to be ftp'd false otherwise.
	 * @exception SQLException if the query to Db fails.
	 */
	
	public boolean dateCheck(Date date,String fileName)
	{
		try
		{
			boolean filePresent=false;
			String finDate=dateTrimmer(date);
			String checkFile="SELECT * FROM SERVER_FILE_STATUS where SFS_FILENAME LIKE '"+fileName+"' ";
			String checkDate = "SELECT * FROM SERVER_FILE_STATUS WHERE SFS_MODIFY_DATE ="+ Variables.dateFunction + "('"+finDate+"','" + Variables.dateFormat + "') AND SFS_FILENAME LIKE '"+fileName+"' ";
			Statement stmt = conn.createStatement();
			Logger.log("Executing: " +checkFile,Logger.INFO);
			/** execute the query*/
			long startTime = System.currentTimeMillis();
			/** This query returns us the last Server Update date.*/
			ResultSet rs = stmt.executeQuery(checkFile);
			if(rs.next())
			{
				
				/** FilePresent = false --> that file is being added first time to db.*/
				filePresent = true;
			}
			if(false == filePresent)
			{
				return true;
			}
			else
			{
				Logger.log("Executing: " +checkDate,Logger.INFO);
				rs = stmt.executeQuery(checkDate);
				long endTime = System.currentTimeMillis();
				long queryTime = endTime - startTime;
				Logger.log("Query Time: "+queryTime,Logger.INFO);
				Logger.log("Done ",Logger.INFO);
				if(rs.next())
				{
					return false;
				}
			}
		}
		catch(SQLException sqlEx)
		{
			Logger.log("error in dateCheck(Date date,String fileName ) " + sqlEx.getMessage(),Logger.INFO);
		}
		return true;
	}
	
	/**
	 * returns the no of records in the table - count(*) tableName
	 * @param tableName Table name string
	 * @return String Returns result of count(*) as string 
	 */
	public String count(String tableName)
	{
		String query = "SELECT count(*) from " + tableName;
		String count = null;
		try
		{
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) 
			{
				count = rs.getString(1);
			}
			rs.close();
			stmt.close();
		} 
		catch (SQLException sqlEx)
		{
			Logger.log("Couldn't execute query: " + query,Logger.INFO);
			Logger.log(sqlEx.getMessage(),Logger.INFO);
		}
		return count;
	}
	
	/** 
	 * Disconnects from the database
	 * @exception FatalException Throws error if error while closing database connection 
	 */
	public void disconnect() throws FatalException 
	{
		try
		{
			Logger.log("total records inserted = " + insertCount,Logger.DEBUG);
			Enumeration values = tableInfoHTable.elements();
			TableInfo tableInfo;
			if(values!=null)
			{
				while(values.hasMoreElements())
				{
					tableInfo = (TableInfo)values.nextElement();
					if(tableInfo!=null)
						if(tableInfo.ps!=null)
							tableInfo.ps.close();
						else
						{
							Logger.log("TableInfo.ps object is null.",Logger.INFO);
						}
					else
					{
						Logger.log("TableInfo object is null.",Logger.INFO);
					}
				}
			}
			else
			{
				Logger.log("Values is null in DBManager.disconnect",Logger.INFO);
			}
			if(conn!=null)
			{
				conn.commit();
				conn.close();
			}
			else
			{
				Logger.log("conn is null in DBManager.disconnect",Logger.INFO);
			}
			/** closing the extra connections opened for creating streaming cursors required to get data row by row
			 * in case of MySQL database.*/
			for(int i = 0; i < m_streamingConnections.size(); i++)
			{
				Connection connTemp = (Connection)m_streamingConnections.get(i);
				if(null != connTemp)
				{
					connTemp.close();
				}
			}
		} 
		catch (SQLException sqlEx)
		{
			throw new FatalException("couldn't close db: " + sqlEx.getMessage());
		}
	}
	
	/**
	 * This function returns the chiptypeID for the given chip name after
	 * querying the CHIPTYPES table.
	 * @param chipName the chip name to be searched in the CHIPTYPES table
	 * @return the chiptypeid corresponding to the chipname in the Chiptypes
	 * table. 0 if no corresponding chiptypeid is found for the given chipname.
	 */
	public int getChipTypeID(String chipName)
	{
		String query = "SELECT CTY_CHIPTYPEID FROM "+Constants.chipTypesTableName+
		" WHERE UPPER(CTY_CHIPNAME) = '"+chipName+"'";
		int chipTypeID = 0;
		Statement stmt = null;
		try 
		{
			stmt = conn.createStatement();
			Logger.log("Executing: " + query,Logger.INFO);
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next())
			{
				/**getString returns the column value OR null (for SQL NULL)*/
				Logger.log("rs string " + rs.getString("CTY_CHIPTYPEID"),Logger.INFO);
				chipTypeID = rs.getInt("CTY_CHIPTYPEID");
			}
			rs.close();
			stmt.close();
		} 
		catch (SQLException sqlEx)
		{
			Logger.log("Couldn't execute query: " + query,Logger.WARNING);
			Logger.log(sqlEx.getMessage(),Logger.DEBUG);
		}
		if (stmt != null)
		{
			try 
			{
				stmt.close();
			}
			catch(SQLException e) 
			{ 
				/** Trap SQL Errors*/
				Logger.log("Error..."+e.toString(),Logger.DEBUG);
			}
		}
		/**return the chiptypeid*/
		return chipTypeID;
	}
	
	/**
	 * This function checks whether chip name passed is already present in 
	 * the chiptypes table if so then it returns that chiptypeid else it will
	 * add new id to chiptypes table with the chip name passed.
	 * @param Chip Name, Organism name 
	 * @return the chiptypeid corresponding to the ChipName
	 */
	public int addChipName(String chipName,String species)
	{
		Logger.log("species of newly added chip " + species,Logger.DEBUG);
		String query = "SELECT CTY_CHIPTYPEID FROM "+Constants.chipTypesTableName+
		" WHERE UPPER(CTY_CHIPNAME) = '"+chipName+"'";
		int chipTypeID = 0;
		Statement stmt = null;
		try 
		{
			stmt = conn.createStatement();
			Logger.log("Executing: " + query,Logger.INFO);
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next())
			{
				/**chip name found in the table so return chiptypeid.*/
				Logger.log("rs string " + rs.getString("CTY_CHIPTYPEID"),Logger.INFO);
				chipTypeID = rs.getInt("CTY_CHIPTYPEID");
			}
			else
			{
				/**chip name not found in the table so add new chipname and return
				*the newly added chiptypeid.*/
				String maxQuery = "SELECT MAX(CTY_CHIPTYPEID) FROM "+Constants.chipTypesTableName;
				chipTypeID = execQuery(maxQuery);
				chipTypeID = chipTypeID + 1;
				/** now insert this new chiptype id with the identifier*/
				String insertQuery = "INSERT INTO " + Constants.chipTypesTableName + " VALUES("+
				chipTypeID + ",'" + chipName + "','" + species + "',NULL,NULL)";
				executeUpdate(insertQuery);
				conn.commit();
			}
			rs.close();
			stmt.close();
		} 
		catch (SQLException sqlEx)
		{
			Logger.log("Couldn't execute query: " + query,Logger.WARNING);
			Logger.log(sqlEx.getMessage(),Logger.DEBUG);
		}
		if (stmt != null)
		{
			try 
			{
				stmt.close();
			}
			catch(SQLException e) 
			{ 
				/**Trap SQL Errors*/
				Logger.log("Error..."+e.toString(),Logger.DEBUG);
			}
		}
		/**return the chiptypeid*/
		return chipTypeID;
		
	}
	
	/**
	 * This function returns the count of the given chiptypeID after
	 * querying the CHIPINFORMATION table.
	 * @param chipTypeID the chiptypeID to be searched in the CHIPINFORMATION table
	 * @return the count of records having the corresponding chiptypeid
	 */
	public int getChipTypeIDCount(int chipTypeID)
	{
		String query = "SELECT COUNT(*) FROM "+Constants.chipTableName+
		" WHERE CIN_CHIPTYPEID = "+chipTypeID;
		int count = -1;
		Statement stmt = null;
		try
		{
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) 
			{
				/**getString returns the column value OR null (for SQL NULL)*/
				count = rs.getInt(1);
			}
			rs.close();
			stmt.close();
		}
		catch (SQLException sqlEx)
		{
			Logger.log("Couldn't execute query: " + query,Logger.WARNING);
			Logger.log(sqlEx.getMessage(),Logger.DEBUG);
		} 
		if (stmt != null)
		{
			try
			{
				stmt.close();
			}
			catch(SQLException e)
			{ 
				Logger.log("Error..."+e.toString(),Logger.DEBUG);
			}
		}
		/**return the chiptypeid*/
		return count;
	}
	
	/**
	 * This function rolls back the chip having the chiptypeID from the
	 * CHIPINFORMATION table.
	 * @param chipTypeID the chiptypeID to be deleted from the CHIPINFORMATION table
	 */
	public void rollbackChip(int chipTypeID)
	{
		String deleteStmt = "delete from " + Constants.chipTableName + " where CIN_CHIPTYPEID=" + chipTypeID;
		try
		{
			executeUpdate(deleteStmt);
			conn.commit();
		}
		catch(SQLException exp)
		{
			Logger.log("Could not rollback newly added chip ",Logger.WARNING);
		}
	}
	
	/**
	 * it gets Long value from first row's first coloumn.
	 * Typically used for functions like SUM etc.
	 * @param query Query that has the single valued function
	 * @return the value as returned by the function.
	 */
	public long getLongFormQuery(String query) throws ApplicationException
	{
		ResultSet rs = null;
		Statement stmt = null;
		String longValue = new String();
		long number = 0;
		try
		{
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery(query);
			
			//get the result set metadata
			ResultSetMetaData rsmd = rs.getMetaData();
			boolean more = rs.next();
			
			if (true == more)
			{
				int noOfResultCols = rsmd.getColumnCount();
				if (noOfResultCols > 0)
				{
					longValue = rs.getString(1);
					number = Long.parseLong(longValue);
				}
				else
					throw new ApplicationException(
					"No records found in DBConnection, getLongFormQuery");
			}
			rs.close();
			stmt.close();
		}
		catch (SQLException sqlEx)
		{
			Logger.log("Unable to get a Long value from database",
					Logger.WARNING);
			Logger.log("Query was: " + query, Logger.WARNING);
			throw new ApplicationException(sqlEx.getMessage());
			
		}
		catch (NumberFormatException nfe)
		{
			Logger.log("Unable to parse: " + longValue, Logger.WARNING);
			Logger.log("Query was: " + query, Logger.DEBUG);
			throw new ApplicationException(nfe.getMessage());
		}
		
		return number;
	}
	
	/**
	 * Commits the transactions
	 */
	public void commit()
	{
		try
		{
			conn.commit();
		}
		catch (SQLException sqle)
		{
			Logger.log("Unable to commit ", Logger.WARNING);
			Logger.log("Exception= " + sqle.getMessage(), Logger.WARNING);
		}
	}
	
	/**
	 * Commits the transactions
	 * @throws FatalException when commit fails
	 */
	public void commitFatal() throws FatalException
	{
		try
		{
			conn.commit();
		}
		catch (SQLException sqle)
		{
			Logger.log("Unable to commit ", Logger.WARNING);
			Logger.log("Exception= " + sqle.getMessage(), Logger.WARNING);
			throw new FatalException(sqle.getMessage());
		}
	}
	
	
	/**
	 * Drop all _U tables after checking whether that data source was processed in current run or not. It will
	 * also drop postwork and caCore _U tables by checking whether their respective flags are true or not. This
	 * function is called to cleanup database from _U tables of previous incomplete run. It will only delete
	 * the _U tables corresponsing to which tables are to be created in this run. This makes sure that when we 
	 * create new _U tables for this run then it will not give error 
	 */	
	public void drop_UTables()
	{
		Logger.log("drop _U tables called ",Logger.DEBUG);
		/** In update mode based on what data sources will be modified the corresponding _U tables if
		 * exist then they will be deleted */
		if(Variables.updateMode)
		{
			/** Drop _U tables for Unigene data source if they exist. Each time when we call this drop method
			 * the error count is not incremented if drop _U fails. This is becaus there may not be any _U 
			 * tables in the schema if last run was successful and complete renameing of _U tables occured*/
			if(Variables.ugupdate)
			{
				for (int i = 0; i < Constants.ugTables.length; i++) 
				{
					executeUpdateSupressError("DROP TABLE " + Constants.ugTables[i] + "_U");					
				}
			}
			if(Variables.llupdate)
			{
				for (int i = 0; i < Constants.llTables.length; i++) 
				{
					executeUpdateSupressError("DROP TABLE " + Constants.llTables[i]+ "_U");					
				}
				
			}
			if(Variables.hmlgupdate)
			{
				for (int i = 0; i < Constants.hmlgTables.length; i++) 
				{
					executeUpdateSupressError("DROP TABLE " + Constants.hmlgTables[i]+ "_U");					
				}
				
			}
			if(Variables.ustupdate)
			{
				for (int i = 0; i < Constants.unistsTables.length; i++) 
				{
					executeUpdateSupressError("DROP TABLE " + Constants.unistsTables[i]+ "_U");					
				}
				
			}
			if(Variables.dbsnpupdate)
			{
				for (int i = 0; i < Constants.dbSnpTables.length; i++) 
				{
					executeUpdateSupressError("DROP TABLE " + Constants.dbSnpTables[i]+ "_U");					
				}
			}
			for (int i = 0; i < Constants.sysCopyTables.length; i++) 
			{
				executeUpdateSupressError("DROP TABLE " + Constants.sysCopyTables[i]+ "_U");					
			}
		}
		/** If we are computing postwork in this run then based on which postwork units will be processed
		 * we delete the corresponding _U tables. each time the flag for particular postwork unit is set if
		 * it is mentioned in postwork execution configuration file. */
		if(Variables.postWork)
		{
			if(Variables.geneinfoPostWork)
			{
				for (int i = 0; i < Constants.geneinfoTables.length; i++) 
				{
					executeUpdateSupressError("DROP TABLE " + Constants.geneinfoTables[i]+ "_U");					
				}
			}
			if(Variables.geneinfo_summaryPostWork)
			{
				for (int i = 0; i < Constants.geneinfo_summaryTables.length; i++) 
				{
					executeUpdateSupressError("DROP TABLE " + Constants.geneinfo_summaryTables[i]+ "_U");					
				}
			}
			if(Variables.geneinfo_marray_summaryPostWork)
			{
				for (int i = 0; i < Constants.geneinfo_marray_summaryTables.length; i++) 
				{
					executeUpdateSupressError("DROP TABLE " + Constants.geneinfo_marray_summaryTables[i]+ "_U");					
				}
			}
			if(Variables.chipdescriptionPostWork)
			{
				for (int i = 0; i < Constants.chipdescriptionTables.length; i++) 
				{
					executeUpdateSupressError("DROP TABLE " + Constants.chipdescriptionTables[i]+ "_U");					
				}
			}
			if(Variables.chipinfo_homoloPostWork)
			{
				for (int i = 0; i < Constants.chipinfo_homoloTables.length; i++) 
				{
					executeUpdateSupressError("DROP TABLE " + Constants.chipinfo_homoloTables[i]+ "_U");					
				}
			}
			if(Variables.chipinfo_omimPostWork)
			{
				for (int i = 0; i < Constants.chipinfo_omimTables.length; i++) 
				{
					executeUpdateSupressError("DROP TABLE " + Constants.chipinfo_omimTables[i]+ "_U");					
				}
			}
			if(Variables.chipinfo_termPostWork)
			{
				for (int i = 0; i < Constants.chipinfo_termTables.length; i++) 
				{
					executeUpdateSupressError("DROP TABLE " + Constants.chipinfo_termTables[i]+ "_U");					
				}
			}
			if(Variables.homologenePostWork)
			{
				for (int i = 0; i < Constants.homolo_postworkTables.length; i++) 
				{
					executeUpdateSupressError("DROP TABLE " + Constants.homolo_postworkTables[i]+ "_U");					
				}
			}
		}
		/** 
		 * If caCore tables are processed in this run then drop the correponding _U tables
		 */
		if(Variables.caCoreSystemPostWork)
		{
			for (int i = 0; i < Constants.caCoreSystemTables.length; i++) 
			{
				executeUpdateSupressError("DROP TABLE " + Constants.caCoreSystemTables[i]+ "_U");					
			}
			if(Variables.dbIdentifier.equalsIgnoreCase(Constants.ORACLE))
			{
				for (int i = 0; i < Constants.caCoreSequences.length; i++) 
				{
					executeUpdateSupressError("DROP SEQUENCE " + Constants.caCoreSequences[i]);					
				}
			}
		}
	}
}