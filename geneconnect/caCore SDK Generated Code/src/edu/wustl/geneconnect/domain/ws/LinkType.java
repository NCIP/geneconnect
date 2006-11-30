
package edu.wustl.geneconnect.domain.ws;

/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */

public class LinkType implements java.io.Serializable
{

	private static final long serialVersionUID = 1234567890L;

	private java.lang.Long id;

	public java.lang.Long getId()
	{
		return id;
	}

	public void setId(java.lang.Long id)
	{
		this.id = id;
	}

	private java.lang.String type;

	public java.lang.String getType()
	{
		return type;
	}

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
