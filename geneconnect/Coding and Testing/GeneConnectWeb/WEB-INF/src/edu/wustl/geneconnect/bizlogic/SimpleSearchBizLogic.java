/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.bizlogic.SimpleSearchBizLogic</p> 
 */

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

import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.exceptionformatter.DefaultExceptionFormatter;
import edu.wustl.common.util.dbManager.DAOException;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.geneconnect.GenomicIdentifierSolution;
import edu.wustl.geneconnect.actionForm.SimpleSearchForm;
import edu.wustl.geneconnect.cacore.CaCoreClient;
import edu.wustl.geneconnect.domain.ConsensusIdentifierData;
import edu.wustl.geneconnect.domain.GenomicIdentifier;
import edu.wustl.geneconnect.domain.GenomicIdentifierSet;
import edu.wustl.geneconnect.metadata.MetadataManager;
import edu.wustl.geneconnect.util.global.GCConstants;
import edu.wustl.geneconnect.util.global.Utility;

/**
 * Simple Serach Business Logic
 * Implements method which call caCore APIs to execute query.
 * Also implements method to validate and parse the user inputs.
 * @author mahesh_nalkande
 * @version 1.0
 */
public class SimpleSearchBizLogic implements BizLogicInterface
{

	private MetadataManager metadataManager = null;

	/**
	 * Output data List contains data source names 
	 */
	List outputDsList = null;

	/**
	 * Store the input data source and genomic id
	 * Input data list contains bean Name=DataSopurceName Value=GenomicID  
	 */
	List inputDsList = null;

	ResultDataInterface resultData = null;

	/**
	 * Applies business logic on the passed input data and retunrs back the result.
	 * 
	 * @param inputData Data on which Business Logic will operate.
	 * @return ResultDataInterface Result data, which can be SuccessResultData or ValidationResultData.
	 * @throws BizLogicException
	 * @throws DAOException
	 * 
	 * @see edu.wustl.geneconnect.bizlogic.BizLogicInterface#getResult(InputDataInterface)
	 */

	public ResultDataInterface getResult(InputDataInterface inputData) throws BizLogicException,
			DAOException
	{
		List resultList = null;
		resultData = new ResultData();
		/**
		 * Apply logic to validate the inputs given by user
		 */
		Map data = inputData.getData();
		SimpleSearchForm simpleSearchForm = (SimpleSearchForm) data.get("Form");
		inputDsList = simpleSearchForm.getInputDsList();
		outputDsList = simpleSearchForm.getOutputDsList();
//		if (readAndValidateInputs(inputData))
		{
			/**
			 * Prepare GenomicIdentifierSet object from the input query given by user 
			 */
			GenomicIdentifierSet genomicIdentifierSet = CaCoreClient.querySimple(inputDsList,
					outputDsList);

			/**
			 * call ApplicationService.query() of caCore API to execute the query and get the result
			 */
			resultList = CaCoreClient.appServiceQuery(GenomicIdentifierSet.class.getName(),
					genomicIdentifierSet);
			//			resultList = CaCoreClient.appServiceQuery(genomicIdentifierSetCriteria,
			//					GenomicIdentifierSet.class.getName());
			/**
			 * Peapare result in a format required to display on web  
			 */
			prepareResult(resultList);

			/**
			 * Remove redundant Set
			 */
			removeRedundant(resultList);
		}
		return resultData;
	}

