/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>ClassName: java com.dataminer.server.parser.Parser</p> 
 */

package com.dataminer.server.parser;

import com.dataminer.server.database.DBManager;
import com.dataminer.server.record.Record;
import com.dataminer.server.exception.FatalException;
import com.dataminer.server.exception.InsertException;
import com.dataminer.server.ftp.FileInfo;
import com.dataminer.server.globals.Constants;
import com.dataminer.server.globals.Variables;
import com.dataminer.server.jobmanager.DPQueue;
import com.dataminer.server.log.Logger;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.FileReader;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 * Parser is an abstract class which declares an abstract method parse() to parse the data files and this function
 * in turn can be implemented by individual parsers according to their specific need. The class is responsible for 
 * parsing the data source files, writing the parsed records to files which in turn can be loaded into the database bye 
 * the loader.
 * @author      Anuj Tiwari
 * @version      1.0
 */

abstract public class Parser 
{
	/** input file reader */
	protected BufferedReader m_inputFileReader;
	/** field delimiter for the input records */
	protected String m_fieldDelimiter;
	/** record delimiter for the input records */
	protected String m_recordDelimiter;
	/** current line that is been parsed */
	protected String m_line;
	/** breaks the line into tokens */
	protected StringTokenizer m_tokenizer;
	/** File Info Object describing file or list of files in the same base directory to be parsed*/
	protected FileInfo m_fileToParse;
	protected String m_fileNameToParse;
	/** DBManger instance */
	protected DBManager m_dbManager;
	/** File Separator for the underlying plateform */
	protected String m_fileSep = System.getProperty("file.separator");
	/**Output Stream Object to Write Records to Database*/
	protected Hashtable m_fileWriterHashTable = new Hashtable();
	/** File Writer for writing the parsed data into file*/
	protected FileWriter m_fileWriter;
	/** Queue in which parsed files will be kept for the Database thread*/
	protected DPQueue m_filesParsed;
	
	/** This string will be used to hold the revision information read and parsed from the source file
	 * Each of the parser will make sure that if such information is there in the source file then it 
	 * will be set in this variable at the end of parsing.  
	 */
	protected String m_RevisionHistory = null;
	
	/**
	 * Constructor method
	 * @param fileToParse Information of the file to parse
	 * @param filesParsed List of parsed files
	 */
	public Parser(FileInfo fileToParse,DPQueue filesParsed)
	{       
		m_fileToParse = fileToParse;
		m_fileNameToParse=(String)fileToParse.getFiles().firstElement();
		m_filesParsed = filesParsed;
		m_dbManager = DBManager.getInstance();
	}
	
	/**
	 * calls the parsing routines
	 */
	public void run(boolean isLocalFile)  
	{
		try 
		{
			if(!m_fileToParse.IsExternalParser())
			{	
			/** opens the first file in the list of files contained by file Info object*/
			open(m_fileToParse);
			
			Logger.log("File(s) : " + m_fileToParse.getFileNames() + " opened",Logger.INFO);
			}
			/** parse and load the records specific to the parsing class for the given data source */
			parse(m_fileToParse);
			if(!m_fileToParse.IsExternalParser())
			{
			/** close the parsed file*/
			close();
			
			Logger.log("File(s) : " + m_fileToParse.getFileNames() + " closed",Logger.INFO);
			}
			/** For few source files the information related to source file build is present in the
			 * file itself which will be parsed by that particular parser and will be loaded in 
			 * m_RevisionHistory. Now if a particular file is not giving any such information then there will
			 * be no entry for it in fileRevisionHistory HashTable and hence later on when populating the
			 * server_file_status table we use the last modified date of file as revision history for the file.
			 */
			if(m_RevisionHistory != null)
			{
				Variables.fileRevisionHistory.put(m_fileToParse,m_RevisionHistory);
			}
			
			/** delete the file which has been downloaded to clean up the 
			* local directory.*/
			if(false == isLocalFile)
			{
				/** By default Variables.deleteDownloadedFiles is false so the FTPed files will not be deleted 
				 * after parsing. But if this parameter is explicitely set to true in server.properties file then 
				 * the downloaded files can be deleted after parsing */
				if(true == Variables.deleteDownloadedFiles)
				{
					deleteDownloadedFile(m_fileToParse);
					Logger.log("File(s) : " + m_fileToParse.getFileNames() + " deleted after parsing",Logger.INFO);
				}
			}
			
		}
		catch(FileNotFoundException fnfe) 
		{
			Logger.log("FileNotFoundException Exception occurred while parsing of " + m_fileToParse.getFileNames(), Logger.WARNING);
			Logger.log(fnfe.getMessage(),Logger.WARNING);
		}
		catch (IOException ioex) 
		{
			Logger.log("IOException Exception occurred while parsing of " + m_fileToParse.getFileNames(), Logger.WARNING);
			Logger.log(ioex.getMessage(),Logger.WARNING);
		}
		catch(FatalException fe) 
		{
			Logger.log("FatalException Exception occurred while parsing of " + m_fileToParse.getFileNames(), Logger.WARNING);
			Logger.log(fe.getMessage(),Logger.WARNING);
		}   	
	}
	
