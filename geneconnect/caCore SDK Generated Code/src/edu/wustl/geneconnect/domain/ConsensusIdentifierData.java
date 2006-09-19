
package edu.wustl.geneconnect.domain;


/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */

/**
 * Contains the actual instance (e.g. Entrez Gene ID 1958) of each genomic identifier that satisfies 
 * the GeneConnect query and its frequency across all result sets 
 * 
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

	private edu.wustl.geneconnect.domain.GenomicIdentifierSet genomicIdentifierSet;

	public edu.wustl.geneconnect.domain.GenomicIdentifierSet getGenomicIdentifierSet()
	{
		/**
		 * Removed a statement calling ApplicationService to get GEnomicIdentifierSet.
		 * The GenomiIdentifierSet object is set in a  business logic (OrmDaoImpl.java)  
		 * while calculating frequency. 
		 */
		return genomicIdentifierSet;
	}

	public void setGenomicIdentifierSet(
			edu.wustl.geneconnect.domain.GenomicIdentifierSet genomicIdentifierSet)
	{
		this.genomicIdentifierSet = genomicIdentifierSet;
	}

	private edu.wustl.geneconnect.domain.GenomicIdentifier genomicIdentifier;

	public edu.wustl.geneconnect.domain.GenomicIdentifier getGenomicIdentifier()
	{
		/**
		 * Removed a statement calling ApplicationService to get GenomicIdentifier.
		 * The GenomiIdentifier object is set in a  business logic (OrmDaoImpl.java)  
		 * while calculating frequency. 
		 */
		return genomicIdentifier;
	}

	public void setGenomicIdentifier(
			edu.wustl.geneconnect.domain.GenomicIdentifier genomicIdentifier)
	{
		this.genomicIdentifier = genomicIdentifier;
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