/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>ClassName: java com.dataminer.server.parser.UniGeneParser</p> 
 */

package edu.wustl.geneconnect.parser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

import com.dataminer.server.exception.FatalException;
import com.dataminer.server.globals.Constants;
import com.dataminer.server.globals.Variables;
import com.dataminer.server.io.PropertiesFileHandeler;
import com.dataminer.server.log.Logger;
import com.dataminer.server.parser.WorkingGZIPInputStream;

/**
 * Class to parse UniGene data-source and populate corresponding tables.
 * @author Sachin Lale
 * @version 1.0
 */
public class UniGeneParser  
{
	String m_fileToParse=new String();
	String m_basedir =new String(); 
	String m_outputFile = new String();
	static String FILESEP = System.getProperty("file.separator");
	/** input file reader */
	protected BufferedReader m_inputFileReader;
	

	private FileWriter m_UnigeneRecordWriter;
	private FileWriter m_UnigeneEntrezRecordWriter;
	/** current line that is been parsed */
	private String m_line;
	/** breaks the line into tokens */
	private StringTokenizer m_tokenizer;
	  
	private StringBuffer[] ugRecord = new StringBuffer[5];
	private Vector ugEntrezRecords;
	//private StringBuffer[] ugEntrezRecord = new StringBuffer[2];
	/** string tokenizer used for parsing file**/
	private StringTokenizer ugIdStok;
	/** Variable to hold the current organism value as detected by separating the prefix of 
	 * UGID eg Hs in case of Hs.0022 */ 
	private String organismInUgid;
	/** record separator */
	private String endOfRecordMarker = "//";
	/** tell whether current record to be parsed is complete */
	private boolean recordCompleted = false;

	static final String UNIGENE_TABLE="UNIGENE";
	static final String UNIGENE_ENTREZ_TABLE="UNIGENE_ENTREZ";
	
	private String m_localTaxid = null;
	
	public HashMap hmOrganismLocalId = new HashMap();
	public HashMap hmTaxidLocalId = new HashMap();
	public HashMap hmOrgAbbreviationName = new HashMap();
	private static String DIRECT_ANNOTATION="1";
	
	/**
	 * Constructor method
	 * @param file Name of the unigene source file to parse
	 * @param DPQueue Queue which holds list of parsed files which will be produced by parser and
	 * will be given to the loader later on for loading
	 */
	UniGeneParser(String fileToParse,String basedir,String outputFile)
	{
		Logger.log("Constructor of UniGene External Parser", Logger.INFO);
		m_basedir=basedir;
		m_fileToParse = fileToParse;
		m_outputFile = outputFile;
	}
	
	/**
	 * open a file for reading 
	 * @param fileName Name of the file to parse
	 * @throws IOException Throws exception if error during opening file
	 * @throws FileNotFoundException Throws exception if error during opening file
	 */
	public void open()
	throws IOException, FileNotFoundException 
	{
		Logger.log("Opened the file for parsing", Logger.INFO);
		/** check if its a compressed(gzip) or uncompressed file*/
		if(m_fileToParse.endsWith(".gz"))
			m_inputFileReader = new BufferedReader
			(new InputStreamReader
					(new WorkingGZIPInputStream
							(new FileInputStream(m_basedir+FILESEP+m_fileToParse))));
		else
			m_inputFileReader = new BufferedReader(new FileReader(m_basedir+FILESEP+m_fileToParse));
		
		// Open file where records will be written
		Logger.log("Opened the file for writing", Logger.INFO);
		m_UnigeneRecordWriter=new FileWriter(m_basedir+FILESEP+UNIGENE_TABLE+ "." + m_fileToParse);
		m_UnigeneRecordWriter.write("LOAD DATA INFILE * APPEND INTO TABLE UNIGENE FIELDS TERMINATED BY '###' (geneid,title,gene,org,taxid)"+"\n"+"BEGINDATA"+"\n");
		m_UnigeneRecordWriter.flush();
		m_UnigeneEntrezRecordWriter=new FileWriter(m_basedir+FILESEP+UNIGENE_ENTREZ_TABLE+ "." + m_fileToParse);
		m_UnigeneEntrezRecordWriter.write("LOAD DATA INFILE * APPEND INTO TABLE unigene_entrez_U FIELDS TERMINATED BY '###' (UEN_UNIGENEID,UEN_ENTREZGENEID,UEN_LINKTYPE)"+"\n"+"BEGINDATA"+"\n");
		m_UnigeneEntrezRecordWriter.flush();
	}
	
	
	
