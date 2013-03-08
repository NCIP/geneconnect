/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.metadata.domain.TableColumn</p> 
 */

package edu.wustl.geneconnect.metadata.domain;

/**
 * This class describes the table columns in the base tables.
 * @author mahesh_nalkande
 * @version 1.0
 */
public class TableColumn
{

	/**
	 * Id of the column
	 */
	private Long id;
	/**
	 * Name of the column
	 */
	private String name;
	/**
	 * Id of the table to which this column belongs to
	 */
	private Long tableId;
	/**
	 * Id of the data source whose data this column contains 
	 */
	private Long dataSourceId;

	/**
	 * Default constructor
	 */
	public TableColumn()
	{
		super();
	}

	/**
	 * @param id Id of the column
	 * @param name Name of the column
	 * @param tableId Id of the table to which this column belongs to
	 */
	public TableColumn(Long id, String name, Long tableId)
	{
		super();
		this.id = id;
		this.name = name;
		this.tableId = tableId;
	}

	/**
	 * @param id Id of the column
	 * @param name Name of the column
	 * @param tableId Id of the table to which this column belongs to
	 * @param dataSourceId Id of the data source whose data this column contains 
	 */
	public TableColumn(Long id, String name, Long tableId, Long dataSourceId)
	{
		super();
		this.id = id;
		this.name = name;
		this.tableId = tableId;
		this.dataSourceId = dataSourceId;
	}

	/**
	 * Getter method for id.
	 * @return Returns the id.
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * Setter method for id
	 * @param id The id to set.
	 */
	public void setId(Long id)
	{
		this.id = id;
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
	 * Getter method for tableId.
	 * @return Returns the tableId.
	 */
	public Long getTableId()
	{
		return tableId;
	}

	/**
	 * Setter method for tableId
	 * @param tableId The tableId to set.
	 */
	public void setTableId(Long tableId)
	{
		this.tableId = tableId;
	}

	/**
	 * Getter method for dataSourceId.
	 * @return Returns the dataSourceId.
	 */
	public Long getDataSourceId()
	{
		return dataSourceId;
	}

	/**
	 * Setter method for dataSourceId
	 * @param dataSourceId The dataSourceId to set.
	 */
	public void setDataSourceId(Long dataSourceId)
	{
		this.dataSourceId = dataSourceId;
	}
}