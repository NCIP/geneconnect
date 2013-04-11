/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>ClassName: java edu.wustl.geneconnect.util.DisplayInformationInterface</p> 
 */

package edu.wustl.geneconnect.util;

/**
 * This interface will be implemented by objects which are used to send information
 * to components in id-value format.
 * 
 * Typically this is used in UI components
 *
 * @author mahesh_nalkande
 * @version 1.0
 */
public interface DisplayInformationInterface
{

	/**
	 * Getter method for id.
	 * @return Returns the id.
	 */
	Object getDisplayId();

	/**
	 * Getter method for value.
	 * @return Returns the value.
	 */
	String getDisplayValue();

}