/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java com.dataminer.server.parser.UniGeneParser</p> 
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
 * Class to parse UniGene data-source and populate corresponding tables.
 * @author Meghana Chitale
 * @version 1.0
 */
public class UniGeneParser extends Parser 
{
	/** string tokenizer used for parsing file**/
	private StringTokenizer ugIdStok;
	/** Variable to hold the current organism value as detected by separating the prefix of 
	 * UGID eg Hs in case of Hs.0022 */ 
	private String organismInUgid;
	/** record separator */
	private String endOfRecordMarker = "//";
	/** tell whether current record to be parsed is complete */
	private boolean recordCompleted = false;
	/** hashtable to save tissue ids. The tissue terms will be compared with the
	 * ones read from other threads so that no duplicate terms will be added in
	 * system termdata. Hashtable is already synchronised so no need to do that separately*/
	private static Hashtable tissueTable = new Hashtable();
	/** tissue id prefix */
	private String tissueTreeIDPrefix = "TISSUE_";
	/** This static ID is used to assign TISSUE_ID to all the tissue terms which are 
	 * read from unigene source files and are populated to system_termdata table */
	private static long tissueTreeID = 1;
	
	/** records for each unigene tables  */
	private Record ugRecord;		/** for UNIGENE table**/
	private Record ugSequenceRecord;/** for UG_SEQUENCE table*/
	private Record ugProtsimRecord;	/** for UG_PROTSIM table*/
	private Record ugExpressRecord;	/** for UG_EXPRESS table*/
	private Record termRecord;		/** for SYSTEM_TERM table*/
	private Record treeRecord;		/** for SYSTEM_TREE table*/
	private Record ugHistory;		/** for UNIGENE_HISTORY table*/
	
	
	private String m_localTaxid = null; 
	/**
	 * Constructor method
	 * @param file Information of the unigene source file to parse
	 * @param DPQueue Queue which holds list of parsed files which will be produced by parser and
	 * will be given to the loader later on for loading
	 */
	public UniGeneParser(FileInfo fileToParse,DPQueue filesParsed)
	{       
		super(fileToParse,filesParsed);
	}
	
	/**
	 * initialize the Unigene base and dimension tables. The initTable method will get the 
	 * metadata information about that table and store it in a map with DBManager class.
	 * @param history Flag to indicate file to parse is unigene history file
	 * @throws FatalException Throws exception if error during initialization of tables
	 */
	private void initializeTables(boolean noHistory) throws FatalException 
	{
		/** Same parser class is used to parse unigene and unigene_history source files.
		 * based on the source file type the related tables will be initialised   	 */
		if(true == noHistory)
		{
			/** All unigene base tables except unigene_history are initialised when 
			 * unigene source file is being parsed	 */
			m_dbManager.initTable(Variables.ugBaseTableName);
			m_dbManager.initTable(Variables.ugSequenceTableName);
			m_dbManager.initTable(Variables.ugProtsimTableName);
			m_dbManager.initTable(Variables.ugExpressTableName);

			/** Unigene source file also populated data into system_termtree and system_termdata tables.
			 * Hence those tables are also initialised in this parser */
			m_dbManager.initTable(Variables.termTableName);
			m_dbManager.initTable(Variables.treeTableName);
		}
		else
		{
			/** unigene_history table is initialised since retired gene source file is 
			 * getting parsed*/
			m_dbManager.initTable(Variables.ugHistoryTableName);
		}
	}
	
