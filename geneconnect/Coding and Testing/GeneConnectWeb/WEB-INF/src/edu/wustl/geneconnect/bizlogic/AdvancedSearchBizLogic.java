/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.bizlogic.AdvancedSearchBizLogic</p> 
 */
package edu.wustl.geneconnect.bizlogic;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.exceptionformatter.DefaultExceptionFormatter;
import edu.wustl.common.util.dbManager.DAOException;
import edu.wustl.common.util.global.Validator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.geneconnect.GenomicIdentifierSolution;
import edu.wustl.geneconnect.actionForm.AdvancedSearchForm;
import edu.wustl.geneconnect.cacore.CaCoreClient;
import edu.wustl.geneconnect.cacore.caCoreThread;
import edu.wustl.geneconnect.domain.ConsensusIdentifierData;
import edu.wustl.geneconnect.domain.DataSource;
import edu.wustl.geneconnect.domain.Gene;
import edu.wustl.geneconnect.domain.GenomicIdentifier;
import edu.wustl.geneconnect.domain.GenomicIdentifierSet;
import edu.wustl.geneconnect.domain.LinkType;
import edu.wustl.geneconnect.domain.MessengerRNA;
import edu.wustl.geneconnect.domain.OrderOfNodeTraversal;
import edu.wustl.geneconnect.domain.Protein;
import edu.wustl.geneconnect.metadata.MetadataManager;
import edu.wustl.geneconnect.util.global.GCConstants;
import edu.wustl.geneconnect.util.global.Utility;

/**
 * Advnaced Serach Business Logic
 * Implements method which call caCore APIs to execute query.
 * Also implements method to validate and parse the user inputs.
 * @author krunal_thakkar
 * @version 1.0
 */
public class AdvancedSearchBizLogic implements BizLogicInterface
{
	ResultDataInterface resultData = null;
	

	/**
	 * Output data List contains data source names 
	 */
	List outputDsList = null;

	/**
	 * Store the input data source and genomic id
	 * Input data list contains bean Name=DataSopurceName Value=GenomicID  
	 */
	//List inputDsList = null;
	Map inputDsMap = null;
	Map columnHeaderMap = null;
//	defining attributes required for generateQueryObjects()
	private Collection consensusCollection;
	private ArrayList dataSourcesWithFrequency;
	private Map giSetMap;
    private Map giSetObjectsMap;
	private List inputDsList;
	private Float confidenceScore =null;
	
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
		
		validate(inputData);
		/**
		 * Generate domain objects and store it in a MAP as
		 * key = Input:1 seleected inptu ds and its value as ds1=1,ds2=2
		 * value= GenomicIdentifierSet Object
		 */
		Map setMap = generateQueryObjects(inputData);
		
		/**
		 * Genrate ONT objects and set in setMAP
		 */
		List ontList = generateOntObjects(inputData);

