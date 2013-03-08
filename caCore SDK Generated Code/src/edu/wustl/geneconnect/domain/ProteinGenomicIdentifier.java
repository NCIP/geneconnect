/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

package edu.wustl.geneconnect.domain;

/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */

/**
 * Derived class of genomic identifier that is limited to protein genomic identifiers. 
 * 
 */

public class ProteinGenomicIdentifier extends GenomicIdentifier implements java.io.Serializable
{

	private static final long serialVersionUID = 1234567890L;
/**
 * Contains genomic identifier value of data source belonging to Protein family.
 */
	protected java.lang.String genomicIdentifier;
/**
 * Returns the genomic identifier value of data source belonging to Protein family.
 * @return genomicIdentifier
 * @see edu.wustl.geneconnect.domain.GenomicIdentifier#getGenomicIdentifier()
 */
	public java.lang.String getGenomicIdentifier()
	{
		return genomicIdentifier;
	}
/**
 * Sets the genomic identifier value of data source belonging to Protein family.
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
		if (obj instanceof ProteinGenomicIdentifier)
		{
			ProteinGenomicIdentifier c = (ProteinGenomicIdentifier) obj;
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