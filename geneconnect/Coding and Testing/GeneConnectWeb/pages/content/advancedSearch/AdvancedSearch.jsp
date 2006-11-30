<!-- GeneConnect Advanced Search page -->

<!-- TagLibs -->
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/core-jstl" prefix="c"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/SpreadSheetTag.tld" prefix="spreadsheet"%>

<!-- Taglibs -->

<!-- Imports -->
<%@
      page language="java" contentType="text/html"
	import="java.util.*"
	import="edu.wustl.geneconnect.actionForm.AdvancedSearchForm"
	import="edu.wustl.geneconnect.util.DisplayInformationInterface"
	import="edu.wustl.common.beans.NameValueBean"
	import="edu.wustl.common.util.Utility"
	import="edu.wustl.geneconnect.util.global.GCConstants"%>
<!-- Imports -->
<%
			 
			Map attributesMap = (HashMap) request.getAttribute(GCConstants.DATASOURCE_ATTRIBUTES);

			List dataSourceList = (List) request.getAttribute(GCConstants.DATA_SOURCES_KEY);

			Object obj = request.getAttribute(GCConstants.ADVANCED_SEARCH_FORM);

			Map inputSources = new HashMap();

			Map outputSources = new HashMap();

			if (obj != null && obj instanceof AdvancedSearchForm)
			{
				AdvancedSearchForm advancedSearchForm = (AdvancedSearchForm) obj;

				inputSources = (HashMap) advancedSearchForm.getInputDataSources();

				outputSources = (HashMap) advancedSearchForm.getOutputDataSources();
			}

	//System.out.println("No. of InputDataSources-->"+inputSources.size());
	//System.out.println("No. of OutputDataSources-->"+outputSources.size());

%>
<!-- Css and Scripts -->
<head>
<!-- Css and Scripts -->
<script language="JavaScript" type="text/javascript"
	src="jss/javaScript.js"></script>
<script src="jss/script.js"></script>

