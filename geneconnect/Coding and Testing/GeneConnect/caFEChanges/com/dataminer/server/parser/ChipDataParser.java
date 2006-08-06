/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java com.dataminer.server.parser.ChipDataParser</p> 
 */

package com.dataminer.server.parser;

import java.io.IOException;
import java.util.Hashtable;
import java.util.StringTokenizer;

import com.dataminer.server.exception.FatalException;
import com.dataminer.server.exception.InsertException;
import com.dataminer.server.ftp.FileInfo;
import com.dataminer.server.globals.Constants;
import com.dataminer.server.globals.Variables;
import com.dataminer.server.jobmanager.DPQueue;
import com.dataminer.server.log.Logger;

/**
 * Text parser for the chip data files. Each chipdata file will have information 
 * about a particular chiptype in following format (tab delimited fields)
 * chipname 	species/organism     //this is file header followed by records
 * Probeset_Id	Accession_Number Description
 * Above file format is "A" which will be specified in the command file record
 * for the loacal file having the chip information. The other formats which 
 * can be there are combinations with A,U,E where A=Accession_Number,U=Unigene_ID
 * E=EntrezGene_ID. The last field of file will be description field
 * @author	Anuj Tiwari
 * @version 1.0
 */
public class ChipDataParser extends ChipInfoParser
{
	
	/** CHIPINFORMATION table can take CHIPINFO DATA with any ONE of
	 * UNIGENE, LOCUSID-ORGANISM OR ACC_NO fields present.*/
	/** format defines which of the field is present in the input */
	private String formatString = "A"; /** default field is ACC_NO*/
	private int max=2048;
	
	/** builds a field id table for CHIPINFORMATION */
	private Hashtable fieldIdTable;
	
	private int [] fieldIndexArray;
	

	/**
	 * Constructor method
	 * @param fileToParse Information of the file to parse
	 * @param filesParsed Queue holding list of parsed files
	 */
	public ChipDataParser(FileInfo fileToParse,DPQueue filesParsed)
	{       
		super(fileToParse,filesParsed);
	}
	
	
	/**
	 * Sets the input data format. CHIPINFORMATION table can import
	 * data with any ONE of UNIGENE, LOCUSID-ORGANISM OR ACC_NO fields present.
	 */
	public void setFormat(String value) 
	{
		formatString = value;
	}
	
	/**
	 * Method to get column names for a file
	 * @return Array of columns present in file to parse
	 */
	private String[] getInputColNames() 
	{
		if (null == formatString)
		{
			return null;
		}
		int length = formatString.length();
		String [] colNames = new String[length];
		char tmp;
		for (int i=0; i<length; i++)
		{
			/** Read the format identifier from the command file and set the corresponding 
			 * column name in colNames  	 */
			tmp = formatString.charAt(i);
			switch (tmp)
			{
			case 'A': case 'a':
				colNames[i] = "CIN_ACC_NO";
				break;
			case 'U': case 'u':
				colNames[i] = "CIN_UGID";
				break;
			case 'O': case 'o':
				colNames[i] = "CIN_LOCAL_TAXID";
				break;
			case 'E': case 'e':
				colNames[i] = "CIN_GENEID";
				break;
			default:
				Logger.log("Character " + tmp + " not a valid format character",Logger.WARNING);
			}
		}
		return colNames;
	}
	
	/** 
	 * builds the field id table for CHIPINFORMATION 
	 * @exception FatalException Throws exception if error while building fields table
	 */
	private void buildFieldTable() throws FatalException
	{
		String [] colNames = m_dbManager.getFieldNames(Constants.chipTableName);
		fieldIdTable = new Hashtable(colNames.length);
		for (int i=0; i< colNames.length; i++)
		{
			Integer id = new Integer(i);
			fieldIdTable.put(colNames[i].toUpperCase(), id);
		}
		Logger.log("BuildFieldTable complete ",Logger.DEBUG);
	}
	
