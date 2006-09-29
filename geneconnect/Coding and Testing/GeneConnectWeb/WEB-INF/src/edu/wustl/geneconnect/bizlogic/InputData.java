/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.bizlogic.InputData</p> 
 */

package edu.wustl.geneconnect.bizlogic;

import java.util.Map;

/**
 * Input data object which is passed to the Business Logic.
 * It contains a Map , which will contain Domain Elements ,Action Form and other 
 * data which is to be passed to the BusinessLogic. 
 * 
 * @author mahesh_nalkande
 * @version 1.0
 */
public class InputData implements InputDataInterface
{

	/**
	 * Map - This map will contain Domain Element ,Action Form and other
	 * data which is to be passed to the BusinessLogic.  
	 */
	private Map data;

	/**
	 * Default constructor
	 */
	public InputData()
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
	 * Setter method for data
	 * @param data - This map will contain Domain Element ,Action Form and other
	 * data which is to be passed to the BusinessLogic. 
	 */
	public void setData(Map data)
	{
		this.data = data;
	}
}