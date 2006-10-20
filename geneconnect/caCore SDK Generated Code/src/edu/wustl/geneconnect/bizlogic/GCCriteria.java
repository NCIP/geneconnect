
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

	String dataSource;
	float predicate;
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
