
package edu.wustl.geneconnect.bizlogic;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.impl.CriteriaImpl;

import edu.wustl.geneconnect.GenomicIdentifierSolution;
import edu.wustl.geneconnect.domain.ConsensusIdentifierData;
import edu.wustl.geneconnect.domain.Gene;
import edu.wustl.geneconnect.domain.GenomicIdentifier;
import edu.wustl.geneconnect.domain.GenomicIdentifierSet;
import edu.wustl.geneconnect.domain.LinkType;
import edu.wustl.geneconnect.domain.MessengerRNA;
import edu.wustl.geneconnect.domain.OrderOfNodeTraversal;
import edu.wustl.geneconnect.domain.Protein;
import edu.wustl.geneconnect.utility.Constants;
import edu.wustl.geneconnect.utility.MetadataManager;
import edu.wustl.geneconnect.utility.Utility;
import gov.nih.nci.system.dao.impl.orm.ORMDAOImpl;

/**
 * This class provides the method for calculating confidence / frequency. 
 * @author sachin_lale
 *
 */
public class GCBizLogic
{

	private static Logger log = Logger.getLogger(GCBizLogic.class.getName());

	Map ontCountForSet = new HashMap();

	public GCBizLogic()
	{

	}

	/**
	 * Calculate the total number of result which contains GenomicIdentifierSet object.
	 * If ONT is selected then total score is equlas to no. of result set
	 * @param rs -  Result set got after executing user query 
	 * @return float totaScore
	 */
	float calculateTotalScore(List rs, List ontList)
	{
		float totalScore = 0.0f;

		/**
		 * Calculate Total score
		 */
		//		if (rs.size() > 0)
		//		{
		//			totalScore = totalScore + rs.size();
		//		}
		if (ontList.size() > 0 && rs.size() > 0)
		{
			totalScore = totalScore + rs.size();
			//			for(int i=0;i<rs.size();i++)
			//			{
			//				GenomicIdentifierSet set = (GenomicIdentifierSet)rs.get(i);
			//				set.setConfidenceScore(new Float(1));
			//			}
		}
		else
		{
			for (int i = 0; i < rs.size(); i++)
			{
				GenomicIdentifierSet set = (GenomicIdentifierSet) rs.get(i);
				Collection ontColl = set.getOrderOfNodeTraversalCollection();
				if (ontColl != null)
				{
					totalScore = totalScore + ontColl.size();
					set.setConfidenceScore(new Float(ontColl.size()));

				}
			}
		}
		log.info("TotalScore of Solution :" + totalScore);
		System.out.println("TotalScore of Solution :" + totalScore);
		return totalScore;
	}

