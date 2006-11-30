/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.action.SimpleSearchAction</p> 
 */

package edu.wustl.geneconnect.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.geneconnect.actionForm.AdvancedSearchForm;
import edu.wustl.geneconnect.bizlogic.ResultDataInterface;
import edu.wustl.geneconnect.util.global.GCConstants;

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
		String forwardTo ="";
		String pageOf="";
		pageOf =request.getParameter(Constants.PAGEOF);
		String pagination = request.getParameter("isPaging");
		String isConfidenceChecked = request.getParameter(GCConstants.CONFIDENCE);
		
		String isFrequencyCheched = request.getParameter(GCConstants.FREQUENCY);
		String sortedColumn = request.getParameter(GCConstants.SORTED_COLUMN);
		String sortedColumnDirection = request.getParameter(GCConstants.SORTED_COLUMN_DIRECTION);
		String queryKey = request.getParameter(GCConstants.QUERY_KEY);
		
		
		/**
		 * If the action is forwarded  from SimpleSearchAction class 
		 */
		if (pagination == null || pagination.equals("false"))
		{
			// get Resultdata and store it in session for pagenation
//			ResultDataInterface resultData = (ResultDataInterface) request
//					.getAttribute(GCConstants.RESULT_DATA_LIST);
			ResultDataInterface resultData = (ResultDataInterface) session
			.getAttribute(GCConstants.RESULT_DATA_LIST);
			
			System.out.println("pageOf " +pageOf);
			if (resultData != null||pageOf!=null)
			{
				//List columnList = resultData.getColumnHeader();

				//List dataList = resultData.getResult();
				List columnList=null;
				List dataList = null;
				/**
				 * IF request from advanced search
				 */
				if(request.getAttribute(Constants.PAGEOF)!=null)
				{
					pageOf = (String)request.getAttribute(Constants.PAGEOF);
				}
				else if(request.getParameter(Constants.PAGEOF)!=null)
				{
					pageOf =request.getParameter(Constants.PAGEOF);
				}
				
				if(pageOf!=null&&pageOf.equalsIgnoreCase(GCConstants.ADVANCED_SEARCH))
				{
					Logger.out.info("getting data and column from resultdata of advanced search");
					queryKey = request.getParameter(GCConstants.QUERY_KEY);
					
						Map allresultMap = resultData.getData();
						/**
						 * If the selected query is null i.e. if it is first time display then selecet first query   
						 */
						if(queryKey==null)
						{
							
							Logger.out.info("Seleting query for advance result page");
					
							List queryList = (List)session.getAttribute(GCConstants.QUERY_KEY_MAP);
							if(queryList.size()>0)
							{
								NameValueBean bean = (NameValueBean)queryList.get(0);
								queryKey = bean.getValue();
								
							//	System.out.println("Break : " +queryKey);
							}
						}
						Logger.out.info("Seleted query for advance result page : "+queryKey);
						Set keySet = allresultMap.keySet();
						for(Iterator iter=keySet.iterator();iter.hasNext();)
						{
							String s = (String)iter.next();
						}

						Map queryresultMap = (Map)allresultMap.get(queryKey);
						columnList = (List)queryresultMap.get(GCConstants.COLUMN_HEADERS);
						List selectInputOutputList= new ArrayList();
						List columnHeaders = columnList;
//						for(Iterator iter=columnHeaders.iterator();iter.hasNext();)
//						{
//							String colName = (String)iter.next();
//							if ((!colName.endsWith(GCConstants.FREQUENCY_KEY_SUFFIX))
//									&& (!colName.endsWith(GCConstants.CONF_SCORE_KEY))
//									&& (!colName.endsWith(GCConstants.SET_ID_KEY)))
//							{
//								selectInputOutputList.add(colName);
//							}
//						}
//						session.setAttribute(GCConstants.SELECTED_DATASOURCES,selectInputOutputList);
						
						// set result list on session so taht it can be used for pagenation and is user selects differnt query to view 
						dataList = (List)queryresultMap.get(GCConstants.RESULT_LIST);
						List resultList = (List)queryresultMap.get(GCConstants.GENOMICIDENTIIER_SET_RESULT_LIST);
						session.setAttribute(GCConstants.GENOMICIDENTIIER_SET_RESULT_LIST,resultList);
						session.setAttribute("advancedSearchForm", new AdvancedSearchForm());
				}
				else
				{	
					/**
					 * request from simple search
					 */
					Logger.out.info("getting data and column from resultdata of simple search");
					columnList = (List)resultData.getValue(GCConstants.COLUMN_HEADERS);
					dataList = (List)resultData.getValue(GCConstants.RESULT_LIST);
					//System.out.println("SIMPLE: " +columnList);
					//System.out.println("SIMPLE: " +dataList);
				}	
				
				session.setAttribute(GCConstants.SPREADSHEET_COLUMN_LIST, columnList);
				session.setAttribute(GCConstants.SPREADSHEET_DATA_LIST, dataList);
			}
		}
		if(pageOf!=null&&pageOf.equalsIgnoreCase(GCConstants.ADVANCED_SEARCH))
		{
			
			forwardTo=GCConstants.FORWARD_TO_ADVANCED_SEARCH_RESULT_PAGE;
		}
		else
		{
			session.setAttribute(GCConstants.QUERY_KEY_MAP,null);
			forwardTo=GCConstants.FORWARD_TO_SIMPLE_SEARCH_RESULT_PAGE;
		}
		Logger.out.info("forwardTo " +forwardTo);
		System.out.println("forwardTo " +forwardTo);
		int pageNum = GCConstants.START_PAGE;
		List paginationDataList = null, dataList = null, columnList = null;

		//Get the SPREADSHEET_DATA_LIST and SPREADSHEET_COLUMN_LIST from the session.
		dataList = (List) session.getAttribute(GCConstants.SPREADSHEET_DATA_LIST);
		columnList = (List) session.getAttribute(GCConstants.SPREADSHEET_COLUMN_LIST);

		if (request.getParameter(GCConstants.PAGE_NUMBER) != null)
		{
			pageNum = Integer.parseInt(request.getParameter(GCConstants.PAGE_NUMBER));
			System.out.println("IN pageNum action " + pageNum);
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
		request.setAttribute(GCConstants.CONFIDENCE,isConfidenceChecked);
		request.setAttribute(GCConstants.FREQUENCY,isFrequencyCheched);
		request.setAttribute(GCConstants.SORTED_COLUMN,sortedColumn);
		request.setAttribute(GCConstants.SORTED_COLUMN_DIRECTION,sortedColumnDirection);
		request.setAttribute(GCConstants.SELECTED_QUERY,queryKey);
		return (mapping.findForward(forwardTo));
	}
}