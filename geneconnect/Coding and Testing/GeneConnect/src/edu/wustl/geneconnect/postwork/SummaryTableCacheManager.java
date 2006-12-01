/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.postwork.SummaryTableCacheManager</p> 
 */

package edu.wustl.geneconnect.postwork;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.dataminer.server.log.Logger;

import edu.wustl.geneconnect.GeneConnectServerConstants;
import edu.wustl.geneconnect.domain.Gene;
import edu.wustl.geneconnect.domain.MessengerRNA;
import edu.wustl.geneconnect.domain.Protein;

/**
 * This class handles caching for genomic data required during summary table creation.
 * @author mahesh_nalkande
 * @version 1.0
 */
public class SummaryTableCacheManager implements GeneConnectServerConstants
{

	/** SummaryTableCacheManager as a singleton class */
	private static SummaryTableCacheManager cacheManagerInstance;

	/** 
	 * Method to return instance of this class
	 * @return SummaryTableCacheManager Returns object of this class
	 */
	public static SummaryTableCacheManager getInstance()
	{
		if (cacheManagerInstance == null)
		{
			cacheManagerInstance = new SummaryTableCacheManager();

		}
		return cacheManagerInstance;
	}

	/**
	 * Default constructor, not accessible to all as it is a singleton class
	 */
	private SummaryTableCacheManager()
	{
		super();
	}

	/** map to hold cached gene records**/
	private Map geneRecords = null;

	/** map to hold cached mrna records**/
	private Map mrnaRecords = null;

	/** map to hold cached protein records**/
	private Map proteinRecords = null;

	/** map to hold cached genomic identifier set records**/
	//private Map genomicIdentifierSetRecords = new HashMap(500000);

	/** map to hold cached set-ont mapping records**/
	//private Map setOntRecords = new HashMap(500000);

	/**
	 * This method adds the given gene record to cache.
	 * @param gene Gene record to be cached
	 * @param geneId Id of the gene.
	 */
	public void addGeneRecordToCache(Gene gene, long geneId)
	{
		geneRecords.put(prepareGeneKey(gene), new Long(geneId));
		return;
	}

	/**
	 * Returns the id of requested gene.
	 * @param gene Gene record whose id is required
	 * @return Id of Gene
	 */
	public Object getGeneIdFromCache(Gene gene)
	{
		return geneRecords.get(prepareGeneKey(gene));
	}

	/**
	 * This method adds the given mrna record to cache.
	 * @param mrna record to be cached
	 * @param mrnaId Id of mrna
	 */
	public void addMrnaRecordToCache(MessengerRNA mrna, long mrnaId)
	{
		mrnaRecords.put(prepareMrnaKey(mrna), new Long(mrnaId));
		return;
	}

	/**
	 * Returns the id of requested mrna
	 * @param mrna Mrna record whose id is required
	 * @return Id of mrna
	 */
	public Object getMrnaIdFromCache(MessengerRNA mrna)
	{
		return mrnaRecords.get(prepareMrnaKey(mrna));
	}

	/**
	 * This method adds the given protein record to cache.
	 * @param protein record to be cached.
	 * @param proteinId Id of protein.
	 */
	public void addProteinRecordToCache(Protein protein, long proteinId)
	{
		proteinRecords.put(prepareProteinKey(protein), new Long(proteinId));
		return;
	}

	/**
	 * Returns the id of requested protein.
	 * @param protein Protein record whose id is required.
	 * @return Id of protein.
	 */
	public Object getProteinIdFromCache(Protein protein)
	{
		return proteinRecords.get(prepareProteinKey(protein));
	}

	public String prepareGeneKey(Gene gene)
	{
		String key = "";

		if (gene.getEnsemblGeneId() != null)
			key = key + gene.getEnsemblGeneId();
		key = key + FIELD_DELIMITER;

		if (gene.getEntrezGeneId() != null)
			key = key + gene.getEntrezGeneId();
		key = key + FIELD_DELIMITER;

		if (gene.getUnigeneClusterId() != null)
			key = key + gene.getUnigeneClusterId();
		key = key + FIELD_DELIMITER;

		return key;
	}

