<!-- GeneConnect Simple Search page -->

<!-- TagLibs -->
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/core-jstl" prefix="c"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>

<!-- Taglibs -->

<!-- Imports -->
<%@
      page language="java" contentType="text/html"
	import="java.util.*"
	import="edu.wustl.geneconnect.actionForm.SimpleSearchForm"
	import="edu.wustl.geneconnect.util.DisplayInformationInterface"
	import="edu.wustl.common.beans.NameValueBean"
	import="edu.wustl.common.util.Utility"
	import="edu.wustl.common.util.global.Constants"
	import="edu.wustl.geneconnect.util.global.GCConstants"%>
<!-- Imports -->
<%List dataSourceList = (List) request.getAttribute(GCConstants.DATA_SOURCES_KEY);

			Object obj = request.getAttribute("simpleSearchForm");
			int noOfRows = 0;
			Map map = null;
			String targetAction = "";
			
			if (obj != null && obj instanceof SimpleSearchForm)
			{
				SimpleSearchForm form = (SimpleSearchForm) obj;
				map = form.getValues();
				noOfRows = form.getCounter();
			
				targetAction=form.getTargetAction();

			}
%>
<head>
<!-- Css and Scripts -->
<script language="JavaScript" type="text/javascript"
	src="jss/javaScript.js"></script>

<script src="jss/script.js"></script>

