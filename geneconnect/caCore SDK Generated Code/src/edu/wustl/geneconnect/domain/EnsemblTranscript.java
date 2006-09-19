
package edu.wustl.geneconnect.domain;


/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */

/**
 * Derived class representing the Ensembl Transcript ID.
 */

public class EnsemblTranscript extends mRNAGenomicIdentifier implements java.io.Serializable
{

	private static final long serialVersionUID = 1234567890L;

	public boolean equals(Object obj)
	{
		boolean eq = false;
		if (obj instanceof EnsemblTranscript)
		{
			EnsemblTranscript c = (EnsemblTranscript) obj;
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