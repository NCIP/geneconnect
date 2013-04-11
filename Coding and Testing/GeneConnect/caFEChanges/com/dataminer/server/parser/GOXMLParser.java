/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>ClassName: java com.dataminer.server.parser.GOXMLParser</p> 
 */

package com.dataminer.server.parser;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import com.dataminer.server.exception.FatalException;
import com.dataminer.server.exception.InsertException;
import com.dataminer.server.ftp.FileInfo;
import com.dataminer.server.globals.Constants;
import com.dataminer.server.globals.Variables;
import com.dataminer.server.jobmanager.DPQueue;
import com.dataminer.server.log.Logger;
import com.dataminer.server.record.Record;

/**
 * Class to parse Gene Ontology data-source
 * @author Anuj Tiwari
 * @version 1.0
 */
public class GOXMLParser extends XmlParser 
{
	 /** Tells if the current record is obsolete */
	 private static boolean isObsolete = false;
	/** Contains the currentTag being Parsed.*/
	private static String currentTag = "";
	/** Contains the currentTermID being Parsed.*/
	private static String currentTermId = "";
	/** It stores the termid as read from the current term xml tag*/
	private static StringBuffer currentTermIdTemp = new StringBuffer();
	
	/** Contains the current Working Directory address.*/
	private static String cwd = System.getProperty("user.dir");
	
	/** records for GO base table  */
	private Record termRecord;
	private Record treeRecord;
	/** Strings representing the description of root record as read from the GOConfig.txt file*/
	private static String xmlRoot = "";
	private static String parentTermID = "";
	private static String parentTermName = "";
	
	/**
	 * Constructor method
	 * @param fileToParse Information of the File to parse
	 * @param filesPared Queue holding list of parsed files
	 * @param xmlReader Object of XMLReader class
	 */
	public GOXMLParser(FileInfo fileToParse , DPQueue filesPared ,XMLReader xmlReader)
	{
		super(fileToParse,filesPared);
	}
	
	/**
	 * initialize the GO base tables.The initTable method will get the 
	 * metadata information about that table and store it in a map with DBManager class.
	 * @throws FatalException Throws exception when error encourtered during parsing
	 */
	private void initializeTables() throws FatalException 
	{
		/** initialize the Go database table*/
		m_dbManager.initTable(Variables.termTableName);
		m_dbManager.initTable(Variables.treeTableName);
	}
	
	/**
	 * Method to initialize File writer objectswhere data will be written after parsing from current
	 * source GO File to as to populate in system_termdata and system_termtree table
	 */
	private void createFileWriter()
	{
		try
		{
			m_fileWriterHashTable.put(Variables.termTableName , new FileWriter(Variables.termTableName+"."+m_fileToParse));
			m_fileWriterHashTable.put(Variables.treeTableName , new FileWriter(Variables.treeTableName+"."+m_fileToParse));
		}
		catch(IOException ioEx)
		{
			Logger.log("Error during initialization of file handlers" + ioEx.getMessage(), Logger.WARNING);
		}
	}
	/**
	 * Create the record objects for all the tables which are to be populated by GO data source.
	 */
	private void createRecords()
	{
		termRecord = new Record(m_dbManager.noOfColumns(Variables.termTableName),
				m_dbManager.getPrecision(Variables.termTableName));
		treeRecord = new Record(m_dbManager.noOfColumns(Variables.treeTableName),
				m_dbManager.getPrecision(Variables.treeTableName));
		
	}
	
