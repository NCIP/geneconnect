/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 * <p>Title: SpreadsheetExportAction Class>
 * <p>Description:	This class exports the data of a spreadsheet to a file.</p>
 * @author Aniruddha Phadnis
 * @version 1.00
 * Created on Oct 24, 2005
 */

package edu.wustl.geneconnect.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.util.ExportReport;
import edu.wustl.common.util.SendFile;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.global.Variables;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.geneconnect.util.global.GCConstants;

/**
 * @author aniruddha_phadnis
 */

public class SpreadsheetExportAction extends Action
{

	public SpreadsheetExportAction()
	{
		super();
		Logger.out.debug("In CONSTRY");
		// TODO Auto-generated constructor stub
	}

	/**
	 * This class exports the data of a spreadsheet to a file.
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException
	{
		//AdvanceSearchForm searchForm = (AdvanceSearchForm)form;
		String isConfidenceChecked = request.getParameter(GCConstants.CONFIDENCE);
		String isFrequencyCheched = request.getParameter(GCConstants.FREQUENCY);
		boolean exportConfidence = false;
		boolean exportfrequency  = false;
		if(isConfidenceChecked !=null&&isConfidenceChecked.equalsIgnoreCase("true"))
		{
			exportConfidence=true;
		}
		if(isFrequencyCheched !=null&&isFrequencyCheched.equalsIgnoreCase("true"))
		{
			exportfrequency=true;
		}
	
		HttpSession session = request.getSession();
		String fileName = Variables.applicationHome + System.getProperty("file.separator")
				+ session.getId() + ".csv";

		//Extracting map from formbean which gives the serial numbers of selected rows
		//Map map = searchForm.getValues();
		//Object [] obj = map.keySet().toArray();

		//Getting column data & grid data from session
		List columnList = (List) session.getAttribute(GCConstants.SPREADSHEET_COLUMN_LIST);
		List dataList = (List) session.getAttribute(GCConstants.SPREADSHEET_DATA_LIST);

		//Mandar 06-Apr-06 Bugid:1165 : Extra ID columns displayed.  start

		Logger.out
				.debug("---------------------------------------------------------------------------------");
		Logger.out.debug("06-apr-06 : cl size :-" + columnList.size());
		
		Logger.out.debug(columnList);
		Logger.out.debug("--");
		Logger.out.debug("06-apr-06 : dl size :-" + dataList.size());
		Logger.out.debug(dataList);

		List tmpColumnList = new ArrayList();
		int idColCount = 0;
		// count no. of ID columns
		for (int cnt = 0; cnt < columnList.size(); cnt++)
		{
			String columnName = (String) columnList.get(cnt);
			Logger.out.debug(columnName + " : " + columnName.length());
			if (columnName.trim().equalsIgnoreCase("ID"))
			{
				idColCount++;
			}
		}
		// remove ID columns
		for (int cnt = 0; cnt < (columnList.size() - idColCount); cnt++)
		{
			String colName = (String) columnList.get(cnt);
			if (!colName.equalsIgnoreCase(GCConstants.SET_ID_KEY)
					&& !colName.equalsIgnoreCase(GCConstants.QUERY_KEY))
			{
				boolean addtoList=true;
				if((colName.equalsIgnoreCase(GCConstants.CONF_SCORE_DISPLAY))&&(!exportConfidence))
				{
					addtoList=false;
				}
				else if((colName.indexOf(GCConstants.FREQUENCY_KEY_SUFFIX)>0)&&(!exportfrequency))
				{
					addtoList=false;
				}
				if(addtoList)
				{
					tmpColumnList.add(columnList.get(cnt));
				}
				
			}	
		}
		
		// datalist filtration for ID data.
		List tmpDataList = new ArrayList();
		for (int dataListCnt = 0; dataListCnt < dataList.size(); dataListCnt++)
		{
			Map tmpList = (Map) dataList.get(dataListCnt);
			List tmpNewList = new ArrayList();
			HashMap setMap = (HashMap) dataList.get(dataListCnt);
			List row = new ArrayList();

			for (int i = 0; i < tmpColumnList.size(); i++)
			{
				String colName = (String) tmpColumnList.get(i);
				if (!colName.equalsIgnoreCase(GCConstants.SET_ID_KEY)
						&& !colName.equalsIgnoreCase(GCConstants.QUERY_KEY))
					tmpNewList.add(setMap.get(colName));
			}
			tmpDataList.add(tmpNewList);
		}

		Logger.out.debug("--");
		Logger.out.debug("tmpcollist :" + tmpColumnList.size());
		Logger.out.debug(tmpColumnList);
		Logger.out.debug("--");
		Logger.out.debug("tmpdatalist :" + tmpDataList.size());
		Logger.out.debug(tmpDataList);

		Logger.out
				.debug("---------------------------------------------------------------------------------");
		columnList = tmpColumnList;
		dataList = tmpDataList;
		//    	Mandar 06-Apr-06 Bugid:1165 : Extra ID columns end  

		List exportList = new ArrayList();

		//Adding first row(column names) to exportData
		exportList.add(columnList);

		//Adding the selected rows to exportData
		for (int j = 0; j < tmpDataList.size(); j++)
		{

			exportList.add((List) tmpDataList.get(j));
		}
		String delimiter = Constants.DELIMETER;
		//Exporting the data to the given file & sending it to user
		ExportReport report = new ExportReport(fileName);
		report.writeData(exportList, delimiter);
		report.closeFile();
		Logger.out.debug(fileName);
		SendFile.sendFileToClient(response, fileName, GCConstants.SEARCH_RESULT,
				"application/download");
		String forwardTo;
		String pageOf = request.getParameter(Constants.PAGEOF);
		if (pageOf != null && pageOf.equalsIgnoreCase(GCConstants.ADVANCED_SEARCH))
		{
			forwardTo = GCConstants.FORWARD_TO_ADVANCED_SEARCH_RESULT_PAGE;
		}
		else
		{
			forwardTo = GCConstants.FORWARD_TO_SIMPLE_SEARCH_RESULT_PAGE;
		}
		return null;
	}
}