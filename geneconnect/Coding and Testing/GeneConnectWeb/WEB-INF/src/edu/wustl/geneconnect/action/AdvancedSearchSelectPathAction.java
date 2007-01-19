/**
 *<p>Copyright: (c) Washington University, School of Medicine 2006.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.action.AdvancedSearchSelectPathAction</p> 
 */

package edu.wustl.geneconnect.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.exceptionformatter.DefaultExceptionFormatter;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.geneconnect.actionForm.AdvancedSearchForm;
import edu.wustl.geneconnect.bizlogic.BizLogicInterface;
import edu.wustl.geneconnect.bizlogic.GeneConnectBizLogicFactory;
import edu.wustl.geneconnect.bizlogic.InputData;
import edu.wustl.geneconnect.bizlogic.InputDataInterface;
import edu.wustl.geneconnect.bizlogic.ResultDataInterface;
import edu.wustl.geneconnect.metadata.MetadataManager;
import edu.wustl.geneconnect.util.global.GCConstants;

/**
 * Action class for advanced search select path operations. This class will work as redirector and will 
 * invoke appropriate business logics for advanced search select path operation.
 * @author krunal_thakkar
 * @version 1.0
 */
public class AdvancedSearchSelectPathAction extends Action
{
	private Map selectedDataSources;
	
	private Map dataSourcesLinksMap;
	
	/**
	 * Defalut Constructor
	 */
	public AdvancedSearchSelectPathAction()
	{
		super();
	}

