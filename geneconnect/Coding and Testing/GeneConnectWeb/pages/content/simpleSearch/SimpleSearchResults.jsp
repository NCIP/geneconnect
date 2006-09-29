<!-- GeneConnect Simple Search Result page -->

<!-- TagLibs -->
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/core-jstl" prefix="c"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/PagenationTag.tld" prefix="custom" %>
<!-- Taglibs -->

<!-- Imports -->
<%@
      page
      language="java"
      contentType="text/html"
      import="java.util.*"
	  import="edu.wustl.geneconnect.actionForm.SimpleSearchForm"
	  import="edu.wustl.geneconnect.util.DisplayInformationInterface"
	  import ="edu.wustl.common.beans.NameValueBean"
	  import ="edu.wustl.common.util.Utility"
	  import="edu.wustl.geneconnect.util.global.GCConstants"
	  import="edu.wustl.geneconnect.bizlogic.ResultDataInterface"
%>
<link href="runtime/styles/xp/grid.css" rel="stylesheet" type="text/css" ></link>
<script src="runtime/lib/grid.js"></script>
<script src="runtime/lib/aw.js"></script>

<script src="runtime/lib/gridcheckbox.js"></script>
<script src="runtime/formats/date.js"></script>
<script src="runtime/formats/string.js"></script>
<script src="runtime/formats/number.js"></script>
<script src="jss/script.js"></script>
<head>
<!-- Css and Scripts -->

<script language="JavaScript" type="text/javascript" src="jss/javaScript.js"></script>
<script language="javascript">
		var colZeroDir='ascending';
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
	</script>

