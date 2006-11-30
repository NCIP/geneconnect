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

	Map graphHighlightPaths = (HashMap)request.getAttribute(GCConstants.GRAPH_HIGHLIGHT_PATHS);

	System.out.println("ontMap size-"+graphHighlightPaths.size());

%>
<html>
  <head>
  <title>GeneConnect Graph</title>
  </head>
  <body>
    <applet code="edu.wustl.geneconnect.graph.GeneConnectChart.class" 
			codebase="http://<%=request.getServerName()%>:<%=request.getServerPort()%><%=request.getContextPath()%>/graph"
            archive="graph.jar"
            width="530" height="600">
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

			for(int i=0; i<(graphHighlightPaths.size()/2); i++)
			{
				highlightNodeList = "highlightNodeList_"+ (i+1);
				
				highlightLinkTypes = "highlightLinkTypes_"+(i+1);

		%>
		<PARAM name="<%=highlightNodeList%>" value="<%=graphHighlightPaths.get(highlightNodeList)%>" >
		<PARAM name="<%=highlightLinkTypes%>" value="<%=graphHighlightPaths.get(highlightLinkTypes)%>" >
		<%
			}
		%>


    </applet>
  </body>
</html>
<!-- GeneConnect Advanced Search page -->