/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java com.dataminer.server.parser.CaArrayInterface</p> 
 */

package com.dataminer.server.parser;
import java.util.HashMap;
import com.dataminer.server.exception.FatalException;
import com.dataminer.server.ftp.FileInfo;
import com.dataminer.server.globals.Constants;
import com.dataminer.server.globals.Variables;
import com.dataminer.server.jobmanager.DPQueue;
import com.dataminer.server.log.Logger;

import gov.nih.nci.common.search.Directable;
import gov.nih.nci.common.search.session.SecureSession;
import gov.nih.nci.common.search.session.SecureSessionFactory;
import gov.nih.nci.mageom.domain.ArrayDesign.ArrayDesign;
import gov.nih.nci.mageom.domain.ArrayDesign.CompositeGroup; 
import gov.nih.nci.mageom.domain.BioSequence.BioSequence;
import gov.nih.nci.mageom.domain.BioSequence.impl.BioSequenceImpl;
import gov.nih.nci.mageom.domain.Description.DatabaseEntry;
import gov.nih.nci.mageom.domain.Description.Description;
import gov.nih.nci.mageom.domain.Description.OntologyEntry;
import gov.nih.nci.mageom.domain.DesignElement.CompositeSequence;
import gov.nih.nci.mageom.search.SearchCriteriaFactory;
import gov.nih.nci.mageom.search.ArrayDesign.ArrayDesignSearchCriteria;

/**
 * This class is an iterface to connect to cArray database and retrive array 
 * details for all as well as specified identifier. If you are fetching information
 * for all identifier then it will check whether that identifier information is 
 * already there. If so then it will skip that array design
 * This class allows user to configure the user name,password and url for the 
 * caArray database and connect to that database. It takes the array identifier
 * information from the command file. If * is specified at the place of array
 * identifiers then it will fetch details for all array designs.
 * @author Madhurima Bhattacharjee
 * @version 1.0
 */
public class CaArrayInterface extends ChipInfoParser
{
	/** Session object to connect to caArray database*/
	SecureSession m_session;
	HashMap hmConfigtokens;	
	
	
	/**
	 * Constructor method 
	 * @param fileToParse Information of the file to be parsed
	 * @param filesParsed Queue of Parsed files
	 */
	public CaArrayInterface(FileInfo fileToParse,DPQueue filesParsed)
	{
		super(fileToParse,filesParsed);
		hmConfigtokens = new HashMap();
	}
	
	/**
	 * parses the chip data.It will be passed the Array identifier as filename.Later
	 * the filename will be set to contain unique-autogenerated filename which will
	 * be used later for creating datafiles.
	 * @return FatalException paring or db insert error
	 */
	public void parse(FileInfo file) throws FatalException 
	{
	    /** CaArrayInterface deals with single file only. So just pick up the first file name from the list of files*/
	    String fileName = (String) file.getFiles().firstElement();
	    
		String arrayIdentifier = fileName;
		
		/** for CaArray the Identifier may have characters which will invalidate 
		 * it from making it a file name,hence below manipulation is done.*/			
		fileName = "CaArray" + System.currentTimeMillis();
		
		Logger.log(" CaArray::parsing started ",Logger.INFO);
		
		/** fileToPrase is set to new value which is temperorality created filename 
		* using timestamp.Previous fileName is used as arrayIdentifier for data.*/
		m_fileToParse = file;
		
		initTables();
		Logger.log("init table over",Logger.INFO);
		
		createRecords();
		Logger.log("create record over ",Logger.INFO);
		
		createFileWriters(fileName);
		Logger.log("create file writers over ",Logger.INFO);
		
		writeMETADATA();
		Logger.log("Write metadata over ",Logger.INFO);
		
		/** caArray conection parameters should be set correctly in the server.properties file for 
		 * connecting to remote caArray database. If they are not found then execution will not continue 
		 * with caArray parsing	 */
		String dbName = (String)Variables.serverProperties.get(Constants.caArrayDatabaseUrl);
		String userName = (String)Variables.serverProperties.get(Constants.caArrayUserName);
		String pwd = (String)Variables.serverProperties.get(Constants.caArraypassword);
		
		if((null == dbName) || (null == userName) || (null == pwd))
		{
			Logger.log("username,password and caArray database URL not correctly set in server.properties ",Logger.FATAL);
			Logger.log("Can not continue with caArray parsing",Logger.FATAL);
		}
		else
		{
			Logger.log("connecting to CaArray database: " + dbName ,Logger.DEBUG);
			Logger.log("connecting to CaArray using username/password as " + userName + "/" + pwd,Logger.DEBUG);
			boolean success = Connect(dbName,userName,pwd);
			
			if (true == success)
			{
				try
				{  		
					/** here fileName refers to Array Identifier which will be used to obtain the array 
					 * details when fetching data from caArray database */
					GetArrayDetails(arrayIdentifier);
					
				}
				catch(Exception e)
				{
					Logger.log("Exception" + e,Logger.INFO);
				}
				finally
				{
					Logger.log("getArrayDetails completed",Logger.INFO);
				}
				/** Below function will end the session*/
				Disconnect(); 
			}
		}
	}
	
