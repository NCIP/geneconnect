/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
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