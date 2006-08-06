/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java com.dataminer.server.jobmanager.BaseBuilder</p> 
 */

package edu.wustl.geneconnect.builder;

import com.dataminer.server.jobmanager.BaseBuilder;
import com.dataminer.server.log.Logger;

/**
 * This  class extends the BaseBuilder and implements GenConnect specific logic 
 * for pre-process nad post process. 
 * Use default implementaion of downloading ,parsing and loading data 
 * provided by BaseBuilder.
 *   
 * @author       Sachin Lale
 * @version      1.0
 */
public  class GCBuilder extends BaseBuilder
{
	public void preProcessing()
	{
		createTables();
		// TODO process blast for GenBank  	
	}
	
	/**
	 * The Application using annotaion parser library must implement this method  to 
	 * perform applciation specific function after download and parse data. 
	 * Such as renaming of '_U' tables etc. 
	 *
	 */
	public void postProcessing()
	{
		// dropEntrezTables();
	}
	
	/**
	 * The Application using annotation parser library must implement this method  to 
	 * perform applciation specific caCore system function after download and parse data. 
	 * Such as renaming creation of caCore tables, post processing for caCore tables etc. 
	 *
	 */
	public void caCoreProcessing()
	{
		
	}
	
	private void dropEntrezTables()
	{
		String[] entrezTableNames = {"entrez_fly","entrez_genenames","entrez_goid",
				"entrez_map","entrez_omim","entrez_phenotype",
				"entrez_pmids","entrez_sts","system_termdata","system_termtree"};
		StringBuffer dropQuery = new StringBuffer();
		for(int i=0;i<entrezTableNames.length;i++)
		{
			dropQuery.append("DROP TABLE ");
			dropQuery.append(entrezTableNames[i]);
			dbInterface.executeUpdate(dropQuery.toString());
			Logger.log("Table dropped: "+entrezTableNames[i],Logger.INFO);
			dropQuery.setLength(0);
		}
	}
}
