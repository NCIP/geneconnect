/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java com.dataminer.server.parser.JDOMParser</p> 
 */
package com.dataminer.server.parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import org.jdom.JDOMException;
import org.jdom.contrib.input.scanner.ElementListener;
import org.jdom.contrib.input.scanner.ElementScanner;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.dataminer.server.exception.FatalException;
import com.dataminer.server.ftp.FileInfo;
import com.dataminer.server.globals.Constants;
import com.dataminer.server.globals.Variables;
import com.dataminer.server.jobmanager.DPQueue;
import com.dataminer.server.log.Logger;

/**
 * XML Parser for EntrezGene Data Source. This is a general purpose class which can be used for creating JDOM parsers 
 * for multiple data sources. Currently It supports only EntrezGene data source. 
 * @author       Anuj Tiwari
 * @version 1.0
 */
public class JDOMParser extends Parser 
{
	/** Data source being parsed */
	private String m_dbType = null;
	/** Initial Map record for SYSTEM_TERMDATA table */ 
	private final String m_termDataInitRecord = Constants.INITIALMAPTERMRECORD;
	/** Initial Map record for SYSTEM_TERMTREE table */
	private final String m_termTreeInitRecord = Constants.INITIALMAPTREERECORD; 
	/** Instance of gereral purpose Parser class which is used to hold an Instance of required specific Parser */
	private Parser m_parserDataObject = null;
	/** Queue to store the already parsed files */
	protected DPQueue m_filesParsed;
	/**
	 * Constructor method
	 * @param dbType Name of the data source  being parsed
	 * @param fileToParse  Information of the file to parse
	 * @param filesParsed List of parsed files
	 */
	public JDOMParser(String dbType, FileInfo fileToParse, DPQueue filesParsed) 
	{
		super(fileToParse, filesParsed);
		m_filesParsed = filesParsed;
		m_dbType = dbType;
	}
	
