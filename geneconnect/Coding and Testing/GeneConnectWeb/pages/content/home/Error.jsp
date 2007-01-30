<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<br>
  <table width="700" border="0" cellspacing="0" cellpadding="0" align="center">
    

    <tr>
      <td>&nbsp;</td>
     </tr>
      <tr>
           <td>&nbsp;</td>
       </tr>
      
       <%
       	String sessionInvalidate = (String)request.getAttribute(edu.wustl.geneconnect.util.global.GCConstants.SESSION_INVALIDATE);
       if(sessionInvalidate!=null)
       {
       %>
        <tr>
       	  <td align="center">
            <b><bean:message key="errors.invalid.session" /></b>
            <BR>
          </td>
        </tr>
       <%
       }
       else
       {
       %>
        <tr>
          <td align="center">
            <b>The server has encountered a severe error.&nbsp;&nbsp;Please try again.</b>
            <BR>
          </td>
        </tr>
        <tr>
          <td align="center">
      	    
           	 <html:link action="viewErrorDetails" >
					View Error Details
		  	 </html:link>
		   </td>
        </tr>
		<%
       }
		%>

  </table>