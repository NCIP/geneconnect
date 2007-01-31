<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>

		<table summary="" cellpadding="0" cellspacing="0" border="0" height="20">
            <tr>
              <td width="1"><!-- anchor to skip main menu -->
              	<a href="#content"><img src="images/shim.gif" alt="Skip Menu" width="1" height="1" border="0" /></a>
              </td>
             
              <!-- link 1 begins -->
			  
			  <td><img src="images/mainMenuSeparator.gif" width="1" height="16" alt="" /></td>
			  <td height="20" class="mainMenuItem" onmouseover="changeMenuStyle(this,'mainMenuItemOver'),showCursor()"
				onmouseout="changeMenuStyle(this,'mainMenuItemOver'),hideCursor()" onclick="document.location.href='Home.do'">
				<html:link styleClass="mainMenuLink" page="/Home.do">
					<bean:message key="app.home" />
				</html:link>
			  </td>
			  <td><img src="images/mainMenuSeparator.gif" width="1" height="16" alt="" /></td>
			 
			  <td height="20" class="mainMenuItem" onmouseover="changeMenuStyle(this,'mainMenuItemOver'),showCursor()"
				onmouseout="changeMenuStyle(this,'mainMenuItem'),hideCursor()" onclick="document.location.href='Home.do'">
				<html:link styleClass="mainMenuLink" page="/Help.do">
					<bean:message key="app.help" />
				</html:link>
			  </td>
			  
			  <td><img src="images/mainMenuSeparator.gif" width="1" height="16" alt="" /></td>
			  
			</tr>
          </table>