<%
	
	String pageName = "SearchResultView.do";	
	//ResultDataInterface resultData = (ResultDataInterface)request.getAttribute(GCConstants.RESULT_DATA_LIST);
		
	int pageNum = Integer.parseInt((String)request.getAttribute(GCConstants.PAGE_NUMBER));
	int totalResults = Integer.parseInt((String)request.getAttribute(GCConstants.TOTAL_RESULTS));
	int numResultsPerPage = Integer.parseInt((String)request.getAttribute(GCConstants.RESULTS_PER_PAGE));
	
	List columnList = (List)request.getAttribute(GCConstants.SPREADSHEET_COLUMN_LIST);
	
	List dataList =  (List)request.getAttribute(GCConstants.PAGINATION_DATA_LIST);
	
	List freqColumns =new ArrayList();
	List genomicColumns = new ArrayList();
	List confColumns = new ArrayList();
	List otherColumns = new ArrayList();
	int columnForSetId=0;
	for(int i=0;i<columnList.size();i++)
	{
		
		String colValue = (String)columnList.get(i);
					
		if((!colValue.endsWith(GCConstants.FREQUENCY_KEY_SUFFIX))&&(!colValue.endsWith(GCConstants.CONF_SCORE_KEY))
				&&(!colValue.endsWith(GCConstants.SET_ID_KEY)))
		{
			genomicColumns.add(""+(i));

		}
		else if(colValue.endsWith(GCConstants.FREQUENCY_KEY_SUFFIX))
		{
			freqColumns.add(""+(i));	
		}
		else if(colValue.endsWith(GCConstants.CONF_SCORE_KEY))
		{
			
			confColumns.add(""+(i));	
		}
		else
		{
			otherColumns.add(""+(i));
			if(colValue.endsWith(GCConstants.SET_ID_KEY))
			{	
				columnForSetId=i;
			}	
		}
	}
	System.out.println("Conf colValue: "+confColumns.size());
	System.out.println("Set colValue: "+otherColumns.size());
	boolean isSpecimenData = false;	
	int IDCount = 0;
	if(dataList != null && dataList.size() != 0)
	{
	%>
		
		<script>
				var myData = [<%int xx;%><%for (xx=0;xx<(dataList.size()-1);xx++){%>
		<%
			
			
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
			
			var columns = [<%int k;%><%for (k=0;k < (columnList.size()-1);k++){if (columnList.get(k).toString().trim().equals("ID")){IDCount++;}%>"<%=columnList.get(k)%>",<%}if (columnList.get(k).toString().trim().equals("ID")){IDCount++;}%>"<%=columnList.get(k)%>","Paths"];
			
		</script>

	<% } %>
<%int imgCol = ((columnList.size()-IDCount));
System.out.println("IMG: "+imgCol);
%>	




<style type="text/css">
<%
String pre=".active-column-"+(imgCol);
String suf="{width:40px;background-image:url('images\\mag1.GIF');background-repeat: no-repeat;background-position: 0% 0%}";

%>
<%=pre+suf%>
<%
String height = "100%";
String width = "800";
%>
.activeColWithOutFreq
{
overflow: auto; width:<%=width%>; height:<%=height%>; padding:0px; margin: 0px; border: 1px solid;
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
	String suffix="{width:0px}";
%>
<%=prefix+suffix%>
<%	
}
%>
<%
for(int i=0;i<confColumns.size();i++)
{
	String prefix=".activeColWithOutFreq .active-column-"+confColumns.get(i);
	String suffix="{width:0px}";
%>
<%=prefix+suffix%>
<%	
}
%>
<%
for(int i=0;i<otherColumns.size();i++)
{
	String prefix=".activeColWithOutFreq .active-column-"+otherColumns.get(i);
	String suffix="{width:0px}";
%>
<%=prefix+suffix%>
<%	
}
%>

.activeColWithFreq
{
overflow: auto; width:<%=width%>; height:<%=height%>; padding:0px; margin: 0px; border: 1px solid;
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
	String suffix="{width:0px}";
%>
<%=prefix+suffix%>
<%	
}
%>
<%
for(int i=0;i<otherColumns.size();i++)
{
	String prefix=".activeColWithFreq .active-column-"+otherColumns.get(i);
	String suffix="{width:0px}";
%>
<%=prefix+suffix%>
<%	
}
%>

.activeColWithFreqConf
{
overflow: auto; width:<%=width%>; height:<%=height%>; padding:0px; margin: 0px; border: 1px solid;
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
	String suffix="{width:0px}";
%>
<%=prefix+suffix%>
<%	
}
%>


.activeColWithConf
{
overflow: auto; width:<%=width%>; height:<%=height%>; padding:0px; margin: 0px; border: 1px solid;
}
<%
for(int i=0;i<genomicColumns.size();i++)
{
	String prefix=".activeColWithConf .active-column-"+genomicColumns.get(i);
	String suffix="{width:100px}";
%>
<%=prefix+suffix%>
<%	
}
%>
<%
for(int i=0;i<freqColumns.size();i++)
{
	String prefix=".activeColWithConf .active-column-"+freqColumns.get(i);
	String suffix="{width:0px}";
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
	String suffix="{width:0px}";
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

		<table summary="" cellpadding="0" cellspacing="0" border="0" width="100%" height="90%">
			<html:form action="/SearchResultView.do">
			
<!-- && pageOf.equals(Constants.PAGEOF_QUERY_RESULTS) -->
				<%
					if(dataList == null || dataList.size()==0)
					{
				%>
						<bean:message key="advanceQuery.noRecordsFound"/>
				<%
					}
					else if(dataList != null && dataList.size() != 0)
					{
				%>
					<tr height="5%">
						 <td  width="100%">
							<br>
						 </td>
					</tr>	
					<tr height="5%">
						 <td class="formTitle" width="100%">
							<bean:message key="simpleSearch.result.title"/>
						 </td>
					</tr>	
		<tr height="5%">
			<td class="dataPagingSection">					
				<custom:test name="Search Results" pageNum="<%=pageNum%>" totalResults="<%=totalResults%>" numResultsPerPage="<%=numResultsPerPage%>" pageName="<%=pageName%>" />
				<html:hidden property="isPaging" value="true"/>			
			</td>
		</tr>
		<tr>
		<td>
				<table summary="" cellpadding="0" cellspacing="0" border="0" width="100%" height="90%">
				<tr>
		
				<td class="formLabelBold" width="5%">
				     	<bean:message key="result.show"/>:
				</td>
				<td class="formLabelNoBorder" width="10%">
				     	<bean:message key="result.frequency"/>
				</td>
				<td class="formLabelLeftNoBorder" width="10%">
					<input type=checkbox  name="checkFreq" id="checkFreq" onClick='showHideFreq(this)'>
				</td>
				<td class="formLabelLeftNoBorder" width="12%">
				     	<bean:message key="result.confidenceScore"/>
				</td>
				<td class="formLabelLeftNoBorder" width="20">
					<input type=checkbox  name="checkConfScore" id="checkConfScore" onClick='showHideFreq(this)'>
				</td>
				<td class="formLabelLeftNoBorder" width="70%">
				</td>
				</tr>
				</table>
				</td>
			</tr>
		
		
		
		<tr height="85%">
			<td width="100%">
				<div id="active_div" class="activeColWithOutFreq">
					<script>
						
							//	create ActiveWidgets Grid javascript object.
							var obj = new Active.Controls.Grid;
							
							
							
							
							obj.setRowProperty("count", <%=dataList.size()%>);
							obj.setColumnProperty("count", <%=(columnList.size()-IDCount) + 1%>);
							
							//	provide cells and headers text
							obj.setDataProperty("text", function(i, j){return myData[i][j]});
							obj.setColumnProperty("text", function(i){return columns[i]});
							

							//	set headers width/height.
							obj.setRowHeaderWidth("28px");
							obj.setColumnHeaderHeight("20px");
							
							var message = function(){
							       var row = obj.getSelectionProperty("index");
							       
							       //var col = obj.getColumnProperty("index");
									var url = "/SimpleSearch.do?setid="+myData[row][<%=columnForSetId%>];
									alert(url);
									//newwindow=window.open('/SimpleSearch.do?setid=','name','height=400,width=200');
									//if (window.focus) {newwindow.focus()}
									
							    }

    						//obj.setAction("selectionChanged", message);
							//obj.setAction("click", message);
							
							obj.getColumnTemplate(<%=imgCol%>).setEvent("onmousedown",message);
							
							//original sort method  
							var _sort = obj.sort; 
							//overide sort function to meet our requirenemnt
						    obj.sort = function(index, direction, alternateIndex){ 
						        
						    //if check box column is clicked
						    //then sort on the flag those are in 8th column
						        if(index==0)
						        {
						        	index=myData[0].length-1;
						        	direction=colZeroDir;
									if(colZeroDir=='ascending')colZeroDir='descending';
									else colZeroDir='ascending';
						        	
						       } 
						        
					            _sort.call(this, index, direction);
						        
						        return true;
						    }
							//	write grid html to the page.
							document.write(obj);
					</script>
				</div>
			</td>
		</tr>
	<% } %>

	<tr>
		<td><html:hidden property="operation" value=""/></td>
		
	</tr>
</html:form>

</table>

