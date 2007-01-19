<!-- dataList and columnList are to be set in the main JSP file -->
	<link rel="STYLESHEET" type="text/css" href="dhtml_comp/css/dhtmlXGrid.css"/>
	<script  src="dhtml_comp/js/dhtmlXCommon.js"></script>
	<script  src="dhtml_comp/js/dhtmlXGrid.js"></script>		
	<script  src="dhtml_comp/js/dhtmlXGridCell.js"></script>	
	<script  src="dhtml_comp/js/dhtmlXGrid_pgn.js"></script>	
	


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
function disp(q)
{
	var tokens = q.split("#");
	var id=tokens[0];
	var queryKey=tokens[1];
	var url = ".."+"<%=request.getContextPath()%>"+"/GeneConnectGraph.do?setid="+id+"&queryKey="+queryKey;
//	alert(url);
//	newwindow=window.open(url,'name','height=600,width=540');
	newwindow=window.open(url,'name','height=750,width=545,left=20, top=0, screenX=20, screenY=0' );
	if (window.focus) {newwindow.focus()}
}
var columns = <%="\""%><%int col;%><%for(col=0;col<(columnList.size()-1);col++){%><%=columnList.get(col)%>,<%}%><%=columnList.get(col)%><%=",Paths\""%>;

var colWidth = <%="\""%><%for(col=0;col<(columnList.size()-1);col++){String colValue = (String)columnList.get(col);%><%=Utility.getGridColumnWidht(colValue,isConfidenceChecked,isFrequencyChecked)%>,<%}%><%=Utility.getGridColumnWidht((String)columnList.get(col),isConfidenceChecked,isFrequencyChecked)%><%=",100\""%>;

var colTypes = <%="\""%><%for(col=0;col<(columnList.size()-1);col++){String colValue = (String)columnList.get(col);%><%=Utility.getGridColumnType(colValue)%>,<%}%><%=Utility.getGridColumnType((String)columnList.get(col))%><%=",link\""%>;

var freqcol = [<%for(col=0;col<(freqColumns.size()-1);col++){%><%="\""%><%=freqColumns.get(col)%><%="\""%>,<%}if(freqColumns.size()>0){%><%="\""%><%=freqColumns.get(col)%><%="\""%><%}%>];

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
			//alert(document.forms[0].sortedColumnDirection.value);
			//alert(mygrid.getHeaderCol(document.forms[0].sortedColumn.value));
			var action = "SearchResultView.do?pageOf=advancedSearch&isPaging=true&isSorting=true";
			document.forms[0].action = action;
			document.forms[0].submit();
			return true;
	}
	
	
	mygrid = new dhtmlXGridObject('gridbox');
	mygrid.setImagePath("dhtml_comp/imgs/");
	mygrid.setHeader(columns);
	mygrid.setEditable("FALSE");
	//mygrid.enableAutoHeigth(false);
	mygrid.setSkin("xp");


	mygrid.setInitWidths(colWidth);

	//mygrid.setColAlign("left,left")
	//mygrid.setColAlign("right,left,left,right,center,left,center");
	//mygrid.setColTypes("ro,ro,ro,ro,ro,ro,ro");
	mygrid.setColSorting(colTypes);
	
	//mygrid.enableMultiselect(true)
	//mygrid.enablePaging(true,1,3,"pagingArea",true,"recinfoArea");
	
	
	mygrid.init();

/*	
	mygrid.loadXML("dhtml_comp/grid.xml");

		clears the dummy data and refreshes the grid.
		fix for grid display on IE for first time.
			

	*/
	//mygrid.clearAll();
	/*
	for(ind=0;ind<freqcol.length;ind++)
	{
		var colind = parseInt(freqcol[ind]);
	//	mygrid.setColWidth(colind,0);
		mygrid.setHeaderCol(colind,"");
		mygrid.setColumnHidden(colind ,true);
	}
	*/	
	mygrid.setOnColumnSort(on_before_sort);
	
	function f1()
	{
		mygrid.setSizes();
	}
	mygrid.setOnResize(f1);

	//mygrid.setColHidden("false,false,true,false,false,false,false");
//	mygrid.setColumnHidden(2 ,true);
<%
	if(sortedColumn!=null&&sortedColumnDirection!=null&&sortedColumnIndex!=null)
	{
	//System.out.println("sortedColumnDirection: " +sortedColumnDirection);	
%>
	mygrid.setSortImgState(true,"<%=sortedColumnIndex%>","<%=sortedColumnDirection.toUpperCase()%>")

<%		
	}
%>
		for(var row=0;row<myData.length;row++)
	{
		mygrid.addRow(row+1,myData[row],row+1);
	}
//	alert("1");
	mygrid.setSizes();
</script>
