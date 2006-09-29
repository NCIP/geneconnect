/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
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