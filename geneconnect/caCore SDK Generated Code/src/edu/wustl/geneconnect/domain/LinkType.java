/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

package edu.wustl.geneconnect.domain;

/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */

/**
 * This class represents the type of link between a pair of genomic identifiers that are linked to each other.
 */
public class LinkType implements java.io.Serializable
{

	private static final long serialVersionUID = 1234567890L;
	/**
	 * Unique identifier of LinkType.
	 */
	private java.lang.Long id;
/**
 * Returns the unique identifier of LinkType.
 * @return unique identifier
 */
	public java.lang.Long getId()
	{
		return id;
	}
/**
 * Sets the unique identifier to this LinkType object.
 * @param id
 */
	public void setId(java.lang.Long id)
	{
		this.id = id;
	}
/**
 * It denotes the type of link between a pair of genomic identifiers. 
 * Possible values could be one of the Direct Annotation, Inferred Annotation or Alignment-based similarity.
 */
	private java.lang.String type;
/**
 * Returns the type of link between a pair of genomic identifiers.
 * @return type
 */
	public java.lang.String getType()
	{
		return type;
	}
/**
 * Sets the type of link between a pair of genomic identifiers.
 * @param type
 */
	public void setType(java.lang.String type)
	{
		this.type = type;
	}

	public boolean equals(Object obj)
	{
		boolean eq = false;
		if (obj instanceof LinkType)
		{
			LinkType c = (LinkType) obj;
			Long thisId = getId();

			if (thisId != null && thisId.equals(c.getId()))
			{
				eq = true;
			}

		}
		return eq;
	}

	public int hashCode()
	{
		int h = 0;

		if (getId() != null)
		{
			h += getId().hashCode();
		}

		return h;
	}

}