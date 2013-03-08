/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

package edu.wustl.geneconnect.bizlogic;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.impl.CriteriaImpl;

import edu.wustl.geneconnect.domain.ConsensusIdentifierData;
import edu.wustl.geneconnect.domain.DataSource;
import edu.wustl.geneconnect.domain.Gene;
import edu.wustl.geneconnect.domain.GenomicIdentifier;
import edu.wustl.geneconnect.domain.GenomicIdentifierSet;
import edu.wustl.geneconnect.domain.LinkType;
import edu.wustl.geneconnect.domain.MessengerRNA;
import edu.wustl.geneconnect.domain.OrderOfNodeTraversal;
import edu.wustl.geneconnect.domain.Protein;
import edu.wustl.geneconnect.utility.Constants;
import edu.wustl.geneconnect.utility.MetadataManager;

/**
 * Provides method to interpret DetachedCriteria and creation of new DetachedCriteria
 * and also methods to interpret the domain objects stroes teh selected in put and output data source for 
 * confidecen and frequecny calculation
 * @author sachin_lale
 *
 */
public class ResultProcessor
{

	private static Logger log = Logger.getLogger(ResultProcessor.class.getName());
	private static final String CURRENT_NODE = "CURRENT_NODE";
	List selectedInputDataSourceList = new ArrayList();
	List selectedInputValueList = new ArrayList();
	List selectedOutputDataSourceList = new ArrayList();
	List ontList = new ArrayList();
	List freqList = new ArrayList();

	//	boolean variable to check whether user has given a predicate on Confidence
	boolean isGreaterThanEqual = false;

	// boolean variable to check whether user has given a predicate on Frequency
	boolean isFreqGreaterThanEqual = false;

	boolean createNewCriteria = false;
	String confScorevalue = "0";

