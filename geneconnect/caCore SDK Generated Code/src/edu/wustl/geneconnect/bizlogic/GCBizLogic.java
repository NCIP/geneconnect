
package edu.wustl.geneconnect.bizlogic;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.wustl.geneconnect.GenomicIdentifierSolution;
import edu.wustl.geneconnect.domain.ConsensusIdentifierData;
import edu.wustl.geneconnect.domain.Gene;
import edu.wustl.geneconnect.domain.GenomicIdentifier;
import edu.wustl.geneconnect.domain.GenomicIdentifierSet;
import edu.wustl.geneconnect.domain.MessengerRNA;
import edu.wustl.geneconnect.domain.OrderOfNodeTraversal;
import edu.wustl.geneconnect.domain.Protein;
import edu.wustl.geneconnect.utility.Constants;
import edu.wustl.geneconnect.utility.MetadataManager;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationServiceProvider;

/**
 * This class provides the method for calculating confidence / frequency. 
 * @author sachin_lale
 *
 */
public class GCBizLogic
{

	private static Logger log = Logger.getLogger(GCBizLogic.class.getName());

	Map ontCountMapForSet = new HashMap();

	public GCBizLogic()
	{

	}

	/**
	 * Calculate the total number of result which contains GenomicIdentifierSet object.
	 * If ONT is selected then total score is equlas to no. of result set
	 * @param rs -  Result set got after executing user query 
	 * @return float totaScore
	 */
	float calculateTotalScore(List rs, List ontList, List selectedInput, List selectedOutput)
	{
		float totalScore = 0.0f;

		/**
		 * If ONT is selected by user Calculate Total score based on ONT selected
		 */
		if (ontList.size() > 0 && rs.size() > 0)
		{
			log.info("calculating total score with ONT list");
			System.out.println("ONT list :" + ontList.size());
			log.info("ONT list :" + ontList.size());
			log.info("ONT list selected :" + ontList.get(0));
			setONTcountMap(rs, ontList);
		}
		/**
		 * else calulate score based on default ont criteria
		 */
		else
		{
			log.info("calculating total score without ONT list");
			setONTcountMap(rs, selectedInput, selectedOutput, Constants.DEFAULT_ONT_SELECTION_LOGIC);

		}
		if (ontCountMapForSet != null)
		{
			Set keys = ontCountMapForSet.keySet();

			for (Iterator iter = keys.iterator(); iter.hasNext();)
			{
				Long setID = (Long) iter.next();
				Integer ontCount = (Integer) ontCountMapForSet.get(setID);
				log.info("ONT count for SetId " + setID + " is " + ontCount);
				totalScore = totalScore + ontCount.floatValue();

			}

		}
		log.info("TotalScore of Solution :" + totalScore);
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
		long t1 = System.currentTimeMillis();
		List dsl = null;
		String[] domainClass = new String[]{"Gene", "MessengerRNA", "Protein"};

		int countForNullGenomicId = 0;
		for (Iterator iter = rs.iterator(); iter.hasNext();)
		{
			GenomicIdentifierSet set = (GenomicIdentifierSet) iter.next();
			Long setID = new Long(set.getId());

			Gene gene = set.getGene();
			Protein protein = set.getProtein();
			MessengerRNA mrna = set.getMessengerRNA();

			countForNullGenomicId = 0;
			/**DUPLICATION Avoided
			 * Check if  selected output data source of Gene,mrna,protein object contains NULL value.
			 * Iterate over each dat source (got from MetaData) and check if the 
			 * data source is select as output. If yes then  get tah value and if value is NULL then
			 * Increment the counter (int countForNullGenomicId).  
			 */
			for (int domainClasscnt = 0; domainClasscnt < domainClass.length; domainClasscnt++)
			{
				String domainClassName = domainClass[domainClasscnt];
				Class dmClass = Class.forName(Constants.DOMAIN_CLASSNAME_PREFIX + "."
						+ domainClassName);
				Object objectMethodToInvoke = null;
				if (domainClassName.equalsIgnoreCase("Gene"))
				{
					objectMethodToInvoke = gene;
				}
				else if (domainClassName.equalsIgnoreCase("MessengerRNA"))
				{
					objectMethodToInvoke = mrna;
				}
				else if (domainClassName.equalsIgnoreCase("Protein"))
				{
					objectMethodToInvoke = protein;
				}
				dsl = MetadataManager.getAttibutes(Constants.CLASS, domainClassName);
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
					if (selectedOutputDataSourceList.contains(dataSourceName))
					{
						String dataSourceAttribute = MetadataManager.getDataSourceAttribute(
								Constants.DATASOURCE_NAME, dataSourceName, Constants.ATTRIBUTE);
						String temp = dataSourceAttribute.substring(0, 1).toUpperCase();
						String methodName = "get" + temp
								+ dataSourceAttribute.substring(1, dataSourceAttribute.length());

						Method method = dmClass.getDeclaredMethod(methodName, null);
						Object value = method.invoke(objectMethodToInvoke, null);
						if ((value == null) || (value.toString().equalsIgnoreCase("NULL")))
						{
							countForNullGenomicId++;
						}
					}
				}
			}

			/** 
			 * If the value counter countForNullGenomicId matches the size of select output data spource list
			 * this means values all select o/p data source of this GenomicIdentifierSet iobejct is NULL.
			 * So remove this SET from teh result list. 
			 *   
			 */
		
			if (countForNullGenomicId > 0
					&& countForNullGenomicId == selectedOutputDataSourceList.size())
			{

				log.info("All NULL values ---remove SETID: " + setID);
				iter.remove();
			}
		}
		long t2 = System.currentTimeMillis();
		log.info("TIME FOR prepare: " + ((t2 - t1) / 1000));
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
		long t1 = System.currentTimeMillis();

