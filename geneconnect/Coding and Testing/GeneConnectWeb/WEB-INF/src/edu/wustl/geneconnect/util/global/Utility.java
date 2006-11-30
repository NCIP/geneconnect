package edu.wustl.geneconnect.util.global;

import java.util.List;


public class Utility
{
	public static boolean listContainValue(Object value,List list)
	{
		for(int i=0;i<list.size();i++)
		{
			Object val = list.get(i);
			if(val.toString().equals(value.toString()))
			{
				return true;
			}
		}
		return false;
	}
}
