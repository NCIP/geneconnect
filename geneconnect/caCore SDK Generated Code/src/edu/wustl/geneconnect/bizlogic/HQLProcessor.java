
package edu.wustl.geneconnect.bizlogic;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.wustl.geneconnect.domain.ConsensusIdentifierData;
import edu.wustl.geneconnect.domain.DataSource;
import edu.wustl.geneconnect.domain.GenomicIdentifier;
import edu.wustl.geneconnect.domain.GenomicIdentifierSet;
import edu.wustl.geneconnect.domain.LinkType;
import edu.wustl.geneconnect.domain.OrderOfNodeTraversal;
import edu.wustl.geneconnect.utility.Constants;
import edu.wustl.geneconnect.utility.MetadataManager;
/**
 * This class interprets trhe HQL string from caGrid data service and creates teh domain object 
 * @author sachin_lale
 *
 */
public class HQLProcessor
{

	private static Logger log = Logger.getLogger(HQLProcessor.class.getName());

	//	public HQLProcessor()
	//	{
	//		String dbURL = "jdbc:oracle:thin:@" + "10.88.177.27" + ":" + "1521" + ":" + "CHIPDB";
	//		try
	//		{
	//			/** load the driver, which also registers the driver*/
	//			Class.forName("oracle.jdbc.driver.OracleDriver");
	//			Connection connection = DriverManager.getConnection(dbURL, "sachin", "sachin");
	//			MetadataManager.connect(connection);
	//
	//			connection.setAutoCommit(false);
	//		}
	//		catch (ClassNotFoundException e)
	//		{
	//			e.printStackTrace();		}
	//		catch (SQLException sqlEx)
	//		{
	//			/** Unable to establish a connection through the driver manager.*/
	//			sqlEx.printStackTrace();
	//		}
	//		catch (Exception Ex)
	//		{
	//			/** Unable to establish a connection through the driver manager.*/
	//			Ex.printStackTrace();
	//		}
	//		 
	//	}

	/**
	 * This method calls the iterpretation of hql String and return the GenomicIdentiiferSet object.
	 * @param hqlString
	 * @return GenomicIdentifierSet
	 * @throws Exception
	 */

	public GenomicIdentifierSet interpretHQL(String hqlString) throws Exception
	{
		Map inputOutputMap = interpretInputOutputDataSource(hqlString);
		GenomicIdentifierSet set = null;
		if (inputOutputMap != null)
		{
			set = getGenomicIdentifierSet(inputOutputMap);
		}
		List ontList = intrepretOnt(hqlString);
		if (ontList != null)
		{
			List ontCollection = getOntCollection(ontList);
			set.setOrderOfNodeTraversalCollection(ontCollection);
		}
		Map frequencyMap = interpretFrequency(hqlString);
		if (frequencyMap != null)
		{
			List consensusColelction = getConsensusCollection(frequencyMap);
			set.setConsensusIdentifierDataCollection(consensusColelction);
		}
		return set;
	}

	/**
	 * Prepares the colelction of ConsensusDataIdentifier object from interpreted hql string 
	 * @param frequencyMap
	 * @return
	 * @throws Exception
	 */
	List getConsensusCollection(Map frequencyMap) throws Exception
	{

		List consensusCollection = new ArrayList();
		Set keySet = frequencyMap.keySet();
		for (Iterator keyiter = keySet.iterator(); keyiter.hasNext();)
		{
			List l = (List) frequencyMap.get(keyiter.next());
			String frequencyValue = (String) l.get(0);
			String genomicClassname = (String) l.get(1);
			GenomicIdentifier genomicObject = (GenomicIdentifier) Class.forName(genomicClassname)
					.newInstance();
			ConsensusIdentifierData data = new ConsensusIdentifierData();
			data.setFrequency(new Float(frequencyValue));
			data.setGenomicIdentifier(genomicObject);
			consensusCollection.add(data);
		}
		if (consensusCollection.size() > 0)
		{
			return consensusCollection;
		}
		return null;
	}