	/**
	 * populate the fieldIndexArray from the format. fieldIndexArray is an interger 
	 * array member which will have ID
	 */
	private void populateFieldArray() 
	{
		Integer fieldId;
		/** get input column names which is populated based on the input file
		 * format specified in the command file */
		String [] inputColNames = getInputColNames();
		int arrLength; /** Number of columns in the input file*/
		if (null == inputColNames)
		{
			/** set array length 2 for two default columns (probeset and chip description)*/
			arrLength = 2;   
		}
		else
		{
			/** Basic Probeset ID and description fields are present for all files 
			 * but the other supplementary information like accession number, unigene and
			 * entrezgene ids may be present one or many based on the file format */
			arrLength = 2 + inputColNames.length; /**  add other columns*/
		}
		/** Field array base elements are Probeset and Chip_Desc plus any of acc_no, ugid, & locusid/organism */
		fieldIndexArray = new int [arrLength];
		
		/**  Now 0th field in input file is probeset*/
		fieldIndexArray[0] = ((Integer) fieldIdTable.get("CIN_PROBESET")).intValue();
		
		if (inputColNames != null) 
		{
			/** get the field ids for all column names*/
			for (int i=0; i< inputColNames.length; i++)
			{
				/** Pick up the column from the list of inout columns as obtained based on
				 * the input file format. Then fetch its ID from the FieldIdTable */
				fieldId = (Integer) fieldIdTable.get(inputColNames[i].toUpperCase());
				if (null == fieldId)
				{
					/** no such column name allowed*/
					Logger.log("Column name " + inputColNames[i] + " is not a valid name",Logger.WARNING);
				}
				else
				{
					/** add the field id to the index array. Thus fieldIndexArray will have all the 
					 * column names depending on the file format*/
					fieldIndexArray[1+i] = fieldId.intValue();
				}
			}
		}
		/** ChipDesc field  is the last column in the input file. First we have added the default first field
		 * as probeset id , then all the middle fields based on the file format and now the last default
		 * field which is chipdescription */
		fieldIndexArray[fieldIndexArray.length-1] = ((Integer) fieldIdTable.get("CIN_CHIP_DESCRIPTION")).intValue();
		Logger.log("populateFieldArray complete ",Logger.DEBUG);
	}
	
	/**
	 * parses the chip data.
	 * @param file Information of the file to be parsed
	 * @return FatalException paring or db insert error
	 */
	public void parse(FileInfo file) throws FatalException 
	{
	    /** ChipDataParser parser deals with single file only. So just pick up 
	     * the first file name from the list of files*/
	    String fileName = (String) file.getFiles().firstElement();
	    
	    Logger.log(" Chip_data::parsing started ",Logger.INFO);
		
		initTables();
		Logger.log("init table over",Logger.INFO);
		
		createRecords();
		Logger.log("create record over ",Logger.INFO);
		
		createFileWriters(fileName);
		Logger.log("create file writers over ",Logger.INFO);
		
		writeMETADATA();
		Logger.log("Write metadata over ",Logger.INFO);
		
		/** This function will build the HashMap FieldIDTable containg the column names from 
		 * chipinformation table and will store them by assigning an ID for each */
		buildFieldTable();
		
		/** This function will set the fieldIndexArray based on the file format. The probeset id
		 * will be default first field and description will be default last field*/ 
		populateFieldArray();
		
		/** Get the chipname from the first line.*/
		String chipname = "";
		String organism = "";
		try 
		{
			/** Get the first line of the chip library file. This should be the chip name and species entry.*/
			m_line = getNextRecord();
			if (m_line != null)
			{
				StringTokenizer sTok = new StringTokenizer(m_line,"\t");
				/** Check whether the correct data is found on the first line */
				if(sTok.countTokens() >= 2)
				{
					chipname = sTok.nextToken();
				}
				if(sTok.hasMoreTokens())
				{
					organism = sTok.nextToken();
				}
				Logger.log("Read chipname " + chipname + " from file",Logger.INFO);
				Logger.log("Read organism " + organism + " from file",Logger.INFO);
			}
		}
		catch (IOException ioex)
		{
			Logger.log(ioex.getMessage(),Logger.DEBUG);
			throw new FatalException(ioex.getMessage());
		}
		
		/** This variable stores chiptypeid for the current chip*/
		int chipTypeID = -1;     
		
		/** Parse the Chipinformation data*/
		try 
		{
			while ( (m_line = getNextRecord()) != null)
			{
				/** break the line into record fields. It will populate baseRecord which will have the
				 * values as parsed from the current line*/
				boolean isSuccess = parseLine();
				if (true == isSuccess)
				{
					/** Get chiptypeID corresponding to the chipname from chiptypes table
					* chipTypeId is -1 for the first line (initially). So this
					* query is done only for the firstline in the file.*/
					if(-1 == chipTypeID)
					{
						/** lookup the chiptypeid in the chiptypes table.*/
						chipTypeID = m_dbManager.getChipTypeID(chipname.toUpperCase());
						/** if no entry for chipname is found 0 is returned then that chip's entry is added
						 * to the chiptypes table with next chiptype id and the information from the hesder in 
						 * the chip library file  */
						if(0 == chipTypeID)
						{
							Logger.log("Chip not found in ChipTypes table :" + chipname,Logger.WARNING);
							/** Here the information from current chip library file's first line will 
							 * be used to add record in chiptypes table.*/
							chipTypeID = m_dbManager.addChipName(chipname,organism);
						}
						else
						{
							Logger.log("ChiptypeID found " + chipTypeID,Logger.INFO);
							/** check how many records with the chiptypeid exist in the chipinformation table.*/
							int count = m_dbManager.getChipTypeIDCount(chipTypeID);
							/** If count is > 0 then the chip already exists then the processing will not
							 * be continued. */
							if(count>0)
							{
								Logger.log("Chip already exists: "+	chipname.toUpperCase(),	Logger.WARNING);
								return;
							}
						}
					}
					/** replace the chipname in baserecord by chiptypeid for current chipname as in chiptypes table */
					baseRecord.fields[0].setLength(0);
					baseRecord.fields[0].append(chipTypeID);
					
					/**write the Chip record to database*/
					try
					{
						/** if organism is being read then convert it to local taxonomy id.*/
						String orgName = ""; 
						if(!baseRecord.fields[4].toString().equals(""))
						{
							orgName = (String)Variables.hmOrganismLocalId.get(baseRecord.fields[4].toString());
							baseRecord.fields[4].setLength(0);
						}
						baseRecord.fields[4].append(orgName);
						writeRecordToDb(Constants.chipTableName, baseRecord);
					}
					catch(InsertException ie)
					{
						Logger.log("Invalid record found in the Chip library " + chipname + ". Rolling back chip.",Logger.WARNING);
						m_dbManager.rollbackChip(chipTypeID);
						break;
					}
					baseRecord.resetAllFields();
				}
				else
				{
					/** parseLine failed so rollback chip*/
					Logger.log("Rolling back chip due to parsing problems:" + chipname,Logger.WARNING);
					m_dbManager.rollbackChip(chipTypeID);
					break;
				}
			}
		}
		catch (IOException ioex) 
		{
			Logger.log(ioex.getMessage(),Logger.DEBUG);
			throw new FatalException(ioex.getMessage());
		}
		Logger.log(" ChiData parsing over ",Logger.INFO);

	}
	
