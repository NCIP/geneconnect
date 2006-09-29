/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.bizlogic.ResultDataInterface</p> 
 */

package edu.wustl.geneconnect.bizlogic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Result Data - marker interface.
 * @author Mahesh Nalkande
 * @version 1.0
 */

public class ResultData implements ResultDataInterface
{

	/**
	 * Stores the data list to obtain by processing query 
	 */
	
	private List result;

	/**
	 * Stores the list of column names obtained from result
	 */
	private List columnHeader;



	/**
	 * Default constructor
	 */
	public ResultData()
	{
		result = new ArrayList();
		columnHeader = new Vector();
	}

	/**
	 * Getter method for data.
	 * @return Returns the data.
	 */
	public List getResult()
	{
		return result;
	}

	/**
	 * Setter method for result
	 * @param result - stores result obtained by processing query 
	 *  
	 */
	public void setResult(List result)
	{
		this.result = result;
	}

	/**
	 * Getter method for columnHeader.
	 * @return java.util.List  
	 */
	public List getColumnHeader()
	{
		return columnHeader;
	}

	/**
	 * Setter method for columnHeader.
	 * @param columnHeader - This list will contain column names
	 */
	public void setColumnHeader(List columnHeader)
	{
		this.columnHeader = columnHeader;
	}
}