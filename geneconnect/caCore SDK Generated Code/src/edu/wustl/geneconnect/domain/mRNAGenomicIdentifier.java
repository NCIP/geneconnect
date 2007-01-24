
package edu.wustl.geneconnect.domain;

/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */

/**
 * Derived class of genomic identifier that is limited to mRNA genomic identifiers.
 */

public class mRNAGenomicIdentifier extends GenomicIdentifier implements java.io.Serializable
{

	private static final long serialVersionUID = 1234567890L;
/**
 * Contains genomic identifier value of data source belonging to MessengerRNA family
 */
	protected java.lang.String genomicIdentifier;

/**
 * Returns genomic identifier value of data source belonging to MessengerRNA family
 * @see edu.wustl.geneconnect.domain.GenomicIdentifier#getGenomicIdentifier()
 */
	public java.lang.String getGenomicIdentifier()
	{
		return genomicIdentifier;
	}
/**
 * Sets genomic identifier value of data source belonging to MessengerRNA family
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
		if (obj instanceof mRNAGenomicIdentifier)
		{
			mRNAGenomicIdentifier c = (mRNAGenomicIdentifier) obj;
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