/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.metadata.domain.BaseTable</p> 
 */

package edu.wustl.geneconnect.metadata.domain;

/**
 * This class describes the base table which stores mapping Ids among 2 adjucent data sources
 * @author mahesh_nalkande
 * @version 1.0
 */
public class BaseTable
{

	/**
	 * Table Id
	 */
	private Long Id;
	/**
	 * Name of the base Table
	 */
	private String name;
	/**
	 * Id of source data source which is connected by this base table.
	 */
	private Long sourceDataSourceId;
	/**
	 * Id of Destination data source which is connected by this base table.
	 */
	private Long destinationDataSourceId;

	/**
	 * Default constructor
	 */
	public BaseTable()
	{
		super();
	}

	/**
	 * @param id Table Id
	 * @param name Name of the base Table
	 * @param sourceDataSourceId Id of source data source which is connected by this base table.
	 * @param destinationDataSourceId Id of Destination data source which is connected by this base table.
	 */
	public BaseTable(Long id, String name, Long sourceDataSourceId, Long destinationDataSourceId)
	{
		super();
		Id = id;
		this.name = name;
		this.sourceDataSourceId = sourceDataSourceId;
		this.destinationDataSourceId = destinationDataSourceId;
	}

	/**
	 * Getter method for destinationDataSourceId.
	 * @return Returns the destinationDataSourceId.
	 */
	public Long getDestinationDataSourceId()
	{
		return destinationDataSourceId;
	}

	/**
	 * Setter method for destinationDataSourceId
	 * @param destinationDataSourceId The destinationDataSourceId to set.
	 */
	public void setDestinationDataSourceId(Long destinationDataSourceId)
	{
		this.destinationDataSourceId = destinationDataSourceId;
	}

	/**
	 * Getter method for id.
	 * @return Returns the id.
	 */
	public Long getId()
	{
		return Id;
	}

	/**
	 * Setter method for id
	 * @param id The id to set.
	 */
	public void setId(Long id)
	{
		Id = id;
	}

	/**
	 * Getter method for name.
	 * @return Returns the name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Setter method for name
	 * @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Getter method for sourceDataSourceId.
	 * @return Returns the sourceDataSourceId.
	 */
	public Long getSourceDataSourceId()
	{
		return sourceDataSourceId;
	}

	/**
	 * Setter method for sourceDataSourceId
	 * @param sourceDataSourceId The sourceDataSourceId to set.
	 */
	public void setSourceDataSourceId(Long sourceDataSourceId)
	{
		this.sourceDataSourceId = sourceDataSourceId;
	}
}