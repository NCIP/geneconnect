/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java com.dataminer.server.parser.UniSTSParser</p> 
 */

package com.dataminer.server.parser;


import com.dataminer.server.exception.FatalException;
import com.dataminer.server.exception.InsertException;
import com.dataminer.server.ftp.FileInfo;
import com.dataminer.server.globals.Constants;
import com.dataminer.server.globals.Variables;
import com.dataminer.server.record.Record;
import com.dataminer.server.jobmanager.DPQueue;
import com.dataminer.server.log.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;
/**
 * Class to parse UniSTS data source and populate corresponding 
 * tables in database
 * @author Meghana Chitale
 * @version 1.0
 */

public class UniStsParser extends Parser
{
	/** records for each UniSts tables  */
	private Record uniStsRecord;
	private Record uniStsAliasRecord;
	private Record uniStsAccNoRecord;
	private String semiColonSeperator = ";";
	
	/**
	 * Constructor method
	 * @param fileTOParse Information of file to parse
	 * @param filesParsed The queue maintaining list of parsed files
	 */
	public UniStsParser(FileInfo fileTOParse, DPQueue filesParsed )
	{
		super(fileTOParse,filesParsed);
		m_fieldDelimiter = "\t";
	}
	
	/**
	 * Initialize the UniSts base and dimension tables. The tables
	 * should be initialized before inserting data into them.
	 * @param alias boolean to show whether file is uniSTS.alias
	 */
	private void initializeTables(boolean noAlias) throws FatalException
	{
		if(true==noAlias)
		{
			m_dbManager.initTable(Variables.uniStsBaseTableName);
			m_dbManager.initTable(Variables.uniStsAccessionTableName);
		}
		else
		{
			m_dbManager.initTable(Variables.uniStsAliasTableName);
		}
	}
	/**
	 * Method to initialize file writers for uniSTS data source
	 * @param alias boolean to show whether file is uniSTS.alias
	 * @throws FatalException Throws exception if unable to initialise file
	 * writers 
	 */
	private void createFileWriters(boolean noAlias) throws FatalException
	{
		try
		{
			/** If UniSTS.sts file is input then unists base table and unists_accession
			 * tables are populated so their file writers are being initialised	 */
			if(true==noAlias)
			{
				m_fileWriterHashTable.put(Variables.uniStsBaseTableName,new FileWriter(Variables.uniStsBaseTableName+"."+m_fileToParse));
				m_fileWriterHashTable.put(Variables.uniStsAccessionTableName,new FileWriter(Variables.uniStsAccessionTableName+"."+m_fileToParse));
			}
			else
			{
				/** In case of UniSTS.alias file initialise corresponsing table */
				m_fileWriterHashTable.put(Variables.uniStsAliasTableName,new FileWriter(Variables.uniStsAliasTableName+"."+m_fileToParse));
			}
		}
		catch(IOException ioEx)
		{
			Logger.log("Uniable to initialize file writers (UniSTS parser): " + ioEx.getLocalizedMessage(),Logger.INFO);
		}
	}
	
	/**
	 * create records for uniSTS data source. This method should be called only after
	 * the tables have been initialized.
	 * @param alias boolean to show whether file is uniSTS.alias
	 */
	private void createRecords(boolean noAlias) 
	{
		/** If UniSTS.sts file is input then unists base table and unists_accession
		 * tables are populated so their corresponding records need to be initialised */
		if(true == noAlias)
		{
			uniStsRecord = new Record(m_dbManager.noOfColumns(Variables.uniStsBaseTableName),
					m_dbManager.getPrecision(Variables.uniStsBaseTableName));
			uniStsAccNoRecord = new Record(m_dbManager.noOfColumns(Variables.uniStsAccessionTableName),
					m_dbManager.getPrecision(Variables.uniStsAccessionTableName));
		}
		else
		{
			/** In case of UniSTS.alias only record corresponding to unists_alias table needs to be initialised */
			uniStsAliasRecord = new Record(m_dbManager.noOfColumns(Variables.uniStsAliasTableName),
					m_dbManager.getPrecision(Variables.uniStsAliasTableName));
			
		}
	}
	