	public void runForCaArray()
	{
		try
		{
			Logger.log("Parser thread for Chipinformation entred",Logger.DEBUG);
			parse(m_fileToParse);
			closeFileWriters();
			Logger.log("Parser thread for Chipinformation exited",Logger.DEBUG);
		}
		catch(FatalException fe) 
		{
			Logger.log("FatalException while processing caArray file " + m_fileToParse ,Logger.WARNING);
			Logger.log(fe.getMessage(),Logger.WARNING);
		}   	
		catch(IOException e)
		{
			
			Logger.log("IOException while processing caArray file " + m_fileToParse ,Logger.WARNING);		
			Logger.log(e.getMessage(),Logger.WARNING);
		}
		
	}
	
	/**
	 * get the next record from file
	 * @return Next line string
	 * @throws IOException Throws exception if error during fetching next line from file
	 */
	protected String getNextRecord() throws IOException 
	{
		String record = m_inputFileReader.readLine();
		return record;
	}
	
	/**
	 * Opens the first file in the list of files contained by file Info object for reading 
	 * Parsers handling mulitple files should override this
	 * @param file File Information object
	 * @throws IOException Throws exception if error during opening file
	 * @throws FileNotFoundException Throws exception if error during opening file
	 */
	protected void open(FileInfo file)
	throws IOException, FileNotFoundException 
	{
	    String fileName = (String) file.getFiles().firstElement();
		/** check if its a compressed(gzip) or uncompressed file*/
		if(fileName.endsWith(".gz"))
			m_inputFileReader = new BufferedReader
			(new InputStreamReader
					(new WorkingGZIPInputStream
							(new FileInputStream(fileName))));
		else
			m_inputFileReader = new BufferedReader(new FileReader(fileName));
	}
	
	/**
	 * closes the (parsed) file
	 * @throws IOException Throws exception if error during closing file
	 */
	protected void close() throws IOException 
	{
		m_inputFileReader.close();
		/** close the fileWriters*/
		closeFileWriters();
	}
	
	/**
	 * closes the (parsed) files
	 * @throws IOException Throws exception if error during closing file
	 */
	protected void closeFileWriters() throws IOException 
	{
		String tableName;
		FileWriter fwriter = null;
		Enumeration fileWriterEnumurator = m_fileWriterHashTable.keys();
		
		while(fileWriterEnumurator.hasMoreElements())
		{
			tableName = (String)fileWriterEnumurator.nextElement();
			m_filesParsed.add(tableName + "." + m_fileNameToParse);
			fwriter = (FileWriter)m_fileWriterHashTable.get(tableName);
			if(fwriter != null)
			{
				fwriter.close();
			}
		}
	}
	
	/**
	 * Overriden in derived classes (UnigeneParser, unists etc.)
	 * where the actual parsing of records is peformed.
	 * @param file Information of the file to parse
	 * @throws FatalException Throws exception if error during parsing file
	 */
	protected abstract void parse(FileInfo file) throws FatalException;
	
	/**
	 * returns the next token (if available)
	 * @return Next token string
	 */
	protected String getNextToken() 
	{
		String token = null;
		if (m_tokenizer.hasMoreTokens()) 
		{
			token =  m_tokenizer.nextToken().trim();
		}
		return token;
	}
	
	/**
	 * another parse value routine that breaks the "line" into name/value pair
	 * @param name Tagname of what value is to be separated in the line
	 * @return Value associated with given name tag
	 */
	public String parseValue(String name) 
	{
		int index = m_line.indexOf(name);
		String value = null;
		if (index != -1)
		{
			value = (m_line.substring(index + name.length())).trim();
		}
		return value;
	}
	
	/**
	 * break "name" into two parts based on delimiter, returns the 2nd part 
	 * @param name Key for which to get value
	 * @param delim Delimiter for getting value
	 * @return Value associated with given name
	 */
	public String parseValue(String name, String delim) 
	{
		int index = name.indexOf(delim);
		String value = null;
		if (index != -1) 
		{
			value = (name.substring(index + delim.length())).trim();
		}
		return value;
	}
	
