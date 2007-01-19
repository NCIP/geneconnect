package edu.wustl.geneconnect.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import edu.wustl.common.util.logger.Logger;



/**
 *<p>Title: </p>
 *<p>Description:  </p>
 *<p>Copyright: (c) Washington University, School of Medicine 2004</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *@author Krunal Thakkar
 *@version 1.0
 */

public class SpreadSheetViewTag extends TagSupport
{
	Map attributes;
	
	String collection;
	
	String className;
	
	Map inputs;
	
	
	public int doStartTag()
    {
		try
        {
			
			int noOfColumns=attributes.size();
		
			Logger.out.debug("No. of columns in SpreadSheetViewTag===>"+noOfColumns);
			
//			Logger.out.debug("Name of the CollectionObject on the form==>"+collection);
		
			//Initializing set of Column Headers
//			Set columnHeaders = attributes.keySet();
			
			//Initializing array of Column Headers
			//Object[]columnHeaderArray = attributes.keySet().toArray();
			List l = new ArrayList(attributes.keySet());
			Collections.sort(l);
			Object[]columnHeaderArray =l.toArray();
			Set inputKeys = inputs.keySet();
			
			//Initializing list of the keys of Inputs
			List inputKeyList = new ArrayList(inputKeys);
			
			TreeMap sortedKeys = new TreeMap();
			
			for(int i=0; i<inputKeys.size(); i++)
			{
				String inputKey = (String)inputKeyList.get(i);
				
				StringTokenizer inputKeyTokenized = new StringTokenizer(inputKey, "_");
				
				sortedKeys.put(inputKeyTokenized.nextToken(), inputKeyTokenized.nextToken());
			}
			
			Set sortedKeySet = sortedKeys.keySet();
			
			List sortedKeyList = new ArrayList(sortedKeySet);
			
//			for(int i=0; i<sortedKeyList.size(); i++)
//			{
//				Logger.out.debug("SpreadShet View Tag  SortedKeys-->"+sortedKeyList.get(i));
//			}
			
			//counting row no. on the basis of already entered rows of Inputs
			int newRowNo = 2;
			if(sortedKeyList.size() > 0)
			{
				String lastKey = (String)sortedKeyList.get(sortedKeyList.size()-1);
				
				newRowNo = new Integer(lastKey.substring(lastKey.length()-1, lastKey.length())).intValue() + 1;
				
//				Logger.out.debug("LastKey-->"+lastKey.substring(lastKey.length()-1, lastKey.length()));
			}
			
			//Initializing Outstream object to write the code replacing the tag
			JspWriter out = pageContext.getOut();
			
			 //Creatin Javascript Function
            out.println("<script language=\"JavaScript\">");
            
            //defining global variable for javascript
            out.println("var rowid="+newRowNo+";");
            
            //writing function to insert Input row
            out.println("function insert"+className+"Row(subdivtag) {");
            
            out.print("var attributes = new Array( ");

            for(int i=0; i<columnHeaderArray.length; i++)
			{
            	if(i<columnHeaderArray.length-1)
            		out.print("\""+(String)columnHeaderArray[i]+"\",");
            	else
            		out.print("\""+(String)columnHeaderArray[i]+"\"");
			}
            out.println(");");
            
            out.println("var rows = new Array(); \n rows = document.getElementById(subdivtag).rows; \n var totalrows = rows.length; \n var newrowno = totalrows + 1;");
            out.println("var newrow=document.getElementById(subdivtag).insertRow(rows.length); \n newrow.id=\""+className+":row_\"" + "+rowid"+";");
			
            out.println("var newcell=newrow.insertCell(0); \n newcell.className=\"formSerialNumberField\";");
            out.println("var newcellname=\"chk_\"+rowid;");
            //out.println("alert(newcellname);");
            out.println("field=\"<input type='checkbox' name='\"+ "+ "newcellname"+ "+" +"\"'>\";");
            out.println("newcell.innerHTML=\"\"+field;");
            
            out.println("for(var i=1; i<="+columnHeaderArray.length+"; i++) {");
            out.println("var newcell=newrow.insertCell(i); \n newcell.className=\"formField\";" );
            out.println("var cellname=\""+collection+"Value("+className+":\""+ "+"+"rowid"+ "+" +"\"_\""+ "+" +"attributes[i-1]+\")\";");
            //out.println("alert('Inserting==>'+cellname);");
            out.println("field=\"<input type='text' name='\"+ "+" "+"cellname" + "+" +"\"' class='formFieldSized10' onfocus='setCurrentTextBox(this)' onkeyup='checkInputOutput()' onpaste='pasteForSafari(event)' >\";");
            out.println("newcell.innerHTML=\"\"+field;");
            out.println("}");//end of for loop
            out.println("rowid=rowid+1;");
            out.println("}");//end of insert function
            
            //writing funciton to delete Input row
            out.println("function delete"+className+"Row(subdivtag) {");
            out.println("var divObj=document.getElementById(subdivtag);");
            out.println("var rows = new Array(); \n rows = document.getElementById(subdivtag).rows; \n var totalrows = rows.length;");
            //out.println("alert('No of Rows in delete folder==>'+totalrows);");
            out.println("var rowIdCounter = 0;");
            out.println("var rowIds = new Array();");
            out.println("var rowsToDelete = \"\";");
            out.println("for(var i=0; i<totalrows; i++) {");
            out.println("var checkbox=divObj.rows[i].cells[0].firstChild;");
            //out.println("alert('Cell object==>'+checkbox.checked);");
            out.println("if(checkbox.checked){");
            //out.println("alert('Row ID to delete==>'+divObj.rows[i].id);");
            //alert(divObj.rows[i].cells[1].firstChild.name);
            out.println("rowIds[rowIdCounter]=divObj.rows[i].id;");
            out.println("rowIdCounter=rowIdCounter+1;");
            out.println("rowsToDelete = rowsToDelete + i +\",\";");
            out.println("}");//end of if
            
            out.println("}");//end of for loop
            
            out.println("for(var i=0; i<rowIds.length; i++) {");
            out.println("var rowObject=document.getElementById(rowIds[i]);");
            //out.println("divObj.removeChild(rowObject);");
            out.println("}");//end of for loop
            
            out.println("var toggleValue = document.getElementById(\"toggle\");");
            out.println("toggleValue.checked=0;");
            
            //out.println("alert(\"Rows to Delete-->\"+rowsToDelete)");
            out.println("if(rowsToDelete != \"\"){");
            out.println("document.forms[0].action = \"AdvancedSearch.do?targetAction=updateMap&rowsToDelete=\"+rowsToDelete;");
            out.println("document.forms[0].submit(); }");
            
            out.println("}"); //end of delete function
            
            
            //writing function to select all Inputs
            out.println("function toggleAll(subdivtag) {");
            out.println("var toggleValue = document.getElementById(\"toggle\");");
            out.println("var divObj=document.getElementById(subdivtag);");
            out.println("var rows = new Array(); \n rows = document.getElementById(subdivtag).rows; \n var totalrows = rows.length;");
            out.println("for(var i=0; i<totalrows; i++) {");
            out.println("var checkbox=divObj.rows[i].cells[0].firstChild;");
            out.println("checkbox.checked=toggleValue.checked;");
            out.println("}");//end of for loop
            
            out.println("}"); //end of toggleAll function
            out.println("</script>");
            

            out.println("<DIV style=\"width:100%\">");
            out.println("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\">");
            out.println("<tr>");
            out.println("<td class=\"formTitle\" height=\"20\"> Select Input Data Sources </td>");
            out.println("<td class=\"formTitle\" height=\"20\" align=\"right\">");
			//out.println("<html:button property=\"\" styleClass=\"actionButton\" onclick=\"pasteData()\"> Paste ID </html:button>");
			out.println("<input type=\"button\" name=\"add"+className+" \" value=\"Add More\" onclick=\"insert"+className+"Row('"+ className+"spreadsheet" +"')\" class=\"actionButton\" >");
			out.println("<input type=\"button\" name=\"delete"+className+" \" value=\"Delete\" onclick=\"delete"+className+"Row('"+ className+"spreadsheet" +"')\" class=\"actionButton\" >");
			out.println("<input type=\"button\" name=\"\" value=\"Paste ID\" onclick=\"pasteData()\" class=\"actionButton\" >");
			out.println("&nbsp;&nbsp;");
            out.println("</td>");
//            out.println("<td>");
//            out.println("&nbsp;");
//            out.println("</td>");
			out.println("</tr>");
			out.println("</table>");
			out.println("</DIV>");
            
            out.println("<DIV class=\"headerDiv\">");
//            out.println("<table cellpadding=\"3\" cellspacing=\"0\" border=\"0\" width=\"" + ( (columnHeaderArray.length+1) * 71) +"\">");
            out.println("<table cellpadding=\"3\" cellspacing=\"0\" border=\"0\" width=\"98.8%\">");
            
            out.println("<tr>");
            
            out.println("<td class=\"formLeftTopSubTableTitle\">");
			out.println("<input type=\"checkbox\" name=\"toggle\" id=\"toggle\" value=\"\" onclick=\"toggleAll('"+className+"spreadsheet')\">");
			out.println("</td>");
			
			//creating column-header row
			for(int i=0; i<columnHeaderArray.length; i++)
			{
				out.println("<td class=\"formRightTopSubTableTitle10Sized\">");
//           		out.println("<label for=\" "+(String)columnHeaderArray[i]+" \" class=\"formFieldSized10\" >");
          		out.println((String)columnHeaderArray[i]);
//           		out.println("</label>");
           		out.println("</td>");
            }
			
			out.println("</tr>");
			out.println("</table>");
			
			
//			out.println("<DIV class=\"spreadsheet\" style=\"width:"+(( (columnHeaderArray.length+1) * 72.5)) +"\">");
			out.println("<DIV class=\"spreadsheet\">");
//            out.println("<table cellpadding=\"3\" cellspacing=\"0\" border=\"0\"\" width=\"" + ( ((columnHeaderArray.length+1) * 68)) +"\">");
			out.println("<table cellpadding=\"3\" cellspacing=\"0\" border=\"0\" width=\"100%\">");
            
            out.println("<tbody id=\""+ className +"spreadsheet\">");
            
            int noOfInputs = inputs.size() / attributes.size();
            
            if(noOfInputs == 0)
            	noOfInputs = 1;
           
            //creating Input rows
            for(int k=1; k<=noOfInputs; k++)
            {
	            out.println("<tr id=\""+className+":row_"+k+"\">");
	            
	            out.println("<td class=\"formSerialNumberField\">");
	            out.println("<input type=\"checkbox\" name=\"chk_"+k+"\" id=\"chk_"+k+"\" value=\"\">");
	            out.println("</td>");

	            for(int i=0; i<columnHeaderArray.length; i++)
				{
	            	String attributeName = (String)columnHeaderArray[i];
	            	
	            		out.print("<td class=\"formField\">");
	            	
	            		if(inputs.size()>0)
	            		{
	            			//Logger.out.debug("Entered Value of the Attribute "+(className+":"+k+"_"+ attributeName)+"-->"+inputs.get(className+":"+k+"_"+ attributeName));
	            			out.print("<input type=\"text\" name=\""+collection+"Value("+(String)sortedKeyList.get(k-1)+"_"+ attributeName +")\" value=\""+inputs.get((String)sortedKeyList.get(k-1)+"_"+ attributeName)+"\" class=\"formFieldSized10\" onfocus=\"setCurrentTextBox(this)\" onkeyup=\"checkInputOutput()\" onpaste=\"pasteForSafari(event)\" >");
	            		}
	            		else
	            		{
	            			out.print("<input type=\"text\" name=\""+collection+"Value("+ className+":"+k+"_"+ attributeName +")\" value=\"\" class=\"formFieldSized10\" onfocus=\"setCurrentTextBox(this)\" onkeyup=\"checkInputOutput()\" onpaste=\"pasteForSafari(event)\">");
	            		}
	            	
	            	out.println("</td>");
				}
	            out.println("</tr>");
            }
            
            out.println("</tbody>");
            
            out.println("</table>");
            
            out.println("</DIV>");
            
            out.println("</DIV>");
            
			
            //creating rows of buttons
//            out.println("<table cellpadding=\"3\" cellspacing=\"0\" border=\"0\">");
//            out.println("<tr>");
//            
//            out.println("<td  colspan=\""+ (columnHeaderArray.length +1 )+"\" >");
//            out.println("<input type=\"button\" name=\"delete"+className+" \" value=\"Delete\" onclick=\"delete"+className+"Row('"+ className+"spreadsheet" +"')\" class=\"actionButton\" >");
//            out.println("<input type=\"button\" name=\"add"+className+" \" value=\"Add More\" onclick=\"insert"+className+"Row('"+ className+"spreadsheet" +"')\" class=\"actionButton\" >");
//            out.println("</td>");
//            
//            out.println("</tr>");
//            out.println("</table>");
            
        }
        catch (Exception e)
        {
        	Logger.out.info("Exception in SpreadSheetViewTag");
        	
        	Logger.out.debug(e.getMessage(),e);
        }
        
        return (SKIP_BODY);
    }
	
	
	/**
	 * @param attributes The attributes to set.
	 */
	public void setAttributes(Map attributes)
	{
		this.attributes = attributes;
	}
	
	/**
	 * @param collection The collection to set.
	 */
	public void setCollection(String collection)
	{
		this.collection = collection;
	}
	
	/**
	 * @param className The className to set.
	 */
	public void setClassName(String className)
	{
		this.className = className;
	}
	
	/**
	 * @param inputs The inputs to set.
	 */
	public void setInputs(Map inputs)
	{
		this.inputs = inputs;
	}
}
