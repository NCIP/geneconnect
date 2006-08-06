/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java com.dataminer.server.parser.HomoloGeneXMLParser</p> 
 */

package com.dataminer.server.parser;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

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
 * Parser for the HomoloGene data files. This Class also serves as a parent class for Ortholog, Gene, HomologeneRec,HomologenePair 
 * classes. The primary responsibilities of this class are to parse the HomoloGene XML data files and write the parsed data
 * to files intended to be loaded in HOMOLOGENE,HOMOLOGENE_TMP,HOMOLOGENE_XML,ORTHOLOG,ORTHOLOGSTARTGENE tables. 
 * @author Anuj Tiwari
 * @version 1.0
 */
public class HomoloGeneXMLParser extends XmlParser
{
	/**
	 * One HomoloGeneEntry node lists the details of each gene in the group
	 * and later gives the details of similiarity between those genes. While giving
	 * similarity statistics between a pair of genes, only prot_gi is referred.
	 * Our goal is to find similarity between two genes referred by gene_id.
	 * The Gene type can be used to gather the details of genes
	 */
	/** Instance of Ortholog class */
	private Ortholog m_ortholog;
	/** group id for current homologene entry */
	private int m_Groupid = 0;
	/**map between ProtGI and Gene*/
	private Hashtable m_ProtGIToGeneMap;
	/** Gene object for current gene in HomoloGene record */
	private Gene m_CurrentGene;
	/** Current pair of genes in HomoloGene record statistics */
	private HomoloGenePair m_CurrentRow;
	/** List of all gene-pairs in current HomoloGene node */
	private ArrayList m_HomologenePairs;
	/** Value for current tag */
	private StringBuffer m_dataValue;
	/**current tag being parsed*/
	private static String m_currentTag = ""; 	
	/**Record representing one row in Homologene Table*/ 
	private Record m_HomoloGeneXMLRecord;
	/** Record representing one row in ortholog*/
	private Record m_OrthologRecord;
	/** Record representing one row in orthologstartgene*/
	private Record m_OrthologStartGeneRecord;
	/** Record representing one row in HOMOLOGENE_TMP table*/
	private Record m_HomoloGeneTempRecord;
	/**specified by the user; used for caluclation of ortholog groups*/
	private float m_CutOff;
	/**
	 * Inner class to hold gene details
	 * @author Anuj Tiwari
	 * @version 1.0
	 */
	class Gene
	{
		private long m_geneid;
		private long m_taxid; 
		private long m_prot_gi;
		/** Getter and Setter methods */
		public long getM_geneid() 
		{
			return m_geneid;
		}
		public void setM_geneid(long geneid) 
		{
			m_geneid = geneid;
		}
		public long getM_taxid() 
		{
			return m_taxid;
		}
		public void setM_taxid(long taxid) 
		{
			m_taxid = taxid;
		}
		public long getM_prot_gi() 
		{
			return m_prot_gi;
		}
		public void setM_prot_gi(long prot_gi) 
		{
			m_prot_gi = prot_gi;
		}
	}
	/**
	 * Inner class to hold homologene details for a pair. It includes the HomoloGene group-id, Instances of Gene class
	 * for the two genes involved in the pair, their alignment and reciprocal values.
	 * @author Anuj Tiwari
	 * @version 1.0
	 */
	class HomoloGenePair
	{
		/** Group ID of the current HomoloGene entry */
		private long m_groupid; 
		/** Gene object of the first gene in the pair */
		private Gene m_gene1;
		/** Gene object of the second gene in the pair */
		private Gene m_gene2;
		/** Alignment value for the HomoloGene pair */
		private float m_alignment;
		/** Reciprocal value for the HomoloGene pair */
		private boolean m_reciprocal;
		/** Getter and setter methods for member variables of HomoloGenePair */
		public long getM_groupid() 
		{
			return m_groupid;
		}
		public void setM_groupid(long groupid) 
		{
			m_groupid = groupid;
		}
		public Gene getM_gene1() 
		{
			return m_gene1;
		}
		public void setM_gene1(Gene gene1) 
		{
			m_gene1 = gene1;
		}
		public Gene getM_gene2() 
		{
			return m_gene2;
		}
		public void setM_gene2(Gene gene2) 
		{
			m_gene2 = gene2;
		}
		public float getM_alignment() 
		{
			return m_alignment;
		}
		public void setM_alignment(float alignment) 
		{
			m_alignment = alignment;
		}
		public boolean isM_reciprocal() 
		{
			return m_reciprocal;
		}
		public void setM_reciprocal(boolean reciprocal) 
		{
			m_reciprocal = reciprocal;
		}
	}
	
	/**
	 *  The type used to hold one homologene record; this type is used while calculating orthologs.
	 * @author Anuj Tiwari
	 * @version 1.0
	 */
	class HomologeneRec
	{
		/** Geneid of first gene in current HomoloGene statistics record*/
		private long m_geneid1;
		/** taxonomy-id of first gene in current HomoloGene statistics record*/
		private long m_taxid1;
		/** Geneid of second gene in current HomoloGene statistics record*/
		private long m_geneid2;
		/** taxonomyid of second gene in current HomoloGene statistics record*/
		private long m_taxid2;
		/** Alignment for the gene-pair in current HomoloGene statistics record*/
		private float m_alignment;
		/** Reciprocal for the gene-pair in current HomoloGene statistics record*/
		private boolean m_reciprocal;
		
