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
 * Contains gene genomic identifiers.
 */

public class Gene implements java.io.Serializable
{	

	private static final long serialVersionUID = 1234567890L;

	/**
	 * Unique identifier for the Gene object.
	 */
	private java.lang.Long id;
	
	/**
	 * Entrez Gene (formerly LocusLink) is a National Center for Biotechnology Information (NCBI) database 
	 * for gene-specific information. It does not include all known or predicted genes; instead Entrez Gene 
	 * focuses on genomes that have been completely sequenced.
	 */
	private java.lang.Long entrezGeneId;
	
	/**
	 * The unique identifier assigned to a gene by ENSEMBL. It is used to uniquely identify 
	 * a gene in the ENSEMBL system.
	 */
	private java.lang.String ensemblGeneId;
	
	/**
	 * The unique identifiers for a gene cluster assigned by UniGene. 
	 * The Cluster IDs begin with the species identifier (e.g., Hs) 
	 * followed by a period and then the cluster ID number.
	 */
	private java.lang.String unigeneClusterId;
	
	/**
	 * The boolean value 'true' denotes that the user has selected Ensembl Gene data source as a part of output solution.
	 */
	private java.lang.Boolean ensemblGeneAsOutput;
	/**
	 * The boolean value 'true' denotes that the user has selected Entrez Gene data source as a part of output solution. 
	 */
	private java.lang.Boolean entrezGeneAsOutput;
	/**
	 * The boolean value 'true' denotes that the user has selected UniGene data source as a part of output solution. 
	 */
	private java.lang.Boolean unigeneAsOutput;

	
	/**
	 * Associated collection of GenomicIdentifierSet objects with this Gene.
	 * @see edu.wustl.geneconnect.domain.GenomicIdentifierSet
	 */
	private java.util.Collection genomicIdentifierSetCollection = new java.util.HashSet();

	/**
	 * Associated MessengerRNA object collection.
	 * @see edu.wustl.geneconnect.domain.MessengerRNA
	 */
	private java.util.Collection messengerRNACollection = new java.util.HashSet();
	
	/**
	 * Associated Protein object collection with this Gene.
	 * @see edu.wustl.geneconnect.domain.Protein
	 */
	private java.util.Collection proteinCollection = new java.util.HashSet();
	
	
	
	/**
	 * Returns the unique identifier assigned to this Gene.
	 * @return unique identifier 
	 */
	public java.lang.Long getId()
	{
		return id;
	}
	/**
	 *  Sets the unique identifier to this Gene.
	 * @param id
	 */
	public void setId(java.lang.Long id)
	{
		this.id = id;
	}
	
	/**
	 * Returns the genomic identifier assigned to a gene by ENSEMBL. It is used to uniquely identify 
	 * a gene in the ENSEMBL system.
	 * @return ensemblGeneId
	 */
	public java.lang.String getEnsemblGeneId()
	{
		return ensemblGeneId;
	}
	/**
	 * Sets the genomic identifier assigned to a gene by ENSEMBL. It is used to uniquely identify
	 * a gene in the ENSEMBL system.
	 * @param ensemblGeneId
	 */
	public void setEnsemblGeneId(java.lang.String ensemblGeneId)
	{
		this.ensemblGeneId = ensemblGeneId;
	}
	
	
	/**
	 * Returns the genomic identifier assigned to a gene by Entrez.
	 * @return entrezGeneId
	 */
	public java.lang.Long getEntrezGeneId()
	{
		return entrezGeneId;
	}
	/**
	 * Sets the genomic identifier assigned to a gene by Entrez.
	 * @param entrezGeneId
	 */
	public void setEntrezGeneId(java.lang.Long entrezGeneId)
	{
		this.entrezGeneId = entrezGeneId;
	}
	
