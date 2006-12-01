/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.metadata.domain.GenomicIdentifierSet</p> 
 */
package edu.wustl.geneconnect.metadata.domain;


/**
 * Genomic Identifier set along with Id of ONT associated with it.
 * @author mahesh_nalkande
 * @version 1.0
 */
public class GenomicIdentifierSetOnt implements Comparable
{
	private long geneId = -1;
	private long mrnaId = -1;
	private long proteinId = -1;
	private long ontId = -1;
	private byte newlyAdded = 0;
	
	/**
	 * Getter method for geneId.
	 * @return Returns the geneId.
	 */
	public long getGeneId()
	{
		return geneId;
	}
	/**
	 * Setter method for geneId
	 * @param geneId The geneId to set.
	 */
	public void setGeneId(long geneId)
	{
		this.geneId = geneId;
	}
	/**
	 * Getter method for mrnaId.
	 * @return Returns the mrnaId.
	 */
	public long getMrnaId()
	{
		return mrnaId;
	}
	/**
	 * Setter method for mrnaId
	 * @param mrnaId The mrnaId to set.
	 */
	public void setMrnaId(long mrnaId)
	{
		this.mrnaId = mrnaId;
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
	 * Getter method for proteinId.
	 * @return Returns the proteinId.
	 */
	public long getProteinId()
	{
		return proteinId;
	}
	/**
	 * Setter method for proteinId
	 * @param proteinId The proteinId to set.
	 */
	public void setProteinId(long proteinId)
	{
		this.proteinId = proteinId;
	}
	
	
	/**
	 * Getter method for newlyAdded.
	 * @return Returns the newlyAdded.
	 */
	public byte getNewlyAdded()
	{
		return newlyAdded;
	}
	/**
	 * Setter method for newlyAdded
	 * @param newlyAdded The newlyAdded to set.
	 */
	public void setNewlyAdded(byte newlyAdded)
	{
		this.newlyAdded = newlyAdded;
	}
	
	/**
	 * Default Constructor 
	 */
	public GenomicIdentifierSetOnt()
	{
		super();
	}
	
	/**
	 * @param geneId
	 * @param mrnaId
	 * @param proteinId
	 */
	public GenomicIdentifierSetOnt(long geneId, long mrnaId, long proteinId)
	{
		super();
		this.geneId = geneId;
		this.mrnaId = mrnaId;
		this.proteinId = proteinId;
	}
	
	/**
	 * @param geneId
	 * @param mrnaId
	 * @param proteinId
	 * @param ontId
	 */
	public GenomicIdentifierSetOnt(long geneId, long mrnaId, long proteinId, long ontId)
	{
		super();
		this.geneId = geneId;
		this.mrnaId = mrnaId;
		this.proteinId = proteinId;
		this.ontId = ontId;
	}
	
	/**
	 * @param geneId
	 * @param mrnaId
	 * @param proteinId
	 * @param ontId
	 * @param newlyAdded
	 */
	public GenomicIdentifierSetOnt(long geneId, long mrnaId, long proteinId, long ontId,
			byte newlyAdded)
	{
		super();
		this.geneId = geneId;
		this.mrnaId = mrnaId;
		this.proteinId = proteinId;
		this.ontId = ontId;
		this.newlyAdded = newlyAdded;
	}
	/**
	 *  
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 * A negative integer, zero, or a positive integer as this object is less than, equal to, 
	 * or greater than the specified object. 
	 */
	public int compareTo(Object arg0)
	{
		GenomicIdentifierSetOnt dest = (GenomicIdentifierSetOnt) arg0; 
		if (this.geneId < dest.geneId)
		{
			return -1;
		}
		else if (this.geneId > dest.geneId)
		{
			return 1;
		}
		else if (this.mrnaId < dest.mrnaId)
		{
			return -1;
		}
		else if (this.mrnaId > dest.mrnaId)
		{
			return 1;
		}
		else if (this.proteinId < dest.proteinId)
		{
			return -1;
		}
		else if (this.proteinId > dest.proteinId)
		{
			return 1;
		}
		else if (this.ontId < dest.ontId)
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
		GenomicIdentifierSetOnt genomicIdentifierSetOnt = (GenomicIdentifierSetOnt) obj;
		if (this.geneId == genomicIdentifierSetOnt.geneId
				&& this.mrnaId == genomicIdentifierSetOnt.mrnaId
				&& this.proteinId == genomicIdentifierSetOnt.proteinId
				&& this.ontId == genomicIdentifierSetOnt.ontId)
			return true;
		else
			return false;
	}
	
	public boolean equalSetIdsOnly(Object obj)
	{
		GenomicIdentifierSetOnt genomicIdentifierSetOnt = (GenomicIdentifierSetOnt) obj;
		if (this.geneId == genomicIdentifierSetOnt.geneId
				&& this.mrnaId == genomicIdentifierSetOnt.mrnaId
				&& this.proteinId == genomicIdentifierSetOnt.proteinId)
			return true;
		else
			return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return "(" + geneId + " " + mrnaId + " " + proteinId + ")\n";
	}
}