	/**
	 * Create record objects for all the tables associated with UniGene 
	 * @param history boolean to show if the current file to parse is UniGene History
	 */
	private void createRecords() 
	{
		/** Same parser class is used to parse unigene and unigene_history source files.
		 * based on the source file type the related records will be initialised */
			/** All unigene base table records except unigene_history are initialised when 
			 * unigene source file is being parsed	 */
		
		ugRecord[0]=new StringBuffer();
		ugRecord[1]=new StringBuffer();
		ugRecord[2]=new StringBuffer();
		ugRecord[3]=new StringBuffer();
		ugRecord[4]=new StringBuffer();
		ugEntrezRecords = new Vector();
		//ugEntrezRecord[0]=new StringBuffer();
		//ugEntrezRecord[1]=new StringBuffer();
		
	}
	private String getUnigeneRecord()
	{
		StringBuffer record = new StringBuffer();
		record.append(ugRecord[0].toString()+Constants.columnSeparator);
		record.append(ugRecord[1].toString()+Constants.columnSeparator);
		record.append(ugRecord[2].toString()+Constants.columnSeparator);
		record.append(ugRecord[3].toString()+Constants.columnSeparator);
		record.append(ugRecord[4].toString()+"\n");
		return record.toString();
	}
	private String getUnigeneEntrezRecord()
	{
		StringBuffer record = new StringBuffer();
		for(int i=0;i<ugEntrezRecords.size();i++)
		{
			record.append(ugRecord[0].toString()+Constants.columnSeparator);
			record.append(ugEntrezRecords.get(i)+Constants.columnSeparator);
			record.append(DIRECT_ANNOTATION+"\n");
			
		}
		return record.toString();
	}
	/**
	 * reset the various unigene records by calling resetAllFields method on each record
	 * @param history boolean to show if the current file to parse is UniGene History
	 */
	private void resetRecords() 
	{
		/** Same parser class is used to parse unigene and unigene_history source files.
		 * based on the source file type the related records will be reset */
			/** All unigene base table records except unigene_history are reset when 
			 * unigene source file is being parsed	 */
		//	ugRecord.resetAllFields();
		ugRecord[0].setLength(0);
		ugRecord[1].setLength(0);
		ugRecord[2].setLength(0);
		ugRecord[3].setLength(0);
		ugRecord[4].setLength(0);
		ugEntrezRecords.clear();
	}
	