	/**
	 * Interpret consensus data from hql and returnns the Map where Key="Frequency_"+i and value = list where list[0] is frequency value
	 * and list[1] is Genomciidentifier class name.
	 * @param hqlString
	 * @return
	 */
	Map interpretFrequency(String hqlString)
	{
		Map frequencyMap = new HashMap();
		List frequencyIndexList = new ArrayList();
		int i = 0;
		/**
		 * look for 'consensusIdentifierDataCollection.id in' string in hql and prepare a list of strings starting 
		 * with'consensusIdentifierDataCollection.id in'. 
		 */
		while (i >= 0)
		{
			//System.out.println("i:" +i);

			int index = hqlString.indexOf(Constants.SEARCH_FOR_CONSENSUS_IDENTIFIER, i);
			//System.out.println("index " +index);
			if (index >= 0)
			{
				frequencyIndexList.add(new Integer(index));
			}
			else if (index < 0)
			{
				break;
			}
			i = index + "Constants.SEARCH_FOR_CONSENSUS_IDENTIFIER".length();
		}
		//System.out.println("list sizew: " +frequencyIndexList.size());
		List frequencyStringList = new ArrayList();
		int n = hqlString.length();

		for (int j = 0; j < frequencyIndexList.size();)
		{
			int k = ((Integer) frequencyIndexList.get(j)).intValue();
			j++;
			n = hqlString.length();
			if (j < frequencyIndexList.size())
			{

				n = ((Integer) frequencyIndexList.get(j)).intValue();
				//System.out.println("val of n :"+n);
			}

			if (n < hqlString.length())
			{

				frequencyStringList.add((hqlString.substring(k, n)));
			}
			else if (n == hqlString.length())
			{

				int lastIndex = hqlString.lastIndexOf(Constants.SEARCH_FOR_TARGETALIAS);
				//System.out.println("val of lastIndex :"+lastIndex);
				//System.out.println("val of k :"+k);
				if (lastIndex > 0 && lastIndex > k)
				{
					frequencyStringList.add(hqlString.substring(k, lastIndex));
				}
				else
				{
					frequencyStringList.add(hqlString.substring(k, n));
				}
			}
		}
		/**
		 * iterate over above list from  each string look for 'frequency' string and get its value by getting substring starting from 
		 * '=' operator. similar for GenomicIdentifier class
		 */
		for (i = 0; i < frequencyStringList.size(); i++)
		{
			//System.out.println(frequencyStringList.get(i));
			String frequencyString = (String) frequencyStringList.get(i);
			int indexOfconfidence = frequencyString.indexOf(Constants.SEARCH_FOR_FREQUENCY);
			String value = null;
			if (indexOfconfidence > 0)
			{
				String valueString = frequencyString.substring(indexOfconfidence
						+ Constants.SEARCH_FOR_FREQUENCY.length());
				int indexStartValue = valueString.indexOf("'");
				int indexEndOfValue = valueString.indexOf("'", indexStartValue + 1);
				value = valueString.substring(indexStartValue + 1, indexEndOfValue);
				log.info("Set frequency====" + value);
			}
			List dsl = null;
			String[] dataSourceClass = new String[]{"Gene", "MessengerRNA", "Protein"};
			for (int dscnt = 0; dscnt < dataSourceClass.length; dscnt++)
			{
				dsl = MetadataManager.getAttibutes(Constants.CLASS, dataSourceClass[dscnt]);

				for (int j = 0; j < dsl.size(); j++)
				{
					Map map = (Map) dsl.get(j);
					String dataSourceClassName = (String) map
							.get(Constants.GENOMIC_IDENTIFIER_CLASS);
					String dataSourceClassWithPackage = Constants.DOMAIN_CLASSNAME_PREFIX + "."
							+ dataSourceClassName;
					log.info("dataSourceClassWithPackage : " + dataSourceClassWithPackage);
					if (frequencyString.indexOf(dataSourceClassWithPackage) > 0 && value != null)
					{
						List freq = new ArrayList();
						freq.add(value);
						freq.add(dataSourceClassWithPackage);
						frequencyMap.put("Frequency_" + i, freq);
					}

				}
			}

		}
		Set keySet = frequencyMap.keySet();
		if (keySet != null && keySet.size() > 0)
		{
			return frequencyMap;
		}

		return null;
	}

