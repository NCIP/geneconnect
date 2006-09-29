/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.bizlogic.ValidationResultData</p> 
 */

package edu.wustl.geneconnect.bizlogic;

import java.util.List;

/**
 * This class represents the resultdata returned by BusinessLogic 
 * when some validation exception occurs during the operation.
 * It contains the list of validation errors.
 * @author mahesh_nalkande
 * @version 1.0
 */
public class ValidationResultData
{

	/**
	 * List of validation errors.
	 */
	private List errorList;

	/**
	 * Default constructor
	 */
	public ValidationResultData()
	{
	}

	/**
	 * Getter method for Error List.
	 * @return Returns the errorList.
	 */
	public List getErrorList()
	{
		return errorList;
	}

	/**
	 * Setter method for Error List.
	 * @param errorList The errorList to set.
	 */
	public void setErrorList(List errorList)
	{
		this.errorList = errorList;
	}
}