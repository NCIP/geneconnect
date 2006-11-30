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
		HttpSession session = request.getSession();
		
		AdvancedSearchForm advancedSearchActionForm = (AdvancedSearchForm) form;
		
		//Obtain target action which needs to be performed
		String targetAction = advancedSearchActionForm.getTargetAction();
		Logger.out.info("targetAction is :" + targetAction);
		
		String pagination = request.getParameter("isPaging");
		
		List dataSources = MetadataManager.getDataSourcesToDisplay();
		Logger.out.info("Setting data source list in request object");
		request.setAttribute(GCConstants.DATA_SOURCES_KEY, dataSources);
		
		
		if (targetAction.equals("filterPaths"))
		{
			List filteredPathsForDataSources = null;
			
			//Creating map of InputDataSources
			Map sourcesMap = advancedSearchActionForm.getInputDataSources();
			
			//Search operation.
			System.out.println("Target Action is filterPaths" + sourcesMap.size()+" selectedPaths-->"+advancedSearchActionForm.getSelectedPaths());
			
			System.out.println("Filer Starts With->"+advancedSearchActionForm.getStartsWithDataSources()+" Ends With->"+advancedSearchActionForm.getEndsWithDataSources()+" PathType->"+advancedSearchActionForm.getPathTypes());
			
			filteredPathsForDataSources = filterPaths(advancedSearchActionForm.getPathTypes(), advancedSearchActionForm.getStartsWithDataSources(), advancedSearchActionForm.getEndsWithDataSources(), session);
			
			request.setAttribute(GCConstants.VALID_PATHS_LIST_FOR_DATA_SOURCES, filteredPathsForDataSources);
			session.setAttribute(GCConstants.SPREADSHEET_PATH_LIST, filteredPathsForDataSources);
			
			request.setAttribute(GCConstants.DATA_SOURCES_LIST, session.getAttribute(GCConstants.DATA_SOURCES_LIST));
			
		}
		else
		{
			List validPathsForDataSources=null;
			System.out.println("pagination " +pagination);
			
			if (pagination == null || pagination.equals("false"))
			{
				List dataSourcesList = createListOfDataSources(form, request);
				
				session.setAttribute(GCConstants.DATA_SOURCES_LIST, dataSourcesList);
				
				validPathsForDataSources = MetadataManager.getAllValidPathsForDataSources(selectedDataSources);
				request.setAttribute(GCConstants.VALID_PATHS_LIST_FOR_DATA_SOURCES, validPathsForDataSources);
				session.setAttribute(GCConstants.SPREADSHEET_PATH_LIST, validPathsForDataSources);
				
				session.setAttribute(GCConstants.VALID_PATHS_LIST_FOR_DATA_SOURCES, validPathsForDataSources);
			}
			
			request.setAttribute(GCConstants.DATA_SOURCES_LIST, session.getAttribute(GCConstants.DATA_SOURCES_LIST));
		}
			
		
		
		int pageNum = GCConstants.START_PAGE;
		List paginationDataList = null, pathList = null;

		//Get the SPREADSHEET_DATA_LIST and SPREADSHEET_COLUMN_LIST from the session.
		//pathList = (List) session.getAttribute(GCConstants.SPREADSHEET_PATH_LIST);
		pathList = (List) session.getAttribute(GCConstants.SPREADSHEET_PATH_LIST);

		if (request.getParameter(GCConstants.PAGE_NUMBER) != null)
		{
			pageNum = Integer.parseInt(request.getParameter(GCConstants.PAGE_NUMBER));
			System.out.println("IN pageNum action " + pageNum);
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

		
		String alreadySelectedPaths = request.getParameter("alreadySelectedPaths");
		if(alreadySelectedPaths != null)
		{
			if(alreadySelectedPaths.length() > 0)
			{
				request.setAttribute(GCConstants.ALREADY_SELECTED_PATHS, setAlreadySelectedPaths(alreadySelectedPaths));
			}
		}
		
		dataSourcesLinksMap = MetadataManager.getDataSourcesLinksMap();
		request.setAttribute(GCConstants.DATA_SOURCES_LINKS_MAP, dataSourcesLinksMap);
		
		Map dataSourcesMap = MetadataManager.getDataSourceMap();
		request.setAttribute(GCConstants.DATA_SOURCES_MAP, dataSourcesMap);
		
//		request.setAttribute("advancedSearchForm", form);
		
		return (mapping.findForward(GCConstants.FORWARD_TO_ADVANCED_SELECT_PATH_PAGE));
	}
	
	private List createListOfDataSources(ActionForm form, HttpServletRequest request) throws Exception
	{
		AdvancedSearchForm advancedSearchActionForm = (AdvancedSearchForm) form;
		
		//Creating map of InputDataSources
		Map sourcesMap = advancedSearchActionForm.getInputDataSources();
		
		Collection keySet = sourcesMap.keySet();
		
		List sortedKeys = new ArrayList(keySet);

		Map enteredDataSources = new HashMap();
		
		selectedDataSources = new HashMap();
		
		Map inputDataSources = new HashMap();
		
		Map outputDataSources = new HashMap();
		
		//Iterating through the list of InputDataSources submitted by User to generate set of GenomicIdentifierSet 
		for (int i = 0; i < sortedKeys.size(); i++)
		{
			
			//Checking whether user has submitted value for InputDataSource
			if (sourcesMap.get((String) sortedKeys.get(i)) != null
					& !(sourcesMap.get((String) sortedKeys.get(i)).equals("")))
			{
				StringTokenizer dataSourceToken = new StringTokenizer((String) sortedKeys.get(i), "_");

				String dataSourceName = dataSourceToken.nextToken();
				
				List inputDataSourcesList = new ArrayList();
				
				if(inputDataSources.get(dataSourceName) != null)
				{
					inputDataSourcesList = (ArrayList) inputDataSources.get(dataSourceName);
					
					String key = dataSourceName;
					
					dataSourceName = dataSourceToken.nextToken();
					
					String id = MetadataManager.getDataSourceAttribute(GCConstants.DATASOURCE_NAME,
							dataSourceName, GCConstants.DATASOURCE_ID);
					
//					System.out.println("Entering into InputDataSources->"+key+" "+dataSourceName);
					
					inputDataSourcesList.add(id);
					
					inputDataSources.put(key, inputDataSourcesList);
				}
				else
				{
					String key = dataSourceName;
					
					dataSourceName = dataSourceToken.nextToken();
					
					String id = MetadataManager.getDataSourceAttribute(GCConstants.DATASOURCE_NAME,
							dataSourceName, GCConstants.DATASOURCE_ID);
					
//					System.out.println("Entering into InputDataSources first time->"+key+" "+dataSourceName);
					
					inputDataSourcesList.add(id);
					
					inputDataSources.put(key, inputDataSourcesList);
					
				}
				
				
				
				String id = MetadataManager.getDataSourceAttribute(GCConstants.DATASOURCE_NAME,
						dataSourceName, GCConstants.DATASOURCE_ID);
				
				enteredDataSources.put(dataSourceName, id);
				
//				System.out.println("InputDataSource entered on AdvanceSearch page:- "+dataSourceName+" id->"+id);
			}
		}
		
		List dataSources = MetadataManager.getDataSourcesToDisplay();
		
		Map frequencyMap = new HashMap();
		
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
					
//					if (freqValue.length() > 0)
//					{
						enteredDataSources.put(bean.getName(), bean.getValue());
	//					System.out.println("OutputDataSource entered on AdvanceSearch page:- "+bean.getName());
//					}
					
					if(freqValue==null||freqValue.length()==0)
					{	
						freqValue="0";
					}	
//					else if (freqValue.length() > 0)
//					{
//					}
					Float freq =  new Float(freqValue);
					frequencyMap.put(bean.getName(),freq);
					
					System.out.println("Entering into OutputDataSources->"+bean.getValue()+" "+bean.getName());
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
		
		advancedSearchActionForm.setOutputDataSources(frequencyMap);
		
		Collection enteredKeys = enteredDataSources.keySet();
		
		List enteredDataSourcesKeyList = new ArrayList(enteredKeys);
		
		List enteredDataSourcesList = new ArrayList();
		for(int i=0; i<enteredDataSourcesKeyList.size(); i++)
		{
			enteredDataSourcesList.add(new NameValueBean(enteredDataSourcesKeyList.get(i),enteredDataSources.get(enteredDataSourcesKeyList.get(i))));
		}
		
		selectedDataSources.put(GCConstants.INPUT_DATA_SOURCES, inputDataSources);
		selectedDataSources.put(GCConstants.OUTPUT_DATA_SOURCES, outputDataSources);
		
		HttpSession session = request.getSession();
		session.setAttribute("advancedSearchForm", advancedSearchActionForm );
		
		return enteredDataSourcesList;
	}
	
	private List filterPaths(String pathType, String startsWithDataSource, String endsWithDataSource, HttpSession session)
	{
		List filteredPathsForDataSources = new ArrayList();
		
		List validPathsForDataSources = (ArrayList)session.getAttribute(GCConstants.VALID_PATHS_LIST_FOR_DATA_SOURCES);
		
		if(validPathsForDataSources != null)
		{
			for(int i=0; i<validPathsForDataSources.size(); i++)
			{
				String validPath = (String)validPathsForDataSources.get(i);
				
				if(startsWithDataSource.equals("-1") & endsWithDataSource.equals("-1"))
				{
					filteredPathsForDataSources = validPathsForDataSources;
					break;
				}
				else
				{
					if( (startsWithDataSource.equals("-1")) & (!endsWithDataSource.equals("-1")) )
					{
						if(validPath.endsWith(endsWithDataSource))
						{
							filteredPathsForDataSources.add(validPath);
						}
					}
					else if( (endsWithDataSource.equals("-1")) & (!startsWithDataSource.equals("-1")) )
					{
						if(validPath.startsWith(startsWithDataSource))
						{
							filteredPathsForDataSources.add(validPath);
						}
					}
					else if( (!startsWithDataSource.equals("-1")) & (!endsWithDataSource.equals("-1")) )
					{
						if(validPath.startsWith(startsWithDataSource) & validPath.endsWith(endsWithDataSource))
						{
							filteredPathsForDataSources.add(validPath);
						}
					}
				}
			}
			
	
			System.out.println("Size before filter-->"+filteredPathsForDataSources.size());
			//Filtering Paths as per LinkType...
			
			//If LinkType to filter is All
			if(pathType.equals("-1"))
			{
				//Nothing to filter  :-)
			}
			//If LinkType to filter is Shortest
			else if(pathType.equals("2"))
			{
				int minNoOfNodes;
				
				minNoOfNodes = (((String)filteredPathsForDataSources.get(0)).length() / 2)+1;
				
				for(int i=1; i<filteredPathsForDataSources.size(); i++)
				{
					int noOfNodes = (((String)filteredPathsForDataSources.get(i)).length() / 2)+1;
					System.out.println("No. of Nodes-->"+noOfNodes+ " Path-->"+(String)filteredPathsForDataSources.get(i));
					if(noOfNodes < minNoOfNodes)
						minNoOfNodes = noOfNodes;
				}
				
				System.out.println("Minimum no of Nodes-->"+minNoOfNodes);
				List shortestPathsForDataSources = new ArrayList();
				
				for(int i=0; i<filteredPathsForDataSources.size(); i++)
				{
					int noOfNodes = (((String)filteredPathsForDataSources.get(i)).length() / 2)+1;
					
					System.out.println("NoOfNodes to check-->"+noOfNodes);
					
					if(noOfNodes == minNoOfNodes)
					{
						System.out.println("Filtering in Shortest-->"+(String)filteredPathsForDataSources.get(i));
						shortestPathsForDataSources.add(filteredPathsForDataSources.get(i));
					}
				}
				
				filteredPathsForDataSources = shortestPathsForDataSources;
				
			}
			//If LinkType to filter is Alignment=Based
			else if(pathType.equals("0"))
			{
				List alignmentPathsForDataSources = new ArrayList();
				
				for(int i=0; i<filteredPathsForDataSources.size(); i++)
				{
					String filteredPath = (String) filteredPathsForDataSources.get(i);
					System.out.println("Filtered Path==>"+filteredPath);
					
					boolean validPath=false;
					int counter = 0;
					
					
					WHILE:
					while(counter< (filteredPath.length()-1))
					{
						List links = (ArrayList)dataSourcesLinksMap.get(filteredPath.substring(counter, counter+3));
						
						for(int j=0; j<links.size(); j++)
						{
							NameValueBean link = (NameValueBean)links.get(j);
							
							if(link.getValue().equals("4") || link.getValue().equals("8"))
							{
								//System.out.println("Breaking while");
								validPath = true;
								break WHILE;
							}
						}
						//System.out.println("Nodes->"+filteredPath.substring(counter, counter+3));
						
						counter+=2;
					}
						
					if(validPath)
					{
						System.out.println("Truely valid Path-->"+filteredPath);
						alignmentPathsForDataSources.add(filteredPathsForDataSources.get(i));
					}
				}
				
				filteredPathsForDataSources = alignmentPathsForDataSources;
			}
			//If LinkType to filter is Non-Alignment
			else if(pathType.equals("1"))
			{
				List nonAlignmentPathsForDataSources = new ArrayList();
				for(int i=0; i<filteredPathsForDataSources.size(); i++)
				{
					String filteredPath = (String) filteredPathsForDataSources.get(i);
					System.out.println("Filtered Path==>"+filteredPath);
					
					boolean validPath=false;
					int counter = 0;
					
					WHILE:
					while(counter< (filteredPath.length()-1))
					{
						List links = (ArrayList)dataSourcesLinksMap.get(filteredPath.substring(counter, counter+3));
						
						for(int j=0; j<links.size(); j++)
						{
							NameValueBean link = (NameValueBean)links.get(j);
							
							if(link.getValue().equals("1") || link.getValue().equals("2"))
							{
								//System.out.println("Breaking while");
								validPath = true;
								break WHILE;
							}
						}
						//System.out.println("Nodes->"+filteredPath.substring(counter, counter+3));
						
						counter+=2;
					}
					
					if(validPath)
					{
						System.out.println("Truely valid Path-->"+filteredPath);
						nonAlignmentPathsForDataSources.add(filteredPathsForDataSources.get(i));
					}
				}
				filteredPathsForDataSources = nonAlignmentPathsForDataSources;
			}
		}
		
		System.out.println("Size after filter-->"+filteredPathsForDataSources.size());
		
		return filteredPathsForDataSources;
	}
	
	private Map setAlreadySelectedPaths(String alreadySelectedPaths)
	{
		Map alreadySelectedPathsMap = new HashMap();
		
//		System.out.println("Already Selected Paths on Action-->"+alreadySelectedPaths);
		
		StringTokenizer alreadySelectedPathsTokenized = new StringTokenizer(alreadySelectedPaths,"#");
		
		while(alreadySelectedPathsTokenized.hasMoreTokens())
		{
			String alreadySelectedPathName = alreadySelectedPathsTokenized.nextToken();
			
			String alreadySelectedPathValue = alreadySelectedPathName.substring((alreadySelectedPathName.indexOf("=")+1), alreadySelectedPathName.length());
			
			alreadySelectedPathName = alreadySelectedPathName.substring(0, alreadySelectedPathName.indexOf("="));
			
			alreadySelectedPathsMap.put(alreadySelectedPathName, alreadySelectedPathValue);
			
//			System.out.println("PathName==>"+alreadySelectedPathName+" PathValue==>"+alreadySelectedPathValue);
		}
		
		return alreadySelectedPathsMap;
	}

}
