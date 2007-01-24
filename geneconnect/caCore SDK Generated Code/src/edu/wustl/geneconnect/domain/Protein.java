
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
/**
 * Unique Identifier of the Protein
 */
	private java.lang.Long id;
/**
 * Returns the unique identifier assigned to this Protein.
 * @return unique identifier
 */
	public java.lang.Long getId()
	{
		return id;
	}
/**
 * Sets the unique identifier to this Protein
 * @param id
 */
	public void setId(java.lang.Long id)
	{
		this.id = id;
	}
/**
 * The unique identifier assigned to a peptide by ENSEMBL. It is used to uniquely identify a peptide in the ENSEMBL system.
 */
	private java.lang.String ensemblPeptideId;
/**
 * Returns the unique identifier assigned to a peptide by ENSEMBL. 
 * @return ensemblPeptideId
 */
	public java.lang.String getEnsemblPeptideId()
	{
		return ensemblPeptideId;
	}
/**
 * Sets the unique identifier assigned to a peptide by ENSEMBL. 
 * @param ensemblPeptideId
 */
	public void setEnsemblPeptideId(java.lang.String ensemblPeptideId)
	{
		this.ensemblPeptideId = ensemblPeptideId;
	}
/**
 * The unique identifier for a Protein sequence assigned in the RefSeq data collection.
 */
	private java.lang.String refseqId;
/**
 * Returns the unique identifier for a Protein sequence assigned in the RefSeq data collection.
 * @return refseqId
 */
	public java.lang.String getRefseqId()
	{
		return refseqId;
	}
/**
 * Sets the unique identifier for a Protein sequence assigned in the RefSeq data collection.
 * @param refseqId
 */
	public void setRefseqId(java.lang.String refseqId)
	{
		this.refseqId = refseqId;
	}
/**
 * The unique genomic identifier for a protein sequence assigned by the Universal Protein Resource Knowledge Base.
 */
	private java.lang.String uniprotkbPrimaryAccession;

/**
 * Returns the unique genomic identifier for a protein sequence assigned by the Universal Protein Resource Knowledge Base.
 * @return uniprotkbPrimaryAccession
 */
	public java.lang.String getUniprotkbPrimaryAccession()
	{
		return uniprotkbPrimaryAccession;
	}
/**
 * Sets the unique genomic identifier for a protein sequence assigned by the Universal Protein Resource Knowledge Base.
 * @param uniprotkbPrimaryAccession
 */
	public void setUniprotkbPrimaryAccession(java.lang.String uniprotkbPrimaryAccession)
	{
		this.uniprotkbPrimaryAccession = uniprotkbPrimaryAccession;
	}
