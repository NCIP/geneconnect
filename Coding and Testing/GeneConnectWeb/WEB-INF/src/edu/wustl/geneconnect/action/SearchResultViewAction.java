/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>ClassName: java edu.wustl.geneconnect.action.SimpleSearchAction</p> 
 */

package edu.wustl.geneconnect.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
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
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.geneconnect.actionForm.AdvancedSearchForm;
import edu.wustl.geneconnect.bizlogic.ResultDataInterface;
import edu.wustl.geneconnect.util.global.GCConstants;
import edu.wustl.geneconnect.util.global.Utility;

/**
 * Action class for simple search result. This class will work as for showing result 
 * and logic for pagenation.
 * 
 * @author sachin_lale
 * @version 1.0
 */
public class SearchResultViewAction extends Action
{

	/**
	 * Defalut Constructor
	 */
	public SearchResultViewAction()
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
		if(session.isNew())
		{
			request.setAttribute(GCConstants.SESSION_INVALIDATE,GCConstants.SESSION_INVALIDATE);
			return (mapping.findForward(GCConstants.FORWARD_TO_ERROR_PAGE));
		}
			
		String forwardTo = "";
		String pageOf = "";
		pageOf = request.getParameter(Constants.PAGEOF);
		String pagination = request.getParameter("isPaging");
		String isSorting = request.getParameter("isSorting");
		String isConfidenceChecked = request.getParameter(GCConstants.CONFIDENCE);

		String isFrequencyCheched = request.getParameter(GCConstants.FREQUENCY);

		// Get the value of sorted column and sorting order.
		String sortedColumn = request.getParameter(GCConstants.SORTED_COLUMN);
		String sortedColumnIndex = request.getParameter(GCConstants.SORTED_COLUMN_INDEX);
		String sortedColumnDirection = request.getParameter(GCConstants.SORTED_COLUMN_DIRECTION);

		// Strores query key selected  by user to display results  
		String queryKey = request.getParameter(GCConstants.QUERY_KEY);
		// stores list of data soruce not found seperated by ',' 
		StringBuffer noMatchFoundMessage = null;

