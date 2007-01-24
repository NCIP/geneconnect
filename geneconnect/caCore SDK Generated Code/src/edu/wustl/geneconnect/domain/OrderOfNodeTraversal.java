
package edu.wustl.geneconnect.domain;

import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationServiceProvider;

import java.util.HashSet;

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
/**
 * Unique identifier of Order Of Node Traversal object.
 */
	private java.lang.Long id;
/**
 * Returns the unique identifier of Order Of Node Traversal object.
 * @return unique identifier
 */
	public java.lang.Long getId()
	{
		return id;
	}
/**
 * Sets the unique identifier to Order Of Node Traversal object.
 * @param id
 */
	public void setId(java.lang.Long id)
	{
		this.id = id;
	}

	/**
	 * Added to search a next path id. 
	 */
	private java.lang.Long childPathId;

	/**
	 * The method is added to get the next node in the linked list of OrderOfNodeTraversal.
	 * This method is used in getChildOrderOfNodeTraversal().    
	 * @return Long
	 */
	public java.lang.Long getChildPathId()
	{
		return childPathId;
	}
/**
 *  The method is added to set the next node in the linked list of OrderOfNodeTraversal.
 * @param childPathId
 */
	public void setChildPathId(java.lang.Long childPathId)
	{
		this.childPathId = childPathId;
	}

	/**
	 * Added to search a previous path id. 
	 */
	private java.lang.Long parentPathid;

	/**
	 * The method is added to get the previous node in the linked list of OrderOfNodeTraversal.
	 * This method is used in getParentOrderOfNodeTraversal().    
	 * @return Long
	 */
	public java.lang.Long getParentPathid()
	{
		return parentPathid;
	}
/**
 * The method is added to set the previous node in the linked list of OrderOfNodeTraversal.
 * @param parentPathid
 */
	public void setParentPathid(java.lang.Long parentPathid)
	{
		this.parentPathid = parentPathid;
	}
	/**
	 * Associated collection of GenomicIdentifierSet objects with this OrderOfNodeTraversal.
	 * @see edu.wustl.geneconnect.domain.GenomicIdentifierSet
	 */
	private java.util.Collection genomicIdentifierSetCollection=new HashSet();
	/**
	 * Returns the associated collection of GenomicIdentifierSet objects with this OrderOfNodeTraversal.
	 * @return genomicIdentifierSetCollection
	 */
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
/**
 * Sets the collection of GenomicIdentifierSet objects with this OrderOfNodeTraversal.
 * @param genomicIdentifierSetCollection
 */
	public void setGenomicIdentifierSetCollection(
			java.util.Collection genomicIdentifierSetCollection)
	{
		this.genomicIdentifierSetCollection = genomicIdentifierSetCollection;
	}
/**
 * Associated DataSource object with this OrderOfNodeTraversal.
 * @see edu.wustl.geneconnect.domain.DataSource
 */
	private edu.wustl.geneconnect.domain.DataSource sourceDataSource;
/**
 * Returns the associated DataSource object with this OrderOfNodeTraversal.
 * @return sourceDataSource
 */
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
/**
 * Sets the DataSource object with this OrderOfNodeTraversal.
 * @param sourceDataSource
 */
	public void setSourceDataSource(edu.wustl.geneconnect.domain.DataSource sourceDataSource)
	{
		this.sourceDataSource = sourceDataSource;
	}
/**
 * Associated LinkType object with this OrderOfNodeTraversal.
 * Denotes the type of link between a pair of genomic identifiers that are linked to each other.
 * @see edu.wustl.geneconnect.domain.LinkType
 */
	private edu.wustl.geneconnect.domain.LinkType linkType;
/**
 * Returns the associated LinkType object with this OrderOfNodeTraversal.
 * @return linkType
 */
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
/**
 * Sets the LinkType object with this OrderOfNodeTraversal.
 * @param linkType
 */
	public void setLinkType(edu.wustl.geneconnect.domain.LinkType linkType)
	{
		this.linkType = linkType;
	}

/**
 * Asscoiated next node of OrderOfNodeTraversal with this linked list of OrderOfNodeTraversal.
 */
	private edu.wustl.geneconnect.domain.OrderOfNodeTraversal childOrderOfNodeTraversal;

	/**
	 * The implementation is modified to get a next node in the ONT list.
	 * Modified to search a id which eqauls to childPathId
	 * instead of (as genarated by caCore )searching a id which eqauls to id
	 * @return OrderOfNodeTraversal
	 */
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
/**
 * Sets the next node of OrderOfNodeTraversal with this linked list of OrderOfNodeTraversal.
 * @param childOrderOfNodeTraversal
 */
	public void setChildOrderOfNodeTraversal(
			edu.wustl.geneconnect.domain.OrderOfNodeTraversal childOrderOfNodeTraversal)
	{
		this.childOrderOfNodeTraversal = childOrderOfNodeTraversal;
	}
/**
 * Asscoiated previous node of OrderOfNodeTraversal with this linked list of OrderOfNodeTraversal.
 */
	private edu.wustl.geneconnect.domain.OrderOfNodeTraversal parentOrderOfNodeTraversal;

	/**
	 * The implementation is modified to get a previous  node in the ONT list.
	 * Modified to search a id which eqauls to parentPathid
	 * instead of (as genarated by caCore )searching a id which eqauls to id
	 * @return OrderOfNodeTraversal
	 */
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
			System.out.println("this.getParentPathid() "+this.getParentPathid());
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
/**
 * Sets the previous node of OrderOfNodeTraversal with this linked list of OrderOfNodeTraversal.
 * @param parentOrderOfNodeTraversal
 */
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