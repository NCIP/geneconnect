<%--L
  Copyright Washington University at St. Louis

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/geneconnect/LICENSE.txt for details.
L--%>

<!-- GeneConnect Advanced Search page -->
<!-- Imports -->
<%@
      page
      language="java"
      contentType="text/html"
      import="java.util.*"
	  import="edu.wustl.geneconnect.actionForm.AdvancedSearchForm"
	  import="edu.wustl.geneconnect.util.DisplayInformationInterface"
	  import ="edu.wustl.common.beans.NameValueBean"
	  import ="edu.wustl.common.util.Utility"
	  import="edu.wustl.geneconnect.util.global.GCConstants"
%>
<!-- Imports -->
<% 

	List graphDataSources = (ArrayList)request.getAttribute(GCConstants.GRAPH_DATASOURCES);
	
	List graphDataSourcesLinks = (ArrayList)request.getAttribute(GCConstants.GRAPH_DATASOURCES_LINKS);

	Map graphHighlightPaths = (TreeMap)request.getAttribute(GCConstants.GRAPH_HIGHLIGHT_PATHS);

	Map graphHighlightPathsCount  = (HashMap) request.getAttribute(GCConstants.GRAPH_HIGHLIGHT_PATHS_COUNTS);

%>
<html>
  <head>
  <title>GeneConnect Graph</title>
  </head>
  <body onResize="resize()" onLoad="resize()" topmargin="0" leftmargin="0" marginwidth="0" marginheight="0">
	<SCRIPT LANGUAGE="JavaScript">
   function resize() {
    if (navigator.appName.indexOf("Microsoft") != -1) {
        width=document.body.clientWidth;
        height=document.body.clientHeight;
    } else {
        var netscapeScrollWidth=15;
        width=window.innerWidth - netscapeScrollWidth;
        height=window.innerHeight - netscapeScrollWidth;
    }
    document.graphApplet.width = width;
    document.graphApplet.height = height;
    window.scroll(0,0);
	}
	window.onResize = resize;
	window.onLoad = resize;
   </SCRIPT>

    <applet name="graphApplet" code="edu.wustl.geneconnect.graph.GeneConnectChart.class" 
			codebase="http://<%=request.getServerName()%>:<%=request.getServerPort()%><%=request.getContextPath()%>/graph"
            archive="graph.jar"
            width="530" height="700">
		<PARAM name="Message" value="">
		<PARAM name="java_code" value="edu.wustl.geneconnect.graph.GeneConnectChart.class">
		<PARAM name="java_archive" value="jung-1.7.4.jar, colt.jar, commons-collections-3.1.jar">
        <PARAM name="cache_archive" value="jung-1.7.4.jar, colt.jar, commons-collections-3.1.jar">

		<PARAM name="noOfDatasources" value="<%=graphDataSources.size()%>">
		<%
			String dataSourceParam;
			for(int i=0; i<graphDataSources.size(); i++)
			{
				dataSourceParam = "DataSource_"+ (i+1);
		%>
		<PARAM name="<%=dataSourceParam%>" value="<%=graphDataSources.get(i)%>" >
		<%
			}
		%>

		<PARAM name="noOfDatasourcesLinks" value="<%=graphDataSourcesLinks.size()%>">
		<%
			String dataSourceLinkParam;
			for(int i=0; i<graphDataSourcesLinks.size(); i++)
			{
				dataSourceLinkParam = "DataSourceLink_"+ (i+1);
		%>
		<PARAM name="<%=dataSourceLinkParam%>" value="<%=graphDataSourcesLinks.get(i)%>" >
		<%
			}
		%>

		<PARAM name="noOfHighlightPaths" value="<%=(graphHighlightPaths.size()/2)%>">
		<%
			String highlightNodeList;
			String highlightLinkTypes;
			String pathCount;
			String pathCountKey;
			Integer pathCountValue;

			for(int i=0; i<(graphHighlightPaths.size()/2); i++)
			{
				highlightNodeList = "highlightNodeList_"+ (i+1);
				
				highlightLinkTypes = "highlightLinkTypes_"+(i+1);

				pathCountKey = graphHighlightPaths.get(highlightNodeList) + "=" + graphHighlightPaths.get(highlightLinkTypes);

				pathCount = "highlightPathCount_"+ (i+1);

				if( graphHighlightPathsCount != null)
					pathCountValue = (Integer)graphHighlightPathsCount.get(pathCountKey);
				else
					pathCountValue = new Integer(0);
		%>
		<PARAM name="<%=highlightNodeList%>" value="<%=graphHighlightPaths.get(highlightNodeList)%>" >
		<PARAM name="<%=highlightLinkTypes%>" value="<%=graphHighlightPaths.get(highlightLinkTypes)%>" >
		<PARAM name="<%=pathCount%>" value="<%=pathCountValue%>" >
		<%
			}
		%>


    </applet>
  </body>
</html>
<!-- GeneConnect Advanced Search page -->