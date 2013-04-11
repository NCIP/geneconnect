/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>ClassName: java edu.wustl.geneconnect.metadata.domain.DataSource</p> 
 */

package edu.wustl.geneconnect.metadata.domain;

/**
 * This class represents a Datasource such as Ensemble,Entrez,etc.
 * @author mahesh_nalkande
 * @version 1.0
 */
public class DataSource
{

	/**
	 * Data Source ID
	 */
	private Long id;
	/**
	 * Name of the DataSource
	 */
	private String name;
	/**
	 * Name of Genomic Identifier class(Gene,mRNA,Protein) to which this datasource belongs to.
	 */
	private String genomicIdentifierClassName;
	/**
	 * Class(Gene,mRNA,Protein) to which this datasource belongs to.
	 */
	private String className;
	/**
	 * Attribute name in the class which represents Id from this data source (e.g. EnsembleGeneId of Class Gene)   
	 */
	private String attributeName;
	/**
	 * Data type of attribuite (e.g. String , Long, etc.)
	 */
	private String attributeType;
	/**
	 * Table (Gene,mRNA,Protein) to which this datasource belongs to.
	 */
	private String tableName;
	/**
	 * Column name in the table which represents Id from this data source (e.g. EnsembleGeneId column of table Gene)
	 */
	private String columnName;
	/**
	 * Output attribute name for this DS
	 */
	private String outputAttribute;
	/**
	 * Row no. in which data source node will be displyed in the graph 
	 */
	private int rowNum;
	/**
	 * Column no. in which data source node will be displyed in the graph
	 */
	private int columnNum;

	/**
	 * Default constructor
	 */
	public DataSource()
	{
	}

	/**
	 * @param id Data Source ID
	 * @param name Name of the DataSource
	 * @param genomicIdentifierClassName  Name of Genomic Identifier class(Gene,mRNA,Protein) 
	 * to which this datasource belongs to.
	 * @param className Class(Gene,mRNA,Protein) to which this datasource belongs to.
	 * @param attributeName Attribute name in the class which represents Id from this
	 *  data source (e.g. EnsembleGeneId of Class Gene)
	 * @param attributeType Data type of attribuite (e.g. String , Long, etc.)
	 * @param tableName Table (Gene,mRNA,Protein) to which this datasource belongs to.
	 * @param columnName Column name in the table which represents Id from this 
	 * data source (e.g. EnsembleGeneId column of table Gene)
	 * @param outputAttribute Output attribute name for this DS
	 */
	public DataSource(Long id, String name, String genomicIdentifierClassName, String className,
			String attributeName, String attributeType, String tableName, String columnName,
			String outputAttribute)
	{
		super();
		this.id = id;
		this.name = name;
		this.genomicIdentifierClassName = genomicIdentifierClassName;
		this.className = className;
		this.attributeName = attributeName;
		this.attributeType = attributeType;
		this.tableName = tableName;
		this.columnName = columnName;
		this.outputAttribute = outputAttribute;
	}

	
	/**
	 *	 * @param id Data Source ID
	 * @param name Name of the DataSource
	 * @param genomicIdentifierClassName  Name of Genomic Identifier class(Gene,mRNA,Protein) 
	 * to which this datasource belongs to.
	 * @param className Class(Gene,mRNA,Protein) to which this datasource belongs to.
	 * @param attributeName Attribute name in the class which represents Id from this
	 *  data source (e.g. EnsembleGeneId of Class Gene)
	 * @param attributeType Data type of attribuite (e.g. String , Long, etc.)
	 * @param tableName Table (Gene,mRNA,Protein) to which this datasource belongs to.
	 * @param columnName Column name in the table which represents Id from this 
	 * data source (e.g. EnsembleGeneId column of table Gene)
	 * @param outputAttribute Output attribute name for this DS
	 * @param rowNum Row no. in which data source node will be displyed in the graph 
	 * @param columnNum Column no. in which data source node will be displyed in the graph 
	 */
	public DataSource(Long id, String name, String genomicIdentifierClassName, String className,
			String attributeName, String attributeType, String tableName, String columnName,
			String outputAttribute, int rowNum, int columnNum)
	{
		super();
		this.id = id;
		this.name = name;
		this.genomicIdentifierClassName = genomicIdentifierClassName;
		this.className = className;
		this.attributeName = attributeName;
		this.attributeType = attributeType;
		this.tableName = tableName;
		this.columnName = columnName;
		this.outputAttribute = outputAttribute;
		this.rowNum = rowNum;
		this.columnNum = columnNum;
	}
	
