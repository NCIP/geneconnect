
package edu.wustl.geneconnect.domain;

import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationServiceProvider;

/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */

/**
 * Represents a link in the genomic identifier graph. The collection of these links constitutes the 
 * order or node traversal through the genomic identifier graph that resulted in this distinct set 
 * of genomic identifiers. 
 * 
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

	/**
	 * Added to search a next path id. 
	 */
	private java.lang.Long childPathId;
	
	public java.lang.Long getChildPathId()
	{
		return childPathId;
	}

	public void setChildPathId(java.lang.Long childPathId)
	{
		this.childPathId = childPathId;
	}
	
	/**
	 * Added to search a prev path id. 
	 */
	private java.lang.Long parentPathid;
	
	public java.lang.Long getParentPathid()
	{
		return parentPathid;
	}

	public void setParentPathid(java.lang.Long parentPathid)
	{
		this.parentPathid = parentPathid;
	}

	private java.util.Collection genomicIdentifierSetCollection = new java.util.HashSet();

	public java.util.Collection getGenomicIdentifierSetCollection()
	{
		try
		{
			if (genomicIdentifierSetCollection.size() == 0)
			{
			}
		}
		catch (Exception e)
		{
			ApplicationService applicationService = ApplicationServiceProvider
					.getApplicationService();
			try
			{

				edu.wustl.geneconnect.domain.OrderOfNodeTraversal thisIdSet = new edu.wustl.geneconnect.domain.OrderOfNodeTraversal();
				thisIdSet.setId(this.getId());
				java.util.Collection resultList = applicationService.search(
						"edu.wustl.geneconnect.domain.GenomicIdentifierSet", thisIdSet);
				genomicIdentifierSetCollection = resultList;
				return resultList;

			}
			catch (Exception ex)
			{
				System.out
						.println("OrderOfNodeTraversal:getGenomicIdentifierSetCollection throws exception ... ...");
				ex.printStackTrace();
			}
		}
		return genomicIdentifierSetCollection;
	}

	public void setGenomicIdentifierSetCollection(
			java.util.Collection genomicIdentifierSetCollection)
	{
		this.genomicIdentifierSetCollection = genomicIdentifierSetCollection;
	}

	
	private edu.wustl.geneconnect.domain.DataSource sourceDataSource;

	public edu.wustl.geneconnect.domain.DataSource getSourceDataSource()
	{

		ApplicationService applicationService = ApplicationServiceProvider.getApplicationService();
		edu.wustl.geneconnect.domain.OrderOfNodeTraversal thisIdSet = new edu.wustl.geneconnect.domain.OrderOfNodeTraversal();
		thisIdSet.setId(this.getId());
		try
		{
			java.util.List resultList = applicationService.search(
					"edu.wustl.geneconnect.domain.DataSource", thisIdSet);

			if (resultList != null && resultList.size() > 0)
			{
				sourceDataSource = (edu.wustl.geneconnect.domain.DataSource) resultList.get(0);
			}
		}
		catch (Exception ex)
		{
			System.out.println("OrderOfNodeTraversal:getSourceDataSource throws exception ... ...");
			ex.printStackTrace();
		}
		return sourceDataSource;

	}

	public void setSourceDataSource(edu.wustl.geneconnect.domain.DataSource sourceDataSource)
	{
		this.sourceDataSource = sourceDataSource;
	}

	private edu.wustl.geneconnect.domain.LinkType linkType;

	public edu.wustl.geneconnect.domain.LinkType getLinkType()
	{

		ApplicationService applicationService = ApplicationServiceProvider.getApplicationService();
		edu.wustl.geneconnect.domain.OrderOfNodeTraversal thisIdSet = new edu.wustl.geneconnect.domain.OrderOfNodeTraversal();
		thisIdSet.setId(this.getId());
		try
		{
			java.util.List resultList = applicationService.search(
					"edu.wustl.geneconnect.domain.LinkType", thisIdSet);

			if (resultList != null && resultList.size() > 0)
			{
				linkType = (edu.wustl.geneconnect.domain.LinkType) resultList.get(0);
			}
		}
		catch (Exception ex)
		{
			System.out.println("OrderOfNodeTraversal:getLinkType throws exception ... ...");
			ex.printStackTrace();
		}
		return linkType;

	}

	public void setLinkType(edu.wustl.geneconnect.domain.LinkType linkType)
	{
		this.linkType = linkType;
	}

	private edu.wustl.geneconnect.domain.OrderOfNodeTraversal childOrderOfNodeTraversal;

	public edu.wustl.geneconnect.domain.OrderOfNodeTraversal getChildOrderOfNodeTraversal()
	{

		ApplicationService applicationService = ApplicationServiceProvider.getApplicationService();
		edu.wustl.geneconnect.domain.OrderOfNodeTraversal thisIdSet = new edu.wustl.geneconnect.domain.OrderOfNodeTraversal();
		
		// Changed to search a path id which eqauls to next_path_id
		// instead of (as genarated by caCore )searching a path id which eqauls to path_id
		if (this.getChildPathId() == null)
		{
			return null;

		}
		else
		{
			thisIdSet.setId(this.getChildPathId());
		}
		try
		{
			java.util.List resultList = applicationService.search(
					"edu.wustl.geneconnect.domain.OrderOfNodeTraversal", thisIdSet);

			if (resultList != null && resultList.size() > 0)
			{
				childOrderOfNodeTraversal = (edu.wustl.geneconnect.domain.OrderOfNodeTraversal) resultList
						.get(0);
			}
		}
		catch (Exception ex)
		{
			System.out
					.println("OrderOfNodeTraversal:getChildOrderOfNodeTraversal throws exception ... ...");
			ex.printStackTrace();
		}

		return childOrderOfNodeTraversal;

	}

	public void setChildOrderOfNodeTraversal(
			edu.wustl.geneconnect.domain.OrderOfNodeTraversal childOrderOfNodeTraversal)
	{
		this.childOrderOfNodeTraversal = childOrderOfNodeTraversal;
	}

	private edu.wustl.geneconnect.domain.OrderOfNodeTraversal parentOrderOfNodeTraversal;

	public edu.wustl.geneconnect.domain.OrderOfNodeTraversal getParentOrderOfNodeTraversal()
	{

		ApplicationService applicationService = ApplicationServiceProvider.getApplicationService();
		edu.wustl.geneconnect.domain.OrderOfNodeTraversal thisIdSet = new edu.wustl.geneconnect.domain.OrderOfNodeTraversal();
		// Changed to search a path id which eqauls to next_path_id
		// instead of (as genarated by caCore )searching a path id which eqauls to path_id
		if (this.getParentPathid() == null)
		{
			return null;
		}
		else
		{
			thisIdSet.setId(this.getParentPathid());
		}
		try
		{
			java.util.List resultList = applicationService.search(
					"edu.wustl.geneconnect.domain.OrderOfNodeTraversal", thisIdSet);

			if (resultList != null && resultList.size() > 0)
			{
				parentOrderOfNodeTraversal = (edu.wustl.geneconnect.domain.OrderOfNodeTraversal) resultList
						.get(0);
			}
		}
		catch (Exception ex)
		{
			System.out
					.println("OrderOfNodeTraversal:getParentOrderOfNodeTraversal throws exception ... ...");
			ex.printStackTrace();
		}
		return parentOrderOfNodeTraversal;

	}

	public void setParentOrderOfNodeTraversal(
			edu.wustl.geneconnect.domain.OrderOfNodeTraversal parentOrderOfNodeTraversal)
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