	/**
	 * Interpret the ONT from hql and return the list of ONTs.
	 * @param hqlString
	 * @return
	 */
	List intrepretOnt(String hqlString)
	{
		List ontIndexList = new ArrayList();
		int i = 0;

		/**
		 * look for 'orderOfNodeTraversalCollection.id in' string and prepare a list of string where each string starts with
		 * 'orderOfNodeTraversalCollection.id in'.
		 */
		while (i >= 0)
		{
			//System.out.println("i:" +i);
			int index = hqlString.indexOf(Constants.SEARCH_FOR_ONT, i);
			//System.out.println("index " +index);
			if (index >= 0)
			{
				ontIndexList.add(new Integer(index));
			}
			else if (index < 0)
			{
				break;
			}
			i = index + Constants.SEARCH_FOR_ONT.length();
		}
		//System.out.println("list sizew: " +ontIndexList.size());
		List ontStringList = new ArrayList();
		int n = hqlString.length();
		for (int j = 0; j < ontIndexList.size();)
		{
			int k = ((Integer) ontIndexList.get(j)).intValue();
			j++;
			n = hqlString.length();
			if (j < ontIndexList.size())
			{

				n = ((Integer) ontIndexList.get(j)).intValue();
				//System.out.println("val of n :"+n);
			}

			if (n < hqlString.length())
			{

				ontStringList.add((hqlString.substring(k, n)));
			}
			else if (n == hqlString.length())
			{

				int lastIndex = hqlString.lastIndexOf("Constants.SEARCH_FOR_TARGETALIAS");
				//System.out.println("val of lastIndex :"+lastIndex);
				//System.out.println("val of k :"+k);
				if (lastIndex > 0 && lastIndex > k)
				{
					ontStringList.add(hqlString.substring(k, lastIndex));
				}
				else
				{
					ontStringList.add(hqlString.substring(k, n));
				}
			}
		}
		Map ontmap = new HashMap();
		StringBuffer tempontString = new StringBuffer();
		/**
		 * iterate over a above list and llok for datasource='' and linktype ='' combination.
		 * split out the value from string by finding out the '='operator on string.
		 * 
		 * Preapare a Map of list where key = 'DataSource_i' or 'LinkType_i' where i is an integer.
		 * for 2 occurance of 'orderOfNodeTraversalCollection.id in' string the map will store key as 
		 * 'DataSource_0' 
		 * 'LinkType_0'
		 * 'DataSource_1' 
		 * 'LinkType_1'
		 */
		for (int listn = 0; listn < ontStringList.size(); listn++)
		{
			//System.out.println("ONT: " + ontStringList.get(listn));
			String ontString = (String) ontStringList.get(listn);
			tempontString.setLength(0);
			tempontString.append(ontString);
			int indexOfname = 0;
			List dataSourceList = new ArrayList();
			while (indexOfname >= 0)
			{
				indexOfname = ontString.indexOf(Constants.SEARCH_FOR_DATASOURCE, indexOfname);
				String startofValue = ontString.substring(indexOfname
						+ Constants.SEARCH_FOR_DATASOURCE.length());
				indexOfname = indexOfname + Constants.SEARCH_FOR_DATASOURCE.length();
				tempontString.setLength(0);
				tempontString.append(startofValue);
				//System.out.println("startofValue :"+startofValue);

				int indexStartValue = startofValue.indexOf("'");
				int indexEndOfValue = startofValue.indexOf("'", indexStartValue + 1);
				//System.out.println("Value " +startofValue.substring(indexStartValue+1,indexEndOfValue));
				dataSourceList.add(startofValue.substring(indexStartValue + 1, indexEndOfValue));
				if (tempontString.indexOf(Constants.SEARCH_FOR_DATASOURCE) < 0)
				{
					break;
				}
			}
			if (dataSourceList.size() > 0)
			{
				//ontList.add(innerDataSourceList);
				ontmap.put("DataSource_" + listn, dataSourceList);
			}
			tempontString.setLength(0);
			tempontString.append(ontString);
			int indexOftype = 0;
			List linkTypeList = new ArrayList();
			while (indexOftype >= 0)
			{
				indexOftype = ontString.indexOf(Constants.SEARCH_FOR_LINKTYPE, indexOftype);
				String startofValue = ontString.substring(indexOftype
						+ Constants.SEARCH_FOR_LINKTYPE.length());
				indexOftype = indexOftype + Constants.SEARCH_FOR_LINKTYPE.length();
				tempontString.setLength(0);
				tempontString.append(startofValue);
				//System.out.println("startofValue :"+startofValue);

				int indexStartValue = startofValue.indexOf("'");
				int indexEndOfValue = startofValue.indexOf("'", indexStartValue + 1);
				//System.out.println("Value " +startofValue.substring(indexStartValue+1,indexEndOfValue));
				linkTypeList.add(startofValue.substring(indexStartValue + 1, indexEndOfValue));
				if (tempontString.indexOf(Constants.SEARCH_FOR_LINKTYPE) < 0)
				{
					break;
				}
			}
			if (linkTypeList.size() > 0)
			{
				//ontList.add(innerDataSourceList);
				ontmap.put("LinkType_" + listn, linkTypeList);
			}

		}
		List ontList = new ArrayList();
		/**
		 * Iterate over a Map and combile 'DataSource_' and 'LinkType_'.
		 * Prepare a list where odd elements is datasource and even elemnts is a linktype.
		 */
		for (int listn = 0; listn < ontStringList.size(); listn++)
		{
			String dataSourcekey = "DataSource_" + listn;
			String linkTypekey = "LinkType_" + listn;
			List dsList = (List) ontmap.get(dataSourcekey);
			List linkList = (List) ontmap.get(linkTypekey);
			log.info("dsList " + dsList);
			log.info("linkList " + linkList);
			if ((dsList != null && linkList != null) && (dsList.size() == (linkList.size() + 1)))
			{
				List innerontList = new ArrayList();
				//StringBuffer dataSourceName = new StringBuffer();
				//StringBuffer linkType = new StringBuffer();
				for (int j = 0; j < dsList.size(); j++)
				{
					//dataSourceName.setLength(0);
					//linkType.setLength(0);

					innerontList.add((String) dsList.get(j));
					if (j < linkList.size())
					{
						innerontList.add((String) linkList.get(j));
					}
				}
				if (innerontList.size() > 0)
				{
					ontList.add(innerontList);
				}
			}
		}
		if (ontList.size() > 0)
		{
			log.info(ontList);
			return ontList;
		}

		return null;
	}