		GenomicIdentifierSolution sol = new GenomicIdentifierSolution();

		try
		{
			List dsl = null;
			String[] domainClass = new String[]{"Gene", "MessengerRNA", "Protein"};
			/**
			 * Map storing the frequency of each genomicIdentifier.as 
			 * key = genomicId and value = count(no of occurnce of genomicId in the result list)  
			 */
			HashMap frequencyMap = new HashMap();

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
		
				Collection dataColl = new HashSet();
				set.setConsensusIdentifierDataCollection(dataColl);
				set.setGenomicIdentifierSolution(sol);
				Long setID = new Long(set.getId());

				Integer n = (Integer) ontCountMapForSet.get(setID);
				int noOfOntForThisSet = n.intValue();

				/**
				 * Get all attributes and  Loop over each attribute of Gene class
				 */
				Gene gene = set.getGene();
				MessengerRNA mrna = set.getMessengerRNA();
				Protein protein = set.getProtein();
				for (int domainClasscnt = 0; domainClasscnt < domainClass.length; domainClasscnt++)
				{
					String domainClassName = domainClass[domainClasscnt];
					Class dmClass = Class.forName(Constants.DOMAIN_CLASSNAME_PREFIX + "."
							+ domainClassName);
					Object objectMethodToInvoke = null;
					if (domainClassName.equalsIgnoreCase("Gene"))
					{
						objectMethodToInvoke = gene;
					}
					else if (domainClassName.equalsIgnoreCase("MessengerRNA"))
					{
						objectMethodToInvoke = mrna;
					}
					else if (domainClassName.equalsIgnoreCase("Protein"))
					{
						objectMethodToInvoke = protein;
					}
					if (objectMethodToInvoke != null)
					{
						dsl = MetadataManager.getAttibutes(Constants.CLASS, domainClassName);
						for (int j = 0; j < dsl.size(); j++)
						{
							Map map = (Map) dsl.get(j);
							String dataSourceName = (String) map.get(Constants.DATASOURCE_NAME);
							String dataSourceAttribute = (String) map.get(Constants.ATTRIBUTE);
							String temp = dataSourceAttribute.substring(0, 1).toUpperCase();
							String methodName = "get"
									+ temp
									+ dataSourceAttribute
											.substring(1, dataSourceAttribute.length());
							Method method = dmClass.getDeclaredMethod(methodName, null);
							/**
							 * Get genomicId of the cuurent Gene attribute 
							 * ensemblGeneId,entrezGeneId,unigeneClusterId
							 */
							Object value = method.invoke(objectMethodToInvoke, null);
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
									dsList.set(0, new Integer(count));
									dsList.add(setID);
								}
							}
							else if (value != null && frequencyMap.get(value.toString()) == null)
							{
								List dsList = new ArrayList();
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
								dsList.set(0, new Integer(count));
								dsList.add(setID);
							}
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
				List dsList = (List) frequencyMap.get(key);
				StringBuffer dsClassName = new StringBuffer("edu.wustl.geneconnect.domain.");
				Map tempMap = (Map) dsList.get(1);
				dsClassName.append((String) tempMap.get(Constants.GENOMIC_IDENTIFIER_CLASS));
				StringBuffer dataSourceType = new StringBuffer();
				dataSourceType.append((String) tempMap.get(Constants.TYPE));
				Integer count = (Integer) dsList.get(0);
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
						else if (!isFreqGreaterThanEqual && !(frequency > fq.getPredicate()))
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
						if (isSetToRemove && set.getId().compareTo(setID) == 0)
						{
							log.info("SET remove for FREQ: " + set.getId() + "---" + frequency);
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
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			log.error("Exception in the ORMDAOImpl while calcuting Frequency - " + ex.getMessage());
			throw new Exception("Exception in the ORMDAOImpl while calcuting Frequency - " + ex);
		}
		long t2 = System.currentTimeMillis();
		log.info("TIME FOR Frequency: " + ((t2 - t1) / 1000));

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
				if (genomicIdforConfidence.length() - 1 > 0)
					genomicIdforConfidence.deleteCharAt(genomicIdforConfidence.length() - 1);
				log.info("genomicIdforConfidence : " + genomicIdforConfidence);
				/**
				 * Put the key in a Map.
				 * If same Key already exist then increment the count of that Key(combine values of genomicIds)
				 * or add new Key and set count = 1.
				 * Also add the SETID in a ArrayList(Value of MAP) to which the current key is belong  
				 */
				String mapKey = genomicIdforConfidence.toString();
				Integer n = (Integer) ontCountMapForSet.get(setID);
				int ontCount = n.intValue();
				log.info("ONT COUNT for " + set.getId() + "----" + ontCount);
				if (confidenceMap.get(mapKey) == null)
				{
					List dsList = new ArrayList();
					dsList.add(new Integer(ontCount));
					dsList.add(setID);
					confidenceMap.put(mapKey, dsList);
					log.info("New Key added confidenceMap : " + mapKey + "--" + ontCount);
				}
				else
				{
					List dsList = (ArrayList) confidenceMap.get(mapKey);
					Integer val = (Integer) dsList.get(0);
					int count = val.intValue();
					count = count + ontCount;
					dsList.set(0, new Integer(count));
					log.info("Increment count in confidenceMap " + mapKey + "--" + count);
					dsList.add(setID);
				}

			}

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
			float confScore = set.getConfidenceScore().floatValue();
			/**
			 * evaluate te condition.
			 * If not satisfied then remove SET from the list
			 */
			if (isGreaterThanEqual && !(confScore >= predicateValue))
			{
				log.info("Set ID Removed for Confidence ******" + set.getId() + "---" + confScore
						+ predicateValue);
				iter.remove();
			}
			else if (!isGreaterThanEqual && !(confScore > predicateValue))
			{
				log.info("Set ID Removed***Confidence" + set.getId() + "***" + confScore);
				iter.remove();
			}
		}
	}

	/**
	 * Count ONT for advanced search for each set iD.
	 * select only those onts that is a part of criteria
	 *  
	 * @param rs
	 * @param ontList
	 */
	void setONTcountMap(List rs, List ontList)
	{

		if (ontList.size() > 0)
		{
			for (Iterator setIter = rs.iterator(); setIter.hasNext();)
			{
				GenomicIdentifierSet set = (GenomicIdentifierSet) setIter.next();
				Long setId = set.getId();
				log.info("comapring ONT of SET ID " + setId);
				Collection ontColl = set.getOrderOfNodeTraversalCollection();
				Collection newOntCollection = new ArrayList();
				int count = 0;
				if (ontColl != null)
				{
					StringBuffer firstDataSource = new StringBuffer();
					StringBuffer lastDataSource = new StringBuffer();
					for (Iterator iter = ontColl.iterator(); iter.hasNext();)
					{
						OrderOfNodeTraversal ont = (OrderOfNodeTraversal) iter.next();
						/** 
						 * Get associates ONT list from metadata
						 */
						List dataSourcelist = MetadataManager.getONTList(ont.getId());
						firstDataSource.append(ont.getSourceDataSource().getName());
						OrderOfNodeTraversal tempont = ont;
						/**
						 * Get the PAth and store it in a list as
						 * DS1,linktype1,DS2,linktype2,DS3
						 */

						for (int i = 0; i < ontList.size(); i++)
						{
							/**
							 * Get selected ONT list and compare wit ont path for current SET
							 */
							List innerList = (List) ontList.get(i);
							int cnt = 0;
							if (dataSourcelist.size() == innerList.size())
							{
								cnt = 0;
								boolean ontCondition = false;
								for (int j = 0; j < dataSourcelist.size(); j++)
								{
									String node1 = (String) dataSourcelist.get(j);
									String node2 = (String) innerList.get(j);
									if (node1.equalsIgnoreCase(node2)
											|| node1.equalsIgnoreCase(Constants.ANY_LINKTYPE))
									{
										cnt++;
									}
								}
								if (cnt == innerList.size())
								{
									/** 
									 * Increment the the count if teh the current path mathes with curretn 
									 * selected path. 
									 */
									count++;
									newOntCollection.add(ont);
									log.info("Select ONT MATCH " + innerList);
								}
							}
						}

					}
				}
				/**
				 * change to count>0 for OR condition
				 */
				if (count > 0)
				{
					log.info("ONT count for " + setId + "---" + count);
					set.setOrderOfNodeTraversalCollection(newOntCollection);
					ontCountMapForSet.put(setId, new Integer(count));
				}
				else
				{
					setIter.remove();
					log.info("Remove set for ONT----" + setId + "---" + count);
				}

			}
		}

	}

	/**
	 * Count ONT for simple search for each set iD.
	 * select only thos onts whose first and last node eqals to selected
	 * input or output data source AND all input / outputs are included.  
	 * @param rs
	 * @param selectedInput
	 * @param selectedOutput
	 * @param ontSelectionLogic - the ONT criteria identifier 1- for default criteria and 2 - for no match found criteria
	 */
	void setONTcountMap(List rs, List selectedInput, List selectedOutput, int ontSelectionLogic)
	{
		long t1 = System.currentTimeMillis();
		List outputDataSourceFound = new ArrayList();
		HashMap setToRemoveList = new HashMap();
		for (Iterator setIter = rs.iterator(); setIter.hasNext();)
		{
			GenomicIdentifierSet set = (GenomicIdentifierSet) setIter.next();
			Long setId = set.getId();
			Collection ontColl = set.getOrderOfNodeTraversalCollection();
			Collection newOntCollection = new ArrayList();
			int count = 0;
			if (ontColl != null)
			{
				StringBuffer firstDataSource = new StringBuffer();
				StringBuffer lastDataSource = new StringBuffer();
				for (Iterator iter = ontColl.iterator(); iter.hasNext();)
				{
					OrderOfNodeTraversal ont = (OrderOfNodeTraversal) iter.next();
					/** 
					 * Get associates ONT list from metadata
					 */
					List dataSourcelist = MetadataManager.getDataSourceListFromONT(ont.getId());

					if (dataSourcelist.size() > 0)
					{

						boolean ontCondition = false;
						firstDataSource.setLength(0);
						lastDataSource.setLength(0);
						firstDataSource.append((String) dataSourcelist.get(0));
						lastDataSource.append((String) dataSourcelist
								.get(dataSourcelist.size() - 1));

						if (selectedInput.contains(firstDataSource.toString())
								|| selectedOutput.contains(firstDataSource.toString()))
						{
							if (selectedInput.contains(lastDataSource.toString())
									|| selectedOutput.contains(lastDataSource.toString()))
							{
								ontCondition = true;
							}

						}
						if (ontSelectionLogic == Constants.DEFAULT_ONT_SELECTION_LOGIC)
						{
							if (ontCondition && dataSourcelist.containsAll(selectedInput)
									&& dataSourcelist.containsAll(selectedOutput))
							{
								count++;
								newOntCollection.add(ont);
								log.info("Select ONT: " + dataSourcelist.toString());
							}
						}
						else if (ontSelectionLogic == Constants.NOMATCH_ONT_SELECTION_LOGIC)
						{
							if (ontCondition && dataSourcelist.containsAll(selectedInput))
							{
								for (int i = 0; i < selectedOutput.size(); i++)
								{
									if (!firstDataSource.toString().equalsIgnoreCase(
											(String) selectedOutput.get(i))
											|| !lastDataSource.toString().equalsIgnoreCase(
													(String) selectedOutput.get(i)))
									{
										if (dataSourcelist.contains(selectedOutput.get(i)))
										{
											count++;
											newOntCollection.add(ont);
											log.info("Select ONT: " + dataSourcelist.toString());
											break;
										}
									}
								}
							}
						}

					}
				}
				if (count > 0)
				{
					log.info("ONT count for " + setId + "---" + count);
					ontCountMapForSet.put(setId, new Integer(count));
					set.setOrderOfNodeTraversalCollection(newOntCollection);
				}
				else
				{
					for (Iterator iter = ontColl.iterator(); iter.hasNext();)
					{
						OrderOfNodeTraversal ont = (OrderOfNodeTraversal) iter.next();
						List dataSourcelist = MetadataManager.getDataSourceListFromONT(ont.getId());
						for (int i = 0; i < dataSourcelist.size(); i++)
						{
							// check if the current data source is selected as outpu and not inserted in found dats source list 
							if (selectedOutput.contains(dataSourcelist.get(i))
									&& !outputDataSourceFound.contains(dataSourcelist.get(i)))
							{
								outputDataSourceFound.add(dataSourcelist.get(i));
							}
						}
					}
				}
			}
		}
		/**
		 * logic to check if result is null and if subset of select output data source is not found in any of ONT
		 * then re-call the method to select onts by setting output data source which are foud in in previos set.
		 */
		log.info("ontCountMapForSet.keySet().size(): " + ontCountMapForSet.keySet().size());
		log.info("outputDataSourceFound.size(): " + outputDataSourceFound.size());
		log.info("selectedOutput.size(): " + selectedOutput.size());
		log.info("outputDataSourceFound: " + outputDataSourceFound);

		/**
		 * If no result found then sekect a ONTs with criteria as 
		 * start/end with input/output AND atleas one output other than start/end is included.
		 * 
		 */
		if (ontCountMapForSet.keySet().size() == 0
				&& outputDataSourceFound.size() < selectedOutput.size())
		{
			ontCountMapForSet = new HashMap();
			for (Iterator outputIter = selectedOutput.iterator(); outputIter.hasNext();)
			{
				if (!outputDataSourceFound.contains(outputIter.next()))
				{
					outputIter.remove();
				}
			}
			log.info("call ing method again with output as :" + selectedOutput);
			setONTcountMap(rs, selectedInput, selectedOutput, Constants.NOMATCH_ONT_SELECTION_LOGIC);

			/**
			 * remove the subset from the results
			 */
			if (ontCountMapForSet != null && ontCountMapForSet.size() > 0)
			{
				removeSubSetFromResult(rs, selectedOutput);
			}
		}
		// remove those record from result where its ONT count is not set. 
		for (Iterator setIter = rs.iterator(); setIter.hasNext();)
		{
			GenomicIdentifierSet set = (GenomicIdentifierSet) setIter.next();
			Long setId = set.getId();
			if (ontCountMapForSet.get(setId) == null)
			{
				setIter.remove();
				log.info("Remove set for ONT----" + setId);
			}
		}
		long t2 = System.currentTimeMillis();
		log.info("TIME FOR ONT: " + ((t2 - t1) / 1000));
	}

	/**
	 * removes the subset from the result
	 * @param rs
	 * @param selectedOutput
	 */
	void removeSubSetFromResult(List rs, List selectedOutput)
	{
		log.info("start of removing subset");
		try
		{
			int countForNullGenomicId = 0;
			Map listOfResultSubSet = new Hashtable();
			/**
			 * Iterate over a result set and get the value of selected output datasource
			 * add this to the list
			 */
			for (Iterator iter = rs.iterator(); iter.hasNext();)
			{
				GenomicIdentifierSet set = (GenomicIdentifierSet) iter.next();
				Long setID = new Long(set.getId());
				
				Gene gene = set.getGene();
				Protein protein = set.getProtein();
				MessengerRNA mrna = set.getMessengerRNA();
				Set resultSubSet = new HashSet();
				for (int i = 0; i < selectedOutput.size(); i++)
				{
					String dataSourceName = (String) selectedOutput.get(i);
					String domainClassName = MetadataManager.getDataSourceAttribute(
							Constants.DATASOURCE_NAME, dataSourceName, Constants.CLASS);
					Class dmClass = Class.forName(Constants.DOMAIN_CLASSNAME_PREFIX + "."
							+ domainClassName);
					Object objectMethodToInvoke = null;
					if (domainClassName.equalsIgnoreCase("Gene"))
					{
						objectMethodToInvoke = gene;
					}
					else if (domainClassName.equalsIgnoreCase("MessengerRNA"))
					{
						objectMethodToInvoke = mrna;
					}
					else if (domainClassName.equalsIgnoreCase("Protein"))
					{
						objectMethodToInvoke = protein;
					}

					String dataSourceAttribute = MetadataManager.getDataSourceAttribute(
							Constants.DATASOURCE_NAME, dataSourceName, Constants.ATTRIBUTE);
					String temp = dataSourceAttribute.substring(0, 1).toUpperCase();
					String methodName = "get" + temp
							+ dataSourceAttribute.substring(1, dataSourceAttribute.length());

					Method method = dmClass.getDeclaredMethod(methodName, null);
					Object value = method.invoke(objectMethodToInvoke, null);
					if ((value != null))
					{
						resultSubSet.add(value);
					}

				}
				boolean addsettolist = true;
				Set keyset = listOfResultSubSet.keySet();
				log.info("set1: " + resultSubSet);

				/**
				 *  compare the set in list with the new set of values.
				 *  if the new set is a subset of any previously added set in the list then do remove new set from result.
				 *  If the new set is super set of any previously added set in the list then remove the subset from list and result.
				 *  else add the new set ina list.   
				 */

				for (Iterator setIt = keyset.iterator(); setIt.hasNext();)
				{
					Long key = (Long) setIt.next();
					Set s = (Set) listOfResultSubSet.get(key);
					log.info("set2: " + s);
					if (resultSubSet.containsAll(s))
					{
						listOfResultSubSet.remove(key);
						listOfResultSubSet.put(setID, resultSubSet);
						log.info("removed set from list exists Set: " + key);
						ontCountMapForSet.remove(key);
						addsettolist = false;
						break;
					}
					if (s.containsAll(resultSubSet))
					{
						log.info("removed set for subset of slreasdy exists Set: " + setID);
						ontCountMapForSet.remove(setID);
						addsettolist = false;
						break;
					}
				}
				if (addsettolist)
				{
					log.info("set1 added: " + resultSubSet);
					listOfResultSubSet.put(setID, resultSubSet);
				}
			}
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * @param ard
	 */
	public static void main(String ard[])
	{
		//		float f1 = 1.0f;
		//		float f2 = 1.0f;
		//		System.out.println(f1 > f2);
		List l = new ArrayList();
		Set set1 = new HashSet();
		set1.add("A");
		set1.add("C");
		Set set2 = new HashSet();
		set2.add("A");
		set2.add("B");
		set2.add("C");
		
		if (set1.containsAll(set2))
			System.out.println("remove set2");
		if (set2.containsAll(set1))
			System.out.println("remove set1");
	}
}
