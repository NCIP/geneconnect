<%--L
  Copyright Washington University at St. Louis

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/geneconnect/LICENSE.txt for details.
L--%>

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
	import="edu.wustl.common.util.Utility"
	import="edu.wustl.geneconnect.util.global.GCConstants"
	import="edu.wustl.common.util.global.Constants"
	import="edu.wustl.geneconnect.bizlogic.ResultDataInterface"%>
<link href="runtime/styles/xp/grid.css" rel="stylesheet" type="text/css"></link>
<script src="runtime/lib/grid.js"></script>
<script src="runtime/lib/aw.js"></script>

<script src="runtime/lib/gridcheckbox.js"></script>
<script src="runtime/formats/date.js"></script>
<script src="runtime/formats/string.js"></script>
<script src="runtime/formats/number.js"></script>
<script src="jss/script.js"></script>
<head>
<!-- Css and Scripts -->

<script language="JavaScript" type="text/javascript"
	src="jss/javaScript.js"></script>
<script language="javascript">
		var colZeroDir='ascending';
		var selectedQuery="";
		function showFreq()
		{
			obj.setColumnIndices([0, 2, 3, 4]);
		}
		function onAddToCart()
		{
			var isChecked = "false";
			for (var i=0;i < document.forms[0].elements.length;i++)
		    {
		    	var e = document.forms[0].elements[i];
		    	
		        if (e.name != "checkAll" && e.type == "checkbox" && e.checked == true)
		        {
		        	isChecked = "true";
		        	break;
		        }
		    }
		    
		    if(isChecked == "true")
		    {
				var action = "ShoppingCart.do?operation=add";
				document.forms[0].operation.value="add";
				document.forms[0].action = action;
				document.forms[0].target = "myframe1";
				document.forms[0].submit();
			}
		}
		
		function onExport()
		{
			var isChecked = "false";
			for (var i=0;i < document.forms[0].elements.length;i++)
		    {
		    	var e = document.forms[0].elements[i];
		    	
		        if (e.name != "checkAll" && e.type == "checkbox" && e.checked == true)
		        {
		        	isChecked = "true";
		        	break;
		        }
		    }
		    
		    if(isChecked == "true")
		    {
				var action = "SpreadsheetExport.do";
				document.forms[0].operation.value="export";
				document.forms[0].action = action;
				//document.forms[0].target = "_blank";
				document.forms[0].submit();
			}
			else
			{
				alert("Please select at least one checkbox");
			}
		}
		//function that is called on click of Define View button for the configuration of search results
		function onSimpleConfigure()
		{
				action="ConfigureSimpleQuery.do?pageOf=pageOfSimpleQueryInterface";
				document.forms[0].action = action;
				document.forms[0].target = "_parent";
				document.forms[0].submit();
		}

		function onAdvanceConfigure()
		{
				action="ConfigureAdvanceSearchView.do?pageOf=pageOfQueryResults";
				document.forms[0].action = action;
				document.forms[0].target = "myframe1";
				document.forms[0].submit();
		}
		function onRedefineSimpleQuery()
		{
			action="SimpleQueryInterface.do?pageOf=pageOfSimpleQueryInterface&operation=redefine";
			document.forms[0].action = action;
			document.forms[0].target = "_parent";
			document.forms[0].submit();
		}
		function onRedefineAdvanceQuery()
		{
			action="AdvanceQueryInterface.do?pageOf=pageOfAdvanceQueryInterface&operation=redefine";
			document.forms[0].action = action;
			document.forms[0].target = "_parent";
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
		function showHideFreq(checkbox)
		{
			var theForm = document.forms[0];
			var freqCnt=0;
			var confCnt=0;
		    selected=new Array();
		
		    for(var i=0,j=0;i<theForm.elements.length;i++)
		    {
		 	  	if(theForm[i].name == 'checkFreq' && theForm[i].type == 'checkbox' && theForm[i].checked==true)
		 	  	{
			       freqCnt++;
			    }
			    if(theForm[i].name == 'checkConfScore' && theForm[i].type == 'checkbox' && theForm[i].checked==true)
		 	  	{
			       confCnt++;
			    }  
			}
			
			if(freqCnt>0 && confCnt>0)
			{
				document.getElementById("active_div").className="activeColWithFreqConf";
			}
			else if(freqCnt>0)
			{
				document.getElementById("active_div").className="activeColWithFreq";
			}	
			else if(confCnt>0)
			{
				document.getElementById("active_div").className="activeColWithConf";
			}
			else
			{
				document.getElementById("active_div").className="activeColWithOutFreq";
			}	
			obj.refresh();
		}
		function showPath(grid)
		{
			var obj = document.getElementById("active_div");
			//var value = obj.getRowProperty("value", 3);
			//var value = obj.getSelectionProperty("value", 3);
			alert(obj.id);
		}
		function onQuerySelect(combo)
		{
			//alert(combo.value);
			if(combo.value!="-1")
			{
				var text = combo.value;
				selectedQuery=text+" ";
				var ind = text.indexOf("_",0)
				var disptext = text.substr(ind+1,text.length);
				//alert(disptext);
				var textarea = document.getElementById("selectedQuery");
				textarea.value=disptext;
			}	
			
		}
		function doGo()
		{
			if(selectedQuery.length>0)
			{
				var action = "SearchResultView.do?pageOf=advancedSearch&isPaging=false&queryKey="+selectedQuery;
				//document.forms[0].operation.value="export";
				document.forms[0].action = action;
				//document.forms[0].target = "_blank";
				document.forms[0].submit();
			}
			else
			{
				
			}
		}
	</script>

<%String pageName = "SearchResultView.do";
			//ResultDataInterface resultData = (ResultDataInterface)request.getAttribute(GCConstants.RESULT_DATA_LIST);
		
			int pageNum = Integer.parseInt((String) request.getAttribute(GCConstants.PAGE_NUMBER));
			int totalResults = Integer.parseInt((String) request
					.getAttribute(GCConstants.TOTAL_RESULTS));
			int numResultsPerPage = Integer.parseInt((String) request
					.getAttribute(GCConstants.RESULTS_PER_PAGE));

			/**Column headers to display*/
			List columnList = (List) request.getAttribute(GCConstants.SPREADSHEET_COLUMN_LIST);
			/**Query keys to select */
			Map queryMap = (Map)session.getAttribute(GCConstants.QUERY_KEY_MAP);
			/**Sorting columns*/ 
			Collections.sort(columnList);
			/**remove Confidence score and SET_ID couln header and ad it to end of sorted list*/
			for (java.util.Iterator iter = columnList.iterator();iter.hasNext();)
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
			/**Datalist containing map where MAP contains column header as key and genoimicIde as value */
			/**One eleemtn in data list contains map for one set*/
			List dataList = (List) request.getAttribute(GCConstants.PAGINATION_DATA_LIST);
			List freqColumns = new ArrayList();
			List genomicColumns = new ArrayList();
			List confColumns = new ArrayList();
			List otherColumns = new ArrayList();
			int columnForSetId = 0;
			
			/**Get Freq,confidence and setid column to bui;ld the css for hiding / unhiding*/
			for (int i = 0; i < columnList.size(); i++)
			{

				String colValue = (String) columnList.get(i);

				if ((!colValue.endsWith(GCConstants.FREQUENCY_KEY_SUFFIX))
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
			
			if (dataList != null && dataList.size()!=0)
			{

				%>

<script>
				var myData = [<%int xx;%><%for (xx=0;xx<(dataList.size()-1);xx++){%>
		<%
			
			/*Build data list an dcolumn headers array to pass to Active grid control*/
			HashMap setMap = (HashMap)dataList.get(xx);
			List row =new ArrayList();	
			
			for(int i=0;i<columnList.size();i++)
			{
				
				String colValue = (String)setMap.get(columnList.get(i));
				row.add(colValue);
			}
			//List row = (List)dataList.get(xx);
	  		int j;
	  		//Bug 700: changed the variable name for the map values as it was same in both AdvanceSearchForm and SimpleQueryInterfaceForm
			String chkName = "value1(CHK_" + xx + ")";
		%>
			[<%for (j=0;j < (row.size()-1);j++){%>"<%=row.get(j)%>",<%}%>"<%=row.get(j)%>","","1"],<%}%>
		<%
			HashMap setMap = (HashMap)dataList.get(xx);
			List row =new ArrayList();	
			for(int i=0;i<columnList.size();i++)
			{
				
				String colValue = (String)setMap.get(columnList.get(i));
				row.add(colValue);
			}
	  		int j;
	  		//Bug 700: changed the variable name for the map values as it was same in both AdvanceSearchForm and SimpleQueryInterfaceForm
			String chkName = "value1(CHK_" + xx + ")";
		%>
			[<%for (j=0;j < (row.size()-1);j++){%>"<%=Utility.toGridFormat(row.get(j))%>",<%}%>"<%=Utility.toGridFormat(row.get(j))%>","","1"]
			];
<%}

			%>			
			
			
</script>
<script>
	var columns = [<%int k;%><%for (k=0;k < (columnList.size()-1);k++){if (columnList.get(k).toString().trim().equals("ID")){IDCount++;}%>"<%=columnList.get(k)%>",<%}if (columnList.get(k).toString().trim().equals("ID")){IDCount++;}%>"<%=columnList.get(k)%>","Paths"];
</script>


<!-- Column number to display path icon-->
<%int imgCol = ((columnList.size() - IDCount));

			%>



<!-- Construc css for hide / unhide frequency and /or confidenc column -->
<style type="text/css">
<%
String pre=".active-column-"+(imgCol);
String suf="{width:40px;background-image:url('images/mag1.GIF');background-repeat: no-repeat;background-position: 0% 0%}";

%>
<%=pre+suf%>
<%
String height = "100%";
String width = "100%";
%>
.activeColWithOutFreq
{
overflow: auto; width:<%=width%>; height:<%=height%>; padding:0px; margin: 0px; border: 0px solid;
}

<%
for(int i=0;i<genomicColumns.size();i++)
{
	String prefix=".activeColWithOutFreq .active-column-"+genomicColumns.get(i);
	String suffix="{width:150px}";
%>
<%=prefix+suffix%>
<%	
}
%>
<%
for(int i=0;i<freqColumns.size();i++)
{
	String prefix=".activeColWithOutFreq .active-column-"+freqColumns.get(i);
	String suffix="{display:none!important}";
%>
<%=prefix+suffix%>
<%	
}
%>
<%
for(int i=0;i<confColumns.size();i++)
{
	String prefix=".activeColWithOutFreq .active-column-"+confColumns.get(i);
	String suffix="{display:none!important}";
%>
<%=prefix+suffix%>
<%	
}
%>
<%
for(int i=0;i<otherColumns.size();i++)
{
	String prefix=".activeColWithOutFreq .active-column-"+otherColumns.get(i);
	String suffix="{display:none!important}";
%>
<%=prefix+suffix%>
<%	
}
%>

.activeColWithFreq
{
overflow: auto; width:<%=width%>; height:<%=height%>; padding:0px; margin: 0px; border: 0px solid;
}
<%
for(int i=0;i<genomicColumns.size();i++)
{
	String prefix=".activeColWithFreq .active-column-"+genomicColumns.get(i);
	String suffix="{width:150px}";
%>
<%=prefix+suffix%>
<%	
}
%>
<%
for(int i=0;i<freqColumns.size();i++)
{
	String prefix=".activeColWithFreq .active-column-"+freqColumns.get(i);
	String suffix="{width:150px}";
%>
<%=prefix+suffix%>
<%	
}
%>
<%
for(int i=0;i<confColumns.size();i++)
{
	String prefix=".activeColWithFreq .active-column-"+confColumns.get(i);
	String suffix="{display:none!important}";
%>
<%=prefix+suffix%>
<%	
}
%>
<%
for(int i=0;i<otherColumns.size();i++)
{
	String prefix=".activeColWithFreq .active-column-"+otherColumns.get(i);
	String suffix="{display:none!important}";
%>
<%=prefix+suffix%>
<%	
}
%>

.activeColWithFreqConf
{
overflow: auto; width:<%=width%>; height:<%=height%>; padding:0px; margin: 0px; border: 0px solid;
}
<%
for(int i=0;i<genomicColumns.size();i++)
{
	String prefix=".activeColWithFreqConf .active-column-"+genomicColumns.get(i);
	String suffix="{width:150px}";
%>
<%=prefix+suffix%>
<%	
}
%>
<%
for(int i=0;i<freqColumns.size();i++)
{
	String prefix=".activeColWithFreqConf .active-column-"+freqColumns.get(i);
	String suffix="{width:150px}";
%>
<%=prefix+suffix%>
<%	
}
%>
<%
for(int i=0;i<confColumns.size();i++)
{
	String prefix=".activeColWithFreqConf .active-column-"+confColumns.get(i);
	String suffix="{width:100px}";
%>
<%=prefix+suffix%>
<%	
}
%>
<%
for(int i=0;i<otherColumns.size();i++)
{
	String prefix=".activeColWithFreqConf .active-column-"+otherColumns.get(i);
	String suffix="{display:none!important}";
%>
<%=prefix+suffix%>
<%	
}
%>


.activeColWithConf
{
overflow: auto; width:<%=width%>; height:<%=height%>; padding:0px; margin: 0px; border: 0px solid;
}
<%
for(int i=0;i<genomicColumns.size();i++)
{
	String prefix=".activeColWithConf .active-column-"+genomicColumns.get(i);
	String suffix="{width:150px}";
%>
<%=prefix+suffix%>
<%	
}
%>
<%
for(int i=0;i<freqColumns.size();i++)
{
	String prefix=".activeColWithConf .active-column-"+freqColumns.get(i);
	String suffix="{display:none!important}";
%>
<%=prefix+suffix%>
<%	
}
%>
<%
for(int i=0;i<confColumns.size();i++)
{
	String prefix=".activeColWithConf .active-column-"+confColumns.get(i);
	String suffix="{width:100px}";
%>
<%=prefix+suffix%>
<%	
}
%>
<%
for(int i=0;i<otherColumns.size();i++)
{
	String prefix=".activeColWithConf .active-column-"+otherColumns.get(i);
	String suffix="{display:none!important}";
%>
<%=prefix+suffix%>
<%	
}
%>


tr#hiddenCombo
{
 display:none;
}

</style>
</head>

<table summary="" cellpadding="0" cellspacing="0" border="0"
	width="100%" height="90%">
	<html:form action="/SearchResultView.do">

		<!-- && pageOf.equals(Constants.PAGEOF_QUERY_RESULTS) -->
		<!-- If no data then display apropriate message -->
		<%if (dataList == null || dataList.size() == 0)
			{
			dataList= new ArrayList();
			%>
				<bean:message key="advanceQuery.noRecordsFound" />
				
		<%}
			if (dataList != null)
			{

				%>
		<tr height="5%">
			<td width="100%"><br>
			</td>
		</tr>
		<tr height="5%">
			<td class="formTitle" width="100%"><bean:message
				key="simpleSearch.result.title" /></td>
		</tr>
		<tr>
			<td class="dataPagingSection">
				<table summary="" cellpadding="2" cellspacing="2" border="0"
					width="100%" height="90%">
					
				<tr>
					<td class="formLabelBold" width="110">
						
    						<bean:message key="buttons.selectQuery" />
    					
		 			</td>
					<td class="formLabelBold" width="20">
					
						<select  class="formDropDownSized" id="queryMap"  onchange="onQuerySelect(this)">
						<option value="-1"><%=Constants.SELECT_OPTION%></option>
							<%
								if(queryMap!=null)
								{
									Set keySet = queryMap.keySet();
									StringBuffer displayValue  = new StringBuffer();
									for(Iterator iter=keySet.iterator();iter.hasNext();)
									{
										String queryKey = (String)iter.next();
										displayValue.setLength(0);
										displayValue.append((String)queryMap.get(queryKey));
							%>
							<option value="<%=queryKey%>" name="<%=queryKey%>"> 
								<%=displayValue.toString()%>
							</option>		
							<% 			
									}
								}
							%>
						</select>
					</td>
					
					<td class="formLabelBold" width="30">
						<!-- html:textarea property="selectedQuery" styleClass="formFieldSized15" /-->
						<textarea id="selectedQuery" class="formFieldSized15" wrap="soft" >
						</textarea>
					</td>
					<td>
						<input type="button" class="actionButton" value="Show results" onclick="doGo()"/>
						<html:hidden property="pageOf" value="advancedSearch" />
					</td>
				</tr>
				</table>  
				
			</td>
			
		</tr>
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
					<td class="formLabelLeftNoBorder" width="12%"><bean:message
						key="result.confidenceScore" /></td>
					<td class="formLabelLeftNoBorder" width="20"><input type=checkbox
						name="checkConfScore" id="checkConfScore"
						onClick='showHideFreq(this)'></td>

					<td class="formLabelLeftNoBorder" width="70%"></td>
				</tr>
			</table>
			</td>
		</tr>



		<tr height="85%">
			<td width="100%">
			<div id="active_div" class="activeColWithOutFreq"><script>
						
							//	create ActiveWidgets Grid javascript object.
							var obj = new Active.Controls.Grid;
							obj.setRowProperty("count", <%=dataList.size()%>);
							obj.setColumnProperty("count", <%=(columnList.size()-IDCount) + 1%>);
							
							//	provide cells and headers text
							<%if(dataList.size()>0)
							{
							%>
							obj.setDataProperty("text", function(i, j){return myData[i][j]});
							<%
							}
							%>
							obj.setColumnProperty("text", function(i){return columns[i]});

							//	set headers width/height.
							obj.setRowHeaderWidth("28px");
							obj.setColumnHeaderHeight("20px");
							obj.setAction("click", function(src)
													{
														varSelCol = src.getColumnProperty('index'); 
								    					varSelRow = src.getRowProperty('index'); 
								    					 if(varSelCol==<%=imgCol%>)
								    					 {
															var url = ".."+"<%=request.getContextPath()%>"+"/GeneConnectGraph.do?setid="+myData[varSelRow][<%=columnForSetId%>];
								    						newwindow=window.open(url,'name','height=600,width=600,resizable=yes');
															if (window.focus) {newwindow.focus()}
								    					 }
													    
													}
												);
							
							var link = new Active.Templates.Link;
							link.setAttribute("href","#");
							obj.setColumnTemplate(link,<%=imgCol%>);
							//original sort method  
							var _sort = obj.sort; 
							document.write(obj);
					</script></div>
			</td>
		</tr>
		<%}

		%>
		<tr>
			<td><html:hidden property="operation" value="" /></td>
			
		</tr>
		<tr>
	</html:form>

</table>