	/**
	 * prepare the ONT collection objects from the interpretd hql.
	 * @param ontStringList
	 * @return
	 */
	List getOntCollection(List ontStringList)
	{
		List ontCollection = new ArrayList();
		for (int i = 0; i < ontStringList.size(); i++)
		{
			List innerList = (List) ontStringList.get(i);
			boolean isdataSourceToken = true;
			List tempOntList = new ArrayList();
			OrderOfNodeTraversal headONT = null;
			boolean isTosetHead = true;
			OrderOfNodeTraversal prevONT = null;
			for (int j = 0; j < innerList.size();)
			{
				String dataSourcename = (String) innerList.get(j);
				String link = null;
				j++;
				if (j < innerList.size())
				{
					link = (String) innerList.get(j);
				}

				OrderOfNodeTraversal orderOfNodeTraversal = new OrderOfNodeTraversal();
				DataSource dataSource = new DataSource();

				dataSource.setName(dataSourcename);
				orderOfNodeTraversal.setSourceDataSource(dataSource);
				if (link != null)
				{
					LinkType linkType = new LinkType();
					linkType.setType(link);
					orderOfNodeTraversal.setLinkType(linkType);
				}
				if (isTosetHead)
				{
					headONT = orderOfNodeTraversal;
					isTosetHead = false;
				}
				if (prevONT != null)
				{
					prevONT.setChildOrderOfNodeTraversal(orderOfNodeTraversal);
				}
				prevONT = orderOfNodeTraversal;
				j++;

			}
			ontCollection.add(headONT);
		}
		if (ontCollection.size() > 0)
		{
			return ontCollection;
		}
		return null;
	}

