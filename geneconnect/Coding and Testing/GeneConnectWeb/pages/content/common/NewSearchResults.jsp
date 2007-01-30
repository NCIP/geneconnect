<!-- GeneConnect Simple Search Result page -->

<!-- TagLibs -->
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/core-jstl" prefix="c"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/PagenationTag.tld" prefix="custom"%>
<!-- Taglibs -->

<!-- Imports -->
<%@
      page language="java" contentType="text/html"
	import="java.util.*"
	import="edu.wustl.geneconnect.actionForm.SimpleSearchForm"
	import="edu.wustl.geneconnect.util.DisplayInformationInterface"
	import="edu.wustl.common.beans.NameValueBean"
	import="edu.wustl.geneconnect.util.global.Utility"
	import="edu.wustl.geneconnect.util.global.GCConstants"
	import="edu.wustl.common.util.global.Constants"
	import="edu.wustl.geneconnect.bizlogic.ResultDataInterface"%>



<script src="jss/script.js"></script>
<head>
<!-- Css and Scripts -->
<%			boolean conf = false;
			boolean freq = false;
			boolean val = false;

%>

<script language="JavaScript" type="text/javascript"
	src="jss/javaScript.js"></script>

<script language="javascript">
		var colZeroDir='ascending';
		var selectedQuery="";
		
		// export the result as CSV		
		function onExport()
		{
			var action = "SpreadsheetExport.do";
			//document.forms[0].operation.value="export";
			document.forms[0].action = action;
			//document.forms[0].target = "_blank";
			document.forms[0].submit();
			
		}
	
		var selected;

		function addCheckBoxValuesToArray(checkBoxName)
		{
			var theForm = document.forms[0];
		    selected=new Array();
		
		    for(var i=0,j=0;i<theForm.elements.length;i++)
		    {
		 	  	if(theForm[i].type == 'checkbox' && theForm[i].checked==true)
			        selected[j++]=theForm[i].value;
			}
		}
		
		
		function checkAll(element)
		{			
			checkUncheck(element);
		
		}
		
		
		function callAction(action)
		{
			document.forms[0].action = action;
			document.forms[0].submit();
		}
		
		// Function for hiding freq and /or confidence columns
		var firsttime=0;
		function showHideFreq(checkbox)
		{
			if(firsttime==0)
			{
				//alert("11");
				for(var ind=0;ind<freqcol.length;ind++)
				{
						var colind = parseInt(freqcol[ind]);
						//alert(mygrid.getColWidth(colind));
						if(mygrid.getColWidth(colind)==0)
						{
						//	alert(colind);
							mygrid.setColWidth(colind ,250);
							mygrid.setColumnHidden(colind ,true);
							mygrid.setSizes();
						}	
				}
				for(var ind=0;ind<confcol.length;ind++)
				{
					var colind = parseInt(confcol[ind]);
					//alert(mygrid.getColWidth(colind));
						if(mygrid.getColWidth(colind)==0)
						{
						//	alert(colind);
							mygrid.setColWidth(colind ,250);
							mygrid.setColumnHidden(colind ,true);
						}	
				}
			}
			firsttime++;			
			var elements;
			elements=document.getElementsByName("checkFreq");	
			for(var i=0;i<elements.length;i++)
			{
				var checkbox = elements[i];
				//alert(checkbox.name+"---"+checkbox.checked);
				for(var ind=0;ind<freqcol.length;ind++)
				{
					//alert("freqcol--"+freqcol[ind]);
					mygrid.setColumnHidden(freqcol[ind],!(checkbox.checked));
				}
				isCheckBoxChecked(checkbox);
			}
			elements=document.getElementsByName("checkConfScore");	
			for(var i=0;i<elements.length;i++)
			{
				var checkbox = elements[i];
				//alert(checkbox.name+"---"+checkbox.checked);
				for(var ind=0;ind<confcol.length;ind++)
				{
					//alert(checkbox.checked);
					mygrid.setColumnHidden(confcol[ind],!(checkbox.checked));
				}
				isCheckBoxChecked(checkbox);
			}	
			
			mygrid.setSizes();
		//	obj.refresh();
		}

		function onQuerySelect(combo)
		{
			//alert(combo.value);
			if(combo.value!="-1")
			{
				var text = combo.value;
				//selectedQuery=text+" ";
				document.forms[0].queryKey.value=text;
				var ind = text.indexOf("_",0)
				var disptext = text.substr(ind+1,text.length);
				//alert(disptext);
				var textarea = document.getElementById("selectedQuery");
				textarea.value=disptext;
			}	
			
		}
		// send a request to server to display the result fo selected query
		function doGo()
		{
			//if(selectedQuery.length>0)
			if(document.forms[0].selectedQuery.value.length>0)
			{
				var action = "SearchResultView.do?pageOf=advancedSearch&isPaging=false";
				//&queryKey="+selectedQuery
				//document.forms[0].operation.value="export";
				document.forms[0].action = action;
				//document.forms[0].target = "_blank";
				document.forms[0].submit();
			}
			else
			{
				
			}
		}
		function isCheckBoxChecked(checkBox)
		{
			var val = "false";
			if(checkBox.checked==true)
			{
				val="true";
			}
			if(checkBox.name=="checkConfScore")
			{
				document.forms[0].confidenceScore.value = val;
			}
			else if(checkBox.name=="checkFreq") 
			{
				document.forms[0].frequency.value = val;
			}
		}
		// funcrion for pagination
		function send(pageNum,numresultsPerPage,prevPage,pageName) 
		{
			//alert(document.forms[0].queryKey.value);
			document.forms[0].action = pageName+'?pageNum='+pageNum;
			document.forms[0].submit();
		}
		
		
	</script>

