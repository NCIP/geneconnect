/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.metadata.domain.TableJoinInformation</p> 
 */
package edu.wustl.geneconnect.metadata.domain;

/**
 * This class describes the table join metadata information 
 * @author mahesh_nalkande
 * @version 1.0
 */
public class TableJoinInformation
{
	/**
	 * Id of source table which is being joined
	 */
	private Long sourceTableId;
	/**
	 * Id of target table which is being joined
	 */
	private Long destinationTableId;
	/**
	 * Id of the column from the source table which is used to define joining constraint
	 */
	private Long sourceColumnId;
	/**
	 * Id of the column from the target table which is used to define joining constraint
	 */
	private Long destinationColumnId;

	/**
	 * Default constructor
	 */
	public TableJoinInformation()
	{
	}

	/**
	 * @param sourceTableId Id of source table which is being joined
	 * @param destinationTableId Id of target table which is being joined
	 * @param sourceColumnId Id of the column from the source table which is used to define joining constraint
	 * @param destinationColumnId Id of the column from the target table which is used to define joining constraint
	 */
	public TableJoinInformation(Long sourceTableId, Long destinationTableId, Long sourceColumnId,
			Long destinationColumnId)
	{
		super();
		this.sourceTableId = sourceTableId;
		this.destinationTableId = destinationTableId;
		this.sourceColumnId = sourceColumnId;
		this.destinationColumnId = destinationColumnId;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return super.hashCode();
	}

	/**
	 * Getter method for destinationColumnId.
	 * @return Returns the destinationColumnId.
	 */
	public Long getDestinationColumnId()
	{
		return destinationColumnId;
	}

	/**
	 * Setter method for destinationColumnId
	 * @param destinationColumnId The destinationColumnId to set.
	 */
	public void setDestinationColumnId(Long destinationColumnId)
	{
		this.destinationColumnId = destinationColumnId;
	}

	/**
	 * Getter method for destinationTableId.
	 * @return Returns the destinationTableId.
	 */
	public Long getDestinationTableId()
	{
		return destinationTableId;
	}

	/**
	 * Setter method for destinationTableId
	 * @param destinationTableId The destinationTableId to set.
	 */
	public void setDestinationTableId(Long destinationTableId)
	{
		this.destinationTableId = destinationTableId;
	}

	/**
	 * Getter method for sourceColumnId.
	 * @return Returns the sourceColumnId.
	 */
	public Long getSourceColumnId()
	{
		return sourceColumnId;
	}

	/**
	 * Setter method for sourceColumnId
	 * @param sourceColumnId The sourceColumnId to set.
	 */
	public void setSourceColumnId(Long sourceColumnId)
	{
		this.sourceColumnId = sourceColumnId;
	}

	/**
	 * Getter method for sourceTableId.
	 * @return Returns the sourceTableId.
	 */
	public Long getSourceTableId()
	{
		return sourceTableId;
	}

	/**
	 * Setter method for sourceTableId
	 * @param sourceTableId The sourceTableId to set.
	 */
	public void setSourceTableId(Long sourceTableId)
	{
		this.sourceTableId = sourceTableId;
	}
}