	/**
	 * Create record objects for all the tables associated with UniGene 
	 * @param history boolean to show if the current file to parse is UniGene History
	 */
	private void createRecords(boolean noHistory) 
	{
		/** Same parser class is used to parse unigene and unigene_history source files.
		 * based on the source file type the related records will be initialised */
		if(true == noHistory)
		{
			/** All unigene base table records except unigene_history are initialised when 
			 * unigene source file is being parsed	 */
			
			ugRecord = new Record(m_dbManager.noOfColumns(Variables.ugBaseTableName),
					m_dbManager.getPrecision(Variables.ugBaseTableName));
			ugSequenceRecord = new Record(m_dbManager.noOfColumns(Variables.ugSequenceTableName),
					m_dbManager.getPrecision(Variables.ugSequenceTableName));
			ugProtsimRecord = new Record(m_dbManager.noOfColumns(Variables.ugProtsimTableName),
					m_dbManager.getPrecision(Variables.ugProtsimTableName));
			ugExpressRecord = new Record(m_dbManager.noOfColumns(Variables.ugExpressTableName),
					m_dbManager.getPrecision(Variables.ugExpressTableName));
			
			/** Unigene source file also populated data into system_termtree and system_termdata tables.
			 * Hence those file writers are also created in this parser */
			termRecord = new Record(m_dbManager.noOfColumns(Variables.termTableName),
					m_dbManager.getPrecision(Variables.termTableName));
			treeRecord = new Record(m_dbManager.noOfColumns(Variables.treeTableName),
					m_dbManager.getPrecision(Variables.treeTableName));
		}
		else
		{
			/** unigene_history record is initialised since retired gene source file is 
			 * getting parsed*/
			ugHistory = new Record(m_dbManager.noOfColumns(Variables.ugHistoryTableName),
					m_dbManager.getPrecision(Variables.ugHistoryTableName));
		}
	}
	
	/**
	 * reset the various unigene records by calling resetAllFields method on each record
	 * @param history boolean to show if the current file to parse is UniGene History
	 */
	private void resetRecords(boolean noHistory) 
	{
		/** Same parser class is used to parse unigene and unigene_history source files.
		 * based on the source file type the related records will be reset */
		if(true == noHistory)
		{
			/** All unigene base table records except unigene_history are reset when 
			 * unigene source file is being parsed	 */
			ugRecord.resetAllFields();
			ugSequenceRecord.resetAllFields();
			ugProtsimRecord.resetAllFields();
			ugExpressRecord.resetAllFields();
		}
		else
		{
			/** unigene_history record is reset since retired gene source file is 
			 * getting parsed*/
			ugHistory.resetAllFields();
		}
	}
	
