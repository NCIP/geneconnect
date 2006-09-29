/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.action.SimpleSearchAction</p> 
 */

package edu.wustl.geneconnect.action;

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

import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.dbManager.DAOException;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.geneconnect.actionForm.SimpleSearchForm;
import edu.wustl.geneconnect.bizlogic.BizLogicInterface;
import edu.wustl.geneconnect.bizlogic.GeneConnectBizLogicFactory;
import edu.wustl.geneconnect.bizlogic.InputData;
import edu.wustl.geneconnect.bizlogic.InputDataInterface;
import edu.wustl.geneconnect.bizlogic.ResultDataInterface;
import edu.wustl.geneconnect.metadata.MetadataManager;
import edu.wustl.geneconnect.metadata.MetadataManagerInterface;
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
		String pagination = request.getParameter("isPaging");
		/**
		 * If the action is forwarded  from SimpleSearchAction class 
		 */
		if (pagination == null || pagination.equals("false"))
		{
			// get Resultdata and store it in session for pagenation
			ResultDataInterface resultData = (ResultDataInterface) request
					.getAttribute(GCConstants.RESULT_DATA_LIST);

			if (resultData != null)
			{
				List columnList = resultData.getColumnHeader();

				List dataList = resultData.getResult();
				session.setAttribute(GCConstants.SPREADSHEET_COLUMN_LIST, columnList);
				session.setAttribute(GCConstants.SPREADSHEET_DATA_LIST, dataList);
			}
		}

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

		return (mapping.findForward(GCConstants.FORWARD_TO_RESULT_PAGE));
	}
}