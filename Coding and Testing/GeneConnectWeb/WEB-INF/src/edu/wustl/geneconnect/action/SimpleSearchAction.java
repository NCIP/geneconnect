/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.action.SimpleSearchAction</p> 
 */

package edu.wustl.geneconnect.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import edu.wustl.common.util.dbManager.DAOException;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.geneconnect.actionForm.AdvancedSearchForm;
import edu.wustl.geneconnect.actionForm.SimpleSearchForm;
import edu.wustl.geneconnect.bizlogic.BizLogicInterface;
import edu.wustl.geneconnect.bizlogic.GeneConnectBizLogicFactory;
import edu.wustl.geneconnect.bizlogic.InputData;
import edu.wustl.geneconnect.bizlogic.InputDataInterface;
import edu.wustl.geneconnect.bizlogic.ResultDataInterface;
import edu.wustl.geneconnect.metadata.MetadataManager;
import edu.wustl.geneconnect.util.global.GCConstants;
import edu.wustl.geneconnect.util.global.Utility;

/**
 * Action class for simple search operations. This class will work as redirector and will 
 * invoke appropriate business logics for simple search.
 * @author mahesh_nalkande
 * @version 1.0
 */
public class SimpleSearchAction extends Action
{

	/**
	 * Defalut Constructor
	 */
	public SimpleSearchAction()
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

		try
		{

			SimpleSearchForm simpleSearchActionForm = (SimpleSearchForm) form;

			//Obtain target action which needs to be performed
			String targetAction = simpleSearchActionForm.getTargetAction();
			Logger.out.info("targetAction is :" + targetAction);

			if (targetAction.equals(GCConstants.TARGET_ACTION_SEARCH))
			{
				if (simpleSearchActionForm.getQueryType().equalsIgnoreCase(
						GCConstants.SINGLE_QUERY_VALUE))
				{
					//Single Search operation.
					return executeDisplayResults(mapping, form, request, response);
				}
				else if (simpleSearchActionForm.getQueryType().equalsIgnoreCase(
						GCConstants.BACTH_QUERY_VALUE))
				{
					// if batch search forward to advavanced bizlogic
					return forwardTtoAdvance(mapping, form, request, response);
				}
			}

		}
		catch (BizLogicException bizExp)
		{
			Logger.out.error(bizExp.getMessage(), bizExp);
			ActionErrors errors = new ActionErrors();
			ActionError error = new ActionError("errors.item", bizExp.getMessage());
			errors.add(ActionErrors.GLOBAL_ERROR, error);
			saveErrors(request, errors);
			//target = new String(Constants.FAILURE);
		}
		catch (DAOException daoExp)
		{
			Logger.out.error(daoExp.getMessage(), daoExp);
			ActionErrors errors = new ActionErrors();
			ActionError error = new ActionError("errors.item", daoExp.getMessage());
			errors.add(ActionErrors.GLOBAL_ERROR, error);
			saveErrors(request, errors);
		}
		List dataSources = MetadataManager.getDataSourcesToDisplay();

		Logger.out.info("Setting data source list in request object");
		request.setAttribute(GCConstants.DATA_SOURCES_KEY, dataSources);
		request.setAttribute("queryType", "Single");
		Logger.out.info("Forwarding to SimpleSearch.jsp");
		return (mapping.findForward(GCConstants.FORWARD_TO_SIMPLE_PAGE));
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
		BizLogicInterface simpleSearchBizLogic = geneConnectBizLogicFactory
				.getBizLogic(GCConstants.SIMPLE_SEARCH_BIZLOGIC);
		//Form the input data which needs to be passed to the business logic
		InputDataInterface inputData = new InputData();
		Map data = new HashMap();
		data.put("Form", form);
		inputData.setData(data);

		//Apply business logic and Get result 
		Logger.out.info("call to simpleSearchBizLogic.getResult");
		ResultDataInterface resultData = simpleSearchBizLogic.getResult(inputData);
		List selectInputOutputList = new ArrayList();
		//List columnHeaders = resultData.getColumnHeader();
		List columnHeaders = (List) resultData.getValue(GCConstants.COLUMN_HEADERS);
		for (Iterator iter = columnHeaders.iterator(); iter.hasNext();)
		{
			String colName = (String) iter.next();
			if ((!colName.endsWith(GCConstants.FREQUENCY_KEY_SUFFIX))
					&& (!colName.endsWith(GCConstants.CONF_SCORE_KEY))
					&& (!colName.endsWith(GCConstants.SET_ID_KEY)))
			{
				selectInputOutputList.add(colName);
			}
		}
		session.setAttribute(GCConstants.SELECTED_DATASOURCES, selectInputOutputList);
		session.setAttribute(GCConstants.RESULT_DATA_LIST, resultData);
		List resultList = (List) resultData.getValue(GCConstants.GENOMICIDENTIIER_SET_RESULT_LIST);
		session.setAttribute(GCConstants.GENOMICIDENTIIER_SET_RESULT_LIST, resultList);
		Logger.out.info("Forwarding to SimpleResult page");

		// forward to SimpleSearch result page
		return (mapping.findForward(GCConstants.FORWARD_TO_RESULT_PAGE));
	}

	/**
	 * This method prepares teh advancedsearch form from simplesearchform and invoke advancedsearchbizlogic
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	private ActionForward forwardTtoAdvance(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		HttpSession session = request.getSession();
		//Get the instance of business logic factory
		GeneConnectBizLogicFactory geneConnectBizLogicFactory = GeneConnectBizLogicFactory
				.getInstance();
		//Get the instance of required business logic 
		BizLogicInterface advancedSearchBizLogic = geneConnectBizLogicFactory
				.getBizLogic(GCConstants.ADVANCED_SEARCH_BIZLOGIC);

		SimpleSearchForm simpleSearchActionForm = (SimpleSearchForm) form;
		AdvancedSearchForm advancedForm = new AdvancedSearchForm();
		// stores selected input data source list
		List inputList = simpleSearchActionForm.getInputDsList();
		// stores selected output data source list
		List outputList = simpleSearchActionForm.getOutputDsList();

		// input and outout data source mao to store in AdvancedSearch form
		Map inputDataSource = new HashMap();
		Map outputDataSource = new HashMap();
		for (int i = 0; i < inputList.size(); i++)
		{
			NameValueBean bean = (NameValueBean) inputList.get(i);
			String key = "Input:" + (i + 1) + "_" + bean.getName();
			inputDataSource.put(key, bean.getValue());
		}
		for (int i = 0; i < outputList.size(); i++)
		{
			String key = outputList.get(i) + GCConstants.ADVANCED_FORM_OUTPUTDS_SUFIX_KEY;
			outputDataSource.put((String) outputList.get(i), new Float("0"));
		}
		// store the input ans output data sources
		advancedForm.setInputDataSources(inputDataSource);
		advancedForm.setOutputDataSources(outputDataSource);
		InputDataInterface inputData = new InputData();
		Map data = new HashMap();

		data.put(GCConstants.FORM, advancedForm);

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

		// sort the query list as per the input order to display in combo box on result page 
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
}