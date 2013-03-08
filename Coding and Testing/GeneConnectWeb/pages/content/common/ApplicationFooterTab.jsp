<%--L
  Copyright Washington University at St. Louis

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/geneconnect/LICENSE.txt for details.
L--%>

<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<%
	//String content = (String)request.getParameter("CONTENTS");
	//String pageName = (String)request.getAttribute("PAGE_TITLE");
%>

<table summary="" cellpadding="0" cellspacing="0" border="0" class="contentPage" width="800">
	
   	 <tr>
	    <td>
	 	 <table summary="" cellpadding="3" cellspacing="0" border="0">
	 	   	<tr>
	 	   		<td>&nbsp;&nbsp;</td>
	 	   	</tr> 
	 	   	 
		 	<tr>
		     	<td class="formTitle" height="20" colspan="3">
		     		<bean:message key="app.contactUs"/>
		     	</td>
		 	</tr>
		 
		 	<tr>
		 		<td>&nbsp;&nbsp;</td>
		 	</tr>
		 	
		 	<tr> 
            	<td colspan="3" class="formMessage">
            		<font color="#000000" size="2" face="Verdana">
            		<p>		
            			Please send questions or comments to the E-mail addresses listed below:
					</p>
					<p>
						<b>E-mail:</b> <a href="mailto:geneconnect_bugs@mga.wustl.edu">geneconnect_bugs@mga.wustl.edu</a>
					</p>	
            			
            		</font>
            	</td>
         	</tr>
           </table>
		 </td>
	 </tr>
</table>