	/**
	 * Check for values of selected in put/output datsources and remove the redundant GenomicIdentifierSet object 
	 * and combine the set_ids.  
	 * @param resultList
	 * @throws BizLogicException
	 * @throws DAOException
	 */
	private void removeRedundant(List resultList) throws BizLogicException, DAOException
	{
		List columnList = (List) resultData.getValue(GCConstants.COLUMN_HEADERS);
		List dataList = (List) resultData.getValue(GCConstants.RESULT_LIST);

		Map dataMap = new HashMap();
		StringBuffer combineGenomicID = new StringBuffer();
		for (Iterator iter = dataList.iterator(); iter.hasNext();)
		{
			combineGenomicID.setLength(0);
			String setID = "";
			HashMap setMap = (HashMap) iter.next();
			for (int i = 0; i < columnList.size(); i++)
			{
				String colName = (String) columnList.get(i);
					
				if (colName.endsWith(GCConstants.SET_ID_KEY))
				{
					setID = (String) setMap.get(columnList.get(i));
				}
				if ((!colName.endsWith(GCConstants.FREQUENCY_KEY_SUFFIX))
						&& (!colName.endsWith(GCConstants.CONF_SCORE_KEY))
						&& (!colName.endsWith(GCConstants.SET_ID_KEY)))
				{

					List row = new ArrayList();

					String dataValue = (String) setMap.get(columnList.get(i));
					combineGenomicID.append(dataValue + "_");
				}
			}
			combineGenomicID.deleteCharAt(combineGenomicID.length() - 1);
			String mapKey = combineGenomicID.toString();
			if (dataMap.get(mapKey) != null)
			{
				Map oldSetMap = (Map) dataMap.get(mapKey);
				String combineSetID = (String) oldSetMap.get(GCConstants.SET_ID_KEY);
				combineSetID = combineSetID + GCConstants.SET_ID_DELIM + setID;
				oldSetMap.remove(GCConstants.SET_ID_KEY);
				oldSetMap.put(GCConstants.SET_ID_KEY, combineSetID);
				dataMap.remove(mapKey);
				dataMap.put(mapKey, oldSetMap);
				Logger.out.debug("Added combine Set " + combineSetID);
				Logger.out.debug("Removing redundant Set " + mapKey);
				Logger.out.info("Removing redundant Set " + mapKey);
				iter.remove();
			}
			else
			{
				dataMap.put(mapKey, setMap);
			}
		}

	}

