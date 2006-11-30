/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.action.AdvancedSearchAction</p> 
 */

package edu.wustl.geneconnect.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.exceptionformatter.DefaultExceptionFormatter;
import edu.wustl.common.util.dbManager.DAOException;
import edu.wustl.common.util.global.Constants;
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
 * Action class for advanced search operations. This class will work as redirector and will 
 * invoke appropriate business logics for advanced search.
 * @author krunal_thakkar
 * @version 1.0
 */
public class AdvancedSearchAction extends Action
{

	/**
	 * Defalut Constructor
	 */
	public AdvancedSearchAction()
	{
		super();
	}

	//TODO : Logging needs to be added.

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
		Map searchAttributes = new HashMap();
		
		AdvancedSearchForm advancedSearchActionForm = (AdvancedSearchForm) form;
		String targetAction = advancedSearchActionForm.getTargetAction();
		
		request.setAttribute("advancedSearchForm", advancedSearchActionForm);
		
		try
		{
			//Obtain target action which needs to be performed
			
			Logger.out.info("targetAction is :" + targetAction);
			
			System.out.println("targetAction is :" + targetAction);
			
			if (targetAction.equals("search"))
			{
				//Search operation.
				
				return executeDisplayResults(mapping, form, request, response);
			}
			
			String targetActionParameter = request.getParameter("targetAction");
			System.out.println("TargetActionParameter on Action-->"+targetActionParameter);
			
			if(targetActionParameter != null && targetActionParameter.equals("updateMap"))
			{
				updateMap(advancedSearchActionForm, request);
			}
			
			
		}
		catch (BizLogicException bizExp)
		{
			Logger.out.error(bizExp.getMessage(), bizExp);
			ActionErrors errors = new ActionErrors();
			ActionError error = new ActionError("errors.item", bizExp.getMessage());
			errors.add(ActionErrors.GLOBAL_ERROR, error);
			saveErrors(request, errors);
		}
		catch (DAOException daoExp)
		{
			Logger.out.error(daoExp.getMessage(), daoExp);
			ActionErrors errors = new ActionErrors();
			ActionError error = new ActionError("errors.item", daoExp.getMessage());
			errors.add(ActionErrors.GLOBAL_ERROR, error);
			saveErrors(request, errors);
		}
		/**
		 * Get datasource attributes such as datasource name , id. 
		 */
		List dataSources = MetadataManager.getDataSourcesToDisplay();

		for (int i = 0; i < dataSources.size(); i++)
		{
			NameValueBean dataSource = (NameValueBean) dataSources.get(i);

			String attribute = MetadataManager.getDataSourceAttribute(GCConstants.DATASOURCE_NAME,
					dataSource.getName(), GCConstants.ATTRIBUTE);

			searchAttributes.put(dataSource.getName(), attribute);
		}

		Logger.out.info("Setting data source attributes map in request object");
		request.setAttribute(GCConstants.DATASOURCE_ATTRIBUTES, searchAttributes);

		Logger.out.info("Setting data source list in request object");
		request.setAttribute(GCConstants.DATA_SOURCES_KEY, dataSources);

		// do action when validation fails
		if(targetAction.equals("failure"))
		{
			// set the ouput data source selected by user in form and forward teh resuest to advanced search query page.
			advancedSearchActionForm.setOutputDataSources(createOutputDataSources(request));
			
			return mapping.findForward("failure");
		}
		// do action when user clicks on back to advanced serc page from select path
		if(targetAction.equals("nothing"))
		{
//			advancedSearchActionForm.setOutputDataSources(createOutputDataSources(request));
		}
			
		
		Logger.out.info("Forwarding to AdvancedSearch.jsp");
		return (mapping.findForward(GCConstants.FORWARD_TO_ADVANCED_PAGE));
	}

	/**
	 * This method will invoke business logic to perform simple serach operation.
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
	private ActionForward executeDisplayResults(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{

		HttpSession session = request.getSession();
		//Get the instance of business logic factory
		GeneConnectBizLogicFactory geneConnectBizLogicFactory = GeneConnectBizLogicFactory
				.getInstance();
		//Get the instance of required business logic 
		BizLogicInterface advancedSearchBizLogic = geneConnectBizLogicFactory
				.getBizLogic(GCConstants.ADVANCED_SEARCH_BIZLOGIC);
		
		//Form the input data which needs to be passed to the business logic
		InputDataInterface inputData = new InputData();
		Map data = new HashMap();
		
		AdvancedSearchForm advancedSearchActionForm = (AdvancedSearchForm) form;
		
		Map outputDataSources = (HashMap)advancedSearchActionForm.getOutputDataSources();
		/**
		 * Prepare Frequency Map where KEY = datasource name and value = predicate  
		 */
		if(outputDataSources.size() == 0)
		{
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
						if(freqValue==null||freqValue.length()==0)
						{	
							freqValue="0";
						}	
//						else if (freqValue.length() > 0)
//						{
//							
//						}
						Float freq =  new Float(freqValue);
						frequencyMap.put(bean.getName(),freq);
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
		}
		
		data.put(GCConstants.FORM, advancedSearchActionForm);
		
