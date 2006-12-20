package edu.wustl.geneconnect.util.global;

import java.util.ArrayList;
import java.util.List;
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
	
	//This method parses string of selected ONTs and populate required string 
	//if User selected 'Any' option in any of the selected ONTs 
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
				
//				Logger.out.debug("Pahts parsed-->"+(String)onts.get(i));
				
				parsedOnts.append(pathAdded);
				
				counter++;
			}
			
			onts = new ArrayList();
		}
		
//		Logger.out.debug("Leaving parseAnyOption()...");
		Logger.out.debug("No. of ONTs selected-->"+counter);
		return parsedOnts.toString();
	}
}
