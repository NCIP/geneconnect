<!-- GeneConnect Advanced Search Select Path page -->

<!-- TagLibs -->
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/core-jstl" prefix="c"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/SpreadSheetTag.tld" prefix="spreadsheet"%>
<%@ taglib uri="/WEB-INF/PagenationTag.tld" prefix="custom"%>
<!-- Taglibs -->

<!-- Imports -->
<%@
      page language="java" contentType="text/html"
	import="java.util.*"
	import="edu.wustl.geneconnect.actionForm.AdvancedSearchForm"
	import="edu.wustl.geneconnect.util.DisplayInformationInterface"
	import="edu.wustl.common.beans.NameValueBean"
	import="edu.wustl.common.util.Utility"
	import="edu.wustl.common.util.global.Constants"
	import="edu.wustl.geneconnect.util.global.GCConstants"%>
<!-- Imports -->
<%
			List dataSourcesList = (ArrayList) request.getAttribute(GCConstants.DATA_SOURCES_LIST);

			//	List validPathsForDataSources = (ArrayList) request.getAttribute(GCConstants.VALID_PATHS_LIST_FOR_DATA_SOURCES);
			List validPathsForDataSources = (List) request
					.getAttribute(GCConstants.PAGINATION_DATA_LIST);
			Map dataSourcesLinksMap = (HashMap) request
					.getAttribute(GCConstants.DATA_SOURCES_LINKS_MAP);

			Map dataSourcesMap = (HashMap) request.getAttribute(GCConstants.DATA_SOURCES_MAP);

			Map alreadySelectedPaths = (HashMap) request
					.getAttribute(GCConstants.ALREADY_SELECTED_PATHS);

			int pageNum = Integer.parseInt((String) request.getAttribute(GCConstants.PAGE_NUMBER));
			int totalResults = Integer.parseInt((String) request
					.getAttribute(GCConstants.TOTAL_RESULTS));
			int numResultsPerPage = Integer.parseInt((String) request
					.getAttribute(GCConstants.RESULTS_PER_PAGE));
			String pageName = "AdvancedSearchSelectPath.do";

			//System.out.println("No. of DataSources entered by User-->"+dataSourcesList.size());
%>
<!-- Css and Scripts -->
<head>
<!-- Css and Scripts -->
<script language="JavaScript" type="text/javascript"
	src="jss/javaScript.js"></script>