		/**
		 * Method to fill homolgene related fields
		 * @param geneid1 Entrez Gene ID1
		 * @param taxid1 Taxonomy ID1
		 * @param geneid2  Entrez Gene ID2
		 * @param taxid2 Taxonomy ID2
		 * @param alignment Alignment value between Gene1 and Gene2
		 * @param reciprocal reciprocal value between Gene1 and Gene2 
		 */
		public HomologeneRec(long geneid1, long taxid1, long geneid2,
				long taxid2, float alignment, boolean reciprocal)
		{
			m_geneid1 = geneid1;
			m_taxid1 = taxid1;
			m_geneid2 = geneid2;
			m_taxid2 = taxid2;
			m_alignment = alignment;
			m_reciprocal = reciprocal;
		}
		/** Getter and setter methods for member variables of HomologeneRec class */
		public long getM_geneid1() 
		{
			return m_geneid1;
		}
		
		public void setM_geneid1(long geneid1) 
		{
			m_geneid1 = geneid1;
		}
		
		public long getM_taxid1() 
		{
			return m_taxid1;
		}
		
		public void setM_taxid1(long taxid1) 
		{
			m_taxid1 = taxid1;
		}
		
		public long getM_geneid2() 
		{
			return m_geneid2;
		}
		
		public void setM_geneid2(long geneid2) 
		{
			m_geneid2 = geneid2;
		}
		
		public long getM_taxid2() 
		{
			return m_taxid2;
		}
		
		public void setM_taxid2(long taxid2) 
		{
			m_taxid2 = taxid2;
		}
		
		public float getM_alignment() 
		{
			return m_alignment;
		}
		
		public void setM_alignment(float alignment) 
		{
			m_alignment = alignment;
		}
		
		public boolean isM_reciprocal() 
		{
			return m_reciprocal;
		}
		
		public void setM_reciprocal(boolean reciprocal) 
		{
			m_reciprocal = reciprocal;
		}
	}
	
	/**
	 * The comparator used to sort the collection of HomologeneRec
	 * order by geneid1, reciprocal, taxid2, alignment
	 */
	class HomologeneRecComparator implements Comparator
    {