		// if request to sotr the column
		if (sortedColumn != null && sortedColumnDirection != null && pagination.equals("true")
				&& isSorting != null)
		{
			List colList = (List) session.getAttribute(GCConstants.SPREADSHEET_COLUMN_LIST);
			if (colList.contains(sortedColumn))
			{
				List dslistToSort = (List) session.getAttribute(GCConstants.SPREADSHEET_DATA_LIST);
				Utility.sortDataList(dslistToSort, sortedColumn, sortedColumnDirection);
				session.setAttribute(GCConstants.SPREADSHEET_DATA_LIST, dslistToSort);
			}
		}
		/**
		 * If the action is forwarded  from SimpleSearchAction class 
		 */
		if (pagination == null || pagination.equals("false"))
		{

			// get Resultdata and store it in session for pagenation
			ResultDataInterface resultData = (ResultDataInterface) session
					.getAttribute(GCConstants.RESULT_DATA_LIST);

			Logger.out.debug("pageOf " + pageOf);
			if (resultData != null || pageOf != null)
			{
				List columnList = null;
				List dataList = null;
				Set columnHeaderRemoved = null;
				/**
				 * IF request from advanced search
				 */
				if (request.getAttribute(Constants.PAGEOF) != null)
				{
					pageOf = (String) request.getAttribute(Constants.PAGEOF);
				}
				else if (request.getParameter(Constants.PAGEOF) != null)
				{
					pageOf = request.getParameter(Constants.PAGEOF);
				}

				if (pageOf != null && pageOf.equalsIgnoreCase(GCConstants.ADVANCED_SEARCH))
				{
					Logger.out.info("getting data and column from resultdata of advanced search");
					queryKey = request.getParameter(GCConstants.QUERY_KEY);

					Map allresultMap = resultData.getData();

					/**
					 * If the selected query is null i.e. if it is first time display then selecet first query  default 'All' 
					 */
					if (queryKey == null)
					{

						Logger.out.info("Seleting query for advance result page");

						List queryList = (List) session.getAttribute(GCConstants.QUERY_KEY_MAP);
						if (queryList.size() > 0)
						{
							NameValueBean bean = (NameValueBean) queryList.get(0);
							queryKey = bean.getValue();
						}
					}
					Logger.out.info("Seleted query for advance result page : " + queryKey);
					Set keySet = allresultMap.keySet();
					for (Iterator iter = keySet.iterator(); iter.hasNext();)
					{
						String s = (String) iter.next();
					}
					/**
					 * If all to display result of all query
					 * merge the column list and data list of all query
					 */
					if (queryKey.equalsIgnoreCase(GCConstants.QUERY_KEY_ALL))
					{
						List queryList = (List) session.getAttribute(GCConstants.QUERY_KEY_MAP);
						List tempColumn = new ArrayList();
						Set tempColumnRemoved = new HashSet();
						List tempData = new ArrayList();
						columnList = new ArrayList();
						dataList = new ArrayList();
						columnHeaderRemoved = new HashSet();
						for (int i = 1; i < queryList.size(); i++)
						{
							NameValueBean bean = (NameValueBean) queryList.get(i);
							Map queryresultMap = (Map) allresultMap.get(bean.getValue());
							tempColumn = (List) queryresultMap.get(GCConstants.COLUMN_HEADERS);
							columnList.addAll(tempColumn);
							// get the column header removed as part of no match found 
							tempColumnRemoved = (Set) queryresultMap
									.get(GCConstants.COLUMN_HEADERS_REMOVED);
							if (tempColumnRemoved != null)
							{
								columnHeaderRemoved.addAll(tempColumnRemoved);

							}
							tempData = (List) queryresultMap.get(GCConstants.RESULT_LIST);
							dataList.addAll(tempData);
						}
						// remove redundant column list
						List temp = new ArrayList();
						for (Iterator i = columnList.iterator(); i.hasNext();)
						{
							String col = (String) i.next();
							if (temp.contains(col))
							{
								i.remove();
							}
							else
							{
								temp.add(col);
							}

						}
					}
					else
					{
						/**
						 * if to display result selected query
						 */

						Map queryresultMap = (Map) allresultMap.get(queryKey);
						// get list of columns to display
						columnList = (List) queryresultMap.get(GCConstants.COLUMN_HEADERS);

						//get the column header removed as part of no match found 
						columnHeaderRemoved = (Set) queryresultMap
								.get(GCConstants.COLUMN_HEADERS_REMOVED);
						// get data list
						dataList = (List) queryresultMap.get(GCConstants.RESULT_LIST);
						// get result list of GenomicIdentifierSet object
						List resultList = (List) queryresultMap
								.get(GCConstants.GENOMICIDENTIIER_SET_RESULT_LIST);
						session.setAttribute(GCConstants.GENOMICIDENTIIER_SET_RESULT_LIST,
								resultList);
					}
					session.setAttribute("advancedSearchForm", new AdvancedSearchForm());
				}
				else
				{
					/**
					 * request from simple search
					 */
					Logger.out.info("getting data and column from resultdata of simple search");
					columnList = (List) resultData.getValue(GCConstants.COLUMN_HEADERS);
					dataList = (List) resultData.getValue(GCConstants.RESULT_LIST);
				}
				/**
				 * prepare list of data source as a part of no match found message
				 */

				if (columnHeaderRemoved != null && columnHeaderRemoved.size() > 0)
				{
					noMatchFoundMessage = new StringBuffer();
					int i = 0;

					for (Iterator it = columnHeaderRemoved.iterator(); it.hasNext();)
					{
						String val = (String) it.next();
						if (noMatchFoundMessage.indexOf(val) < 0)
						{
							if (it.hasNext())
							{
								noMatchFoundMessage.append(val + ",");
							}
							else
							{
								noMatchFoundMessage.append(val);
							}

						}

					}
					Logger.out.info("noMatchFoundMessage :" + noMatchFoundMessage);
				}
				session.setAttribute(GCConstants.NO_MATCH_FOUND_MESSAGE, noMatchFoundMessage);
				/**Sorting columns*/
				Collections.sort(columnList);
				/**remove Confidence score and SET_ID couln header and ad it to end of sorted list*/
				for (java.util.Iterator iter = columnList.iterator(); iter.hasNext();)
				{
					String colValue = (String) iter.next();
					if ((colValue.endsWith(GCConstants.CONF_SCORE_KEY))
							|| (colValue.endsWith(GCConstants.SET_ID_KEY)))
					{
						iter.remove();

					}
				}
				columnList.add(GCConstants.CONF_SCORE_KEY);
				columnList.add(GCConstants.SET_ID_KEY);
				session.setAttribute(GCConstants.SPREADSHEET_COLUMN_LIST, columnList);
				session.setAttribute(GCConstants.SPREADSHEET_DATA_LIST, dataList);
				/**
				 * If the column is sorted on previous selected query tehn sort agin on same column if exists on
				 * current selected query
				 */
				if (sortedColumn != null && sortedColumn.length() > 0
						&& sortedColumnDirection != null)
				{
					if (columnList.contains(sortedColumn))
					{
						sortedColumnIndex = "" + columnList.indexOf(sortedColumn);
						List dslistToSort = (List) session
								.getAttribute(GCConstants.SPREADSHEET_DATA_LIST);
						Utility.sortDataList(dslistToSort, sortedColumn, sortedColumnDirection);
						session.setAttribute(GCConstants.SPREADSHEET_DATA_LIST, dslistToSort);
					}
					else
					{
						sortedColumn = null;
						sortedColumnDirection = null;
					}
				}
			}
		}
		if (pageOf != null && pageOf.equalsIgnoreCase(GCConstants.ADVANCED_SEARCH))
		{

			forwardTo = GCConstants.FORWARD_TO_ADVANCED_SEARCH_RESULT_PAGE;
		}
		else
		{
			session.setAttribute(GCConstants.QUERY_KEY_MAP, null);
			forwardTo = GCConstants.FORWARD_TO_SIMPLE_SEARCH_RESULT_PAGE;
		}
		Logger.out.info("forwardTo " + forwardTo);
		Logger.out.debug("forwardTo " + forwardTo);
		int pageNum = GCConstants.START_PAGE;
		List paginationDataList = null, dataList = null, columnList = null;