	/**
	 * Peapare result in a format required to display on web
	 * Add column names and respective data list to ResultData
	 * @param resultList
	 * @throws BizLogicException
	 * @throws DAOException
	 */
	private void prepareResult(List resultList) throws BizLogicException, DAOException
	{
		try
		{
			List columnHeader = new ArrayList();

			List result = new ArrayList();
			int counter = 0;
			Map frequency = new HashMap();
			/**
			 * get column names to be set in result data 
			 * The column names will be as
			 * Data_Source1	
			 * Data_Source1(Frequency)
			 * Data_Source2
			 * Data_Source2(Frequency)
			 * SET_ID
			 * Confidence Score			
			 */
			for (int i = 0; i < inputDsList.size(); i++)
			{
				NameValueBean bean = (NameValueBean) inputDsList.get(i);
				Logger.out.debug("INP DS : " + bean.getName());
				columnHeader.add(bean.getName());
				//columnHeader.add(bean.getName() + GCConstants.FREQUENCY_KEY_SUFFIX);
			}
			for (int i = 0; i < outputDsList.size(); i++)
			{
				columnHeader.add((String) outputDsList.get(i));
				Logger.out.debug("OUtP DS : " + outputDsList.get(i));
				columnHeader.add((String) outputDsList.get(i) + GCConstants.FREQUENCY_KEY_SUFFIX);
			}
			columnHeader.add(GCConstants.CONF_SCORE_KEY);
			columnHeader.add(GCConstants.SET_ID_KEY);
			/**
			 * Store the frequency of each genomic identiifer in map as
			 * Key = genomicId 
			 * value = frequency 
			 */
			if (resultList.size() > 0)
			{
				GenomicIdentifierSet set = (GenomicIdentifierSet) resultList.get(0);

				GenomicIdentifierSolution solution = set.getGenomicIdentifierSolution();
				Collection coll = solution.getConsensusIdentifierDataCollection();
				Logger.out.debug("Genomic Identifer\tFrequency");
				for (Iterator iter1 = coll.iterator(); iter1.hasNext();)
				{
					//OrderOfNodeTraversal ont = (OrderOfNodeTraversal)iter1.next();
					ConsensusIdentifierData freqData = (ConsensusIdentifierData) iter1.next();
					GenomicIdentifier g = freqData.getGenomicIdentifier();
					if (g != null)
					{
						Logger.out.debug("\t" + g.getGenomicIdentifier() + "\t\t\t"
								+ freqData.getFrequency());
						/**
						 * If genomicIdentifier is Null the add key as GenomicIdentifierClass + '_NULL'
						 * else genomicIdentifier
						 */
						if (g.getGenomicIdentifier() == null)
						{
							frequency
									.put(g.getClass().getName() + "_NULL", freqData.getFrequency());
						}
						else
						{
							frequency.put(g.getGenomicIdentifier().toString(), freqData
									.getFrequency());
						}
					}
				}
			}
			/**
			 * Loop through result list and prepare a data list.
			 * each element of data list will contain a Map
			 * Where Map conatins values of each column
			 * Key = column name (obtain from ablove logic)
			 * Value = genomicId / frequency / setid / confidence w.r.t column
			 */
			for (Iterator iter = resultList.iterator(); iter.hasNext();)
			{
				GenomicIdentifierSet genomicIdentifierSet = (GenomicIdentifierSet) iter.next();
				Long setId = genomicIdentifierSet.getId();
				Float confScore = genomicIdentifierSet.getConfidenceScore();
				Map setMap = new HashMap();
				for (int i = 0; i < columnHeader.size(); i++)
				{
					StringBuffer temp = new StringBuffer();
					/**
					 * get the column name i.e  data source name
					 */
					String column = (String) columnHeader.get(i);
					if (column.endsWith(GCConstants.FREQUENCY_KEY_SUFFIX)
							|| column.endsWith(GCConstants.CONF_SCORE_KEY)
							|| column.endsWith(GCConstants.SET_ID_KEY))
						continue;
					/**
					 * get CLASS of data source i.e Gene or MessengerRNA or Protein
					 */
					String className = MetadataManager.getDataSourceAttribute(
							GCConstants.DATASOURCE_NAME, column, GCConstants.CLASS);
					//Logger.out.debug("className : " + className);

					temp.append(MetadataManager.getRoleName("GenomicIdentifierSet", className));

					temp.replace(0, 1, temp.substring(0, 1).toUpperCase());

					/**
					 * Get method name of GenomicIdentifierSet to retrive class 
					 * i.e Gene or MessengerRNA or Protein
					 */
					String methodForclassName = "get" + temp.toString();
					temp.setLength(0);

					/**
					 * Get method name of Gene or MessengerRNA or Protein to retrive GenomicID of data source
					 */
					String classAttribute = MetadataManager.getDataSourceAttribute(
							GCConstants.DATASOURCE_NAME, column, GCConstants.ATTRIBUTE);
					String methodForClassAttribute = "get"
							+ classAttribute.substring(0, 1).toUpperCase()
							+ classAttribute.substring(1, classAttribute.length());

					//Logger.out.debug("methodForClassAttribute: " + methodForClassAttribute);
					Logger.out.info("methodForClassAttribute: " + methodForClassAttribute);
					Class genomicClass = Class.forName("edu.wustl.geneconnect.domain." + className);

					Method method = null;

					/**
					 * Invoke method of GenomicIdentifierSet.getGene() or GenomicIdentifierSet.getMesseagerRNA()
					 * or GenomicIdentifierSet.getProtein()
					 */
					method = GenomicIdentifierSet.class.getDeclaredMethod(methodForclassName, null);
					Object genomicObject = method.invoke(genomicIdentifierSet, null);

					/**
					 * Invoke method of Gene or MessengerRNA or Protein to retrive GenomicID of data source
					 */
					method = genomicClass.getDeclaredMethod(methodForClassAttribute, null);
					Object value = method.invoke(genomicObject, null);

					//Logger.out.debug("Resutl : " + methodForClassAttribute + "------" + value);
					//Logger.out.debug("Freq : " + value.toString());
					/**
					 * Add entry in Map to strore data w.r.t column
					 */
					//Logger.out.debug("value1: "+ value);
					String key = "";
					/**
					 * If genoimicIdentifier is null the set key = GenomicIdentifier class + '_NULL'
					 * else key = genomicidentifier value
					 */
					if (value == null || value.equals("NULL"))
					{
						//Logger.out.debug("value: " + value);
						value = new String(GCConstants.NO_MATCH_FOUND);
						StringBuffer genomicIDClass = new StringBuffer(
								"edu.wustl.geneconnect.domain.");

						genomicIDClass.append(MetadataManager.getDataSourceAttribute(
								GCConstants.DATASOURCE_NAME, column,
								GCConstants.GENOMIC_IDENTIFIER_CLASS));
						genomicIDClass.append("_NULL");
						key = genomicIDClass.toString();
					}
					else
					{
						key = value.toString();
					}
					//Logger.out.debug(column+"===="+value.toString());
					setMap.put(column, value.toString());
					Float freq = (Float) frequency.get(key);
					String freqValue = "";
					if (freq == null)
						freqValue = new String(GCConstants.NA);
					else
						freqValue = freq.toString();

					setMap.put(column + GCConstants.FREQUENCY_KEY_SUFFIX, freqValue);

				}
				counter++;
				setMap.put(GCConstants.SET_ID_KEY, setId.toString());
				setMap.put(GCConstants.CONF_SCORE_KEY, confScore.toString());

				result.add(setMap);
			}
			//			resultData.setColumnHeader(columnHeader);
			//			resultData.setResult(result);
			Map data = new HashMap();
			data.put(GCConstants.COLUMN_HEADERS, columnHeader);
			data.put(GCConstants.RESULT_LIST, result);
			data.put(GCConstants.GENOMICIDENTIIER_SET_RESULT_LIST, resultList);
			resultData.setData(data);
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			throw new BizLogicException(e.getMessage(), e);
		}

	}