	/**
	 * Interpret selected intput datasource and output data source and returns the Map where key = Input or Output and value = 
	 * value given by user. 
	 * @param hqlString
	 * @return
	 * @throws Exception
	 */
	Map interpretInputOutputDataSource(String hqlString) throws Exception
	{
		Map inputOutputMap = new HashMap();
		List dsl = null;
		/**
		 * Loop for each doain calss and look for the data source values set.
		 *  Get the corresponding values from string and set into map as Input_datasourcename or output_datasourcename
		 *   
		 */
		String domainClass[] = new String[]{"Gene", "Protein", "MessengerRNA"};
		for (int domainClassCnt = 0; domainClassCnt < domainClass.length; domainClassCnt++)
		{
			String searchFor = "";
			if (domainClass[domainClassCnt].equalsIgnoreCase("Gene"))
			{
				searchFor = Constants.SEARCH_FOR_GENE;
			}
			else if (domainClass[domainClassCnt].equalsIgnoreCase("Protein"))
			{
				searchFor = Constants.SEARCH_FOR_PROTEIN;
			}
			else if (domainClass[domainClassCnt].equalsIgnoreCase("MessengerRNA"))
			{
				searchFor = Constants.SEARCH_FOR_MRNA;
			}
			int geneIndex = hqlString.indexOf(searchFor);
			if (geneIndex > 0)
			{
				int lastgeneIndex = hqlString.indexOf(")", geneIndex);
				String geneHQLString = hqlString.substring(geneIndex, lastgeneIndex);
				dsl = MetadataManager.getAttibutes(Constants.CLASS, domainClass[domainClassCnt]);
				for (int j = 0; j < dsl.size(); j++)
				{
					Map map = (Map) dsl.get(j);
					String dataSourceName = (String) map.get(Constants.DATASOURCE_NAME);
					String dataSourceAttribute = (String) map.get(Constants.ATTRIBUTE);
					String outputAttribute = (String) map.get(Constants.OUTPUT_ATTRIBUTE);
					int indexOfInputAttribute = geneHQLString.indexOf(dataSourceAttribute);
					if (indexOfInputAttribute > 0)
					{
						String valueString = geneHQLString.substring(indexOfInputAttribute
								+ dataSourceAttribute.length());
						int indexStartValue = valueString.indexOf("'");
						int indexEndOfValue = valueString.indexOf("'", indexStartValue + 1);
						String value = valueString.substring(indexStartValue + 1, indexEndOfValue);
						log.info("Set input :" + dataSourceAttribute + "====" + value);
						inputOutputMap.put("Input_" + dataSourceName, value);
					}
					int indexOfOutputAttribute = geneHQLString.indexOf(outputAttribute);
					if (indexOfOutputAttribute > 0)
					{
						inputOutputMap.put("Output_" + dataSourceName, "true");
						log.info("Set Output :" + outputAttribute);
					}

				}
			}
		}
		/**
		 * Looh for confidence score is given in query or not.
		 * if given spilt the value from string and add it into map.
		 */
		int indexOfconfidence = hqlString.indexOf(Constants.SEARCH_FOR_CONFIDENCE);
		if (indexOfconfidence > 0)
		{
			String valueString = hqlString.substring(indexOfconfidence
					+ Constants.SEARCH_FOR_CONFIDENCE.length());
			int indexStartValue = valueString.indexOf("'");
			int indexEndOfValue = valueString.indexOf("'", indexStartValue + 1);
			String value = valueString.substring(indexStartValue + 1, indexEndOfValue);
			log.info("Set confidence====" + value);
			inputOutputMap.put("ConfidenceScore", value);
		}
		if (inputOutputMap.keySet().size() > 0)
		{
			log.info("Retunning MAP");
			return inputOutputMap;
		}

		return null;
	}