	/**
	 * Interpret the DetachedCriteria and stores the condition on confidence , frequency, ONT  
	 * @param impl
	 */
	public void interpretCriteria(CriteriaImpl impl)
	{

		// List which stores FrequecyCriteria objects representing Frequency predicates given by user  

		int ontCounter = -1;
		int freqCounter = -1;
		int dsCounter = -1;

		String intValue = "0";
		String strValue = "0";
		log.info("CIRT: --" + impl);
		log.info("entity: --" + impl.getEntityOrClassName());
		if(impl.getEntityOrClassName().equalsIgnoreCase(GenomicIdentifierSet.class.getName()))
		{
			createNewCriteria = true;
		}
		Iterator iter = impl.iterateExpressionEntries();
		Iterator iter1 = impl.iterateSubcriteria();
		/**
		 * Iterate over Expression specified in Criteria
		 */
		while (iter.hasNext())
		{
			Object o = iter.next();
			String predicate = o.toString();
			CriteriaImpl.CriterionEntry a = (CriteriaImpl.CriterionEntry) o;

			String associationClassTemp = a.getCriteria().toString();
			int ind1 = associationClassTemp.indexOf("(");
			int ind2 = associationClassTemp.indexOf(":");
			String associationClass = associationClassTemp.substring(ind1 + 1, ind2);
			log.info("associationClass* ************* " + associationClass);
			log.info("Expression Predicates* ************* " + predicate);
			System.out.println("Expression Predicates* ************* " + predicate);
			int ind = -1;
			ind = predicate.indexOf("=");
			if (ind > 0)
			{
				/**
				 * Store selected input data source name in a selectedInputDataSourceList.
				 * Strore selected output data source name in a selectedOutputDataSourceList.
				 */
				int trueInd = predicate.indexOf("=true");
				if (trueInd > 0)
				{

					String temp = predicate.substring(0, trueInd);
					String dsName = MetadataManager.getDataSourceAttribute(
							Constants.OUTPUT_ATTRIBUTE, temp, Constants.DATASOURCE_NAME);
					if (dsName != null)
					{
						log.info("selectedOutputDataSourceList: " + dsName);
						selectedOutputDataSourceList.add(dsName);
						log.info("OUTPUT VALUE = " + dsName);
					}
				}
				else
				{
					String temp = predicate.substring(0, ind);
					String attrClass = MetadataManager.getRoleLookUpAttribute(
							Constants.SOURCE_CLASS, "GenomicIdentifierSet", Constants.ROLE_NAME,
							associationClass, Constants.TARGET_CLASS);
					String dsName = MetadataManager.getDataSourceAttribute(Constants.ATTRIBUTE,
							temp, Constants.CLASS, attrClass, Constants.DATASOURCE_NAME);

					if (dsName != null)
					{
						log.info("selectedInputDataSourceList: " + dsName);
						selectedInputDataSourceList.add(dsName);

						String value = predicate.substring(temp.length() + 1, predicate.length());
						selectedInputValueList.add(dsName + "=" + value);

						log.info("INPUT VALUE = " + dsName + "=" + value);
					}
				}
			}
			if (predicate.startsWith("confidenceScore"))
			{
				createNewCriteria = true;
				/**
				 * Criteria Query contains expression like 'confidenceScore>=value' or 
				 * 'confidenceScore>value' 
				 * store value and set boolean variable to indicate user has give predicate on confidence
				 */
				if (predicate.indexOf(">=") >= 0)
				{
					confScorevalue = predicate.substring("confidenceScore>=".length(), predicate
							.length());
					isGreaterThanEqual = true;

				}
				else if (predicate.indexOf(">") >= 0)
				{
					confScorevalue = predicate.substring("confidenceScore>".length(), predicate
							.length());

				}
				log.info("Predicate " + confScorevalue + "===" + isGreaterThanEqual);
			}
			if (predicate.startsWith("frequency"))
			{
				createNewCriteria = true;
				/**
				 * Criteria Query contains expression like 'frequency>=value AND dataSource=dsName'. 
				 * Store value in freqList and set boolean variable to indicate user has give predicate 
				 * on frequency
				 */
				if (predicate.indexOf(">=") >= 0)
				{
					GCCriteria f = new GCCriteria();
					intValue = predicate.substring("frequency>=".length(), predicate.length());
					isFreqGreaterThanEqual = true;
					f.setPredicate(Float.valueOf(intValue).floatValue());
					freqCounter++;
					freqList.add(f);
					log.info("isFreqGreaterThanEqual************** " + isFreqGreaterThanEqual);
				}
				else if (predicate.indexOf(">") >= 0)
				{
					GCCriteria f = new GCCriteria();
					intValue = predicate.substring("frequency>".length(), predicate.length());
					isFreqGreaterThanEqual = true;
					f.setPredicate(Float.valueOf(intValue).floatValue());
					freqCounter++;
					freqList.add(f);
					log.info("isFreqGreaterThanEqual************** " + isFreqGreaterThanEqual);
				}
			}
			if (predicate.startsWith("dataSource") && !predicate.startsWith("dataSourceName"))
			{
				if (predicate.indexOf("=") >= 0)
				{
					strValue = predicate.substring("dataSource=".length(), predicate.length());
					dsCounter++;
					GCCriteria f = (GCCriteria) freqList.get(dsCounter);
					f.setDataSource(strValue);
				}
			}
			if (predicate.startsWith("name"))
			{
				if (predicate.indexOf("=") >= 0)
				{
					createNewCriteria = true;
					GCCriteria f = new GCCriteria();
					strValue = predicate.substring("name=".length(), predicate.length());
					ontCounter++;
					f.setDataSource(strValue);
					ontList.add(f);
				}
			}
			if (predicate.startsWith("type"))
			{
				int indexOfOR = predicate.indexOf("or type");
				if (indexOfOR > 0)
				{
					int firstEq = predicate.indexOf("=", 0);
					int seconfEq = predicate.indexOf(predicate.substring(indexOfOR
							+ "or type".length()));
					GCCriteria f = (GCCriteria) ontList.get(ontCounter);
					String firsttype = predicate.substring(firstEq + 1, indexOfOR - 1);
					String secondType = predicate.substring(seconfEq + 1, predicate.length());
					List type = new ArrayList();
					type.add(firsttype);
					type.add(secondType);
					f.setType(type);
				}
				else if (predicate.indexOf("=") >= 0)
				{

					strValue = predicate.substring("type=".length(), predicate.length());
					GCCriteria f = (GCCriteria) ontList.get(ontCounter);
					List type = new ArrayList();
					type.add(strValue);

					f.setType(type);
				}
			}
		}
		log.info("ONT predicate list size: " + ontList.size());
	
		log.info("Frequency predicate list size: " + freqList.size());

	}

