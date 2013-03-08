/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

package edu.wustl.geneconnect.domain.ws;

/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */

public class MessengerRNA implements java.io.Serializable
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

	private java.lang.String ensemblTranscriptId;

	public java.lang.String getEnsemblTranscriptId()
	{
		return ensemblTranscriptId;
	}

	public void setEnsemblTranscriptId(java.lang.String ensemblTranscriptId)
	{
		this.ensemblTranscriptId = ensemblTranscriptId;
	}

	private java.lang.String genbankAccession;

	public java.lang.String getGenbankAccession()
	{
		return genbankAccession;
	}

	public void setGenbankAccession(java.lang.String genbankAccession)
	{
		this.genbankAccession = genbankAccession;
	}

	private java.lang.String refseqId;

	public java.lang.String getRefseqId()
	{
		return refseqId;
	}

	public void setRefseqId(java.lang.String refseqId)
	{
		this.refseqId = refseqId;
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

	private java.util.Collection proteinCollection = new java.util.HashSet();

	public java.util.Collection getProteinCollection()
	{
		return proteinCollection;
	}

	public void setProteinCollection(java.util.Collection proteinCollection)
	{
		this.proteinCollection = proteinCollection;
	}

	private java.util.Collection geneCollection = new java.util.HashSet();

	public java.util.Collection getGeneCollection()
	{
		return geneCollection;
	}

	public void setGeneCollection(java.util.Collection geneCollection)
	{
		this.geneCollection = geneCollection;
	}

	public boolean equals(Object obj)
	{
		boolean eq = false;
		if (obj instanceof MessengerRNA)
		{
			MessengerRNA c = (MessengerRNA) obj;
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
