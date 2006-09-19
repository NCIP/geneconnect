
package edu.wustl.geneconnect.domain;

import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationServiceProvider;

/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */

/**
 * Represents a distinct set of genomic identifiers that are linked using one or more orders of node 
 * traversal through the genomic identifier graph 
 * 
 */

public class GenomicIdentifierSet implements java.io.Serializable
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

	private java.lang.Float confidenceScore;

	public java.lang.Float getConfidenceScore()
	{
		return confidenceScore;
	}

	public void setConfidenceScore(java.lang.Float confidenceScore)
	{
		this.confidenceScore = confidenceScore;
	}

	private java.util.Collection orderOfNodeTraversalCollection = new java.util.HashSet();

	public java.util.Collection getOrderOfNodeTraversalCollection()
	{
		try
		{
			if (orderOfNodeTraversalCollection.size() == 0)
			{
			}
		}
		catch (Exception e)
		{
			ApplicationService applicationService = ApplicationServiceProvider
					.getApplicationService();
			try
			{

				edu.wustl.geneconnect.domain.GenomicIdentifierSet thisIdSet = new edu.wustl.geneconnect.domain.GenomicIdentifierSet();
				thisIdSet.setId(this.getId());
				java.util.Collection resultList = applicationService.search(
						"edu.wustl.geneconnect.domain.OrderOfNodeTraversal", thisIdSet);
				orderOfNodeTraversalCollection = resultList;
				return resultList;

			}
			catch (Exception ex)
			{
				System.out
						.println("GenomicIdentifierSet:getOrderOfNodeTraversalCollection throws exception ... ...");
				ex.printStackTrace();
			}
		}
		return orderOfNodeTraversalCollection;
	}

	public void setOrderOfNodeTraversalCollection(
			java.util.Collection orderOfNodeTraversalCollection)
	{
		this.orderOfNodeTraversalCollection = orderOfNodeTraversalCollection;
	}

	private edu.wustl.geneconnect.domain.Protein protein;

	public edu.wustl.geneconnect.domain.Protein getProtein()
	{

		ApplicationService applicationService = ApplicationServiceProvider.getApplicationService();
		edu.wustl.geneconnect.domain.GenomicIdentifierSet thisIdSet = new edu.wustl.geneconnect.domain.GenomicIdentifierSet();
		thisIdSet.setId(this.getId());

		try
		{
			java.util.List resultList = applicationService.search(
					"edu.wustl.geneconnect.domain.Protein", thisIdSet);
			if (resultList != null && resultList.size() > 0)
			{
				protein = (edu.wustl.geneconnect.domain.Protein) resultList.get(0);
			}

		}
		catch (Exception ex)
		{
			System.out.println("GenomicIdentifierSet:getProtein throws exception ... ...");
			ex.printStackTrace();
		}
		return protein;

	}

	public void setProtein(edu.wustl.geneconnect.domain.Protein protein)
	{
		this.protein = protein;
	}

	private edu.wustl.geneconnect.domain.MessengerRNA messengerRNA;

	public edu.wustl.geneconnect.domain.MessengerRNA getMessengerRNA()
	{

		ApplicationService applicationService = ApplicationServiceProvider.getApplicationService();
		edu.wustl.geneconnect.domain.GenomicIdentifierSet thisIdSet = new edu.wustl.geneconnect.domain.GenomicIdentifierSet();
		thisIdSet.setId(this.getId());

		try
		{
			java.util.List resultList = applicationService.search(
					"edu.wustl.geneconnect.domain.MessengerRNA", thisIdSet);
			if (resultList != null && resultList.size() > 0)
			{
				messengerRNA = (edu.wustl.geneconnect.domain.MessengerRNA) resultList.get(0);
			}

		}
		catch (Exception ex)
		{
			System.out.println("GenomicIdentifierSet:getMessengerRNA throws exception ... ...");
			ex.printStackTrace();
		}
		return messengerRNA;

	}

	public void setMessengerRNA(edu.wustl.geneconnect.domain.MessengerRNA messengerRNA)
	{
		this.messengerRNA = messengerRNA;
	}

	private edu.wustl.geneconnect.domain.Gene gene;

	public edu.wustl.geneconnect.domain.Gene getGene()
	{

		ApplicationService applicationService = ApplicationServiceProvider.getApplicationService();
		edu.wustl.geneconnect.domain.GenomicIdentifierSet thisIdSet = new edu.wustl.geneconnect.domain.GenomicIdentifierSet();
		thisIdSet.setId(this.getId());

		try
		{
			java.util.List resultList = applicationService.search(
					"edu.wustl.geneconnect.domain.Gene", thisIdSet);
			if (resultList != null && resultList.size() > 0)
			{
				gene = (edu.wustl.geneconnect.domain.Gene) resultList.get(0);
			}

		}
		catch (Exception ex)
		{
			System.out.println("GenomicIdentifierSet:getGene throws exception ... ...");
			ex.printStackTrace();
		}
		return gene;

	}

	public void setGene(edu.wustl.geneconnect.domain.Gene gene)
	{
		this.gene = gene;
	}

	private java.util.Collection consensusIdentifierDataCollection = new java.util.HashSet();

	public java.util.Collection getConsensusIdentifierDataCollection()
	{
		/**
		 * Removed a statement calling ApplicationService to get ConsensusIdentifierData.
		 * The ConsensusIdentifierData object is set in a  business logic (OrmDaoImpl.java)  
		 * while calculating frequency. 
		 */
		return consensusIdentifierDataCollection;
	}

	public void setConsensusIdentifierDataCollection(
			java.util.Collection consensusIdentifierDataCollection)
	{
		this.consensusIdentifierDataCollection = consensusIdentifierDataCollection;
	}

	private edu.wustl.geneconnect.GenomicIdentifierSolution genomicIdentifierSolution;

	public edu.wustl.geneconnect.GenomicIdentifierSolution getGenomicIdentifierSolution()
	{
		return genomicIdentifierSolution;
	}

	public void setGenomicIdentifierSolution(
			edu.wustl.geneconnect.GenomicIdentifierSolution genomicIdentifierSolution)
	{
		this.genomicIdentifierSolution = genomicIdentifierSolution;
	}

	public boolean equals(Object obj)
	{
		boolean eq = false;
		if (obj instanceof GenomicIdentifierSet)
		{
			GenomicIdentifierSet c = (GenomicIdentifierSet) obj;
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