<%			
	String pageName = "SearchResultView.do";
	int queryKeyCol = 0;
	StringBuffer noMatchFoundMessage = (StringBuffer) session
			.getAttribute(GCConstants.NO_MATCH_FOUND_MESSAGE);
	int pageNum = Integer.parseInt((String) request.getAttribute(GCConstants.PAGE_NUMBER));
	int totalResults = Integer.parseInt((String) request
			.getAttribute(GCConstants.TOTAL_RESULTS));
	int numResultsPerPage = Integer.parseInt((String) request
			.getAttribute(GCConstants.RESULTS_PER_PAGE));
	// attibutes such as confidence ,frequecy checkbox chekced by  user on previous page persisted 
	// even after page change
	String isConfidenceChecked = (String) request.getAttribute(GCConstants.CONFIDENCE);
	String isFrequencyChecked = (String) request.getAttribute(GCConstants.FREQUENCY);
	String sortedColumn = (String) request.getAttribute(GCConstants.SORTED_COLUMN);
	String sortedColumnIndex = (String) request
			.getAttribute(GCConstants.SORTED_COLUMN_INDEX);
	String sortedColumnDirection = (String) request
			.getAttribute(GCConstants.SORTED_COLUMN_DIRECTION);

	/**Column headers to display*/
	List columnList = (List) request.getAttribute(GCConstants.SPREADSHEET_COLUMN_LIST);

	/**Query keys to select */
	List queryList = (List) session.getAttribute(GCConstants.QUERY_KEY_MAP);

	/// selected query fro advanced search
	String selectedQueryKey = (String) request.getAttribute(GCConstants.SELECTED_QUERY);
	/**Datalist containing map where MAP contains column header as key and genoimicIde as value */
	/**One eleemtn in data list contains map for one set*/
	boolean disableExportButton = false;
	List dataList = (List) request.getAttribute(GCConstants.PAGINATION_DATA_LIST);
	if (dataList.size() == 0)
	{

		disableExportButton = true;
	}

	List freqColumns = new ArrayList();
	List genomicColumns = new ArrayList();
	List confColumns = new ArrayList();
	List otherColumns = new ArrayList();
	int columnForSetId = 0;

	/**Get Freq,confidence and setid column to bui;ld the css for hiding / unhiding*/
	for (int i = 0; i < columnList.size(); i++)
	{

		String colValue = (String) columnList.get(i);
		if (colValue.endsWith(GCConstants.QUERY_KEY))
		{
			queryKeyCol = i;
		}
		else if ((!colValue.endsWith(GCConstants.FREQUENCY_KEY_SUFFIX))
				&& (!colValue.endsWith(GCConstants.CONF_SCORE_KEY))
				&& (!colValue.endsWith(GCConstants.SET_ID_KEY)))
		{
			genomicColumns.add("" + (i));

		}
		else if (colValue.endsWith(GCConstants.FREQUENCY_KEY_SUFFIX))
		{
			freqColumns.add("" + (i));
		}
		else if (colValue.endsWith(GCConstants.CONF_SCORE_KEY))
		{

			confColumns.add("" + (i));
		}
		else
		{
			otherColumns.add("" + (i));
			if (colValue.endsWith(GCConstants.SET_ID_KEY))
			{
				columnForSetId = i;
			}
		}
	}
	boolean isSpecimenData = false;
	int IDCount = 0;