        public int compare(Object o1, Object o2)
        {
            HomologeneRec rec1 = (HomologeneRec) o1;
            HomologeneRec rec2 = (HomologeneRec) o2;

            if (rec1.m_geneid1 == rec2.m_geneid1)
            {
            	if (rec1.m_taxid2 == rec2.m_taxid2)
            	{
            		if (rec1.m_reciprocal == rec2.m_reciprocal)
            		{
            			{
            				if (rec1.m_alignment > rec2.m_alignment)
            					return -1;
            				else if (rec1.m_alignment < rec2.m_alignment)
            					return 1;
            				else
            					return 0;
            			}
            		}
            		else if (rec1.m_reciprocal == true)
            		{
            			return -1;
            		}
            	}
            	else if (rec1.m_taxid1 < rec2.m_taxid2)
            	{
            		return -1;
            	}
            }
            else if (rec1.m_geneid1 < rec2.m_geneid1)
            	return -1;
            
            return 1;
            
        }
    }
	/**
	 * class to hold orthog details
	 * @author Anuj Tiwari
	 * @version 1.0
	 */
	class Ortholog
	{	
        /**
         * list is the collection of HomologeneRec tuples sorted in the 
         * the order of geneid1, reciprocal, taxid2 and alignment
         * 
         * for one locusid, true orthologs are associated first, followed by
         * false orthologs. there can be more than one false ortholog record 
         * between two organisms and in such case the one with greater alignment
         * is picked. 
         */
        public void computeOrthologs(ArrayList list)
                throws SQLException, FatalException
        {
            if (list.size() == 0)
                return; //do nothing
            
            long prev_geneid1 = -1;
            long prev_taxid2 = -1;
            long startGeneid = -1;
            
            System.gc();
            HashSet currTgroup = new HashSet();
            HashSet currFgroup = new HashSet();           
            HashMap orthoGroupToId = new HashMap();
            
            for (int i = 0; i < list.size(); i++)
            {
            	/**
            	 * List of HomologeneRec is passed to this function which is sorted based on geneid1,taxid2,
            	 * reciprocal and alignment. We consider qroups formed based on geneid1s and then we from each
            	 * geneid1 we create one True and one False ortholog group.
            	 */
                HomologeneRec rec = (HomologeneRec) list.get(i);              
                long geneid1 = rec.m_geneid1;
                long geneid2 = rec.m_geneid2;
                long taxid1 = rec.m_taxid1;
                long taxid2 = rec.m_taxid2;
                boolean reciprocal = rec.m_reciprocal;
                float alignment = rec.m_alignment;
                
                /**
                 * When the geneid1 changes i.e is different from the previous one then the group has changed. Here 
                 * group means one geneid1 which will have two one T and one F ortholog groups corresponding to
                 * it. If groups changes then we need to 1. Strore the T and F groups formed from the previous geneid1
                 * and initialise new T and F groups for current run
                 */
                if(geneid1 != prev_geneid1)
                {
                	/**
                	 * If we are entering this loop for the first time then there is no info about the 
                	 * group(geneid1 group) which needs to be stored in ortholog table. Hennce we skip this loop 
                	 * below for the firt time
                	 */
                	if(prev_geneid1 != -1)
                	{
                		/**Old group has completed since geneid1 changed at this point. Now create two ortholog 
                		 * groups for the current geneid1 one T and one F ortholog group. These two groups will
                		 * be stored in a map. If such group is already there in the map it will not be stored.
                		 * But its ortholog id will be extracted and associated with the current geneid1 in the
                		 * orthologstartlocus table entry. Same way to check and add T and F groups
                		 */
                		long orthologGroupId;
                		Object trueGroup = orthoGroupToId.get(currTgroup);
                		/**
                		 * If group similar to True group is already there then just use its ortholog id as it is.
                		 * Else create new ortholog group and add it to ortholog table. Also add the T and F groups
                		 * corresponding to the current geneid1 in the orthologstartlocus table
                		 */
                		if(null != trueGroup)
                		{
	                		orthologGroupId = ((Long)trueGroup).longValue();
                		}
                		else
                		{
                			orthologGroupId = Variables.orthologId;
                			orthoGroupToId.put(currTgroup,new Long(Variables.orthologId));
                			insertIntoOrthologTable(orthologGroupId,currTgroup);
                			Variables.orthologId++;
                		}
                   		insertIntoOrthologStartLocusTable(orthologGroupId,startGeneid,true);
                   		
                   		Object falseGroup = orthoGroupToId.get(currFgroup);
                		if(null != falseGroup)
                		{
	                		orthologGroupId = ((Long)falseGroup).longValue();
                		}
                		else
                		{
                			orthologGroupId = Variables.orthologId;
                			orthoGroupToId.put(currFgroup,new Long(Variables.orthologId));
                			insertIntoOrthologTable(orthologGroupId,currFgroup);
                			Variables.orthologId++;
                		}
                		insertIntoOrthologStartLocusTable(orthologGroupId,startGeneid,false);
                		/**
                		 * Here we are done with the current T and F groups and they can be cleared. The same
                		 * group will store the geneids for the next T and F groups formed for next geneid1
                		 */
                		currTgroup = new HashSet();
                		currFgroup = new HashSet();

                	}
                	/**
                	 * Current loop is entered when you change the geneid1 this will be each time you start with new
                	 * geneid1 range from which you will be creating the T and F ortholog groups.
                	 */
                	startGeneid = geneid1;
                	/** Startgeneid will be marked only when new groups of geneid1 starts so that this will be used
                	 * later to put entry in the orthologstartlocus table
                	 */
                	/**
                	 * If reciprocal is true then it means you need to add the geneid1 and geneid2 in the current
                	 * T and F group. But if reciprocal is F then just add geneid1 in the T group and geneid1 
                	 * and geneid2 in the F group. 
                	 */
                	if(true == reciprocal)
                	{
                		currTgroup.add(new Long(geneid1));
                		currTgroup.add(new Long(geneid2));
                	}
                	else 
                	{
                		currTgroup.add(new Long(geneid1));
                	}
                	currFgroup.add(new Long(geneid1));
                	currFgroup.add(new Long(geneid2));
                	insertIntoHomologeneTempTable(geneid1,getOrg(taxid1),geneid2,getOrg(taxid2),alignment,reciprocal);                	
                }
                else
                {
                	/**
                	 * Every time except when geneid1 in the homologene record list changes you enter this else 
                	 * section. Here you are adding T and F entries to the respective groups formed for the 
                	 * current geneid1. From the list we take only one record per taxid in the T and F groups.
                	 * Each time we change the taxid2 with the next record being read. When the taxid2 is different
                	 * from the previoud then only the record will go in the T and F group based on the reciprocal
                	 * value. If it is True then it goes in T and F both groups but if it is False then it goes only
                	 * in the F group.
                	 */
                	if(prev_taxid2 != taxid2)
                	{
                		if(true == reciprocal)
                		{
                			currTgroup.add(new Long(geneid2));
                		}
                		currFgroup.add(new Long(geneid2));
                		insertIntoHomologeneTempTable(geneid1,getOrg(taxid1),geneid2,getOrg(taxid2),alignment,reciprocal);
                	}
                }
                prev_geneid1 = geneid1;
                prev_taxid2 = taxid2;
            }
    		/**
    		 * The last T and F groups formed for the last geneid1 in the sorted list of the homologene
    		 * records needs to be written to the ortholog and startlocus tables similar to what we do when 
    		 * the geneid1 changes. The below code is same as the one above executed when geneid1 != prev_geneid1
    		 */
    		long orthologGroupId;
    		
    		if(null != orthoGroupToId.get(currTgroup))
    		{
        		orthologGroupId = ((Long)orthoGroupToId.get(currTgroup)).longValue();
    		}
    		else
    		{
    			orthologGroupId = Variables.orthologId;
    			orthoGroupToId.put(currTgroup,new Long(Variables.orthologId));
    			insertIntoOrthologTable(orthologGroupId,currTgroup);
    			Variables.orthologId++;
    		}
       		insertIntoOrthologStartLocusTable(orthologGroupId,startGeneid,true);
    		if(null != orthoGroupToId.get(currFgroup))
    		{
        		orthologGroupId = ((Long)orthoGroupToId.get(currFgroup)).longValue();
    		}
    		else
    		{
    			orthologGroupId = Variables.orthologId;
    			orthoGroupToId.put(currFgroup,new Long(Variables.orthologId));
    			insertIntoOrthologTable(orthologGroupId,currFgroup);
    			Variables.orthologId++;
    		}
    		insertIntoOrthologStartLocusTable(orthologGroupId,startGeneid,false);
    		
            /** set the ortholog id to be used when populating single gene ortholog groups.*/
            Variables.tempOrthologId = Variables.orthologId;

        }
		/**
		 * list is the collection of HomologeneRec tuples sorted in the 
		 * the order of geneid1, reciprocal, taxid2 and alignment
		 * for one locusid, true orthologs are associated first, fllowed by
		 * false orthologs. there can be more than one false ortholog record 
		 * between two organisms and in such case the one with greater alignment
		 * is picked. 
		 * @param list ArrayList containing homologene pairs in current node of the data file
		 * @throws SQLException, FatalException
		 */
		public void computerOrthologs(ArrayList list)
		throws SQLException, FatalException
		{
			if (0 == list.size())
				return; 
			long curr_geneid1;
			long prev_geneid1 = -1;
			long geneid2;
			boolean curr_reciprocal;
			boolean prev_reciprocal = false;
			long curr_taxid2;
			long prev_taxid2 = -1;
			long start_locusid = -1;
			float alignment;
			long taxid1;
			/** map between ortholog group to ortholog id*/
			HashMap orthoGroupToId = new HashMap(); 
			/**list of ortholog groups*/
			ArrayList groups = new ArrayList(); 
			/**set of locusids in the current ortholog group*/
			HashSet curr_group = new HashSet();
			for (int i = 0; i < list.size(); i++)
			{
				HomologeneRec rec = (HomologeneRec) list.get(i);
				curr_geneid1 = rec.getM_geneid1();
				geneid2 = rec.getM_geneid2();
				curr_taxid2 = rec.getM_taxid2();
				curr_reciprocal = rec.isM_reciprocal();
				taxid1 = rec.getM_taxid1();
				alignment = rec.getM_alignment();
				
				if (-1 == prev_geneid1)
				{
					prev_geneid1 = curr_geneid1;
					start_locusid = curr_geneid1;
					curr_group.add(new Long(curr_geneid1));
					curr_group.add(new Long(geneid2));
					prev_taxid2 = curr_taxid2;
					prev_reciprocal = curr_reciprocal;
					insertIntoHomologeneTempTable(curr_geneid1, getOrg(taxid1),
							geneid2, getOrg(curr_taxid2), alignment,
							curr_reciprocal);
					continue;
				}
				/**indicates the end of the current group*/
				if ((curr_geneid1 != prev_geneid1) || (curr_reciprocal != prev_reciprocal)) 
				{
					boolean found = false;
					/**find if the curr_group exists in groups*/
					HashSet g;
					for (int j = 0; j < groups.size(); j++)
					{
						g = (HashSet) groups.get(j);
						/**New group will match one of the existing groups only if their sizes are equal**/
						if(curr_group.size() == g.size())
						{
							/**Check if the new group is already present **/
							if (curr_group.containsAll(g))
							{
								found = true; /**group exists*/
							
								long id = ((Long) orthoGroupToId.get(g)).longValue(); 
								/**find the ortholog id of the found group
								 * add corresponding row to orthologstarlocus table; no need to add to ortholog table*/
								insertIntoOrthologStartLocusTable(id,
									start_locusid, prev_reciprocal);
								curr_group.clear();
								break;
							}
						}
					}
					/** If group does not already exists */
					if (!found)
					{
						groups.add(curr_group);
						orthoGroupToId.put(curr_group, new Long(Variables.orthologId));
						/** insert into ortholog as well as orthologstartlocus tables*/
						insertIntoOrthologTable(Variables.orthologId, curr_group);
						insertIntoOrthologStartLocusTable(Variables.orthologId,
								start_locusid, prev_reciprocal);
						g = curr_group;
						Variables.orthologId++;
						curr_group = new HashSet();
						/**loose ortholog group exploration started*/
						if (curr_geneid1 == prev_geneid1)
						{
							for (Iterator itr = g.iterator(); itr.hasNext();)
								curr_group.add(itr.next());
						}
					}
					start_locusid = curr_geneid1;
					curr_group.add(new Long(curr_geneid1));
					curr_group.add(new Long(geneid2));
					insertIntoHomologeneTempTable(curr_geneid1, getOrg(taxid1),
							geneid2, getOrg(curr_taxid2), alignment,
							curr_reciprocal);
				}
				else if (curr_taxid2 != prev_taxid2)
				{
					/**in loose ortholog groups, if there are more than one record between a pair of organisms,
					 * consider the first(see the sorted order) and ignore the rest;*/
					curr_group.add(new Long(geneid2));
					insertIntoHomologeneTempTable(curr_geneid1, getOrg(taxid1),
							geneid2, getOrg(curr_taxid2), alignment,
							curr_reciprocal);
				}
				prev_geneid1 = curr_geneid1;
				prev_reciprocal = curr_reciprocal;
				prev_taxid2 = curr_taxid2;
			}
			
			boolean found = false;
			/** find if the curr_group exists in groups*/
			HashSet g;
			for (int j = 0; j < groups.size(); j++)
			{
				g = (HashSet) groups.get(j);
				if (curr_group.containsAll(g) && g.containsAll(curr_group))
				{
					found = true;
					long id = ((Long) orthoGroupToId.get(g)).longValue();
					insertIntoOrthologStartLocusTable(id, start_locusid,
							prev_reciprocal);
					break;
				}
			}
			if (!found)
			{
				insertIntoOrthologTable(Variables.orthologId, curr_group);
				insertIntoOrthologStartLocusTable(Variables.orthologId, start_locusid,
						prev_reciprocal);
				Variables.orthologId++;
			}
			/** set the ortholog id to be used when populating single gene ortholog groups.*/
			Variables.tempOrthologId =  Variables.orthologId;
			
			System.gc();
			
		}
		/**
		 * Method to insert details into ortholog table
		 * @param orthologId Ortholog id 
		 * @param locusids set of Locuslink Ids
		 * @throws SQLException Throws exception if error during insert
		 * @throws FatalException Throws exception if error during insert
		 */ 
		public void insertIntoOrthologTable(long orthologId,HashSet locusids) throws SQLException, FatalException
		{
			for (Iterator itr = locusids.iterator(); itr.hasNext();)
			{
				m_OrthologRecord.fields[0].append("" + orthologId);
				m_OrthologRecord.fields[1].append(""
						+ ((Long) itr.next()).longValue());
				try
				{
					writeRecordToDb(Variables.orthologTableName,m_OrthologRecord);
				}
				catch(InsertException ie)
				{
					Logger.log("Error during inserting record in file (Homologene)" + ie.getMessage(), Logger.WARNING);
				}
				m_OrthologRecord.resetAllFields();
			}
		}
		/**
		 * Method to insert record into ortholog Start locus table
		 * @param orthologId ortholog ID
		 * @param start_locusid start locus id
		 * @param trueHomologues boolean to show if its true ortholog
		 * @throws SQLException Throws exception if error during insert
		 * @throws FatalException Throws exception if error during insert
		 */
		public void insertIntoOrthologStartLocusTable(long orthologId,
				long start_locusid, boolean trueHomologues)
		throws SQLException, FatalException
		{
			
			m_OrthologStartGeneRecord.fields[0].append("" + orthologId);
			m_OrthologStartGeneRecord.fields[1].append("" + start_locusid);
			m_OrthologStartGeneRecord.fields[2].append(""
					+ ((true == trueHomologues) ? "T" : "F"));
			
			try
			{
				writeRecordToDb(Variables.orthologStartGeneName,m_OrthologStartGeneRecord);
			}
			catch(InsertException ie)
			{
				Logger.log("Error during inserting record in file (Homologene) " + ie.getMessage(), Logger.WARNING);
			}
			m_OrthologStartGeneRecord.resetAllFields();
			
		}
		/**
		 * 
		 * @param geneid1 Entrez Gene ID1
		 * @param org1 Organism 1
		 * @param geneid2 ntrez Gene ID2
		 * @param org2 Organism 2
		 * @param alignment alignment value between Gene ID1 and Gene ID2
		 * @param reciprocal Reciprocal value between Gene ID1 and Gene ID2
		 * @throws SQLException Throws exception if error during insert
		 * @throws FatalException Throws exception if error during insert
		 */
		public void insertIntoHomologeneTempTable(long geneid1,
				String org1, long geneid2, String org2, float alignment,
				boolean reciprocal) throws SQLException, FatalException
				{
			/**ignore ugid fields for time being; those will be filled later
			 from locus_unigene table */
			m_HomoloGeneTempRecord.fields[0].append("" + geneid1);
			m_HomoloGeneTempRecord.fields[1].append("" + org1);
			m_HomoloGeneTempRecord.fields[2].append("" + geneid2);
			m_HomoloGeneTempRecord.fields[3].append("" + org2);
			m_HomoloGeneTempRecord.fields[4].append("" + alignment);
			m_HomoloGeneTempRecord.fields[5].append(""
					+ ((true == reciprocal) ? "T" : "F"));
			try
			{
				writeRecordToDb(Variables.homologeneTempTableName,m_HomoloGeneTempRecord);
			}
			catch(InsertException ie)
			{
				Logger.log("Error during inserting record in file (Homologene) " + ie.getMessage(), Logger.WARNING);
			}
			m_HomoloGeneTempRecord.resetAllFields();
			
				}
		/**
		 * Get organism for this taxonomy ID
		 * @param taxid Taxonomy ID
		 * @return organism value for this taxonomy ID
		 */
		public String getOrg(long taxid)
		{
			return (String) Variables.hmTaxidLocalId.get("" + taxid);
		}
	}
	
