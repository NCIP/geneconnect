
package edu.wustl.geneconnect.domain.ws;

/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */

public class ConsensusIdentifierData implements java.io.Serializable
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

	private java.lang.Float frequency;

	public java.lang.Float getFrequency()
	{
		return frequency;
	}

	public void setFrequency(java.lang.Float frequency)
	{
		this.frequency = frequency;
	}

	private edu.wustl.geneconnect.domain.ws.GenomicIdentifierSet genomicIdentifierSet;

	public edu.wustl.geneconnect.domain.ws.GenomicIdentifierSet getGenomicIdentifierSet()
	{
		return null;
	}

	public void setGenomicIdentifierSet(
			edu.wustl.geneconnect.domain.ws.GenomicIdentifierSet genomicIdentifierSet)
	{
		this.genomicIdentifierSet = genomicIdentifierSet;
	}

	private edu.wustl.geneconnect.domain.ws.GenomicIdentifier genomicIdentifier;

	public edu.wustl.geneconnect.domain.ws.GenomicIdentifier getGenomicIdentifier()
	{
		return genomicIdentifier;
	}

	public void setGenomicIdentifier(
			edu.wustl.geneconnect.domain.ws.GenomicIdentifier genomicIdentifier)
	{
		this.genomicIdentifier = genomicIdentifier;
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
		if (obj instanceof ConsensusIdentifierData)
		{
			ConsensusIdentifierData c = (ConsensusIdentifierData) obj;
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