	public String prepareMrnaKey(MessengerRNA messengerRNA)
	{
		String key = "";

		if (messengerRNA.getEnsemblTranscriptId() != null)
			key = key + messengerRNA.getEnsemblTranscriptId();
		key = key + FIELD_DELIMITER;

		if (messengerRNA.getGenbankAccession() != null)
			key = key + messengerRNA.getGenbankAccession();
		key = key + FIELD_DELIMITER;

		if (messengerRNA.getRefseqId() != null)
			key = key + messengerRNA.getRefseqId();
		key = key + FIELD_DELIMITER;

		return key;
	}

	public String prepareProteinKey(Protein protein)
	{
		String key = "";

		if (protein.getEnsemblPeptideId() != null)
			key = key + protein.getEnsemblPeptideId();
		key = key + FIELD_DELIMITER;

		if (protein.getRefseqId() != null)
			key = key + protein.getRefseqId();
		key = key + FIELD_DELIMITER;

		if (protein.getUniprotkbPrimaryAccession() != null)
			key = key + protein.getUniprotkbPrimaryAccession();
		key = key + FIELD_DELIMITER;

		if (protein.getGenbankAccession() != null)
			key = key + protein.getGenbankAccession();
		key = key + FIELD_DELIMITER;
		return key;
	}