	/**
	 * main parse routine that parses the input files and inserts records into
	 * various tables in the database.
	 * @param file FileInfo object which contains all the information about the file to parse
	 * @exception Throws FatalException if error occurs during parsing
	 */
	public void parse(FileInfo file) throws FatalException
	{
	    /** Unigene parser deals with single file only. So just pick up the first file name from the list of files*/
	    String fileName = (String) file.getFiles().firstElement();
	    
		Logger.log(" UniGene::parsing started " + fileName,Logger.INFO);
		String token;
		
		boolean noHistory = false;
		/** Based on the name of source file to parse detect whether the history or unigene data 
		 * file is getting parsed. This can be detected by checking whether the filename has "retired"
		 * keyword in its name. It indicates that the file name is having information about retired genes 
		 * and hence will populate gene_history table and not the other unigene base tables. Below
		 * function returns true or false based on whether the file is for gene_history or not	 */
		noHistory = findFormat(fileName);
		
		/** call initialise table method to set the metadata information about each of the
		 * used table. the information will be present with DBManager */
		initializeTables(noHistory);  
		Logger.log("initialise table over",Logger.INFO);      
		
		/** Create required record objects based on what(unigene/retired gene) file is getting parsed.
		 *  These records will later hold the field information which is to be written to file for
		 *  loading into the database	 */
		createRecords(noHistory);
		Logger.log("Create record over",Logger.INFO);
		
		/** Initialise file writers for each of the file which will correspond to each of the table
		 * being populated. The file writers will be stored in m_fileWriterHashTable present with the
		 * base class parser. Later on when writeRecordToDb method is called file writer for the required
		 * table is obtained from the HashTable and record is written there */
		createFileWriters(noHistory);
		Logger.log("Create file writers over",Logger.INFO);
		
		/** This function will be called to write metadata information in each of the file where records
		 * will be written later. This file will be input to sqlloader and mysqlimport. sqlloader requires 
		 * the table meat information to be present in the data file. This is done by the below function 
		 * before we start writing records to the file*/ 
		writeMETADATA();
		Logger.log("write metadata over",Logger.INFO);
		
		if(true == noHistory) 
		{
			/** Add a dummy root record to System_TermTree and System_Term tables
			 * The rest of the tissues will be children of the root node.
			 */
			termRecord.fields[0].append(tissueTreeIDPrefix + "0");
			termRecord.fields[1].append("Tissue Expression");
			treeRecord.fields[0].append(termRecord.fields[0].toString());
			treeRecord.fields[1].append("0");
			treeRecord.fields[2].append("1");
			/** write the term and tree_term table record to database*/
			try
			{
				writeRecordToDb(Variables.termTableName, termRecord);
				writeRecordToDb(Variables.treeTableName, treeRecord);
			}
			catch(InsertException ie)
			{
				Logger.log("Error in inserting records :" + ie.getMessage(),Logger.INFO);
			}
			Logger.log("Inserted dummy Tissue records",Logger.INFO);
			/** Reset the fields of termRecord and treeRecord before populating it through data \
			 * from unigene file */
			termRecord.resetAllFields();
			treeRecord.resetAllFields();
			
			Logger.log("unigene parse started",Logger.INFO);
			try 
			{
				/** Read all the lines from source file for parsing*/
				while ( (m_line = getNextRecord()) != null) 
				{
					/** Get tab delimited tokens from the current line */
					m_tokenizer = new StringTokenizer(m_line);
					token = getNextToken();
					if (token != null ) 
					{
						/** each line in the source file has differnt property values which will parsed
						 * and loaded into appropriate record by the below function. The record completion
						 * is detected by end of record marker in the source file which will allow writing
						 * of completed record to files for loader	 */
						parseToken(token);
						/**insert the record if it is complete*/
						if (recordCompleted) 
						{
							/** write the unigene base table record to files*/
							try
							{
								/** In case of incorrect unigene record it will be added to logger for debugging*/ 
								if(ugRecord.fields.length>7)
									ugRecord.print();
								writeRecordToDb(Variables.ugBaseTableName, ugRecord);
							}
							catch(InsertException ie)
							{
								Logger.log("Error while writing to file (Unigene)" + ie.getMessage(), Logger.WARNING);
							}
							/** mark starting of new record.*/
							recordCompleted = false;
							/** current record is completed, reset various table records. The noHistory flag
							 * passed tells whether the history file is getting parsed or not. If so then 
							 * those records will be reset else unigene base table records will be reset*/
							resetRecords(noHistory);
						}
					} 
					/** else ignore the line if no tokens on it*/
				}
				Logger.log("unigene parse complete", Logger.INFO);
				/** m_localTaxid will give the name of organism parsed from the current file. We can 
				 * get the revision number for this file. Then we associate that number with the current file
				 * and make it available to populate the revision_history table	 */
				Variables.UniGeneRevisionHistory.put(m_localTaxid,getFileRevisionHistory(fileName));
			} 
			
			catch (IOException ioex) 
			{
				/** io exception occured - remaining records will be skipped */
				Logger.log(ioex.getMessage(),Logger.DEBUG);
				throw new FatalException(ioex.getMessage());
			}
			finally 
			{
				Logger.log(" UniGene::parsing over ",Logger.INFO);
			}
		}
		else
		{
			/** Parsing unigene history file for information*/
			/** Structure of retired genes file/ unigene history file 
			UniGene Release Bos taurus  72
			Tue Sep 13 18:56:18 EDT 2005
			#previous UniGene Cluster ID
			#current  UniGene Cluster ID
			#UniGene sequence ID
			#Genbank accession
			Bt.1	Bt.1	11834287	AW312154
			Bt.1	Bt.1	11834288	AW312155
			Bt.1	Bt.1	11870510	AW632759
			Bt.1	Bt.1	11897615	AV591375
            */
			try
			{
				int cnt = 0;
				/** Parse each of the line read from the input source file*/
				while ( (m_line = getNextRecord()) != null) 
				{
					/** Ignore the first 6 lines from the source file which have the release 
					 * date and the comments */
					if(cnt>5)
					{
						StringTokenizer sTok = new StringTokenizer(m_line);
						/** Next lines on the source file will be tokenised to get the 4 tokens 
						 * out of which three are used and one is discarded	 */
						while (sTok.hasMoreTokens()) 
						{
							/** Reset the record by passing correct file format identifier before
							 * starting the parsing of next line to newly populate the record*/
							resetRecords(noHistory);
							/** First field in the file is previoug ugid. Second is cuurent ugid
							 * and the fourth is the accession number corresponding to that ugid*/
							ugHistory.fields[1].append(sTok.nextToken());
							ugHistory.fields[0].append(sTok.nextToken());
							sTok.nextToken(); /** Ignore this value*/
							ugHistory.fields[2].append(sTok.nextToken());;
							try
							{
								/** Write the populated unigene_history record to file by 
								 * calling below function and passing the table name and record */
								writeRecordToDb(Variables.ugHistoryTableName, ugHistory);
							}
							catch(InsertException ie)
							{
								Logger.log("Error while writing to file (Unigene)" + ie.getMessage(), Logger.WARNING);
							}
						}
					}
					cnt++;
				}
			}
			catch(IOException e)
			{
				Logger.log("IO Exception while unigene history parsing",Logger.FATAL);
				Logger.log(e.getMessage(),Logger.DEBUG);
			}
		}
	}
	