	/**
	 * main parse routine that parses the input files and inserts records into
	 * various tables in the database.
	 * @param fileName Name of the file to parse
	 * @exception Throws FatalException if error occurs during parsing
	 */
	protected void parse() throws FatalException
	{
		Logger.log("Parse method", Logger.INFO);
		//Logger.log(" UniGene::parsing started " + fileName,Logger.INFO);
		String token;
		
		
		/** Create required record objects based on what(unigene/retired gene) file is getting parsed.
		 *  These records will later hold the field information which is to be written to file for
		 *  loading into the database	 */
		createRecords();
	//	Logger.log("Create record over",Logger.INFO);
		
		
		/** This function will be called to write metadata information in each of the file where records
		 * will be written later. This file will be input to sqlloader and mysqlimport. sqlloader requires 
		 * the table meat information to be present in the data file. This is done by the below function 
		 * before we start writing records to the file*/ 
	//writeMETADATA();
	//	Logger.log("write metadata over",Logger.INFO);
		
		
			
	//		Logger.log("unigene parse started",Logger.INFO);
			try 
			{
				/** Read all the lines from source file for parsing*/
				while ( (m_line = getNextRecord()) != null) 
				{
//					if(m_line.indexOf("ID")==0)
//						System.out.println("m_line: "+m_line);
//					if(m_line.indexOf("GENE_ID")>=0)
//						System.out.println("m_line: "+m_line);
					
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
								if(ugRecord.length>7)
								{	
									//ugRecord.print();
								}	
								//writeRecordToDb(Variables.ugBaseTableName, ugRecord);
								m_UnigeneRecordWriter.write(getUnigeneRecord());
								m_UnigeneRecordWriter.flush();
								m_UnigeneEntrezRecordWriter.write(getUnigeneEntrezRecord());
								m_UnigeneEntrezRecordWriter.flush();
								//System.out.print(ugRecord[i] +"###");
								
								//System.out.println("");
							}
							catch(Exception ie)
							{
		//						ie.printStackTrace();
								Logger.log("Error while writing to file (Unigene)" + ie.getMessage(), Logger.WARNING);
							}
							/** mark starting of new record.*/
							recordCompleted = false;
							/** current record is completed, reset various table records. The noHistory flag
							 * passed tells whether the history file is getting parsed or not. If so then 
							 * those records will be reset else unigene base table records will be reset*/
							resetRecords();
						}
					} 
					/** else ignore the line if no tokens on it*/
				}
		//		Logger.log("unigene parse complete", Logger.INFO);
	
			} 
			
			catch (IOException ioex) 
			{
				/** io exception occured - remaining records will be skipped */
		//		Logger.log(ioex.getMessage(),Logger.DEBUG);
				ioex.printStackTrace();
				Logger.log("Parse Metgod: " + ioex.getMessage(),Logger.FATAL);
				//throw new FatalException(ioex.getMessage());
			}
			finally 
			{
		//		Logger.log(" UniGene::parsing over ",Logger.INFO);
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
			
			ugRecord[0].append(id);
			

			/** The UGID value is of type Hs.00001 which has first characters before the "." 
			 * representing the organism to which the UGID refers. Tokeniser will separate the 
			 * organism value	 */
			ugIdStok = new StringTokenizer (ugRecord[0].toString(), new String("."));
		//	System.out.println("ugIdStok: " +ugIdStok);
			organismInUgid = ugIdStok.nextToken();
			/** From the abbreviated organism value obtained from the UGID we get the scientific
			 * of the organism and the local taxid corresponding to it in the organism_taxonomymap table
			 * hmOrgAbbreviationName map stores the abberivated org names and corresponding
			 * scientific names. hmOrganismLocalId maps the scientific name of organism to the local
			 * taxid which is constant irrespective of the updation of ncbi taxid. 
			 */
			String orgName = (String)hmOrgAbbreviationName.get(organismInUgid);
			//System.out.println("orgName: " +orgName);
			ugRecord[3].append(orgName);
			//System.out.println("ugRecord[3]: " +ugRecord[3]);
			
			/** m_localTaxid variable will store the organism which is getting parsed from the
			 * current source file. It will be overwritten per record basis but since one unigene
			 * file gives one organism we will get the current organism name at the end
			 */
			m_localTaxid = (String)hmOrganismLocalId.get(orgName);
			ugRecord[4].append(m_localTaxid);
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
			ugRecord[2].append(tempString.trim());
		}
		else if (token.equalsIgnoreCase("GENE_ID"))
		{
			/** Against the LOCUSLINK there will be information about LOCUSLINK(Entrezgene) ID which
			 * will stored as UGE_GENEID in the sixth field of unigene table*/ 
			/**
			 * Entrez gene identifier associated with at least one sequence in this cluster; 
	           to be used instead of LocusLink. 
			 */
			tempString = getNextToken();
			String entrezID = "";
			StringTokenizer strToken =  new StringTokenizer(tempString,";");
			if(strToken.countTokens()>1)
			System.out.println("countTokens: "+strToken.countTokens());
			
			while(strToken.hasMoreTokens())
			{
				entrezID=strToken.nextToken().trim();
				//System.out.println("entrezID: "+entrezID);
				ugEntrezRecords.add(entrezID);
				
			}
			
			//int index = tempString.indexOf(";");
			
			//if(-1!=index)
				//ugRecord.fields[5].append(tempString.substring(0,index));
			//else
				//ugRecord.fields[5].append(tempString.trim());
		} 
		
		else if(token.equalsIgnoreCase(endOfRecordMarker)) 
		{
			/** "//" is the end of current record in unigene source file. If record is complete then the 
			 * parse method appropriately completed records will be written to files */
			recordCompleted = true;
		} /** else the token did not match any criteria: so ignore the line*/
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
	 * Parse the title string
	 */
	public void parseTitle() 
	{
		String buf = parseValue("TITLE");
		ugRecord[1].append(buf);
	}
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
	 * Method to populate map consisting of abbriviated organism names and FULL org names
	 * This map will be used for obtaining the scientific name of organism when parsing files
	 * from UNIGENE source. From UNIGENE files we get the organism information in the form of 
	 * abbreviated org name as Gga,Bt,Hs etc. We convert this to the local taxid in UNIGENE
	 * parser when we want to store the local taxid.
	 */
	public void populateAbbreviatedOrganismMap()
	{
		try
		{
			BufferedReader fReader = new BufferedReader(new FileReader(m_basedir + FILESEP + "Config"
					+ FILESEP + Constants.unigeneAbbreviatedOrgFile));
			String line;
			/** We UnigeneOrganismAbbreviations.txt file to populate the map between 
			 * abbreviated names- full names of organisms */
			while((line = fReader.readLine())!=null)
			{
				StringTokenizer sTok = new StringTokenizer(line,"\t");
				try
				{
					if(sTok.countTokens()>=2)
					{
						String abbreviation = sTok.nextToken();
						String orgName = sTok.nextToken();
						hmOrgAbbreviationName.put(abbreviation.trim(),orgName.trim());
					}
				}
				catch(NoSuchElementException e)
				{
					e.printStackTrace();
					Logger.log("Misiing token on line "+line,Logger.INFO);
				}
			}
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
			Logger.log(Constants.unigeneAbbreviatedOrgFile + " file not found ",Logger.FATAL);
			Logger.log(e.getMessage(),Logger.DEBUG);
			Variables.errorCount++;
		}
		catch(IOException e)
		{
			e.printStackTrace();
			Logger.log("IO Exception while reading file " + Constants.unigeneAbbreviatedOrgFile ,Logger.FATAL);
			Logger.log(e.getMessage(),Logger.DEBUG);
			Variables.errorCount++;
		}
	}
	public void populateOrgaismTaxonomyMap(String driverName,String dbURL,String userName,String passWord)
	{
		Connection conn=null;
		
		
		
		try
		{
			Class.forName(driverName);				
			conn = DriverManager.getConnection(dbURL, userName, passWord);
			Logger.log("connection successful",Logger.INFO);
			conn.setAutoCommit(false);
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
				hmOrganismLocalId.put(rs.getString(3).trim(),rs.getString(1).trim());
				i++;
			}
			Logger.log("Added " + i + " records in maps for organism taxonomy",Logger.INFO);
			rs.close();
			stmt.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			Logger.log("SQL Exception in organismTaxonomymap ",Logger.FATAL);
			Logger.log(e.getMessage(),Logger.DEBUG);
		}
		catch(ClassNotFoundException cfe)
		{
			cfe.printStackTrace();
			Logger.log("SQL Exception in organismTaxonomymap ",Logger.FATAL);
			Logger.log(cfe.getMessage(),Logger.DEBUG);
		}
	}
	
	public static void main(String args[])
	{
		
//		String basedir= "D:/Eclipse/workspace/caFEServer/Datfiles";
//		String filetoParse = "Hs.data.gz";
//		String outputFile = "D:/sachin.txt";
		
		try
		{
			//DBManager dbInterface = DBManager.getInstance();
			Logger.log("Inside main method of unigene external parser ", Logger.INFO);
			String config_file = args[1];
			
			PropertiesFileHandeler configProperty = new PropertiesFileHandeler(config_file);
			
			String basedir=configProperty.getValue("BASEDIR");
			String fileToParse = configProperty.getValue("INPUTFILE");
			String outputFile = configProperty.getValue("OUTPUTFILE");
			
			String fileName = basedir + FILESEP + "Config" + FILESEP + Constants.serverPropertiesFile;
			PropertiesFileHandeler pfh = new PropertiesFileHandeler(fileName);
			
			String dbUser = pfh.getValue(Constants.DATABASE_USERNAME).trim();
			String dbPwd = pfh.getValue(Constants.DATABASE_PASSWORD).trim();
				
			String driverName = pfh.getValue(Constants.DATABASE_DRIVER).trim();
			String dbURL = pfh.getValue(Constants.DATABASE_URL).trim();		
			
			//dbInterface.connect(driverName,dbURL,dbUser,dbPwd);
			
			//dbInterface.populateOrgaismTaxonomyMap();
			
			UniGeneParser ugp = new UniGeneParser(fileToParse,basedir,outputFile);
			
			ugp.populateOrgaismTaxonomyMap(driverName,dbURL,dbUser,dbPwd);
			ugp.populateAbbreviatedOrganismMap();
			ugp.open();
			ugp.parse();		
			FileWriter outputWriter = new FileWriter(basedir+FILESEP+outputFile);
			//outputWriter.write(UNIGENE_TABLE+ "."+fileToParse+"\n");
			outputWriter.write(UNIGENE_ENTREZ_TABLE+ "."+fileToParse+"\n");
			outputWriter.flush();
			outputWriter.close();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Logger.log("Exception in UGParser: " + e.getMessage(),Logger.FATAL);
			System.out.println("Exception in UGParser: " + e.getMessage());
		}
	}

}
