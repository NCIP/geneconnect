
package edu.wustl.geneconnect.domain;

import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationServiceProvider;

/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */

/**
 * Contains protein genomic identifiers.
 */

public class Protein implements java.io.Serializable
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

	private java.lang.String ensemblPeptideId;

	public java.lang.String getEnsemblPeptideId()
	{
		return ensemblPeptideId;
	}

	public void setEnsemblPeptideId(java.lang.String ensemblPeptideId)
	{
		this.ensemblPeptideId = ensemblPeptideId;
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

	private java.lang.String uniprotkbPrimaryAccession;

	public java.lang.String getUniprotkbPrimaryAccession()
	{
		return uniprotkbPrimaryAccession;
	}

	public void setUniprotkbPrimaryAccession(java.lang.String uniprotkbPrimaryAccession)
	{
		this.uniprotkbPrimaryAccession = uniprotkbPrimaryAccession;
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

	private java.lang.Boolean ensemblPeptideAsOutput;
	private java.lang.Boolean genbankProteinAsOutput;
	private java.lang.Boolean refseqProteinAsOutput;
	private java.lang.Boolean uniprotkbAsOutput;

	public java.lang.Boolean getEnsemblPeptideAsOutput()
	{
		return ensemblPeptideAsOutput;
	}

	public void setEnsemblPeptideAsOutput(java.lang.Boolean ensemblPeptideAsOutput)
	{
		this.ensemblPeptideAsOutput = ensemblPeptideAsOutput;
	}

	public java.lang.Boolean getGenbankProteinAsOutput()
	{
		return genbankProteinAsOutput;
	}

	public void setGenbankProteinAsOutput(java.lang.Boolean genbankProteinAsOutput)
	{
		this.genbankProteinAsOutput = genbankProteinAsOutput;
	}

	public java.lang.Boolean getRefseqProteinAsOutput()
	{
		return refseqProteinAsOutput;
	}

	public void setRefseqProteinAsOutput(java.lang.Boolean refseqProteinAsOutput)
	{
		this.refseqProteinAsOutput = refseqProteinAsOutput;
	}

	public java.lang.Boolean getUniprotkbAsOutput()
	{
		return uniprotkbAsOutput;
	}

	public void setUniprotkbAsOutput(java.lang.Boolean uniprotkbAsOutput)
	{
		this.uniprotkbAsOutput = uniprotkbAsOutput;
	}

	private java.util.Collection genomicIdentifierSetCollection = new java.util.HashSet();

	public java.util.Collection getGenomicIdentifierSetCollection()
	{
		try
		{
			if (genomicIdentifierSetCollection.size() == 0)
			{
			}
		}
		catch (Exception e)
		{
			ApplicationService applicationService = ApplicationServiceProvider
					.getApplicationService();
			try
			{

				edu.wustl.geneconnect.domain.Protein thisIdSet = new edu.wustl.geneconnect.domain.Protein();
				thisIdSet.setId(this.getId());
				java.util.Collection resultList = applicationService.search(
						"edu.wustl.geneconnect.domain.GenomicIdentifierSet", thisIdSet);
				genomicIdentifierSetCollection = resultList;
				return resultList;

			}
			catch (Exception ex)
			{
				System.out
						.println("Protein:getGenomicIdentifierSetCollection throws exception ... ...");
				ex.printStackTrace();
			}
		}
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
		try
		{
			if (messengerRNACollection.size() == 0)
			{
			}
		}
		catch (Exception e)
		{
			ApplicationService applicationService = ApplicationServiceProvider
					.getApplicationService();
			try
			{

				edu.wustl.geneconnect.domain.Protein thisIdSet = new edu.wustl.geneconnect.domain.Protein();
				thisIdSet.setId(this.getId());
				java.util.Collection resultList = applicationService.search(
						"edu.wustl.geneconnect.domain.MessengerRNA", thisIdSet);
				messengerRNACollection = resultList;
				return resultList;

			}
			catch (Exception ex)
			{
				System.out.println("Protein:getMessengerRNACollection throws exception ... ...");
				ex.printStackTrace();
			}
		}
		return messengerRNACollection;
	}

	public void setMessengerRNACollection(java.util.Collection messengerRNACollection)
	{
		this.messengerRNACollection = messengerRNACollection;
	}

	private java.util.Collection geneCollection = new java.util.HashSet();

	public java.util.Collection getGeneCollection()
	{
		try
		{
			if (geneCollection.size() == 0)
			{
			}
		}
		catch (Exception e)
		{
			ApplicationService applicationService = ApplicationServiceProvider
					.getApplicationService();
			try
			{

				edu.wustl.geneconnect.domain.Protein thisIdSet = new edu.wustl.geneconnect.domain.Protein();
				thisIdSet.setId(this.getId());
				java.util.Collection resultList = applicationService.search(
						"edu.wustl.geneconnect.domain.Gene", thisIdSet);
				geneCollection = resultList;
				return resultList;

			}
			catch (Exception ex)
			{
				System.out.println("Protein:getGeneCollection throws exception ... ...");
				ex.printStackTrace();
			}
		}
		return geneCollection;
	}

	public void setGeneCollection(java.util.Collection geneCollection)
	{
		this.geneCollection = geneCollection;
	}

	public boolean equals(Object obj)
	{
		boolean eq = false;
		if (obj instanceof Protein)
		{
			Protein c = (Protein) obj;
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