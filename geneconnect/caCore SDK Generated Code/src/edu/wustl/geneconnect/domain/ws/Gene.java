
package edu.wustl.geneconnect.domain.ws;

/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */

public class Gene implements java.io.Serializable
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

	private java.lang.String ensemblGeneId;

	public java.lang.String getEnsemblGeneId()
	{
		return ensemblGeneId;
	}

	public void setEnsemblGeneId(java.lang.String ensemblGeneId)
	{
		this.ensemblGeneId = ensemblGeneId;
	}

	private java.lang.Long entrezGeneId;

	public java.lang.Long getEntrezGeneId()
	{
		return entrezGeneId;
	}

	public void setEntrezGeneId(java.lang.Long entrezGeneId)
	{
		this.entrezGeneId = entrezGeneId;
	}

	private java.lang.String unigeneClusterId;

	public java.lang.String getUnigeneClusterId()
	{
		return unigeneClusterId;
	}

	public void setUnigeneClusterId(java.lang.String unigeneClusterId)
	{
		this.unigeneClusterId = unigeneClusterId;
	}

	private java.util.Collection genomicIdentifierSetCollection = new java.util.HashSet();

	public java.util.Collection getGenomicIdentifierSetCollection()
	{
		return genomicIdentifierSetCollection;
	}

	public void setGenomicIdentifierSetCollection(
			java.util.Collection genomicIdentifierSetCollection)
	{
		this.genomicIdentifierSetCollection = genomicIdentifierSetCollection;
	}

	private java.util.Collection messengerRNACollection = new java.util.HashSet();

	public java.util.Collection getMessengerRNACollection()
	{
		return messengerRNACollection;
	}

	public void setMessengerRNACollection(java.util.Collection messengerRNACollection)
	{
		this.messengerRNACollection = messengerRNACollection;
	}

	private java.util.Collection proteinCollection = new java.util.HashSet();

	public java.util.Collection getProteinCollection()
	{
		return proteinCollection;
	}

	public void setProteinCollection(java.util.Collection proteinCollection)
	{
		this.proteinCollection = proteinCollection;
	}

	public boolean equals(Object obj)
	{
		boolean eq = false;
		if (obj instanceof Gene)
		{
			Gene c = (Gene) obj;
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