//		data.put(GCConstants.FREQUENCY_MAP,frequencyMap);
		
		
		inputData.setData(data);
		/**
		 * call bizliogc method to build domain objects and query the system
		 */
		ResultDataInterface resultData = advancedSearchBizLogic.getResult(inputData);
		
		/**
		 * Prepare Query list that os to be listed on search page in view display result combo box.
		 */
		Map allresultMap = resultData.getData();
		//Map queryKeyMap = new HashMap();
		List queryKeyList = new ArrayList();
		Set keySet = allresultMap.keySet();
		List keyList = new ArrayList(keySet);
		Collections.sort(keyList);
		for(Iterator iter=keyList.iterator();iter.hasNext();)
		{
			String k = (String)iter.next();
			System.out.println("Key :" +k);
			int ind = k.indexOf("_");
			if(ind>0)
			{
				String displayKey = k.substring(ind+1);
				NameValueBean  bean = new NameValueBean();
				bean.setName(displayKey);
				bean.setValue(k);
				//queryKeyMap.put(k,displayKey);
				queryKeyList.add(bean);
				
			}
		}
		/**
		 * Set the result in session and forward to SearchResultView Action
		 */
		//session.setAttribute(GCConstants.SELECTED_DATASOURCES,selectInputOutputList);
		session.setAttribute(GCConstants.RESULT_DATA_LIST, resultData);
		//session.setAttribute(GCConstants.QUERY_KEY_MAP, queryKeyMap);
		session.setAttribute(GCConstants.QUERY_KEY_MAP, queryKeyList);
		request.setAttribute(Constants.PAGEOF, GCConstants.ADVANCED_SEARCH);
		
		return (mapping.findForward(GCConstants.FORWARD_TO_RESULT_PAGE));
	}
	
	private Map createOutputDataSources(HttpServletRequest request) throws Exception
	{
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
					if(freqValue==null||freqValue.length()==0)
					{	
						freqValue="0";
					}	
//					else if (freqValue.length() > 0)
//					{
//						
//					}
					Float freq =  new Float(freqValue);
					frequencyMap.put(bean.getName(),freq);
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
		
		return frequencyMap;
	}

	private void updateMap(AdvancedSearchForm advancedSearchActionForm, HttpServletRequest request) throws Exception
	{
//		System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&  Update Map &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
//		System.out.println("Rows To Delete-->"+request.getParameter("rowsToDelete"));
		
		List dataSources = MetadataManager.getDataSourcesToDisplay();
		
		Map inputs = advancedSearchActionForm.getInputDataSources();
		
		Set inputKeys = inputs.keySet();
		
		List inputKeyList = new ArrayList(inputKeys);
		
		TreeMap sortedKeys = new TreeMap();
		
		for(int i=0; i<inputKeys.size(); i++)
		{
			String inputKey = (String)inputKeyList.get(i);
			
			StringTokenizer inputKeyTokenized = new StringTokenizer(inputKey, "_");
			
			sortedKeys.put(inputKeyTokenized.nextToken(), inputKeyTokenized.nextToken());
		}
		
		Set sortedKeySet = sortedKeys.keySet();
		
		List sortedKeyList = new ArrayList(sortedKeySet);
		
		String rowsToDelete = request.getParameter("rowsToDelete");
		
		StringTokenizer rowsTokened = new StringTokenizer(rowsToDelete, ",");
		
		while(rowsTokened.hasMoreTokens())
		{
			String rowId = rowsTokened.nextToken();
			
			if( (sortedKeyList.size()) > new Integer(rowId).intValue() )
			{
				String inputHeader = (String)sortedKeyList.get(new Integer(rowId).intValue());
				
				for(int i=0; i<dataSources.size(); i++)
				{
					NameValueBean dataSource = (NameValueBean)dataSources.get(i);
					
					inputs.remove(inputHeader+"_"+dataSource.getName());
					
//					System.out.println("To Delete-->"+(inputHeader+dataSource.getName()));
				}
			}
		}
		
		advancedSearchActionForm.setInputDataSources(inputs);
		
		request.setAttribute("advancedSearchForm", advancedSearchActionForm);
		
		HttpSession session = request.getSession();
		session.setAttribute("advancedSearchForm", new AdvancedSearchForm());
		
	}
	

}