	/**
	 * Prepares the GenomicIdentifierSet object from interpreted Hql
	 * @param inputOutputMap
	 * @return
	 * @throws Exception
	 */
	GenomicIdentifierSet getGenomicIdentifierSet(Map inputOutputMap) throws Exception
	{
		Set mapKeys = inputOutputMap.keySet();
		Map addedAssociation = new HashMap();
		GenomicIdentifierSet genomicIdentifierSet = new GenomicIdentifierSet();
		for (Iterator keyIter = mapKeys.iterator(); keyIter.hasNext();)
		{
			String key = (String) keyIter.next();
			boolean isInput = false;
			if (key.startsWith("Input_"))
			{
				isInput = true;
			}
			if (key.startsWith("ConfidenceScore"))
			{
				String val = (String) inputOutputMap.get(key);
				genomicIdentifierSet.setConfidenceScore(new Float(val));
				continue;
			}
			String val = (String) inputOutputMap.get(key);
			String dataSourcename = key.substring(key.indexOf("_") + 1);
			String className = MetadataManager.getDataSourceAttribute(Constants.DATASOURCE_NAME,
					dataSourcename, Constants.CLASS);

			String roleName = MetadataManager.getRoleName("GenomicIdentifierSet", className);

			/** get attribute name of Gene.MRNA or protein representing data source
			 * depends on input or output get appropriate field.
			 */ 
			String classAttribute = null;
			
			if (isInput)
			{
				classAttribute = MetadataManager.getDataSourceAttribute(Constants.DATASOURCE_NAME,
						dataSourcename, Constants.ATTRIBUTE);
			}
			else
			{

				classAttribute = MetadataManager.getDataSourceAttribute(Constants.DATASOURCE_NAME,
						dataSourcename, Constants.OUTPUT_ATTRIBUTE);
			}
			// get type of attribute
			String attributeType = MetadataManager.getDataSourceAttribute(
					Constants.DATASOURCE_NAME, dataSourcename, Constants.TYPE);

			/**
			 * Check if Gene,MRNA,protein object already created.IF yes then get the Gene or MRNA OR protein  object from Map
			 * else crete new. 
			 */
			Class associationClass = null;
			Object associationObject = addedAssociation.get(roleName);

			associationClass = Class.forName(Constants.DOMAIN_CLASSNAME_PREFIX + "." + className);
			if (associationObject == null)
			{
				/**
				 * The genomic(gene,mrna,protein) object is not set so create new.
				 * set the object to GenomicIDSEt object and add into Map   
				 */
				associationObject = associationClass.newInstance();
				log.info("associationObject null creating new for class :" + className);
				String temp = roleName.substring(0, 1).toUpperCase();
				String methodName = "set" + temp + roleName.substring(1, roleName.length());
				//System.out.println("methodName: " +methodName);

				Class[] paramClass = new Class[]{associationClass};
				Object[] paramObject = new Object[]{associationObject};
				Method method = GenomicIdentifierSet.class
						.getDeclaredMethod(methodName, paramClass);
				method.invoke(genomicIdentifierSet, paramObject);
				addedAssociation.put(roleName, associationObject);

			}
			/**
			 * call appropriate set method to set values of given data source
			 */
			String temp = classAttribute.substring(0, 1).toUpperCase();
			String methodName = "set" + temp + classAttribute.substring(1, classAttribute.length());
			Method method = null;
			Class[] attributes = null;

			Object[] attributesValue = null;
			if (isInput)
			{
				if (attributeType.equalsIgnoreCase("java.lang.Long"))
				{
					attributes = new Class[]{Long.class};
					method = associationClass.getDeclaredMethod(methodName, attributes);
					attributesValue = new Object[]{new Long(val)};
				}
				else
				{
					attributes = new Class[]{String.class};
					method = associationClass.getDeclaredMethod(methodName, attributes);
					attributesValue = new Object[]{new String(val)};
				}
			}
			else
			{
				attributes = new Class[]{Boolean.class};
				method = associationClass.getDeclaredMethod(methodName, attributes);
				attributesValue = new Object[]{new Boolean("true")};

			}
			method.invoke(associationObject, attributesValue);
			log.info("Attribute set :" + classAttribute);
		}
		return genomicIdentifierSet;
	}

