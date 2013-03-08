/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

package edu.wustl.geneconnect.domain.ws;

/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */

public class OrderOfNodeTraversal implements java.io.Serializable
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

	private java.util.Collection genomicIdentifierSetCollection = new java.util.HashSet();

	public java.util.Collection getGenomicIdentifierSetCollection()
	{
		return genomicIdentifierSetCollection;
	}

	public void setGenomicIdentifierSetCollection(
			java.util.Collection genomicIdentifierSetCollection)
	{
		this.genomicIdentifierSetCollection = genomicIdentifierSetCollection;
	}

	private edu.wustl.geneconnect.domain.ws.DataSource sourceDataSource;

	public edu.wustl.geneconnect.domain.ws.DataSource getSourceDataSource()
	{
		return sourceDataSource;
	}

	public void setSourceDataSource(edu.wustl.geneconnect.domain.ws.DataSource sourceDataSource)
	{
		this.sourceDataSource = sourceDataSource;
	}

	private edu.wustl.geneconnect.domain.ws.LinkType linkType;

	public edu.wustl.geneconnect.domain.ws.LinkType getLinkType()
	{
		return linkType;
	}

	public void setLinkType(edu.wustl.geneconnect.domain.ws.LinkType linkType)
	{
		this.linkType = linkType;
	}

	private edu.wustl.geneconnect.domain.ws.OrderOfNodeTraversal childOrderOfNodeTraversal;

	public edu.wustl.geneconnect.domain.ws.OrderOfNodeTraversal getChildOrderOfNodeTraversal()
	{
		return childOrderOfNodeTraversal;
	}

	public void setChildOrderOfNodeTraversal(
			edu.wustl.geneconnect.domain.ws.OrderOfNodeTraversal childOrderOfNodeTraversal)
	{
		this.childOrderOfNodeTraversal = childOrderOfNodeTraversal;
	}

	private edu.wustl.geneconnect.domain.ws.OrderOfNodeTraversal parentOrderOfNodeTraversal;

	public edu.wustl.geneconnect.domain.ws.OrderOfNodeTraversal getParentOrderOfNodeTraversal()
	{
		return parentOrderOfNodeTraversal;
	}

	public void setParentOrderOfNodeTraversal(
			edu.wustl.geneconnect.domain.ws.OrderOfNodeTraversal parentOrderOfNodeTraversal)
	{
		this.parentOrderOfNodeTraversal = parentOrderOfNodeTraversal;
	}

	public boolean equals(Object obj)
	{
		boolean eq = false;
		if (obj instanceof OrderOfNodeTraversal)
		{
			OrderOfNodeTraversal c = (OrderOfNodeTraversal) obj;
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
