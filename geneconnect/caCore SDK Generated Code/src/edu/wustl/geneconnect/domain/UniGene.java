
package edu.wustl.geneconnect.domain;


/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */

/**
 * Derived class representing the UniGene cluster ID.
 */

public class UniGene extends GeneGenomicIdentifier implements java.io.Serializable
{

	private static final long serialVersionUID = 1234567890L;

	protected java.lang.String genomicIdentifier;

	public java.lang.String getGenomicIdentifier()
	{
		return genomicIdentifier;
	}

	public void setGenomicIdentifier(java.lang.String genomicIdentifier)
	{
		this.genomicIdentifier = genomicIdentifier;
	}
	
	
	public boolean equals(Object obj)
	{
		boolean eq = false;
		if (obj instanceof UniGene)
		{
			UniGene c = (UniGene) obj;
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