	/**
	 * Getter method for attributeName.
	 * @return Returns the attributeName.
	 */
	public String getAttributeName()
	{
		return attributeName;
	}

	/**
	 * Setter method for attributeName
	 * @param attributeName The attributeName to set.
	 */
	public void setAttributeName(String attributeName)
	{
		this.attributeName = attributeName;
	}

	/**
	 * Getter method for attributeType.
	 * @return Returns the attributeType.
	 */
	public String getAttributeType()
	{
		return attributeType;
	}

	/**
	 * Setter method for attributeType
	 * @param attributeType The attributeType to set.
	 */
	public void setAttributeType(String attributeType)
	{
		this.attributeType = attributeType;
	}

	/**
	 * Getter method for className.
	 * @return Returns the className.
	 */
	public String getClassName()
	{
		return className;
	}

	/**
	 * Setter method for className
	 * @param className The className to set.
	 */
	public void setClassName(String className)
	{
		this.className = className;
	}

	/**
	 * Getter method for columnName.
	 * @return Returns the columnName.
	 */
	public String getColumnName()
	{
		return columnName;
	}

	/**
	 * Setter method for columnName
	 * @param columnName The columnName to set.
	 */
	public void setColumnName(String columnName)
	{
		this.columnName = columnName;
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
	 * Getter method for tableName.
	 * @return Returns the tableName.
	 */
	public String getTableName()
	{
		return tableName;
	}

	/**
	 * Setter method for tableName
	 * @param tableName The tableName to set.
	 */
	public void setTableName(String tableName)
	{
		this.tableName = tableName;
	}

	/**
	 * Getter method for outputAttribute.
	 * @return Returns the outputAttribute.
	 */
	public String getOutputAttribute()
	{
		return outputAttribute;
	}

	/**
	 * Getter method for genomicIdentifierClassName.
	 * @return Returns the genomicIdentifierClassName.
	 */
	public String getGenomicIdentifierClassName()
	{
		return genomicIdentifierClassName;
	}

	/**
	 * Setter method for genomicIdentifierClassName
	 * @param genomicIdentifierClassName The genomicIdentifierClassName to set.
	 */
	public void setGenomicIdentifierClassName(String genomicIdentifierClassName)
	{
		this.genomicIdentifierClassName = genomicIdentifierClassName;
	}

	/**
	 * Setter method for outputAttribute
	 * @param outputAttribute The outputAttribute to set.
	 */
	public void setOutputAttribute(String outputAttribute)
	{
		this.outputAttribute = outputAttribute;
	}

	public boolean equals(Object obj)
	{
		boolean flag = false;
		if (obj instanceof DataSource)
		{
			DataSource datasource = (DataSource) obj;
			Long long1 = getId();
			if (long1 != null && long1.equals(datasource.getId()))
				flag = true;
		}
		return flag;
	}

	public int hashCode()
	{
		int i = 0;
		if (getId() != null)
			i += getId().hashCode();
		return i;
	}
	
	/**
	 * Getter method for columnNum.
	 * @return Returns the columnNum.
	 */
	public int getColumnNum()
	{
		return columnNum;
	}
	/**
	 * Setter method for columnNum
	 * @param columnNum The columnNum to set.
	 */
	public void setColumnNum(int columnNum)
	{
		this.columnNum = columnNum;
	}
	/**
	 * Getter method for rowNum.
	 * @return Returns the rowNum.
	 */
	public int getRowNum()
	{
		return rowNum;
	}
	/**
	 * Setter method for rowNum
	 * @param rowNum The rowNum to set.
	 */
	public void setRowNum(int rowNum)
	{
		this.rowNum = rowNum;
	}
}