<script>
		var currentTextbox;
		
		// Check all output data source
		function checkAll(element)
		{			
			var checkbox2=element;
			var checkBox;
			<%
				for(int i=0;i<dataSourceList.size();i++)
				{
					NameValueBean bean =(NameValueBean)dataSourceList.get(i);
					String check = bean.getName();
			%>	
				checkBox = document.getElementById("<%=check%>");
				if(checkbox2.checked==true)
				{
					checkBox.checked=true;
				}
				else
				{
					checkBox.checked=false;
				}
				checkFrequency(checkBox);	
			<%		
				}
			%>		
		
		}
		// enable frequency textbox if ouput data source chekc box is checked
		function checkFrequency(element)
		{
			var textBoxName = element.name + "_FrequenceValue";
			if(element.checked)
			{
				var textBox = document.getElementById(textBoxName);
				textBox.disabled = false;
			}
			else
			{
				var textBox = document.getElementById(textBoxName);
				textBox.value = "";
				textBox.disabled = true;
			}
		}
		
		// validate is float value enter
		var dotcontain=false;
		
		function intOnly(field) 
		{
			if(field.value.length>0) 
			{
				field.value = field.value.replace(/[^\d.]+/g, ''); 
			}
		}
		
		function pasteData()
		{
		//	alert(currentTextbox.name);
			if(currentTextbox==null)
			{
				var tbodytemp = document.getElementById("Inputspreadsheet");
				var temp = tbodytemp.getElementsByTagName("input");
				var j;
					
					
				for(j=0;j<temp.length;j++)
				{
					if(temp[j].type!="text")
					{
						currentTextbox=temp[j];
					}
				}
			}
			if(currentTextbox==null)
			{
				alert("Select texbox from where to start the paste");
				return;
			}
			
			var text = currentTextbox.name ;
			var ind = text.indexOf("_",0)
			var startdsName= text.substr(ind+1,text.length-2);
			//alert(startdsName);
			var rows = splitGenomicIds();
			var pastedCell=new Array();
			var pastedCellCount=0;
			var pastedRowsCount=0;
			var lastPastedTextbox;
			if(rows.length>0)
			{
				//alert("rows.length "+rows.length);
				var isTopaste=false;
				var j=0;
				var prevj=0;
				var innerRowCount=0;
				var innerRowLen=0;
				for (var i=0; i<rows.length; )
				{
					//alert("i " +i);
					var innerRow=rows[i];	
					//alert("innerRow.length "+innerRow.length);	
					innerRowCount=0;
					innerRowLen=innerRow.length;
					var tbody = document.getElementById("Inputspreadsheet");
					var t = tbody.getElementsByTagName("input");
					
					var isTopaste=false;
					if(i==0)
					{
						for(m=0;m<t.length;m++)
						{
							if(t[m].name==text)
							{
								//alert("j " +j);
								prevj=j;
								break;
							}
							j++;
						}
					}
					else
					{
						j=prevj;
					}
					for(j=prevj;j<t.length;j++)
					{
						//if(t[i].name==("inputDataSourcesValue(Input:2_Ensembl Transcript)"||isTopaste)
						//if(currentTextbox!=null&&t[j].name==currentTextbox.name)
						if(t[j].type=="checkbox"&&innerRowCount>0)
						{
							innerRowCount=innerRowLen;
							break;
						}
						var ispasted=false;
						for(var n=0;n<pastedCellCount;n++)
						{
							
							if(pastedCell[n]==t[j].name)
							{
								//alert("pastedRows[n] "+pastedRows[n]);
								ispasted=true;
							}
						}
						
						if(t[j].name.indexOf(startdsName,0)>0)
						{
							//alert("isTopaste " +t[j].name)
							if(ispasted==false)
							{
								isTopaste=true;
							}	
						}
						
						pastedCell[pastedCellCount]=t[j].name;
						pastedCellCount++;
						//alert("isTo " +t[j].name)
						
						if(t[j].type!="checkbox"&&isTopaste)
						{
							//alert("pasete" +i);
							//alert("isTopaste1 " +t[j].name)
							t[j].value= innerRow[innerRowCount];
							lastPastedTextbox=t[j];
							//isTopaste=true;
							
							innerRowCount++;
							//alert(innerRowCount+"--"+innerRow.length);
							if(innerRowCount==innerRow.length)
							{
								//alert(innerRowCount+"--"+innerRow.length);
								//alert("pastedRowsCount--"+pastedRowsCount);
								pastedRowsCount++;
								break;
							}
						}
					}
					i++;
					isTopaste=false;
					
					//alert("i  "+i+"==="+rows.length);
					if(i==rows.length)
					{	
						//alert("break");
						break;
					}
					else if(i<rows.length)
					{
					
						if(hasNextRow(lastPastedTextbox)==false)
						{
							insertInputRow('Inputspreadsheet');
						}
					}
				}	
			}	
		}
		//method to check is there any more row from cuurently seleted row
		function hasNextRow(lastPastedTextbox)
		{
			var text = lastPastedTextbox.name;
			var ind = text.indexOf("_",0);
			var colind = text.indexOf(":",0);
			var temp = text.substr(colind+1,((ind-colind)-1));
			var tbody = document.getElementById("Inputspreadsheet");
			var t = tbody.getElementsByTagName("input");
			var rowAvail = new Array();
			var rowAvailCount=0;
			for(j=0;j<t.length;j++)
			{
				
				if(t[j].type=="checkbox")
				{
					text = t[j].name;
					//alert(t[j].name);
					ind = text.indexOf("_",0);
					var rownum = text.substr(ind+1,text.length); 
					
					if(temp.indexOf(rownum,0)>=0)
					{
						//alert("rownum"+temp+"--"+rownum);
						rowAvailCount++;
						//alert("rowAvailCount " +rowAvailCount);
					}
					if(rowAvailCount>0)
					{	
						rowAvailCount=rowAvailCount+1;
						//alert("v1 "+rowAvailCount);
					}
				}
				
			}
			rowAvailCount--;
			//alert("v "+rowAvailCount);
			if(rowAvailCount>1)
			{
				//alert("true");
				return true;
			}
			//alert("false");
			return false;
			//alert(temp);
			

		} 
		//get the data from clipboard and  split in a form of matrix.
		function splitGenomicIds()
		{
			var copiedData;
			var returnRows = new Array();
			var rowCount=0;
			var rows ;
			// get data from clipboard
		
			if(window.clipboardData)
			{
		
				copiedData = clipboardData.getData('Text');
			}
			else	
			{
				
				//user_pref("signed.applets.codebase_principal_support", true);
				netscape.security.PrivilegeManager.enablePrivilege('UniversalXPConnect');
				// Store support string in an object.
				var str = Components.classes["@mozilla.org/supports-string;1"].createInstance(Components.interfaces.nsISupportsString);
				
				if (!str) return false;

	      		// Make transferable.
    	  		var trans = Components.classes["@mozilla.org/widget/transferable;1"].createInstance(Components.interfaces.nsITransferable);
        		if (!trans) return false;

		        // Specify what datatypes we want to obtain, which is text in this case.
        		trans.addDataFlavor("text/unicode");
  	            var clipid=Components.interfaces.nsIClipboard;
		        var clip = Components.classes["@mozilla.org/widget/clipboard;1"].getService(clipid);
		        if (!clip) return false;
		        clip.getData(trans,clipid.kGlobalClipboard);
		   	    var str1 = new Object(); 
				var strLength = new Object(); 
				trans.getTransferData("text/unicode",str1,strLength); 		
				if (str1) str1 = str1.value.QueryInterface(Components.interfaces.nsISupportsString); 
				if (str1) copiedData = str1.data.substring(0,strLength.value / 2); 
				
			}

			// split data with delima as \n
			rows = copiedData.split("\n");
			//alert("2");
			//alert("rows.length "+rows.length);
			if(rows.length>=1)
			{
				// if the length of splitted data is 1 then 
				// split data with delima as \t\
				//alert("rows.length "+rows.length);
				for(var i=0;i<rows.length;i++)
				{
					var innerRow;
					innerRow=rows[i].split("\t");
					if(innerRow.lenght<=1)
					{
						innerRow=rows[i].split(",");
					}
					//alert(rows[i]+"---"+innerRow);
					//alert("innerRow:" +innerRow.length);
					returnRows[rowCount]=innerRow;
					rowCount=rowCount+1;
				}
			}
			var formattedRow = new Array();
			var formattedRowCnt=0;
			for(var rcnt=0;rcnt<returnRows.length;rcnt++)
			{
				var irow = returnRows[rcnt];
				var allEmpty=0;
				for(var icnt=0;icnt<irow.length;icnt++)
				{
					if(irow[icnt]=="")
					{
						allEmpty++;
					}
				}
				if(allEmpty<irow.length)
				{
					formattedRow[formattedRowCnt]=irow;
					formattedRowCnt++;
				}
//				if(irow.length-1>0)
//				{
//					formattedRow[formattedRowCnt]=irow;
//					formattedRowCnt++;
//				}
			}
			return formattedRow;
		}
		function setCurrentTextBox(t)
		{
			currentTextbox=t;
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
			key="advancedSearch.title" /></td>
	</tr>

</table>
<!-- Displays Title -->
<!-- Content of page -->
<!-- action="AdvancedSearch.do?targetAction=search"-->
<html:form action="AdvancedSearchResults.do?targetAction=search">
	<table summary="" cellpadding="0" cellspacing="0" border="0"
		class="contentPage" width="800">
		<tr>
			<td valign="top" halign="Left">
			<table summary="" cellpadding="1" cellspacing="0" border="0"
				width="800">
				<tr>
					<td>
<!-- Paste Button-->					
					<table summary="" cellpadding="0" cellspacing="0" border="1">
						<tr>
							<td class="formTitle" height="20"><bean:message
								key="simpleSearch.input" /></td>
							<td class="formTitle" height="20" align="right"><html:button
								property="" styleClass="actionButton" onclick="pasteData()">
								<bean:message key="buttons.pasteMultipleIds" />
							</html:button></td>
						</tr>
						<tr>
<!-- Spread sheet view-->						
							<td colspan="2" class="formField"><spreadsheet:sheetview
								className="Input" attributes="<%=attributesMap%>"
								collection="inputDataSources" inputs="<%=inputSources%>" /></td>
						</tr>
						<tr>
<!--Confidence score textbo x-->						
							<td class="formLeftTopSubTableTitle" colspan="2"><bean:message
								key="advancedSearch.confidenceScore" /> <html:text
								styleId="confidenceScore" property="confidenceScore"
								styleClass="formFieldSized10" onkeypress="intOnly(this);"
								onchange="intOnly(this);" onkeyup="intOnly(this);" /></td>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td height="10"></td>
				</tr>
				<tr>
					<td valign="top" width="250">
					<table summary="" cellpadding="3" cellspacing="0" border="1"
						width="400">
						<tr>
<!--Output data sources-->						
							<td class="formTitle" height="20" colspan="3" nowrap><bean:message
								key="simpleSearch.output" /></td>
						</tr>
						<tr>
							<td class="formLeftTopSubTableTitle" width="10%"><input
								type='checkbox' name='checkAll2' id='checkAll2'
								onClick='checkAll(this)' /></td>
							<td class="formRightTopSubTableTitle" width="40%"><span
								class="formField"> <bean:message
								key="advancedSearch.outputDataSource" /> </span></td>
							<td class="formRightTopSubTableTitle" width="50%"><span
								class="formField"> <bean:message key="advancedSearch.frequency" />
							</span></td>
						</tr>
					</table>
					<DIV class="spreadsheet200">
					<table summary="" cellpadding="3" cellspacing="0" border="1"
						width="400">
						<%for (int i = 0; i < dataSourceList.size(); i++)
			{
				
				//popoulate the values on UI from advanced search form

				NameValueBean bean = (NameValueBean) dataSourceList.get(i);
				String dsName = bean.getName();
				String frequencyValue = dsName + "_" +GCConstants.FREQUENCY_VALUE;

				if (outputSources.get(dsName) != null)
				{

					String frequency = ((Float) outputSources.get(dsName)).toString();
					if (frequency.equals("0.0"))
						frequency = "";

					%>
						<tr>
							<td class="formField" width="10%"><input type=checkbox
								property="<%=dsName %>" name="<%=dsName %>" id="<%=dsName %>"
								onclick="checkFrequency(this)" checked></td>
							<td class="formField" width="40%"><%=dsName%></td>
							<td class="formField" width="50%"><input type="text"
								class="formFieldSized10" name="<%=frequencyValue%>"
								id="<%=frequencyValue%>" onkeypress="intOnly(this);"
								onchange="intOnly(this);" onkeyup="intOnly(this);"
								value="<%=frequency%>"></td>
						</tr>
						<%}
				else
				{

					%>
						<tr>
							<td class="formField" width="10%"><input type=checkbox
								property="<%=dsName %>" name="<%=dsName %>" id="<%=dsName %>"
								onclick="checkFrequency(this)"></td>
							<td class="formField" width="40%"><%=dsName%></td>
							<td class="formField" width="50%"><input type="text"
								class="formFieldSized10" name="<%=frequencyValue%>"
								id="<%=frequencyValue%>" disabled onkeypress="intOnly(this);"
								onchange="intOnly(this);" onkeyup="intOnly(this);"></td>
						</tr>

						<%}
			}

		%>
					</table>
					</DIV>
					</td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td align="right" colspan="3"><!-- action buttons begins -->
			<table cellpadding="10" cellspacing="0" border="0">
				<tr>
					<td><html:submit styleClass="actionButton">
						<bean:message key="buttons.search" />
					</html:submit></td>
					<td><html:button styleClass="actionButton" property=""
						onclick="submitForm('AdvancedSearchSelectPath.do')" disabled="true">
						<bean:message key="buttons.selectPath" />
					</html:button></td>
				</tr>
			</table>
			<!-- action buttons end --></td>
		</tr>
		<tr>
			<td></td>
		</tr>
		<input type="hidden" name="targetAction">
	</table>
</html:form>
<!-- Content of page -->
<!-- GeneConnect Advanced Search page -->