		if(ontList!=null&&ontList.size()>0)
		{
			Set setKeys = setMap.keySet();
			for(Iterator setKeysIter = setKeys.iterator();setKeysIter.hasNext();)
			{
				GenomicIdentifierSet gset = (GenomicIdentifierSet)setMap.get(setKeysIter.next());
				gset.setOrderOfNodeTraversalCollection(ontList);
			}
		}
		/**
		 * Prepare column header to displau w.r.t each query
		 */
		prepareColumnHeaders();
		/**
		 * Iterate over Map and call caCOre API to query
		 */
		Set setKeys = setMap.keySet();
		int counter=0;
		if(setKeys!=null)
		{
			List threadList = new ArrayList();
			long t1 = System.currentTimeMillis();
			for(Iterator setIter=setKeys.iterator();setIter.hasNext();)
			{
				String key = (String)setIter.next();
				GenomicIdentifierSet querySet = (GenomicIdentifierSet)setMap.get(key);
				Logger.out.debug("'querySet :" +querySet);

//				caCoreThread thread = new caCoreThread(querySet,key);
//				threadList.add(thread);
//				thread.start();
				
				resultList = CaCoreClient.appServiceQuery(GenomicIdentifierSet.class.getName(),querySet);
				prepareResult(resultList,key);
				removeRedundant(key);
				Logger.out.debug("Result Size: " + resultList.size());
			}
//			counter=0;
//			//Logger.out.debug("threadList.size(): "+threadList.size());
//			while(true)
//			{
//				for(Iterator threaditer=threadList.iterator();threaditer.hasNext();)
//				{
//					caCoreThread thread = (caCoreThread)threaditer.next();
//					resultList = thread.getResult();
//					String key = thread.getName();
//					Exception ex = thread.getExp();
//					if(ex!=null)
//					{
//						throw new BizLogicException(ex.getMessage(),ex);
//					}
//					//Logger.out.debug(key+"---"+resultList);
//					if(resultList!=null)
//					{
//						prepareResult(resultList,key);
//						removeRedundant(key);
//						threaditer.remove();
//						counter++;
//					}
//				}
//				if(counter>=setKeys.size())
//				{	
//					Logger.out.info("Got all results:"+counter);
//					break;
//				}	
//			} 
			long t2 = System.currentTimeMillis();
			Logger.out.debug("Time required to get resu;t: "+(t2-t1)/1000);
			Logger.out.info("Time required to get resu;t: "+(t2-t1)/1000);
			
		}
		return resultData;
	}
	
	
	/**
	 * validaes the input data.
	 * Check for input data source
	 * Check if ouput data osurce is selected,
	 * Check for float values has been enterd for confidnce anf frequency 
	 * @param inputData
	 * @return
	 * @throws BizLogicException
	 * @throws DAOException
	 */
	private boolean validate(InputDataInterface inputData) throws BizLogicException,
	DAOException
	{
		
		
		Validator validator = new Validator();
		Map data = inputData.getData();
		
		AdvancedSearchForm advancedSearchActionForm = (AdvancedSearchForm) data.get(GCConstants.FORM);
		
//		Map frequencyMap = (Map)data.get(GCConstants.FREQUENCY_MAP);
		Map frequencyMap = (Map)advancedSearchActionForm.getOutputDataSources();
		
		/**
		 * Frequency validation is done in Action
		 * 
		 */
		/*
		 * Oupt data source validation
		 */
		Set keysets = frequencyMap.keySet();
		Logger.out.info("No.of selected output data source"+keysets.size());
		if(keysets.size()==0)
		{
			throw new BizLogicException("Select atleast one Output data source.");
		}
		
		
		
		
		/**
		 * Confidence Score validation
		 */
		String conf = advancedSearchActionForm.getConfidenceScore();
		

		if(conf.length()>0 && !validator.isDouble(conf))
		{
			String arg[] = new String[]{"Confidence Score"};
			String errmsg = new DefaultExceptionFormatter().getErrorMessage("errors.confFreq.value", arg);
			Logger.out.info(errmsg);
			throw new BizLogicException(errmsg);
		}
		else if(conf.length()>0 && validator.isDouble(conf))
		{
			float f = new Float(conf).floatValue();
			if(f<0||f>1)
			{
				String arg[] = new String[]{"Confidence Score"};
				String errmsg = new DefaultExceptionFormatter().getErrorMessage(
						"errors.confFreq.value", arg);
				Logger.out.info(errmsg);
				throw new BizLogicException(errmsg);
			}
		}
		/**
		 * Input data sopurce validation
		 */
		Map sourcesMap = advancedSearchActionForm.getInputDataSources();
		keysets = sourcesMap.keySet();
		int cnt = keysets.size();
		int i=0;
		for(Iterator iter=keysets.iterator();iter.hasNext();)
		{
			String k = (String)iter.next();
			String val = (String)sourcesMap.get(k);
			if(val.length()==0)
				i++;
		}
		if(i==cnt)
		{
			String arg[] = new String[]{"Input data source"};
			String errmsg = new DefaultExceptionFormatter().getErrorMessage(
					"errors.one.item.required", arg);
			Logger.out.info(errmsg);
			throw new BizLogicException(errmsg);
		}
		return true;
	}
	
	/**
	 * prepare column headers Map for each query  
	 *
	 */
	private void prepareColumnHeaders()
	{
		columnHeaderMap = new HashMap();
		if(inputDsMap!=null)
		{
			Set kset = inputDsMap.keySet();
			
			
			for(Iterator it = kset.iterator();it.hasNext();)
			{
				List columnHeaders = new ArrayList();
				Map tempMap = new HashMap();
				String key = (String)it.next();
				//Logger.out.debug("Key " + key);
				List l = (List)inputDsMap.get(key);
				//Logger.out.debug("input sisise----"+l.size());
				for(int i=0;i<l.size();i++)
				{
					String dsName = (String)l.get(i);
					if(tempMap.get(dsName)==null)
					{
						columnHeaders.add(dsName);
						//Logger.out.debug("I DSNAME : " +dsName);
						tempMap.put(dsName,dsName);
					}
				}
				//Logger.out.debug("outputDsList sisise----"+outputDsList.size());
				for(int i=0;i<outputDsList.size();i++)
				{
					String dsName = (String)outputDsList.get(i);
					if(tempMap.get(dsName)==null)
					{
						columnHeaders.add(dsName);
						columnHeaders.add(dsName+GCConstants.FREQUENCY_KEY_SUFFIX);
						tempMap.put(dsName,dsName);
						//Logger.out.debug("O DSNAME : " +dsName);
					}
				}
				columnHeaders.add(GCConstants.CONF_SCORE_KEY);
				columnHeaders.add(GCConstants.SET_ID_KEY);
				/**
				 * Add column headers w.r.t to query
				 */
				//Logger.out.debug("KEy--coluimn " + key+"----"+columnHeaders);
				columnHeaderMap.put(key,columnHeaders);
			}
		}
	}
	public List threading()
	{
		try
		{
			for(int i=0;i<6;i++)
			{
				caCoreThread t = new caCoreThread(null,""+i);
				t.start();
				
			}
			while(true)
			{
				if(caCoreThread.getCounter()>=6)
					break;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		Logger.out.debug("return");
		return null;
	}
	public static void main(String ap[])
	{
		
		try
		{
			AdvancedSearchBizLogic b = new AdvancedSearchBizLogic();
			b.threading();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		Logger.out.debug("END");
	}
	public static void main1(String ap[])
	{
		try
		{
			List selectedOntList = new ArrayList();
			selectedOntList.add("1_4_9_3_8");
			selectedOntList.add("2_8_6_2_7");
			selectedOntList.add("3_4_7_3_9");
			InputDataInterface inputData = new InputData();
			
			Map map = new HashMap();
			map.put(GCConstants.SELECTED_ONT_LIST,selectedOntList);
			inputData.setData(map);
			AdvancedSearchBizLogic b = new AdvancedSearchBizLogic();
			List l = b.generateOntObjects(inputData);
			
			for(int i=0;i<l.size();i++)
			{
				Logger.out.debug("New ONn \n");
				OrderOfNodeTraversal ont = (OrderOfNodeTraversal)l.get(i);
				List innerOntList = new ArrayList();
				OrderOfNodeTraversal tempont = ont;
				String roleName="";
				Field field=null;
				while (tempont!=null)
				{
					//roleName = MetadataManager.getRoleName(Constants.ONT_CLASS_NAME,Constants.DATASOURCE_CLASS_NAME);
					roleName = "sourceDataSource";
					field = OrderOfNodeTraversal.class.getDeclaredField(roleName);
					field.setAccessible(true);
					DataSource ds = (DataSource)field.get(tempont);
					
					//roleName = MetadataManager.getRoleName(Constants.ONT_CLASS_NAME,Constants.LINKTYPE_CLASS_NAME);
					roleName = "linkType";
					field = OrderOfNodeTraversal.class.getDeclaredField(roleName);
					field.setAccessible(true);
					LinkType link = (LinkType)field.get(tempont);
					
					if(ds!=null&&ds.getName()!=null)
					{
						Logger.out.debug("DS: " +ds.getName()); 
					}
					if(link!=null&&link.getType()!=null)
					{
						Logger.out.debug("LInk : " +link.getType());				
					}
					roleName = "childOrderOfNodeTraversal";
					field = OrderOfNodeTraversal.class.getDeclaredField(roleName);
					field.setAccessible(true);
					OrderOfNodeTraversal nextont = (OrderOfNodeTraversal)field.get(tempont);
					tempont = nextont;
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * Check for values of selected in put/output datsources and remove the redundant GenomicIdentifierSet object 
	 * and combine the set_ids.  
	 * @param resultList
	 * @param key
	 * @throws BizLogicException
	 * @throws DAOException
	 */
	private void removeRedundant(String key) throws BizLogicException, DAOException
	{
//		List columnList = resultData.getColumnHeader();
//		List dataList = resultData.getResult();
		Map queryMap = (Map)resultData.getValue(key);
		
		List columnList = (List)queryMap.get(GCConstants.COLUMN_HEADERS);
		List dataList = (List)queryMap.get(GCConstants.RESULT_LIST);
		
		Map dataMap = new HashMap();
		StringBuffer combineGenomicID= new StringBuffer();
		for (Iterator iter=dataList.iterator();iter.hasNext();)
		{
			combineGenomicID.setLength(0);
			String setID="";
			HashMap setMap = (HashMap) iter.next();
			for (int i = 0; i < columnList.size(); i++)
			{
				String colName = (String) columnList.get(i);
				
				if(colName.endsWith(GCConstants.SET_ID_KEY))
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
			if(dataMap.get(mapKey)!=null)
			{
				Map oldSetMap = (Map)dataMap.get(mapKey);
				String combineSetID = (String)oldSetMap.get(GCConstants.SET_ID_KEY); 
				combineSetID = combineSetID +","+setID;
				oldSetMap.remove(GCConstants.SET_ID_KEY);
				oldSetMap.put(GCConstants.SET_ID_KEY,combineSetID);
				dataMap.remove(mapKey);
				dataMap.put(mapKey,oldSetMap);
				Logger.out.debug("Added combine Set " + combineSetID);
				Logger.out.debug("Removing redundant Set " + mapKey);
				Logger.out.info("Removing redundant Set " + mapKey);
				iter.remove();
			}
			else
			{
				dataMap.put(mapKey,setMap);
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
	private void prepareResult(List resultList,String setMapkey) throws BizLogicException, DAOException
	{
		try
		{
			List inputDsList = (List) inputDsMap.get(setMapkey);
			List columnHeader = (List)columnHeaderMap.get(setMapkey);
			//Logger.out.debug(setMapkey+"----"+columnHeader);
			List result = new ArrayList();
			int counter = 0;
			Map frequency = new HashMap();
			
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
				//Logger.out.debug("Genomic Identifer\tFrequency");
				for (Iterator iter1 = coll.iterator(); iter1.hasNext();)
				{
					//OrderOfNodeTraversal ont = (OrderOfNodeTraversal)iter1.next();
					ConsensusIdentifierData freqData = (ConsensusIdentifierData) iter1.next();
					GenomicIdentifier g = freqData.getGenomicIdentifier();
					if (g != null)
					{
//						Logger.out.debug("\t" + g.getGenomicIdentifier() + "\t\t\t"
//								+ freqData.getFrequency());
						/**
						 * If genomicIdentifier is Null the add key as GenomicIdentifierClass + '_NULL'
						 * else genomicIdentifier
						 */
						if(g.getGenomicIdentifier()==null)
						{
							frequency.put(g.getClass().getName()+"_NULL", freqData.getFrequency());
						}
						else
						{	
							frequency.put(g.getGenomicIdentifier().toString(), freqData.getFrequency());
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
					String key="";
					/**
					 * If genoimicIdentifier is null the set key = GenomicIdentifier class + '_NULL'
					 * else key = genomicidentifier value
					 */
					if (value == null || value.equals("NULL"))
					{
						//Logger.out.debug("value: " + value);
						value = new String(GCConstants.NO_MATCH_FOUND);
						StringBuffer genomicIDClass = new StringBuffer("edu.wustl.geneconnect.domain.");
						
						genomicIDClass.append(MetadataManager.getDataSourceAttribute(
								GCConstants.DATASOURCE_NAME, column, GCConstants.GENOMIC_IDENTIFIER_CLASS));
						genomicIDClass.append("_NULL");
						key=genomicIDClass.toString();
					}
					else 
					{
						key=value.toString();
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
			Map queryData = resultData.getData();
			
			Map innerData = new HashMap();
			innerData.put(GCConstants.COLUMN_HEADERS,columnHeader);
			innerData.put(GCConstants.RESULT_LIST,result);
			innerData.put(GCConstants.GENOMICIDENTIIER_SET_RESULT_LIST,resultList);
			
			Logger.out.debug("ResultData key "+setMapkey);
			if(queryData==null)
			{
				queryData = new HashMap();
				queryData.put(setMapkey,innerData);
				resultData.setData(queryData);
			}
			else
			{
				queryData.put(setMapkey,innerData);
				
			}
			
			
			
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			throw new BizLogicException(e.getMessage(), e);
		}

	}
	
	
	/**
	 * Generate genomicIdentifierSet objects by setting the appropiate 
	 * I/O data source attribute of gene,mrna,protein
	 * 
	 * @param inputData
	 * @return
	 * @throws BizLogicException
	 */
	private Map generateQueryObjects(InputDataInterface inputData) throws BizLogicException
	{
		try
		{
			Logger.out.info("Inside generateQueryObjects method");
			outputDsList = new ArrayList();
			
			Map data = inputData.getData();
			
			AdvancedSearchForm advancedSearchActionForm = (AdvancedSearchForm) data.get(GCConstants.FORM);

			//fetching ConfidenceScore submitted by User 
			if(advancedSearchActionForm.getConfidenceScore().length()>0)
			{
				confidenceScore = new Float(advancedSearchActionForm.getConfidenceScore());
			}
			
//			Map frequencyMap = (Map)data.get(GCConstants.FREQUENCY_MAP);
			Map frequencyMap = (Map)advancedSearchActionForm.getOutputDataSources();
			
			//  Creating ConsensusCollection
			consensusCollection = new ArrayList();
	
			//Creating list to store OutputDataSources submitted by User
			dataSourcesWithFrequency = new ArrayList();
			
			Set keySets = frequencyMap.keySet();
			Logger.out.debug("keySets : " +keySets);
			
			//Generating list of Output DataSource submitted by User.
			for (Iterator setIter = keySets.iterator();setIter.hasNext();)
			{
					String dataSource = (String)setIter.next();
					String displayName = MetadataManager.getDataSourceAttribute(GCConstants.DATASOURCE_NAME,dataSource, 
							GCConstants.GENOMIC_IDENTIFIER_CLASS);
					Logger.out.info("Display Name->" + displayName);
		
					String className = "edu.wustl.geneconnect.domain." + displayName;
					
					GenomicIdentifier gid = (GenomicIdentifier) Class.forName(className).newInstance();
		
					ConsensusIdentifierData cid = new ConsensusIdentifierData();
					
					Logger.out.info("Frequency Value==>"+dataSource+"----"+frequencyMap.get(dataSource));
		
					cid.setFrequency((Float)frequencyMap.get(dataSource));	
					cid.setGenomicIdentifier(gid);
					consensusCollection.add(cid);
		
					dataSourcesWithFrequency.add(dataSource);
					outputDsList.add(dataSource);
			}
	
			Logger.out.info("No. of objects in ConsensusCollection-->" + consensusCollection.size());
	
	
			//Creating map of InputDataSources
			Map sourcesMap = advancedSearchActionForm.getInputDataSources();
			
			Collection keySet = sourcesMap.keySet();
			
			List sortedKeys = new ArrayList(keySet);
	
			Collections.sort(sortedKeys);
	
			giSetMap = new HashMap();
			giSetObjectsMap = new HashMap();
	
			String tempDataSourceName = "";
	
			int dataSourcesCounter = 0;
	
			StringBuffer giSetMapKeys = new StringBuffer();
			inputDsMap = new HashMap();
			inputDsList=new ArrayList();
			
			//Iterating through the list of InputDataSources submitted by User to generate set of GenomicIdentifierSet 
			for (int i = 0; i < sortedKeys.size(); i++)
			{
				//inputDsList = new ArrayList();
				String k =  (String)sortedKeys.get(i);
				
				//Checking whether user has submitted value for InputDataSource
				if (advancedSearchActionForm.getInputDataSourcesValue((String) sortedKeys.get(i)) != null
						& !(advancedSearchActionForm.getInputDataSourcesValue((String) sortedKeys.get(i)).equals("")))
				{
					StringTokenizer dataSourceToken = new StringTokenizer((String) sortedKeys.get(i), "_");
	
					String dataSourceName = dataSourceToken.nextToken();
					/**
					 * Add datasourcename as selected input
					 */
					if (dataSourcesCounter == 0)
					{
						//Logger.out.info("Asigning first datasource-->"+dataSourceName);
						tempDataSourceName = dataSourceName;
	
						dataSourcesCounter++;
					}
	
					//checking whether new InputDataSource has been found to iterate
					if (!tempDataSourceName.equals(dataSourceName))
					{
						Logger.out.info("$$$$$$$$$$$$$$$$$$$  New DataSource found  $$$$$$$$$$$$$$$$$$$$$$");
						Logger.out.info(tempDataSourceName + " " + dataSourceName);
						
						//Generating GenomicIdentifierSet Map 
						generateGISetMap(tempDataSourceName, giSetMapKeys);
	
						//Logger.out.info("MapKey-> "+dataSourceKey);
						giSetMapKeys.delete(0, giSetMapKeys.length());
						//Logger.out.info("After Delete MapKey-> "+giSetMapKeys);
	
						tempDataSourceName = dataSourceName;
						
					}
	
					dataSourceName = dataSourceToken.nextToken();
	
					//Fetching required DataSourceAttribute for DataSource
					String className = MetadataManager.getDataSourceAttribute(
							GCConstants.DATASOURCE_NAME, dataSourceName, GCConstants.CLASS);
					String attributeName = MetadataManager.getDataSourceAttribute(GCConstants.DATASOURCE_NAME, dataSourceName, GCConstants.ATTRIBUTE);
					String attributeType = MetadataManager.getDataSourceAttribute(GCConstants.DATASOURCE_NAME, dataSourceName, GCConstants.TYPE);
	
					Logger.out.info("Key" + i + "-->" + ((String) sortedKeys.get(i)) + " ClassName-->"
							+ className + " DataSourceName-->" + dataSourceName + " AttributeName-->"
							+ attributeName + " Type-->" + attributeType);
	
					String attributeFirstCharacter = attributeName.substring(0, 1);
	
					//Creating setter method name for DataSource
					String methodName = "set" + attributeFirstCharacter.toUpperCase() + attributeName.substring(1, (attributeName.length()));
	
					Class[] attributes = null;
	
					Object[] attributesValue = null;
	
					//Setting parameter type of the setter method of DataSource
					if (attributeType.equals("java.lang.Long"))
					{
						//					Logger.out.info("Attribute Type--> Long");
						attributes = new Class[]{Long.class};
						try
						{
						attributesValue = new Object[]{new Long(advancedSearchActionForm
								.getInputDataSourcesValue((String) sortedKeys.get(i)).toString())};
						}
						catch(NumberFormatException e)
						{
							Logger.out.error(e.getMessage(),e);
							throw new BizLogicException("Genomic Identifier for DataSource "+dataSourceName+" must be Integer");
						}
					}
					else if (attributeType.equals("java.lang.String"))
					{
						//					Logger.out.info("Attribute Type--> String");
						attributes = new Class[]{String.class};
						attributesValue = new Object[]{new String(advancedSearchActionForm
								.getInputDataSourcesValue((String) sortedKeys.get(i)).toString())};
					}
	
					Class dataSourceClass = Class.forName("edu.wustl.geneconnect.domain." + className);
	
					Object dataSourceObject;
	
					if (giSetObjectsMap.containsKey(className))
						dataSourceObject = giSetObjectsMap.get(className);
					else
						dataSourceObject = dataSourceClass.newInstance();
	
					Logger.out.info("ClassName-->" + dataSourceClass.getName() + " Method Name-->"
							+ methodName);
	
					//Invoking setterMethod for DataSource
					Method dataSourceMethod = dataSourceClass.getDeclaredMethod(methodName, attributes);
	
					dataSourceMethod.invoke(dataSourceObject, attributesValue);
	
					//generating "DataSource=Value" string  
					giSetMapKeys.append(dataSourceName+ "=" + advancedSearchActionForm.getInputDataSourcesValue((String) sortedKeys.get(i)) + ", ");
					
					//Putting DataSource Object into the map of GenomicIdentifierSet
					giSetObjectsMap.put(className, dataSourceObject);
					//Logger.out.debug("Sachin L " +dataSourceName);
					
					inputDsList.add(dataSourceName);
					
				}
				else
				{
					Logger.out.info("Blank value for the attribute->" + (String) sortedKeys.get(i));
				}
			}
			
			//Call to generate Map of GenomicIdentifierSet for last GenomicIdentifierSet
			generateGISetMap(tempDataSourceName, giSetMapKeys);
			
	
			Logger.out.info("No. of objects in giSet Map-->" + giSetMap.size());
	
			return giSetMap;
		}
		catch(Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			throw new BizLogicException(e.getMessage(), e);
		}
	}
	/**
	 * generate Map where key = input query and value = correspondicg GenomicIUdentifierSet object
	 * @param dataSourceName
	 * @param giSetMapKeys
	 * @throws Exception
	 */
	private void generateGISetMap(String dataSourceName, StringBuffer giSetMapKeys) throws Exception
	{
		//Creating object of GenomicIdentifierSet
		GenomicIdentifierSet giSet = new GenomicIdentifierSet();
		
		//Setting ConfidenceScore for GenomicIdentifierSet
		giSet.setConfidenceScore(confidenceScore);
		
		giSetMapKeys.delete(giSetMapKeys.length()-2,giSetMapKeys.length());
		String dataSourceKey = dataSourceName + "_" + giSetMapKeys;

		//Creating Parameter list for setter method of GenomicIdentifierSet
		Class[] giSetAttributes = null;

		Object[] giSetAttributesValue = null;

		giSetAttributes = new Class[]{Boolean.class};

		giSetAttributesValue = new Object[]{new Boolean(true)};

		//Setting setter method for Output DataSources submitted by User.
		for (int j = 0; j < dataSourcesWithFrequency.size(); j++)
		{
			String className = MetadataManager.getDataSourceAttribute(GCConstants.DATASOURCE_NAME, 
					dataSourcesWithFrequency.get(j).toString(), GCConstants.CLASS);

			String outputAttribute = MetadataManager.getDataSourceAttribute(GCConstants.DATASOURCE_NAME, 
					dataSourcesWithFrequency.get(j).toString(), GCConstants.OUTPUT_ATTRIBUTE);

			String outputAttributeMethodName = "set" + outputAttribute.substring(0, 1).toUpperCase() 
													 + outputAttribute.substring(1, outputAttribute.length());

			Logger.out.info("Setting " + outputAttributeMethodName + " of " + className);

			Class dataSourceClass = Class.forName("edu.wustl.geneconnect.domain." + className);

			Object dataSourceObject = giSetObjectsMap.get(className);

			if (dataSourceObject == null)
			{
				dataSourceObject = dataSourceClass.newInstance();
			}

			Method giSetMethod = dataSourceClass.getDeclaredMethod(outputAttributeMethodName, giSetAttributes);

			giSetMethod.invoke(dataSourceObject, giSetAttributesValue);

			giSetObjectsMap.put(className, dataSourceObject);

		}
		
		Object[] classes = giSetObjectsMap.keySet().toArray();
		
		String giSetMethodName = "";

		Class giSetClass = giSet.getClass();

		//Invoking setter method for Input DataSource submitted by User.
		for (int classesCounter = 0; classesCounter < classes.length; classesCounter++)
		{
			giSetMethodName = "set" + new String(classes[classesCounter].toString()).substring(0, 1).toUpperCase()
					+ classes[classesCounter].toString().substring(1, (classes[classesCounter].toString().length()));
			
			giSetAttributesValue = new Object[]{giSetObjectsMap.get(classes[classesCounter].toString())};

			if (classes[classesCounter].toString().equals("Gene"))
			{
				giSetAttributes = new Class[]{Gene.class};
			}
			else if (classes[classesCounter].toString().equals("Protein"))
			{
				giSetAttributes = new Class[]{Protein.class};
			}
			else if (classes[classesCounter].toString().equals("MessengerRNA"))
			{
				giSetAttributes = new Class[]{MessengerRNA.class};
			}

			//Invoking setter method of GenomicIdentifierSet
			Method giSetMethod = giSetClass.getDeclaredMethod(giSetMethodName, giSetAttributes);

			Logger.out.info("Calling giSet's method " + giSetMethodName);

			giSetMethod.invoke(giSet, giSetAttributesValue);

		}

		//Setting ConsensusIdentifierDataCollection for GenomicIdentifierSet
		giSet.setConsensusIdentifierDataCollection(consensusCollection);

		Logger.out.info("Setting giSet object with MapKey-> " + dataSourceKey);
		//Putting GenomicIdentifierSet into Map
		giSetMap.put(dataSourceKey, giSet);
		
		inputDsMap.put(dataSourceKey,inputDsList);
		inputDsList=new ArrayList();
		giSetObjectsMap = new HashMap();

	}
	/**
	 * generete collection of ONT objects from the path selecte by user on Select path page.
	 * @param inputData
	 * @return
	 */
	List generateOntObjects(InputDataInterface inputData)
	{
		ArrayList ontList = new ArrayList();
		try
		{
		
		Map data = inputData.getData();
		AdvancedSearchForm advanceSearchForm = (AdvancedSearchForm)data.get(GCConstants.FORM);
		
		String selectedOnt = advanceSearchForm.getSelectedPaths();
		Logger.out.debug("selectedOnt--" +selectedOnt);
		// path selected is delimeted by '$' 
		if(selectedOnt==null||selectedOnt.length()==0)
		{
			Logger.out.debug("return null selected ont");
			return null;
		}	
		
		selectedOnt = Utility.parseAnyOption(selectedOnt);
		
		StringTokenizer selectedOntList = new StringTokenizer(selectedOnt,"$",false);
		while(selectedOntList.hasMoreTokens())
		{
			String stringONT = (String)selectedOntList.nextToken();
			// each datasource and link type is delimeted by '_'
			// 1_3_4 -- in this 1 and 4  is datasource and link type is 3 
			
			StringTokenizer stringOntToken = new StringTokenizer(stringONT,GCConstants.DELEMITER,false);
			boolean isdataSourceToken=true;
			List tempOntList = new ArrayList();
			OrderOfNodeTraversal headONT = null;
			boolean isTosetHead =true;
			OrderOfNodeTraversal prevONT = null;
			while(stringOntToken.hasMoreTokens())
			{
				String datasSourceToken = stringOntToken.nextToken();
				String linkToken = null;
				if(stringOntToken.hasMoreTokens())
				{
					linkToken=stringOntToken.nextToken(); 
				}
				
				OrderOfNodeTraversal orderOfNodeTraversal = new OrderOfNodeTraversal();
				DataSource dataSource = new DataSource();
				String dsName = MetadataManager.getDataSourceName(datasSourceToken);
				dataSource.setName(dsName);
				orderOfNodeTraversal.setSourceDataSource(dataSource);
				if(linkToken!=null)
				{
					LinkType linkType = new LinkType();
					String linkName = MetadataManager.getLinkTypeName(linkToken);
					linkType.setType(linkName);
					orderOfNodeTraversal.setLinkType(linkType);
				}
				if(isTosetHead)
				{
					headONT = orderOfNodeTraversal;
					isTosetHead=false;
				}
				if(prevONT!=null)
				{
					prevONT.setChildOrderOfNodeTraversal(orderOfNodeTraversal);
				}
				prevONT = orderOfNodeTraversal;	

			}
			ontList.add(headONT);
			
		}
				
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return ontList;
	}
}