	/**
	 * This method sets connection with the caArray database and returns true if
	 * connection is set, otherwise returns false. 
	 * @param      username(username to connect to the caArray datbase)
	 * @param      password (password to connect to the caArray database)
	 * @return     true if session of connection with a caArray database is set, otherwise false.
	 */
	public boolean Connect(String dbUrl,String username, String password) 
	{
		boolean success = false;
		try
		{
			m_session = SecureSessionFactory.defaultSecureSession();
			((Directable)m_session).direct(dbUrl);
			success = m_session.start(username,password);
			
		}  
		catch(Exception e)
		{
			Logger.log("Exception while connecting to caArray data source : " + e.getMessage(), Logger.WARNING);
		}
		return success;
	}
	
	/**
	 * This method gets the probeset and accession details and writes the chipinformation
	 * record based on the information obtained.  
	 * @param      Identifier which forms the search criteria.		 
	 * @return     Vector of ArrayResult objectS which consists of probeset identifier,
	 * accession number and description.
	 */
	public void GetArrayDetails(String identifier)throws Exception
	{   
		Logger.log("Get Array Details entered ",Logger.DEBUG);
		/** add the record corresponding to this array identifier in chiptypes
		* and get its corresponding chiptype id which will be used later.*/
		int chipTypeId;
		String organism = "";
		
		/** Add Search criteria*/
		ArrayDesignSearchCriteria adsc = (ArrayDesignSearchCriteria) SearchCriteriaFactory.newSearchCriteria(ArrayDesign.class.getName());
		
		adsc.setSessionId(m_session.getSessionId());
		/** Array identifier which represents the Array design or it can be * indicating that information
		 * about all the Array designs should be fetched from the caArray database	 */
		adsc.setIdentifier(identifier);	  
		ArrayDesign[] designs = (ArrayDesign[]) adsc.search().getResultSet();
		
		Logger.log("Design Length ",Logger.DEBUG);
		if(designs.length > 0) /**  Take first array into account*/
		{
			/** If there are nultiple Array designs selected using the current Array identifier then
			 * loop through the designs[] array to get properties and chipinformation from all Array designs 
			 */
			for(int arrayNum = 0; arrayNum < designs.length; arrayNum++ )
			{
				/** Get composite groups*/
				Logger.log(" Design : getName " + designs[arrayNum].getName(),Logger.DEBUG);
				Logger.log(" Design : identifier " + designs[arrayNum].getIdentifier(),Logger.DEBUG);
				/** decide whether the chipname already exists in the chiptypes table if not then fetch and 
				/* populate data of that array type*/
				
				/** query to select count(*) from chipinformation where chipname = curr name. If > 1 then chip 
				* information has already been populated from caArray database so skip it*/
				String QueryCheckChip = "SELECT COUNT(*) FROM " + Constants.chipTableName + "," +
				Constants.chipTypesTableName + " WHERE CIN_CHIPTYPEID = CTY_CHIPTYPEID AND CTY_CHIPNAME = '" + 
				designs[arrayNum].getName() + "'";
				
				int recCnt = m_dbManager.execQuery(QueryCheckChip);
				Logger.log("Count records of this type in chipinformation " + recCnt,Logger.DEBUG);
				/** if information related to current array is already there in the table then don't get it,
				* else get its related information*/
				if(0 == recCnt)
				{
					
					Description desc[] = designs[arrayNum].getDescriptions();
					Logger.log(" desc length "  + desc.length,Logger.DEBUG);
					/** Read the organism information from the Array design -> Ontology Entry for species*/
					if(desc.length > 0)
					{
						Logger.log(desc[0].getText(),Logger.DEBUG);
						OntologyEntry oe[] = desc[0].getAnnotations();
						Logger.log("OntologyEntry length " + oe.length,Logger.DEBUG);
						int entryCnt = oe.length;
						while( entryCnt > 0)
						{
							Logger.log("category " + oe[entryCnt-1].getCategory(),Logger.DEBUG);
							Logger.log("value " + oe[entryCnt-1].getValue(),Logger.DEBUG);
							if(oe[entryCnt-1].getCategory()!=null)
							{
								if(oe[entryCnt-1].getCategory().toString().equalsIgnoreCase(Constants.SPECIES))
								{
									organism = oe[entryCnt-1].getValue().toString();
									break;
								}
							}
							entryCnt--;
						}
					}
					
					
					/** Array design name will be taken as chipname*/
					chipTypeId = m_dbManager.addChipName(designs[arrayNum].getName(),organism);
					/** above method takes chipname and organism(species) to add record into chiptypes table*/
					Logger.log("Adding entry to Chiptypes table complete",Logger.DEBUG);
					CompositeGroup[] compGroups = designs[arrayNum].getCompositeGroups();
					Logger.log("CompGr[] Length " + compGroups.length,Logger.DEBUG);
					for (int j = 0; j < compGroups.length; j++)
					{
						CompositeSequence[] cs = compGroups[j].getCompositeSequences();
						/** For every composite sequence*/
						if(cs != null)
						{
							Logger.log("CompSeq[] Length " + cs.length,Logger.DEBUG);
							for (int k = 0; k <cs.length; k++)
							{
								ArrayResult probeDetails = new ArrayResult();
								if(probeDetails != null)
								{
									probeDetails.m_probesetName = cs[k].getName();	                    
									BioSequence[] bioSeq =  cs[k].getBiologicalCharacteristics();
									if(bioSeq != null)
									{
										for(int b=0; b<bioSeq.length; b++)
										{
											BioSequenceImpl seq = (BioSequenceImpl )bioSeq[b];	                        
											DatabaseEntry[] dbEntry = seq.getSequenceDatabases();
											//Logger.log("dbEntry length " + dbEntry.length,Logger.DEBUG);
											for(int d=0; d<dbEntry.length; d++)
												probeDetails.m_accession = dbEntry[d].getAccession();
										}
										baseRecord.fields[0].append(chipTypeId);
										baseRecord.fields[1].append(probeDetails.m_probesetName);
										baseRecord.fields[2].append(probeDetails.m_accession);
										writeRecordToDb(Constants.chipTableName,baseRecord);
										resetRecords();
									}
								}
							}
						}
					}
				}
			}
			
		}
	}
	
	/**
	 * End the secure session opened for caArray database
	 */
	public void  Disconnect()
	{
		m_session.end();
	}
	
	
	/**
	 * Class to store chiplibrary details
	 * @author Pratibha Dhok 
	 * @version 1.0
	 */
	class ArrayResult
	{
		public String m_probesetName;
		public  String m_accession;
		public  String m_description;
		
		public ArrayResult()
		{
			
		}
	}	
}