	public void interpretCriteria(GenomicIdentifierSet set) throws Exception
	{
		List dsl = null;
		String roleName = "";
		Field field = null;
		roleName = MetadataManager.getRoleName(Constants.GENOMICIDENTIFIERSET_CLASS_NAME,
				Constants.GENE_CLASS_NAME);
		log.info("debug1");
		field = GenomicIdentifierSet.class.getDeclaredField(roleName);
		field.setAccessible(true);
		Object gene = field.get(set);

		roleName = MetadataManager.getRoleName(Constants.GENOMICIDENTIFIERSET_CLASS_NAME,
				Constants.MRNA_CLASS_NAME);
		log.info("debug2");
		field = GenomicIdentifierSet.class.getDeclaredField(roleName);
		field.setAccessible(true);
		Object mrna = field.get(set);

		roleName = MetadataManager.getRoleName(Constants.GENOMICIDENTIFIERSET_CLASS_NAME,
				Constants.PROTEIN_CLASS_NAME);
		log.info("debug3");
		field = GenomicIdentifierSet.class.getDeclaredField(roleName);
		field.setAccessible(true);
		Object protein = field.get(set);

		if (gene != null)
		{
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
				
				String dataSourceAttribute = (String) map.get(Constants.ATTRIBUTE);
				String outputAttribute = (String) map.get(Constants.OUTPUT_ATTRIBUTE);
				String temp = dataSourceAttribute.substring(0, 1).toUpperCase();
				String methodName = "get" + temp
						+ dataSourceAttribute.substring(1, dataSourceAttribute.length());
				Method method = Gene.class.getDeclaredMethod(methodName, null);
				Object value = method.invoke(gene, null);
				System.out.println(methodName + "--------" + value);

				if (value != null)
				{
					System.out.println("input Addedd :" + dataSourceName);
					selectedInputDataSourceList.add(dataSourceName);
				}
				temp = outputAttribute.substring(0, 1).toUpperCase();
				methodName = "get" + temp + outputAttribute.substring(1, outputAttribute.length());
				method = Gene.class.getDeclaredMethod(methodName, null);
				value = method.invoke(gene, null);
				if (value != null)
				{
					System.out.println("output Addedd :" + dataSourceName);
					selectedOutputDataSourceList.add(dataSourceName);
				}
			}
		}

