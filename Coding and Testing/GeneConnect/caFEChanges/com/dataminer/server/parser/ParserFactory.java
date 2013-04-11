/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>ClassName: java com.dataminer.server.parser.ParserFactory</p> 
 */


package com.dataminer.server.parser;

import com.dataminer.server.ftp.FileInfo;
import com.dataminer.server.jobmanager.DPQueue;
import com.dataminer.server.log.Logger;

/**
 * @author Meghana Chitale
 * This class creates parser objects based on the data source selected. 
 */
public class ParserFactory 
{
	
	public ParserFactory() 
	{
	}
	
	public static Parser getParser(FileInfo file,DPQueue filesParsed) 
	{
		Parser parser = null; //parser instance
		String parserName = file.getDatabaseType();
		String fileToParse = (String)file.getFiles().firstElement();
		/**
		 * if the file has to used as external parsers than create the instance of ExternalParser 
		 */
		if(file.IsExternalParser())
		{
			parser = new ExternalParserInvoker(file,filesParsed);
		}
		else if (parserName.equalsIgnoreCase("UNIGENE")) 
		{
			parser = new UniGeneParser(file,filesParsed);
		}
		else if (parserName.equalsIgnoreCase("ENTREZGENE")) 
		{
			parser = new JDOMParser(parserName,file,filesParsed);
		}
		else if (parserName.equalsIgnoreCase("HOMOLOGENE")) 
		{
			parser = XmlParser.getParser("HOMOLOGENE",file,filesParsed);
		}
		else if (parserName.equalsIgnoreCase("GO")) 
		{
			parser = XmlParser.getParser("GO",file,filesParsed);
		}
		else if (parserName.equalsIgnoreCase("CHIPINFORMATION")) 
		{
			parser = new ChipDataParser(file,filesParsed);
			String inputFormat = file.getInputFormat();
			if (inputFormat != null) 
			{
				((ChipDataParser)parser).setFormat(inputFormat);
				Logger.log ("Set format = " + inputFormat,Logger.INFO);
			}
			
		}
		else if (parserName.equalsIgnoreCase("UNISTS")) 
		{
			parser = new UniStsParser(file,filesParsed);
		}
		else if (parserName.equalsIgnoreCase("DBSNP")) 
		{
			parser = XmlParser.getParser("DBSNP",file,filesParsed);
		} 
		else if (parserName.equalsIgnoreCase("CaArray"))
		{
			parser = new CaArrayInterface(file,filesParsed);
		}
		return parser;
	}
}