	/**
	 * writes the record to database
	 * @param tableName Name of the table
	 * @param rec Record to write to file
	 * @throws FatalException Throws exception if error while writing record to file
	 * @throws InsertException Throws exception if error while writing record to file
	 */
	protected void writeRecordToDb(String tableName, Record rec)
	throws FatalException,InsertException 
	{
		/** Write the record to its corresponding file */
		try 
		{
			if(m_dbManager.checkNullability(rec,tableName))
			{
				m_fileWriter = (FileWriter)m_fileWriterHashTable.get(tableName);
				m_fileWriter.write(rec.toString()+"\n");
			}
		} 
		catch(IOException ioEx)
		{
			Logger.log("Error in writing to file (Parse):"+ioEx.getMessage(), Logger.WARNING);
		}
	}
	/**
	 * Write meta data at the begining of file for data loaders.The meta data contains
	 * information about columns and their data types and sequence in which they are
	 * to be read from the data file.
	 * @throws FatalException Throws exception if error while writing meta data to file
	 */
	protected void writeMETADATA() throws FatalException
	{
		Enumeration tables = m_fileWriterHashTable.keys();
		String tableName = null;
		String fieldNames[][] = null;
		while(tables.hasMoreElements())
		{
			
			/** Take the Table Name From Hash Table*/
			tableName = (String)tables.nextElement();
			Logger.log("file writer table name : "+ tableName,Logger.INFO);             
			/** Get All the Field Names For This Table*/
			fieldNames = m_dbManager.getFieldNamesANDType(tableName);
			
			/** Get the FileWirter Object for this table from HashTable*/
			m_fileWriter = (FileWriter)m_fileWriterHashTable.get(tableName);
			
			try
			{
				/** for all character fields if their size is greater than 255 then due to default buffer
				 * size of 255 the loading fails.hence we need to read it using char(5000). But same if 
				 * used for reading number fields then errors are logged and loading fails.Hence below
				 * we check for field type and set buffer based on that.*/
				
				if(Variables.dbIdentifier.equals("Oracle"))
				{
					m_fileWriter.write("LOAD DATA INFILE * APPEND INTO TABLE " + tableName +" FIELDS TERMINATED BY \"" + Constants.columnSeparator + "\" (");
					for(int i = 0 ; i < fieldNames.length -1 ; i++)
					{
						if(fieldNames[i][1].equalsIgnoreCase("CHAR") || fieldNames[i][1].equalsIgnoreCase("VARCHAR2"))
						{
							m_fileWriter.write(fieldNames[i][0] + " char(5000) NULLIF " +  fieldNames[i][0] + "='-'" + ",");
						}
						else
						{
							m_fileWriter.write(fieldNames[i][0] + " NULLIF " +  fieldNames[i][0] + "='-'" + ",");            				 
						}
					}
					if(fieldNames[fieldNames.length-1][1].equalsIgnoreCase("CHAR") || fieldNames[fieldNames.length-1][1].equalsIgnoreCase("VARCHAR2"))
					{
						m_fileWriter.write(fieldNames[fieldNames.length-1][0] + " char(5000) NULLIF " + fieldNames[fieldNames.length-1][0] + "='-'" + "\n");
						
					}
					else
					{
						m_fileWriter.write(fieldNames[fieldNames.length-1][0] + " char(5000) NULLIF " + fieldNames[fieldNames.length-1][0] + "='-'" + "\n");
						
					}
					m_fileWriter.write(")\n BEGINDATA\n");
				}
				else if (Variables.dbIdentifier.equals("Mysql"))
				{
					/** In case of mysql loader file the control parameters are
					 * provided to exec call and not present in the data file.*/
				}
			}
			catch(IOException ioExp)
			{
				throw new FatalException("IOException during file write operation for table " + tableName + ioExp);
			}
		}
	}
	/**
	 * Method to delete dowmloaded files
	 * @param file File Info Object which contains list of Name of the file to delete.
	 */
	protected void deleteDownloadedFile(FileInfo file)
	{
		File f = null;
		String gzFileName = "";
		for (int i = 0; i < file.getFiles().size() ; i++)
		{
		    gzFileName = (String) file.getFiles().firstElement();
		    deleteDownloadedFile(gzFileName);
		}
	}
	
	/**
	 * Method to delete dowmloaded file
	 * @param gzFileName Name of the file to delete
	 */
	protected void deleteDownloadedFile(String gzFileName)
	{
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
	
	/** 
	 * This function returns the revision date of the file which has been selected 
	 * through the command file. The file based on whether it is ftp ed or local or 
	 * http will be present in any of the maps for those files. This function will
	 * check all those maps and return the revision date for the file.
	 * @param fileName Name of the file whose revision date is to be fetched
	 * @return Revision date for the file selected. The file will be in one of the
	 * maps based on whether it is FTPed, HTTPed or LOCAL file.
	 */
	protected String getFileRevisionHistory(String fileName)
	{
		String revInfo = new String();
		if(Variables.ftpFiles.get(fileName) != null)
		{
			revInfo = (String)Variables.ftpFiles.get(fileName);
		}
		else if(Variables.httpFiles.get(fileName) != null)
		{
			revInfo = (String)Variables.httpFiles.get(fileName);
		}
		else if (Variables.localFiles.get(fileName) != null)
		{
			revInfo = (String)Variables.localFiles.get(fileName);
		}
		return revInfo;
	}
}