	/**
	 * Cosntructor method
	 * @param fileToParse Information of the File to parse
	 * @param filesParsed List of parsed files
	 * @param xmlReader XMLReader object for this file
	 */
	public HomoloGeneXMLParser(FileInfo fileToParse,DPQueue filesParsed, XMLReader xmlReader)
	{       
		super(fileToParse,filesParsed);
		m_ortholog = new Ortholog();
		m_ProtGIToGeneMap = new Hashtable();
		m_HomologenePairs = new ArrayList();
		m_dataValue = new StringBuffer();
		m_CutOff = Float.parseFloat((String)Variables.serverProperties.get(Constants.CUT_OFF));
		Logger.log("Homologene Config File read Set Cut off " + m_CutOff, Logger.INFO);
	}
	
	
	/**
	 * Initialize the Homologne Base Table
	 * @exception FatalException Throws exception if error during initialization of tables
	 */
	private void initializeTables() throws FatalException
	{
		m_dbManager.initTable(Variables.homologeneXMLTableName);
		/**initialize ortholog and orthlogstartlocus tables*/
		Logger.log("ortholog init table entered",Logger.INFO);
		m_dbManager.initTable(Variables.orthologTableName);
		m_dbManager.initTable(Variables.orthologStartGeneName);
		m_dbManager.initTable(Variables.homologeneTempTableName);
		Logger.log("ortholog init table exit",Logger.INFO);
	}
	
