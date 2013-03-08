/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.metadata.domain.GenomicIdentifierSetOntWithAllDSIds</p> 
 */
package edu.wustl.geneconnect.metadata.domain;

import java.util.List;

import edu.wustl.geneconnect.GeneConnectServerConstants;
import edu.wustl.geneconnect.domain.Gene;
import edu.wustl.geneconnect.domain.MessengerRNA;
import edu.wustl.geneconnect.domain.Protein;
import edu.wustl.geneconnect.postwork.SummaryReflectionUtil;


/**
 * Genomic Identifier set along with all genomic Ids and Id of ONT associated with it.
 * @author mahesh_nalkande
 * @version 1.0
 */
public class GenomicIdentifierSetOntWithAllDSIds implements Comparable
{
	private Gene gene = null;
	private MessengerRNA messengerRNA = null;
	private Protein protein = null;
	private long ontId = -1;
	
	/**
	 * Getter method for gene.
	 * @return Returns the gene.
	 */
	public Gene getGene()
	{
		return gene;
	}
	/**
	 * Setter method for gene
	 * @param gene The gene to set.
	 */
	public void setGene(Gene gene)
	{
		this.gene = gene;
	}
	/**
	 * Getter method for messengerRNA.
	 * @return Returns the messengerRNA.
	 */
	public MessengerRNA getMessengerRNA()
	{
		return messengerRNA;
	}
	/**
	 * Setter method for messengerRNA
	 * @param messengerRNA The messengerRNA to set.
	 */
	public void setMessengerRNA(MessengerRNA messengerRNA)
	{
		this.messengerRNA = messengerRNA;
	}
	/**
	 * Getter method for ontId.
	 * @return Returns the ontId.
	 */
	public long getOntId()
	{
		return ontId;
	}
	/**
	 * Setter method for ontId
	 * @param ontId The ontId to set.
	 */
	public void setOntId(long ontId)
	{
		this.ontId = ontId;
	}
	/**
	 * Getter method for protein.
	 * @return Returns the protein.
	 */
	public Protein getProtein()
	{
		return protein;
	}
	/**
	 * Setter method for protein
	 * @param protein The protein to set.
	 */
	public void setProtein(Protein protein)
	{
		this.protein = protein;
	}
	
	/**
	 * Default constructor
	 */
	public GenomicIdentifierSetOntWithAllDSIds()
	{
		super();
	}

	/**
	 * @param geneId
	 * @param mrnaId
	 * @param proteinId
	 */
	public GenomicIdentifierSetOntWithAllDSIds(Gene gene, MessengerRNA messengerRNA, Protein protein)
	{
		super();
		this.gene = gene;
		this.messengerRNA = messengerRNA;
		this.protein = protein;
	}
	
	/**
	 * @param geneId
	 * @param mrnaId
	 * @param proteinId
	 * @param ontId
	 */
	public GenomicIdentifierSetOntWithAllDSIds(Gene gene, MessengerRNA messengerRNA, Protein protein, long ontId)
	{
		super();
		this.gene = gene;
		this.messengerRNA = messengerRNA;
		this.protein = protein;
		this.ontId = ontId;
	}
	
	/**
	 *  
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 * A negative integer, zero, or a positive integer as this object is less than, equal to, 
	 * or greater than the specified object. 
	 */
	public int compareTo(Object arg0)
	{
		GenomicIdentifierSetOntWithAllDSIds dest = (GenomicIdentifierSetOntWithAllDSIds) arg0; 
		List srcGenomicIdentifierValues = SummaryReflectionUtil.getGenomicIdentifierSetValues(gene,
				messengerRNA, protein);
		List destGenomicIdentifierValues = SummaryReflectionUtil.getGenomicIdentifierSetValues(dest.gene,
				dest.messengerRNA, dest.protein);
		
		Comparable currentSrcGenomicIdentifierValues = null;
		
		for (int i = 0; i < srcGenomicIdentifierValues.size(); i++)
		{
			if (srcGenomicIdentifierValues.get(i).equals(""))
			{
				if (destGenomicIdentifierValues.get(i).equals(""))
				{
					continue;
				}
				else
				{
					return -1; 
				}
			}
			else if (destGenomicIdentifierValues.get(i).equals(""))
			{
				return 1;
			}
			
			if (!srcGenomicIdentifierValues.get(i).equals(destGenomicIdentifierValues.get(i)))
			{
				currentSrcGenomicIdentifierValues = (Comparable) srcGenomicIdentifierValues.get(i);
				return currentSrcGenomicIdentifierValues.compareTo(destGenomicIdentifierValues.get(i));
			}
		}
		
		if (this.ontId < dest.ontId)
		{
			return -1;
		}
		else if (this.ontId > dest.ontId)
		{
			return 1;
		}
		return 0;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj)
	{
		GenomicIdentifierSetOntWithAllDSIds dest = (GenomicIdentifierSetOntWithAllDSIds) obj;
		List srcGenomicIdentifierValues = SummaryReflectionUtil.getGenomicIdentifierSetValues(gene,
				messengerRNA, protein);
		List destGenomicIdentifierValues = SummaryReflectionUtil.getGenomicIdentifierSetValues(
				dest.gene, dest.messengerRNA, dest.protein);
		
		for (int i = 0; i < srcGenomicIdentifierValues.size(); i++)
		{
			if (!srcGenomicIdentifierValues.get(i).equals(destGenomicIdentifierValues.get(i)))
			{
				return false;
			}
		}
		
		if (this.ontId != dest.ontId)
		{
			return false;
		}
		
		return true;
	}
	
	public boolean equalSetIdsOnly(Object obj)
	{
		GenomicIdentifierSetOntWithAllDSIds dest = (GenomicIdentifierSetOntWithAllDSIds) obj;
		List srcGenomicIdentifierValues = SummaryReflectionUtil.getGenomicIdentifierSetValues(gene,
				messengerRNA, protein);
		List destGenomicIdentifierValues = SummaryReflectionUtil.getGenomicIdentifierSetValues(
				dest.gene, dest.messengerRNA, dest.protein);

		for (int i = 0; i < srcGenomicIdentifierValues.size(); i++)
		{
			if (!srcGenomicIdentifierValues.get(i).equals(destGenomicIdentifierValues.get(i)))
			{
				return false;
			}
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		String genomicIdSetOntRecord = SummaryReflectionUtil.getGeneValueString(gene)
				+ GeneConnectServerConstants.FIELD_DELIMITER
				+ SummaryReflectionUtil.getMessengeRNAValuesString(messengerRNA)
				+ GeneConnectServerConstants.FIELD_DELIMITER
				+ SummaryReflectionUtil.getProteinValuesString(protein)
				+ GeneConnectServerConstants.FIELD_DELIMITER + ontId;

		return genomicIdSetOntRecord;
	}
}
