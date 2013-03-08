/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.bizlogic.ResultDataInterface</p> 
 */

package edu.wustl.geneconnect.bizlogic;

import java.util.Map;

/**
 * Result Data - marker interface.
 * @author Mahesh Nalkande
 * @version 1.0
 */

public class ResultData implements ResultDataInterface
{

	private Map data;

	public Map getData()
	{

		return data;
	}

	public void setData(Map data)
	{

		this.data = data;
	}

	public Object getValue(Object key)
	{
		Object valueObject = null;
		if (data != null)
		{
			valueObject = data.get(key);

		}
		return valueObject;
	}
	/**
	 * Default constructor
	 */
}