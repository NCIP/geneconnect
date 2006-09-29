/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.metadata.MetadataManager</p> 
 */

package edu.wustl.geneconnect.metadata;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.dbManager.DAOException;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.geneconnect.dao.JDBCDAO;
import edu.wustl.geneconnect.util.global.GCConstants;

/**
 * This class will implement all business logic to provide geneconnect metadata.
 * @author mahesh_nalkande
 */
public class MetadataManager //implements MetadataManagerInterface
{

	// Store datasoruceid , dataSource name
	private static List dataSourcesToDisplay = null;
	
	// represents teh DATSOURCE meta data table
	private static List dataSources = null;
	
	// represents teh ROLE_LOOKUP meta data table
	private static List roleLookup = null;

	private static JDBCDAO jdbcDao = null;

	/** MetadataManager as a singleton class */
	private static MetadataManager metadataManagerInstance = new MetadataManager();

/** Method to return instance of this class
 * @return MetadataManager Returns object of this class
 */
	public static MetadataManager getInstance() throws DAOException
	{
		jdbcDao = JDBCDAO.getInstance();
		return metadataManagerInstance;
	}
/**
 * Calls method to populate metadata     
 * @throws DAOException
 */
	public static void populateMetadata() throws DAOException
	{
		try
		{
			jdbcDao = JDBCDAO.getInstance();
			setDataSourceToDisplay();
			setDataSource();
			setRoleLookup();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new DAOException(e.getMessage());
		}
	}
/**
 * Execute the query to store dat source naem to display on UI 
 * @throws DAOException
 */
	private static void setDataSourceToDisplay() throws DAOException
	{
		dataSourcesToDisplay = new ArrayList();
		ResultSet resultSet = jdbcDao
				.executeSQLQuery("select DATASOURCE_ID,DATASOURCE_NAME from DATASOURCE");
		try
		{

			while (resultSet.next())
			{
				dataSourcesToDisplay.add(new NameValueBean(resultSet.getString(2), resultSet
						.getString(1)));
			}
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			throw new DAOException(e.getMessage());
		}
	}
/**
 * Execute query and store te meta data of DATASOURCE
 * @throws DAOException
 */
	private static void setDataSource() throws DAOException
	{
		try
		{
			dataSources = new ArrayList();
			ResultSet result = jdbcDao.executeSQLQuery("SELECT " + GCConstants.DATASOURCE_ID + ","
					+ GCConstants.DATASOURCE_NAME + "," + GCConstants.ATTRIBUTE + ","
					+ GCConstants.CLASS + "," + GCConstants.TYPE + " FROM DATASOURCE");

			while (result.next())
			{
				Map temp = new HashMap();
				temp.put(GCConstants.CLASS, result.getString(GCConstants.CLASS));
				temp.put(GCConstants.DATASOURCE_ID, result.getString(GCConstants.DATASOURCE_ID));
				temp
						.put(GCConstants.DATASOURCE_NAME, result
								.getString(GCConstants.DATASOURCE_NAME));
				temp.put(GCConstants.ATTRIBUTE, result.getString(GCConstants.ATTRIBUTE));
				temp.put(GCConstants.TYPE, result.getString(GCConstants.TYPE));
				dataSources.add(temp);
			}
		}
		catch (SQLException e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			throw new DAOException(e.getMessage());

		}
	}
/**
 * Execute query and store te meta data of ROLE_LOOKUP
 * @throws DAOException
 */
	private static void setRoleLookup() throws DAOException
	{
		try
		{
			roleLookup = new ArrayList();
			ResultSet result = jdbcDao.executeSQLQuery("SELECT " + GCConstants.SOURCE_CLASS + ","
					+ GCConstants.TARGET_CLASS + "," + GCConstants.ROLE_NAME + " FROM ROLE_LOOKUP");

			while (result.next())
			{
				Map temp = new HashMap();
				temp.put(GCConstants.SOURCE_CLASS, result.getString(GCConstants.SOURCE_CLASS));
				temp.put(GCConstants.TARGET_CLASS, result.getString(GCConstants.TARGET_CLASS));
				temp.put(GCConstants.ROLE_NAME, result.getString(GCConstants.ROLE_NAME));
				roleLookup.add(temp);
			}
		}
		catch (SQLException e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			throw new DAOException(e.getMessage());

		}
	}

	//TODO : Main method for testing purpose -- needs to remove later
	public static void main(String[] args)
	{
		MetadataManager metadataBizLogic = new MetadataManager();
		/*List dataSources = metadataBizLogic.getDataSources();
		 Iterator iterator = dataSources.iterator();
		 while (iterator.hasNext()) {
		 System.out.println("\n " + iterator.next());
		 }*/
		System.out.println("******************");

		//List dataSources = metadataBizLogic.getPossibleLinkTypes(new Integer(2), new Integer(1));
		//		Iterator iterator = dataSources.iterator();
		//		while (iterator.hasNext())
		//		{
		//			System.out.println("\n " + iterator.next());
		//		}
	}

	/**
	 * Returns a list of all data sources.
	 * @return List of Data source names.
	 */
	public static List getDataSourcesToDisplay() throws DAOException, BizLogicException
	{
		return dataSourcesToDisplay;
	}