	/**
	 * Execute method which will be invoked by struts framework.
	 * @param mapping -
	 *            This is the action mapping object
	 * @param form -
	 *            Contains the form data
	 * @param request -
	 *            Request Object
	 * @param response -
	 *            Response Object
	 * @return ActionForward - Returns the the next page to forward to
	 * @throws Exception -
	 *             Throws any action related exceptions
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		//Initializing session object of request
		HttpSession session = request.getSession();
		
		//Initializing received formBean object
		AdvancedSearchForm advancedSearchActionForm = (AdvancedSearchForm) form;
		
		//Obtain target action which needs to be performed
		String targetAction = advancedSearchActionForm.getTargetAction();
		Logger.out.info("targetAction is :" + targetAction);
		
		//getting parameter from request
		String pagination = request.getParameter("isPaging");
		
		//fetching list of DataSources
		List dataSources = MetadataManager.getDataSourcesToDisplay();
		Logger.out.info("Setting data source list in request object");
		
		//setting DataSources into request object
		request.setAttribute(GCConstants.DATA_SOURCES_KEY, dataSources);
		
		//fetching map of the DataSources Links
		dataSourcesLinksMap = MetadataManager.getDataSourcesLinksMap();
		
		//setting map of the DataSource Links into request object
		request.setAttribute(GCConstants.DATA_SOURCES_LINKS_MAP, dataSourcesLinksMap);
		
		//checking if action to follow is to filter paths
		if (targetAction.equals("filterPaths"))
		{
			List filteredPathsForDataSources = null;
			
			//Creating map of InputDataSources
			Map sourcesMap = advancedSearchActionForm.getInputDataSources();
			
			//Search operation.
			Logger.out.debug("Target Action is filterPaths" + sourcesMap.size()+" selectedPaths-->"+advancedSearchActionForm.getSelectedPaths());
			
			Logger.out.debug("Filer Starts With->"+advancedSearchActionForm.getStartsWithDataSources()+" Ends With->"+advancedSearchActionForm.getEndsWithDataSources()+"ONT Filter Code->"+advancedSearchActionForm.getOntFilterCode()+" PathType->"+advancedSearchActionForm.getPathTypes());
			
			//Get the instance of business logic factory
			GeneConnectBizLogicFactory geneConnectBizLogicFactory = GeneConnectBizLogicFactory.getInstance();
			//Get the instance of required business logic 
			BizLogicInterface ontBizLogic = geneConnectBizLogicFactory.getBizLogic(GCConstants.ONT_BIZLOGIC);
			//Form the input data which needs to be passed to the business logic
			InputDataInterface inputData = new InputData();
			Map data = new HashMap();
			
			//putting all required parameters to filter paths into data map
			data.put(GCConstants.SELECTED_DATA_SOURCES, selectedDataSources);
			data.put(GCConstants.ONT_FILTER_CODE, new Integer(advancedSearchActionForm.getOntFilterCode()) );
			data.put(GCConstants.PATH_TYPE, advancedSearchActionForm.getPathTypes());
			data.put(GCConstants.STARTS_WITH_DATA_SOURCE, advancedSearchActionForm.getStartsWithDataSources());
			data.put(GCConstants.ENDS_WITH_DATA_SOURCE, advancedSearchActionForm.getEndsWithDataSources());
			
			//setting data map into instance of InputDataInterface
			inputData.setData(data);

			//Apply business logic and Get result 
			Logger.out.info("call to ONTBizLogic.getResult");
			ResultDataInterface resultData = ontBizLogic.getResult(inputData);
			
			//fetching map of result from instance of ResultDataInterface
			data = resultData.getData();
			
			//fetching list of filtered paths from data map returned by BizLogic
			filteredPathsForDataSources = (ArrayList)data.get(GCConstants.RESULT_DATA_LIST);
			
			//setting filtered paths into request object to display on JSP page
			request.setAttribute(GCConstants.VALID_PATHS_LIST_FOR_DATA_SOURCES, filteredPathsForDataSources);
			
			//setting filtered paths into session object
			session.setAttribute(GCConstants.SPREADSHEET_PATH_LIST, filteredPathsForDataSources);
			
			//setting list of DataSources into request object
			request.setAttribute(GCConstants.DATA_SOURCES_LIST, session.getAttribute(GCConstants.DATA_SOURCES_LIST));
			
		}
		//in case of this action is being called for the first time 
		else
		{
			List validPathsForDataSources=null;
			Logger.out.debug("pagination " +pagination);
			
			if (pagination == null || pagination.equals("false"))
			{
				//creating list of InputOutput datasources submitted by user
				List dataSourcesList = createListOfDataSources(form, request);
				
				session.setAttribute(GCConstants.DATA_SOURCES_LIST, dataSourcesList);
				
				Logger.out.debug("ONT Filter Code at getAllValidPathsForDataSources()->"+advancedSearchActionForm.getOntFilterCode());
				Logger.out.debug("PathType at getAllValidPathsForDataSources()->"+advancedSearchActionForm.getPathTypes());
				
				//Get the instance of business logic factory
				GeneConnectBizLogicFactory geneConnectBizLogicFactory = GeneConnectBizLogicFactory.getInstance();
				//Get the instance of required business logic 
				BizLogicInterface ontBizLogic = geneConnectBizLogicFactory.getBizLogic(GCConstants.ONT_BIZLOGIC);
				//Form the input data which needs to be passed to the business logic
				InputDataInterface inputData = new InputData();
				Map data = new HashMap();
				
				//putting all required parameters to filter paths into data map
				data.put(GCConstants.SELECTED_DATA_SOURCES, selectedDataSources);
				data.put(GCConstants.ONT_FILTER_CODE, new Integer(advancedSearchActionForm.getOntFilterCode()) );
				data.put(GCConstants.PATH_TYPE, advancedSearchActionForm.getPathTypes());
				data.put(GCConstants.STARTS_WITH_DATA_SOURCE, advancedSearchActionForm.getStartsWithDataSources());
				data.put(GCConstants.ENDS_WITH_DATA_SOURCE, advancedSearchActionForm.getEndsWithDataSources());
				
				//setting data map into instance of InputDataInterface
				inputData.setData(data);

				//Apply business logic and Get result 
				Logger.out.info("call to ONTBizLogic.getResult");
				ResultDataInterface resultData = ontBizLogic.getResult(inputData);
				
				//fetching map of result from instance of ResultDataInterface
				data = resultData.getData();
				
				//fetching list of filtered paths from data map returned by BizLogic
				validPathsForDataSources = (ArrayList)data.get(GCConstants.RESULT_DATA_LIST);
				
				Logger.out.debug("No. of DefaultValidPaths-->"+validPathsForDataSources.size());
				
				//setting filtered paths into request object to display on JSP page
				request.setAttribute(GCConstants.VALID_PATHS_LIST_FOR_DATA_SOURCES, validPathsForDataSources);
				
				//setting filtered paths into session object
				session.setAttribute(GCConstants.SPREADSHEET_PATH_LIST, validPathsForDataSources);
				
				//setting valid filtered paths into session object 
				session.setAttribute(GCConstants.VALID_PATHS_LIST_FOR_DATA_SOURCES, validPathsForDataSources);
			}
			//setting list of DataSources into request object
			request.setAttribute(GCConstants.DATA_SOURCES_LIST, session.getAttribute(GCConstants.DATA_SOURCES_LIST));
		}
		
		Logger.out.debug("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! Back from SelectPath-->"+advancedSearchActionForm.isBackFromSelectPath());
		//to check whether InputOutput datasources are changed by user while calling this action for second time
		if(advancedSearchActionForm.isBackFromSelectPath())
		{
			//fetching list of InputOutput datasources entered by user this time
			List inputOutputs = (ArrayList)session.getAttribute(GCConstants.DATA_SOURCES_LIST);
			
			Logger.out.debug("InputOutput size==>"+inputOutputs.size());
			Logger.out.debug("Initial InputOutput-->"+advancedSearchActionForm.getInitialInputOutput());
			Logger.out.debug("AlreadySelectedPaths-->"+advancedSearchActionForm.getSelectedPaths());
			
			//fetching string of initial InputOutput datasources entered by user
			String initialIOs = advancedSearchActionForm.getInitialInputOutput();
			
			//tokenizing string of initial InputOutput datasources by delimiter i.e. $
			StringTokenizer initialIOsTokenized = new StringTokenizer(initialIOs, "$");
			
			boolean isSame = true;
			
			//iterating through all InputOutput datasources intially submitted
			while(initialIOsTokenized.hasMoreTokens())
			{
				String io = initialIOsTokenized.nextToken();
				
				if(io.equals("-1"))
					continue;
				
				boolean isAvailable = false;
				
				//checking whether initially entered Input/Output datasource 
				//is available into list of InputOutput datasources entered this time
				for(int i=0; i<inputOutputs.size(); i++)
				{
					NameValueBean dataSource = (NameValueBean)inputOutputs.get(i);
					
					if(io.equals(dataSource.getValue()))
					{
						isAvailable = true;
						break;
					}
				}
				
				//checking if datasource is not available
				if(!isAvailable)
				{
					isSame = false;
					break;
				}
			}
			
			//InputOutput datasources are same
			if(isSame)
			{
				Logger.out.debug("!!!!!!!!!!!!!!!!!!!!!!! Same InputOutput !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				//setting already selected paths by user into request object to display on JSP page
				request.setAttribute(GCConstants.ALREADY_SELECTED_PATHS, setAlreadySelectedPaths(advancedSearchActionForm.getSelectedPaths()));
			}
		}
		
		
		int pageNum = GCConstants.START_PAGE;
		List paginationDataList = null, pathList = null;

		//Get the SPREADSHEET_DATA_LIST and SPREADSHEET_COLUMN_LIST from the session.
		pathList = (List) session.getAttribute(GCConstants.SPREADSHEET_PATH_LIST);

		if (request.getParameter(GCConstants.PAGE_NUMBER) != null)
		{
			pageNum = Integer.parseInt(request.getParameter(GCConstants.PAGE_NUMBER));
			Logger.out.debug("IN pageNum action " + pageNum);
		}

		if (pathList != null)
		{
			//Set the start index of the list.
			int startIndex = (pageNum - 1) * GCConstants.NUMBER_RESULTS_PER_PAGE_SEARCH;
			//Set the end index of the list.
			int endIndex = startIndex + GCConstants.NUMBER_RESULTS_PER_PAGE_SEARCH;
			if (endIndex > pathList.size())
			{
				endIndex = pathList.size();
			}
			//Get the paginationDataList from startIndex to endIndex of the dataList.
			paginationDataList = pathList.subList(startIndex, endIndex);

			//Set the total no of records in the request object to be used by pagination tag.
			request.setAttribute(GCConstants.TOTAL_RESULTS, Integer.toString(pathList.size()));
		}
		else
		{
			//Set the total no of records in the request object to be used by pagination tag.
			request.setAttribute(GCConstants.TOTAL_RESULTS, Integer.toString(0));
		}

		//Set the paginationDataList in the request to be shown by grid control.
		request.setAttribute(GCConstants.PAGINATION_DATA_LIST, paginationDataList);

		//Set the columnList in the request to be shown by grid control.
		

		//Set the current pageNum in the request to be uesd by pagination Tag.
		request.setAttribute(GCConstants.PAGE_NUMBER, Integer.toString(pageNum));

		//Set the result per page attribute in the request to be uesd by pagination Tag.
		request.setAttribute(GCConstants.RESULTS_PER_PAGE, Integer
				.toString(GCConstants.NUMBER_RESULTS_PER_PAGE_SEARCH));

		//setting SelectedPaths by user into request object, this will happen while user submits the same action to filter paths
		String alreadySelectedPaths = request.getParameter("alreadySelectedPaths");
		if(alreadySelectedPaths != null)
		{
			if(alreadySelectedPaths.length() > 0)
			{
				request.setAttribute(GCConstants.ALREADY_SELECTED_PATHS, setAlreadySelectedPaths(alreadySelectedPaths));
			}
		}
		
		//storing map of Datasources into request object
		Map dataSourcesMap = MetadataManager.getDataSourceMap();
		request.setAttribute(GCConstants.DATA_SOURCES_MAP, dataSourcesMap);
		
		//forwarding to AdvancedSearchSelectPath page 
		return (mapping.findForward(GCConstants.FORWARD_TO_ADVANCED_SELECT_PATH_PAGE));
	}
	
	private List createListOfDataSources(ActionForm form, HttpServletRequest request) throws Exception
	{
		//initializing received formbean object
		AdvancedSearchForm advancedSearchActionForm = (AdvancedSearchForm) form;
		
		//Creating map of InputDataSources
		Map sourcesMap = advancedSearchActionForm.getInputDataSources();
		
		//creating collection of keys of Input datasources
		Collection keySet = sourcesMap.keySet();
		
		//creating list of keys
		List keysList = new ArrayList(keySet);

		//creating map to store entered datasources
		Map enteredDataSources = new HashMap();
		
		//initializing map object for selected datasources
		selectedDataSources = new HashMap();
		
		//creating map for Input datasources
		Map inputDataSources = new HashMap();
		
		//creating map for Output datasources
		Map outputDataSources = new HashMap();
		
		//Iterating through the list of InputDataSources submitted by User 
		//to generate map of Input datasources submitted with value
		for (int i = 0; i < keysList.size(); i++)
		{
			//Checking whether user has submitted value for InputDataSource
			if ( (sourcesMap.get((String) keysList.get(i)) != null)	& !(sourcesMap.get((String) keysList.get(i)).equals("")) )
			{
				StringTokenizer dataSourceToken = new StringTokenizer((String) keysList.get(i), "_");

				String dataSourceName = dataSourceToken.nextToken();
				
				List inputDataSourcesList = new ArrayList();
				
				if(inputDataSources.get(dataSourceName) != null)
				{
					inputDataSourcesList = (ArrayList) inputDataSources.get(dataSourceName);
					
					String key = dataSourceName;
					
					dataSourceName = dataSourceToken.nextToken();
					
					String id = MetadataManager.getDataSourceAttribute(GCConstants.DATASOURCE_NAME,
							dataSourceName, GCConstants.DATASOURCE_ID);
					
					Logger.out.debug("Entering into InputDataSources->"+key+" "+dataSourceName);
					
					inputDataSourcesList.add(id);
					
					inputDataSources.put(key, inputDataSourcesList);
				}
				else
				{
					String key = dataSourceName;
					
					dataSourceName = dataSourceToken.nextToken();
					
					String id = MetadataManager.getDataSourceAttribute(GCConstants.DATASOURCE_NAME,
							dataSourceName, GCConstants.DATASOURCE_ID);
					
					Logger.out.debug("Entering into InputDataSources first time->"+key+" "+dataSourceName);
					
					inputDataSourcesList.add(id);
					
					inputDataSources.put(key, inputDataSourcesList);
					
				}
				
				String id = MetadataManager.getDataSourceAttribute(GCConstants.DATASOURCE_NAME,
						dataSourceName, GCConstants.DATASOURCE_ID);
				
				enteredDataSources.put(dataSourceName, id);
				
				Logger.out.debug("InputDataSource entered on AdvanceSearch page:- "+dataSourceName+" id->"+id);
			}
		}
		
		//fetching list of datasources
		List dataSources = MetadataManager.getDataSourcesToDisplay();
		
		Map frequencyMap = new HashMap();
		
		//Iterating through the list of OutputDataSources submitted by User 
		//to generate map of Output datasources selected by User
		for (int i = 0; i < dataSources.size(); i++)
		{
			try
			{
				NameValueBean bean = (NameValueBean) dataSources.get(i);
				if ((request.getParameter(bean.getName()) != null))
				{
					Logger.out.info("Frequency Value==>"
							+ request.getParameter(bean.getName() + "_FrequenceValue"));
	
					String freqValue = request.getParameter(bean.getName() + "_FrequenceValue");
					
					enteredDataSources.put(bean.getName(), bean.getValue());
					Logger.out.debug("OutputDataSource entered on AdvanceSearch page:- "+bean.getName());
					
					if(freqValue==null||freqValue.length()==0)
					{	
						freqValue="0";
					}	

					Float freq =  new Float(freqValue);
					frequencyMap.put(bean.getName(),freq);
					
					Logger.out.debug("Entering into OutputDataSources->"+bean.getValue()+" "+bean.getName());
					outputDataSources.put(bean.getValue(), bean.getName());
				}
			}
			catch (NumberFormatException e)
			{
				Logger.out.info("Entered value is not a float value for Frequency");
				String arg[] = new String[]{"Frequency"};
				String errmsg = new DefaultExceptionFormatter().getErrorMessage(
						"errors.confFreq.value", arg);
				Logger.out.info(errmsg);
				throw new BizLogicException(errmsg);
			}
		}
		
		//setting map of Output datasources into formbean object
		advancedSearchActionForm.setOutputDataSources(frequencyMap);
		
		Collection enteredKeys = enteredDataSources.keySet();
		
		List enteredDataSourcesKeyList = new ArrayList(enteredKeys);
		
		//creating list of entered InputOutput datasources from the generated map named 'enteredDataSources' 
		List enteredDataSourcesList = new ArrayList();
		for(int i=0; i<enteredDataSourcesKeyList.size(); i++)
		{
			enteredDataSourcesList.add(new NameValueBean(enteredDataSourcesKeyList.get(i),enteredDataSources.get(enteredDataSourcesKeyList.get(i))));
		}
		
		//seting Input and Output datasources into map of selected datasources
		selectedDataSources.put(GCConstants.INPUT_DATA_SOURCES, inputDataSources);
		selectedDataSources.put(GCConstants.OUTPUT_DATA_SOURCES, outputDataSources);
		
		//putting updated formbean into session to populate entered InputOutput datasources
		HttpSession session = request.getSession();
		session.setAttribute("advancedSearchForm", advancedSearchActionForm );
		
		//returning list of entered InputOutput datasources
		return enteredDataSourcesList;
	}
	
	/**
	 * This function work as a parser to create map of selected paths(ONTs) by user on AdvancesSearchSelectPath page 
	 */
	private Map setAlreadySelectedPaths(String alreadySelectedPaths)
	{
		//creating map to store selected pahts
		Map alreadySelectedPathsMap = new HashMap();
		
//		Logger.out.debug("Already Selected Paths on Action-->"+alreadySelectedPaths);
		
		//tokenizing string of selected paths by delimiter i.e. #
		StringTokenizer alreadySelectedPathsTokenized = new StringTokenizer(alreadySelectedPaths,"#");
		
		while(alreadySelectedPathsTokenized.hasMoreTokens())
		{
			String alreadySelectedPathName = alreadySelectedPathsTokenized.nextToken();
			
			//fetching value of the path from tokened path
			String alreadySelectedPathValue = alreadySelectedPathName.substring((alreadySelectedPathName.indexOf("=")+1), alreadySelectedPathName.length());
			
			//fetching name of the path from tokened path
			alreadySelectedPathName = alreadySelectedPathName.substring(0, alreadySelectedPathName.indexOf("="));
			
			//putting Name and Value of the selected path into map 
			alreadySelectedPathsMap.put(alreadySelectedPathName, alreadySelectedPathValue);
			
			Logger.out.debug("PathName==>"+alreadySelectedPathName+" PathValue==>"+alreadySelectedPathValue);
		}
		
		//returning map of selected paths
		return alreadySelectedPathsMap;
	}

}