/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.postwork.QueryBuilder</p> 
 */
package edu.wustl.geneconnect.postwork;

import java.util.Iterator;
import java.util.List;

import com.dataminer.server.log.Logger;

import edu.wustl.geneconnect.metadata.domain.BaseTable;
import edu.wustl.geneconnect.metadata.domain.TableColumn;


/**
 * This class builds outer join queries for summary table calculation given a 
 * non-redudant longest path.
 * @author mahesh_nalkande
 * @version 1.0
 */
public class QueryBuilder
{

	/**
	 * Default constructor
	 */
	public QueryBuilder()
	{
	}
	
	/**
	 * Metadata Manager instance used to obtain all metadata. 
	 */
	private MetadataManager metadataManager = MetadataManager.getInstance();

	/**
	 * Builds outer join queries for summary table calculation given a 
	 * non-redudant longest path.
	 * @param path non-redudant longest path for the outer join query has to be build. 
	 * @param tablesInvolvedInCurrentQuery Returns Base table names involved the query
	 * @return Outer join query string
	 */
	public String buildQuery(String path, List tablesInvolvedInCurrentQuery)
	{
		Logger.log("Generating query for the path : " + path, Logger.DEBUG);
		BaseTable sourceJoiningTable, destinationJoiningTable, baseTable;
		TableColumn tableColumn = null;
		List columns = null;
		String joiningColumns[];

		String query = "", resultColumnNames = "";

		String[] pathNodes = path.split("_");

		if (pathNodes.length < 2)
		{
			Logger.log("Path should contain atleast 2 nodes", Logger.DEBUG);
			return null;
		}
		//obtain first base table information join first 2 data sources in the path
		sourceJoiningTable = metadataManager.getBaseTableInformation(new Long(pathNodes[0]), new Long(pathNodes[1]));
		tablesInvolvedInCurrentQuery.add(sourceJoiningTable);

		query = query + sourceJoiningTable.getName();

		for (int i = 1; i < pathNodes.length - 1; i++)
		{
			sourceJoiningTable = metadataManager.getBaseTableInformation(new Long(pathNodes[i - 1]), new Long(
					pathNodes[i]));
			destinationJoiningTable = metadataManager.getBaseTableInformation(new Long(pathNodes[i]), new Long(
					pathNodes[i + 1]));
			tablesInvolvedInCurrentQuery.add(destinationJoiningTable);

			//System.out.println("Joining : " + sourceJoiningTable.getName() + " to " + destinationJoiningTable.getName());
			joiningColumns = metadataManager.getJoiningColumnNames(sourceJoiningTable.getId(),
					destinationJoiningTable.getId());

			query = query + " FULL OUTER JOIN " + destinationJoiningTable.getName() + " ON "
					+ sourceJoiningTable.getName() + "." + joiningColumns[0] + " = "
					+ destinationJoiningTable.getName() + "." + joiningColumns[1];
		}

		Iterator iterator = tablesInvolvedInCurrentQuery.iterator();
		while (iterator.hasNext())
		{
			baseTable = (BaseTable) iterator.next();
			columns = metadataManager.getAllColumns(baseTable.getId());
			Iterator columnIterator = columns.iterator();
			while (columnIterator.hasNext())
			{
				tableColumn = (TableColumn) columnIterator.next();
				if (resultColumnNames.equals(""))
				{
					resultColumnNames = baseTable.getName() + "." + tableColumn.getName();
				}
				else
				{
					resultColumnNames = resultColumnNames + ", " + baseTable.getName() + "."
							+ tableColumn.getName();
				}
			}
		}

		query = "SELECT " + resultColumnNames + " FROM " + query;
		return query;
	}
}
