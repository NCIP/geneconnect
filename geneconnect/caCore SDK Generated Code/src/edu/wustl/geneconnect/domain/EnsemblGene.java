
package edu.wustl.geneconnect.domain;

/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */

/**
 * Derived class representing the Ensembl Gene ID.
 */

public class EnsemblGene extends GeneGenomicIdentifier implements java.io.Serializable
{

	private static final long serialVersionUID = 1234567890L;
/**
 * Contains genomic identifier value for EnsemblGene data source.
 */
	protected java.lang.String genomicIdentifier;
	/**
	 * Returns the genomic identifier value for EnsemblGene data source.
	 * @return genomicIdentifier
	 * @see edu.wustl.geneconnect.domain.GenomicIdentifier#getGenomicIdentifier()
	 */	
	public java.lang.String getGenomicIdentifier()
	{
		return genomicIdentifier;
	}
	/**
	 * Sets the genomic identifier value for EnsemblGene data source.
	 * @param genomicIdentifier
	 * @see edu.wustl.geneconnect.domain.GenomicIdentifier#setGenomicIdentifier(java.lang.Object genomicIdentifier)
	 */	
	public void setGenomicIdentifier(java.lang.String genomicIdentifier)
	{
		this.genomicIdentifier = genomicIdentifier;
	}

	public boolean equals(Object obj)
	{
		boolean eq = false;
		if (obj instanceof EnsemblGene)
		{
			EnsemblGene c = (EnsemblGene) obj;
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