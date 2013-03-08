/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.bizlogic.SuccessResultData</p> 
 */

package edu.wustl.geneconnect.bizlogic;

import java.util.Map;

/**
 * This class represents the resultdata returned by BusinessLogic 
 * after performing the successfull operation.
 * 
 * It contains the map, which will contain Domain Element and other
 * data returned by the BusinessLogic. 
 * 
 * @author mahesh_nalkande
 */

public class SuccessResultData
{

	/**
	 * Map - This map will contain Domain Element and other
	 * data returned by the BusinessLogic.  
	 */
	private Map data;

	/**
	 * Default constructor
	 */
	public SuccessResultData()
	{
	}

	/**
	 * Getter method for data.
	 * @return Returns the data.
	 */
	public Map getData()
	{
		return data;
	}

	/**
	 * Setter method for the data
	 * @param data - This map will contain Domain Element and other
	 * data returned by the BusinessLogic.  
	 */
	public void setData(Map data)
	{
		this.data = data;
	}
}