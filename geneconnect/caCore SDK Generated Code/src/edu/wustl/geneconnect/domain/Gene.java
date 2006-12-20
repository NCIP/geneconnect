
package edu.wustl.geneconnect.domain;


/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */

/**
 * Contains gene genomic identifiers.
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

	private java.lang.Boolean ensemblGeneAsOutput;
	private java.lang.Boolean entrezGeneAsOutput;
	private java.lang.Boolean unigeneAsOutput;

	public java.lang.Boolean getEnsemblGeneAsOutput()
	{
		return ensemblGeneAsOutput;
	}

	public void setEnsemblGeneAsOutput(java.lang.Boolean ensemblGeneAsOutput)
	{
		this.ensemblGeneAsOutput = ensemblGeneAsOutput;
	}

	public java.lang.Boolean getEntrezGeneAsOutput()
	{
		return entrezGeneAsOutput;
	}

	public void setEntrezGeneAsOutput(java.lang.Boolean entrezGeneAsOutput)
	{
		this.entrezGeneAsOutput = entrezGeneAsOutput;
	}

	public java.lang.Boolean getUnigeneAsOutput()
	{
		return unigeneAsOutput;
	}

	public void setUnigeneAsOutput(java.lang.Boolean unigeneAsOutput)
	{
		this.unigeneAsOutput = unigeneAsOutput;
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
//				edu.wustl.geneconnect.domain.Gene thisIdSet = new edu.wustl.geneconnect.domain.Gene();
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
//						.println("Gene:getGenomicIdentifierSetCollection throws exception ... ...");
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

	private java.util.Collection messengerRNACollection = new java.util.HashSet();

	public java.util.Collection getMessengerRNACollection()
	{
//		try
//		{
//			if (messengerRNACollection.size() == 0)
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
//				edu.wustl.geneconnect.domain.Gene thisIdSet = new edu.wustl.geneconnect.domain.Gene();
//				thisIdSet.setId(this.getId());
//				java.util.Collection resultList = applicationService.search(
//						"edu.wustl.geneconnect.domain.MessengerRNA", thisIdSet);
//				messengerRNACollection = resultList;
//				return resultList;
//
//			}
//			catch (Exception ex)
//			{
//				System.out.println("Gene:getMessengerRNACollection throws exception ... ...");
//				ex.printStackTrace();
//			}
//		}
		return messengerRNACollection;
	}

	public void setMessengerRNACollection(java.util.Collection messengerRNACollection)
	{
		this.messengerRNACollection = messengerRNACollection;
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
//				edu.wustl.geneconnect.domain.Gene thisIdSet = new edu.wustl.geneconnect.domain.Gene();
//				thisIdSet.setId(this.getId());
//				java.util.Collection resultList = applicationService.search(
//						"edu.wustl.geneconnect.domain.Protein", thisIdSet);
//				proteinCollection = resultList;
//				return resultList;
//
//			}
//			catch (Exception ex)
//			{
//				System.out.println("Gene:getProteinCollection throws exception ... ...");
//				ex.printStackTrace();
//			}
//		}
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