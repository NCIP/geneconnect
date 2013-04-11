/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>ClassName: java com.dataminer.server.parser.EntrezParser</p> 
 */


package com.dataminer.server.parser;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.StringTokenizer;

import com.dataminer.server.exception.FatalException;
import com.dataminer.server.exception.InsertException;
import com.dataminer.server.ftp.FileInfo;
import com.dataminer.server.globals.Variables;
import com.dataminer.server.jobmanager.DPQueue;
import com.dataminer.server.log.Logger;
import com.dataminer.server.record.Record;
/**
 * Initialises the required tables and creates the required output files.
 * @author   Anuj Tiwari
 * @version  1.0
 */
public class EntrezParser extends Parser 
{
	/** File to be parsed */
	protected String m_fileName = null;
    /** hashtable to save chromosome map ids */
    protected static Hashtable m_mapHTable = new Hashtable();
    /** map_term id prefix */
    protected String m_maptermIDPrefix = "MAP_";
    /** Id for Map terms */
    protected static long m_maptermID = 1;
    /** records for ENTREZGENE tables */;
  //  protected Record m_baseRecord;
    /** record for ENTREZ_UniGene table */ 
    protected Record m_ugRecord;
    /** Record for ENTREZ_OMIM table */
//    protected Record m_omimRecord;
//    /** Record for ENTREZ_PMID table */
//    protected Record m_pmidRecord;
//    /** Record for ENTREZ_GOID table */
//    protected Record m_goRecord;
//    /** Record for ENTREZ_PHENOTYPE table */
//    protected Record m_phenotypeRecord;
//    /** Record for ENTREZ_GENENAMES table */
//    protected Record m_genenamesRecord;
//    /** Record for ENTREZ_MAP table */
//    protected Record m_mapRecord;
//    /** Record for SYSTEM_TERMDATA table */
//    protected Record m_termRecord;
//    /** Record for SYSTEM_TERMTREE table */
//    protected Record m_treeRecord;
//    /** MAP Record for SYSTEM_TERMDATA table */
//    protected Record m_maptermRecord;
//    /** MAP Record for SYSTEM_TERMTREE table */
//    protected Record m_maptreeRecord;
//    /** Record for ENTREZ_STS table */
//    protected Record m_stsRecord;
//    /** Record for ENTREZ_FLY table */
//    protected Record m_dmRecord;
//    /** Record for GENE_HISTORY table */
//    protected Record m_geneHistoryRecord;
    

	/**
	 * Constructor method
	 * @param fileName Name of the file to parse
	 * @param filesParsed List of parsed files
	 * @param history Boolean to indicate if file is entrez history file
	 */
	public EntrezParser(FileInfo fileName, DPQueue filesParsed, boolean history)
	{
		super(fileName, filesParsed);
		
		Logger.log("Initialised EntrezParser Constructor with history:" + history, Logger.DEBUG);
		m_fileName = (String)fileName.getFiles().firstElement();
		try
		{
			Variables.llUgTableName=Variables.llUgTableName+"_U";
			Logger.log("tabkle:" + Variables.llUgTableName, Logger.DEBUG);
			this.initializeTables(history);
			Logger.log("Initialise Tables over",Logger.INFO);
			this.createRecords(history);
			Logger.log("Create records over",Logger.INFO);
			this.createFileWriters(history);
			Logger.log("Create file writers over",Logger.INFO);
			writeMETADATA();
			Logger.log("Writer metadata over",Logger.INFO);
			this.resetRecords(history);
			Logger.log("debug 22",Logger.INFO);
		}
		catch (FatalException excp)
		{
			Logger.log("FatalException has occured: " + excp.toString(), Logger.INFO);
		}
		
	}
	/**
	 * @param fileName Name of the file to parse
	 * @exception FatalException Throws exception if error during parsing
	 */
	protected void parse(FileInfo file) throws FatalException 
	{

	}
	
