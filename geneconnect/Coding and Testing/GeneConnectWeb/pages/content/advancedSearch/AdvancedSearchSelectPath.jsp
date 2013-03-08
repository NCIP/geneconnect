<%--L
  Copyright Washington University at St. Louis

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/geneconnect/LICENSE.txt for details.
L--%>

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
			List validPathsForDataSources = (List) request.getAttribute(GCConstants.PAGINATION_DATA_LIST);
			Map dataSourcesLinksMap = (HashMap) request.getAttribute(GCConstants.DATA_SOURCES_LINKS_MAP);

			Map dataSourcesMap = (HashMap) request.getAttribute(GCConstants.DATA_SOURCES_MAP);

			Map alreadySelectedPaths = (HashMap) request.getAttribute(GCConstants.ALREADY_SELECTED_PATHS);

			int pageNum = Integer.parseInt((String) request.getAttribute(GCConstants.PAGE_NUMBER));
			int totalResults = Integer.parseInt((String) request.getAttribute(GCConstants.TOTAL_RESULTS));
			int numResultsPerPage = Integer.parseInt((String) request.getAttribute(GCConstants.RESULTS_PER_PAGE));
			String pageName = "AdvancedSearchSelectPath.do";

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
						
		%>
					//alert('ListBoxName-->'+'<%=listBoxName%>');
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
				
		%>	
		var checkBox = document.getElementById(<%=i%>);
		
		if(checkBox.checked)
		{
			//alert("Selected Path-->"+checkBox.name+":"+checkBox.value);
			var rows = new Array(); 
			rows = document.getElementById('pathDiv').rows; 
			var totalrows = rows.length; 
			var isPathAlreadySelected = false;
			//var temp = divObj.getElementsByTagName("input");
	
			for(var i=0; i<totalrows; i++)
			{
				var checkbox=document.getElementById('pathDiv').rows[i].cells[0].firstChild.id;
				//var checkbox=temp[i].id;
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

		var selectAllPathsCheckBox = document.getElementById('selectAllPaths');
		selectAllPathsCheckBox.checked = false;
	}

	function removePath()
	{
		//alert("In RemovePath()...");
		var divObj=document.getElementById('pathDiv');
		var rows = new Array(); 
		rows = document.getElementById('pathDiv').rows; 
		var totalrows = rows.length;
		var rowIdCounter = 0;
		var rowIds = new Array();
		var temp = divObj.getElementsByTagName("input");
		
		for(var i=0; i<totalrows; i++) 
		{
			//var checkbox=divObj.rows[i].cells[0].firstChild;
			var checkbox=temp[i];
			if(checkbox.checked)
			{
				//alert("To Delete-->"+divObj.rows[i].id);
				//rowIds[rowIdCounter]=divObj.rows[i].id;
				//alert("To Delete-->"+temp[i].name);
				rowIds[rowIdCounter]=temp[i].name;
				rowIdCounter=rowIdCounter+1;
			}
		}
	
		for(var i=0; i<rowIds.length; i++) 
		{
			var rowObject=document.getElementById(rowIds[i]);
			divObj.removeChild(rowObject);
		}

		var removeAllSelectedPathsCheckBox=document.getElementById('removeAllSelectedPaths');
		removeAllSelectedPathsCheckBox.checked = false;
	}

	function submitToSearch(action)
	{
		var selectedPaths="";

		var divObj = document.getElementById('pathDiv');
		var rows = new Array(); 
		rows = document.getElementById('pathDiv').rows; 
		var totalrows = rows.length; 
		var temp = divObj.getElementsByTagName("input");

		for(var i=0; i<totalrows; i++)
		{
			//selectedPaths = selectedPaths + divObj.rows[i].cells[0].firstChild.id + "$";
			selectedPaths = selectedPaths + temp[i].id + "$";
		}

		//alert("selectedPaths-->"+selectedPaths);
		
		var action = action;
		
		document.forms[0].selectedPaths.value = selectedPaths;

		document.forms[0].action = action;
		document.forms[0].submit();
	}

	function submitToAdvancedSearchPage(action)
	{
		var selectedPaths="";

		var divObj = document.getElementById('pathDiv');
		var rows = new Array(); 
		rows = document.getElementById('pathDiv').rows; 
		var totalrows = rows.length; 
		var temp = divObj.getElementsByTagName("input");
			
		for(var i=0; i<totalrows; i++)
		{
			//selectedPaths = selectedPaths + divObj.rows[i].cells[0].firstChild.name + "="+divObj.rows[i].cells[0].firstChild.id+"#";
			selectedPaths = selectedPaths + temp[i].name + "="+temp[i].id+"#";
		}

		var io = "";
		for (var i=document.forms[0].startsWithDataSources.options.length-1; i>=0; i--)
		{
			io+=document.forms[0].startsWithDataSources.options[i].value+"$";
			//alert(document.forms[0].startsWithDataSources.options[i].value);
		}
		
		var action = action;
		
		document.forms[0].selectedPaths.value = selectedPaths;
		//alert("Paths selected-->"+selectedPaths);

		document.forms[0].backFromSelectPath.value = true;
		document.forms[0].initialInputOutput.value = io;

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
						
		%>
					//alert('ListBoxName-->'+'<%=listBoxName%>');
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
		
		//alert(selectedPathsForGraph);
		var url = ".."+"<%=request.getContextPath()%>"+"/GeneConnectGraph.do?selectedPathsForGraph="+selectedPathsForGraph;
		newwindow=window.open(url,'name','height=750,width=545,left=20, top=0, screenX=20, screenY=0');
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
		var temp = divObj.getElementsByTagName("input");

		for(var i=0; i<totalrows; i++)
		{
			//selectedPaths = selectedPaths + divObj.rows[i].cells[0].firstChild.name + "="+divObj.rows[i].cells[0].firstChild.id+"#";
			selectedPaths = selectedPaths + temp[i].name + "="+temp[i].id+"#";
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
			var temp = divObj.getElementsByTagName("input"); 
				
			for(var i=0; i<totalrows; i++)
			{
				//selectedPaths = selectedPaths + divObj.rows[i].cells[0].firstChild.name + "="+divObj.rows[i].cells[0].firstChild.id+"#";
				selectedPaths = selectedPaths + temp[i].name + "="+temp[i].id+"#";
			}
	
			document.forms[0].alreadySelectedPaths.value = selectedPaths;
	
			document.forms[0].action = "AdvancedSearchSelectPath.do?targetAction=filterPaths";
			document.forms[0].submit();
		}
	}

	function setAllPaths(element)
	{
		<%
		for(int i=0;i<validPathsForDataSources.size();i++)
		{
		%>
		var checkBox = document.getElementById(<%=i%>);
	
		checkBox.checked=element.checked;
		<%
		}
		%>
	}

	function removeAllPaths(element)
	{
		var divObj = document.getElementById('pathDiv');
		var rows = new Array(); 
		rows = document.getElementById('pathDiv').rows; 
		var totalrows = rows.length; 
		var temp = divObj.getElementsByTagName("input");
				
		for(var i=0; i<totalrows; i++)
		{
			//var selectedPath = divObj.rows[i].cells[0].firstChild;
			var selectedPath = temp[i];
			selectedPath.checked = element.checked;
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
	width="100%" height="30">

	<tr>
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
			<td>
				<html:hidden property="selectedPaths" value="" /> 
				<html:hidden property="targetAction" value="" />
			</td>
		</tr>
		<tr>
			<td valign="top" halign="Left">
				<table summary="" cellpadding="1" cellspacing="0" border="0" width="800">
					<tr height="9%">
						<td class="dataPagingSection1" colspan="2">
							<custom:test name="Search Results" pageNum="<%=pageNum%>" totalResults="<%=totalResults%>" numResultsPerPage="<%=numResultsPerPage%>" pageName="<%=pageName%>" /> 
							<html:hidden property="isPaging" value="true" /> 
							<html:hidden property="alreadySelectedPaths" value="" />
							<html:hidden property="backFromSelectPath" value="" />
							<html:hidden property="initialInputOutput" value="" />
						</td>
					</tr>
					<tr>
						<td class="formTitleSmallFont10Sized" rowspan="3" width="2%">		
							<input type='checkbox' name='selectAllPaths' id='selectAllPaths'onClick='setAllPaths(this)' />	
						</td>	
						<td class="formTitleSmallFont" height="20" width="98%" align="center">
							<bean:message key="advancedSearchSelectPath.path" /> 
							<html:select property="ontFilterCode" styleClass="formFieldSized60" styleId="ontFilterCode" size="1" disabled="false">
								<html:option key="1" value="1">All Paths</html:option>
								<html:option key="2" value="2">Paths with at least 1 input and 1 output</html:option>
								<html:option key="3" value="3">Paths with all inputs and subset of outputs</html:option>
								<html:option key="4" value="4">Paths with all inputs and outputs</html:option>
								<html:option key="5" value="5">Paths with all inputs and outputs starting with an input or output and ending with an input or output</html:option>
								<html:option key="6" value="6">Paths with all inputs and outputs starting with an input and ending with an output</html:option>
								<html:option key="7" value="7">Paths with all inputs and outputs only in any order</html:option>
								<html:option key="8" value="8">Paths with inputs and outputs only traversing all inputs first and then all outputs</html:option>
							</html:select>
						</td>
					</tr>
					<tr>
						<td class="formTitleSmallFont" height="20" align="center">
							<bean:message key="advancedSearchSelectPath.pathType" /> 
							<html:select property="pathTypes" styleClass="formFieldSized10" styleId="pathTypes" size="1" disabled="false">
								<html:option key="-1" value="-1">Any</html:option>
								<html:option key="0" value="0">Alignment Based</html:option>
								<html:option key="1" value="1">Non-Alignment</html:option>
							</html:select>
							<bean:message key="advancedSearchSelectPath.startsWith" /> 
							<html:select property="startsWithDataSources" styleClass="formFieldSized15" styleId="startsWithDataSources" size="1" disabled="false">
								<html:option key="0" value="-1">Any </html:option>
								<html:options collection="<%=GCConstants.DATA_SOURCES_LIST%>" labelProperty="name" property="value" />
							</html:select>
							<bean:message key="advancedSearchSelectPath.endsWith" /> 
							<html:select property="endsWithDataSources" styleClass="formFieldSized15" styleId="endsWithDataSources" size="1" disabled="false">
								<html:option key="0" value="-1">Any </html:option>
								<html:options collection="<%=GCConstants.DATA_SOURCES_LIST%>" labelProperty="name" property="value" />
							</html:select> 
							
						</td>
					</tr>
					<tr>
						<td class="formTitleSmallFont" height="20" align="center">
							<html:button styleClass="actionButton" property="" onclick="submitToFilter()">
								<bean:message key="buttons.go" />
							</html:button>
							<html:button styleClass="actionButton" property="" onclick="addPath()">
								<bean:message key="buttons.addToList" />
							</html:button>
							<html:button styleClass="actionButton" property="" onclick="showGraph()">
								<bean:message key="buttons.showGraph" />
							</html:button>
						</td>
					</tr>
					<%String pathType = ""; %>
					<logic:equal name="advancedSearchForm" property="pathTypes" value="0">
						<%pathType = "0";%>
					</logic:equal>
					<logic:equal name="advancedSearchForm" property="pathTypes" value="1">
						<%pathType = "1";%>
					</logic:equal>
					<tr>
						<td colspan="3">
							<DIV class="selectPathColumnDiv">
								<table summary="" cellpadding="1" cellspacing="0" border="0" width="800">
									<tbody id="validPathsDiv">
									<%
									String tdClassName;
									String listClassName;
									for (int i = 0; i < validPathsForDataSources.size(); i++)
									{
									%>
										<tr class="formField800WidthMediumGray">
										<%String checkBoxName = "" + i; %>
											<td class="formSerialNumberField10sized">
												<input type="checkbox" name="<%=checkBoxName%>" id="<%=checkBoxName%>" value="<%=validPathsForDataSources.get(i)%>">
											</td>
											<%
											if ((i % 2) == 0)
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
											<td class="<%=tdClassName%>">
												<%
												String path;
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
	
												%> 
												<%=dataSourcesMap.get(sourceDataSource)%> 
												<%
												String listBoxName = i + ":" + sourceDataSource + "-" + targetDataSource;
												%>
												<select name="<%=listBoxName%>" id="<%=listBoxName%>" class="<%=listClassName%>">
												<% 
												String valueForAnyOption = "";
												for(int k=0; k<links.size(); k++)
												{
													NameValueBean option = (NameValueBean) links.get(k);
													//Alignment-Based  4,8
													if(pathType.equals("0") & linkTypeSelected == false)
													{
														if(option.getValue().equals("4") || option.getValue().equals("8"))
														{
												%>
													<option value="<%=option.getValue() %>" selected> <%=option.getName()%> </option>
												<%
															linkTypeSelected = true;
														}
														else
														{
												%>
													<option value="<%=option.getValue() %>"> <%=option.getName()%> </option>
												<%
														}
													}
													//Non-Alignment  1,2
													else if (pathType.equals("1") & linkTypeSelected == false)
													{
														if(option.getValue().equals("1") || option.getValue().equals("2"))
														{
												%>
													<option value="<%=option.getValue() %>" selected> <%=option.getName()%> </option>
												<%
															linkTypeSelected = true;
														}
														else
														{
												%>
													<option value="<%=option.getValue() %>"> <%=option.getName()%> </option>
												<%
														}
													}
													else
													{
												%>
													<option value="<%=option.getValue() %>"> <%=option.getName()%> </option>
												<%
													}
													valueForAnyOption+=option.getValue();
												}	
									
												//valueForAnyOption += "*";

												if(links.size()>1)
												{
												%>
													<option value="<%=valueForAnyOption%>"> ANY </option>
												<%
												}
												%>
												</select> 
												<%
												}
												sourceDataSource = targetDataSource;
											}
											%> 
											<%=dataSourcesMap.get(sourceDataSource)%>
											</td>
										</tr>
										<%
										}
										%>
									</tbody>
								</table>
							</DIV>
						</td>
					</tr>
					<tr>
						<td height="10" colspan="3"></td>
					</tr>
					<tr>
						<td colspan="3">
							<table summary="" cellpadding="3" cellspacing="0" border="1" width="100%">
								<tr>
									<td class="formTitleSmallFont10Sized">		
										<input type='checkbox' name='removeAllSelectedPaths' id='removeAllSelectedPaths'onClick='removeAllPaths(this)' />	
									</td>	
									<td class="formTitle" height="20" nowrap>
										<bean:message key="advancedSearchSelectPath.searchList" />
									</td>
									<td align="right" class="formTitle" height="20" nowrap>
										<html:button styleClass="actionButton" property="" onclick="removePath()">
											<bean:message key="buttons.remove" />
										</html:button>
									</td>
								</tr>
							</table>
							<div style="overflow:auto;width:100%;height:100px;border:1px solid #336699;padding-left:5px; float:left">
								<table>
									<tbody id="pathDiv">
										<%
										if (alreadySelectedPaths != null)
										{
	
											Collection keySet = alreadySelectedPaths.keySet();
					
											List keys = new ArrayList(keySet);
							
											String alreadySelectedPathName = "";
					
											String alreadySelectedPathValue = "";
					
											for (int i = 0; i < alreadySelectedPaths.size(); i++)
											{
												alreadySelectedPathName = (String) keys.get(i);
					
												alreadySelectedPathValue = (String) alreadySelectedPaths.get(alreadySelectedPathName);
										%>
										<tr id="<%=alreadySelectedPathName%>">
											<td class="blankFormField">
												<input type="checkbox" id="<%=alreadySelectedPathValue%>" name="<%=alreadySelectedPathName %>" value="<%=alreadySelectedPathValue%>">
												<%=alreadySelectedPathName%>
											</td>
										</tr>
										<%
											}
										}
										%>
									</tbody>
								</table>
							</div>
						</td>
					</tr>
					<tr>
						<td align="right" colspan="3">
							<html:button styleClass="actionButton" property="" onclick="submitToSearch('AdvancedSearch.do?targetAction=search&pageOf=advancedSearch&isPaging=false')">
								<bean:message key="buttons.search" />
							</html:button>
							<html:button styleClass="actionButton" property="" onclick="submitToAdvancedSearchPage('AdvancedSearch.do?targetAction=nothing&pageOf=advancedSearch&isPaging=false')">
								<bean:message key="buttons.backToQuery" />
							</html:button>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</html:form>
<!-- Content of page -->
<!-- GeneConnect Advanced Search Select Path page -->
