<!-- GeneConnect Simple Search page -->

<!-- TagLibs -->
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/core-jstl" prefix="c"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
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
%>
<!-- Imports -->
<% 
		List dataSourceList = (List)request.getAttribute(GCConstants.DATA_SOURCES_KEY);
		
		Object obj = request.getAttribute("simpleSearchForm");
		int noOfRows=0;
		Map map = null;

		if(obj != null && obj instanceof SimpleSearchForm)
		{
			SimpleSearchForm form = (SimpleSearchForm)obj;
			map = form.getValues();
			noOfRows = form.getCounter();
		}
%>
<head>
<!-- Css and Scripts -->
<script language="JavaScript" type="text/javascript" src="jss/javaScript.js"></script>
<script>

		//Add More functionality
		function insRow(subdivtag)
		{
		
			var val = parseInt(document.forms[0].counter.value);
			
			
			val = val + 1;
			if(val>10)
			{
				
			}	
			document.forms[0].counter.value = val;
			
			var r = new Array(); 
			
			r = document.getElementById(subdivtag).rows;
			
			var q = r.length;
			var x=document.getElementById(subdivtag).insertRow(q);
			
			// First Cell
			var spreqno=x.insertCell(0);
			spreqno.className="formSerialNumberField";
			sname=(q+1);
			var identifier = "value(Input:" + (q+1) +"_systemIdentifier)";
			sname = sname + "<input type='hidden' name='" + identifier + "' value='' id='" + identifier + "'>";
			spreqno.innerHTML="" + sname;

			//Second Cell
			
			var spreqtype=x.insertCell(1);
			
			spreqtype.className="formField";
			sname="";

			var name = "value(Input:" + (q+1) + "_DataSource_Id)";
			sname="<select name='" + name + "' size='1' class='formFieldSized15' id='" + name + "'>";
			<%
				if(dataSourceList!=null)
				{
					Iterator iterator = dataSourceList.iterator();
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
		
			name = "value(Input:" + (q+1) + "_Genomic_Id)";
			sname= "";
			sname="<input type='text' name='" + name + "' size='30' maxlength='50'  class='formFieldSized15' id='" + name + "'>";
			spreqsubtype.innerHTML="" + sname;
		}
		
		function textLimit(field) 
		{
			if(field.value.length>0) 
				field.value = field.value.replace(/[^\d]+/g, '');
				
			/*if (element.value.length > maxlen + 1)
				alert('your input has been truncated!');*/
			/*if (field.value.length > maxlen)
			{
				//field.value = field.value.substring(0, maxlen);
				field.value = field.value.replace(/[^\d]+/g, '');
			}*/
		}
		function intOnly(field) 
		{
			if(field.value.length>0) 
			{
				field.value = field.value.replace(/[^\d]+/g, ''); 
			}
		}


</script>
</head>
<html:errors />
<html:messages id="messageKey" message="true" header="messages.header" footer="messages.footer">
	<%=messageKey%>
</html:messages>


<!-- Css and Scripts -->


<!-- Displays Title -->
<table summary="" cellpadding="0" cellspacing="0" border="0" width="100%" height="6%">
	<tr height="5%">
		 <td  width="100%">
			<br>
		</td>
	</tr>	
	<tr height="5%">
		<td class="formTitle" width="100%">
			<bean:message key="simpleSearch.title"/>
		</td>
	</tr>
	<tr height="5%">
		 <td width="100%">
			<br>
		 </td>
	</tr>	
</table>
<!-- Content of page -->
<html:form action="SimpleSearch.do?targetAction=search">
<table summary="" cellpadding="0" cellspacing="0" border="0" class="contentPage" width="800">
		<tr>
			<td valign="top" halign="Left">
				<table summary="" cellpadding="1" cellspacing="0" border="0" width="400">
					 <tr>
						 <td><html:hidden property="counter"/></td>
					 </tr>
				 	<tr>
				    	 <!-- Input data source title -->
				    	 <td class="formTitle" height="20" colspan="2">
    						<bean:message key="simpleSearch.input"/>
						</td>
					     <td class="formButtonField">
							<html:button property="addKeyValue" styleClass="actionButton" onclick="insRow('addMore')">
								<bean:message key="buttons.addMore"/>
							</html:button>
							<html:button property="deleteValue" styleClass="actionButton" onclick="deleteChecked('addMore','Participant.do&pageOf=pageOfParticipant&status=true',document.forms[0].counter,'chk_',false)"  disabled="false">
								<bean:message key="buttons.pasteID"/>
							</html:button>
					    </td>
					  </tr>
			  		  <tr>
						 	<td class="formSerialNumberLabel" width="5">
					     	#
						    </td>
				    		<td class="formLeftSubTableTitle">
								<bean:message key="simple.input.dataSource"/>
							</td>
						    <td class="formRightSubTableTitle">
								<bean:message key="simple.input.id"/>
							</td>
					 </tr>
		
					 <tbody id="addMore">
						<%
							for(int i=1;i<=noOfRows;i++)
							{
								String dataSourceName = "value(Input:"+i+"_DataSource_Id)";
								String genomicId = "value(Input:"+i+"_Genomic_Id)";
								String identifier = "value(Input:" + i +"_systemIdentifier)";
								String check = "chk_"+i;
						%>
						 <tr>
				 			<td class="formSerialNumberField" width="5"><%=i%>.
						 		<html:hidden property="<%=identifier%>" />
						 	</td>
						    <td class="formField">
								<html:select property="<%=dataSourceName%>" styleClass="formFieldSized15" styleId="<%=dataSourceName%>" size="1" disabled="false">
									<html:options collection="<%=GCConstants.DATA_SOURCES_KEY%>" labelProperty="name" property="value"/>		
								</html:select>
							</td>
			 				<td class="formField">
						     	<html:text styleClass="formFieldSized15" maxlength="50" size="30" styleId="<%=genomicId%>" property="<%=genomicId%>" />
						    </td>
						 </tr>
						 <%
							}
						 %>
					 </tbody>
				</table>
			</td>
			<td valign="top" halign="right">
				<table summary="" cellpadding="3" cellspacing="0" border="1" width="300">
					 <tr>
					    <td class="formTitle" height="20" colspan="5">
    						<bean:message key="simpleSearch.output"/>
						</td>
					  </tr>
					  <%
						for(int i=0;i<dataSourceList.size();i++)
						{
										
							NameValueBean bean =(NameValueBean)dataSourceList.get(i);
							String check = "value(Output:DataSource_Id_"+bean.getValue()+")";
							String dsName = bean.getName();
					  %>
					 <tr>
						 <td class="formField" width="5">
							<input type=checkbox property="<%=check %>" name="<%=check %>" id="<%=check %>">		
						 </td>
						 <td class="formField" width="5" colspan="3">
							<%=dsName%>
		 				 </td>
				  	</tr>
					<%
						}
					%>
			</table>	
		</td>
	</tr>	  
	<tr>
		<td align="right" colspan="3">
			<!-- action buttons begins -->
			<table cellpadding="10" cellspacing="0" border="0">
				<tr>
					<td>
						<html:submit styleClass="actionButton">
							<bean:message  key="buttons.search" />
						</html:submit>
					</td>
					<td>
						<html:reset styleClass="actionButton" >
							<bean:message  key="buttons.reset" />
						</html:reset>
					</td>
				</tr>
			</table>
			<!-- action buttons end -->
		</td>
	</tr>
	<input type="hidden" name="targetAction">
	</table>
	</html:form>
<!-- GeneConnect Simple Search page -->