%>



<!-- Column number to display path icon-->
<%int imgCol = ((columnList.size() - IDCount));%>

<style>
	div.hdr{
		background-color:lightgrey;
		margin-bottom:10px;
		padding-left:10px;
		font-family:arial;
		font-size:12px
	}
</style>

<!-- Construc css for hide / unhide frequency and /or confidenc column -->
<style type="text/css">
tr#hiddenCombo
{
 display:none;
}

</style>
</head>

<table summary="" cellpadding="0" cellspacing="0" border="0"
	width="100%" height="30">
	<tr>
		<td class="formTitle" width="100%"><bean:message
			key="simpleSearch.result.title" /></td>
	</tr>
</table>

<table summary="" cellpadding="0" cellspacing="0" border="0"
	width="100%" height="90%">
	<html:form action="/SearchResultView.do">

		<!-- && pageOf.equals(Constants.PAGEOF_QUERY_RESULTS) -->
		<!-- If no data then display apropriate message -->
		<%if (noMatchFoundMessage != null)
			{

				%>
		<li><font color="red"><bean:message key="result.noMatchFound.message" /><%=": " + noMatchFoundMessage%></font></li>
		<%}

			%>

		<%if (dataList != null)
			{

				%>
		<tr height="5%">
			<td width="100%"><br>
			</td>
		</tr>

		<%// if the result is for advanced search show query selection combo box 
				if (queryList != null)
				{

					%>
		<tr>
			<td class="dataPagingSection">
			<table summary="" cellpadding="2" cellspacing="0" border="0"
				width="100%" height="90%">

				<tr>
					<td class="formLabelBold" width="110"><bean:message
						key="buttons.selectQuery" /></td>
					<td class="formLabelBold" width="20"><select
						class="formDropDownSized" id="queryMap"
						onchange="onQuerySelect(this)">
						<option value="-1"><%=Constants.SELECT_OPTION%></option>
						<%int selectedIndex = 0;
					int queryCount = 0;
					String textAreaValue = "";
					if (queryList != null)
					{

						StringBuffer displayValue = new StringBuffer();
						for (int n = 0; n < queryList.size(); n++)
						{
							NameValueBean bean = (NameValueBean) queryList.get(n);
							String queryKey = bean.getValue();
							displayValue.setLength(0);
							displayValue.append(bean.getName());
							if (queryKey.equalsIgnoreCase(selectedQueryKey))
							{
								selectedIndex = queryCount + 1;
								textAreaValue = displayValue.toString();
							}
							queryCount++;

							%>
						<option value="<%=queryKey%>" name="<%=queryKey%>"><%=displayValue.toString()%>
						</option>
						<%}

					}

					%>
					</select> <script>
						var c= document.getElementById("queryMap");
						c.selectedIndex=<%=selectedIndex%>;
						//alert(c.selectedIndex);
					</script></td>

					<td class="formLabelBold" width="30"><!-- html:textarea property="selectedQuery" styleClass="formFieldSized15" /-->
					<textarea id="selectedQuery" class="formFieldSized15" wrap="soft"><%=textAreaValue%></textarea></td>
					<td><input type="button" class="actionButton" value="Show results"
						onclick="doGo()" /> <html:hidden property="pageOf"
						value="advancedSearch" /></td>
				</tr>
			</table>

			</td>

		</tr>
		<%}

				%>
		<tr height="5%">
			<td class="dataPagingSection"><custom:test name="Search Results"
				pageNum="<%=pageNum%>" totalResults="<%=totalResults%>"
				numResultsPerPage="<%=numResultsPerPage%>" pageName="<%=pageName%>" />
			<html:hidden property="isPaging" value="true" /></td>
		</tr>
		<tr>
			<td>
			<table summary="" cellpadding="0" cellspacing="0" border="0"
				width="100%" height="90%">
				<tr>

					<td class="formLabelBold" width="5%"><bean:message
						key="result.show" />:</td>
					<td class="formLabelNoBorder" width="10%"><bean:message
						key="result.frequency" /></td>
					<td class="formLabelLeftNoBorder" width="10%"><input type=checkbox
						name="checkFreq" id="checkFreq" onClick='showHideFreq(this)'></td>
					<td class="formLabelLeftNoBorder" width="6%"><bean:message
						key="result.confidenceScore" /></td>
					<td class="formLabelLeftNoBorder" width="20"><input type=checkbox
						name="checkConfScore" id="checkConfScore"
						onClick='showHideFreq(this)'></td>
					<td><html:hidden property="confidenceScore" value="" /></td>
					<td><html:hidden property="frequency" value="" /></td>
					<td class="formLabelRightNoBorder" width="70%"><html:button
						styleClass="actionButton" property="" onclick="onExport()"
						disabled="<%=disableExportButton%>">
						<bean:message key="buttons.export" />
					</html:button></td>
					<td class="formLabelLeftNoBorder" width="70%"></td>
				</tr>
			</table>
			</td>
		</tr>



		<tr height="85%">
			<td width="100%"><%@ include
				file="/pages/content/common/GridPage.jsp"%></td>
		</tr>
		<%}

			%>
		<tr>
			<td><html:hidden property="operation" value="" /></td>
			<td><html:hidden property="sortedColumn" value="" /></td>
			<td><html:hidden property="sortedColumnIndex" value="" /></td>
			<td><html:hidden property="sortedColumnDirection" value="" /></td>
			<td><html:hidden property="queryKey" value="" /></td>
		</tr>
		<tr>
	</html:form>