	/**
	 * Validaets teh input and outoput data list specified by user on UI.
	 * aftervalidating stores the input in LIst as:
	 * Input data list contains bean Name=DataSopurceName Value=GenomicID
	 * Output data List contains data source names 
	 * @param inputData contains list of input data source and output data source 
	 * @return true if validated 
	 * @throws BizLogicException
	 * @throws DAOException
	 */
	private boolean readAndValidateInputs(InputDataInterface inputData) throws BizLogicException,
			DAOException
	{
		boolean validate = true;

		outputDsList = new ArrayList();
		inputDsList = new ArrayList();
		//metadataManager = MetadataManager.getInstance();
		Map data = inputData.getData();
		Map inputMap = new HashMap();
		List testList = new ArrayList();

		SimpleSearchForm simpleSearchForm = (SimpleSearchForm) data.get("Form");
		Map map = simpleSearchForm.getValues();
		Set s = map.keySet();

		/** Input format from Simple Search is stored in Map as 
		 * 	Key = Input:1_Genomic_Id
		 * 	Value : hh
		 * 	Key : Input:1_DataSource_Id
		 *  Value : 1
		 *  Key : Output:DataSource_Id_4
		 *  Value : on
		 *  Key : Input:1_systemIdentifier
		 *  Value :
		 *  
		 *  Loop over the map and parse the input.
		 *  Store the input data source and genomic id in inputDsList
		 *  and store the oputput  data source in outputDsList
		 */

		int noOfInputs = 0;
		for (Iterator iter = s.iterator(); iter.hasNext();)
		{
			String str = (String) iter.next();
			//Logger.out.debug("KEys: " + str);
			if (str.endsWith("_systemIdentifier"))
			{
				//noOfInputs++;
				//Logger.out.debug("noOfInputs: " + noOfInputs);
			}
			else if (str.startsWith("Input:"))
			{
				/**
				 * Store inmput data in MAp for further validating processs
				 */
				Logger.out.debug("map.get(str): " + map.get(str));
				if (str.endsWith("DataSource_Id") && ((String) map.get(str)).equalsIgnoreCase("-1"))
				{
					throw new BizLogicException("Select valid data source");
				}
				if ((((String) map.get(str)).equalsIgnoreCase(Constants.SELECT_OPTION)))
				{
					throw new BizLogicException("Select valid data source");
				}
				if (str.endsWith("DataSource_Id"))
					noOfInputs++;
				if (map.get(str) != null)
					inputMap.put(str, map.get(str));
				Logger.out.info("Input Key  " + str);
				Logger.out.info("Input Value : " + map.get(str));
				//Logger.out.debug("Input Key  " + str);
				//Logger.out.debug("Input Value : " + map.get(str));
			}
			else if (str.startsWith("Output:"))
			{
				/**
				 * Store output data in list
				 */
				String temp = "Output:DataSource_Id_";
				String dataSourceID = str.substring(temp.length(), str.length());
				String dataSourceName = MetadataManager.getDataSourceName(dataSourceID);
				Logger.out.info("Output DS selected : " + dataSourceName);
				outputDsList.add(dataSourceName);
			}
		}
		/**
		 * O/p DS should not be empty
		 */
		if (outputDsList.size() <= 0)
		{
			String arg[] = new String[]{""};
			String errmsg = new DefaultExceptionFormatter().getErrorMessage(
					"errors.no.outputDatasource", arg);
			Logger.out.info(errmsg);
			throw new BizLogicException(errmsg);
		}
		String temp = null;
		List dsName = new ArrayList();
		Logger.out.info("noOfInputs: " + noOfInputs);
		//Logger.out.debug("noOfInputs: " + noOfInputs);

		/**
		 * Loop over Input map 
		 * Validate input data and store it in list
		 */
		Set inputCounter = new HashSet();
		Set keySet = inputMap.keySet();
		for (Iterator it = keySet.iterator(); it.hasNext();)
		{
			String key = (String) it.next();
			if (key.startsWith("Input:") && key.endsWith("DataSource_Id"))
			{
				temp = key.substring(key.indexOf("Input:") + "Input:".length(), key.indexOf("_"));
				//Logger.out.debug("temp " +temp+"---"+temp.length());
				if (!inputCounter.contains(temp))
				{
					inputCounter.add(temp);
				}
			}
		}
		for (Iterator it = inputCounter.iterator(); it.hasNext();)
		{
			NameValueBean bean = new NameValueBean();
			String key = (String) it.next();
			//Logger.out.debug("KEEYY " +key);
			temp = "Input:" + key + "_DataSource_Id";
			//"Input:" + i + "_DataSource_Id";
			String dataSourceID = (String) inputMap.get(temp);
			Logger.out.debug("dataSourceID " + dataSourceID);
			String dataSourceName = MetadataManager.getDataSourceName(dataSourceID);
			Logger.out.debug("dataSourceName " + dataSourceName);
			Logger.out.info("size: " + dsName.size());
			/**
			 * Logic to check whther user has enter more than one genomicId for same input data source
			 */
			for (int j = 0; j < dsName.size(); j++)
			{
				//Logger.out.debug("dataSourceName :"+dataSourceName);
				//Logger.out.debug("dsName : "+dsName.get(j).toString());
				if (dataSourceName.equalsIgnoreCase(dsName.get(j).toString()))
				{
					String arg[] = new String[]{dataSourceName};
					String errmsg = new DefaultExceptionFormatter().getErrorMessage(
							"errors.duplicate.datasource", arg);
					//Logger.out.debug("errmsg : "+errmsg);
					Logger.out.info(errmsg);
					throw new BizLogicException(errmsg);
				}
			}

			dsName.add(dataSourceName);

			String tempkey = "Input:" + key + "_Genomic_Id";
			String genomicID = (String) inputMap.get(tempkey);
			/**
			 * Logic to check whther user has enter null value for any of genomicId
			 */
			if (genomicID.length() <= 0)
			{
				String arg[] = new String[]{dataSourceName};
				String errmsg = new DefaultExceptionFormatter().getErrorMessage(
						"error.null.genomicid", arg);
				Logger.out.info(errmsg);
				throw new BizLogicException(errmsg);
			}
			bean.setName(dataSourceName);
			bean.setValue(genomicID);
			inputDsList.add(bean);
			Logger.out.info("Input Ds selected : " + bean.getName() + "---" + bean.getValue());
			testList.add(dataSourceName);

		}

		/**
		 * Test if data source is in both Input and output list  
		 */
		for (int i = 0; i < testList.size(); i++)
		{
			String dataSourceName = (String) testList.get(i);
			if (Utility.listContainValue(dataSourceName, outputDsList))
			{
				String arg[] = new String[]{dataSourceName};
				String errmsg = new DefaultExceptionFormatter().getErrorMessage(
						"errors.datasource.inputOutput", arg);
				Logger.out.info(errmsg);
				throw new BizLogicException(errmsg);
			}

		}
		return validate;
	}

}