	public static void main(String ard[])
	{
		try
		{
			//String hqlString = "From edu.wustl.geneconnect.domain.GenomicIdentifierSet as xxTargetAliasxx where xxTargetAliasxx.gene.id in (select id From edu.wustl.geneconnect.domain.Gene where ensemblGeneId = 'ENS1') AND xxTargetAliasxx.protein.id in (select id From edu.wustl.geneconnect.domain.Protein where ensemblPeptideAsOutput = true) AND xxTargetAliasxx.orderOfNodeTraversalCollection.id in (select id From edu.wustl.geneconnect.domain.OrderOfNodeTraversal where sourceDataSource.id in (select id From edu.wustl.geneconnect.domain.DataSource where name = 'EnsemblGene') AND linkType.id in (select id From edu.wustl.geneconnect.domain.LinkType where type = 'DIRECT') AND childOrderOfNodeTraversal.id in (select id From edu.wustl.geneconnect.domain.OrderOfNodeTraversal where sourceDataSource.id in (select id From edu.wustl.geneconnect.domain.DataSource where name = 'EnsemblTranscript') AND linkType.id in (select id From edu.wustl.geneconnect.domain.LinkType where type = 'DIRECT') AND childOrderOfNodeTraversal.id in (select id From edu.wustl.geneconnect.domain.OrderOfNodeTraversal where sourceDataSource.id in (select id From edu.wustl.geneconnect.domain.DataSource where name = 'RefSeqmRNA') ))) AND xxTargetAliasxx.orderOfNodeTraversalCollection.id in (select id From edu.wustl.geneconnect.domain.OrderOfNodeTraversal where sourceDataSource.id in (select id From edu.wustl.geneconnect.domain.DataSource where name = 'Entrez Gene') AND linkType.id in (select id From edu.wustl.geneconnect.domain.LinkType where type = 'DIRECT') AND childOrderOfNodeTraversal.id in (select id From edu.wustl.geneconnect.domain.OrderOfNodeTraversal where sourceDataSource.id in (select id From edu.wustl.geneconnect.domain.DataSource where name = 'UniGene') AND linkType.id in (select id From edu.wustl.geneconnect.domain.LinkType where type = 'DIRECT') AND childOrderOfNodeTraversal.id in (select id From edu.wustl.geneconnect.domain.OrderOfNodeTraversal where sourceDataSource.id in (select id From edu.wustl.geneconnect.domain.DataSource where name = 'RefSeqmRNA')))) AND xxTargetAliasxx.consensusIdentifierDataCollection.id in (select id From edu.wustl.geneconnect.domain.ConsensusIdentifierData where frequency = '0.2' where genomicIdentifier.id in (select id From edu.wustl.geneconnect.domain.GenomicIdentifier where dataSource = 'RefSeqProtein'))";
			//String hqlString ="From edu.wustl.geneconnect.domain.GenomicIdentifierSet as xxTargetAliasxx where xxTargetAliasxx.gene.id in (select id From edu.wustl.geneconnect.domain.Gene where ensemblGeneId = 'ENS2') AND xxTargetAliasxx.messengerRNA.id in (select id From edu.wustl.geneconnect.domain.MessengerRNA where ensemblTranscriptAsOutput = true) AND xxTargetAliasxx.orderOfNodeTraversalCollection.id in (select id From edu.wustl.geneconnect.domain.OrderOfNodeTraversal where sourceDataSource.id in (select id From edu.wustl.geneconnect.domain.DataSource where name = 'EnsemblGene') AND linkType.id in (select id From edu.wustl.geneconnect.domain.LinkType where type = 'DIRECT') AND childOrderOfNodeTraversal.id in (select id From edu.wustl.geneconnect.domain.OrderOfNodeTraversal where sourceDataSource.id in (select id From edu.wustl.geneconnect.domain.DataSource where name = 'EnsemblTranscript') AND linkType.id in (select id From edu.wustl.geneconnect.domain.LinkType where type = 'DIRECT') AND childOrderOfNodeTraversal.id in (select id From edu.wustl.geneconnect.domain.OrderOfNodeTraversal where sourceDataSource.id in (select id From edu.wustl.geneconnect.domain.DataSource where name = 'RefSeqmRNA'))))";
			//String hqlString ="From edu.wustl.geneconnect.domain.GenomicIdentifierSet as xxTargetAliasxx where xxTargetAliasxx.gene.id in (select id From edu.wustl.geneconnect.domain.Gene where ensemblGeneId = 'ENS2') AND xxTargetAliasxx.messengerRNA.id in (select id From edu.wustl.geneconnect.domain.MessengerRNA where ensemblTranscriptAsOutput = true) AND xxTargetAliasxx.orderOfNodeTraversalCollection.id in (select id From edu.wustl.geneconnect.domain.OrderOfNodeTraversal where sourceDataSource.id in (select id From edu.wustl.geneconnect.domain.DataSource where name = 'EnsemblGene') AND linkType.id in (select id From edu.wustl.geneconnect.domain.LinkType where type = 'DIRECT') AND childOrderOfNodeTraversal.id in (select id From edu.wustl.geneconnect.domain.OrderOfNodeTraversal where sourceDataSource.id in (select id From edu.wustl.geneconnect.domain.DataSource where name = 'EnsemblTranscript') AND linkType.id in (select id From edu.wustl.geneconnect.domain.LinkType where type = 'DIRECT') AND childOrderOfNodeTraversal.id in (select id From edu.wustl.geneconnect.domain.OrderOfNodeTraversal where sourceDataSource.id in (select id From edu.wustl.geneconnect.domain.DataSource where name = 'RefSeqmRNA')))) AND xxTargetAliasxx.consensusIdentifierDataCollection.id in (select id From edu.wustl.geneconnect.domain.ConsensusIdentifierData where frequency = '0.2' where genomicIdentifier.id in (select id From edu.wustl.geneconnect.domain.EntrezGene))";
			String hqlString = "From edu.wustl.geneconnect.domain.GenomicIdentifierSet as xxTargetAliasxx where xxTargetAliasxx.gene.id in (select id From edu.wustl.geneconnect.domain.Gene where entrezGeneId = '1958' AND unigeneAsOutput = true) AND xxTargetAliasxx.messengerRNA.id in (select id From edu.wustl.geneconnect.domain.MessengerRNA where ensemblTranscriptAsOutput = true) AND xxTargetAliasxx.protein.id in (select id From edu.wustl.geneconnect.domain.Protein where ensemblPeptideAsOutput = true) AND xxTargetAliasxx.orderOfNodeTraversalCollection.id in (select id From edu.wustl.geneconnect.domain.OrderOfNodeTraversal where sourceDataSource.id in (select id From edu.wustl.geneconnect.domain.DataSource where name = 'UniGene') AND linkType.id in (select id From edu.wustl.geneconnect.domain.LinkType where type = 'DIRECT') AND childOrderOfNodeTraversal.id in (select id From edu.wustl.geneconnect.domain.OrderOfNodeTraversal where sourceDataSource.id in (select id From edu.wustl.geneconnect.domain.DataSource where name = 'Entrez Gene') AND linkType.id in (select id From edu.wustl.geneconnect.domain.LinkType where type = 'DIRECT') AND childOrderOfNodeTraversal.id in (select id From edu.wustl.geneconnect.domain.OrderOfNodeTraversal where sourceDataSource.id in (select id From edu.wustl.geneconnect.domain.DataSource where name = 'RefSeq mRNA') AND linkType.id in (select id From edu.wustl.geneconnect.domain.LinkType where type = 'INFERRED') AND childOrderOfNodeTraversal.id in (select id From edu.wustl.geneconnect.domain.OrderOfNodeTraversal where sourceDataSource.id in (select id From edu.wustl.geneconnect.domain.DataSource where name = 'Ensembl Transcript') AND linkType.id in (select id From edu.wustl.geneconnect.domain.LinkType where type = 'DIRECT') AND childOrderOfNodeTraversal.id in (select id From edu.wustl.geneconnect.domain.OrderOfNodeTraversal where sourceDataSource.id in (select id From edu.wustl.geneconnect.domain.DataSource where name = 'Ensembl Protein'))))))";
			HQLProcessor p = new HQLProcessor();
			List ontCollection = p.getOntCollection(p.intrepretOnt(hqlString));
			String roleName = "";
			java.lang.reflect.Field field = null;
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
							System.out.println("ds.getName(): " + ds.getName());
							innerOntList.add(ds.getName());
						}
						if (link != null && link.getType() != null)
						{
							System.out.println("link.getType(): " + link.getType());
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
						System.out.println("--" + innerOntList);
					}

				}
			}
			//p.interpretFrequency(hqlString);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		//String hql = "Ad GenomicIdentifierSet dfC  GenomicIdentifierSet  DRGdfFH";

	}
}
