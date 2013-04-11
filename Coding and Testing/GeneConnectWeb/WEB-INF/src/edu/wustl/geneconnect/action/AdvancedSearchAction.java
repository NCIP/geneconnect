/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>ClassName: java edu.wustl.geneconnect.action.AdvancedSearchAction</p> 
 */

package edu.wustl.geneconnect.action;

import java.util.ArrayList;
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
import edu.wustl.geneconnect.util.global.Utility;

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
		//Initializing map of Attributes to search 
		Map searchAttributes = new HashMap();

		//typecasting formbean object to AdvanceSearchFrom instance
		AdvancedSearchForm advancedSearchActionForm = (AdvancedSearchForm) form;

		//setting formbean instance into request object  
		request.setAttribute("advancedSearchForm", advancedSearchActionForm);
		//logging attributes if this request is called by clicking "Back To Query" from AdvancedSearchSelectPath page
		Logger.out.debug("@@@@@@@@@@@@@@@@@@@@@@@ Back from SelectPath-->"
				+ advancedSearchActionForm.isBackFromSelectPath());
		if (advancedSearchActionForm.isBackFromSelectPath())
		{
			Logger.out.debug("InputDataSources size-->"
					+ advancedSearchActionForm.getInputDataSources().size());
			Logger.out.debug("OutputDataSources size-->"
					+ advancedSearchActionForm.getInputDataSources().size());
			Logger.out.debug("AlreadySelectedPaths-->"
					+ advancedSearchActionForm.getSelectedPaths());
			Logger.out.debug("Initial IO-->" + advancedSearchActionForm.getInitialInputOutput());
		}

		//initializing string of target action to be performed
		String targetAction = advancedSearchActionForm.getTargetAction();

		try
		{
			//Obtain target action which needs to be performed
			Logger.out.debug("targetAction is :" + targetAction);

			if (targetAction.equals("search"))
			{
				//Search operation.
				return executeDisplayResults(mapping, form, request, response);
			}

			String targetActionParameter = request.getParameter("targetAction");
			Logger.out.debug("TargetActionParameter on Action-->" + targetActionParameter);

			if (targetActionParameter != null && targetActionParameter.equals("updateMap"))
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

		//populating map of the attributes to search
		for (int i = 0; i < dataSources.size(); i++)
		{
			NameValueBean dataSource = (NameValueBean) dataSources.get(i);

			String attribute = MetadataManager.getDataSourceAttribute(GCConstants.DATASOURCE_NAME,
					dataSource.getName(), GCConstants.ATTRIBUTE);

			searchAttributes.put(dataSource.getName(), attribute);
		}

		//Setting data source attributes map in request object
		Logger.out.info("Setting data source attributes map in request object");
		request.setAttribute(GCConstants.DATASOURCE_ATTRIBUTES, searchAttributes);

		//Setting data source list in request object
		Logger.out.info("Setting data source list in request object");
		request.setAttribute(GCConstants.DATA_SOURCES_KEY, dataSources);

		// do action when validation fails
		if (targetAction.equals("failure"))
		{
			// set the ouput data source selected by user in form and forward teh resuest to advanced search query page.
			advancedSearchActionForm.setOutputDataSources(createOutputDataSources(request));

			return mapping.findForward("failure");
		}
		// do action when user clicks on back to advanced search page from select path
		if (targetAction.equals("nothing"))
		{
			//nothing required to perform for this target action
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

		Map outputDataSources = (HashMap) advancedSearchActionForm.getOutputDataSources();
		/**
		 * Prepare Frequency Map where KEY = datasource name and value = predicate  
		 */
		if (outputDataSources.size() == 0)
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
						if (freqValue == null || freqValue.length() == 0)
						{
							freqValue = "0";
						}

						Float freq = new Float(freqValue);
						frequencyMap.put(bean.getName(), freq);
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

		//setting selected ONTs into formbean in case of User selected some ONTs 
		//on ONT page and calling search action from AdvancedSearch page 
		String selectedPaths = "";
		String selectedONTs = advancedSearchActionForm.getSelectedPaths();

		if (selectedONTs != null)
		{
			if (selectedONTs.length() > 0 & (!selectedONTs.endsWith("$")))
			{
				StringTokenizer ontTokenized = new StringTokenizer(selectedONTs, "#");

				while (ontTokenized.hasMoreTokens())
				{
					String ont = ontTokenized.nextToken();

					selectedPaths += ont.substring(ont.indexOf("=") + 1, ont.length()) + "$";
					Logger.out.debug("ONT Token--> "
							+ ont.substring(ont.indexOf("=") + 1, ont.length()));
				}

				Logger.out.debug("selectedONTs-->" + selectedPaths);
				advancedSearchActionForm.setSelectedPaths(selectedPaths);
			}
		}

		data.put(GCConstants.FORM, advancedSearchActionForm);

		inputData.setData(data);
		/**
		 * call bizliogc method to build domain objects and query the system
		 */
		ResultDataInterface resultData = advancedSearchBizLogic.getResult(inputData);

		/**
		 * Prepare Query list that os to be listed on search page in view display result combo box.
		 */
		Map allresultMap = resultData.getData();
		List queryKeyList = new ArrayList();
		Set keySet = allresultMap.keySet();
		List keyList = new ArrayList(keySet);

		// sort the keys as per input order 
		Utility.sortInputQueryKeys(keyList);
		NameValueBean allbean = new NameValueBean();
		allbean.setName(GCConstants.QUERY_KEY_ALL);
		allbean.setValue(GCConstants.QUERY_KEY_ALL);
		queryKeyList.add(allbean);
		for (Iterator iter = keyList.iterator(); iter.hasNext();)
		{
			String k = (String) iter.next();
			Logger.out.debug("Key :" + k);
			int ind = k.indexOf("_");
			if (ind > 0)
			{
				String displayKey = k.substring(ind + 1);
				NameValueBean bean = new NameValueBean();
				bean.setName(displayKey);
				bean.setValue(k);
				queryKeyList.add(bean);

			}
		}
		/**
		 * Set the result in session and forward to SearchResultView Action
		 */
		session.setAttribute(GCConstants.RESULT_DATA_LIST, resultData);
		session.setAttribute(GCConstants.QUERY_KEY_MAP, queryKeyList);
		request.setAttribute(Constants.PAGEOF, GCConstants.ADVANCED_SEARCH);

		return (mapping.findForward(GCConstants.FORWARD_TO_RESULT_PAGE));
	}

	/**
	 * this method populates map of Output Datasources selected by user on AdvancedSearch page
	 * The map contains data source name as key and frequency as value 
	 */
	private Map createOutputDataSources(HttpServletRequest request) throws Exception
	{
		//initializing list of datasources
		List dataSources = MetadataManager.getDataSourcesToDisplay();

		//initializing map for Output Datasources
		Map frequencyMap = new HashMap();

		//populating map of Output Datasources
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
					if (freqValue == null || freqValue.length() == 0)
					{
						freqValue = "0";
					}

					Float freq = new Float(freqValue);
					frequencyMap.put(bean.getName(), freq);
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

		//returning map of Output Datasources
		return frequencyMap;
	}

	/**
	 * This method updates map of formbean InputDatasources while user deletes any of the already entered InputDatasources 
	 * @param advancedSearchActionForm - formbean object to update
	 * @param request - request object associated with action
	 * @throws Exception
	 */
	private void updateMap(AdvancedSearchForm advancedSearchActionForm, HttpServletRequest request)
			throws Exception
	{

		//initializing list of datasources
		List dataSources = MetadataManager.getDataSourcesToDisplay();

		//initializing map of InputDatasources
		Map inputs = advancedSearchActionForm.getInputDataSources();

		Set inputKeys = inputs.keySet();

		List inputKeyList = new ArrayList(inputKeys);

		TreeMap sortedKeys = new TreeMap();

		//sorting InputDatasources
		for (int i = 0; i < inputKeys.size(); i++)
		{
			String inputKey = (String) inputKeyList.get(i);

			StringTokenizer inputKeyTokenized = new StringTokenizer(inputKey, "_");

			sortedKeys.put(inputKeyTokenized.nextToken(), inputKeyTokenized.nextToken());
		}

		Set sortedKeySet = sortedKeys.keySet();

		List sortedKeyList = new ArrayList(sortedKeySet);

		String rowsToDelete = request.getParameter("rowsToDelete");

		StringTokenizer rowsTokened = new StringTokenizer(rowsToDelete, ",");

		//traversing through sorted InputDatasources to delete Inputs selected by user to delete
		while (rowsTokened.hasMoreTokens())
		{
			String rowId = rowsTokened.nextToken();

			if ((sortedKeyList.size()) > new Integer(rowId).intValue())
			{
				String inputHeader = (String) sortedKeyList.get(new Integer(rowId).intValue());

				for (int i = 0; i < dataSources.size(); i++)
				{
					NameValueBean dataSource = (NameValueBean) dataSources.get(i);

					inputs.remove(inputHeader + "_" + dataSource.getName());

					Logger.out.debug("To Delete-->" + (inputHeader + dataSource.getName()));
				}
			}
		}

		//setting InputDatasources of formbean with updated Inputs
		advancedSearchActionForm.setInputDataSources(inputs);

		//setting updated formbean into request object
		request.setAttribute("advancedSearchForm", advancedSearchActionForm);

		//storing new instance of formbean into session as formbean values are changed
		HttpSession session = request.getSession();
		session.setAttribute("advancedSearchForm", new AdvancedSearchForm());

	}
}
