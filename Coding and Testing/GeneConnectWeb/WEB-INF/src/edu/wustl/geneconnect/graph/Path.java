/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>ClassName: java edu.wustl.geneconnect.graph.Path</p> 
 */

package edu.wustl.geneconnect.graph;

public class Path
{

	//	private String source;

	//	private String destination;

	private int pathType;

	private Integer source;

	private Integer destination;

	public Path(Integer source, Integer destination, int pathType)
	{
		this.source = source;
		this.destination = destination;
		this.pathType = pathType;
	}

	/**
	 * @return Returns the pathType.
	 */
	public int getPathType()
	{
		return pathType;
	}

	/**
	 * @param pathType The pathType to set.
	 */
	public void setPathType(int pathType)
	{
		this.pathType = pathType;
	}

	/**
	 * @return Returns the destination.
	 */
	public Integer getDestination()
	{
		return destination;
	}

	/**
	 * @param destination The destination to set.
	 */
	public void setDestination(Integer destination)
	{
		this.destination = destination;
	}

	/**
	 * @return Returns the source.
	 */
	public Integer getSource()
	{
		return source;
	}

	/**
	 * @param source The source to set.
	 */
	public void setSource(Integer source)
	{
		this.source = source;
	}
}