	/**
	 * Main parse routine that parses the input files and inserts records into
	 * various GO tables in the database.
	 * @param fileName Information of the file to be parsed
	 * @exception FatalException Throws exception if error encountered during parsing
	 */
	public void parse(FileInfo file) throws FatalException 
	{
	    /** GOXMLParser parser deals with single file only. So just pick up the first file name from the list of files*/
	    String fileName = (String) file.getFiles().firstElement();
	    
		Logger.log(" GO::parsing started ", Logger.INFO);

		/** call initialise table method to set the metadata information about each of the
		 * used table. the information will be present with DBManager */
		initializeTables();
		Logger.log("initialise table over",Logger.INFO);
		
		/** Initialise file writers for each of the file which will correspond to each of the table
		 * being populated. The file writers will be stored in m_fileWriterHashTable present with the
		 * base class parser. Later on when writeRecordToDb method is called file writer for the required
		 * table is obtained from the HashTable and record is written there */
		createFileWriter();
		Logger.log("Create file writers over",Logger.INFO);
		
		/** Create required record objects for each of the files which are to be populated by parsing 
		 * GO data source. These records will later hold the field information which is to be written to file for
		 *  loading into the database	 */
		createRecords();
		Logger.log("Create record over",Logger.INFO);

		/** This function will be called to write metadata information in each of the file where records
		 * will be written later. This file will be input to sqlloader and mysqlimport. sqlloader requires 
		 * the table meat information to be present in the data file. This is done by the below function 
		 * before we start writing records to the file*/ 
		writeMETADATA();
		Logger.log("write metadata over",Logger.INFO);
		
		/** Read the config file and set the values for root and Parent Node in system termtree.*/
		readConfigFile();
		/** Inserting information about Root Node into table System_TermTree and System_TermData*/
		insertRootNode();
		try
		{
			InputSource inp = createInputStream(fileName);
			/** Create an input Source Object.*/
			inp.setSystemId(cwd);
			/** Actual GO source file Parsing Starts*/
			xmlReader.parse(inp);
			
			parseFileNameForRevisionHistory(fileName);
			/** Revision history is also set in global variable goRevisionHistory which will be used
			 * later to associate the same value with all the local_taxids in the revision_history 
			 * table.Since the GO file is applicable for all orgs we copy in the table same revision
			 * number for all organisms.
			 */
			Variables.goRevisionHistory = m_RevisionHistory;
		}
		catch (SAXException sax)
		{
			Logger.log(" Error (SAX) occuring while Parsing (GO): ", Logger.INFO);
			throw new FatalException(sax.getMessage());
		}
		catch (FileNotFoundException fnf) 
		{
			Logger.log(" Error (File Not Found) occuring while Parsing (GO): ",
					Logger.INFO);
			throw new FatalException(fnf.getMessage());
		}
		catch (IOException iox)
		{
			Logger.log(" Error (IO) occuring while Parsing: ", Logger.INFO);
			throw new FatalException(iox.getMessage());
		}
		finally
		{
			Logger.log(" GO::parsing over ", Logger.INFO);
		}
		
	}
	/**
	 * Insert root node into System_TermData and System_TermTree table
	 */
	private void insertRootNode()
	{
		/** Creating Records for Trem Data and Term Tree*/
		termRecord.fields[0].append(parentTermID);
		termRecord.fields[1].append(parentTermName);
		
		treeRecord.fields[0].append(parentTermID);
		treeRecord.fields[1].append("0");
		treeRecord.fields[2].append("1");
		
		try
		{
			Logger.log("Inserting Root Node into System_TermData Table",Logger.INFO);
			writeRecordToDb(Variables.termTableName, termRecord);
			termRecord.resetAllFields();
			
			Logger.log("Inserting Root Node into System_TermTree Table",Logger.INFO);
			writeRecordToDb(Variables.treeTableName, treeRecord);
			treeRecord.resetAllFields();
		}
		catch (InsertException exp)
		{
			Logger.log("Caught Insert Exception could not insert Root Node Cannot Continue Exiting... "
					+ exp, Logger.DEBUG);
		}
		catch (FatalException exp)
		{
			Logger.log("Caught Fatal Exception could not insert Root Node Cannot Continue Exiting... "
					+ exp, Logger.DEBUG);
		}
	}
	/**
	 * Read configuration file associated with GO parser
	 */
	private void readConfigFile()
	{
		xmlRoot = (String)Variables.serverProperties.get(Constants.XML_ROOT);
		Logger.log("Read XML ROOT as " + xmlRoot, Logger.INFO);
			
		parentTermID = (String)Variables.serverProperties.get(Constants.PARENT_TERMID);
		Logger.log("Read PARENT TERM ID  as " + parentTermID, Logger.INFO);
			
		parentTermName = (String)Variables.serverProperties.get(Constants.PARENT_TERMNAME);
		Logger.log("Read PARENT TERM NAME as " + parentTermName,Logger.INFO);
			
	}
	/**
	 * Whenever a start tag is encountered, the parser enters this Function.
	 * The values in the fields currentTag,currentAttribute gets filled here. 
	 * @throws SAXException Throws exception when error encountered during parsing
	 */
	public void startElement(String namespaceURI, String localName, String qName,
			Attributes atts) throws SAXException
			{
		currentTag = qName;
		String eName = localName;
		String parent_id = null;
		if ("".equals(eName))
		{
			eName = qName; // namespaceAware = false
		}
		if (atts != null)
		{
			if (eName.equalsIgnoreCase("go:is_a")) 
			{
				String aName = atts.getValue(0); 
				int index = aName.indexOf("#");
				parent_id = aName.substring(index + 1).trim();
				/** If any term points to Go XML root like "TOP" make it point to parent ID
				* specified in the config file.*/
				if (parent_id.equalsIgnoreCase(xmlRoot))
				{
					parent_id = parentTermID;
				}
				treeRecord.resetAllFields();
				treeRecord.fields[1].append(parent_id);
				treeRecord.fields[2].append("1");
				
			}
			else if (eName.equalsIgnoreCase("go:part_of"))
			{
				String aName = atts.getValue(0); 
				int index = aName.indexOf("#");
				parent_id = aName.substring(index + 1).trim();
				/** If any term points to Go XML root like "TOP" make it point to parent ID
				* specified in the config file.*/
				if (parent_id.equalsIgnoreCase(xmlRoot))
				{
					parent_id = parentTermID;
				}
				treeRecord.resetAllFields();
				treeRecord.fields[1].append(parent_id);
				treeRecord.fields[2].append("1");
			}
		}
			}
	
