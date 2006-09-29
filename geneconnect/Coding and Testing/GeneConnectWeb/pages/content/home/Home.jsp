<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ page import="edu.wustl.common.util.global.ApplicationProperties,edu.wustl.common.beans.SessionDataBean;"%>

<table summary="" cellpadding="0" cellspacing="0" border="0" width="100%" height="100%">
    <tr>
      <td height="100%">
        <!-- target of anchor to skip menus --><a name="content" />
        <table summary="" cellpadding="0" cellspacing="0" border="0" height="100%">
          <tr>
            <td>
            
              <!-- welcome begins -->
              <table summary="" cellpadding="0" cellspacing="0" border="0" width="804" height="100%">
                <tr>
                	<td class="welcomeTitle" height="20" width="100%">
                		<bean:message key="app.welcomeNote" arg0="<%=ApplicationProperties.getValue("app.name")%>"
															arg1="<%=ApplicationProperties.getValue("app.version")%>"/>
					</td>
                </tr>
                <tr>
                	<td>
                  		<!--img src="images/HomeImage.jpg" alt="ApplicationProperties.getValue("app.name")" border="0" /-->
                  </td>
                </tr>
              </table>
              <!-- welcome ends -->
            
            </td>
            <td valign="top" halign="right">
              
              <!-- sidebar begins -->
              
              <!-- sidebar ends -->
              
            </td>
          </tr>
        </table>
      </td>
    </tr>
</table>