<script src="jss/script.js"></script>
<script>
	
	var selectedPath="";
	var selectedPathName="";

	function addPath()
	{			
		<%
			for(int i=0;i<validPathsForDataSources.size();i++)
			{
				String dataSources = (String)validPathsForDataSources.get(i);
			
				StringTokenizer pathTokenized = new StringTokenizer(dataSources, "-");

				String selectedPathName = "";

				String sourceDataSource = new String();
				String targetDataSource = new String();
				String dataSourcesLinksKey;
				boolean firstToken = true;
				String listBoxName = new String();
				while(pathTokenized.hasMoreTokens())
				{
					if(firstToken)
					{	
						sourceDataSource = pathTokenized.nextToken();
						selectedPathName+= (dataSourcesMap.get(sourceDataSource)+"-");
						firstToken = false;
			%>
					selectedPath = selectedPath + "<%=sourceDataSource%>"+"_";
					selectedPathName = selectedPathName + "<%=dataSourcesMap.get(sourceDataSource)%>";
			<%
					}

					if(pathTokenized.hasMoreTokens())
					{
						targetDataSource = pathTokenized.nextToken();

						dataSourcesLinksKey = sourceDataSource+"-"+targetDataSource;

						listBoxName= i+":"+sourceDataSource+"-"+targetDataSource;
						
						//System.out.println("ListBoxName-->"+listBoxName);	
		%>
					var listBoxValue = document.getElementById('<%=listBoxName%>');
					selectedPath = selectedPath + listBoxValue.value + "_" + "<%=targetDataSource%>" + "_";
					//alert("List Box Value-->"+listBoxValue.options[listBoxValue.selectedIndex].text);
					selectedPathName = selectedPathName + " {" + listBoxValue.options[listBoxValue.selectedIndex].text + "} ";
					selectedPathName= selectedPathName + "<%=dataSourcesMap.get(targetDataSource)%>";
		<%
						selectedPathName+= (dataSourcesMap.get(targetDataSource)+"-");			
					}
					
					//selectedPathName+= (dataSourcesMap.get(pathTokenized.nextToken())+"-");
					sourceDataSource = targetDataSource;
				}

				selectedPathName = selectedPathName.substring(0, selectedPathName.length()-1);
				
				//System.out.println("Selected Path DataSources-->"+selectedPathName);				
	
		%>	
		var checkBox = document.getElementById(<%=i%>);
		
		if(checkBox.checked)
		{
			//alert("Selected Path-->"+checkBox.name+":"+checkBox.value);
			var rows = new Array(); 
			rows = document.getElementById('pathDiv').rows; 
			var totalrows = rows.length; 
			var isPathAlreadySelected = false;
				
			for(var i=0; i<totalrows; i++)
			{
				var checkbox=document.getElementById('pathDiv').rows[i].cells[0].firstChild.id;
				//alert("Already Selected rows-->"+checkbox);
				if(checkbox == selectedPath)
				{
					isPathAlreadySelected = true;
					break;
				}
			}

			if(isPathAlreadySelected)
			{
				alert("Path is already selected");
			}
			else
			{
				var newrowno = totalrows + 1;
				var newrow=document.getElementById('pathDiv').insertRow(rows.length); 
				newrow.id=selectedPathName;
				var newcell=newrow.insertCell(0); 
				newcell.className="blankFormField";
				//field="<input type='checkbox' id='path' name='path' value='"+selectedPath+"'>"+"<%=selectedPathName%>";
				field="<input type='checkbox' id='"+selectedPath+"' name='"+selectedPathName+"' value='"+selectedPath+"'>"+selectedPathName;
				newcell.innerHTML=""+field;
			}
			//alert("Path prepared-->"+selectedPath);
			//alert("Path Display Value-->"+selectedPathName);
			
			checkBox.checked = false;
			//checkBox.disabled = true;
		}
		selectedPath="";
		selectedPathName="";
		<%		
			}
		%>		
	}

	function removePath()
	{
		var divObj=document.getElementById('pathDiv');
		var rows = new Array(); 
		rows = document.getElementById('pathDiv').rows; 
		var totalrows = rows.length;
		var rowIdCounter = 0;
		var rowIds = new Array();
		
		for(var i=0; i<totalrows; i++) 
		{
			var checkbox=divObj.rows[i].cells[0].firstChild;
			if(checkbox.checked)
			{
				//alert("To Delete-->"+divObj.rows[i].id);
				rowIds[rowIdCounter]=divObj.rows[i].id;
				rowIdCounter=rowIdCounter+1;
			}
		}
	
		for(var i=0; i<rowIds.length; i++) 
		{
			var rowObject=document.getElementById(rowIds[i]);
			divObj.removeChild(rowObject);
		}
	}

	function submitToSearch(action)
	{
		var selectedPaths="";

		var divObj = document.getElementById('pathDiv');
		var rows = new Array(); 
		rows = document.getElementById('pathDiv').rows; 
		var totalrows = rows.length; 
			
		for(var i=0; i<totalrows; i++)
		{
			selectedPaths = selectedPaths + divObj.rows[i].cells[0].firstChild.id + "#";
		}

		//alert("selectedPaths-->"+selectedPaths);
		
		var action = action;
		
		document.forms[0].selectedPaths.value = selectedPaths;

		document.forms[0].action = action;
		document.forms[0].submit();
	}

	function showGraph()
	{
		var selectedPathsForGraph = "";

			<%
			for(int i=0;i<validPathsForDataSources.size();i++)
			{
				String dataSources = (String)validPathsForDataSources.get(i);
			
				StringTokenizer pathTokenized = new StringTokenizer(dataSources, "-");

				String selectedPathName = "";

				String sourceDataSource = new String();
				String targetDataSource = new String();
				String dataSourcesLinksKey;
				boolean firstToken = true;
				String listBoxName = new String();
				while(pathTokenized.hasMoreTokens())
				{
					if(firstToken)
					{	
						sourceDataSource = pathTokenized.nextToken();
						selectedPathName+= (dataSourcesMap.get(sourceDataSource)+"-");
						firstToken = false;
			%>
					selectedPath = selectedPath + "<%=sourceDataSource%>"+"_";
					selectedPathName = selectedPathName + "<%=dataSourcesMap.get(sourceDataSource)%>";
			<%
					}

					if(pathTokenized.hasMoreTokens())
					{
						targetDataSource = pathTokenized.nextToken();

						dataSourcesLinksKey = sourceDataSource+"-"+targetDataSource;

						listBoxName= i+":"+sourceDataSource+"-"+targetDataSource;
						
						//System.out.println("ListBoxName-->"+listBoxName);	
		%>
					var listBoxValue = document.getElementById('<%=listBoxName%>');
					selectedPath = selectedPath + listBoxValue.value + "_" + "<%=targetDataSource%>" + "_";
					//alert("List Box Value-->"+listBoxValue.options[listBoxValue.selectedIndex].text);
					selectedPathName = selectedPathName + " {" + listBoxValue.options[listBoxValue.selectedIndex].text + "} ";
					selectedPathName= selectedPathName + "<%=dataSourcesMap.get(targetDataSource)%>";
		<%
						selectedPathName+= (dataSourcesMap.get(targetDataSource)+"-");			
					}
					
					//selectedPathName+= (dataSourcesMap.get(pathTokenized.nextToken())+"-");
					sourceDataSource = targetDataSource;
				}

				selectedPathName = selectedPathName.substring(0, selectedPathName.length()-1);
				
				//System.out.println("Selected Path DataSources-->"+selectedPathName);				
	
		%>	
		var checkBox = document.getElementById(<%=i%>);
		
		if(checkBox.checked)
		{
			selectedPathsForGraph = selectedPathsForGraph + selectedPath + "$";
			
			//checkBox.checked = false;
			//checkBox.disabled = true;
		}

		selectedPath="";
		selectedPathName="";
		<%		
			}
		%>		
		
		var url = ".."+"<%=request.getContextPath()%>"+"/GeneConnectGraph.do?selectedPathsForGraph="+selectedPathsForGraph;
		newwindow=window.open(url,'name','height=600,width=540');
		if (window.focus) {newwindow.focus()}
		
		//alert("Paths Selected for Graph-->"+selectedPathsForGraph);
	}

	function send(pageNum,numresultsPerPage,prevPage,pageName) 
	{

		var selectedPaths="";

		var divObj = document.getElementById('pathDiv');
		var rows = new Array(); 
		rows = document.getElementById('pathDiv').rows; 
		var totalrows = rows.length; 

		for(var i=0; i<totalrows; i++)
		{
			selectedPaths = selectedPaths + divObj.rows[i].cells[0].firstChild.name + "="+divObj.rows[i].cells[0].firstChild.id+"#";
		}

		document.forms[0].alreadySelectedPaths.value = selectedPaths;
	
		//alert("Local send function called...");
		document.forms[0].action = pageName+'?pageNum='+pageNum;
		document.forms[0].submit();
	}

	function submitToFilter()
	{
		/* if(document.forms[0].startsWithDataSources.value == "-2")
		{
			alert("Please select Starts With DataSource Search criteria");
		}
		else if(document.forms[0].endsWithDataSources.value == "-2")
		{
			alert("Please select Ends With DataSource Search criteria");
		} */
		if( (document.forms[0].startsWithDataSources.value != "-1") &  (document.forms[0].endsWithDataSources.value != "-1") & (document.forms[0].startsWithDataSources.value == document.forms[0].endsWithDataSources.value) )
		{
			alert("Starts With DataSource cannot be same as Ends With DataSource");
		}
		else
		{
			var selectedPaths="";
	
			var divObj = document.getElementById('pathDiv');
			var rows = new Array(); 
			rows = document.getElementById('pathDiv').rows; 
			var totalrows = rows.length; 
				
			for(var i=0; i<totalrows; i++)
			{
				selectedPaths = selectedPaths + divObj.rows[i].cells[0].firstChild.name + "="+divObj.rows[i].cells[0].firstChild.id+"#";
			}
	
			document.forms[0].alreadySelectedPaths.value = selectedPaths;
	
			document.forms[0].action = "AdvancedSearchSelectPath.do?targetAction=filterPaths";
			document.forms[0].submit();
		}
	}