	/**
	 * Method to create homologene record
	 */
	private void createRecords()
	{
		/**create a record object to store data*/
		m_HomoloGeneXMLRecord = new Record(m_dbManager
				.noOfColumns(Variables.homologeneXMLTableName), m_dbManager
				.getPrecision(Variables.homologeneXMLTableName));
		m_OrthologRecord = new Record(m_dbManager.noOfColumns(Variables.orthologTableName), 
				m_dbManager.getPrecision(Variables.orthologTableName));
		m_OrthologStartGeneRecord= new Record(m_dbManager.noOfColumns(Variables.orthologStartGeneName), 
				m_dbManager.getPrecision(Variables.orthologStartGeneName));
		m_HomoloGeneTempRecord = new Record(m_dbManager.noOfColumns(Variables.homologeneTempTableName), 
				m_dbManager.getPrecision(Variables.homologeneTempTableName));
		
	}
	
	/**
	 * Main parse routine that parses the input files and inserts records into
	 * Homologene table in the database.
	 * @param file Information of the file to parse
	 * @exception FatalException Throws exception if error during parsing
	 */
	
	public void parse(FileInfo file) throws FatalException
	{
	    /** HomoloGeneXMLParser parser deals with single file only. So just pick up the first file name from the list of files*/
	    String fileName = (String) file.getFiles().firstElement();
	    
		Logger.log(" Homologene::parsing started ", Logger.INFO);
		
		/**initialize the Homologene database tables*/
		initializeTables();
		Logger.log(" initialise tables over",Logger.INFO);
		/** Create  record objects for Homologene tables to store data*/
		createRecords();
		Logger.log("create record over",Logger.INFO);
		createFileWriters();
		Logger.log("Create file writers over",Logger.INFO);
		
		/** Write MetaData in the Files*/
		writeMETADATA();
		Logger.log("write metadata over",Logger.INFO);
		
		try
		{
			InputSource inp = createInputStream(fileName);
			Logger.log("Input Stream Created",Logger.DEBUG);
			/**Create an input Source Object.*/
			inp.setSystemId(Constants.CWD);
			/** Actual Parsing Starts*/
			Logger.log("XML parsing started",Logger.DEBUG);
			xmlReader.parse(inp);
			/** Add the homologene revision history as the last modified date
			 * of homologene source file. There is no explicite tag in the homologene
			 * source file which gives this information */
			Variables.homologeneRevisionHistory = getFileRevisionHistory(fileName);
		}
		catch (SAXException sax)
		{
			Logger.log(" Error (SAX) occuring while Parsing: " + sax.getMessage(), Logger.WARNING);
		}
		catch (FileNotFoundException fnf)
		{
			Logger.log(" Error (File Not Found) occuring while Parsing: ",
					Logger.INFO);
			throw new FatalException(fnf.getMessage());
		}
		catch (IOException iox)
		{
			Logger.log(" Error (IO) occuring while Parsing: " + iox.getMessage(), Logger.INFO);
			throw new FatalException(iox.getMessage());
		}
		finally
		{
			Logger.log(" Homologene::parsing over ", Logger.INFO);
		}
		
		Logger.log("Homologene Parsing Done",Logger.INFO);
		
	}
	