	/**
	 * This method prepare the result list for further calcultion.
	 * Checks for NULL values for all selected Data source.
	 * If a row(SET) contain NULL value for all selected input/output Data source. then remove 
	 * SET from list
	 * @param rs
	 * @throws Exception
	 */
	void prepareResult(List rs, List selectedOutputDataSourceList) throws Exception
	{
		List dsl = null;

		int countForNullGenomicId = 0;
		for (Iterator iter = rs.iterator(); iter.hasNext();)
		{
			GenomicIdentifierSet set = (GenomicIdentifierSet) iter.next();
			Long setID = new Long(set.getId());

			Gene gene = set.getGene();
			Protein protein = set.getProtein();
			MessengerRNA mrna = set.getMessengerRNA();
			countForNullGenomicId = 0;
			dsl = MetadataManager.getAttibutes(Constants.CLASS, "Gene");
			/**
			 * Check if  selected output data source of Gene object contains NULL value.
			 * Iterate over each dat source of Gene(got from MetaData) and check if the 
			 * data source is select as output. If yes then  get tah value and if value is NULL then
			 * Increment the counter (int countForNullGenomicId).  
			 */
			for (int j = 0; j < dsl.size(); j++)
			{
				Map map = (Map) dsl.get(j);
				String dataSourceName = (String) map.get(Constants.DATASOURCE_NAME);
				//System.out.println("dataSourceName to set false : "+dataSourceName);

				if (Utility.listContainValue(dataSourceName, selectedOutputDataSourceList))
				{
					String dataSourceAttribute = MetadataManager.getDataSourceAttribute(
							Constants.DATASOURCE_NAME, dataSourceName, Constants.ATTRIBUTE);
					String temp = dataSourceAttribute.substring(0, 1).toUpperCase();
					String methodName = "get" + temp
							+ dataSourceAttribute.substring(1, dataSourceAttribute.length());
					//	System.out.println("in OP DS LISt : "+methodName); 
					Method method = Gene.class.getDeclaredMethod(methodName, null);
					Object value = method.invoke(gene, null);
					//System.out.println("dataSourceName :" +dataSourceName +"--"+value);
					if ((value == null) || (value.toString().equalsIgnoreCase("NULL")))
					{
						//System.out.println("NULL VALUE");
						log.info("NULL value got for " + methodName);
						countForNullGenomicId++;
					}
				}
			}
			/**
			 * Check if  selected output data source of MessengerRNA object contains NULL value.
			 * Iterate over each dat source of MessengerRNA(got from MetaData) and check if the 
			 * data source is select as output. If yes then  get tah value and if value is NULL then
			 * Increment the counter (int countForNullGenomicId).  
			 */
			dsl = MetadataManager.getAttibutes(Constants.CLASS, "MessengerRNA");
			for (int j = 0; j < dsl.size(); j++)
			{
				Map map = (Map) dsl.get(j);
				String dataSourceName = (String) map.get(Constants.DATASOURCE_NAME);
				//System.out.println("dataSourceName to set false : "+dataSourceName);

				if (Utility.listContainValue(dataSourceName, selectedOutputDataSourceList))
				{
					String dataSourceAttribute = MetadataManager.getDataSourceAttribute(
							Constants.DATASOURCE_NAME, dataSourceName, Constants.ATTRIBUTE);
					String temp = dataSourceAttribute.substring(0, 1).toUpperCase();
					String methodName = "get" + temp
							+ dataSourceAttribute.substring(1, dataSourceAttribute.length());
					//System.out.println("in OP DS LISt : "+methodName); 
					Method method = MessengerRNA.class.getDeclaredMethod(methodName, null);
					Object value = method.invoke(mrna, null);
					//System.out.println("dataSourceName :" +dataSourceName +"--"+value.toString());
					if ((value == null) || (value.toString().equalsIgnoreCase("NULL")))
					{
						//System.out.println("NULL VALUE");
						log.info("NULL value got for " + methodName);
						countForNullGenomicId++;
					}
				}
			}
			/**
			 * Check if  selected output data source of Protein object contains NULL value.
			 * Iterate over each dat source of Protein(got from MetaData) and check if the 
			 * data source is select as output. If yes then  get tah value and if value is NULL then
			 * Increment the counter (int countForNullGenomicId).  
			 */
			dsl = MetadataManager.getAttibutes(Constants.CLASS, "Protein");
			for (int j = 0; j < dsl.size(); j++)
			{
				Map map = (Map) dsl.get(j);
				String dataSourceName = (String) map.get(Constants.DATASOURCE_NAME);
				//System.out.println("dataSourceName to set false : "+dataSourceName);
				if (Utility.listContainValue(dataSourceName, selectedOutputDataSourceList))
				{
					String dataSourceAttribute = MetadataManager.getDataSourceAttribute(
							Constants.DATASOURCE_NAME, dataSourceName, Constants.ATTRIBUTE);
					String temp = dataSourceAttribute.substring(0, 1).toUpperCase();
					String methodName = "get" + temp
							+ dataSourceAttribute.substring(1, dataSourceAttribute.length());
					//System.out.println("in OP DS LISt : "+methodName); 
					Method method = Protein.class.getDeclaredMethod(methodName, null);
					Object value = method.invoke(protein, null);
					//System.out.println("dataSourceName :" +dataSourceName +"--"+value);
					if ((value == null) || (value.toString().equalsIgnoreCase("NULL")))
					{
						//System.out.println("NULL VALUE");
						log.info("NULL value got for " + methodName);
						countForNullGenomicId++;
					}
				}
			}
			/** 
			 * If the value counter countForNullGenomicId matches the size of select output data spource list
			 * this means values all select o/p data source of this GenomicIdentifierSet iobejct is NULL.
			 * So remove this SET from teh result list. 
			 *   
			 */
			log.info("CHECK for NULL : " + countForNullGenomicId + "---"
					+ selectedOutputDataSourceList.size());
			if (countForNullGenomicId > 0
					&& countForNullGenomicId == selectedOutputDataSourceList.size())
			{

				log.info("All NULL values ---remove SETID: " + setID);
				iter.remove();
			}
		}
	}

