<%--L
  Copyright Washington University at St. Louis

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/geneconnect/LICENSE.txt for details.
L--%>

<!-- dataList and columnList are to be set in the main JSP file -->
<link rel="STYLESHEET" type="text/css" href="dhtml_comp/css/dhtmlXGrid.css"/>
	<script  src="dhtml_comp/js/dhtmlXCommon.js"></script>
	<script  src="dhtml_comp/js/dhtmlXGrid.js"></script>		
	<script  src="dhtml_comp/js/dhtmlXGridCell.js"></script>	
	<script  src="dhtml_comp/js/dhtmlXGrid_mcol.js"></script>	
<script>
var myData={};
<%if(dataList != null && dataList.size() != 0)
{
%>
myData = [<%int i;%><%for (i=0;i<(dataList.size()-1);i++){%>
<%
HashMap setMap = (HashMap)dataList.get(i);
List row =new ArrayList();	
int setI=0;
for(int k=0;k<columnList.size();k++)
{
	String colName= (String)columnList.get(k);
	String colValue = (String)setMap.get(columnList.get(k));
	if(colName.indexOf(edu.wustl.geneconnect.util.global.GCConstants.SET_ID_KEY)>=0)
	{
		setI=k;	
	}
	if(colValue==null)
	{
		row.add("");
	}
	else
	{
		row.add(colValue);
	}	
}
int j;
%>
<%="\""%><%for (j=0;j < (row.size()-1);j++){%><%=row.get(j)%>,<%}%><%=row.get(j)%><%=",<a href='javascript:"%><%="disp("%>\"<%=row.get(setI)+"#"+row.get(queryKeyCol)%>\"<%=");'><img src='images/mag1.GIF'border='0'/></a>\""%>,<%}%>
<%
	// prepare the column heasder and data to display
	// the column name is string where each column is seperated by ','
	// dats list is a array of string where each field is separeated by ','.
	HashMap setMap = (HashMap)dataList.get(i);
	List row =new ArrayList();	
	int setI=0;
	for(int k=0;k<columnList.size();k++)
	{
		String colName= (String)columnList.get(k);
		String colValue = (String)setMap.get(columnList.get(k));
		if(colName.indexOf(edu.wustl.geneconnect.util.global.GCConstants.SET_ID_KEY)>=0)
		{
			setI=k;	
		}
		if(colValue==null)
		{
			row.add("");
		}
		else
		{
			row.add(colValue);
		}	
	}
  	int j;
  	
%>
<%="\""%><%for (j=0;j < (row.size()-1);j++){%><%=row.get(j)%>,<%}%><%=row.get(j)%><%=",<a href='javascript:"%><%="disp("%>\"<%=row.get(setI)+"#"+row.get(queryKeyCol)%>\"<%=");'><img src='images/mag1.GIF'border='0'/></a>\""%>
];
<%
}
%>
// function to show the graph widow
function disp(q)
{
	var tokens = q.split("#");
	var id=tokens[0];
	var queryKey=tokens[1];
	// has to modified the key because for display purpose the delimenters of input identifier is changed to '|' instaead of ','.
	// the actual key is input1=AAA=aaa,BBB=bbb
	var modifiedqueryKey=queryKey.replace("|",",");
	var url = ".."+"<%=request.getContextPath()%>"+"/GeneConnectGraph.do?setid="+id+"&queryKey="+modifiedqueryKey;
	newwindow=window.open(url,'name','height=750,width=545,left=20, top=0, screenX=20, screenY=0' );
	if (window.focus) {newwindow.focus()}
}
// column names
var columns = <%="\""%><%int col;%><%for(col=0;col<(columnList.size()-1);col++){%><%=columnList.get(col)%>,<%}%><%=columnList.get(col)%><%=",Paths\""%>;
// width of each column
var colWidth = <%="\""%><%for(col=0;col<(columnList.size()-1);col++){String colValue = (String)columnList.get(col);%><%=Utility.getGridColumnWidht(colValue,isConfidenceChecked,isFrequencyChecked)%>,<%}%><%=Utility.getGridColumnWidht((String)columnList.get(col),isConfidenceChecked,isFrequencyChecked)%><%=",100\""%>;
// data stype fo each column
var colTypes = <%="\""%><%for(col=0;col<(columnList.size()-1);col++){String colValue = (String)columnList.get(col);%><%=Utility.getGridColumnType(colValue)%>,<%}%><%=Utility.getGridColumnType((String)columnList.get(col))%><%=",link\""%>;
// column index containong frequency values.
var freqcol = [<%for(col=0;col<(freqColumns.size()-1);col++){%><%="\""%><%=freqColumns.get(col)%><%="\""%>,<%}if(freqColumns.size()>0){%><%="\""%><%=freqColumns.get(col)%><%="\""%><%}%>];
// column index containg confidence values
var confcol = [<%for(col=0;col<(confColumns.size()-1);col++){%><%="\""%><%=confColumns.get(col)%><%="\""%>,<%}%><%="\""%><%=confColumns.get(col)%><%="\""%>];
</script>

<table width="100%">
	
	<tr>
		<td>
			<div id="gridbox" width="100%" height="300px" style="background-color:white;overflow:hidden"></div>
		</td>
	</tr>

</table>
<script>
	
	// on sort send a reqest to server column_index,column_name and order
	function on_before_sort(column_index,type,order,art)
	{
		if(column_index==<%=columnList.size()%>)
		{
			return true;
		}
			var s=mygrid.getHeaderCol(column_index);
			var colname = s.substring(s.indexOf(">")+1,s.indexOf("</"));
			document.forms[0].sortedColumnIndex.value=column_index;
			document.forms[0].sortedColumn.value=colname;	
			document.forms[0].sortedColumnDirection.value=order;	
			var action = "SearchResultView.do?pageOf=advancedSearch&isPaging=true&isSorting=true";
			document.forms[0].action = action;
			document.forms[0].submit();
			return true;
	}
	
	function init_grid()
	{
		mygrid = new dhtmlXGridObject('gridbox');
		mygrid.setImagePath("dhtml_comp/imgs/");
		mygrid.setHeader(columns);
		mygrid.setEditable("FALSE");
		mygrid.setSkin("xp");
		mygrid.setInitWidths(colWidth);
		mygrid.setColSorting(colTypes);
		mygrid.init();
	
		mygrid.setOnColumnSort(on_before_sort);
		
		function f1()
		{
			mygrid.setSizes();
		}
		mygrid.setOnResize(f1);
	
		// set sorting order image on column
	<%
		if(sortedColumn!=null&&sortedColumnDirection!=null&&sortedColumnIndex!=null)
		{
	%>
		mygrid.setSortImgState(true,"<%=sortedColumnIndex%>","<%=sortedColumnDirection.toUpperCase()%>")
	<%		
		}
	%>
		// add data row in grid
		for(var row=0;row<myData.length;row++)
		{
			mygrid.addRow(row+1,myData[row],row+1);
		}
		mygrid.setSizes();
	}
	window.onload=init_grid;
</script>
