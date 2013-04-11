/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>ClassName: java com.dataminer.server.parser.DbSNPParser</p> 
 */


package com.dataminer.server.parser;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import com.dataminer.server.exception.FatalException;
import com.dataminer.server.exception.InsertException;
import com.dataminer.server.ftp.FileInfo;
import com.dataminer.server.globals.Variables;
import com.dataminer.server.jobmanager.DPQueue;
import com.dataminer.server.log.Logger;
import com.dataminer.server.record.Record;

/**
 * Parser to parse dbSNP data source
 * @author Pratibha Dhok
 * @version 1.0
 */
public class DbSnpParser extends XmlParser 
{
	/** record for each homologene base table  */
	private Record dbSnpBaseRecord;
	private Record dbSnpContigRecord;
	private Record dbSnpFxnRecord;
	private Record dbSnpContigMapRecord;
	private Record dbSnpLocusRecord;
	
	//Contains the current Working Directory address.
	private static String cwd = System.getProperty("user.dir");
		
	private long m_taxonomyId;
	private boolean m_ssTagFound = false;
	private boolean m_ComponentTagFound = false;
	private StringBuffer m_currentData;
	//	Contains the currentSNPID being Parsed.
	private long m_currentSnpId;
	//Contains the currentContigId being Parsed.
	private long m_currentContigId;
	private long m_maplocID = 1;
	
	//variables to hold the values of dbSNP build as in current source file
	private StringBuffer m_dbSNPBuild = new StringBuffer();
	//variable to hold the timestamp when the file was built. The time stamp will be read
	// from the "generated" attribute of ExchangeSet xml.
	private StringBuffer m_generatedAt = new StringBuffer();
	
	/**
	 * Constructor method
	 * @param fileToParse Information of the file to parse
	 * @param filesParsed Oueue maintaining list of parsed filles
	 * @param xmlReader Object of XML Reader class
	 */
	public DbSnpParser(FileInfo fileToParse , DPQueue filesParsed , XMLReader xmlReader)
	{
		super(fileToParse,filesParsed);
		m_currentData = new StringBuffer();
	}
	
	/**
	 * Method to initialize sbSNP related base tables
	 * @throws FatalException Throws fatal exception if error during initialization of tables
	 */
	private void initializeTables() throws FatalException 
	{
		m_dbManager.initTable(Variables.dbSnpBaseTableName);
		m_dbManager.initTable(Variables.dbSnpContigTableName);
		m_dbManager.initTable(Variables.dbSnpContigMapTableName);
		m_dbManager.initTable(Variables.dbSnpFxnTableName);
		m_dbManager.initTable(Variables.dbSnpLocusTableName);
	}
	
	/**
	 * Method to initialize record ojbjects for all the tables of dbSNP
	 */
	
	private void createRecords() 
	{
		dbSnpBaseRecord = new Record(m_dbManager.noOfColumns(Variables.dbSnpBaseTableName),
				m_dbManager.getPrecision(Variables.dbSnpBaseTableName));
		dbSnpContigRecord = new Record(m_dbManager.noOfColumns(Variables.dbSnpContigTableName),
				m_dbManager.getPrecision(Variables.dbSnpContigTableName));
		dbSnpFxnRecord = new Record(m_dbManager.noOfColumns(Variables.dbSnpFxnTableName),
				m_dbManager.getPrecision(Variables.dbSnpFxnTableName));
		dbSnpContigMapRecord = new Record(m_dbManager.noOfColumns(Variables.dbSnpContigMapTableName),
				m_dbManager.getPrecision(Variables.dbSnpContigMapTableName));
		dbSnpLocusRecord = new Record(m_dbManager.noOfColumns(Variables.dbSnpLocusTableName),
				m_dbManager.getPrecision(Variables.dbSnpLocusTableName));
	}
	
	/** 
	 * Create FileWriter Objects
	 */
	private void createFileWriters()
	{
		try
		{
			m_fileWriterHashTable.put(Variables.dbSnpBaseTableName,new FileWriter(Variables.dbSnpBaseTableName+"."+m_fileToParse));
			m_fileWriterHashTable.put(Variables.dbSnpContigTableName,new FileWriter(Variables.dbSnpContigTableName+"."+m_fileToParse));
			m_fileWriterHashTable.put(Variables.dbSnpContigMapTableName,new FileWriter(Variables.dbSnpContigMapTableName+"."+m_fileToParse));
			m_fileWriterHashTable.put(Variables.dbSnpFxnTableName,new FileWriter(Variables.dbSnpFxnTableName+"."+m_fileToParse));
			m_fileWriterHashTable.put(Variables.dbSnpLocusTableName,new FileWriter(Variables.dbSnpLocusTableName+"."+m_fileToParse));
		}
		catch(IOException ioEx)
		{
			Logger.log("Error while initializing file writer objects (dbSNP) : "+ ioEx.getMessage(), Logger.WARNING);
		}
	}
	