</script>

</head>

<html:errors />
<html:messages id="messageKey" message="true" header="messages.header"
	footer="messages.footer">
	<%=messageKey%>
</html:messages>
<!-- Css and Scripts -->

<!-- Displays Title -->
<table summary="" cellpadding="0" cellspacing="0" border="0"
	width="100%" height="6%">

	<tr height="5%">
		<td class="formTitle" width="100%"><bean:message
			key="advancedSearchSelectPath.title" /></td>
	</tr>

</table>
<!-- Displays Title -->
<!-- Content of page -->
<html:form action="AdvancedSearch.do?targetAction=search">
	<table summary="" cellpadding="0" cellspacing="0" border="0"
		class="contentPage" width="800">
		<tr>
			<td><html:hidden property="selectedPaths" value="" /> <html:hidden
				property="targetAction" value="" /></td>
		</tr>
		<tr>
			<td valign="top" halign="Left">
			<table summary="" cellpadding="1" cellspacing="0" border="0"
				width="800">
				<tr height="9%">
					<td class="dataPagingSection1"><custom:test name="Search Results"
						pageNum="<%=pageNum%>" totalResults="<%=totalResults%>"
						numResultsPerPage="<%=numResultsPerPage%>"
						pageName="<%=pageName%>" /> <html:hidden property="isPaging"
						value="true" /> <html:hidden property="alreadySelectedPaths"
						value="" /></td>
				</tr>
				<tr>
					<td class="formTitleSmallFont" height="40"><bean:message
						key="advancedSearchSelectPath.pathType" /> <html:select
						property="pathTypes" styleClass="formFieldSized10"
						styleId="pathTypes" size="1" disabled="false">
						<html:option key="-1" value="-1">All</html:option>
						<html:option key="0" value="0">Alignment Based</html:option>
						<html:option key="1" value="1">Non-Alignment</html:option>
						<html:option key="2" value="2">Shortest</html:option>
					</html:select> <bean:message
						key="advancedSearchSelectPath.startsWith" /> <html:select
						property="startsWithDataSources" styleClass="formFieldSized10"
						styleId="startsWithDataSources" size="1" disabled="false">
						<html:option key="0" value="-1">Any </html:option>
						<html:options collection="<%=GCConstants.DATA_SOURCES_LIST%>"
							labelProperty="name" property="value" />
					</html:select> <bean:message
						key="advancedSearchSelectPath.endsWith" /> <html:select
						property="endsWithDataSources" styleClass="formFieldSized10"
						styleId="endsWithDataSources" size="1" disabled="false">
						<html:option key="0" value="-1">Any </html:option>
						<html:options collection="<%=GCConstants.DATA_SOURCES_LIST%>"
							labelProperty="name" property="value" />
					</html:select> <html:button styleClass="actionButton" property=""
						onclick="submitToFilter()">
						<bean:message key="buttons.go" />
					</html:button> <html:button styleClass="actionButton" property=""
						onclick="addPath()">
						<bean:message key="buttons.addToList" />
					</html:button> <html:button styleClass="actionButton" property=""
						onclick="showGraph()">
						<bean:message key="buttons.showGraph" />
					</html:button></td>
				</tr>

				<%String pathType = "";

			%>
				<logic:equal name="advancedSearchForm" property="pathTypes"
					value="0">
					<%pathType = "0";

			%>
				</logic:equal>
				<logic:equal name="advancedSearchForm" property="pathTypes"
					value="1">
					<%pathType = "1";

			%>
				</logic:equal>
				<%System.out.println("Path Type-->" + pathType);

			%>
				<tr>
					<td>
					<DIV class="selectPathColumnDiv">
					<table summary="" cellpadding="1" cellspacing="0" border="0"
						width="800">
						<tbody id="validPathsDiv">
							<%String tdClassName;
			String listClassName;
			for (int i = 0; i < validPathsForDataSources.size(); i++)
			{

				%>
							<tr class="formField800WidthMediumGray">
								<%String checkBoxName = "" + i;

				%>
								<td class="formSerialNumberField10sized"><input type="checkbox"
									name="<%=checkBoxName%>" id="<%=checkBoxName%>"
									value="<%=validPathsForDataSources.get(i)%>"></td>

								<%if ((i % 2) == 0)
				{
					tdClassName = "formField800WidthLightGray";
					listClassName = "formFieldSized8LightGray";
				}
				else
				{
					tdClassName = "formField800WidthMediumGray";
					listClassName = "formFieldSized8MediumGray";
				}

				%>
								<td class="<%=tdClassName%>"><%String path;
				StringTokenizer pathTokenized;

				path = (String) validPathsForDataSources.get(i);

				pathTokenized = new StringTokenizer(path, "-");

				String sourceDataSource = new String();
				String targetDataSource = new String();
				String dataSourcesLinksKey;
				boolean firstToken = true;

				while (pathTokenized.hasMoreTokens())
				{
					boolean linkTypeSelected = false;
					if (firstToken)
					{
						sourceDataSource = pathTokenized.nextToken();
						firstToken = false;
					}

					if (pathTokenized.hasMoreTokens())
					{
						targetDataSource = pathTokenized.nextToken();
						dataSourcesLinksKey = sourceDataSource + "-" + targetDataSource;

						List links = (ArrayList) dataSourcesLinksMap.get(dataSourcesLinksKey);
						request.setAttribute("links", links);

						%> <%=dataSourcesMap.get(sourceDataSource)%> <%String listBoxName = i + ":" + sourceDataSource + "-" + targetDataSource;

						%>
								<select name="<%=listBoxName%>" id="<%=listBoxName%>"
									class="<%=listClassName%>">
									<c:forEach var="link" items="${links}">
										<%//Alignment-Based  4,8
						if (pathType.equals("0") & linkTypeSelected == false)
						{

							%>
										<c:if test="${link.value == '4'}">
											<option value="<c:out value="${link.value}"/>" selected><c:out
												value="${link.name}" /></option>
											<%linkTypeSelected = true;%>
										</c:if>
										<c:if test="${link.value == '8'}">
											<option value="<c:out value="${link.value}"/>" selected><c:out
												value="${link.name}" /></option>
											<%linkTypeSelected = true;%>
										</c:if>
										<c:if test="${ (link.value ne '4') and (link.value ne '8') }">
											<option value="<c:out value="${link.value}"/>"><c:out
												value="${link.name}" /></option>
										</c:if>
										<%}
						//Non-Alignment  1,2
						else if (pathType.equals("1") & linkTypeSelected == false)
						{

							%>
										<c:if test="${link.value == '1'}">
											<option value="<c:out value="${link.value}"/>" selected><c:out
												value="${link.name}" /></option>
											<%linkTypeSelected = true;%>
										</c:if>
										<c:if test="${link.value == '2'}">
											<option value="<c:out value="${link.value}"/>" selected><c:out
												value="${link.name}" /></option>
											<%linkTypeSelected = true;%>
										</c:if>
										<c:if test="${ (link.value ne '1') and (link.value ne '2') }">
											<option value="<c:out value="${link.value}"/>"><c:out
												value="${link.name}" /></option>
										</c:if>
										<%}
						else
						{

						%>
										<option value="<c:out value="${link.value}"/>"><c:out
											value="${link.name}" /></option>
										<%}%>
									</c:forEach>
								</select> <%}
					sourceDataSource = targetDataSource;
				}

				%> <%=dataSourcesMap.get(sourceDataSource)%></td>
							</tr>
							<%}%>
						</tbody>
					</table>
					</DIV>
					</td>
				</tr>
				<tr>
					<td height="10"></td>
				</tr>
				<tr>
					<td>
					<table summary="" cellpadding="3" cellspacing="0" border="1"
						width="100%">
						<tr>
							<td class="formTitle" height="20" nowrap><bean:message
								key="advancedSearchSelectPath.searchList" /></td>
							<td align="right" class="formTitle" height="20" nowrap><html:button
								styleClass="actionButton" property="" onclick="removePath()">
								<bean:message key="buttons.remove" />
							</html:button></td>
						</tr>
					</table>
					<div
						style="overflow:auto;width:100%;height:100px;border:1px solid #336699;padding-left:5px">
					<table>
						<tbody id="pathDiv">
							<%if (alreadySelectedPaths != null)
			{

				Collection keySet = alreadySelectedPaths.keySet();

				List keys = new ArrayList(keySet);

				String alreadySelectedPathName = "";

				String alreadySelectedPathValue = "";

				for (int i = 0; i < alreadySelectedPaths.size(); i++)
				{
					alreadySelectedPathName = (String) keys.get(i);

					alreadySelectedPathValue = (String) alreadySelectedPaths
							.get(alreadySelectedPathName);
					System.out.println("alreadySelectedPathValue: "+alreadySelectedPathValue);

					%>
							<tr id=<%=alreadySelectedPathName%>>
								<td class="blankFormField"><input type="checkbox"
									id="<%=alreadySelectedPathValue%>"
									name="<%=alreadySelectedPathName %>"
									value="<%=alreadySelectedPathValue%>"> <%=alreadySelectedPathName%>
								</td>
							</tr>
							<%}
			}

		%>
						</tbody>
					</table>
					</div>
					</td>
				</tr>
				<tr>
					<td align="right"><html:button styleClass="actionButton"
						property=""
						onclick="submitToSearch('AdvancedSearch.do?targetAction=search&pageOf=advancedSearch&isPaging=false')">
						<bean:message key="buttons.search" />
					</html:button> <html:button styleClass="actionButton" property=""
						onclick="submitForm('AdvancedSearch.do?targetAction=nothing&pageOf=advancedSearch&isPaging=false')">
						<bean:message key="buttons.backToQuery" />
					</html:button></td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</html:form>
<!-- Content of page -->
<!-- GeneConnect Advanced Search Select Path page -->
