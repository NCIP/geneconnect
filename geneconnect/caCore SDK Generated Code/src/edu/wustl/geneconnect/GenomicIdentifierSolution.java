/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

package edu.wustl.geneconnect;

import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationServiceProvider;

/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */

/**
 * Encapsulates the result set of a GeneConnect query
 */

public class GenomicIdentifierSolution implements java.io.Serializable
{

	private static final long serialVersionUID = 1234567890L;
	/**
	 * Unique identifier of the Genomic Identifier Solution object.
	 */
	private java.lang.Long id;
	/**
	 * Returns the unique identifier of the Genomic Identifier Solution object.
	 * @return unique identifier
	 */ 
	public java.lang.Long getId()
	{
		return id;
	}
/**
 * Sets the unique identifier of the Genomic Identifier Solution object.
 * @param id
 */
	public void setId(java.lang.Long id)
	{
		this.id = id;
	}
/**
 * Associated GenomicIdentifierSet object collection with this GenomicIdentifierSolution.
 */
	private java.util.Collection genomicIdentifierSetCollection = new java.util.HashSet();
/**
 * Returns the associated collection of GenomicIdentifierSet object with this GenomicIdentifierSolution.
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

				edu.wustl.geneconnect.GenomicIdentifierSolution thisIdSet = new edu.wustl.geneconnect.GenomicIdentifierSolution();
				thisIdSet.setId(this.getId());
				java.util.Collection resultList = applicationService.search(
						"edu.wustl.geneconnect.domain.GenomicIdentifierSet", thisIdSet);
				genomicIdentifierSetCollection = resultList;
				return resultList;

			}
			catch (Exception ex)
			{
				System.out
						.println("GenomicIdentifierSolution:getGenomicIdentifierSetCollection throws exception ... ...");
				ex.printStackTrace();
			}
		}
		return genomicIdentifierSetCollection;
	}
/**
 * Sets the collection of GenomicIdentifierSet objects with this GenomicIdentifierSolution.
 * @param genomicIdentifierSetCollection
 */
	public void setGenomicIdentifierSetCollection(
			java.util.Collection genomicIdentifierSetCollection)
	{
		this.genomicIdentifierSetCollection = genomicIdentifierSetCollection;
	}
	/**
	 * Associated collection of ConsensusIdentifierData object with this GenomicIdentifierSolution.
	 */
	private java.util.Collection consensusIdentifierDataCollection = new java.util.HashSet();
	/**
	 * Returns the associated collection of ConsensusIdentifierData object with this GenomicIdentifierSolution.
	 * @return consensusIdentifierDataCollection
	 */
	public java.util.Collection getConsensusIdentifierDataCollection()
	{
		try
		{
			if (consensusIdentifierDataCollection.size() == 0)
			{
			}
		}
		catch (Exception e)
		{
			ApplicationService applicationService = ApplicationServiceProvider
					.getApplicationService();
			try
			{

				edu.wustl.geneconnect.GenomicIdentifierSolution thisIdSet = new edu.wustl.geneconnect.GenomicIdentifierSolution();
				thisIdSet.setId(this.getId());
				java.util.Collection resultList = applicationService.search(
						"edu.wustl.geneconnect.domain.ConsensusIdentifierData", thisIdSet);
				consensusIdentifierDataCollection = resultList;
				return resultList;

			}
			catch (Exception ex)
			{
				System.out
						.println("GenomicIdentifierSolution:getConsensusIdentifierDataCollection throws exception ... ...");
				ex.printStackTrace();
			}
		}
		return consensusIdentifierDataCollection;
	}
/**
 * Sets the collection of ConsensusIdentifierData object with this GenomicIdentifierSolution.
 * @param consensusIdentifierDataCollection
 */
	public void setConsensusIdentifierDataCollection(
			java.util.Collection consensusIdentifierDataCollection)
	{
		this.consensusIdentifierDataCollection = consensusIdentifierDataCollection;
	}

	public boolean equals(Object obj)
	{
		boolean eq = false;
		if (obj instanceof GenomicIdentifierSolution)
		{
			GenomicIdentifierSolution c = (GenomicIdentifierSolution) obj;
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