</table>
<script>
		//retain the criteria on page change
		// criteria such as checed frequency / confidence / sroted column / selected query
		var checkbox;
		<%
		if(sortedColumn!=null&&sortedColumnDirection!=null&&sortedColumnIndex!=null)
		{
		%>
		document.forms[0].sortedColumn.value="<%=sortedColumn%>";	
		document.forms[0].sortedColumnDirection.value="<%=sortedColumnDirection%>";
		document.forms[0].sortedColumnIndex.value="<%=sortedColumnIndex%>";	
			
		<%
		}
		%>
		<%
			if(isConfidenceChecked!=null&&isConfidenceChecked.equalsIgnoreCase("true"))
			{
		%>
			checkbox=document.getElementById("checkConfScore");
			checkbox.checked=true;
			isCheckBoxChecked(checkbox);
		//	showHideFreq(checkbox);
		<%
			}
			if(isFrequencyChecked!=null&&isFrequencyChecked.equalsIgnoreCase("true"))
			{
		%>
				checkbox=document.getElementById("checkFreq");
				checkbox.checked=true;
				isCheckBoxChecked(checkbox);
		//		showHideFreq(checkbox);
		<%
			}
		%>	
		<%
		if(selectedQueryKey!=null&&selectedQueryKey.length()>0)
		{
		%>
			document.forms[0].queryKey.value="<%=selectedQueryKey%>";
		<%
		}
		%>	
</script>