	/**
	 * Initialise tables for entrez parser
	 * @param history Boolean to indicate if file is entrez history file
	 * @throws FatalException Throws exception if error during parsing
	 */
    private void initializeTables(boolean history) throws FatalException
    {
    	/**
         * Initialize the Locuslink base and dimension tables. The tables
         * should be initialized before inserting data into them.
         */
//        if (history == false)
//        {
//        	m_dbManager.initTable(Variables.locusBaseTableName);
//        	m_dbManager.initTable(Variables.llGoidTableName);
//        	m_dbManager.initTable(Variables.llOmimTableName);
//        	m_dbManager.initTable(Variables.llPhenotypeTableName);
//        	m_dbManager.initTable(Variables.llPmidTableName);
        	m_dbManager.initTable(Variables.llUgTableName);
//        	m_dbManager.initTable(Variables.llGeneNamesTableName);
//        	m_dbManager.initTable(Variables.termTableName);
//        	m_dbManager.initTable(Variables.treeTableName);
//        	m_dbManager.initTable(Variables.llMapTableName);
//        	m_dbManager.initTable(Variables.locusStsTableName);
//        	m_dbManager.initTable(Variables.locusFlyTableName);
//        }
//        else 
//        {
//        	m_dbManager.initTable(Variables.llHistoryTableName);
//        }
    }

    /**
     * Method to create record objects for all the tables associated with Entrez gene
     * @param history Boolean to indicate if file is entrez history file
     */
    private void createRecords(boolean history) 
    {
    	/**create the records.  This method should be called only after
    	 the tables have been initialized. */
    	if (history == false)
    	{
//    		m_baseRecord = new Record(m_dbManager.noOfColumns(Variables.locusBaseTableName),
//    				m_dbManager.getPrecision(Variables.locusBaseTableName));
    		
    		m_ugRecord = new Record(m_dbManager.noOfColumns(Variables.llUgTableName),
    				m_dbManager.getPrecision(Variables.llUgTableName));
    		
//    		m_omimRecord = new Record(m_dbManager.noOfColumns(Variables.llOmimTableName),
//    				m_dbManager.getPrecision(Variables.llOmimTableName));
//    		m_stsRecord = new Record(m_dbManager.noOfColumns(Variables.locusStsTableName),
//    				m_dbManager.getPrecision(Variables.locusStsTableName));
//    		m_pmidRecord = new Record(m_dbManager.noOfColumns(Variables.llPmidTableName),
//    				m_dbManager.getPrecision(Variables.llPmidTableName));
//    		m_goRecord = new Record(m_dbManager.noOfColumns(Variables.llGoidTableName),
//    				m_dbManager.getPrecision(Variables.llGoidTableName));
//    		m_phenotypeRecord = new Record(m_dbManager.noOfColumns(Variables.llPhenotypeTableName),
//    				m_dbManager.getPrecision(Variables.llPhenotypeTableName));
//    		m_genenamesRecord = new Record(m_dbManager.noOfColumns(Variables.llGeneNamesTableName),
//    				m_dbManager.getPrecision(Variables.llGeneNamesTableName));
//    		m_termRecord = new Record(m_dbManager.noOfColumns(Variables.termTableName),
//    				m_dbManager.getPrecision(Variables.termTableName));
//    		m_treeRecord = new Record(m_dbManager.noOfColumns(Variables.treeTableName),
//    				m_dbManager.getPrecision(Variables.treeTableName));
//    		m_maptermRecord = new Record(m_dbManager.noOfColumns(Variables.termTableName),
//    				m_dbManager.getPrecision(Variables.termTableName));
//    		m_maptreeRecord = new Record(m_dbManager.noOfColumns(Variables.treeTableName),
//    				m_dbManager.getPrecision(Variables.treeTableName));
//    		m_mapRecord = new Record(m_dbManager.noOfColumns(Variables.llMapTableName),
//    				m_dbManager.getPrecision(Variables.llMapTableName));
//    		m_dmRecord =  new Record(m_dbManager.noOfColumns(Variables.locusFlyTableName),
//    				m_dbManager.getPrecision(Variables.locusFlyTableName));
    	}
    	else
    	{
//    		m_geneHistoryRecord = new Record(m_dbManager.noOfColumns(Variables.llHistoryTableName),
//    				m_dbManager.getPrecision(Variables.llHistoryTableName));
    	}
    }
    