	/**
	 * Method to create file handlers for parsed Unigene data 
	 * @param history boolean to show if the current file to parse is UniGene History
	 */
	private void createFileWriters(boolean noHistory)
	{
		try
		{
			/** The file name is of the form as below so that if database loader required
			* the table name if will be obtained from string before .
			* Same parser class is used to parse unigene and unigene_history source files.
			* based on the source file type the related tables will be initialised   	 */
			if(true == noHistory)
			{
				/** Unigene source file is being parsed. So file writers corresponding to all 
				 * unigene base tables except unigene_history are created and added to m_fileWriterHashTable */
				m_fileWriterHashTable.put(Variables.ugBaseTableName,    new FileWriter( Variables.ugBaseTableName + "." + m_fileToParse ));                     
				m_fileWriterHashTable.put(Variables.ugExpressTableName, new FileWriter( Variables.ugExpressTableName + "." + m_fileToParse ));            
				m_fileWriterHashTable.put(Variables.ugProtsimTableName, new FileWriter( Variables.ugProtsimTableName + "." + m_fileToParse));
				m_fileWriterHashTable.put(Variables.ugSequenceTableName,new FileWriter( Variables.ugSequenceTableName + "." + m_fileToParse));        	

				/** Unigene source file also populated data into system_termtree and system_termdata tables.
				 * Hence those file writers are also created in this parser */
				m_fileWriterHashTable.put(Variables.treeTableName,      new FileWriter( Variables.treeTableName + "." + m_fileToParse));
				m_fileWriterHashTable.put(Variables.termTableName,      new FileWriter( Variables.termTableName + "." + m_fileToParse));
			}
			else
			{
				/** Unigene_history source file is being parsed. So file writers corresponding to  
				 * unigene_history table is created and added to m_fileWriterHashTable */
				m_fileWriterHashTable.put(Variables.ugHistoryTableName,      new FileWriter( Variables.ugHistoryTableName+ "." + m_fileToParse));
			}
		}
		catch(IOException ie)
		{
			Logger.log("Cannot Create File Writers " + m_fileToParse + ie.getMessage(), Logger.WARNING);
		}
		
	}
	