	/**
	 * parse the input line and create the output record
	 * @return It returns whether parsing the current line of chip data file was successful or not
	 */
	private boolean parseLine()
	{
		String delim = "\t";
		m_tokenizer = new StringTokenizer(m_line, delim, true);
		String token;
		
		int tokCount = m_tokenizer.countTokens();
		if ((fieldIndexArray.length <= tokCount) &&	(tokCount <= fieldIndexArray.length*2))
		{
			int lastDelim = -2; /** case when line has consecutive (>=2) tabs!*/
			int columnIndex = 0;
			/** Tokenise the chip data file's line to separate the data to be put in various columns*/
			for (int i=0; i< tokCount; i++) 
			{
				token = m_tokenizer.nextToken();
				if (token.equalsIgnoreCase(delim)) 
				{
					if (lastDelim + 1 == i) 
					{
						/** 2 consecutive tags - empty string value*/
						if(columnIndex<fieldIndexArray.length)
							baseRecord.fields[fieldIndexArray[columnIndex++]].append("");
						else
							return false;
					}
					lastDelim = i;
				} 
				else 
				{
					/** Each time when new token rrpresenting column data is encountered then columnIndex is 
					 * incremented and tokens are stored till the token number is less than total number
					 * of tokens on one line according to fieldIndexArray */
					if(columnIndex < fieldIndexArray.length)
					{
						/** If total description length exceeds the precision of that field in the table then 
						 * it will be truncated to load it in the description field*/
						if(columnIndex == (fieldIndexArray.length-1))
						{
							if (token.length() > max)
							{
								StringBuffer tokenbuffer = new StringBuffer(token);
								tokenbuffer.setLength(max);
								if(tokenbuffer.charAt(max-1) != '"')
									tokenbuffer.append('"');
								token=tokenbuffer.toString();
							}
						}
						/** Here fieldIndexArray will give the actual location of particular token
						 * in the chipinformation table. This mapping is already done in fieldIndexArray */
						baseRecord.fields[fieldIndexArray[columnIndex++]].append(token);
						
					}
					else
						return false;
				}
			}
			return true;
		} 
		else
		{
			Logger.log("Invalid record: "+ tokCount + " fields. ",
					Logger.WARNING);
			Logger.log("Expected tokens =  "+ fieldIndexArray.length,
					Logger.WARNING);
			return false;
		}
	}
}