		//Get the SPREADSHEET_DATA_LIST and SPREADSHEET_COLUMN_LIST from the session.
		dataList = (List) session.getAttribute(GCConstants.SPREADSHEET_DATA_LIST);
		columnList = (List) session.getAttribute(GCConstants.SPREADSHEET_COLUMN_LIST);

		if (request.getParameter(GCConstants.PAGE_NUMBER) != null)
		{
			pageNum = Integer.parseInt(request.getParameter(GCConstants.PAGE_NUMBER));
			Logger.out.debug("IN pageNum action " + pageNum);
		}

		if (dataList != null)
		{
			//Set the start index of the list.
			int startIndex = (pageNum - 1) * GCConstants.NUMBER_RESULTS_PER_PAGE_SEARCH;
			//Set the end index of the list.
			int endIndex = startIndex + GCConstants.NUMBER_RESULTS_PER_PAGE_SEARCH;
			if (endIndex > dataList.size())
			{
				endIndex = dataList.size();
			}
			//Get the paginationDataList from startIndex to endIndex of the dataList.
			paginationDataList = dataList.subList(startIndex, endIndex);

			//Set the total no of records in the request object to be used by pagination tag.
			request.setAttribute(GCConstants.TOTAL_RESULTS, Integer.toString(dataList.size()));
		}
		else
		{
			//Set the total no of records in the request object to be used by pagination tag.
			request.setAttribute(GCConstants.TOTAL_RESULTS, Integer.toString(0));
		}

		//Set the paginationDataList in the request to be shown by grid control.
		request.setAttribute(GCConstants.PAGINATION_DATA_LIST, paginationDataList);

		//Set the columnList in the request to be shown by grid control.
		request.setAttribute(GCConstants.SPREADSHEET_COLUMN_LIST, columnList);

		//Set the current pageNum in the request to be uesd by pagination Tag.
		request.setAttribute(GCConstants.PAGE_NUMBER, Integer.toString(pageNum));

		//Set the result per page attribute in the request to be uesd by pagination Tag.
		request.setAttribute(GCConstants.RESULTS_PER_PAGE, Integer
				.toString(GCConstants.NUMBER_RESULTS_PER_PAGE_SEARCH));
		/**
		 * store user selection attibutes in request so that it can be persit on page change.
		 */
		request.setAttribute(GCConstants.CONFIDENCE, isConfidenceChecked);
		request.setAttribute(GCConstants.FREQUENCY, isFrequencyCheched);
		request.setAttribute(GCConstants.SORTED_COLUMN, sortedColumn);
		request.setAttribute(GCConstants.SORTED_COLUMN_INDEX, sortedColumnIndex);
		request.setAttribute(GCConstants.SORTED_COLUMN_DIRECTION, sortedColumnDirection);
		request.setAttribute(GCConstants.SELECTED_QUERY, queryKey);
		return (mapping.findForward(forwardTo));
	}

	public static void main(String a[])
	{
		String s = "<DIV>Ens tartn</DIV>";
		System.out.println(s.substring(s.indexOf(">") + 1, s.indexOf("</")));
	}

}