	/**
	 * reset the various unigene records
	 * @param alias boolean to show whether file is uniSTS.alias
	 */
	private void resetRecords(boolean noAlias) 
	{
		if(true==noAlias)
		{
			uniStsRecord.resetAllFields();
			uniStsAccNoRecord.resetAllFields();
		}
		else
		{
			uniStsAliasRecord.resetAllFields();
		}
	}
	/**
	 * Prase downloaded uniSTS data source file
	 * @param file Name of the file to parse
	 * @throws FatalException Throws exception if error occurs during parsing
	 */
	public void parse(FileInfo file) throws FatalException
	{
	    /** UniStsParser parser deals with single file only. So just pick up the first file name from the list of files*/
	    String fileName = (String) file.getFiles().firstElement();
	    
		System.out.println("UNISTS parsing started");
		Logger.log(" UniSts::parsing started "+fileName,Logger.INFO);
		boolean noAlias = false;
		/** Based on the name of source file to parse detect whether the aliase or base unists 
		 * file is getting parsed. This can be detected by checking whether the filename has "alias"
		 * keyword in its name. It indicates that the file name is having information about unists aliases 
		 * and hence will populate unists_alias table and not the other uniSTS base tables. Below
		 * function returns true or false based on whether the file is for UniSTS Alias or not.	 */
		noAlias = findFormat(fileName);
		
		/** call initialise table method to set the metadata information about each of the
		 * used table. the information will be present with DBManager */
		initializeTables(noAlias);
		Logger.log("initilalise tables over",Logger.INFO);
		
		/** Initialise file writers for each of the file which will correspond to each of the table
		 * being populated. The file writers will be stored in m_fileWriterHashTable present with the
		 * base class parser. Later on when writeRecordToDb method is called file writer for the required
		 * table is obtained from the HashTable and record is written there */
		createFileWriters(noAlias);
		Logger.log("Create file writers over",Logger.INFO);
		
		/** Create required record objects based on what(UniSTS/UniSTS alias) file is getting parsed.
		 *  These records will later hold the field information which is to be written to file for
		 *  loading into the database	 */
		createRecords(noAlias);
		Logger.log("create records over",Logger.INFO);
		
		/** This function will be called to write metadata information in each of the file where records
		 * will be written later. This file will be input to sqlloader and mysqlimport. sqlloader requires 
		 * the table meat information to be present in the data file. This is done by the below function 
		 * before we start writing records to the file*/ 
		writeMETADATA();
		Logger.log("Write metadata over",Logger.INFO);
		try 
		{
			while ( (m_line = getNextRecord()) != null)
			{
				if(false==m_line.startsWith("#"))
				{
					parseLine(noAlias);
					try
					{
						if(true == noAlias)
						{
							writeRecordToDb(Variables.uniStsBaseTableName, uniStsRecord);
							
							String strAccNos = uniStsAccNoRecord.fields[2].toString();
							StringTokenizer sTok = new StringTokenizer(strAccNos,";");
							while(sTok.hasMoreTokens())
							{
								String strAccNo = sTok.nextToken();
								uniStsAccNoRecord.fields[2].setLength(0);
								uniStsAccNoRecord.fields[2].append(strAccNo);
								writeRecordToDb(Variables.uniStsAccessionTableName,uniStsAccNoRecord);
							}
							
						}
					}
					catch(InsertException ie)
					{
						Logger.log("Exception in UniSTS parser :" + ie.getMessage(),Logger.DEBUG);
					}
					/** current record is completed, reset various table records*/
					resetRecords(noAlias);
				}
			}
		}
		catch (IOException ioex) 
		{
			/**io exception occured - remaining records will be skipped ? ?*/
			Logger.log("Exception in UniSTS parser :" + ioex.getMessage(),Logger.DEBUG);
			throw new FatalException(ioex.getMessage());
		} 
		finally 
		{
			Logger.log(" UNISTS::parsing over. ",Logger.INFO);
		}
		/** set the variable representing the unists last modified value in
		 * the revision_history table	 */
		Variables.UniSTSRevisionHistory = getFileRevisionHistory(fileName); 
	}
	
	
	/**
	 * Parsing of the Unigene record for 1st,5th,7th and 8th token.
	 * The first field indicates STS id.
	 * The fifth (NAME) coresponds to STSNAME.
	 * The seventh(ACCESSION NUMBER) & eighth(ORGANISM).
	 * For the UniStsAlias Record we use 1st and 2nd token
	 * The first field indicates STS id.
	 * The first field indicates Aliases.
	 * @param alias boolean to indicate if file being parsed in unSTS.alias
	 */
	private void parseLine(boolean noAlias) throws FatalException
	{
		m_tokenizer = new StringTokenizer(m_line, m_fieldDelimiter);
		if(true==noAlias)
		{	/**noAlias = true ---> filename = UNISTS.STS*/
			if (m_tokenizer.countTokens() >= Constants.uniStsTableCols)
			{
				String uniStsElement = null;
				int count = 0;
				while (m_tokenizer.hasMoreTokens())
				{
					uniStsElement = m_tokenizer.nextToken();
					uniStsElement = uniStsElement.trim();
					count++; //ith term
					if (count == 1)   
					{
						uniStsRecord.fields[0].append(uniStsElement);
						uniStsAccNoRecord.fields[0].append(uniStsElement);
					}
					else if(count == 5)
					{
						uniStsRecord.fields[1].append(uniStsElement);
					}
					else if(count == 7)
					{
						uniStsAccNoRecord.fields[2].append(uniStsElement);
					}
					else if(count == 8)
					{
						String localTaxid = (String)Variables.hmOrganismLocalId.get(uniStsElement);
						uniStsRecord.fields[2].append(localTaxid);
						uniStsAccNoRecord.fields[1].append(localTaxid);
					}
				}
			}
		}
		else
		{
			/**noAlias = false ---> filename = UNISTSalias file*/
			if (m_tokenizer.countTokens() >= Constants.uniStsAliasTableCols)
			{
				String uniStsAliasElement = null;
				int count = 0;
				/**here we have to add the records with same locus id,different alias.*/
				while (m_tokenizer.hasMoreTokens())
				{
					uniStsAliasElement = m_tokenizer.nextToken();
					uniStsAliasElement = uniStsAliasElement.trim();
					count++; //ith term
					if (count == 1)
						uniStsAliasRecord.fields[0].append(uniStsAliasElement);
					else if(count==2)
					{
						StringTokenizer token = new StringTokenizer(uniStsAliasElement,semiColonSeperator);
						int counttoken=token.countTokens();
						if(counttoken!=0)
						{
							StringBuffer id =  new StringBuffer(uniStsAliasRecord.fields[0].toString());
							uniStsAliasRecord.resetAllFields();
							while(token.hasMoreElements())
							{
								uniStsAliasRecord.fields[0].append(id.toString());
								uniStsAliasRecord.fields[1].append(token.nextElement());
								try
								{
									writeRecordToDb(Variables.uniStsAliasTableName, uniStsAliasRecord);
								}
								catch(InsertException ie)
								{
									Logger.log("Exception in UniSTS parser :" + ie.getMessage(),Logger.DEBUG);
								}
								uniStsAliasRecord.resetAllFields();
							}
						}
						else
						{
							uniStsAliasRecord.fields[1].append(uniStsAliasElement);
							try
							{
								writeRecordToDb(Variables.uniStsAliasTableName, uniStsAliasRecord);
							}
							catch(InsertException ie)
							{
								Logger.log("Exception in UniSTS parser :" + ie.getMessage(),Logger.DEBUG);
							}
						}
						resetRecords(false);
					}
				}
			}
		}
	}
	/**
	 * This Function returns true if file is UNISTS false for UNISTSAlias.
	 * @param fileName FileName for which to check if it is UniSTS alias file
	 * @return b
	 */
	private boolean findFormat(String fileName)
	{
		if(-1 == fileName.lastIndexOf("alias"))
		{
			Logger.log(" alias = false ",Logger.INFO);
			return true;
		}
		else
		{
			Logger.log(" alias = true",Logger.INFO);
			return false;
		}
	}
}