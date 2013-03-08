/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

package edu.wustl.geneconnect.bizlogic;

import java.util.ArrayList;
import java.util.List;

/**
 * Class store the predicate criteria given by user on frequency.
 * @author sachin_lale
 *
 */
public class GCCriteria
{

	/**
	 * store Data source name
	 */
	String dataSource;

	/**
	 * store predicate value 
	 */
	float predicate;
	/**
	 * Link type
	 */
	List type = new ArrayList();

	public String getDataSource()
	{
		return dataSource;
	}

	public void setDataSource(String dataSource)
	{
		this.dataSource = dataSource;
	}

	public float getPredicate()
	{
		return predicate;
	}

	public void setPredicate(float predicate)
	{
		this.predicate = predicate;
	}

	public List getType()
	{
		return type;
	}

	public void setType(List type)
	{
		this.type = type;
	}

}
