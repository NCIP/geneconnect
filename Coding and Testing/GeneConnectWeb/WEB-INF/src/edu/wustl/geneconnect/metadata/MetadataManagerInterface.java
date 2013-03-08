/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.metadata.MetadataManagerInterface</p> 
 */

package edu.wustl.geneconnect.metadata;

import java.util.List;

import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.dbManager.DAOException;

/**
 * Interface implemnted by MetaData Manager. 
 * @author mahesh_nalkande
 * @version 1.0
 */
public interface MetadataManagerInterface
{

	/**
	 * Returns a list of all data sources.
	 * @return List of Data source names.
	 */
	public List getDataSources() throws DAOException, BizLogicException;

	/**
	 * Returns list of all possible link types among the given 2 datasources 
	 * @param sourceDataSourceName Source Data source name of the link 
	 * @param targetDataSourceName Target Data source name of the link
	 * @return List of link type names.
	 */
	public List getPossibleLinkTypes(String sourceDataSourceName, String targetDataSourceName)
			throws DAOException, BizLogicException;

	/**
	 * Returns list of all possible link types among the given 2 datasources 
	 * @param sourceDataSourceId ID of Source Data source of the link 
	 * @param targetDataSourceId ID of Target Data source of the link
	 * @return List of link type names.
	 */
	public List getPossibleLinkTypes(Object sourceDataSourceId, Object targetDataSourceId)
			throws DAOException, BizLogicException;

	/**
	 * Returns all possible paths(ONTs) which satisfies the given criteria.
	 * @param inputDataSources List of input datasource IDs (starting nodes of the paths)
	 * @param outputDataSources List of output datasource IDs (ending nodes of the paths)
	 * @param searchCriteria Search Criteria can be one of the following:
	 * 0 - All
	 * 1 - Shortest
	 * 2 - Alignment based
	 * 3 - Non-Alignment based 
	 * @param sourceDataSource ID of Source Data source (starting node of the paths)
	 * If sourceDataSource has been specified, inputDataSources will be ignored   
	 * @param targetDataSource ID of Target Data source (ending node of the paths)
	 * If targetDataSource has been specified, outputDataSources will be ignored
	 * @return List of all paths starting with given datasource.
	 */
	public List getPaths(List inputDataSources, List outputDataSources, String searchCriteria,
			Object sourceDataSource, Object targetDataSource) throws DAOException,
			BizLogicException;
}