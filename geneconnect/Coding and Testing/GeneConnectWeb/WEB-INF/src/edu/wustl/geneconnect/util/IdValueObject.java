/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.util.IdValueObjectr</p> 
 */

package edu.wustl.geneconnect.util;

import java.io.Serializable;

import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * This class is used to send information to components in id-value format.
 * Typically this is used in UI components
 *
 * @author mahesh_nalkande
 * @version 1.0
 */

public class IdValueObject implements DisplayInformationInterface, Serializable
{

	/**
	 * identifier of this object
	 */
	Object displayId;

	/**
	 * value/description for the identifier
	 */
	String displayValue;

	/**
	 * Constructor taking all paramters
	 * @param id object id
	 * @param value value of id
	 */
	public IdValueObject(Object id, String value)
	{
		this.displayId = id;
		this.displayValue = value;
	}

	/**
	 * Default Constructor
	 */
	public IdValueObject()
	{
		displayId = null;
		displayValue = null;

	}

	/**
	 * @return object id
	 */
	public Object getDisplayId()
	{
		return this.displayId;
	}

	/**
	 * @param id object id
	 */
	public void setDisplayId(Object id)
	{
		this.displayId = id;
	}

	/**
	 * @return value of this id
	 */
	public String getDisplayValue()
	{
		return this.displayValue;
	}

	/**
	 * setter
	 * @param value value for this id 
	 */
	public void setDisplayValue(String value)
	{
		this.displayValue = value;
	}

	/** 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o)
	{
		IdValueObject idv = (IdValueObject) o;
		if (this.displayId.equals(idv.displayId))
		{
			return true;
		}
		return false;
	}

	/**
	 * @return int - hashCode of object
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return new HashCodeBuilder().append(getDisplayId()).toHashCode();
	}
}