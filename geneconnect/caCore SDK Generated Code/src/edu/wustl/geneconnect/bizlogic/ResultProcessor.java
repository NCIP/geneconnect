
package edu.wustl.geneconnect.bizlogic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.impl.CriteriaImpl;

import edu.wustl.geneconnect.domain.GenomicIdentifierSet;
import edu.wustl.geneconnect.utility.Constants;
import edu.wustl.geneconnect.utility.MetadataManager;

/**
 * Provides method to interpret DetachedCriteria and creation of new DetachedCriteria
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
		//		List ontValueList = new ArrayList();
		//		List ontNameList = new ArrayList();
		//		Map ontMap = new HashMap();
		//		
		//		List inputDsList=new ArrayList();
		//		List outputDsList=new ArrayList();

		int freqCounter = -1;
		int dsCounter = -1;

		String intValue = "0";
		String strValue = "0";
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

			//System.out.println("CLASSNAME SACHin:" + a.getCriteria().toString());
			String associationClassTemp = a.getCriteria().toString();
			int ind1 = associationClassTemp.indexOf("(");
			int ind2 = associationClassTemp.indexOf(":");
			String associationClass = associationClassTemp.substring(ind1 + 1, ind2);
			//System.out.println("associationClass* ************* " + associationClass);	
			//System.out.println("Expression Predicates* ************* " + predicate);
			log.info("associationClass* ************* " + associationClass);
			log.info("Expression Predicates* ************* " + predicate);
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
					//System.out.println("SDACHIN role class: " + attrClass);
					String dsName = MetadataManager.getDataSourceAttribute(Constants.ATTRIBUTE,
							temp, Constants.CLASS, attrClass, Constants.DATASOURCE_NAME);

					//System.out.println("SDACHIN data sourece : " + dsName);
					if (dsName != null)
					{
						log.info("selectedInputDataSourceList: " + dsName);
						selectedInputDataSourceList.add(dsName);

						String value = predicate.substring(temp.length() + 1, predicate.length());
						selectedInputValueList.add(dsName + "=" + value);

						//System.out.println("INPUT VALUE = " + temp + "=" + value);
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

					//ontValueList.add(strValue);
					//ontNameList.add("name");
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
					//ontList.add(strValue);
					//ontValueList.add(strValue);
					//ontNameList.add("type");
				}
			}
		}
		//System.out.println("ontList : " + ontList.size());
		log.info("ONT predicate list size: " + ontList.size());
		//		for (int i = 0; i < ontList.size(); i++)
		//		{
		//
		//			GCCriteria f = (GCCriteria) ontList.get(i);
		//			String ds = f.getDataSource();
		//			List type = f.getType();
		//			for (int k = 0; k < type.size(); k++)
		//			{
		//				System.out.print(f.getDataSource() + "-->" + type.get(k));
		//			}
		//		}
		log.info("Frequency predicate list size: " + freqList.size());

	}

	/**
	 * Calls a method of bizlogic for calulating confidence / frequency
	 * @param rs
	 * @throws Exception
	 */
	public void processResult(List rs) throws Exception
	{
		GCBizLogic gcBizlogic = new GCBizLogic();

		gcBizlogic.prepareResult(rs, selectedOutputDataSourceList);

		log.info("ResultSet Size after prepareResult(): " + rs.size());

		// calulate total no of traversable paths i.e. number of GenomicIdentifierSet
		float totalSet = gcBizlogic.calculateTotalScore(rs, ontList);

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

			//			className = className.substring(0, 1).toLowerCase()
			//					+ className.substring(1, className.length());

			//			System.out.println("Data Source ROLE :  " + roleName);
			//			System.out.println("Data Source ATTRIBUTE:  " + classAttribute);
			//			System.out.println("Data Source ATTRIBUTE TYPE :  " + attributeType);
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
			//Logger.out.info("Added Op Attribute :" +outputAttribute);
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
				System.out.println("LINK ADDED " + linkType);
			}
		}
		System.out.println(genomicIdSetCriteria);
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
