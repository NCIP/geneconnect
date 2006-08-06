/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java com.dataminer.server.parser.ChipInfoParser</p> 
 */

package com.dataminer.server.parser;

import java.io.FileWriter;
import java.io.IOException;

import com.dataminer.server.exception.FatalException;
import com.dataminer.server.ftp.FileInfo;
import com.dataminer.server.globals.Constants;
import com.dataminer.server.jobmanager.DPQueue;
import com.dataminer.server.log.Logger;
import com.dataminer.server.record.Record;
/**
 * Abstract base class having common methods used by chipDataParser and CaArray
 * parser.
 * @author	Meghana Chitale
 * @version 1.0
 */
abstract public class ChipInfoParser extends Parser 
{
	/** base record for chipinformation table  */
	protected Record baseRecord;
	/** record to store chip_name and organism information */
	
	/**
	 * Constructor method
	 * @param fileToParse Information of the file to parse
	 * @param filesParsed Queue holding list of parsed files
	 */
	public ChipInfoParser(FileInfo fileToParse,DPQueue filesParsed)
	{       
		super(fileToParse,filesParsed);
	}
	
	/** This method will create a file writer to write the records for a particular table.
	 * Filename will be "table name.actual source file name". This function will also add 
	 * the file writer to Hash Table present with the parser base class. The Hash table will
	 * keep list of all file writers and it will later close all of them 
	 * @param fileName
	 */
	public void createFileWriters(String fileName)
	{
		try
		{
			m_fileWriterHashTable.put(Constants.chipTableName,new FileWriter(Constants.chipTableName+"."+fileName));
		}
		catch(IOException e)
		{
			Logger.log("Error during parsing file : " + e.getMessage(), Logger.WARNING);
		}
		
	}
	
	/** This function creates a record object to store data. Record object will be initialised 
	 * according to the number of columns and precidsion for the columns*/
	public void createRecords()
	{
		
		baseRecord = new Record(m_dbManager.noOfColumns(Constants.chipTableName),
				m_dbManager.getPrecision(Constants.chipTableName));
	}

	/** This function initialises TableInfo object for chipinformation table by putting 
	 * its metadata in Hash map presnet with dbManager. After calling the init table method
	 * the table's metadata information can be used for other functions*/
	public void initTables()
	{
		try
		{
			m_dbManager.initTable(Constants.chipTableName);
		}
		catch(FatalException fe)
		{
			Logger.log("Fatal Exception in ChipInfoParser::initTables " + fe.getMessage(),Logger.FATAL);
		}
	}
	
	/** This method will clear all the fields from the record*/
	public void resetRecords()
	{
		baseRecord.resetAllFields();
	}
}
