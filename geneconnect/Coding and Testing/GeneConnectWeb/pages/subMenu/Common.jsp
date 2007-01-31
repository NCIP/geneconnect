<!-- Sachin Lale -->
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>

<table summary="" cellpadding="0" cellspacing="0" border="0" width="190" height="100%">
  <tiles:insert attribute="submenu"></tiles:insert>            
   
  <tr>
    <td class="subMenuPrimaryTitle" height="21">
    	<!--<bean:message key="app.quickLinks" />-->
   
    	<a href="#content">
    		<img src="images/shim.gif" alt="Skip Menu" width="1" height="1" border="0" />
    	</a>
    </td>
  </tr>
  <tr>
  	<td class="subMenuSecondaryTitle" onmouseover="changeMenuStyle(this,'subMenuSecondaryTitleOver'),showCursor()" onmouseout="changeMenuStyle(this,'subMenuSecondaryTitle'),hideCursor()" height="20" onclick="document.location.href='SimpleSearch.do'">
		<a class="subMenuSecondary" href="SimpleSearch.do">
			<bean:message key="app.simpleSearch" />
		</a>
	</td>
  </tr>
  <tr>
  	<td class="subMenuSecondaryTitle" onmouseover="changeMenuStyle(this,'subMenuSecondaryTitleOver'),showCursor()" onmouseout="changeMenuStyle(this,'subMenuSecondaryTitle'),hideCursor()" height="20" onclick="document.location.href='AdvancedSearch.do?targetAction=populate'">
		<a class="subMenuSecondary" href="AdvancedSearch.do?targetAction=populate">
			<bean:message key="app.advancedSearch" />
		</a>
	</td>
  </tr>
  <tr>
    <td class="subMenuPrimaryTitle" height="21">
    	<bean:message key="app.quickLinks" />
   
    	<a href="#content">
    		<img src="images/shim.gif" alt="Skip Menu" width="1" height="1" border="0" />
    	</a>
    </td>
  </tr>
  <tr>
  	<td class="subMenuSecondaryTitle" onmouseover="changeMenuStyle(this,'subMenuSecondaryTitleOver'),showCursor()" onmouseout="changeMenuStyle(this,'subMenuSecondaryTitle'),hideCursor()" height="20" onclick="document.location.href='http://cabig.nci.nih.gov/'">
		<a class="subMenuSecondary" href="http://cabig.nci.nih.gov/">
			<bean:message key="app.cabigHome" />
		</a>
	</td>
  </tr>
  
  <tr>
  	<td class="subMenuSecondaryTitle" onmouseover="changeMenuStyle(this,'subMenuSecondaryTitleOver'),showCursor()" onmouseout="changeMenuStyle(this,'subMenuSecondaryTitle'),hideCursor()" height="20" onclick="document.location.href='http://ncicb.nci.nih.gov/'">
  		<a class="subMenuSecondary" href="http://ncicb.nci.nih.gov/">
  			<bean:message key="app.ncicbHome" />
  		</a>
  	</td>
  </tr>
  
  <tr>
  	<td class="subMenuSecondaryTitle" onmouseover="changeMenuStyle(this,'subMenuSecondaryTitleOver'),showCursor()" onmouseout="changeMenuStyle(this,'subMenuSecondaryTitle'),hideCursor()" height="20" onclick="document.location.href='#'">
  		<a class="subMenuSecondary" href="http://bioinformatics.wustl.edu">
  			<bean:message key="app.siteHome" />
  		</a>
  	</td>
  </tr>
 
  <tr>
  	<td class="subMenuFill" height="100%">
  		&nbsp;
  	</td>
  </tr>
  <tr>
  	<td class="subMenuFooter" height="22">
  		&nbsp;
  	</td>
  </tr>
</table>