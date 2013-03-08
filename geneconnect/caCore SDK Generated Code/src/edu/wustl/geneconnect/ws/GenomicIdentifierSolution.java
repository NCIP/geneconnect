/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

package edu.wustl.geneconnect.ws;

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

	public void setGenomicIdentifierSetCollection(
			java.util.Collection genomicIdentifierSetCollection)
	{
		this.genomicIdentifierSetCollection = genomicIdentifierSetCollection;
	}

	private java.util.Collection consensusIdentifierDataCollection = new java.util.HashSet();

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