	/**
	 * Method to actually parse of the unigene record
	 * @param token Token to parse to get unigene record
	 * @throws FatalException Throws exception if error occurs while parsing
	 */
	public void parseToken(String token) throws FatalException 
	{
		/** Structure of unigene source data file 
		ID          Bt.1
		TITLE       Casein kinase 2, alpha 1 polypeptide
		GENE        CSNK2A1
		CYTOBAND    13
		LOCUSLINK   282419
		HOMOL       YES
		EXPRESS     mixed ; extraembryonic tissue ; stomach ; brain ; small intestine ; ovary ; Embryo ; embryonic tissue ; uterus 
		CHROMOSOME  13
		STS         ACC=CSNK2A1 UNISTS=253713
		STS         ACC=U51866 UNISTS=125766
		PROTSIM     ORG=Arabidopsis thaliana; PROTGI=15227778; PROTID=ref:NP_179889.1; PCT=75.78; ALN=322
		PROTSIM     ORG=Caenorhabditis elegans; PROTGI=125269; PROTID=sp:P18334; PCT=78.59; ALN=355
		SCOUNT      47
		SEQUENCE    ACC=X54962.1; NID=g610; PID=g611; SEQTYPE=mRNA
		SEQUENCE    ACC=M93665.1; NID=g162776; PID=g162777; SEQTYPE=mRNA
		*/

		String tempString;
		/** If token is ID then the next token will represent the UGID value for that
		 * record in the file */
		if (token.equalsIgnoreCase("ID")) 
		{
			tempString = getNextToken();
			String id = (tempString != null) ? tempString.trim() : tempString;
			/** UGID is the primary key for all the UG records. All the records of different unigene
			 * base tables which will be populated from this source record will have the current
			 * UGID so the record object fields are set accordingly*/
			ugRecord.fields[0].append(id);
			ugSequenceRecord.fields[0].append(id);
			ugProtsimRecord.fields[0].append(id);
			ugExpressRecord.fields[0].append(id);
			/** The UGID value is of type Hs.00001 which has first characters before the "." 
			 * representing the organism to which the UGID refers. Tokeniser will separate the 
			 * organism value	 */
			ugIdStok = new StringTokenizer (ugRecord.fields[0].toString(), new String("."));
			organismInUgid = ugIdStok.nextToken();
			/** From the abbreviated organism value obtained from the UGID we get the scientific
			 * of the organism and the local taxid corresponding to it in the organism_taxonomymap table
			 * hmOrgAbbreviationName map stores the abberivated org names and corresponding
			 * scientific names. hmOrganismLocalId maps the scientific name of organism to the local
			 * taxid which is constant irrespective of the updation of ncbi taxid. 
			 */
			String orgName = (String)Variables.hmOrgAbbreviationName.get(organismInUgid);
			ugRecord.fields[1].append(Variables.hmOrganismLocalId.get(orgName));
			
			/** m_localTaxid variable will store the organism which is getting parsed from the
			 * current source file. It will be overwritten per record basis but since one unigene
			 * file gives one organism we will get the current organism name at the end
			 */
			m_localTaxid = ugRecord.fields[1].toString();
			
			/** the revision history of given taxonomyid will be stored w.r.t. UNIGENE data source
			 * in the form of last modified date of the source */
		} 
		else if (token.equalsIgnoreCase("TITLE")) 
		{
			/**This method will separate the title value from the corresponding source file record.
			 * Below function will store the title value in ugRecord third field which corresponds to
			 * dat ain unigene table */
			parseTitle();
		}
		else if (token.equalsIgnoreCase("GENE"))
		{
			/** value stored against the GENE tag will be stored in fourth field in the unigene table*/
			tempString = getNextToken();
			ugRecord.fields[3].append(tempString.trim());
		}
		else if (token.equalsIgnoreCase("CYTOBAND"))
		{
			/** Next token on the current line after CYTOBAND will be value of cytoband for the 
			 * current record which will be stored in the fifth field in the ugRecord */
			tempString = getNextToken();
			ugRecord.fields[4].append(tempString.trim());
		}
		else if (token.equalsIgnoreCase("EXPRESS")) 
		{
			/** It tokenises the data against EXPRESS tag and populates ug_express 
			 * and system term records*/
			parseExpress();
		}
		else if (token.equalsIgnoreCase("LOCUSLINK"))
		{
			/** Against the LOCUSLINK there will be information about LOCUSLINK(Entrezgene) ID which
			 * will stored as UGE_GENEID in the sixth field of unigene table*/ 
			tempString = getNextToken();
			int index = tempString.indexOf(";");
			if(-1!=index)
				ugRecord.fields[5].append(tempString.substring(0,index));
			else
				ugRecord.fields[5].append(tempString.trim());
		} 
		else if (token.equalsIgnoreCase("CHROMOSOME"))
		{
			/** Information about the CHROMOSOME obatained form the line having CHROMOSOME token will
			 * be stored in the seventh field UGE_CHROMOSOME of unigene table */
			tempString = getNextToken();
			if(ugRecord.fields[6].length() > 0)
				ugRecord.fields[6].append(",");
			ugRecord.fields[6].append(tempString.trim());
		} 
		else if (token.equalsIgnoreCase("PROTSIM"))
		{
			/** If token is PROTSIM then  the function below will parse the data following it to 
			 * populate ug_protsim table */
			parseProtsim();
		}
		else if (token.equalsIgnoreCase("SEQUENCE"))
		{
			/** If token is SEQUENCE then  the function below will parse the data following it to 
			 * populate ug_sequence table */
			parseSequences();
		} 
		else if(token.equalsIgnoreCase(endOfRecordMarker)) 
		{
			/** "//" is the end of current record in unigene source file. If record is complete then the 
			 * parse method appropriately completed records will be written to files */
			recordCompleted = true;
		} /** else the token did not match any criteria: so ignore the line*/
	}
	
