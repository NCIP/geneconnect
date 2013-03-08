/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

package edu.wustl.geneconnect.domain;

import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationServiceProvider;


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
/**
 * Unique Identifier for the Messenger RNA class
 */
	private java.lang.Long id;
	
	/**
	 * The unique identifier assigned to a transcript by ENSEMBL. 
	 * It is used to uniquely identify a gene transcript in the ENSEMBL system.
	 */
	private java.lang.String ensemblTranscriptId;
	
	/**
	 * The accession number is the unique identifier assigned to the entire sequence 
	 * record when the record is submitted to GenBank. 
	 */
	private java.lang.String genbankAccession;
	
	/**
	 * The unique identifier for a Messenger RNA sequence assigned in the RefSeq data collection.
	 */
	private java.lang.String refseqId;
	
	/**
	 * The boolean value 'true' denotes  that the user has selected Ensembl Transcript data source as a part of output solution.
	 */
	private java.lang.Boolean ensemblTranscriptAsOutput;
	
	/**
	 * The boolean value 'true' denotes  that the user has selected GenBank mRNA data source as a part of output solution.
	 */
	private java.lang.Boolean genbankmRNAAsOutput;
	
	/**
	 * The boolean value 'true' denotes  that the user has selected RefSeq mRNA data source as a part of output solution.
	 */
	private java.lang.Boolean refseqmRNAAsOutput;
	
	/**
	 * Returns the unique identifier assigned to this MessengerRNA.
	 * @return unique identifier 
	 */
	public java.lang.Long getId()
	{
		return id;
	}
	
	/**
	 *  Sets the unique identifier to this MessengerRNA.
	 * @param id
	 */
	public void setId(java.lang.Long id)
	{
		this.id = id;
	}

	/**
	 * Returns the unique identifier assigned to a transcript by ENSEMBL. 
	 * @return ensemblTranscriptId
	 */
	public java.lang.String getEnsemblTranscriptId()
	{
		return ensemblTranscriptId;
	}
	/**
	 * Sets the genomic identifier assigned to a transcript by ENSEMBL. It is used to uniquely identify
	 * a transcript in the ENSEMBL system.
	 * @param ensemblTranscriptId
	 */
	public void setEnsemblTranscriptId(java.lang.String ensemblTranscriptId)
	{
		this.ensemblTranscriptId = ensemblTranscriptId;
	}
	
	/**
	 * Returns the accession number, the unique identifier assigned to the entire sequence 
	 * record when the record is submitted to GenBank
	 * @return genbankAccession
	 */
	
	public java.lang.String getGenbankAccession()
	{
		return genbankAccession;
	}
	/**
	 * Sets the accession number, the unique identifier assigned to the entire sequence 
	 * record when the record is submitted to GenBank
	 * @param genbankAccession
	 */
	public void setGenbankAccession(java.lang.String genbankAccession)
	{
		this.genbankAccession = genbankAccession;
	}

	/**
	 * Returns the unique identifier for a Messenger RNA sequence assigned in the RefSeq data collection.
	 * @return refseqId
	 */

	public java.lang.String getRefseqId()
	{
		return refseqId;
	}
	/**
	 * Sets the unique identifier for a Messenger RNA sequence assigned in the RefSeq data collection.
	 * @param refseqId
	 */
	public void setRefseqId(java.lang.String refseqId)
	{
		this.refseqId = refseqId;
	}

	/**
	 * Returns the boolean value denoting Ensembl Transcript data source is selected as a part of output solution.
	 * @return ensemblTranscriptAsOutput
	 */

	public java.lang.Boolean getEnsemblTranscriptAsOutput()
	{
		return ensemblTranscriptAsOutput;
	}
	/**
	 * Sets the boolean value denoting Ensembl Transcript data source is selected as a part of output solution.
	 * @param ensemblTranscriptAsOutput
	 */
	public void setEnsemblTranscriptAsOutput(java.lang.Boolean ensemblTranscriptAsOutput)
	{
		this.ensemblTranscriptAsOutput = ensemblTranscriptAsOutput;
	}
	/**
	 * Returns the boolean value denoting GenBank mRNA data source is selected as a part of output solution.
	 * @return genbankmRNAAsOutput
	 */
	public java.lang.Boolean getGenbankmRNAAsOutput()
	{
		return genbankmRNAAsOutput;
	}
	/**
	 * Sets the boolean value denoting GenBank mRNA data source is selected as a part of output solution.
	 * @param genbankmRNAAsOutput
	 */
	public void setGenbankmRNAAsOutput(java.lang.Boolean genbankmRNAAsOutput)
	{
		this.genbankmRNAAsOutput = genbankmRNAAsOutput;
	}
	/**
	 * Returns the boolean value denoting Refseq mRNA data source is selected as a part of output solution.
	 * @return refseqmRNAAsOutput
	 */
	public java.lang.Boolean getRefseqmRNAAsOutput()
	{
		return refseqmRNAAsOutput;
	}
	/**
	 * Sets the boolean value denoting Refseq mRNA data source is selected as a part of output solution.
	 * @param refseqmRNAAsOutput
	 */
	public void setRefseqmRNAAsOutput(java.lang.Boolean refseqmRNAAsOutput)
	{
		this.refseqmRNAAsOutput = refseqmRNAAsOutput;
	}
	
	/**
	 * Associated collection of GenomicIdentifierSet objects with this MessengerRNA.
	 * @see edu.wustl.geneconnect.domain.GenomicIdentifierSet
	 */
	private java.util.Collection genomicIdentifierSetCollection = new java.util.HashSet();
	
	/**
	 * Returns associated collection of GenomicIdentifierSet objects with this MessengerRNA.
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

				edu.wustl.geneconnect.domain.MessengerRNA thisIdSet = new edu.wustl.geneconnect.domain.MessengerRNA();
				thisIdSet.setId(this.getId());
				java.util.Collection resultList = applicationService.search(
						"edu.wustl.geneconnect.domain.GenomicIdentifierSet", thisIdSet);
				genomicIdentifierSetCollection = resultList;
				return resultList;

			}
			catch (Exception ex)
			{
				System.out
						.println("MessengerRNA:getGenomicIdentifierSetCollection throws exception ... ...");
				ex.printStackTrace();
			}
		}
		return genomicIdentifierSetCollection;
	}
	/**
	 * Sets the collection of GenomicIdentifierSet objects with this MessengerRNA.
	 * @param genomicIdentifierSetCollection
	 */
	public void setGenomicIdentifierSetCollection(
			java.util.Collection genomicIdentifierSetCollection)
	{
		this.genomicIdentifierSetCollection = genomicIdentifierSetCollection;
	}
	/**
	 * Associated collection of Protein objects with this MessengerRNA.
	 * @see edu.wustl.geneconnect.domain.Protein
	 */
	private java.util.Collection proteinCollection = new java.util.HashSet();
	/**
	 * Returns associated collection of Protein objects with this MessengerRNA.
	 * @return proteinCollection
	 */
	public java.util.Collection getProteinCollection()
	{
		try
		{
			if (proteinCollection.size() == 0)
			{
			}
		}
		catch (Exception e)
		{
			ApplicationService applicationService = ApplicationServiceProvider
					.getApplicationService();
			try
			{

				edu.wustl.geneconnect.domain.MessengerRNA thisIdSet = new edu.wustl.geneconnect.domain.MessengerRNA();
				thisIdSet.setId(this.getId());
				java.util.Collection resultList = applicationService.search(
						"edu.wustl.geneconnect.domain.Protein", thisIdSet);
				proteinCollection = resultList;
				return resultList;

			}
			catch (Exception ex)
			{
				System.out.println("MessengerRNA:getProteinCollection throws exception ... ...");
				ex.printStackTrace();
			}
		}
		return proteinCollection;
	}
	/**
	 * Sets collection of Protein objects with this MessengerRNA.
	 * @param proteinCollection
	 */
	public void setProteinCollection(java.util.Collection proteinCollection)
	{
		this.proteinCollection = proteinCollection;
	}

	/**
	 * Associated collection of Gene objects with this MessengerRNA.
	 * @see edu.wustl.geneconnect.domain.Gene
	 */
	private java.util.Collection geneCollection = new java.util.HashSet();
	
	/**
	 * Returns associated collection of Gene objects with this MessengerRNA.
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

				edu.wustl.geneconnect.domain.MessengerRNA thisIdSet = new edu.wustl.geneconnect.domain.MessengerRNA();
				thisIdSet.setId(this.getId());
				java.util.Collection resultList = applicationService.search(
						"edu.wustl.geneconnect.domain.Gene", thisIdSet);
				geneCollection = resultList;
				return resultList;

			}
			catch (Exception ex)
			{
				System.out.println("MessengerRNA:getGeneCollection throws exception ... ...");
				ex.printStackTrace();
			}
		}
		return geneCollection;
	}
	/**
	 * Sets collection of Gene objects with this MessengerRNA.
	 * @param geneCollection
	 */
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