	/**
	 * Returns a list of all data sources.
	 * @return List of Data source names.
	 */
	public static List getDataSources() throws DAOException, BizLogicException
	{
		return dataSources;
	}
/**
 * Returns data source name from DATASOURCE meta data 
 * @param dataSourceId
 * @return
 */
	public static String getDataSourceName(String dataSourceId)
	{
		String dsName = null;
		for (int i = 0; i < dataSourcesToDisplay.size(); i++)
		{
			NameValueBean bean = (NameValueBean) dataSourcesToDisplay.get(i);
			if (bean.getValue().equalsIgnoreCase(dataSourceId))
			{
				dsName = bean.getName();
				Logger.out.info("Retrun DSName : " + bean.getName());
				break;
			}
		}
		return dsName;
	}
/**
 * Returns the value of given column name of DATASOURCE 
 * @param searchkey - Search criteria on column name
 * @param searchvalue - value to be search on column name
 * @param returnkey - column name whose value to return
 * @return
 */
	public static String getDataSourceAttribute(String searchkey, String searchvalue,
			String returnkey)
	{
		String dataSourceAttribute = "";
		List l = new ArrayList();
		for (int i = 0; i < dataSources.size(); i++)
		{
			Map map = (Map) dataSources.get(i);
			String str = (String) map.get(searchkey);
			if (str.equalsIgnoreCase(searchvalue))
			{
				dataSourceAttribute = (String) map.get(returnkey);
				return dataSourceAttribute;
			}
		}
		return null;
	}
/**
 * returns the association role name as specified in Object Model  
 * @param sourceClass
 * @param targetClass
 * @return
 */
	public static String getRoleName(String sourceClass, String targetClass)
	{
		String roleName = "";

		for (int i = 0; i < roleLookup.size(); i++)
		{
			Map map = (Map) roleLookup.get(i);
			String sourceStr = (String) map.get(GCConstants.SOURCE_CLASS);
			String targetStr = (String) map.get(GCConstants.TARGET_CLASS);
			if ((sourceStr.equalsIgnoreCase(sourceClass))
					&& (targetStr.equalsIgnoreCase(targetClass)))
			{
				roleName = (String) map.get(GCConstants.ROLE_NAME);
				return roleName;
			}
		}
		return null;
	}

	/**
	 * Returns list of all possible link types among the given 2 datasources 
	 * @param sourceDataSource Source Data source name of the link 
	 * @param targetDataSource Target Data source name of the link
	 * @return List of link type names.
	 */
	public static List getPossibleLinkTypes(String sourceDataSourceName, String targetDataSourceName)
			throws DAOException
	{
		List linkTypes = new ArrayList();

		String sqlQuery = "SELECT LINK_NAME FROM LINKTYPE"
				+ " WHERE LINK_TYPE_ID = ANY ( SELECT LINK_TYPE_ID FROM DATASOURCE_LINKS"
				+ " WHERE SOURCE_DATASOURCE_ID = "
				+ "(SELECT DATASOURCE_ID FROM DATASOURCE WHERE DATASOURCE_NAME = '"
				+ sourceDataSourceName + "') " + " AND TARGET_DATASOURCE_ID = "
				+ "(SELECT DATASOURCE_ID FROM DATASOURCE WHERE DATASOURCE_NAME = '"
				+ targetDataSourceName + "'))";

		ResultSet resultSet = jdbcDao.executeSQLQuery(sqlQuery);
		try
		{
			while (resultSet.next())
			{
				linkTypes.add(resultSet.getString(1));
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return linkTypes;
	}

	/**
	 * Returns list of all possible link types among the given 2 datasources 
	 * @param sourceDataSourceId ID of Source Data source of the link 
	 * @param targetDataSourceId ID of Target Data source of the link
	 * @return List of link type names.
	 */
	public static List getPossibleLinkTypes(Object sourceDataSourceId, Object targetDataSourceId)
			throws DAOException
	{
		List linkTypes = new ArrayList();

		String sqlQuery = "SELECT LINK_NAME FROM LINKTYPE"
				+ " WHERE LINK_TYPE_ID = ANY ( SELECT LINK_TYPE_ID FROM DATASOURCE_LINKS"
				+ " WHERE SOURCE_DATASOURCE_ID = '" + sourceDataSourceId + "'"
				+ " AND TARGET_DATASOURCE_ID = '" + targetDataSourceId + "')";

		ResultSet resultSet = jdbcDao.executeSQLQuery(sqlQuery);
		try
		{
			while (resultSet.next())
			{
				linkTypes.add(resultSet.getString(1));
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return linkTypes;
	}

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
	public static List getPaths(List inputDataSources, List outputDataSources,
			String searchCriteria, Object sourceDataSource, Object targetDataSource)
			throws DAOException
	{
		List paths = new ArrayList();

		//TODO :: Add logic over here

		String sqlQuery = "SELECT ";

		ResultSet resultSet = jdbcDao.executeSQLQuery(sqlQuery);
		try
		{
			while (resultSet.next())
			{
				paths.add(resultSet.getString(1));
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return paths;
	}

}