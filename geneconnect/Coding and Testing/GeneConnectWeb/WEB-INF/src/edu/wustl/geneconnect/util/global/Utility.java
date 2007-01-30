package edu.wustl.geneconnect.util.global;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import edu.wustl.common.util.logger.Logger;


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
	
	/**
	 * This method parses string of selected ONTs and populate required string 
	 * if User selected 'Any' option in any of the selected ONTs 
	 */ 
	public static String parseAnyOption(String selectedOnts)
	{
		Logger.out.debug("Inside parseAnyOption()...");
		Logger.out.debug("Selected ONTs-->"+selectedOnts);
		
		StringTokenizer selectedOntsTokenized = new StringTokenizer(selectedOnts,"$",false);
		
		ArrayList onts = new ArrayList();
		
		StringBuffer parsedOnts=new StringBuffer("");
		
		int counter =0;
		while(selectedOntsTokenized.hasMoreTokens())
		{
			String ont = selectedOntsTokenized.nextToken();
			
//			Logger.out.debug("ONT--->"+ont);
			
			StringTokenizer ontTokenized = new StringTokenizer(ont, "_", true);
			
			String path ="";
			
			boolean firstEntry = true;
			
			while(ontTokenized.hasMoreTokens())
			{
				String token = ontTokenized.nextToken();
//				Logger.out.debug("Token--->"+token);
				
				if( (token.length() > 1) || (firstEntry==false) )
				{
					if(firstEntry)
					{
						for(int i=0; i<token.length(); i++)
						{
							String pathEntry = path + token.charAt(i);
							
//							Logger.out.debug("first time adding int ont list-->"+pathEntry);
							onts.add(pathEntry);
						}
						
						firstEntry = false;
					}
					else
					{
//						Logger.out.debug("Else of firstEntry...token length->"+token.length());
						
						if(token.length() > 1)
						{
							ArrayList tempList = new ArrayList();
							
							for(int i=0; i<token.length(); i++)
							{
								String link = new Character(token.charAt(i)).toString();
								
								for(int j=0; j<onts.size(); j++)
								{
									String pathEntry = (String) onts.get(j);
									
									pathEntry+=(link);
									
//									Logger.out.debug("Adding into TempList-->"+pathEntry);
									
									tempList.add(pathEntry);
								}
							}
							onts = tempList;
						}
						else
						{
							for(int i=0; i<onts.size(); i++)
							{	
								String pathEntry = (String)onts.get(i);
								
								pathEntry+=token;
								
								onts.remove(i);
								onts.add(i, pathEntry);
							}
						}
					}
				}
				else
				{
					path+=token;
				}
			}
			
			if(firstEntry)
			{
				onts.add(path);
			}
			
			for(int i=0; i<onts.size(); i++)
			{
				String pathAdded = (String)onts.get(i) + "$";
				
				Logger.out.debug("Pahts parsed-->"+(String)onts.get(i));
				
				parsedOnts.append(pathAdded);
				
				counter++;
			}
			
			onts = new ArrayList();
		}
		
//		Logger.out.debug("Leaving parseAnyOption()...");
		Logger.out.debug("No. of ONTs selected-->"+counter);
		return parsedOnts.toString();
	}
	/**
	 * Sort the input query list
	 * @param l
	 */
	public static void sortInputQueryKeys(List l)
	{
		for(int i=0;i<l.size();i++)
		{
			for(int j=0;j<l.size()-1;j++)
			{
				String k1 = (String)l.get(j);
				int k = j+1;
				String k2 = (String)l.get(k);
				String i1 = k1.substring(k1.indexOf(":")+1,k1.indexOf("_"));
				String i2 = k2.substring(k2.indexOf(":")+1,k2.indexOf("_"));
				if(Integer.decode(i1).intValue()>Integer.decode(i2).intValue())
				{
					l.remove(j);
					l.add(j,k2);
					l.remove(k);
					l.add(k,k1);
					
				}
			}
		}	
	}
	/**
	 * Sort the data list  
	 * @param l - data list to srt
	 * @param sortOnColumn - column name
	 * @param order - order of sorting asc or desc
	 */
	public static void sortDataList(List l,String sortOnColumn,String order)
	{
		boolean floatSorting=false;
		boolean isAscending=false;
		if(sortOnColumn.indexOf(GCConstants.CONF_SCORE_KEY)>=0||sortOnColumn.indexOf(GCConstants.FREQUENCY_DISPLAY_SUFFIX)>0)
		{
			floatSorting=true;
		}
		if(order.indexOf(GCConstants.SORTED_COLUMN_ASC_ORDER)>=0)
		{
			isAscending=true;
		}
		for(int i=0;i<l.size();i++)
		{
			for(int j=0;j<l.size()-1;j++)
			{
				boolean swap=false;
				Map map1  = (Map)l.get(j);
				int k = j+1;
				Map map2  = (Map)l.get(k);
				String i1 = (String)map1.get(sortOnColumn);
				String i2 = (String)map2.get(sortOnColumn);
				if(floatSorting)
				{
					if((Float.parseFloat(i1)>Float.parseFloat(i2))&&isAscending)
					{
						swap=true;
					}
					else if((Float.parseFloat(i1)<Float.parseFloat(i2))&&!isAscending)
					{
						swap=true;
					}
				}
				else
				{
					int compare = i1.compareToIgnoreCase(i2);
					if((compare>0)&&isAscending)
					{
						swap=true;
					}
					else if(compare<0&&!isAscending)
					{
						swap=true;
	
					}
				}
				if(swap)
				{
					l.remove(j);
					l.add(j,map2);
					l.remove(k);
					l.add(k,map1);
				}
			}
		}	
	}
	public static void main(String a[])
	{
		List l = new ArrayList();
		Map m1 = new HashMap();
		m1.put("Confidence Score","0.1");
		Map m2 = new HashMap();
		m2.put("Confidence Score","0.2");
		Map m3 = new HashMap();
		m3.put("Confidence Score","0.3");
		l.add(m1);
		l.add(m3);
		l.add(m2);
		System.out.println("before sort: " +l);
		sortDataList(l,"Confidence Score","asc");
		System.out.println("after  sort: " +l);
		
	}
	public static String getGridColumnWidht(String colName,String isConfidenceChecked,String isFrequencyChecked)
	{
		//System.out.println(colName+"--" +colName.indexOf(GCConstants.CONF_SCORE_DISPLAY));
		if((colName.indexOf(GCConstants.SET_ID_KEY)>=0)||(colName.indexOf(GCConstants.QUERY_KEY)==0))
		{
			return "0";
		}
		boolean isconf = new Boolean(isConfidenceChecked).booleanValue();
		boolean isfreq = new Boolean(isFrequencyChecked).booleanValue();
		if((colName.indexOf(GCConstants.FREQUENCY_DISPLAY_SUFFIX)>=0)&&!isfreq)
		{
			System.out.println("retuning 0 for frequency");
			return "0";
		}
		if((colName.indexOf(GCConstants.CONF_SCORE_DISPLAY)==0)&&!isconf)
		{
			return "0";
		}
		return "250";
	}
	public static String getGridColumnType(String colName)
	{
		
		if((colName.indexOf(GCConstants.FREQUENCY_DISPLAY_SUFFIX)>=0)||(colName.indexOf(GCConstants.CONF_SCORE_DISPLAY)>=0))
		{
			return "int";
		}
		return "str";
	}

}
