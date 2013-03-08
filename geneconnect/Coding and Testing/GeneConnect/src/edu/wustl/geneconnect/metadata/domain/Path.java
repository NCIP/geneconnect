/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.metadata.domain.Path</p> 
 */

package edu.wustl.geneconnect.metadata.domain;

/**
 * This class describes path in the GeneConnect graph.
 * @author mahesh_nalkande
 * @version 1.0
 */
public class Path
{

	/**
	 * Defualt Constructor
	 */
	public Path()
	{
	}

	/**
	 * Id of the path
	 */
	private Long id;
	/**
	 * Id of the source node of the path
	 */
	private Long sourceDataSourceId;
	/**
	 * Id of the target node of the path
	 */
	private Long targetDataSourceId;
	/**
	 * Intermediate nodes in the payh. "_" seperated list of ds node Ids
	 */
	private String path;

	/**
	 * Return string representing complete path. 
	 * "_" seperated list of ds node Ids
	 * @return String representing complete path.
	 */
	public String getCompletePath()
	{
		if (path == null)
		{
			return sourceDataSourceId + "_" + targetDataSourceId;
		}
		else
		{
			return sourceDataSourceId + "_" + path + "_" + targetDataSourceId;
		}
	}

	/**
	 * @param id  Id of the path
	 * @param sourceDataSourceId Id of the source node of the path
	 * @param targetDataSourceId Id of the target node of the path
	 * @param path Intermediate nodes in the payh. "_" seperated list of ds node Ids
	 */
	public Path(Long id, Long sourceDataSourceId, Long targetDataSourceId, String path)
	{
		super();
		this.id = id;
		this.sourceDataSourceId = sourceDataSourceId;
		this.targetDataSourceId = targetDataSourceId;
		this.path = path;
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
	 * Getter method for path.
	 * @return Returns the path.
	 */
	public String getPath()
	{
		return path;
	}

	/**
	 * Setter method for path
	 * @param path The path to set.
	 */
	public void setPath(String path)
	{
		this.path = path;
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

	/**
	 * Getter method for targetDataSourceId.
	 * @return Returns the targetDataSourceId.
	 */
	public Long getTargetDataSourceId()
	{
		return targetDataSourceId;
	}

	/**
	 * Setter method for targetDataSourceId
	 * @param targetDataSourceId The targetDataSourceId to set.
	 */
	public void setTargetDataSourceId(Long targetDataSourceId)
	{
		this.targetDataSourceId = targetDataSourceId;
	}
}