	/**
	 * Returns the unique identifier for a gene cluster assigned by UniGene.
	 * @return unigeneClusterId
	 */
	public java.lang.String getUnigeneClusterId()
	{
		return unigeneClusterId;
	}
	/**
	 * Sets the unique identifier for a gene cluster assigned by UniGene.
	 * @param unigeneClusterId
	 */
	public void setUnigeneClusterId(java.lang.String unigeneClusterId)
	{
		this.unigeneClusterId = unigeneClusterId;
	}
	 
	 
	 
	
	/**
	 * Returns the boolean value denoting Ensembl Gene data source selected as a part of output solution.
	 * @return ensemblGeneAsOutput
	 */
	public java.lang.Boolean getEnsemblGeneAsOutput()
	{
		return ensemblGeneAsOutput;
	}
	/**
	 * Sets the boolean value denoting Ensembl Gene data source selected as a part of output solution.
	 * @param ensemblGeneAsOutput
	 */
	public void setEnsemblGeneAsOutput(java.lang.Boolean ensemblGeneAsOutput)
	{
		this.ensemblGeneAsOutput = ensemblGeneAsOutput;
	}
	/**
	 * Returns the boolean value denoting Entrez Gene data source selected as a part of output solution.
	 * @return entrezGeneAsOutput
	 */
	public java.lang.Boolean getEntrezGeneAsOutput()
	{
		return entrezGeneAsOutput;
	}
	/**
	 * Sets the boolean value denoting Entrez Gene data source selected as a part of output solution.
	 * @param entrezGeneAsOutput
	 */
	public void setEntrezGeneAsOutput(java.lang.Boolean entrezGeneAsOutput)
	{
		this.entrezGeneAsOutput = entrezGeneAsOutput;
	}
	/**
	 * Returns the boolean value denoting UniGene data source selected as a part of output solution.
	 * @return unigeneAsOutput
	 */
	public java.lang.Boolean getUnigeneAsOutput()
	{
		return unigeneAsOutput;
	}
	/**
	 * Sets the boolean value denoting UniGene data source selected as a part of output solution.
	 * @param unigeneAsOutput
	 */
	public void setUnigeneAsOutput(java.lang.Boolean unigeneAsOutput)
	{
		this.unigeneAsOutput = unigeneAsOutput;
	}
	
	/**
	 * Returns associated collection of GenomicIdentifierSet objects with this Gene.
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

				edu.wustl.geneconnect.domain.Gene thisIdSet = new edu.wustl.geneconnect.domain.Gene();
				thisIdSet.setId(this.getId());
				java.util.Collection resultList = applicationService.search(
						"edu.wustl.geneconnect.domain.GenomicIdentifierSet", thisIdSet);
				genomicIdentifierSetCollection = resultList;
				return resultList;

			}
			catch (Exception ex)
			{
				System.out
						.println("Gene:getGenomicIdentifierSetCollection throws exception ... ...");
				ex.printStackTrace();
			}
		}
		return genomicIdentifierSetCollection;
	}
	/**
	 * Sets the collection of GenomicIdentifierSet objects with this Gene.
	 * @param genomicIdentifierSetCollection
	 */
	public void setGenomicIdentifierSetCollection(
			java.util.Collection genomicIdentifierSetCollection)
	{
		this.genomicIdentifierSetCollection = genomicIdentifierSetCollection;
	}
	
	/**
	 * Returns associated collection of MessengerRNA objects with this Gene.
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

				edu.wustl.geneconnect.domain.Gene thisIdSet = new edu.wustl.geneconnect.domain.Gene();
				thisIdSet.setId(this.getId());
				java.util.Collection resultList = applicationService.search(
						"edu.wustl.geneconnect.domain.MessengerRNA", thisIdSet);
				messengerRNACollection = resultList;
				return resultList;

			}
			catch (Exception ex)
			{
				System.out.println("Gene:getMessengerRNACollection throws exception ... ...");
				ex.printStackTrace();
			}
		}
		return messengerRNACollection;
	}
	/**
	 * Sets the collection of MessengerRNA objects with this Gene.
	 * @param messengerRNACollection
	 */
	public void setMessengerRNACollection(java.util.Collection messengerRNACollection)
	{
		this.messengerRNACollection = messengerRNACollection;
	}
	
	/**
	 * Returns associated collection of Protein objects with this Gene.
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

				edu.wustl.geneconnect.domain.Gene thisIdSet = new edu.wustl.geneconnect.domain.Gene();
				thisIdSet.setId(this.getId());
				java.util.Collection resultList = applicationService.search(
						"edu.wustl.geneconnect.domain.Protein", thisIdSet);
				proteinCollection = resultList;
				return resultList;

			}
			catch (Exception ex)
			{
				System.out.println("Gene:getProteinCollection throws exception ... ...");
				ex.printStackTrace();
			}
		}
		return proteinCollection;
	}
	/**
	 * Sets the collection of Protein objects with this Gene.
	 * @param proteinCollection
	 */
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