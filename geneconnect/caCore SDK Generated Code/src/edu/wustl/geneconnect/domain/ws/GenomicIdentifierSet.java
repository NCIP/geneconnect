
package edu.wustl.geneconnect.domain.ws;

/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
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
		return orderOfNodeTraversalCollection;
	}

	public void setOrderOfNodeTraversalCollection(
			java.util.Collection orderOfNodeTraversalCollection)
	{
		this.orderOfNodeTraversalCollection = orderOfNodeTraversalCollection;
	}

	private edu.wustl.geneconnect.domain.ws.Protein protein;

	public edu.wustl.geneconnect.domain.ws.Protein getProtein()
	{
		return null;
	}

	public void setProtein(edu.wustl.geneconnect.domain.ws.Protein protein)
	{
		this.protein = protein;
	}

	private edu.wustl.geneconnect.domain.ws.MessengerRNA messengerRNA;

	public edu.wustl.geneconnect.domain.ws.MessengerRNA getMessengerRNA()
	{
		return null;
	}

	public void setMessengerRNA(edu.wustl.geneconnect.domain.ws.MessengerRNA messengerRNA)
	{
		this.messengerRNA = messengerRNA;
	}

	private java.util.Collection consensusIdentifierDataCollection = new java.util.HashSet();

	public java.util.Collection getConsensusIdentifierDataCollection()
	{
		return consensusIdentifierDataCollection;
	}

	public void setConsensusIdentifierDataCollection(
			java.util.Collection consensusIdentifierDataCollection)
	{
		this.consensusIdentifierDataCollection = consensusIdentifierDataCollection;
	}

	private edu.wustl.geneconnect.domain.ws.Gene gene;

	public edu.wustl.geneconnect.domain.ws.Gene getGene()
	{
		return null;
	}

	public void setGene(edu.wustl.geneconnect.domain.ws.Gene gene)
	{
		this.gene = gene;
	}

	private edu.wustl.geneconnect.ws.GenomicIdentifierSolution genomicIdentifierSolution;

	public edu.wustl.geneconnect.ws.GenomicIdentifierSolution getGenomicIdentifierSolution()
	{
		return null;
	}

	public void setGenomicIdentifierSolution(
			edu.wustl.geneconnect.ws.GenomicIdentifierSolution genomicIdentifierSolution)
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
