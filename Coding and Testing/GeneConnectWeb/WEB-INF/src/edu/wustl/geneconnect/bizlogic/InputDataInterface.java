/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>ClassName: java edu.wustl.geneconnect.bizlogic.InputDataInterface</p> 
 */

package edu.wustl.geneconnect.bizlogic;

import java.util.Map;

/**
 * Interface to be implemented by input data object passed 
 * to the BusinessLogic.
 * 
 * <p>Input data object contains a Map, which will contain Domain Element ,Action Form
 * and other data which is to be passed to the BusinessLogic.
 *  
 * @author mahesh_nalkande
 * @version 1.0
 */
public interface InputDataInterface
{

	/**
	 * Getter method for Data.
	 * @return java.util.Map  
	 */
	Map getData();

	/**
	 * Setter method for Data.
	 * @param data - This map will contain Domain Element ,Action Form and other
	 * data which is to be passed to the BusinessLogic.  
	 */
	void setData(Map data);
}