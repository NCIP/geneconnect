
package edu.wustl.geneconnect.domain;


/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */

/**
 * Contains mRNA genomic identifiers.
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

	private java.lang.Boolean ensemblTranscriptAsOutput;
	private java.lang.Boolean genbankmRNAAsOutput;
	private java.lang.Boolean refseqmRNAAsOutput;

	public java.lang.Boolean getEnsemblTranscriptAsOutput()
	{
		return ensemblTranscriptAsOutput;
	}

	public void setEnsemblTranscriptAsOutput(java.lang.Boolean ensemblTranscriptAsOutput)
	{
		this.ensemblTranscriptAsOutput = ensemblTranscriptAsOutput;
	}

	public java.lang.Boolean getGenbankmRNAAsOutput()
	{
		return genbankmRNAAsOutput;
	}

	public void setGenbankmRNAAsOutput(java.lang.Boolean genbankmRNAAsOutput)
	{
		this.genbankmRNAAsOutput = genbankmRNAAsOutput;
	}

	public java.lang.Boolean getRefseqmRNAAsOutput()
	{
		return refseqmRNAAsOutput;
	}

	public void setRefseqmRNAAsOutput(java.lang.Boolean refseqmRNAAsOutput)
	{
		this.refseqmRNAAsOutput = refseqmRNAAsOutput;
	}

	private java.util.Collection genomicIdentifierSetCollection = new java.util.HashSet();

	public java.util.Collection getGenomicIdentifierSetCollection()
	{
//		try
//		{
//			if (genomicIdentifierSetCollection.size() == 0)
//			{
//			}
//		}
//		catch (Exception e)
//		{
//			ApplicationService applicationService = ApplicationServiceProvider
//					.getApplicationService();
//			try
//			{
//
//				edu.wustl.geneconnect.domain.MessengerRNA thisIdSet = new edu.wustl.geneconnect.domain.MessengerRNA();
//				thisIdSet.setId(this.getId());
//				java.util.Collection resultList = applicationService.search(
//						"edu.wustl.geneconnect.domain.GenomicIdentifierSet", thisIdSet);
//				genomicIdentifierSetCollection = resultList;
//				return resultList;
//
//			}
//			catch (Exception ex)
//			{
//				System.out
//						.println("MessengerRNA:getGenomicIdentifierSetCollection throws exception ... ...");
//				ex.printStackTrace();
//			}
//		}
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
//		try
//		{
//			if (proteinCollection.size() == 0)
//			{
//			}
//		}
//		catch (Exception e)
//		{
//			ApplicationService applicationService = ApplicationServiceProvider
//					.getApplicationService();
//			try
//			{
//
//				edu.wustl.geneconnect.domain.MessengerRNA thisIdSet = new edu.wustl.geneconnect.domain.MessengerRNA();
//				thisIdSet.setId(this.getId());
//				java.util.Collection resultList = applicationService.search(
//						"edu.wustl.geneconnect.domain.Protein", thisIdSet);
//				proteinCollection = resultList;
//				return resultList;
//
//			}
//			catch (Exception ex)
//			{
//				System.out.println("MessengerRNA:getProteinCollection throws exception ... ...");
//				ex.printStackTrace();
//			}
//		}
		return proteinCollection;
	}

	public void setProteinCollection(java.util.Collection proteinCollection)
	{
		this.proteinCollection = proteinCollection;
	}

	private java.util.Collection geneCollection = new java.util.HashSet();

	public java.util.Collection getGeneCollection()
	{
//		try
//		{
//			if (geneCollection.size() == 0)
//			{
//			}
//		}
//		catch (Exception e)
//		{
//			ApplicationService applicationService = ApplicationServiceProvider
//					.getApplicationService();
//			try
//			{
//
//				edu.wustl.geneconnect.domain.MessengerRNA thisIdSet = new edu.wustl.geneconnect.domain.MessengerRNA();
//				thisIdSet.setId(this.getId());
//				java.util.Collection resultList = applicationService.search(
//						"edu.wustl.geneconnect.domain.Gene", thisIdSet);
//				geneCollection = resultList;
//				return resultList;
//
//			}
//			catch (Exception ex)
//			{
//				System.out.println("MessengerRNA:getGeneCollection throws exception ... ...");
//				ex.printStackTrace();
//			}
//		}
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