	/**
	 * @author sachin_lale
	 * Business Logic to caluculate frequency and filter the result
	 * Frequency is calculated as count of genomicIdentifier throughout the set divided by Total_No_of_ONTs 
	 * throughout the set. 
	 * @param rs
	 * @param freqList
	 */
	void processFrequency(List rs, List freqList, float totalScore, boolean isFreqGreaterThanEqual)
			throws Exception
	{
		GenomicIdentifierSolution sol = new GenomicIdentifierSolution();

		//System.out.println("freqList.size() :" +freqList.size());
		try
		{
			List dsl = null;

			/**
			 * Map storing the frequency of each genomicIdentifier.as 
			 * key = genomicId and value = count(no of occurnce of genomicId in the result list)  
			 */
			HashMap frequencyMap = new HashMap();

			log.info("rs.size(): " + rs.size());
			if (rs.size() == 0)
				return;

			/**
			 * Loop over the GenomicIdentifierSet and count each genomic identifer appeared throughout 
			 * the result set 
			 * adn stored it in frequencyMap as genomicIdentifer(Key) and 
			 * count,DataSourceName,GenomicIdentifierSet Ids(Values as ArrayLIst)
			 */
			for (int i = 0; i < rs.size(); i++)
			{

				GenomicIdentifierSet set = (GenomicIdentifierSet) rs.get(i);

				Collection ontCollection = set.getOrderOfNodeTraversalCollection();
				int noOfOntForThisSet = 1;
				if (ontCollection != null)
					noOfOntForThisSet = ontCollection.size();

				Collection dataColl = new HashSet();
				set.setConsensusIdentifierDataCollection(dataColl);
				set.setGenomicIdentifierSolution(sol);
				Long setID = new Long(set.getId());

				/**
				 * Get all attributes and  Loop over each attribute of Gene class
				 */
				Gene gene = set.getGene();
				if (gene != null)
				{
					dsl = MetadataManager.getAttibutes(Constants.CLASS, "Gene");
					for (int j = 0; j < dsl.size(); j++)
					{
						Map map = (Map) dsl.get(j);
						String dataSourceName = (String) map.get(Constants.DATASOURCE_NAME);
						String dataSourceAttribute = (String) map.get(Constants.ATTRIBUTE);
						String temp = dataSourceAttribute.substring(0, 1).toUpperCase();
						String methodName = "get" + temp
								+ dataSourceAttribute.substring(1, dataSourceAttribute.length());
						//log.info("Method invoke Gene: " + methodName);
						Method method = Gene.class.getDeclaredMethod(methodName, null);
						/**
						 * Get genomicId of the cuurent Gene attribute 
						 * ensemblGeneId,entrezGeneId,unigeneClusterId
						 */
						Object value = method.invoke(gene, null);
						log.info("SAC value " + value + "---" + dataSourceName);
						/**
						 * if value is already in frequncyMap trhe increment the count by no.of ONT else add new entry
						 */
						if (value == null)
						{
							String keyForNull = dataSourceName + "_NULL";
							List dsList = (ArrayList) frequencyMap.get(keyForNull);
							if (dsList == null)
							{
								dsList = new ArrayList();
								//dsList.add(new Integer(1));
								dsList.add(new Integer(noOfOntForThisSet));
								dsList.add(map);
								dsList.add(setID);
								frequencyMap.put(keyForNull, dsList);
							}
							else
							{
								Integer val = (Integer) dsList.get(0);
								int count = val.intValue();
								count += noOfOntForThisSet;
								//count++;
								dsList.set(0, new Integer(count));
								dsList.add(setID);
							}
						}
						else if (value != null && frequencyMap.get(value.toString()) == null)
						{
							List dsList = new ArrayList();
							//dsList.add(new Integer(1));
							dsList.add(new Integer(noOfOntForThisSet));
							dsList.add(map);
							dsList.add(setID);
							frequencyMap.put(value.toString(), dsList);
						}
						else if (value != null)
						{
							List dsList = (ArrayList) frequencyMap.get(value.toString());
							Integer val = (Integer) dsList.get(0);
							int count = val.intValue();
							count += noOfOntForThisSet;
							//count++;
							dsList.set(0, new Integer(count));
							dsList.add(setID);
						}
					}
				}
				/**
				 * Get all attributes and  Loop over each attribute of MessengerRNA class
				 */
				MessengerRNA mrna = set.getMessengerRNA();
				if (mrna != null)
				{
					dsl = MetadataManager.getAttibutes(Constants.CLASS, "MessengerRNA");
					for (int j = 0; j < dsl.size(); j++)
					{
						Map map = (Map) dsl.get(j);
						String dataSourceName = (String) map.get(Constants.DATASOURCE_NAME);
						String dataSourceAttribute = (String) map.get(Constants.ATTRIBUTE);
						String temp = dataSourceAttribute.substring(0, 1).toUpperCase();
						String methodName = "get" + temp
								+ dataSourceAttribute.substring(1, dataSourceAttribute.length());
						//log.info("Method invoke RNA: " + methodName);
						Method method = MessengerRNA.class.getDeclaredMethod(methodName, null);
						/**
						 * Get genomicId of the cuurent MessengerRNA attribute 
						 * ensemblTranscriptId,genbankAccession,refseqId
						 */
						Object value = method.invoke(mrna, null);
						//System.out.println("SAC value " + value + "---"+dataSourceName);
						log.info("SAC value " + value + "---" + dataSourceName);
						/**
						 * if value is already in frequncyMap trhe increment the count by no.of ONT else add new entry
						 */
						if (value == null)
						{
							String keyForNull = dataSourceName + "_NULL";
							List dsList = (ArrayList) frequencyMap.get(keyForNull);
							if (dsList == null)
							{
								dsList = new ArrayList();
								//dsList.add(new Integer(1));
								dsList.add(new Integer(noOfOntForThisSet));
								dsList.add(map);
								dsList.add(setID);
								frequencyMap.put(keyForNull, dsList);
							}
							else
							{
								Integer val = (Integer) dsList.get(0);
								int count = val.intValue();
								count += noOfOntForThisSet;
								//count++;
								dsList.set(0, new Integer(count));
								dsList.add(setID);
							}
						}
						else if (value != null && frequencyMap.get(value.toString()) == null)
						{
							List dsList = new ArrayList();
							//dsList.add(new Integer(1));
							dsList.add(new Integer(noOfOntForThisSet));
							dsList.add(map);
							dsList.add(setID);
							frequencyMap.put(value.toString(), dsList);
						}
						else if (value != null)
						{
							List dsList = (ArrayList) frequencyMap.get(value.toString());
							Integer val = (Integer) dsList.get(0);
							int count = val.intValue();
							count += noOfOntForThisSet;
							//count++;
							dsList.set(0, new Integer(count));
							dsList.add(setID);
						}
					}
				}
				/**
				 * Get all attributes and  Loop over each attribute of Protein class
				 */
				Protein protein = set.getProtein();
				if (protein != null)
				{
					dsl = MetadataManager.getAttibutes(Constants.CLASS, "Protein");

					for (int j = 0; j < dsl.size(); j++)
					{
						Map map = (Map) dsl.get(j);
						String dataSourceName = (String) map.get(Constants.DATASOURCE_NAME);
						String dataSourceAttribute = (String) map.get(Constants.ATTRIBUTE);
						String temp = dataSourceAttribute.substring(0, 1).toUpperCase();
						String methodName = "get" + temp
								+ dataSourceAttribute.substring(1, dataSourceAttribute.length());
						//log.info("Method invoke Protein: " + methodName);
						Method method = Protein.class.getDeclaredMethod(methodName, null);
						/**
						 * Get genomicId of the cuurent Protein attribute 
						 * ensemblPeptideId,refseqId,uniprotkbPrimaryAccession,genbankAccession
						 */
						Object value = method.invoke(protein, null);
						//System.out.println("SAC value " + value + "---"+dataSourceName);
						log.info("SAC value " + value + "---" + dataSourceName);
						/**
						 * if value is already in frequncyMap trhe increment the count by no.of ONT else add new entry
						 */
						if (value == null)
						{
							String keyForNull = dataSourceName + "_NULL";
							List dsList = (ArrayList) frequencyMap.get(keyForNull);
							if (dsList == null)
							{
								dsList = new ArrayList();
								//dsList.add(new Integer(1));
								dsList.add(new Integer(noOfOntForThisSet));
								dsList.add(map);
								dsList.add(setID);
								frequencyMap.put(keyForNull, dsList);
							}
							else
							{
								Integer val = (Integer) dsList.get(0);
								int count = val.intValue();
								count += noOfOntForThisSet;
								//count++;
								dsList.set(0, new Integer(count));
								dsList.add(setID);
							}
						}
						else if (value != null && frequencyMap.get(value.toString()) == null)
						{
							List dsList = new ArrayList();
							//dsList.add(new Integer(1));
							dsList.add(new Integer(noOfOntForThisSet));
							dsList.add(map);
							dsList.add(setID);
							frequencyMap.put(value.toString(), dsList);
						}
						else if (value != null)
						{
							List dsList = (ArrayList) frequencyMap.get(value.toString());
							Integer val = (Integer) dsList.get(0);
							int count = val.intValue();
							count += noOfOntForThisSet;
							//count++;
							dsList.set(0, new Integer(count));
							dsList.add(setID);
						}

					}
				}
			}

			/**
			 * Construct ConsensusData object and calculate frequency as genomicIdentifier_Count/Total_No_of_ONTs 
			 * 
			 */
			Collection consensusDataCollection = new ArrayList();
			Set keySet = frequencyMap.keySet();
			for (Iterator iter = keySet.iterator(); iter.hasNext();)
			{
				boolean isSetToRemove = false;
				int setIdCounter = 0;
				String key = (String) iter.next();
				//				if (key.toString().equalsIgnoreCase("NULL"))
				//					continue;
				List dsList = (List) frequencyMap.get(key);
				StringBuffer dsClassName = new StringBuffer("edu.wustl.geneconnect.domain.");
				Map tempMap = (Map) dsList.get(1);
				dsClassName.append((String) tempMap.get(Constants.GENOMIC_IDENTIFIER_CLASS));
				StringBuffer dataSourceType = new StringBuffer();
				dataSourceType.append((String) tempMap.get(Constants.TYPE));
				Integer count = (Integer) dsList.get(0);
				//System.out.println("KEY AND COUNT : " + key + "====" + count+"===="+dsClassName.toString());
				log.info("KEY AND COUNT : " + key + "====" + count + "===="
						+ dsClassName.toString());
				float frequency = count.floatValue() / totalScore;
				log.info("Genomic ID & Freq : " + key + "====" + frequency);
				/**
				 * Loop over predictes on frequency given by user
				 * And set boolean value indicatin GenomicIdentifierSet object to remove 
				 * from list which is not satifsfying the condition  
				 */
				for (int i = 0; i < freqList.size(); i++)
				{
					GCCriteria fq = (GCCriteria) freqList.get(i);
					if (fq.getDataSource().equalsIgnoreCase(
							(String) tempMap.get(Constants.DATASOURCE_NAME)))
					{
						if (isFreqGreaterThanEqual && !(frequency >= fq.getPredicate()))
						{
							isSetToRemove = true;
						}
						else if (!(frequency > fq.getPredicate()))
						{
							isSetToRemove = true;
						}
					}
				}
				/**
				 * Construct  GenomicIdentifer object with respect to its DataSource and set its 
				 * genomicIdentifier attribute
				 */
				Class dataSourceClass = Class.forName(dsClassName.toString());
				Class typeClass = Class.forName(dataSourceType.toString());
				Method methodToSetId = null;
				GenomicIdentifier genomicIdentifier = (GenomicIdentifier) Class.forName(
						dsClassName.toString()).newInstance();
				methodToSetId = dataSourceClass.getMethod("setGenomicIdentifier", typeClass);

				// checck whther the type of attribute is String or Long(EntrezGene)
				if (key.endsWith("_NULL"))
				{
					methodToSetId.invoke(genomicIdentifier, new Object[]{null});
				}
				else if (typeClass.getName().equalsIgnoreCase(Long.class.getName()))
				{
					methodToSetId.invoke(genomicIdentifier, new Long[]{new Long(key)});
				}
				else
				{
					methodToSetId.invoke(genomicIdentifier, new String[]{key});
				}

				/**
				 * Construct ConsensusIdentifierData object and associate it with above created GenomicIdentifier
				 */
				ConsensusIdentifierData data = new ConsensusIdentifierData();
				data.setFrequency(new Float(frequency));
				data.setGenomicIdentifier((GenomicIdentifier) genomicIdentifier);

				/**
				 * Loop over list from frequnecyMap and get the SetIds associted with current genomicIdentifier  
				 */
				for (int i = 2; i < dsList.size(); i++)
				{

					/**
					 * if the genomic identiifer appears in this GenomicIdentifierSet 
					 * then set ConsensusIdentifierData object.
					 * Also if ConsensusIdentifierData object not satisfies teh condition remove the 
					 * Set from teh result
					 */
					Long setID = (Long) dsList.get(i);
					for (Iterator rsIter = rs.iterator(); rsIter.hasNext();)
					{
						GenomicIdentifierSet set = (GenomicIdentifierSet) rsIter.next();
						//System.out.println("Set in LIst for FREQ: " + set.getId()+"-----"+key+"------"+frequency);
						//log.info("Set in LIst for FREQ: " + set.getId()+"-----"+key+"------"+frequency);
						if (isSetToRemove && set.getId().compareTo(setID) == 0)
						{
							//System.out.println("Set remove for Frequency: " + set.getId());
							log.info("SET remove for FREQ: " + set.getId());
							rsIter.remove();
						}
						else if (set.getId().compareTo(setID) == 0)
						{
							Collection temp = set.getConsensusIdentifierDataCollection();
							temp.add(data);
							set.setGenomicIdentifierSolution(sol);
						}
					}
				}
				if (!isSetToRemove)
					consensusDataCollection.add(data);
			}
			/**
			 * Associate ConsensusIdentifierData objects with Solution. 
			 */
			sol.setConsensusIdentifierDataCollection(consensusDataCollection);
			for (int i = 0; i < rs.size(); i++)
			{
				GenomicIdentifierSet set = (GenomicIdentifierSet) rs.get(i);
				//				System.out.println("Data Collection: "
				//						+ set.getConsensusIdentifierDataCollection().size());
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			log.error("Exception in the ORMDAOImpl while calcuting Frequency - " + ex.getMessage());
			throw new Exception("Exception in the ORMDAOImpl while calcuting Frequency - " + ex);
		}
	}

	/**
	 * @author sachin_lale
	 * 
	 * Logic to calulate confidence score. and also removes the SET unsatisfying 
	 * the predicate given on confidence
	 * Algo is:
	 * 1. Iterate over result SET
	 * 2. Get the genomicIDs of all seleted Input/Output Data Sourece. The IDs delemited by '_'
	 * 3. Store the above Ids in MAP as Key and Value as ArrayList, Wher ArrayList contains 
	 *    0th element as cont of same combination of IDs.
	 *    Later elemetn stoer teh SET ids in which the combination occurs.
	 * 4. Iterate over teh MAP
	 * 5. calulate confScore=count_from_map / total_no_of_result
	 * 6. Check predicatVlaue if not satisfied 
	 * 7. remove all that set from result list
	 *      
	 * @param rs
	 * @param predicateValue
	 * @param totalScore
	 * @param isGreaterThanEqual
	 * @throws Exception
	 */
	void calculateConfidence(List rs, List ontList, float predicateValue, float totalScore,
			boolean isGreaterThanEqual, List selectedInputDataSourceList,
			List selectedOutputDataSourceList) throws Exception
	{
		GenomicIdentifierSolution sol = new GenomicIdentifierSolution();

		//System.out.println("freqList.size() :" +freqList.size());
		try
		{
			List dsl = null;

			/**
			 * Map storing the confidence of each set 
			 * key = genomicId of select input and output data source seperatd by '_'  
			 * value = count(no of occurnce of of above key in the result list)  
			 */
			HashMap confidenceMap = new HashMap();

			log.info("rs.size(): " + rs.size());
			if (rs.size() == 0)
				return;

			/**
			 * Iterate over result list and get genomic ids  of selectetd Input/Output data source
			 * and put this in a Map as Key.Where Value of Map is ArrayList consist of
			 * count,DataSourceName,GenomicIdentifierSet Ids
			 */
			//System.out.println("SACHIN rs.size : " +rs.size());
			for (int i = 0; i < rs.size(); i++)
			{

				GenomicIdentifierSet set = (GenomicIdentifierSet) rs.get(i);
				Long setID = new Long(set.getId());

				Gene gene = set.getGene();
				Protein protein = set.getProtein();
				MessengerRNA mrna = set.getMessengerRNA();

				StringBuffer genomicIdforConfidence = new StringBuffer();
				/**
				 * Loop over seleted input data source and get genomic ids of select Input DS.
				 */
				for (int inc = 0; inc < selectedInputDataSourceList.size(); inc++)
				{
					String dsName = (String) selectedInputDataSourceList.get(inc);
					String dataSourceAttribute = MetadataManager.getDataSourceAttribute(
							Constants.DATASOURCE_NAME, dsName, Constants.ATTRIBUTE);
					String dsClass = MetadataManager.getDataSourceAttribute(
							Constants.DATASOURCE_NAME, dsName, Constants.CLASS);
					String temp = dataSourceAttribute.substring(0, 1).toUpperCase();
					String methodName = "get" + temp
							+ dataSourceAttribute.substring(1, dataSourceAttribute.length());
					Method method = null;
					Object value = null;
					if (dsClass.equalsIgnoreCase("Gene"))
					{
						method = Gene.class.getDeclaredMethod(methodName, null);
						value = method.invoke(gene, null);

					}
					else if (dsClass.equalsIgnoreCase("MessengerRNA"))
					{
						method = MessengerRNA.class.getDeclaredMethod(methodName, null);
						value = method.invoke(mrna, null);
					}
					else if (dsClass.equalsIgnoreCase("Protein"))
					{
						method = Protein.class.getDeclaredMethod(methodName, null);
						value = method.invoke(protein, null);
					}
					if (value != null)
					{
						genomicIdforConfidence.append(value.toString() + "_");
					}
				}
				/**
				 * Loop over seleted output data source and get genomic ids of select Output DS.
				 */
				for (int inc = 0; inc < selectedOutputDataSourceList.size(); inc++)
				{
					String dsName = (String) selectedOutputDataSourceList.get(inc);
					String dataSourceAttribute = MetadataManager.getDataSourceAttribute(
							Constants.DATASOURCE_NAME, dsName, Constants.ATTRIBUTE);
					String dsClass = MetadataManager.getDataSourceAttribute(
							Constants.DATASOURCE_NAME, dsName, Constants.CLASS);
					String temp = dataSourceAttribute.substring(0, 1).toUpperCase();
					String methodName = "get" + temp
							+ dataSourceAttribute.substring(1, dataSourceAttribute.length());
					Method method = null;
					Object value = null;
					if (dsClass.equalsIgnoreCase("Gene"))
					{
						method = Gene.class.getDeclaredMethod(methodName, null);
						value = method.invoke(gene, null);

					}
					else if (dsClass.equalsIgnoreCase("MessengerRNA"))
					{
						method = MessengerRNA.class.getDeclaredMethod(methodName, null);
						value = method.invoke(mrna, null);
					}
					else if (dsClass.equalsIgnoreCase("Protein"))
					{
						method = Protein.class.getDeclaredMethod(methodName, null);
						value = method.invoke(protein, null);
					}
					if (value != null)
					{
						genomicIdforConfidence.append(value.toString() + "_");
					}
				}

				genomicIdforConfidence.deleteCharAt(genomicIdforConfidence.length() - 1);
				//System.out.println("genomicIdforConfidence : "+genomicIdforConfidence);
				log.info("genomicIdforConfidence : " + genomicIdforConfidence);
				/**
				 * Put the key in a Map.
				 * If same Key already exist then increment the count of that Key(combine values of genomicIds)
				 * or add new Key and set count = 1.
				 * Also add the SETID in a ArrayList(Value of MAP) to which the current key is belong  
				 */
				String mapKey = genomicIdforConfidence.toString();
				int ontCount = set.getConfidenceScore().intValue();
				System.out.println("ONT COUNT for " + set.getId() + "----" + ontCount);
				if (confidenceMap.get(mapKey) == null)
				{
					List dsList = new ArrayList();
					//dsList.add(new Integer(1));
					dsList.add(new Integer(ontCount));
					//dsList.add(map);
					dsList.add(setID);
					confidenceMap.put(mapKey, dsList);
					//System.out.println("New KEy added confidenceMap : " + mapKey + "--" + ontCount);
					log.info("New Key added confidenceMap : " + mapKey + "--" + ontCount);
				}
				else
				{
					List dsList = (ArrayList) confidenceMap.get(mapKey);
					Integer val = (Integer) dsList.get(0);
					int count = val.intValue();
					//count++;
					count = count + ontCount;
					dsList.set(0, new Integer(count));
					//System.out.println("Increment count in confidenceMap " + mapKey + "--" + count);
					log.info("Increment count in confidenceMap " + mapKey + "--" + count);
					dsList.add(setID);
				}

			}
			//System.out.println("SACHIN ENd: ");

			/**
			 * Iterate over the confidenceMap ans set the confidenceScore attribute of each set.
			 */
			Set keySet = confidenceMap.keySet();
			for (Iterator iter = keySet.iterator(); iter.hasNext();)
			{

				/**
				 * Loop over list from confidenceMap and get the SetIds associted with current genomicIdentifier  
				 */
				String key = (String) iter.next();
				List dsList = (List) confidenceMap.get(key);
				for (int i = 1; i < dsList.size(); i++)
				{

					/**
					 * Calcute confidence score as
					 *   
					 * confScore = No_of_Ocurance_of_row_in_result/Total_no_of_result
					 * Total result list includes redunant data. 
					 */
					Integer genomicIdforConfidenceCount = (Integer) dsList.get(0);
					Long setID = (Long) dsList.get(i);
					float confScore = genomicIdforConfidenceCount.floatValue();
					confScore = confScore / totalScore;
					/**
					 * iterate over result list and set the confidenceScore of each SETs obtained
					 */
					for (Iterator rsIter = rs.iterator(); rsIter.hasNext();)
					{
						GenomicIdentifierSet set = (GenomicIdentifierSet) rsIter.next();
						if (set.getId().compareTo(setID) == 0)
						{
							set.setConfidenceScore(new Float(confScore));
							System.out.println("Set in LIst for CONFIDENCE: " + set.getId()
									+ "-----" + key + "------");
							log.info("SET in list for CONFIDENCE: " + set.getId() + "-----" + key
									+ "------");

						}
					}
				}
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			log.error("Exception in the ORMDAOImpl while calcuting Frequency - " + ex.getMessage());
			throw new Exception("Exception in the ORMDAOImpl while calcuting Frequency - " + ex);
		}
	}

	/**
	 * filter out the SET not satisfying teh condition on confidence 
	 * @param rs
	 * @param predicateValue
	 * @param totalScore
	 * @param isGreaterThanEqual
	 */
	void filterForConfidence(List rs, float predicateValue, float totalScore,
			boolean isGreaterThanEqual)
	{
		for (Iterator iter = rs.iterator(); iter.hasNext();)
		{
			GenomicIdentifierSet set = (GenomicIdentifierSet) iter.next();
			Float confScore = set.getConfidenceScore();
			/**
			 * evaluate te condition.
			 * If not satisfied then remove SET from the list
			 */
			if (isGreaterThanEqual && !(confScore >= predicateValue))
			{
				log.info("Set ID Removed for Confidence ******" + set.getId());
				iter.remove();
			}
			else if (!(confScore > predicateValue))
			{
				System.out.println("Set ID Removed******" + set.getId() + "---" + confScore);
				log.info("Set ID Removed***Confidence" + set.getId() + "***" + confScore);
				//newRs.add(set);
				iter.remove();
			}
		}
	}
	//void ontbizloic()
	//{
	//	int matchONTcount=0;
	//	int ontCount= 0;
	//	
	//	for(int i=0;i<rs.size();i++)
	//	{
	//		GenomicIdentifierSet set = (GenomicIdentifierSet)rs.get(i);
	//		Collection ontColl = set.getOrderOfNodeTraversalCollection();
	//		
	//		if(ontColl!=null)
	//		{
	//			
	//			
	//				
	//			for(Iterator iter = ontColl.iterator();iter.hasNext();)
	//			{
	//				
	//				ontCount= 0;
	//				List setOntList = new ArrayList();
	//				OrderOfNodeTraversal ont = (OrderOfNodeTraversal)iter.next();
	//				OrderOfNodeTraversal tempont = ont;
	//				while (tempont != null)
	//				{
	//					
	//					LinkType ltype = tempont.getLinkType();
	//					String linkType = "";
	//					
	//					 String dsName = tempont.getSourceDataSource().getName();
	//					 setOntList.add(dsName);
	//					 if (ltype != null)
	//					 {	 
	//						 linkType = ltype.getType();
	//						 setOntList.add(linkType);
	//					 }	 
	////					System.out.println(tempont.getId() + "----"
	////							+ tempont.getSourceDataSource().getName() + "------" + linkType);
	//					OrderOfNodeTraversal nextont = tempont.getChildOrderOfNodeTraversal();
	//					tempont = nextont;
	//				}
	//				
	//				/**
	//				 * Check if ONT match with selected ONT
	//				 */
	//				
	//				System.out.println("ONT FOR SET :" + set.getId());
	//				for(int j =0;j<setOntList.size();j++)
	//				{
	//					System.out.println(setOntList.get(j));
	//				}
	//				int ontNodecount=0;
	//				//if(ontList.size()==setOntList.size())
	//			//	{
	//					
	//				int selectedontCount=0;
	//				
	//				GET_NEXT_NODE:
	//				for(int j =0;j<setOntList.size();j++)
	//				{
	//					String dsName = (String)setOntList.get(j);
	//					String linkType = (String)setOntList.get(++j);
	//					if(selectedontCount>ontList.size())
	//						break;
	//					GCCriteria f = (GCCriteria)ontList.get(selectedontCount);
	//					String ds = f.getDataSource();
	//					List type = f.getType();
	//					selectedontCount++;
	//					System.out.println("COMPARIN DS " + ds +"WITH " + dsName);
	//					if(dsName.equalsIgnoreCase(ds))
	//					{
	//						
	//						for(int k=0;k<type.size();k++)
	//						{
	//							String selectedLinktype = (String )type.get(k);
	//							System.out.println("COMPARIN LINK " + selectedLinktype);
	//							ontNodecount++;
	//							if(selectedLinktype.equalsIgnoreCase(linkType))
	//							{
	//								ontNodecount++;
	//								continue GET_NEXT_NODE; 
	//							}
	//							//System.out.print(f.getDataSource()+"-->"+type.get(k));
	//						}
	//						if(linkType.equalsIgnoreCase(""))
	//							ontNodecount++;
	//						else
	//						{
	//							ontNodecount=0;
	//							break;
	//						}	
	//					}
	//					else
	//					{
	//						ontNodecount=0;
	//						break;
	//					}
	//				}
	//			//	}
	//				System.out.println("ontNodecount :" +ontNodecount);
	//				if(ontNodecount==setOntList.size())
	//				{	
	//					ontCount++;
	//					Long cnt = (Long)ontCountForSet.get(set.getId()); 
	//					if(cnt==null)
	//					{	
	//						ontCountForSet.put(set.getId(),new Long(1));
	//					}
	//					else
	//					{
	//						int val = cnt.intValue();
	//						val++;
	//						cnt = new Long(val);
	//						ontCountForSet.remove(set.getId());
	//						System.out.println("SET & VAL" + set.getId()+"---"+val);
	//						ontCountForSet.put(set.getId(),new Long(val));
	//					}
	//					System.out.println("\n\nSELECT ONT FOR COUNT");
	//					for(int l =0;l<setOntList.size();l++)
	//					{
	//						System.out.println(setOntList.get(l));
	//					}
	//				}	
	//			}
	//			
	//		}	
	//	}
	//	totalScore = totalScore+ontCount;
	//	Set s = ontCountForSet.keySet();
	//	System.out.println("SETID     COUNT");
	//	for(Iterator it = s.iterator();it.hasNext();)
	//	{
	//		Long key = (Long)it.next();
	//		Long i = (Long)ontCountForSet.get(key);
	//		System.out.println(key+"     "+i);
	//	}
	//}
}