	/**
	 * Main parse routine that parses the input files and inserts records into
	 * various DBSNP tables in the database.
	 * @param fileName Information of the file to be parsed
	 * @throws FatalException Throws fatal exception if error during parsing
	 */
	
	public void parse(FileInfo file) throws FatalException 
	{
	    /** DbSnpParser parser deals with single file only. So just pick up the 
	     * first file name from the list of files*/
	    String fileName = (String) file.getFiles().firstElement();
	    
		Logger.log(" DbSnp::parsing started ",Logger.INFO);
		//initialize the dbSnp database tables
		initializeTables();
		Logger.log("Tables initialised",Logger.INFO);
		
		//Create File Writer Objects for dbSnp database tables.  
		createFileWriters();
		Logger.log("file writers created",Logger.INFO);
		
		//create  record objects for DBSNP tables to store data
		createRecords();
		Logger.log("records created",Logger.INFO);
		
		writeMETADATA();
		Logger.log("write metadata over",Logger.INFO);
		
		Logger.log("DBSNP parsing started",Logger.INFO);
		try
		{
			InputSource inp = createInputStream(fileName);
			//Create an input Source Object.
			inp.setSystemId(cwd);
			//Actual Parsing Starts
			xmlReader.parse(inp);
			m_RevisionHistory = m_generatedAt.toString() +  ";Build#" + m_dbSNPBuild.toString() ;
			Variables.dbSNPRevisionHistory.put(Variables.hmTaxidLocalId.get(m_taxonomyId + ""),getFileRevisionHistory(fileName));
		}
		catch (SAXException sax)
		{
			Logger.log(" Error (SAX) occuring while Parsing (dbSNP): " + sax.getMessage(),Logger.INFO);
			throw new FatalException(sax.getMessage());
		}
		catch (FileNotFoundException fnf)
		{
			Logger.log(" Error (File Not Found) occuring while Parsing (dbSNP): ",Logger.INFO);
			throw new FatalException(fnf.getMessage());
		}
		catch (IOException iox)
		{
			Logger.log(" Error (IO) occuring while Parsing: ",Logger.INFO);
			throw new FatalException(iox.getMessage());
		}
		finally
		{
			Logger.log(" DbSnp::parsing over ",Logger.INFO);
		}
		Logger.log("DBSNP parsing done",Logger.INFO);
		
	}
	/**
	 * Whenever a start tag is encountered, the parser enters this Function.
	 * The values in the fields currentTag, currentAttribute gets filled here. 
	 */
	public void startElement(String namespaceURI,String localName, String qName,
			Attributes atts)throws SAXException 
	{
		/** From the ExchangeSet node below we will extract the dbSnpBuild and generated values.
		 * They both will later be appended to give the revision history for the dbSNP file.
		 * <ExchangeSet xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
		 * xmlns="http://www.ncbi.nlm.nih.gov/SNP/docsum" 
		 * xsi:schemaLocation="http://www.ncbi.nlm.nih.gov/SNP/docsum http://www.ncbi.nlm.nih.gov/SNP/docsum/docsum_2005.xsd" 
		 * specVersion="1.0" dbSnpBuild="125" generated="2005-10-30 00:14">
		 */
		if(true == qName.equalsIgnoreCase("ExchangeSet"))
		{
			m_dbSNPBuild.append(atts.getValue("dbSnpBuild"));
			m_generatedAt.append(atts.getValue("generated"));
		}
		
		/** We need to fetch the taxonomy id of current file from tag 
		 *  <SourceDatabase taxId="9606" organism="human" gpipeOrgAbbr="Hs"/>
		 *  Later it will be converted into local taxonomyid and used to relate
		 *  with the revision history of this taxonomy id w.r.t. the current source */
		
		if(true == qName.equalsIgnoreCase("SourceDatabase"))
		{
			m_taxonomyId = Integer.parseInt(atts.getValue("taxId"));
			//m_organism = atts.getValue("organism");
			//m_gpipeOrgAbbr = atts.getValue("gpipeOrgAbbr");
		}
		else
		if(true == qName.equalsIgnoreCase("Rs"))
		{
			// Get list of all the attributes associated with RS
			dbSnpBaseRecord.fields[0].append(atts.getValue("rsId"));
			m_currentSnpId = Long.parseLong(atts.getValue("rsId"));
			dbSnpBaseRecord.fields[1].append(m_taxonomyId);
			dbSnpBaseRecord.fields[2].append(atts.getValue("snpClass"));
			dbSnpBaseRecord.fields[3].append(atts.getValue("snpType"));
			dbSnpBaseRecord.fields[7].append(atts.getValue("molType"));
		}
		else
		if(true == qName.equalsIgnoreCase("Het"))
		{
			dbSnpBaseRecord.fields[8].append(atts.getValue("type"));
			dbSnpBaseRecord.fields[9].append(atts.getValue("value"));
			dbSnpBaseRecord.fields[10].append(atts.getValue("stdError"));
		}
		else
		if(true == qName.equalsIgnoreCase("Ss"))
		{
			m_ssTagFound = true;
		}
		else
		if(true == qName.equalsIgnoreCase("Component"))
		{
			//<Component componentType="contig" ctgId="960600491" accession="NT_079592.1" 
			//name="Hs7b_79657_34" chromosome="7" start="706460" end="58344711" orientation="fwd" gi="37538986" groupTerm="alt_assembly_1" contigLabel="HSC_TCAG">
			dbSnpContigRecord.fields[0].append(m_currentSnpId);
			dbSnpContigRecord.fields[1].append(atts.getValue("ctgId"));
			m_currentContigId = Long.parseLong(atts.getValue("ctgId"));
			dbSnpContigRecord.fields[2].append(atts.getValue("componentType"));
			dbSnpContigRecord.fields[3].append(atts.getValue("accession"));
			dbSnpContigRecord.fields[4].append(atts.getValue("name"));
			dbSnpContigRecord.fields[5].append(atts.getValue("chromosome"));
			dbSnpContigRecord.fields[6].append(atts.getValue("start"));
			dbSnpContigRecord.fields[7].append(atts.getValue("end"));
			dbSnpContigRecord.fields[8].append(atts.getValue("orientation"));
			dbSnpContigRecord.fields[9].append(atts.getValue("gi"));
			m_ComponentTagFound = true;
		}
		else
		if((true == m_ComponentTagFound) && (true == qName.equalsIgnoreCase("MapLoc")))
		{
			//<MapLoc asnFrom="6866028" asnTo="6866028" locType="exact" alnQuality="0.87" 
			//orient="forward" physMapStr="7572489" physMapInt="7572488" 
			//leftFlankNeighborPos="999" rightFlankNeighborPos="1001" 
			//leftContigNeighborPos="6866027" rightContigNeighborPos="6866029" 
			//numberOfMismatches="79" numberOfDeletions="2" numberOfInsertions="3"/>
			dbSnpContigMapRecord.fields[0].append(m_maplocID);
			dbSnpContigMapRecord.fields[1].append(m_currentSnpId);
			dbSnpContigMapRecord.fields[2].append(m_currentContigId);
			dbSnpContigMapRecord.fields[3].append(atts.getValue("asnFrom"));
			dbSnpContigMapRecord.fields[4].append(atts.getValue("asnTo"));
			dbSnpContigMapRecord.fields[5].append(atts.getValue("locType"));
			dbSnpContigMapRecord.fields[6].append(atts.getValue("alnQuality"));
			dbSnpContigMapRecord.fields[7].append(atts.getValue("orient"));
			dbSnpContigMapRecord.fields[8].append(atts.getValue("physMapStr"));
			dbSnpContigMapRecord.fields[9].append(atts.getValue("physMapInt"));
			dbSnpContigMapRecord.fields[10].append(atts.getValue("numberOfMismatches"));
			dbSnpContigMapRecord.fields[11].append(atts.getValue("numberOfDeletions"));
			dbSnpContigMapRecord.fields[12].append(atts.getValue("numberOfInsertions"));
		}
		else
		if(true == qName.equalsIgnoreCase("FxnSet")) // handle fxn set tag in xml
		{
			//<FxnSet geneId="27328" symbol="PCDH11X" mrnaAcc="NM_032968" 
			//mrnaVer="2" protAcc="NP_116750" protVer="1" fxnClass="intron"/>
			dbSnpFxnRecord.fields[0].append(m_currentSnpId);
			dbSnpFxnRecord.fields[1].append(m_currentContigId);
			dbSnpFxnRecord.fields[2].append(m_maplocID);
			dbSnpFxnRecord.fields[3].append(atts.getValue("geneId"));
			dbSnpFxnRecord.fields[4].append(atts.getValue("symbol"));
			dbSnpFxnRecord.fields[5].append(atts.getValue("mrnaAcc"));
			dbSnpFxnRecord.fields[6].append(atts.getValue("mrnaVer"));
			dbSnpFxnRecord.fields[7].append(atts.getValue("protAcc"));
			dbSnpFxnRecord.fields[8].append(atts.getValue("protVer"));
			dbSnpFxnRecord.fields[9].append(atts.getValue("fxnClass"));
			dbSnpLocusRecord.fields[0].append(m_currentSnpId);
			dbSnpLocusRecord.fields[1].append(atts.getValue("geneId"));
			dbSnpLocusRecord.fields[2].append(atts.getValue("symbol"));
		}
		m_currentData.setLength(0);
	}
	