/**
 * The accession number is the unique identifier assigned to the entire sequence record when the record is 
 * submitted to GenBank. 
 */
	private java.lang.String genbankAccession;

	/**
	 * Returns the accession number, the unique identifier assigned to the entire sequence 
	 * record when the record is submitted to GenBank.
	 * @return genbankAccession
	 */
	public java.lang.String getGenbankAccession()
	{
		return genbankAccession;
	}

	/**
	 * Sets the accession number, the unique identifier assigned to the entire sequence record when the record is submitted to GenBank.
	 * @param genbankAccession
	 */
	public void setGenbankAccession(java.lang.String genbankAccession)
	{
		this.genbankAccession = genbankAccession;
	}

	/**
	 * The boolean value 'true' denotes  that the user has selected Ensembl Peptide data source as a part of output solution.
	 */
	private java.lang.Boolean ensemblPeptideAsOutput;
	/**
	 * The boolean value 'true' denotes  that the user has selected GenBank Protein data source as a part of output solution.
	 */
	private java.lang.Boolean genbankProteinAsOutput;
	/**
	 * The boolean value 'true' denotes  that the user has selected RefSeq Protein data source as a part of output solution.
	 */
	private java.lang.Boolean refseqProteinAsOutput;
	/**
	 * The boolean value 'true' denotes  that the user has selected UniProtKB data source as a part of output solution.
	 */
	private java.lang.Boolean uniprotkbAsOutput;
	
	/**
	 * Returns the boolean value denoting Ensembl Peptide data source is selected as a part of output solution.
	 * @return ensemblPeptideAsOutput
	 */
	public java.lang.Boolean getEnsemblPeptideAsOutput()
	{
		return ensemblPeptideAsOutput;
	}
	/**
	 * Sets the boolean value denoting Ensembl Peptide data source is selected as a part of output solution.
	 * @param ensemblPeptideAsOutput
	 */
	public void setEnsemblPeptideAsOutput(java.lang.Boolean ensemblPeptideAsOutput)
	{
		this.ensemblPeptideAsOutput = ensemblPeptideAsOutput;
	}
	/**
	 * Returns the boolean value denoting GenBank Protein data source is selected as a part of output solution.
	 * @return genbankProteinAsOutput
	 */
	public java.lang.Boolean getGenbankProteinAsOutput()
	{
		return genbankProteinAsOutput;
	}
	/**
	 * Sets the boolean value denoting GenBank Protein data source is selected as a part of output solution.
	 * @param genbankProteinAsOutput
	 */
	public void setGenbankProteinAsOutput(java.lang.Boolean genbankProteinAsOutput)
	{
		this.genbankProteinAsOutput = genbankProteinAsOutput;
	}
	/**
	 * Returns the boolean value denoting RefSeq Protein  data source is selected as a part of output solution.
	 * @return refseqProteinAsOutput
	 */
	public java.lang.Boolean getRefseqProteinAsOutput()
	{
		return refseqProteinAsOutput;
	}
	/**
	 * Sets the boolean value denoting RefSeq Protein  data source is selected as a part of output solution.
	 * @param refseqProteinAsOutput
	 */
	public void setRefseqProteinAsOutput(java.lang.Boolean refseqProteinAsOutput)
	{
		this.refseqProteinAsOutput = refseqProteinAsOutput;
	}
	/**
	 * Returns the boolean value denoting UniProtKB data source is selected as a part of output solution.
	 * @return uniprotkbAsOutput
	 */
	public java.lang.Boolean getUniprotkbAsOutput()
	{
		return uniprotkbAsOutput;
	}
	
	/**
	 * Sets the boolean value denoting UniProtKB data source is selected as a part of output solution.
	 * @param uniprotkbAsOutput
	 */
	public void setUniprotkbAsOutput(java.lang.Boolean uniprotkbAsOutput)
	{
		this.uniprotkbAsOutput = uniprotkbAsOutput;
	}
	
	/**
	 * Associated GenomicIdentifierSet object collection
	 * @see edu.wustl.geneconnect.domain.GenomicIdentifierSet
	 */
	private java.util.Collection genomicIdentifierSetCollection = new java.util.HashSet();
	
	/**
	 * Returns associated collection of GenomicIdentifierSet objects with this Protein.
	 * @return genomicIdentifierSetCollection
	 */
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
	/**
	 * Sets the collection of GenomicIdentifierSet objects with this Protein.
	 * @param genomicIdentifierSetCollection
	 */
	public void setGenomicIdentifierSetCollection(
			java.util.Collection genomicIdentifierSetCollection)
	{
		this.genomicIdentifierSetCollection = genomicIdentifierSetCollection;
	}
	/**
	 * Associated collection of MessengerRNA objects with this Protein.
	 * @see edu.wustl.geneconnect.domain.MessengerRNA
	 */
	private java.util.Collection messengerRNACollection = new java.util.HashSet();
	/**
	 * Returns associated collection of MessengerRNA objects with this Protein.
	 * @return messengerRNACollection
	 */
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
	/**
	 * Sets collection of MessengerRNA objects with this Protein.
	 * @param messengerRNACollection
	 */
	public void setMessengerRNACollection(java.util.Collection messengerRNACollection)
	{
		this.messengerRNACollection = messengerRNACollection;
	}
	/**
	 * Associated collection of Gene objects with this Protein.
	 * @see edu.wustl.geneconnect.domain.Gene
	 */
	private java.util.Collection geneCollection = new java.util.HashSet();
	
	/**
	 * Returns associated collection of Gene objects with this Protein.
	 * @return geneCollection
	 */
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
	/**
	 * Sets collection of Gene objects with this Protein.
	 * @param geneCollection
	 */
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