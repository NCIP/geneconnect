/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>ClassName: java edu.wustl.geneconnect.bizlogic.ResultDataInterface</p> 
 */

package edu.wustl.geneconnect.bizlogic;

import java.util.Map;

/**
 * Result Data - marker interface.
 * @author Mahesh Nalkande
 * @version 1.0
 */

public interface ResultDataInterface
{

	/**
	 * Getter method for Data.
	 * @return java.util.Map  
	 */

	Map getData();

	void setData(Map data);

	Object getValue(Object key);

	//	List getResult();
	//
	//	/**
	//	 * Setter method for Data.
	//	 * @param data - This map will contain Domain Element ,Action Form and other
	//	 * data which is to be passed to the BusinessLogic.  
	//	 */
	//	void setResult(List result);
	//
	//	/**
	//	 * Getter method for columnHeader.
	//	 * @return java.util.List  
	//	 */
	//	List getColumnHeader();
	//
	//	/**
	//	 * Setter method for columnHeader.
	//	 * @param columnHeader - This list will contain column names
	//	 */
	//	void setColumnHeader(List columnHeader);

}