<script>
		var currentTextbox;
		var seletedInputDataSource = new Array(<%=noOfRows%>);
		var lasttrid=1;
		//Add More functionality
		function insRow(subdivtag)
		{
			disableAllOutputDataSource()
			
			var val = parseInt(document.forms[0].counter.value);
			
			
			val = val + 1;
			
			document.forms[0].counter.value = val;
			var r = new Array(); 
			
			r = document.getElementById(subdivtag).rows;
			
			var q = r.length;
			if(q==10)
			{
				alert('The Number Input data source is limited to 10');
				return;
			}	
			var x=document.getElementById(subdivtag).insertRow(q);
			lasttrid=lasttrid+1;
			x.id="tr_"+(lasttrid)
			// First Cell
			var spreqno=x.insertCell(0);
			spreqno.className="formField";
		//	sname=(q+1);
			var identifier = "value(Input:" + lasttrid +"_systemIdentifier)";
			sname="";
			sname = sname + "<input type='checkbox' name='chk_" + (q+1) + "' id='chk_" + lasttrid + "'/>";
			spreqno.innerHTML="" + sname;

			//Second Cell
			
			var spreqtype=x.insertCell(1);
			spreqtype.className="formField";
			
			sname = sname + "<input type='hidden' name='" + identifier + "' value='' id='" + identifier + "' />";
			var name = "value(Input:" + lasttrid + "_DataSource_Id)";
			
			sname="<select name='" + name + "' size='1' class='formFieldSized15' id='" + name + "' onchange=disableOutputDataSource(this,"+val+")>";
			<%
				if(dataSourceList!=null)
				{
					Iterator iterator = dataSourceList.iterator();
			%>		
					sname = sname + "<option value='-1'><%=Constants.SELECT_OPTION%></option>";
			<%		
					while(iterator.hasNext())
					{
						NameValueBean bean = (NameValueBean)iterator.next();
			%>
						
						sname = sname + "<option value='<%=bean.getValue()%>'><%=bean.getName()%></option>";
			<%		}
				}
			%>
			sname = sname + "</select>";
			spreqtype.innerHTML="" + sname;
			
			//Third Cellvalue(ParticipantMedicalIdentifier:1_medicalRecordNumber)
			var spreqsubtype=x.insertCell(2);
			spreqsubtype.className="formField";
			sname="";
		
			name = "value(Input:" + lasttrid + "_Genomic_Id)";
			sname= "";
			sname="<input type='text' name='" + name + "' size='30' maxlength='50'  class='formFieldSized15' id='" + name + "' onfocus='setCurrentTextBox(this)'>";
			spreqsubtype.innerHTML="" + sname;
		}
		
		//validate int field
		function intOnly(field) 
		{
			if(field.value.length>0) 
			{
				field.value = field.value.replace(/[^\d]+/g, ''); 
			}
		}
		// check all output data source
		function checkAll(element)
		{			
			var checkbox2=element;
			if(checkbox2.checked==true)
			{
				alert("Please be noted alignment based data is not available. Thus selecting all data sources may lead to empty results.");
			}
			
			var checkBox;
			<%
				for(int i=0;i<dataSourceList.size();i++)
				{
					NameValueBean bean =(NameValueBean)dataSourceList.get(i);
					String check = "value(Output:DataSource_Id_"+bean.getValue()+")";
			%>	
				checkBox = document.getElementById("<%=check%>");
				if(checkbox2.checked==true)
				{
					if(checkBox.disabled==false)
						checkBox.checked=true;
				}
				else
				{
					checkBox.checked=false;
				}	
			<%		
				}
			%>		
		
		}
		// function to disable selected ouput data source which is selected as input 
		function disableOutputDataSource(ele,i)
		{
			//alert("In disableOutputDataSource()...");

			if(ele.options[ele.selectedIndex].value == "-- Select --" || ele.options[ele.selectedIndex].value == "-1")
			{
				disableAllOutputDataSource();

				seletedInputDataSource[i]=null;
			}
			else
			{
				var checkBox;
				<%
					for(int i=0;i<dataSourceList.size();i++)
					{
						NameValueBean bean =(NameValueBean)dataSourceList.get(i);
						String check = "value(Output:DataSource_Id_"+bean.getValue()+")";
				%>	
						checkBox = document.getElementById("<%=check%>");
					
						checkBox.disabled = false;
				<%		
					}
				%>		
	
				var chk;
				
				if(seletedInputDataSource[i]!=null)
				{
					chk = "value(Output:DataSource_Id_"+seletedInputDataSource[i]+")";
					checkBox = document.getElementById(chk);
					if(checkBox!=null)
					{
						checkBox.disabled=false;
					}	
					
				}
				seletedInputDataSource[i]=ele.options[ele.selectedIndex].value;
				var j=1;
				for(j=1;j<=seletedInputDataSource.length;j++)
				{
					if(seletedInputDataSource[j]!=null)
					{
						chk = "value(Output:DataSource_Id_"+seletedInputDataSource[j]+")";
						checkBox = document.getElementById(chk);
						if(checkBox!=null)
						{
							checkBox.disabled=true;
						}	
					}	
				}
			}
			
			checkAllInputDataSourceValues();
		}
		//paste functionality
		function pasteData()
		{
			if(currentTextbox==null)
			{
				currentTextbox=document.getElementById( "value(Input:1_Genomic_Id)");
			}
			var rows = splitGenomicIds();
			// get row number from currently selected text box id
			var stringObject = currentTextbox.id;
			var ind1 = stringObject.indexOf(":",0);
			var ind2 = stringObject.indexOf("_",ind1);
			var inddiff= ind2-ind1;

			var temp = stringObject.substr(ind1+1,inddiff-1);
			// stores row number from currently selected text box id
			
			var rowNum =parseInt(temp);			
			//var rowCount = document.forms[0].counter.value;
			
			if(rows.length>0)
			{
				var rowCount=0;
				for (var i=0; i<rows.length; i++)
				{
					// set the data from splitted values
					currentTextbox.value=rows[i];
					if(i+1==rows.length)
					{
						break;
					}
					var tbody = document.getElementById("addMore");
					var tbodyRows = tbody.getElementsByTagName("input");
					var isToselect=false;
					var currentRowNum=0;
					
					for(var n=0;n<tbodyRows.length;n++)
					{
						if(tbodyRows[n].id==currentTextbox.id)
						{
							currentRowNum=n;
							break;
							//isToselect=true;
						}
					}
					if(currentRowNum+1==tbodyRows.length)
					{
						insRow('addMore');
					}
					currentTextbox=tbodyRows[currentRowNum+2];
					if(currentTextbox==null)
						return;
				}
			}	
		}
		
		// get data form clipboad=rd and split with \n delimenter
		function splitGenomicIds()
		{
			var copiedData;
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
			if(rows.length<=1)
			{
				// if the length of splitted data is 1 then 
				// split data with delima as \t
				rows = copiedData.split("\t");
				// if the length of splitted data is 1 then 
				// split data with delima as ','
				if(rows.length<=1)
				{
					rows = copiedData.split(",");
				}		
			}
			var formattedRows=new Array();
			var formattedRowCount=0;
			for(var n=0;n<rows.length;n++)
			{
				if(rows[n]!="")
				{
					formattedRows[formattedRowCount]=rows[n];
					formattedRowCount++;
				}
			}
			return formattedRows;
		}
		
		function setCurrentTextBox(t)
		{
			currentTextbox=t;
		}
		// delete the selected input data source
		function deleteInputRow(subdivtag) 
		{
			var divObj=document.getElementById(subdivtag);
			var rows = new Array(); 
			rows = document.getElementById(subdivtag).rows; 
			var totalrows = rows.length;
			var rowIdCounter = 0;
			var rowIds = new Array();
					for(var i=0; i<totalrows; i++) 
			{
				var checkbox=divObj.rows[i].cells[0].firstChild;

				if(checkbox.checked)
				{
					rowIds[rowIdCounter]=divObj.rows[i];
					rowIdCounter=rowIdCounter+1;
					
				}
			}
			for(var i=0; i<rowIds.length; i++) 
			{
				var rowObject=document.getElementById(rowIds[i].id);
				var combo=rowIds[i].cells[1].firstChild;
				var j=0;
				for(j=1;j<=seletedInputDataSource.length;j++)
				{
					if(combo.options!=null && seletedInputDataSource[j]==combo.options[combo.selectedIndex].value)
					{
//						disableOutputDataSource(combo,j)
						chk = "value(Output:DataSource_Id_"+seletedInputDataSource[j]+")";
						checkBox = document.getElementById(chk);
						checkBox.disabled=false;
						seletedInputDataSource[j]=null;
					}
				}
				divObj.removeChild(rowObject);
				var value = document.forms[0].counter.value
				value=value-1;
				document.forms[0].counter.value=value;
			}
		}

		// function to disable all ouput data sources
		function disableAllOutputDataSource()
		{
			//alert("In disableAllOutputDataSource()...");
			var checkBox;

			checkBox = document.getElementById("checkAll2");
			checkBox.checked = false;

			<%
				for(int i=0;i<dataSourceList.size();i++)
				{
					NameValueBean bean =(NameValueBean)dataSourceList.get(i);
					String check = "value(Output:DataSource_Id_"+bean.getValue()+")";
			%>	
					checkBox = document.getElementById("<%=check%>");
				
					checkBox.checked = false;
					checkBox.disabled = true;
			<%		
				}
			%>		

		}

		function checkAllInputDataSourceValues()
		{
			var divObj=document.getElementById("addMore");
			var rows = new Array(); 
			rows = document.getElementById("addMore").rows; 
			var totalrows = rows.length;
			
			for(var i=0; i<totalrows; i++) 
			{
				var combo=divObj.rows[i].cells[1].firstChild;
				
				if(combo.value == "-- Select --" || combo.value == "-1")
				{
					disableAllOutputDataSource();
				}
				//alert("Input Value-->"+combo.value);
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
			key="simpleSearch.title" /></td>
	</tr>
</table>
<!-- Content of page -->

<html:form action="SimpleSearch.do?targetAction=search">
	<table summary="" cellpadding="0" cellspacing="0" border="0"
		class="contentPage" width="800">
		<tr>
			<td valign="top" halign="Left">
			<table summary="" cellpadding="1" cellspacing="0" border="0"
				width="500">
				<tr>
					<td><html:hidden property="counter" /></td>
				</tr>
				<tr>
<!-- Input data source title -->
					<td class="formTitle" height="20" colspan="2"><bean:message
						key="simpleSearch.input" /></td>
					<td class="formButtonField"><html:button property="addKeyValue"
						styleClass="actionButton" onclick="insRow('addMore')">
						<bean:message key="buttons.addMore" />
					</html:button> <html:button property="delete"
						styleClass="actionButton" onclick="deleteInputRow('addMore')"
						disabled="false">
						<bean:message key="buttons.delete" />
					</html:button> <html:button property="pasteId"
						styleClass="actionButton" onclick="pasteData()" disabled="false">
						<bean:message key="buttons.pasteID" />
					</html:button></td>
				</tr>
				<tr>

					<td class="formLeftSubTableTitle" width="5%"></td>
					<td class="formLeftSubTableTitle"><bean:message
						key="simple.input.dataSource" /></td>
					<td class="formRightSubTableTitle"><bean:message
						key="simple.input.id" /></td>
				</tr>

				<tbody id="addMore">
					<%for (int i = 1; i <= noOfRows; i++)
			{
				String dataSourceName = "value(Input:" + i + "_DataSource_Id)";
				String genomicId = "value(Input:" + i + "_Genomic_Id)";
				String identifier = "value(Input:" + i + "_systemIdentifier)";
				String check = "chk_" + i;
				String trid = "tr_" + i;
				String func = "disableOutputDataSource(this," + i + ")";

				%>
					<tr id="<%=trid%>">
						<td class="formField"><input type="checkbox" id="<%=check%>"
							name="<%=check%>" /></td>
						<td class="formField">
						<html:select property="<%=dataSourceName%>"
							styleClass="formFieldSized15" styleId="<%=dataSourceName%>"
							size="1" disabled="false" onchange="<%=func%>">
							<html:option key="-1" value="<%=Constants.SELECT_OPTION%>" />
							<html:options collection="<%=GCConstants.DATA_SOURCES_KEY%>"
								labelProperty="name" property="value" />
						</html:select><html:hidden property="<%=identifier%>" /></td>
						<td class="formField"><html:text styleClass="formFieldSized15"
							maxlength="50" size="30" styleId="<%=genomicId%>"
							property="<%=genomicId%>" onfocus="setCurrentTextBox(this)" /></td>
					</tr>
					<%}

			%>
				</tbody>

			</table>
			</td>
<!-- Output data source title -->			
			<td valign="top" halign="right">
			<table summary="" cellpadding="3" cellspacing="0" border="1"
				width="275">
				<tr>
					<td class="formTitle" height="20" colspan="2" nowrap><bean:message
						key="simpleSearch.output" /></td>
				</tr>
				<tr>
					<td class="formField" width="20%"><input type='checkbox'
						name='checkAll2' id='checkAll2' onClick='checkAll(this)' /></td>
					<td class="formField" width="80%"><span class="formField"> <bean:message
						key="buttons.checkAll" /> </span></td>
				</tr>

				<%for (int i = 0; i < dataSourceList.size(); i++)
				{

				NameValueBean bean = (NameValueBean) dataSourceList.get(i);
				String check = "value(Output:DataSource_Id_" + bean.getValue() + ")";
				String dsName = bean.getName();

				%>
				<tr>
					<%if(targetAction.equals("search")){ %>
					<td class="formField" width="20%"><input type=checkbox
						property="<%=check %>" name="<%=check %>" id="<%=check %>"></td>
					<%} 
					  else{%>
					<td class="formField" width="20%"><input type=checkbox
						property="<%=check %>" name="<%=check %>" id="<%=check %>" disabled></td>
					<%} %>
					<td class="formField" width="80%"><%=dsName%></td>
				</tr>
				<%}

			%>
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
					<td>
						<html:button property="" styleClass="actionButton" onclick="loadForm('SimpleSearch.do')">
							<bean:message key="buttons.reset" />
						</html:button>
					</td>
				</tr>
			</table>
			<!-- action buttons end --></td>
		</tr>
		<input type="hidden" name="targetAction">
	</table>
</html:form>

<script>
	var i=0;
		for(i=1;i<=<%=noOfRows%>;i++)
		{
			var id ="value(Input:" + i + "_DataSource_Id)";
			//alert("value(Input:" + i + "_DataSource_Id)");
			var combo = document.getElementById(id);
			if(combo!=null)
			{
				//alert(combo.value);
				var checkBox = document.getElementById("value(Output:DataSource_Id_"+combo.value+")");
				if(checkBox!=null)
				{

					seletedInputDataSource[i] = combo.value;
					checkBox.disabled=true;
				}	
				
			}	
		}
	</script>
<!-- GeneConnect Simple Search page -->
