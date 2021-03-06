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
 * Genomic Identifier denotes one of the genomic data sources.
 */

public class GenomicIdentifier implements java.io.Serializable
{

	private static final long serialVersionUID = 1234567890L;

	protected java.lang.Long id;

	public java.lang.Long getId()
	{
		return id;
	}

	public void setId(java.lang.Long id)
	{
		this.id = id;
	}
	/**
	 * Contains genomic identifier value of data source belonging to MessengerRNA or Gene or Protein family
	 */
	protected java.lang.Object genomicIdentifier;

	/**
	 * The method is externally added to reslove the issue of different genomic identifier data 
	 * types of child classes.
	 * For instance data type of EntrezGene genomic identifier is java.lang.Long and for other 
	 * it is java.lang.String.
	 *   
	 * @return Object
	 */
	public java.lang.Object getGenomicIdentifier()
	{
		return genomicIdentifier;
	}

	/**
	 * The method is externally added to reslove the issue of different genomic identifier data 
	 * types of child classes.
	 * For instance data type of EntrezGene genomic identifier is java.lang.Long and for other 
	 * it is  java.lang.String .
	 */
	public void setGenomicIdentifier(java.lang.Object genomicIdentifier)
	{
		this.genomicIdentifier = genomicIdentifier;
	}
/**
 * The attribute dataSource is added as discriminator value to determine the GenomicIdentifier class representing 
 * the data source.
 */
	protected java.lang.String dataSource;

	/**
	 * The attribute dataSource is added as discriminator value to determine the GenomicIdentifier class representing 
	 * the data source.
	 * This attribute is used for querying on frequency. 
	 * @return java.lang.String
	 */
	public java.lang.String getDataSource()
	{
		return dataSource;
	}
/**
 * Sets the string value determining the GenomicIdentifier class representing the data source.
 * @param dataSource
 */
	public void setDataSource(java.lang.String dataSource)
	{
		this.dataSource = dataSource;
	}
	/**
	 * Associated ConsensusIdentifierData object with this GenomicIdentifier.
	 * @see edu.wustl.geneconnect.domain.ConsensusIdentifierData
	 */

	private edu.wustl.geneconnect.domain.ConsensusIdentifierData consensusIdentifierData;

	/**
	 * Modified the implementation generated by caCore SDK as the ConsensusIdentifierData object 
	 * is set in a  business logic while calculating frequency.
	 * Returns the associated ConsensusIdentifierData object with this GenomicIdentifier.
	 * @return consensusIdentifierData
	 */
	public edu.wustl.geneconnect.domain.ConsensusIdentifierData getConsensusIdentifierData()
	{
		/**
		 * Removed a statement calling ApplicationService to get ConsensusIdentifierData.
		 * The ConsensusIdentifierData object is set in a  business logic while calculating frequency. 
		 */
		return consensusIdentifierData;
	}
/**
 * Sets the ConsensusIdentifierData object with this GenomicIdentifier.
 * @param consensusIdentifierData
 */
	public void setConsensusIdentifierData(
			edu.wustl.geneconnect.domain.ConsensusIdentifierData consensusIdentifierData)
	{
		this.consensusIdentifierData = consensusIdentifierData;
	}

	public boolean equals(Object obj)
	{
		boolean eq = false;
		if (obj instanceof GenomicIdentifier)
		{
			GenomicIdentifier c = (GenomicIdentifier) obj;
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