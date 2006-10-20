
package edu.wustl.geneconnect.domain;

/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */

/**
 * Derived class representing the Ensembl Peptide ID.
 */

public class EnsemblPeptide extends ProteinGenomicIdentifier implements java.io.Serializable
{

	private static final long serialVersionUID = 1234567890L;

	public boolean equals(Object obj)
	{
		boolean eq = false;
		if (obj instanceof EnsemblPeptide)
		{
			EnsemblPeptide c = (EnsemblPeptide) obj;
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