	/**
	 * This function is fired whenever the parser encounters the starting of XML element 
	 */
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException
			{
		m_currentTag = qName;
		String eName = localName;
		if ("".equals(eName))
		{
			eName = qName; 
		}
		if (eName.equalsIgnoreCase(Constants.HOMOLOGENEENTRYTAG))
		{
			/** clear the map; we no longer need the previous group details*/
			if (!m_ProtGIToGeneMap.isEmpty())
				m_ProtGIToGeneMap.clear();
			/** clear homologene records too*/
			m_HomologenePairs.clear();
			System.gc();
		}
		else if (eName.equalsIgnoreCase(Constants.GENETAG))
		{
			m_dataValue.setLength(0);
			m_CurrentGene = new Gene();
		}
		else if (eName.equalsIgnoreCase(Constants.GENEGENEIDTAG) || eName.equalsIgnoreCase(Constants.GENETAXIDTAG) || eName.equalsIgnoreCase(Constants.GENEPROTGIIDTAG) ||
				eName.equalsIgnoreCase(Constants.STATSGI1TAG) || eName.equalsIgnoreCase(Constants.STATSGI2TAG) || eName.equalsIgnoreCase(Constants.ALIGNMENTTAG))
		{
			m_dataValue.setLength(0);
			m_currentTag = eName;
		}
		
		else if (eName.equalsIgnoreCase(Constants.HOMOLOGENEENTRYDISTANCETAG))
		{
			m_dataValue.setLength(0);
		}
		else if (eName.equalsIgnoreCase(Constants.STATSTAG))
		{
			m_dataValue.setLength(0);
			m_CurrentRow = new HomoloGenePair();
		}
		else if (eName.equalsIgnoreCase(Constants.STATSRECIPBESTTAG))
		{
			if (true == atts.getValue("value").equalsIgnoreCase("true"))
				m_CurrentRow.setM_reciprocal(true);
			else
				m_CurrentRow.setM_reciprocal(false);
		}
			}
	