    /**
     * Method to reset record objects values for all the tables associated with Entrez gene
     * @param history Boolean to indicate if file is entrez history file
     */
    protected void resetRecords(boolean history)
    {
    	/**
    	 * reset the various records for EntrezGene
    	 */
    	if (history == false)
    	{
    		//m_baseRecord.resetAllFields();
    		m_ugRecord.resetAllFields();
//    		m_omimRecord.resetAllFields();
//    		m_pmidRecord.resetAllFields();
//    		m_goRecord.resetAllFields();
//    		m_phenotypeRecord.resetAllFields();
//    		m_genenamesRecord.resetAllFields();
//    		m_mapRecord.resetAllFields();
//    		m_stsRecord.resetAllFields();
//    		m_dmRecord.resetAllFields();
//    		m_termRecord.resetAllFields();
//    		m_treeRecord.resetAllFields();
    	}
    	else
    	{
    		//reset record not required for gene_history data
    //		m_geneHistoryRecord.resetAllFields();
    	}
    }
     
    /**
     * Method to create file writer objects for all the tables associated with Entrez gene
     * @param history Boolean to indicate if file is entrez history file
     */
    private void createFileWriters(boolean history)
    {
    	/**
    	 * create the FileWriter objects, one for each base table to be populated.
    	 */
    	try
    	{
    		if (history == false)
    		{
    			m_fileWriterHashTable.put(Variables.llUgTableName,new FileWriter(Variables.llUgTableName+"."+m_fileName));
//    			m_fileWriterHashTable.put(Variables.locusBaseTableName,new FileWriter(Variables.locusBaseTableName+"."+m_fileName));
//    			m_fileWriterHashTable.put(Variables.llGoidTableName,new FileWriter(Variables.llGoidTableName+"."+m_fileName));
//    			m_fileWriterHashTable.put(Variables.llOmimTableName,new FileWriter(Variables.llOmimTableName+"."+m_fileName));
//    			m_fileWriterHashTable.put(Variables.llPhenotypeTableName,new FileWriter(Variables.llPhenotypeTableName+"."+m_fileName));
//    			m_fileWriterHashTable.put(Variables.llPmidTableName,new FileWriter(Variables.llPmidTableName+"."+m_fileName));

//    			m_fileWriterHashTable.put(Variables.llGeneNamesTableName,new FileWriter(Variables.llGeneNamesTableName+"."+m_fileName));
//    			m_fileWriterHashTable.put(Variables.termTableName,new FileWriter(Variables.termTableName+"."+m_fileName));
//    			m_fileWriterHashTable.put(Variables.treeTableName,new FileWriter(Variables.treeTableName+"."+m_fileName));
//    			m_fileWriterHashTable.put(Variables.llMapTableName,new FileWriter(Variables.llMapTableName+"."+m_fileName));
//    			m_fileWriterHashTable.put(Variables.locusStsTableName,new FileWriter(Variables.locusStsTableName+"."+m_fileName));
//    			m_fileWriterHashTable.put(Variables.locusFlyTableName,new FileWriter(Variables.locusFlyTableName+"."+m_fileName));
    		}
    		else
    		{
//   			m_fileWriterHashTable.put(Variables.llHistoryTableName,new FileWriter(Variables.llHistoryTableName+"."+m_fileToParse));
    		}
    	}
    	catch(IOException ioEx)      
    	{
    		Logger.log("FatalException has occured: " + ioEx.getMessage(), Logger.WARNING);
    	}
    }
    
    public void parseGeneHistory(boolean history,StringTokenizer sTok) 
    {
		String field1 = null;
		String field0 = null;
		
		resetRecords(history);
		sTok.nextToken(); //ignore this token
		
		field1 = sTok.nextToken();
		field0 = sTok.nextToken();

		if(field0.equals("-"))
				field0 = Variables.dbSpecificNullCharacter;
		if(field1.equals("-"))
				field1 = Variables.dbSpecificNullCharacter;
		
//		m_geneHistoryRecord.fields[1].append(field1);
//		m_geneHistoryRecord.fields[0].append(field0);
		while (sTok.hasMoreTokens()) 
    	{
    		sTok.nextToken();
    	}
//		try
//		{
//			//writeRecordToDb(Variables.llHistoryTableName, m_geneHistoryRecord);
//		}
//		catch(InsertException ie)
//		{
//			Logger.log("FatalException has occured: " + ie.getMessage(), Logger.WARNING);
//		}
//		catch (FatalException e) 
//		{
//			Logger.log("FatalException has occured: " + e.getMessage(), Logger.WARNING);
//		}

    }    
}
