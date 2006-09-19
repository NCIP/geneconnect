
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

	protected java.lang.Object genomicIdentifier;

	public java.lang.Object getGenomicIdentifier()
	{
		return genomicIdentifier;
	}

	public void setGenomicIdentifier(java.lang.Object genomicIdentifier)
	{
		this.genomicIdentifier = genomicIdentifier;
	}

	protected java.lang.String dataSource;

	public java.lang.String getDataSource()
	{
		return dataSource;
	}

	public void setDataSource(java.lang.String dataSource)
	{
		this.dataSource = dataSource;
	}

	private edu.wustl.geneconnect.domain.ConsensusIdentifierData consensusIdentifierData;

	public edu.wustl.geneconnect.domain.ConsensusIdentifierData getConsensusIdentifierData()
	{
		/**
		 * Removed a statement calling ApplicationService to get ConsensusIdentifierData.
		 * The ConsensusIdentifierData object is set in a  business logic (OrmDaoImpl.java)  
		 * while calculating frequency. 
		 */
		return consensusIdentifierData;
	}

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