	/**
	 * All the chunk of data between the start and end tags
	 * calls the characters function.
	 */
	public void characters(char buf[], int offset, int len) throws SAXException
	{
		/**Trim the String to avoid extra white spaces.*/
		String s = new String(buf, offset, len).trim();
		if ((s.indexOf("\n") > -1) || (s.indexOf("\r\n") > -1)
				|| (s.indexOf("\r")) > -1)
			s = "";
		if (0 != s.length())
		{
			if (m_currentTag.equalsIgnoreCase(Constants.HOMOLOGENEENTRYHGIDTAG))
			{
				try
				{
					m_Groupid = Integer.parseInt(s);
				}
				catch (NumberFormatException nfe)
				{
					Logger.log("NumberFormatException " + nfe.getMessage(),
							Logger.WARNING);
				}
			}
			else if (m_currentTag.equalsIgnoreCase(Constants.GENEGENEIDTAG) || m_currentTag.equalsIgnoreCase(Constants.GENETAXIDTAG) || m_currentTag.equalsIgnoreCase(Constants.GENEPROTGIIDTAG)
					|| m_currentTag.equalsIgnoreCase(Constants.STATSGI1TAG) || m_currentTag.equalsIgnoreCase(Constants.STATSGI2TAG) || m_currentTag.equalsIgnoreCase(Constants.ALIGNMENTTAG))
			{
				m_dataValue.append(s);
			}
		}
	}
	
	/**
	 * A very important thing to note is that All the WritetoDbs must be done at the end of the Tag and hence
	 * Must be present in this function.Besides the StringBuffer in the 
	 */
	public void endElement(String namespaceURI, String localName, String qName)
	throws SAXException
	{
		m_currentTag = qName;
		/** Check if it is the start of a new HomoloGene node */
		if (m_currentTag.equalsIgnoreCase(Constants.HOMOLOGENEENTRYTAG))
		{
			/**put data into Database for each element in the list of HomoloGenePair*/
			
			ArrayList homologeneRecs = new ArrayList();
			for (int i = 0; i < m_HomologenePairs.size(); i++)
			{
				HomoloGenePair h1r = (HomoloGenePair) m_HomologenePairs.get(i);
				try
				{
					String taxStr1 = "";
					String taxStr2 = "";
					float alignmentVal = 1 - h1r.getM_alignment();
					boolean reciprocal = h1r.isM_reciprocal();
					/** adding both loose and strict ortholog groups and their reflexive entries */
					if ((alignmentVal > m_CutOff || (true == reciprocal)) && (h1r.getM_gene1().getM_taxid() != h1r.getM_gene2().getM_taxid()))
					{
						HomologeneRec rec1 = new HomologeneRec(
								h1r.getM_gene1().getM_geneid(), h1r.getM_gene1().getM_taxid(),
								h1r.getM_gene2().getM_geneid(), h1r.getM_gene2().getM_taxid(),
								alignmentVal, reciprocal);
						homologeneRecs.add(rec1);
						/** Enter symmetric records for true groups */

						HomologeneRec rec2 = new HomologeneRec(
								h1r.getM_gene2().getM_geneid(), h1r.getM_gene2().getM_taxid(),
								h1r.getM_gene1().getM_geneid(), h1r.getM_gene1().getM_taxid(),
								alignmentVal, reciprocal);
						homologeneRecs.add(rec2);
					}
					/** Get the local taxonomyid for the two genes */
					taxStr1 = (String)Variables.hmTaxidLocalId.get("" + h1r.getM_gene1().getM_taxid());
					taxStr2 = (String)Variables.hmTaxidLocalId.get("" + h1r.getM_gene2().getM_taxid());
					/** Populate records for Homologene_XML table */
					m_HomoloGeneXMLRecord.fields[0].append("" + h1r.getM_groupid());
					m_HomoloGeneXMLRecord.fields[1].append("" + h1r.getM_gene1().getM_geneid());
					m_HomoloGeneXMLRecord.fields[2].append(taxStr1);
					m_HomoloGeneXMLRecord.fields[3].append("" + h1r.getM_gene2().getM_geneid());
					m_HomoloGeneXMLRecord.fields[4].append(taxStr2);
					m_HomoloGeneXMLRecord.fields[5].append("" + alignmentVal);
					m_HomoloGeneXMLRecord.fields[6].append(""
							+ ((true == reciprocal) ? "T" : "F"));
					writeRecordToDb(Variables.homologeneXMLTableName,
							m_HomoloGeneXMLRecord);
					
					m_HomoloGeneXMLRecord.resetAllFields();
					/** Write the symmetric entry for HomoloGene_XML table */
					m_HomoloGeneXMLRecord.fields[0].append("" + h1r.getM_groupid() );
					m_HomoloGeneXMLRecord.fields[1].append("" + h1r.getM_gene2().getM_geneid());
					m_HomoloGeneXMLRecord.fields[2].append("" + taxStr2);
					m_HomoloGeneXMLRecord.fields[3].append("" + h1r.getM_gene1().getM_geneid());
					m_HomoloGeneXMLRecord.fields[4].append("" + taxStr1);
					m_HomoloGeneXMLRecord.fields[5].append("" + alignmentVal);
					m_HomoloGeneXMLRecord.fields[6].append(""
							+ ((true == reciprocal) ? "T" : "F"));
					writeRecordToDb(Variables.homologeneXMLTableName,
							m_HomoloGeneXMLRecord);
					
					m_HomoloGeneXMLRecord.resetAllFields();
				}
				catch (FatalException fe)
				{
					Logger.log("unable to insert following recod",
							Logger.WARNING);
					Logger.log(h1r.getM_groupid()  + "  " + h1r.getM_gene1().getM_geneid() + "  "
							+ h1r.getM_gene1().getM_taxid() + "  " + h1r.getM_gene2().getM_geneid() + "  "
							+ h1r.getM_gene2().getM_taxid() + "  " + h1r.getM_alignment(),
							Logger.WARNING);
				}
				catch (InsertException ie)
				{
					Logger.log("unable to insert following record",
							Logger.WARNING);
					Logger.log(h1r.getM_groupid()  + "  " + h1r.getM_gene1().getM_geneid() + "  "
							+ h1r.getM_gene1().getM_taxid() + "  " + h1r.getM_gene2().getM_geneid() + "  "
							+ h1r.getM_gene2().getM_taxid() + "  " + h1r.getM_alignment(),
							Logger.WARNING);
				}
			}
			/** Sort the homologene-pair records for the current HomoloGene Entry according to geneid1, reciprocal, taxid2 */
			Collections.sort((java.util.List) homologeneRecs,
					new HomologeneRecComparator());
			try
			{
				/** Compute the orthologs using HomoloGene-pair records in the current HomoloGene node */ 
				m_ortholog.computeOrthologs(homologeneRecs);
			}
			catch (SQLException e)
			{
				Logger.log(e.getMessage(), Logger.FATAL);
			}
			catch (FatalException e)
			{
				Logger.log(e.getMessage(), Logger.FATAL);
			}
		}
		else if (m_currentTag.equalsIgnoreCase(Constants.GENETAG))
		{
			m_ProtGIToGeneMap.put(new Long(m_CurrentGene.getM_prot_gi()), m_CurrentGene);
		}
		else if (m_currentTag.equalsIgnoreCase(Constants.GENEGENEIDTAG))
		{
			m_CurrentGene.setM_geneid(Long.parseLong(m_dataValue.toString()));
		}
		else if (m_currentTag.equalsIgnoreCase(Constants.GENETAXIDTAG))
		{
			m_CurrentGene.setM_taxid(Long.parseLong(m_dataValue.toString()));
		}
		else if (m_currentTag.equalsIgnoreCase(Constants.GENEPROTGIIDTAG))
		{
			m_CurrentGene.setM_prot_gi(Long.parseLong(m_dataValue.toString()));
		}
		else if (m_currentTag.equalsIgnoreCase(Constants.STATSTAG))
		{
			m_CurrentRow.setM_groupid(m_Groupid);
			if ((m_CurrentRow.getM_gene1() != null) && (m_CurrentRow.getM_gene2() != null))
				m_HomologenePairs.add(m_CurrentRow);
			else
				Logger.log("skipped inserting because gene object was null",
						Logger.WARNING);
		}
		else if (m_currentTag.equalsIgnoreCase(Constants.STATSGI1TAG))
		{
			if (false == m_ProtGIToGeneMap.containsKey(new Long(m_dataValue
					.toString())))
			{
				Logger.log("did not find gi1 for " + m_dataValue.toString(),
						Logger.WARNING);
			}
			else
				m_CurrentRow.setM_gene1((Gene) m_ProtGIToGeneMap.get(new Long(
						m_dataValue.toString())));
			
		}
		else if (m_currentTag.equalsIgnoreCase(Constants.STATSGI2TAG))
		{
			if (false == m_ProtGIToGeneMap.containsKey(new Long(m_dataValue
					.toString())))
			{
				Logger.log("did not find gi2 for " + m_dataValue.toString(),
						Logger.WARNING);
			}
			else
				m_CurrentRow.setM_gene2((Gene) m_ProtGIToGeneMap.get(new Long(
						m_dataValue.toString())));
		}
		else if (m_currentTag.equalsIgnoreCase(Constants.ALIGNMENTTAG))
		{
			m_CurrentRow.setM_alignment(Float.parseFloat(m_dataValue.toString()));
		}
	}
	