		if (mrna != null)
		{
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
				String dataSourceAttribute = (String) map.get(Constants.ATTRIBUTE);
				String outputAttribute = (String) map.get(Constants.OUTPUT_ATTRIBUTE);
				String temp = dataSourceAttribute.substring(0, 1).toUpperCase();
				String methodName = "get" + temp
						+ dataSourceAttribute.substring(1, dataSourceAttribute.length());
				Method method = MessengerRNA.class.getDeclaredMethod(methodName, null);
				Object value = method.invoke(mrna, null);
				System.out.println(methodName + "--------" + value);
				if (value != null)
				{
					System.out.println("input Addedd :" + dataSourceName);
					selectedInputDataSourceList.add(dataSourceName);
				}
				temp = outputAttribute.substring(0, 1).toUpperCase();
				methodName = "get" + temp + outputAttribute.substring(1, outputAttribute.length());
				method = MessengerRNA.class.getDeclaredMethod(methodName, null);
				value = method.invoke(mrna, null);
				if (value != null)
				{
					System.out.println("output Addedd :" + dataSourceName);
					selectedOutputDataSourceList.add(dataSourceName);
				}
			}
		}
		if (protein != null)
		{
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

				String dataSourceAttribute = (String) map.get(Constants.ATTRIBUTE);
				String outputAttribute = (String) map.get(Constants.OUTPUT_ATTRIBUTE);
				String temp = dataSourceAttribute.substring(0, 1).toUpperCase();
				String methodName = "get" + temp
						+ dataSourceAttribute.substring(1, dataSourceAttribute.length());
				Method method = Protein.class.getDeclaredMethod(methodName, null);
				Object value = method.invoke(protein, null);
				System.out.println(methodName + "--------" + value);
				if (value != null)
				{
					System.out.println("input Addedd :" + dataSourceName);
					selectedInputDataSourceList.add(dataSourceName);
				}
				temp = outputAttribute.substring(0, 1).toUpperCase();
				methodName = "get" + temp + outputAttribute.substring(1, outputAttribute.length());
				method = Protein.class.getDeclaredMethod(methodName, null);
				value = method.invoke(protein, null);
				if (value != null)
				{
					System.out.println("output Addedd :" + dataSourceName);
					selectedOutputDataSourceList.add(dataSourceName);
				}
			}
		}
		Float conf = set.getConfidenceScore();
		isGreaterThanEqual = true;
		if (conf != null)
		{
			confScorevalue = conf.toString();
			set.setConfidenceScore(new Float(1));

		}
		
		Collection consensusCollection = set.getConsensusIdentifierDataCollection();
		isFreqGreaterThanEqual = true;
		if (consensusCollection != null && consensusCollection.size() > 0)
		{
			for (Iterator iter = consensusCollection.iterator(); iter.hasNext();)
			{
				ConsensusIdentifierData consensusData = (ConsensusIdentifierData) iter.next();
				Float freq = consensusData.getFrequency();

				GenomicIdentifier genomicIdentifier = consensusData.getGenomicIdentifier();
				if (freq != null && genomicIdentifier != null)
				{
					String str = genomicIdentifier.getClass().getName();
					String genomicClass = str.substring(str.lastIndexOf(".") + 1);
					String dataSourceName = MetadataManager.getDataSourceAttribute(
							Constants.GENOMIC_IDENTIFIER_CLASS, genomicClass,
							Constants.DATASOURCE_NAME);
					GCCriteria f = new GCCriteria();
					System.out.println("Adding Frequency : " + dataSourceName + "---" + freq);
					f.setDataSource(dataSourceName);
					f.setPredicate(freq.floatValue());
					freqList.add(f);
					consensusData.setFrequency(new Float(1));

				}

			}
			set.setConsensusIdentifierDataCollection(null);
		}
		roleName = MetadataManager.getRoleName(Constants.GENOMICIDENTIFIERSET_CLASS_NAME,
				Constants.ONT_CLASS_NAME);
		field = GenomicIdentifierSet.class.getDeclaredField(roleName);

		field.setAccessible(true);
		Collection ontCollection = (Collection) field.get(set);
		if (ontCollection != null && ontCollection.size() > 0)
		{
			for (Iterator it = ontCollection.iterator(); it.hasNext();)
			{
				OrderOfNodeTraversal ont = (OrderOfNodeTraversal) it.next();
				List innerOntList = new ArrayList();
				OrderOfNodeTraversal tempont = ont;
				while (tempont != null)
				{
					roleName = MetadataManager.getRoleName(Constants.ONT_CLASS_NAME,
							Constants.DATASOURCE_CLASS_NAME);
					field = OrderOfNodeTraversal.class.getDeclaredField(roleName);
					field.setAccessible(true);
					DataSource ds = (DataSource) field.get(tempont);

					roleName = MetadataManager.getRoleName(Constants.ONT_CLASS_NAME,
							Constants.LINKTYPE_CLASS_NAME);
					field = OrderOfNodeTraversal.class.getDeclaredField(roleName);
					field.setAccessible(true);
					LinkType link = (LinkType) field.get(tempont);

					if (ds != null && ds.getName() != null)
					{
						innerOntList.add(ds.getName());
					}
					if (link != null && link.getType() != null)
					{
						innerOntList.add(link.getType());
					}
					roleName = "childOrderOfNodeTraversal";
					field = OrderOfNodeTraversal.class.getDeclaredField(roleName);
					field.setAccessible(true);
					OrderOfNodeTraversal nextont = (OrderOfNodeTraversal) field.get(tempont);
					tempont = nextont;
				}

				if (innerOntList.size() > 0)
				{
					ontList.add(innerOntList);
				}

			}
			log.info("ONT selected by USER");
			for (int i = 0; i < ontList.size(); i++)
			{
				log.info(ontList.get(i));
			}
			set.setOrderOfNodeTraversalCollection(null);
		}

	}

	/**
	 * @param ard
	 */
	public static void main(String ard[])
	{
		String hql = "From edu.wustl.geneconnect.domain.GenomicIdentifierSet as xxTargetAliasxx where xxTargetAliasxx.gene.id in (select id From edu.wustl.geneconnect.domain.Gene where ensemblGeneId = 'ENS1') AND xxTargetAliasxx.protein.id in (select id From edu.wustl.geneconnect.domain.Protein where ensemblPeptideAsOutput = true) AND xxTargetAliasxx.orderOfNodeTraversalCollection.id in (select id From edu.wustl.geneconnect.domain.OrderOfNodeTraversal where sourceDataSource.id in (select id From edu.wustl.geneconnect.domain.DataSource where name = 'EnsemblGene') AND linkType.id in (select id From edu.wustl.geneconnect.domain.LinkType where type = 'DIRECT') AND childOrderOfNodeTraversal.id in (select id From edu.wustl.geneconnect.domain.OrderOfNodeTraversal where sourceDataSource.id in (select id From edu.wustl.geneconnect.domain.DataSource where name = 'EnsemblTranscript') AND linkType.id in (select id From edu.wustl.geneconnect.domain.LinkType where type = 'DIRECT') AND childOrderOfNodeTraversal.id in (select id From edu.wustl.geneconnect.domain.OrderOfNodeTraversal where sourceDataSource.id in (select id From edu.wustl.geneconnect.domain.DataSource where name = 'RefSeqmRNA') AND linkType.id in (select id From edu.wustl.geneconnect.domain.LinkType where type = 'DIRECT')))) AND xxTargetAliasxx.orderOfNodeTraversalCollection.id in (select id From edu.wustl.geneconnect.domain.OrderOfNodeTraversal where sourceDataSource.id in (select id From edu.wustl.geneconnect.domain.DataSource where name = 'EnsemblGene') AND linkType.id in (select id From edu.wustl.geneconnect.domain.LinkType where type = 'DIRECT') AND childOrderOfNodeTraversal.id in (select id From edu.wustl.geneconnect.domain.OrderOfNodeTraversal where sourceDataSource.id in (select id From edu.wustl.geneconnect.domain.DataSource where name = 'EnsemblTranscript') AND linkType.id in (select id From edu.wustl.geneconnect.domain.LinkType where type = 'DIRECT') AND childOrderOfNodeTraversal.id in (select id From edu.wustl.geneconnect.domain.OrderOfNodeTraversal where sourceDataSource.id in (select id From edu.wustl.geneconnect.domain.DataSource where name = 'RefSeqmRNA') AND linkType.id in (select id From edu.wustl.geneconnect.domain.LinkType where type = 'DIRECT')))) AND xxTargetAliasxx.consensusIdentifierDataCollection.id in (select id From edu.wustl.geneconnect.domain.ConsensusIdentifierData where frequency = '0.2' where genomicIdentifier.id in (select id From edu.wustl.geneconnect.domain.GenomicIdentifier where dataSource = 'RefSeqProtein'))";
		//String hql = "Ad GenomicIdentifierSet dfC  GenomicIdentifierSet  DRGdfFH";
		List ontIndexList = new ArrayList();
		int i=0;
		while(i>=0)
		{
			System.out.println("i:" +i);
			int index = hql.indexOf("orderOfNodeTraversalCollection.id in",i);
			System.out.println("index " +index);
			if(index>=0)
			{
				ontIndexList.add(new Integer(index));
			}
			else if(index<0)
			{
				break;
			}
			i=index+"orderOfNodeTraversalCollection.id in".length();
		}
		System.out.println("list sizew: " +ontIndexList.size());
		List ontStringList = new ArrayList();
		int n=hql.length();
		for(int j=0;j<ontIndexList.size();)
		{
			int k = ((Integer)ontIndexList.get(j)).intValue();
			j++;
			n=hql.length();
			if(j<ontIndexList.size())
			{
				n=((Integer)ontIndexList.get(j)).intValue();
			}
			if(n<hql.length())
			{
				
				ontStringList.add((hql.substring(k,n)));
			}
			else if(n==hql.length())
			{
				
				int lastIndex = hql.lastIndexOf("xxTargetAliasxx");
				if(lastIndex>0)
				{
					ontStringList.add(hql.substring(k,lastIndex));
				}
				else
				{
					ontStringList.add(hql.substring(k,n));
				}
			}
		}
		for(int listn=0;listn<ontStringList.size();listn++)
		{
			System.out.println("ONT: " + ontStringList.get(listn));
		}	
		
		
	}

	/**
	 * Calls a method of bizlogic for calulating confidence / frequency
	 * @param rs
	 * @throws Exception
	 */
	public void processResult(List rs) throws Exception
	{
		GCBizLogic gcBizlogic = new GCBizLogic();

		//gcBizlogic.prepareResult(rs, selectedOutputDataSourceList);

		log.info("ResultSet Size after prepareResult(): " + rs.size());

		// calulate total no of traversable paths i.e. number of GenomicIdentifierSet
		float totalSet = gcBizlogic.calculateTotalScore(rs, ontList, selectedInputDataSourceList,
				selectedOutputDataSourceList);

		/**
		 * calculate Confidence score for each GenomicIdentiferSet Object and remove 
		 * if not satisfies the condition.
		 */
		gcBizlogic.calculateConfidence(rs, ontList, Float.valueOf(confScorevalue).floatValue(),
				totalSet, isGreaterThanEqual, selectedInputDataSourceList,
				selectedOutputDataSourceList);
		log.info("ResultSet Size after confidence(): " + rs.size());

		/**
		 * Calculate frequency for each genomic identifier and remove the set which not satifies 
		 * the condition given on frequency  
		 */
		gcBizlogic.processFrequency(rs, freqList, totalSet, isFreqGreaterThanEqual);
		log.info("ResultSet Size after frequency(): " + rs.size());

		gcBizlogic.filterForConfidence(rs, Float.valueOf(confScorevalue).floatValue(), totalSet,
				isGreaterThanEqual);
	}

	/**
	 * Create new DetachedCriteria from old one excluding confidence and frequency
	 * @return
	 * @throws Exception
	 */
	public DetachedCriteria createNewCriteria() throws Exception
	{
		/**
		 * Create root DetachedCriteria for GenomicIdentifierSet
		 */
		DetachedCriteria genomicIdSetCriteria = DetachedCriteria
				.forClass(GenomicIdentifierSet.class);
		//System.out.println("caCore deprepnputDsList.size() :  " + inputDsList.size());
		Map addedAssociation = new HashMap();
		for (int i = 0; i < selectedInputValueList.size(); i++)
		{
			String predicate = (String) selectedInputValueList.get(i);
			int ind = predicate.indexOf("=");
			String dsName = predicate.substring(0, ind);

			String dsValue = predicate.substring(ind + 1, predicate.length());

			// Get Class of data source i.e Gene,MRNA or Protein
			String className = MetadataManager.getDataSourceAttribute(Constants.DATASOURCE_NAME,
					dsName, Constants.CLASS);

			String roleName = MetadataManager.getRoleName("GenomicIdentifierSet", className);

			// get attribute name of Gene.MRNA or protein representing data source 
			String classAttribute = MetadataManager.getDataSourceAttribute(
					Constants.DATASOURCE_NAME, dsName, Constants.ATTRIBUTE);
			// get type of attribute
			String attributeType = MetadataManager.getDataSourceAttribute(
					Constants.DATASOURCE_NAME, dsName, Constants.TYPE);
		
			log.info("Adding " + roleName + ":" + classAttribute);
			/**
			 * Create DetachedCriteria for search on given datasource and its genomicId
			 */
			/**
			 * Check if Criteria already created.IF yed teh get teh DetachedCriteria object from Map
			 * else crete new DetachedCriteria 
			 */
			DetachedCriteria genomicCriteria = (DetachedCriteria) addedAssociation.get(roleName);
			if (genomicCriteria == null)
			{
				genomicCriteria = genomicIdSetCriteria.createCriteria(roleName);
				addedAssociation.put(roleName, genomicCriteria);
			}
			if (attributeType.equalsIgnoreCase("java.lang.Long"))
			{
				Long longValue = null;
				try
				{
					longValue = new Long(dsValue);
				}
				catch (Exception e)
				{
					log.error(e.getMessage(), e);
					throw new Exception("Genomic Identifier for DataSource " + dsName
							+ " must be Integer");
				}
				genomicCriteria.add(Restrictions.eq(classAttribute, longValue));
			}
			else
			{
				genomicCriteria.add(Restrictions.eq(classAttribute, dsValue));
			}
		}

		for (int j = 0; j < selectedOutputDataSourceList.size(); j++)
		{
			String outputDSName = (String) selectedOutputDataSourceList.get(j);
			String outputClassName = MetadataManager.getDataSourceAttribute(
					Constants.DATASOURCE_NAME, outputDSName, Constants.CLASS);
			String roleName = MetadataManager.getRoleName("GenomicIdentifierSet", outputClassName);
			DetachedCriteria outputGenomicCriteria = (DetachedCriteria) addedAssociation
					.get(roleName);
			if (outputGenomicCriteria == null)
			{
				outputGenomicCriteria = genomicIdSetCriteria.createCriteria(roleName);
				addedAssociation.put(roleName, outputGenomicCriteria);
			}

			String outputAttribute = MetadataManager.getDataSourceAttribute(
					Constants.DATASOURCE_NAME, outputDSName, Constants.OUTPUT_ATTRIBUTE);
			log.info("Adding " + roleName + ":" + outputAttribute);
			outputGenomicCriteria.add(Restrictions.eq(outputAttribute, new Boolean(true)));
		}
		Map currentNode = new HashMap();

		if (ontList.size() > 0)
		{
			DetachedCriteria ontCrit = genomicIdSetCriteria
					.createCriteria("orderOfNodeTraversalCollection");
			currentNode.put(CURRENT_NODE, ontCrit);
		}
		for (int i = 0; i < ontList.size(); i++)
		{

			DetachedCriteria ontCrit = (DetachedCriteria) currentNode.get(CURRENT_NODE);
			GCCriteria f = (GCCriteria) ontList.get(i);
			String ds = f.getDataSource();
			List type = f.getType();
			String linkType = null;
			if (type.size() > 0)
				linkType = (String) type.get(0);
			ontCrit.createCriteria("sourceDataSource").add(Restrictions.eq("name", ds));
			log.info("Adding ONT ds " + ds);
			DetachedCriteria ontCritF1 = null;
			if (linkType != null)
			{
				ontCrit.createCriteria("linkType").add(Restrictions.eq("type", linkType));
				ontCritF1 = ontCrit.createCriteria("childOrderOfNodeTraversal");
				currentNode.remove(CURRENT_NODE);
				currentNode.put(CURRENT_NODE, ontCritF1);
				log.info("Adding ONT link " + linkType);
			}
			else
			{
				ontCrit.add(Restrictions.isNull("childOrderOfNodeTraversal"));
				log.info("Adding ISNULL next ");
			}
		}
		return genomicIdSetCriteria;
	}

	public boolean isFreqGreaterThanEqual()
	{
		return isFreqGreaterThanEqual;
	}

	public boolean isGreaterThanEqual()
	{
		return isGreaterThanEqual;
	}

	public List getSelectedInputDataSourceList()
	{
		return selectedInputDataSourceList;
	}

	public List getSelectedInputValueList()
	{
		return selectedInputValueList;
	}

	public List getSelectedOutputDataSourceList()
	{
		return selectedOutputDataSourceList;
	}

	public boolean isCreateNewCriteria()
	{
		return createNewCriteria;
	}

}
