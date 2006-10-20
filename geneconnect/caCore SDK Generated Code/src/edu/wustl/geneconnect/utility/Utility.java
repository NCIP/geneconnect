
package edu.wustl.geneconnect.utility;

import java.util.List;

public class Utility
{

	/**
	 * @author sachin_lale
	 * utility method to search for given object is in a list
	 * @param value boolean true if obj is in a list
	 * @param list
	 * @return
	 */
	public static boolean listContainValue(Object value, List list)
	{
		for (int i = 0; i < list.size(); i++)
		{
			Object val = list.get(i);
			if (val.toString().equals(value.toString()))
			{
				return true;
			}
		}
		return false;
	}

}