	/**
	 * Ignore white spaces
	 */
	public void ignorableWhitespace(char[] ch, int start, int length)
	throws SAXException
	{
	}
	
	public void error(SAXParseException exception) throws SAXException
	{
		Logger.log(
				" Error in Parsing (Normal Error) " + exception.getMessage(),
				Logger.WARNING);
	}
	/**
	 * Handle fatal error
	 */
	public void fatalError(SAXParseException exception) throws SAXException
	{
		Logger.log(" Error in Parsing (Fatal) " + exception.getMessage(),
				Logger.WARNING);
	}
	/**
	 * Here we are fooling the parser to believe that we do have a valid
	 * DTD while we merely have a byte Stream. 
	 */
	public InputSource resolveEntity(String publicId, String systemId)
	{
		/**Here we are fooling the parser to believe that we do have a valid
		 * DTD while we merely have a byte Stream.*/
		String dummyXml = "<?xml version='1.0' encoding='UTF-8'?>";
		ByteArrayInputStream bis = new ByteArrayInputStream(dummyXml.getBytes());
		InputSource is = new InputSource(bis);
		return is;
	}
	/**
	 * Method to create file writer objects
	 */
	private void createFileWriters()
	{
		try
		{
			m_fileWriterHashTable.put(Variables.homologeneXMLTableName,new FileWriter( Variables.homologeneXMLTableName + "." + m_fileToParse ));
			m_fileWriterHashTable.put(Variables.orthologStartGeneName,new FileWriter(Variables.orthologStartGeneName + "." + m_fileToParse));
			m_fileWriterHashTable.put(Variables.orthologTableName,new FileWriter(Variables.orthologTableName + "." + m_fileToParse));
			m_fileWriterHashTable.put(Variables.homologeneTempTableName,new FileWriter(Variables.homologeneTempTableName + "." + m_fileToParse));
		}
		catch(IOException ie)
		{
			Logger.log("Cannot Create File Writers " + m_fileToParse + ie.getMessage(), Logger.WARNING);
			Variables.errorCount++;
		}
	}  
}