	/**
	 * Parse the title string
	 */
	public void parseTitle() 
	{
		String buf = parseValue("TITLE");
		ugRecord.fields[2].append(buf);
	}
	
	/**
	 * tokenizes the input line into Express record
	 * @throws Throws FatalException when an error is encountered
	 */
	public void parseExpress() throws FatalException 
	{
		String express = parseValue("EXPRESS");
		StringTokenizer sTok = new StringTokenizer(express, ";");
		
		String tissue = null; 
		String tissueID = null; 
		while (sTok.hasMoreTokens()) 
		{
			/** setting the tissue_name case to lower case. Because it is required
			* later to get back all the "distinct" tissues in the UG cluster.
			* ugExpressRecord.fields[1].append(sTok.nextToken().toLowerCase());*/
			tissue = sTok.nextToken().toLowerCase().trim();
			
			long curTissueTreeID = tissueTreeID;
			tissueID = getTissueID(tissue);
			
			/** populate and ugexpress, term and treeterm records*/
			ugExpressRecord.fields[1].append(tissueID);
			/** write the ugExpressRecord*/
			try
			{
				writeRecordToDb(Variables.ugExpressTableName, ugExpressRecord);
			}
			catch(InsertException ie){}
			/** reset the tissue name field.*/
			ugExpressRecord.fields[1].setLength(0);
			
			/** write the term and termTree record into database
			* ignore if tissue already in database*/
			if (tissueTreeID != curTissueTreeID) 
			{
				
				termRecord.fields[0].append(tissueID);
				termRecord.fields[1].append(tissue);
				try
				{
					writeRecordToDb(Variables.termTableName, termRecord );
				}
				catch(InsertException ie){}
				/**tissueID*/
				treeRecord.fields[0].append(tissueID);
				/**parent ID*/
				treeRecord.fields[1].append(tissueTreeIDPrefix + "0");
				treeRecord.fields[2].append("1");
				try
				{
					writeRecordToDb(Variables.treeTableName, treeRecord );
				}
				catch(InsertException ie){}
				/**reset record fields*/
				termRecord.resetAllFields();
				treeRecord.resetAllFields();
			}
		}
	}
	
	/**
	 * Returns an ID for the tissue. tissueTable variable is shared across the instances of 
	 * Unigene parser. Hence it makes sure that if the term has been read from some other
	 * file then it will not be put again in the table. Operation on Hashtable are synchronized
	 * across the threads 
	 * @param tissue String term for which to get identifier
	 * @return Term Identifer for given tissue term
	 */
	private String getTissueID(String tissue)
	{
		String id = (String) tissueTable.get(tissue);
		if (id == null)
		{
			/** new child*/
			id = tissueTreeIDPrefix + tissueTreeID++;
			tissueTable.put(tissue.trim(), id);
		}
		return id;
	}
	