	/**
	 * All the chunk of data between the start and end tags
	 * calls the characters function.
	 */
	public void characters(char buf[], int offset, int len)
	throws SAXException
	{
		//Trim the String to avoid extra white spaces.
		m_currentData.append(new String(buf, offset, len).trim());
		/*m_currentData = new String(buf, offset, len).trim();
		if(m_currentData.indexOf("\n") > 0 )
			m_currentData="";*/
	}
	
	/**
	 * handle end of xml tag event 
	 */
	public void endElement(String namespaceURI,String localName,String qName)
	throws SAXException 
	{
		
		try
		{
			// The tag is Sequence tag and Seq 5' tag found 
			if((true == qName.equalsIgnoreCase("Seq5")) && (false == m_ssTagFound))
			{
				dbSnpBaseRecord.fields[5].append(m_currentData.toString());
			}
			else
			if((true == qName.equalsIgnoreCase("Seq3")) && (false == m_ssTagFound)) 
			{
				dbSnpBaseRecord.fields[6].append(m_currentData.toString());
			}
			else
			if((true == qName.equalsIgnoreCase("Observed")) && (false == m_ssTagFound)) 
			{
				dbSnpBaseRecord.fields[4].append(m_currentData.toString());
			}	
			else
			if(true == qName.equalsIgnoreCase("Ss")) // if end of ss tag found
			{
				m_ssTagFound = false;
			}
			else
			if(true == qName.equalsIgnoreCase("Rs")) // if end of Rs tag found
			{
				writeRecordToDb(Variables.dbSnpBaseTableName, dbSnpBaseRecord);
				dbSnpBaseRecord.resetAllFields();
			}
			else
			if(true == qName.equalsIgnoreCase("Component")) // if end of Component tag found
			{
				writeRecordToDb(Variables.dbSnpContigTableName, dbSnpContigRecord);
				dbSnpContigRecord.resetAllFields();
			}
			else
			if(true == qName.equalsIgnoreCase("MapLoc")) // if end of MapLoc tag found
			{
				writeRecordToDb(Variables.dbSnpContigMapTableName, this.dbSnpContigMapRecord);
				dbSnpContigMapRecord.resetAllFields();
				m_maplocID++;
			}
			else
			if(true == qName.equalsIgnoreCase("FxnSet")) // if end of FxnSet tag found
			{
				writeRecordToDb(Variables.dbSnpFxnTableName, this.dbSnpFxnRecord);
				writeRecordToDb(Variables.dbSnpLocusTableName, this.dbSnpLocusRecord);
				dbSnpFxnRecord.resetAllFields();
				dbSnpLocusRecord.resetAllFields();
			}
			
		}
		catch(InsertException ie)
		{
			Logger.log(" Exception while inserting data into SNP tables: " + ie.getMessage(),Logger.INFO);
		}
		catch(FatalException fe)
		{
			Logger.log(" Exception while inserting data into SNP tables: " + fe.getMessage(),Logger.INFO);
		}
	}
	/**
	 * Method to ignore white spaces
	 */
	public void ignorableWhitespace(char[] ch,int start,int length)
	throws SAXException
	{
	}
	/**
	 * Method to handle errors
	 */
	public void error(SAXParseException exception) throws SAXException
	{
		Logger.log(" Error in Parsing (Normal Error) "+exception.getMessage(),Logger.WARNING);
	}
	/**
	 * Method to handle fatal errors during parsing
	 */
	public void fatalError(SAXParseException exception)throws SAXException
	{
		Logger.log(" Error in Parsing (Fatal) "+exception.getMessage(),Logger.WARNING);
	}
	
	/**
	 * Make parser believe that we have a valid DTD entry
	 */
	public InputSource resolveEntity (String publicId, String systemId)
	{
		//Here we are fooling the parser to believe that we do have a valid
		//DTD while we merely have a byte Stream.
		String dummyXml = "<?xml version='1.0' encoding='UTF-8'?>";
		ByteArrayInputStream bis = new ByteArrayInputStream(dummyXml.getBytes());
		InputSource is = new InputSource(bis);
		return is;
	}
	
	
	
}