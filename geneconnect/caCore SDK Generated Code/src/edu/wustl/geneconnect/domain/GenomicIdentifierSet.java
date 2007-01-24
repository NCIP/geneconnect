
package edu.wustl.geneconnect.domain;

import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationServiceProvider;

/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */

/**
 * Represents a distinct set of genomic identifiers that are linked using one or more orders of node 
 * traversal through the genomic identifier graph 
 * 
 */

public class GenomicIdentifierSet implements java.io.Serializable
{

	private static final long serialVersionUID = 1234567890L;
	/**
	 * Unique identifier of Genomic Identifier Set.
	 */
	private java.lang.Long id;
	/**
	 * Returns the unique identifier of Genomic Identifier Set.
	 * @return unique identifier
	 */
	public java.lang.Long getId()
	{
		return id;
	}
	/**
	 * Sets the unique identifier of Genomic Identifier Set.
	 * @param id
	 */
	public void setId(java.lang.Long id)
	{
		this.id = id;
	}
	/**
	 * The ratio of the number of occurrences of a 'set' of genomic identifiers to the total number 
	 * of rows in the result set
	 */
	private java.lang.Float confidenceScore;
	/**
	 * Returns the ratio of the number of occurrences of a 'set' of genomic identifiers to 
	 * the total number of rows in the result set
	 * @return confidenceScore
	 */
	public java.lang.Float getConfidenceScore()
	{
		return confidenceScore;
	}
	/**
	 * Sets the ratio of the number of occurrences of a 'set' of genomic identifiers to the total number of rows in the result set.
	 *  
	 * @param confidenceScore
	 */
	public void setConfidenceScore(java.lang.Float confidenceScore)
	{
		this.confidenceScore = confidenceScore;
	}
	/**
	 * Represents a link in the genomic identifier graph.  The collection of these links constitutes the order 
	 * or node traversal through the genomic identifier graph that resulted in this distinct set of genomic identifiers.
	 * @see edu.wustl.geneconnect.domain.OrderOfNodeTraversal
	 */
	private java.util.Collection orderOfNodeTraversalCollection;
	/**
	 * Returns the associated collection of order or node traversal through the genomic identifier graph that 
	 * resulted in this distinct set of genomic identifiers.
	 * @return orderOfNodeTraversalCollection
	 */
	public java.util.Collection getOrderOfNodeTraversalCollection()
	{
		try
		{
			if (orderOfNodeTraversalCollection.size() == 0)
			{
			}
		}
		catch (Exception e)
		{
			ApplicationService applicationService = ApplicationServiceProvider
					.getApplicationService();
			try
			{

				edu.wustl.geneconnect.domain.GenomicIdentifierSet thisIdSet = new edu.wustl.geneconnect.domain.GenomicIdentifierSet();
				thisIdSet.setId(this.getId());
				java.util.Collection resultList = applicationService.search(
						"edu.wustl.geneconnect.domain.OrderOfNodeTraversal", thisIdSet);
				orderOfNodeTraversalCollection = resultList;
				return resultList;

			}
			catch (Exception ex)
			{
				System.out
						.println("GenomicIdentifierSet:getOrderOfNodeTraversalCollection throws exception ... ...");
				ex.printStackTrace();
			}
		}
		return orderOfNodeTraversalCollection;
	}
	/**
	 * Sets the collection of order or node traversal through the genomic identifier graph that 
	 * resulted in this distinct set of genomic identifiers.
	 * @param orderOfNodeTraversalCollection
	 */
	public void setOrderOfNodeTraversalCollection(
			java.util.Collection orderOfNodeTraversalCollection)
	{
		this.orderOfNodeTraversalCollection = orderOfNodeTraversalCollection;
	}
	/**
	 * Associated Protein object  with this GenomicIdentifierSet.
	 */
	private edu.wustl.geneconnect.domain.Protein protein;
	/**
	 * Returns the associated Protein object  with this GenomicIdentifierSet.
	 * @return protein
	 */
	public edu.wustl.geneconnect.domain.Protein getProtein()
	{

		ApplicationService applicationService = ApplicationServiceProvider.getApplicationService();
		edu.wustl.geneconnect.domain.GenomicIdentifierSet thisIdSet = new edu.wustl.geneconnect.domain.GenomicIdentifierSet();
		thisIdSet.setId(this.getId());

		try
		{
			java.util.List resultList = applicationService.search(
					"edu.wustl.geneconnect.domain.Protein", thisIdSet);
			if (resultList != null && resultList.size() > 0)
			{
				protein = (edu.wustl.geneconnect.domain.Protein) resultList.get(0);
			}

		}
		catch (Exception ex)
		{
			System.out.println("GenomicIdentifierSet:getProtein throws exception ... ...");
			ex.printStackTrace();
		}
		return protein;

	}
	/**
	 * Sets the Protein object  with this GenomicIdentifierSet.
	 * @param protein
	 */
	public void setProtein(edu.wustl.geneconnect.domain.Protein protein)
	{
		this.protein = protein;
	}
	/**
	 * Associated MessengerRNA object with this GenomicIdentifierSet.
	 */
	private edu.wustl.geneconnect.domain.MessengerRNA messengerRNA;
	/**
	 * Returns the associated MessengerRNA  object with this GenomicIdentifierSet.
	 * @return messengerRNA
	 */
	public edu.wustl.geneconnect.domain.MessengerRNA getMessengerRNA()
	{

		ApplicationService applicationService = ApplicationServiceProvider.getApplicationService();
		edu.wustl.geneconnect.domain.GenomicIdentifierSet thisIdSet = new edu.wustl.geneconnect.domain.GenomicIdentifierSet();
		thisIdSet.setId(this.getId());

		try
		{
			java.util.List resultList = applicationService.search(
					"edu.wustl.geneconnect.domain.MessengerRNA", thisIdSet);
			if (resultList != null && resultList.size() > 0)
			{
				messengerRNA = (edu.wustl.geneconnect.domain.MessengerRNA) resultList.get(0);
			}

		}
		catch (Exception ex)
		{
			System.out.println("GenomicIdentifierSet:getMessengerRNA throws exception ... ...");
			ex.printStackTrace();
		}
		return messengerRNA;

	}
/**
 * Sets the MessengerRNA  object  with this GenomicIdentifierSet.
 * @param messengerRNA
 */
	public void setMessengerRNA(edu.wustl.geneconnect.domain.MessengerRNA messengerRNA)
	{
		this.messengerRNA = messengerRNA;
	}
/**
 * Associated Gene object  with this GenomicIdentifierSet.
 */
	private edu.wustl.geneconnect.domain.Gene gene;
/**
 * Returns the associated Gene object with this GenomicIdentifierSet.
 * @return gene
 */
	public edu.wustl.geneconnect.domain.Gene getGene()
	{

		ApplicationService applicationService = ApplicationServiceProvider.getApplicationService();
		edu.wustl.geneconnect.domain.GenomicIdentifierSet thisIdSet = new edu.wustl.geneconnect.domain.GenomicIdentifierSet();
		thisIdSet.setId(this.getId());

		try
		{
			java.util.List resultList = applicationService.search(
					"edu.wustl.geneconnect.domain.Gene", thisIdSet);
			if (resultList != null && resultList.size() > 0)
			{
				gene = (edu.wustl.geneconnect.domain.Gene) resultList.get(0);
			}

		}
		catch (Exception ex)
		{
			System.out.println("GenomicIdentifierSet:getGene throws exception ... ...");
			ex.printStackTrace();
		}

		return gene;

	}
/**
 * Sets the Gene object with this GenomicIdentifierSet.
 * @param gene
 */
	public void setGene(edu.wustl.geneconnect.domain.Gene gene)
	{
		this.gene = gene;
	}
/**
 * Contains the collection actual instance of each genomic identifier that satisfies the
 * GeneConnect query and its frequency across all result sets.
 * @see edu.wustl.geneconnect.domain.ConsensusIdentifierData
 */
	private java.util.Collection consensusIdentifierDataCollection = new java.util.HashSet();
	/**
	 *  Modified the implementation generated by caCore SDK as the ConsensusIdentifierData object
	 *  is set in a  business logic while calculating frequency.
	 *  Returns the associated collection of actual instance of each genomic identifier that satisfies 
	 *  the GeneConnect query and its frequency across all result sets.
	 *   
	 * @return consensusIdentifierDataCollection
	 */
	public java.util.Collection getConsensusIdentifierDataCollection()
	{
		/**
		 * Removed a statement calling ApplicationService to get ConsensusIdentifierData.
		 * The ConsensusIdentifierData object is set in a  business logic (OrmDaoImpl.java)  
		 * while calculating frequency. 
		 */
		return consensusIdentifierDataCollection;
	}
/**
 * Sets the collection of actual instance of each genomic identifier that satisfies the 
 * GeneConnect query and its frequency across all result sets.
 * @param consensusIdentifierDataCollection
 */
	public void setConsensusIdentifierDataCollection(
			java.util.Collection consensusIdentifierDataCollection)
	{
		this.consensusIdentifierDataCollection = consensusIdentifierDataCollection;
	}
/**
 * Encapsulates the result set of a GeneConnect query
 */
	
	private edu.wustl.geneconnect.GenomicIdentifierSolution genomicIdentifierSolution;
/**
 * Returns the assciated GenomicIdentifierSolution object with this GenomicIdentifierSet.
 * @return genomicIdentifierSolution
 */
	public edu.wustl.geneconnect.GenomicIdentifierSolution getGenomicIdentifierSolution()
	{
		return genomicIdentifierSolution;
	}
/**
 * Sets the GenomicIdentifierSolution object with this GenomicIdentifierSet.
 * @param genomicIdentifierSolution
 */
	public void setGenomicIdentifierSolution(
			edu.wustl.geneconnect.GenomicIdentifierSolution genomicIdentifierSolution)
	{
		this.genomicIdentifierSolution = genomicIdentifierSolution;
	}

	public boolean equals(Object obj)
	{
		boolean eq = false;
		if (obj instanceof GenomicIdentifierSet)
		{
			GenomicIdentifierSet c = (GenomicIdentifierSet) obj;
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