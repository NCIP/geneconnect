
package edu.wustl.geneconnect.domain;

/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */

/**
 * This class represents the data sources used in the system.
 */
public class DataSource implements java.io.Serializable
{

	private static final long serialVersionUID = 1234567890L;
	/**
	 * Unique identifier of DataSource.
	 */
	private java.lang.Long id;
	/**
	 * Returns the unique identifier of DataSource.
	 * @return unique identifier
	 */
	public java.lang.Long getId()
	{
		return id;
	}
/**
 * Sets the unique identifier to this DataSource object.
 * @param id
 */
	public void setId(java.lang.Long id)
	{
		this.id = id;
	}
/**
 * This denotes the name of the data source. The example data source names are UniGene, EntrezGene, UniProtKB etc.
 */
	private java.lang.String name;
/**
 * Returns the the name of the data source.
 * @return name
 */
	public java.lang.String getName()
	{
		return name;
	}
/**
 * Sets the the name of the data source.
 * @param name
 */
	public void setName(java.lang.String name)
	{
		this.name = name;
	}

	public boolean equals(Object obj)
	{
		boolean eq = false;
		if (obj instanceof DataSource)
		{
			DataSource c = (DataSource) obj;
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