	/**
	 * parse the Protsim record
	 * @exception Throws exception if error has been encountered during parse 
	 */
	public void parseProtsim() throws FatalException 
	{
		String temp = "PROTSIM";
		String locTaxId = "";
		String value="";
		int dotIndex=-1;
		/**get the PROTSIM value from the line*/
		String protValue = parseValue(temp);
		
		/** separate the protein data*/
		StringTokenizer sTok = new StringTokenizer(protValue.trim(), ";");
		/** separate the ORG, PROTGI and PROTID*/
		while (sTok.hasMoreTokens())
		{
			String token = sTok.nextToken();
			StringTokenizer data = new StringTokenizer(token, "=");
			/** separate values*/
			while(data.hasMoreTokens())
			{
				temp = data.nextToken().trim();
				if (temp.equalsIgnoreCase("ORG"))
				{
					locTaxId = (String)Variables.hmOrganismLocalId.get(data.nextToken());
					ugProtsimRecord.fields[1].append(locTaxId);
					locTaxId = "";
				} 
				else if (temp.equalsIgnoreCase("PROTGI"))
				{
					ugProtsimRecord.fields[2].append(data.nextToken());
				}
				else if (temp.equalsIgnoreCase("PROTID"))
				{
					value=data.nextToken();
					dotIndex= value.indexOf(".");
					if(dotIndex!=-1)
						value=value.substring(0,dotIndex);
					ugProtsimRecord.fields[3].append(value);
				}
			}
		}
		/**insert protsim record*/
		try
		{
			writeRecordToDb(Variables.ugProtsimTableName, ugProtsimRecord);
		}
		catch(InsertException ie){}
		/**reset the fields as the record is inserted*/
		ugProtsimRecord.fields[1].setLength(0);
		ugProtsimRecord.fields[2].setLength(0);
		ugProtsimRecord.fields[3].setLength(0);
	}
	
	/**
	 * paser the sequence
	 * @exception Throws exception if error has been encountered during parse
	 */
	public void parseSequences() throws FatalException 
	{
		String temp = "SEQUENCE";
		/**line without the "SEQUENCE" token*/
		String remStr = parseValue(m_line, temp);
		
		if (remStr == null) 
		{
			return;
		}
		String name, value;
		int nameIdx, valueIdx,dotIndex=-1;
		while (remStr.length() > 0) 
		{
			nameIdx = remStr.indexOf('=');//Index of the First "=" in rem STR
			if (nameIdx != 1)
			{
				/** get name of the Variable e.g. ACC/NID/PID...*/
				name = remStr.substring(0,nameIdx);
				/**get String from value of variable until end.*/
				remStr = remStr.substring(nameIdx+1).trim();
				value = remStr;
				/**Now get the index of the first ";"*/
				valueIdx = remStr.indexOf(';');
				if (valueIdx != -1)
				{
					/**if index is not null,then get value and get remaining String after ;*/
					value = remStr.substring(0, valueIdx);
					remStr = remStr.substring(valueIdx+1).trim();
				}
				else
				{
					remStr = "";
				}
				if (name.equals("ACC"))
				{
					dotIndex= value.indexOf(".");
					if(dotIndex!=-1)
						value=value.substring(0,dotIndex);
					ugSequenceRecord.fields[1].append(value);
				}
				else if (name.equals("NID"))
				{
					ugSequenceRecord.fields[2].append(value);
				}
				else if (name.equals("PID"))
				{
					ugSequenceRecord.fields[3].append(value);
				}
			}
		}
		
		/**insert sequence record*/
		try
		{
			writeRecordToDb(Variables.ugSequenceTableName, ugSequenceRecord);
		}
		catch(InsertException ie){}
		/**reset the fields as the record is inserted*/
		ugSequenceRecord.fields[1].setLength(0);
		ugSequenceRecord.fields[2].setLength(0);
		ugSequenceRecord.fields[3].setLength(0);
	}
	/**
	 * Method to find if given file is unigene history file
	 * @param fileName File to check for format
	 * @return false if file is history file
	 */
	private boolean findFormat(String fileName)
	{
		if(-1 == fileName.lastIndexOf("retired"))
		{
			Logger.log(" history = false ",Logger.INFO);
			return true;
		}
		else
		{
			Logger.log(" history = true",Logger.INFO);
			return false;
		}
	}
}