	/**
	 * All the chunk of data between the start and end tags
	 * calls the characters function.
	 * @throws SAXException Throws exception when error encountered during parsing
	 */
	public void characters(char buf[], int offset, int len) throws SAXException
	{
		/** rim the String to avoid extra white spaces.*/
		
		String s = new String(buf, offset, len).trim();
		
		if ((s.indexOf("\n") > -1)||(s.indexOf("\r\n") > -1)||(s.indexOf("\r")) > -1)
			s = "";
		if (0 != s.length()) 
		{
			if (currentTag.equalsIgnoreCase("go:accession")) 
			{
				/** If it is Obselete term or Gene Ontology make term id as null*/
				if (s.equalsIgnoreCase(parentTermID))
				{
					s = "";
				}
				
				termRecord.fields[0].append(s);
				currentTermIdTemp.append(s);
				
			}
			else if (currentTag.equalsIgnoreCase("go:name"))
			{
				termRecord.fields[1].append(s);
			}
			
		}
	}
	
	/**
	 * A very important thing to note is that All the WritetoDbs must be done at the end of the Tag and hence
	 * Must be present in this function.Besides the StringBuffer in the
	 * Before insering any data we need to check term id is not null and all parent ID starts with GO this
	 * filters the unwanted terms.
	 * @throws SAXException Throws exception when error encountered during parsing
	 */
	public void endElement(String namespaceURI, String localName, String qName) throws
	SAXException 
	{
		currentTag = qName;
		try
		{
			if (currentTag.equalsIgnoreCase("go:accession"))
			{
				currentTermId = new String(currentTermIdTemp.toString());
				currentTermIdTemp.setLength(0);
			}
			else if (currentTag.equalsIgnoreCase("go:term")
					&& !currentTermId.equalsIgnoreCase("")
					&& (termRecord.fields[0].toString().startsWith("GO:") || termRecord.fields[0].toString().startsWith("ALL")
					&& (-1 == termRecord.fields[1].lastIndexOf("obsolete"))))
			{
				if (false == isObsolete)
				{
					writeRecordToDb(Variables.termTableName, termRecord);
				}
				else
					isObsolete = false;
				
				termRecord.resetAllFields();
			}
			else if ((currentTag.equalsIgnoreCase("go:is_a") || currentTag.equalsIgnoreCase("go:part_of"))
					&& !currentTermId.equalsIgnoreCase(""))
			{
				treeRecord.fields[0].append(currentTermId);
				if(!currentTermId.startsWith("GO"))
					System.out.println(treeRecord.toString());
				if ((treeRecord.fields[1].toString().startsWith("GO:") || treeRecord.fields[1].toString().startsWith("ALL")))
				{
					writeRecordToDb(Variables.treeTableName, treeRecord);
				}
				else if (-1 != treeRecord.fields[1].lastIndexOf("obsolete"))
				{
					isObsolete = true;
				}
				treeRecord.resetAllFields();
			}
		}
		catch (InsertException ie) 
		{
			Logger.log("Error while inserting data (GO) :" + ie.getMessage(), Logger.WARNING);	
		}
		catch (FatalException fe)
		{
			Logger.log("Fatal Error while inserting data (GO) :" + fe.getMessage(), Logger.WARNING);
		}
	}
	
	/**
	 * Ignore white spaces
	 */
	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException 
	{
	}
	
	/**
	 * Handle errors in SAX parser
	 */
	public void error(SAXParseException exception) throws SAXException 
	{
		Logger.log(" Error in Parsing (Normal Error) " + exception.getMessage(),
				Logger.WARNING);
	}
	/**
	 * Handle Fatal errors in SAX parser
	 */
	public void fatalError(SAXParseException exception) throws SAXException
	{
		Logger.log(" Error in Parsing (Fatal) " + exception.getMessage(),
				Logger.WARNING);
	}
	/**
	 * Make parser believe that we do have a valid
	 */
	public InputSource resolveEntity(String publicId, String systemId) 
	{
		/** Here we are fooling the parser to believe that we do have a valid
		* DTD while we merely have a byte Stream.*/
		String dummyXml = "<?xml version='1.0' encoding='UTF-8'?>";
		ByteArrayInputStream bis = new ByteArrayInputStream(dummyXml.getBytes());
		InputSource is = new InputSource(bis);
		return is;
	}
	/** GO file name itself contains the information about when the file was
	 * last updated . set m_RevisionHistory by parsing filename. The file name is 
	 * of the format go_200512-termdb.rdf-xml.gz where the string 200512 will form
	 * the revision information of the file.
	 * @param fileName Name of the GO file whose revision history information is
	 * to be extracted from the file name
	 */
	private void parseFileNameForRevisionHistory(String fileName)
	{
		StringTokenizer tok = new StringTokenizer(fileName,"_-.");
		if(true == tok.hasMoreTokens())
		{
			tok.nextToken();
			m_RevisionHistory = tok.nextToken();
		}
	}
	
}