	public void prepareGeneCache()
	{
		Logger.log("Preparing Gene Cache...", Logger.DEBUG);
		Logger.log("Free memory available : " + Runtime.getRuntime().freeMemory(), Logger.DEBUG);
		geneRecords = new HashMap(GENE_CACHE_INITIAL_SIZE);
		try
		{
			BufferedReader geneDataFile = new BufferedReader(new FileReader(GENE_DATA_FILENAME));
			String currentRecord = "";
			int recordIndex;
			geneDataFile.readLine();
			geneDataFile.readLine();
			while ((currentRecord = geneDataFile.readLine()) != null)
			{
				recordIndex = currentRecord.indexOf(FIELD_DELIMITER);
				geneRecords.put(currentRecord.substring(recordIndex + 1), new Long(currentRecord.substring(
						0, recordIndex)));
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		Logger.log("Total No. of cached Gene records : " + geneRecords.size(), Logger.DEBUG);
		Logger.log("Free memory available after caching : " + Runtime.getRuntime().freeMemory(), Logger.DEBUG);
	}

	public void prepareMrnaCache()
	{
		Logger.log("Preparing mrna Cache...", Logger.DEBUG);
		Logger.log("Free memory available : " + Runtime.getRuntime().freeMemory(), Logger.DEBUG);
		mrnaRecords = new HashMap(MRNA_CACHE_INITIAL_SIZE);
		try
		{
			BufferedReader mrnaDataFile = new BufferedReader(new FileReader(MRNA_DATA_FILENAME));
			String currentRecord = "";
			int recordIndex;
			mrnaDataFile.readLine();
			mrnaDataFile.readLine();
			while ((currentRecord = mrnaDataFile.readLine()) != null)
			{
				recordIndex = currentRecord.indexOf(FIELD_DELIMITER);
				mrnaRecords.put(currentRecord.substring(recordIndex + 1), new Long(currentRecord.substring(
						0, recordIndex)));
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		Logger.log("Total No. of cached Mrna records : " + mrnaRecords.size(), Logger.DEBUG);
		Logger.log("Free memory available after caching : " + Runtime.getRuntime().freeMemory(), Logger.DEBUG);
	}

	public void prepareProteinCache()
	{
		Logger.log("Preparing protein Cache...", Logger.DEBUG);
		Logger.log("Free memory available : " + Runtime.getRuntime().freeMemory(), Logger.DEBUG);
		
		proteinRecords = new HashMap(PROTEIN_CACHE_INITIAL_SIZE);
		try
		{
			BufferedReader proteinDataFile = new BufferedReader(new FileReader(PROTEIN_DATA_FILENAME));
			String currentRecord = "";
			int recordIndex;
			proteinDataFile.readLine();
			proteinDataFile.readLine();
			while ((currentRecord = proteinDataFile.readLine()) != null)
			{
				recordIndex = currentRecord.indexOf(FIELD_DELIMITER);
				proteinRecords.put(currentRecord.substring(recordIndex + 1), new Long(currentRecord
						.substring(0, recordIndex)));
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		Logger.log("Total No. of cached Protein records : " + proteinRecords.size(), Logger.DEBUG);  
		Logger.log("Free memory available after caching : " + Runtime.getRuntime().freeMemory(), Logger.DEBUG);		
	}

	public void resetGeneCache()
	{
		geneRecords = null;
		Runtime.getRuntime().gc();
	}

	public void resetMrnaCache()
	{
		mrnaRecords = null;
		Runtime.getRuntime().gc();
	}

	public void resetProteinCache()
	{
		proteinRecords = null;
		Runtime.getRuntime().gc();
	}
	
	public void resetAllCache()
	{
		Logger.log("Clearing gene, mrna and protein Cache...", Logger.DEBUG);
		Logger.log("Free memory available : " + Runtime.getRuntime().freeMemory(), Logger.DEBUG);
		
		if (geneRecords != null)
			geneRecords.clear();

		if (mrnaRecords != null)
			mrnaRecords.clear();

		if (proteinRecords != null)
			proteinRecords.clear();
		
		geneRecords = null;
		mrnaRecords = null;
		proteinRecords = null;
		Runtime.getRuntime().gc();
		
		Logger.log("Free memory available after clean up : " + Runtime.getRuntime().freeMemory(), Logger.DEBUG);
	}
	
	public void initializeCache()
	{
		geneRecords = new HashMap();
		mrnaRecords = new HashMap();
		proteinRecords = new HashMap();
	}

	/**
	 * This method adds the given genomic identifier set record to cache.
	 * @param geneId Gene Id 
	 * @param mrnaId Mrna Id
	 * @param proteinId Protein Id
	 * @param genomicIdentifierSetId Genomic Identifier set Id
	 */
	/*public void addGenomicIdentifierSetRecordToCache(Object geneId, Object mrnaId,
			Object proteinId, long genomicIdentifierSetId)
	{
		String key = "";
		if (geneId != null)
		{
			key = geneId.toString();
		}
		else
		{
			key = "0";
		}
		if (mrnaId != null)
		{
			key = key + "_" + mrnaId.toString();
		}
		else
		{
			key = key + "_" + "0";
		}
		if (proteinId != null)
		{
			key = key + "_" + proteinId.toString();
		}
		else
		{
			key = key + "_" + "0";
		}
		genomicIdentifierSetRecords.put(key, new Long(genomicIdentifierSetId));
		return;
	}

	*//**
	 * Returns the id of requested Genomic Identifier set.
	 * @param geneId Gene Id 
	 * @param mrnaId Mrna Id
	 * @param proteinId Protein Id
	 * @return Genomic Identifier set Id
	 *//*
	public Object getGenomicIdentifierSetIdFromCache(Object geneId, Object mrnaId, Object proteinId)
	{
		String key = "";
		if (geneId != null)
		{
			key = geneId.toString();
		}
		else
		{
			key = "0";
		}
		if (mrnaId != null)
		{
			key = key + "_" + mrnaId.toString();
		}
		else
		{
			key = key + "_" + "0";
		}
		if (proteinId != null)
		{
			key = key + "_" + proteinId.toString();
		}
		else
		{
			key = key + "_" + "0";
		}
		return genomicIdentifierSetRecords.get(key);
	}

	*//**
	 * This method adds the given set-ont mapping record to cache.
	 * @param setId Genomic Identifier set Id
	 * @param ontId Ont Id
	 *//*
	public void addSetOntRecordToCache(Long setId, String ontId)
	{
		String existingOntIds = (String) setOntRecords.get(setId);

		if (existingOntIds == null)
		{
			setOntRecords.put(setId, ontId);
		}
		else
		{
			existingOntIds = existingOntIds + "_" + ontId;
			setOntRecords.put(setId, existingOntIds);
		}
	}

	*//**
	 * Checks whether given set and ont are associated or not.
	 * @param setId Genomic Identifier set Id
	 * @param ontId Ont Id
	 * @return boolean
	 *//*
	public boolean isSetOntAssociated(Long setId, String ontId)
	{
		String associatedOntIds = (String) setOntRecords.get(setId);
		if (associatedOntIds != null)
		{
			String[] ontIds = associatedOntIds.split("_");
			for (int i = 0; i < ontIds.length; i++)
			{
				if (ontIds[i].equals(ontId))
					return true;
			}
		}
		return false;
	}*/
}