	/**
	 * Method to parse file
	 * @param file Information of the file to parse
	 * @exception  FatalException throws exception if error during parsing
	 */
	public void parse(FileInfo file) throws FatalException 
	{
	    /** JDOM parser deals with single file only. So just pick up the first file name from the list of files*/
	    String fileName = (String) file.getFiles().firstElement();
	    
		String oldFileName = fileName;
		
		/** JDOM element scanner for the xml file */ 
		ElementScanner xmlScanner;
		Logger.log("Starting parsing for " + m_dbType + ": " + fileName, Logger.INFO);
		ElementListener xmlListener = null; 
		try
		{
			long startParsingTime = System.currentTimeMillis();
			boolean history = false;
			history = findFormat(fileName);
			/** Check if file to be parsed is a Gene-History file */
			if (false == history)
			{
				xmlScanner = new ElementScanner();
				xmlScanner.setValidation(false);
				/** add the listener for the individual nodes according the the dbType specified. */
				if (m_dbType.equalsIgnoreCase(Constants.ENTREZGENE))
				{
					String convertedFileName = null;
					if(fileName.endsWith(".xml") == false)
					{
						convertedFileName = ConvertToXml(fileName);
						fileName = convertedFileName;
					}
					else
					{
						convertedFileName = fileName;
					}
					Logger.log("Converted .asn file to XML " + convertedFileName, Logger.WARNING);
					m_parserDataObject = new EntrezParser(file, m_filesParsed, history);
					/** Initialise the system_termtree and system_termdata tables. */
					startSystemTreeDataFiles();  
					xmlListener = new JDOMXMLListener(m_dbType, convertedFileName, ((EntrezParser)m_parserDataObject));
					/** add a listener for node 'Entrezgene' in	xml file */
					xmlScanner.addElementListener(xmlListener,Constants.ENTREZNODE); 
				}
				Logger.log(xmlScanner.toString(), Logger.INFO);
				Logger.log("input to xmlscanner " + fileName,Logger.INFO);
				/** scan the input file and call elementMatched function whenever the required node is found in the input file*/
				
				xmlScanner.parse(new InputSource(fileName));  
				/** close all the open fileWriter streams. */
				m_parserDataObject.closeFileWriters();
				deleteDownloadedFile(file);
			}
			/** Or if it is a Gene-History file */
			else
			{
				Logger.log("Parsing started for GENE_HISTORY table", Logger.INFO);
				m_parserDataObject = new EntrezParser(file, m_filesParsed, history);
				EntrezParser tempParser = (EntrezParser)m_parserDataObject;
				while ( (m_line = getNextRecord()) != null) 
				{
					StringTokenizer sTok = new StringTokenizer(m_line);
					tempParser.parseGeneHistory(history,sTok);
				}
				m_parserDataObject.closeFileWriters();
			}
			Logger.log("Parsing over for " + m_dbType + ": " + fileName, Logger.INFO);
			long endParsingTime = System.currentTimeMillis();
			long parsingTime = endParsingTime - startParsingTime; 
			Logger.log("Parsing Time for: " + m_dbType + ": " + parsingTime, Logger.INFO);
		}
		catch (NullPointerException nexcp)
		{
			Variables.errorCount++;
			Logger.log("NullPointerException has occured: " + nexcp.getMessage(), Logger.FATAL);
		}
		catch (OutOfMemoryError outexcp)
		{
			Variables.errorCount++;
			Logger.log("OutOfMemoryError has occured: " + outexcp.getMessage(), Logger.FATAL);
		}
		catch (JDOMException jexcp)
		{
			Variables.errorCount++;
			Logger.log("JDOMException has occured: " + jexcp.getMessage(), Logger.FATAL);
		}
		catch (SAXException saxexcp)
		{
			Variables.errorCount++;
			Logger.log("SAXException has occured: " + saxexcp.getMessage(), Logger.FATAL);
		}
		catch (IOException ioexcp)
		{
			Variables.errorCount++;
			Logger.log("IOException has occured: " + ioexcp.getMessage(), Logger.FATAL);
		}
	}
	/**
	 * Method to find format of the file  (History or normal file)
	 * @param fileName Name of the file
	 * @return true if file is not history file
	 */
	private boolean findFormat(String fileName)
	{
		/** Check if the file-name contains "history" in it */
		if(-1 == fileName.lastIndexOf(Constants.HISTORYFILE))
		{
			Logger.log(" history = false ",Logger.DEBUG);
			return false;
		}
		else
		{
			Logger.log(" history = true",Logger.DEBUG);
			return true;
		}
	}
	/**
	 * Method to convert .asn file to .xml
	 * @param fileName name of the file
	 * @return Path of newly created .xml file
	 * @throws FileNotFoundException Throws exception if error during conversion
	 * @throws IOException Throws exception if error during conversion
	 */
	private String ConvertToXml(String fileName) throws FileNotFoundException, IOException
	{
		String outFileName = new String(fileName + ".xml");
		int index = fileName.lastIndexOf(".gz");
		String unzippedFileName = fileName.substring(0,index);
		Logger.log("input file name is " + fileName , Logger.INFO);
		Logger.log("unzipped file name is " + unzippedFileName,Logger.INFO);
		m_inputFileReader.close();
		Runtime run = Runtime.getRuntime();		
		try
		{
			StringBuffer cmdUnzip = new StringBuffer();
			cmdUnzip.append("gzip -d " + fileName);
			/** o/p file will be file name without gz as extension */
			Logger.log(cmdUnzip.toString(),Logger.DEBUG);
			Process unzip =  run.exec(cmdUnzip.toString());
			/** Close the Stream of The Process.If the Stream is not closed 
			* then it will not allow next process to create new Stream. */
			unzip.getInputStream().close();
			unzip.getOutputStream().close();
			unzip.waitFor();                              
			Logger.log("process exit val " + unzip.exitValue(),Logger.INFO);
		}
		catch(InterruptedException ie)
		{
			Logger.log("Exception : Unzipping file " + fileName + " interrupted",Logger.FATAL);
			Logger.log(ie.getMessage(),Logger.FATAL);
		}
		catch(IOException io)
		{
			Logger.log("IOException : unzipping file " + fileName + " failed",Logger.FATAL);
			Logger.log(io.getMessage(),Logger.FATAL);
		}
		Logger.log("successfully unzipped file " + fileName,Logger.INFO);
		try
		{
			StringBuffer cmd = new StringBuffer();
			cmd.append(Variables.currentDir + System.getProperty("file.separator"));
			cmd.append("gene2xml -b -i ");
			cmd.append(unzippedFileName);
			cmd.append(" -o ");
			cmd.append(outFileName);
			Logger.log("converting asn file " + fileName + " to xml file " + outFileName,Logger.INFO);
			System.out.println(cmd);
			
			Process gene2xml =  run.exec(cmd.toString());
			// Close the Stream of The Process.If the Stream is not closed 
			// then it will not allow next process to create new Stream.
			
			InputStream stderr = gene2xml.getInputStream();
			InputStreamReader isr = new InputStreamReader(stderr);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			String errCheck = br.readLine();
			System.out.println(errCheck);
			while ( (line = br.readLine()) != null)
			{
				System.out.println("inside print output line");
				System.out.println(line);
			}
			
			gene2xml.getInputStream().close();
			gene2xml.waitFor();                               
			Logger.log("completed asn to xml file convertion",Logger.INFO);
			Logger.log("process exit val " + gene2xml.exitValue(),Logger.INFO);
			deleteDownloadedFile(unzippedFileName);
		}
		catch(IOException io)
		{
			Logger.log("IOException : Gene to Xml convertion for Entrezegene file " + fileName + " failed",Logger.FATAL);
			Logger.log(io.getMessage(),Logger.FATAL);
			outFileName =  null;
		}
		catch(InterruptedException ie)
		{
			Logger.log("Exception : Gene to Xml convertion for Entrezegene file " + fileName + " interrupted",Logger.FATAL);
			Logger.log(ie.getMessage(),Logger.FATAL);
			outFileName =  null;
		}
		Logger.log("Converted file name "+ outFileName,Logger.INFO);
		return outFileName;
	}
	
	/**
	 * Create file writers for system tree data 
	 */
	private void startSystemTreeDataFiles()
	{
//		FileWriter fwriteSystemTree  = (FileWriter)m_parserDataObject.m_fileWriterHashTable.get(Variables.treeTableName);
//		Logger.log("fwriteSystemTree:" + fwriteSystemTree, Logger.INFO);
//		FileWriter fwriteSystemTerm  = (FileWriter)m_parserDataObject.m_fileWriterHashTable.get(Variables.termTableName);
//		Logger.log("fwriteSystemTerm:" + fwriteSystemTerm, Logger.INFO);
//		try
//		{
//			fwriteSystemTerm.write(m_termDataInitRecord);
//			fwriteSystemTree.write(m_termTreeInitRecord);
//		}
//		catch (IOException ioexcp)
//		{
//			Logger.log("IOException has occured (JDOMParser): " + ioexcp.getMessage(), Logger.DEBUG);
//		}			
	}
}
