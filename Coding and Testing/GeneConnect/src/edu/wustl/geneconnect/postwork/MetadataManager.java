/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>ClassName: java edu.wustl.geneconnect.postwork.MetadataManager</p> 
 */

package edu.wustl.geneconnect.postwork;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;

import com.dataminer.server.database.DBManager;
import com.dataminer.server.exception.FatalException;
import com.dataminer.server.jobmanager.BaseBuilder;
import com.dataminer.server.log.Logger;

import edu.wustl.geneconnect.GeneConnectServerConstants;
import edu.wustl.geneconnect.metadata.domain.BaseTable;
import edu.wustl.geneconnect.metadata.domain.DataSource;
import edu.wustl.geneconnect.metadata.domain.Path;
import edu.wustl.geneconnect.metadata.domain.TableColumn;
import edu.wustl.geneconnect.metadata.domain.TableJoinInformation;

/**
 * This class will implement all business logic to provide geneconnect metadata.
 * It caches all the metdata when it is invoked first time in order to avoid database queries every time. 
 * @author mahesh_nalkande
 * @version 1.0
 */
public class MetadataManager implements GeneConnectServerConstants
{

	/** MetadataManager as a singleton class */
	private static MetadataManager metadataManagerInstance;

	/** 
	 * Method to return instance of this class
	 * @return MetadataManager Returns object of this class
	 */
	public static MetadataManager getInstance()
	{
		if (metadataManagerInstance == null)
		{
			metadataManagerInstance = new MetadataManager();
			//Cache all metadata required 
			metadataManagerInstance.cacheMetadata();
		}
		return metadataManagerInstance;
	}

	/**
	 * Default constructor, not accessible to all as it is a singleton class
	 */
	private MetadataManager()
	{
		super();
	}

	/**
	 * Data Base Manager instance used for all database operations
	 */
	private DBManager dbManager = DBManager.getInstance();

	/**
	 * Map to store cached Data Source Information
	 */
	private Map dataSources = new HashMap();
	/**
	 * Map to store cached base table metadata
	 */
	private Map baseTableMetaData = new HashMap();
	/**
	 * Map to store cached column metadata
	 */
	private Map columnMetaData = new HashMap();
	/**
	 * Map to store cached table join metadata
	 */
	private List tableJoinMetaData = new ArrayList();
	/**
	 * Map to store cached Path Information
	 */
	private Map paths = new HashMap();
	/**
	 * Contains multimaps, one each for a source node
	 */
	private List masterPathList = new ArrayList();
	/**
	 * List of subPath Ids for every path.
	 * n th element in the list contains subPath Ids (delimited by '_') of the path Id n.
	 */
	private List subPathIds = new ArrayList();
	/**
	 * List of Ont Ids for every path.
	 * n th element in the list contains ont Ids (delimited by '_') of the path Id n.
	 */
	private List ontIds = new ArrayList();
	/**
	 * Caches Link types (delimited by '_') for every ont.
	 * Key : Ont Id
	 * Value : Link types (delimited by '_') 
	 */
	private Map ontLinkTypes = new HashMap();

	/**
	 * This method caches all metadata required during summary table genration, 
	 * in order to avoid data base server queries. 
	 */
	private void cacheMetadata()
	{
		Logger.log("Caching MetaData...", Logger.DEBUG);
		Logger.log("Total memory available : " + Runtime.getRuntime().totalMemory(), Logger.DEBUG);
		Logger.log("Free memory available : " + Runtime.getRuntime().freeMemory(), Logger.DEBUG);

		/** set up Data Base connection object*/
		connectToDB();
		cacheDataSourceInformation();
		cacheBaseTableMetaData();
		cacheColumnMetaData();
		cacheTableJoinInformation();
		cachePaths();
		cachePathSubPathsMapping();
		cachePathOntMapping();
		cacheOntLinkTypes();
		Logger.log("Metadata has been cached.", Logger.DEBUG);
		Logger.log("Free memory available after caching : " + Runtime.getRuntime().freeMemory(),
				Logger.DEBUG);
	}

	private void cacheDataSourceInformation()
	{
		DataSource dataSource = null;
		String sqlQuery = "SELECT DATASOURCE_ID, DATASOURCE_NAME, GENOMIC_IDENTIFIER_CLASS, CLASS, ATTRIBUTE, ATTRIBUTE_TYPE, "
				+ "TABLE_NAME, COLUMN_NAME, OUTPUT_ATTRIBUTE FROM DATASOURCE ORDER BY DATASOURCE_ID";
		//execute query to get all the data from the BASETABLE_METADATA table
		ResultSet resultSet = dbManager.executeSQLQuery(sqlQuery);
		try
		{
			while (resultSet.next())
			{
				//Prepae base tabel object
				dataSource = new DataSource(new Long(resultSet.getString(1)), resultSet
						.getString(2), resultSet.getString(3), resultSet.getString(4), resultSet
						.getString(5), resultSet.getString(6), resultSet.getString(7), resultSet
						.getString(8), resultSet.getString(9));

				//put it into the Map, which will be refered later on
				dataSources.put(new Long(resultSet.getString(1)), dataSource);
			}
		}
		catch (SQLException e)
		{
			Logger.log("SQLException occured while fetching the metadata from the database",
					Logger.FATAL);
			SummaryExceptionHandler.handleException(e);
		}
	}

	/**
	 * This method cahces all base table metadata. 
	 */
	private void cacheBaseTableMetaData()
	{
		BaseTable baseTable = null;
		String sqlQuery = "select TABLE_ID,TABLE_NAME,SOURCE_DATASOURCE_ID,TARGET_DATASOURCE_ID "
				+ " from BASETABLE_METADATA";
		//execute query to get all the data from the BASETABLE_METADATA table
		ResultSet resultSet = dbManager.executeSQLQuery(sqlQuery);
		try
		{
			while (resultSet.next())
			{
				//Prepae base tabel object
				baseTable = new BaseTable(new Long(resultSet.getString(1)), resultSet.getString(2),
						new Long(resultSet.getString(3)), new Long(resultSet.getString(4)));
				//put it into the Map, which will be refered later on
				baseTableMetaData.put(new Long(resultSet.getString(1)), baseTable);
			}
		}
		catch (SQLException e)
		{
			Logger.log("SQLException occured while fetching the metadata from the database",
					Logger.FATAL);
			SummaryExceptionHandler.handleException(e);
		}
	}

	/**
	 * This method cahces all column metadata. 
	 */
	private void cacheColumnMetaData()
	{
		TableColumn columnInfo = null;
		String sqlQuery = "select COLUMN_ID,COLUMN_NAME,TABLE_ID,DATASOURCE_ID from COLUMN_METADATA";

		//execute query to get all the data from the COLUMN_METADATA table
		ResultSet resultSet = dbManager.executeSQLQuery(sqlQuery);
		try
		{
			while (resultSet.next())
			{
				//Prepae table column object
				columnInfo = new TableColumn(new Long(resultSet.getString(1)), resultSet
						.getString(2), new Long(resultSet.getString(3)), null);
				if (resultSet.getString(4) != null)
				{
					columnInfo.setDataSourceId(new Long(resultSet.getString(4)));
				}
				//put it into the Map, which will be refered later on
				columnMetaData.put(new Long(resultSet.getString(1)), columnInfo);
			}
		}
		catch (SQLException e)
		{
			Logger.log("SQLException occured while fetching the metadata from the database",
					Logger.FATAL);
			SummaryExceptionHandler.handleException(e);
		}
	}

	/**
	 * This method cahces all Table Join metadata. 
	 */
	private void cacheTableJoinInformation()
	{
		TableJoinInformation tableJoinInfo = null;
		String sqlQuery = "select SOURCE_TABLE_ID,TARGET_TABLE_ID,SOURCE_COLUMN_ID,TARGET_COLUMN_ID "
				+ "from TABLEJOIN_METADATA";

		//execute query to get all the data from the TABLEJOIN_METADATA table
		ResultSet resultSet = dbManager.executeSQLQuery(sqlQuery);
		try
		{
			while (resultSet.next())
			{
				//Prepae table column object
				tableJoinInfo = new TableJoinInformation(new Long(resultSet.getString(1)),
						new Long(resultSet.getString(2)), new Long(resultSet.getString(3)),
						new Long(resultSet.getString(4)));
				//put it into the Map, which will be refered later on
				tableJoinMetaData.add(tableJoinInfo);
			}
		}
		catch (SQLException e)
		{
			Logger.log("SQLException occured while fetching the metadata from the database",
					Logger.FATAL);
			SummaryExceptionHandler.handleException(e);
		}
	}

	private void cachePaths()
	{
		for (int i = 0; i < dataSources.size(); ++i)
		{
			MultiMap pathMap = new MultiValueMap();
			masterPathList.add(pathMap);
		}

		Path currentPath = null;
		String sqlQuery = "select PATH_ID, SOURCE_DATASOURCE_ID, TARGET_DATASOURCE_ID, PATH FROM PATH";
		//execute query to get all the data from the BASETABLE_METADATA table
		ResultSet resultSet = dbManager.executeSQLQuery(sqlQuery);
		try
		{
			while (resultSet.next())
			{
				//Prepae base tabel object
				currentPath = new Path(new Long(resultSet.getString(1)), new Long(resultSet
						.getString(2)), new Long(resultSet.getString(3)), resultSet.getString(4));

				//put it into the Map, which will be refered later on
				paths.put(new Long(resultSet.getString(1)), currentPath);

				MultiMap pathMap = (MultiMap) masterPathList.get(currentPath
						.getSourceDataSourceId().intValue());
				pathMap.put(currentPath.getTargetDataSourceId(), currentPath);
			}
		}
		catch (SQLException e)
		{
			Logger.log("SQLException occured while fetching the metadata from the database",
					Logger.FATAL);
			SummaryExceptionHandler.handleException(e);
		}
	}

	private void cachePathSubPathsMapping()
	{
		for (int i = 0; i <= paths.size(); i++)
		{
			subPathIds.add(i, new String());
		}
		String sqlQuery = "select PATH_ID,SUBPATH_ID from SUBPATH order by PATH_ID";
		//execute query to get all the data from the SUBPATH table
		ResultSet resultSet = dbManager.executeSQLQuery(sqlQuery);
		String currentSubPathIds, subPathId;
		int pathId;
		try
		{
			while (resultSet.next())
			{
				pathId = resultSet.getInt(1);
				subPathId = resultSet.getString(2);
				currentSubPathIds = (String) subPathIds.get(pathId);
				if (currentSubPathIds.equals(""))
				{
					currentSubPathIds = subPathId;
				}
				else
				{
					currentSubPathIds = currentSubPathIds + SUBPATH_IDS_DELIMITER + subPathId;
				}
				subPathIds.remove(pathId);
				subPathIds.add(pathId, currentSubPathIds);
			}
		}
		catch (SQLException e)
		{
			Logger.log("SQLException occured while fetching the metadata from the database",
					Logger.FATAL);
			SummaryExceptionHandler.handleException(e);
		}
	}

	private void cachePathOntMapping()
	{
		for (int i = 0; i <= paths.size(); i++)
		{
			ontIds.add(i, new String());
		}
		String sqlQuery = "select PATH_ID,ONT_ID from PATH_ONT order by PATH_ID";
		//execute query to get all the data from the PATH_ONT table
		ResultSet resultSet = dbManager.executeSQLQuery(sqlQuery);
		String currentOntIds, ontId;
		int pathId;
		try
		{
			while (resultSet.next())
			{
				pathId = resultSet.getInt(1);
				ontId = resultSet.getString(2);
				currentOntIds = (String) ontIds.get(pathId);
				if (currentOntIds.equals(""))
				{
					currentOntIds = ontId;
				}
				else
				{
					currentOntIds = currentOntIds + ONT_IDS_DELIMITER + ontId;
				}
				ontIds.remove(pathId);
				ontIds.add(pathId, currentOntIds);
			}
		}
		catch (SQLException e)
		{
			Logger.log("SQLException occured while fetching the metadata from the database",
					Logger.FATAL);
			SummaryExceptionHandler.handleException(e);
		}
	}

	private void cacheOntLinkTypes()
	{
		String sqlQuery = "select PATH_ID,LINKTYPE_ID,NEXT_PATH_ID,PREV_PATH_ID from ONT order by PATH_ID";
		//execute query to get all the data from the ONT table

		ResultSet resultSet = dbManager.executeSQLQuery(sqlQuery);
		String currentOntLinkTypes = "";
		long prevPathId = 0, nextPathId = 0, currentOntId = 0;
		try
		{
			while (resultSet.next())
			{
				prevPathId = resultSet.getLong(4);
				nextPathId = resultSet.getLong(3);

				if (prevPathId == 0) //Begining of new Ont Definition
				{
					currentOntId = resultSet.getLong(1);
					currentOntLinkTypes = resultSet.getString(2);
				}
				else if (nextPathId == 0) //End of of Ont Definition
				{
					//put it into the Map, which will be refered later on
					ontLinkTypes.put(new Long(currentOntId), currentOntLinkTypes);
				}
				else
				//intermediate node of the ONT
				{
					currentOntLinkTypes = currentOntLinkTypes + LINK_TYPES_DELIMITER
							+ resultSet.getString(2);
				}
			}
		}
		catch (SQLException e)
		{
			Logger.log("SQLException occured while fetching the metadata from the database",
					Logger.FATAL);
			SummaryExceptionHandler.handleException(e);
		}

	}

	public Path getPath(Long pathId)
	{
		return (Path) paths.get(pathId);
	}

	public String getSubPaths(Long pathId)
	{
		return subPathIds.get(pathId.intValue()).toString();
	}

	public String getOnts(Long pathId)
	{
		return ontIds.get(pathId.intValue()).toString();
	}

	public String getOntLinkTypes(Long ontId)
	{
		return ontLinkTypes.get(ontId).toString();
	}

	public Path getPath(String path)
	{
		String[] pathNodes = path.split(PATH_NODES_DELIMITER);
		Long sourceNodeId = new Long(pathNodes[0]);
		Long destinationNodeId = new Long(pathNodes[pathNodes.length - 1]);
		MultiMap pathMap = (MultiMap) masterPathList.get(sourceNodeId.intValue());
		Collection possiblePathsFromSrcToDest = (Collection) pathMap.get(destinationNodeId);

		if (possiblePathsFromSrcToDest != null)
		{
			Path currentPath = null;
			for (Iterator iter = possiblePathsFromSrcToDest.iterator(); iter.hasNext();)
			{
				currentPath = (Path) iter.next();
				if (path.equals(currentPath.getCompletePath()))
				{
					return currentPath;
				}
			}
		}
		return null;
	}

	public List getPaths(Long sourceId, Long destinationId)
	{
		return null;
	}

	/**
	 * This method gives base table information given a data source Ids of source and destination
	 * @param sourceDataSourceId Id of the source datasource
	 * @param destinationDataSourceId Id of the destination datasource
	 * @return BaseTable base table information object 
	 */
	public BaseTable getBaseTableInformation(Long sourceDataSourceId, Long destinationDataSourceId)
	{
		BaseTable baseTable = null;
		Iterator iterator = baseTableMetaData.values().iterator();
		while (iterator.hasNext())
		{
			baseTable = (BaseTable) iterator.next();
			if (sourceDataSourceId.equals(baseTable.getSourceDataSourceId())
					&& destinationDataSourceId.equals(baseTable.getDestinationDataSourceId()))
			{
				return baseTable;
			}
		}
		Logger.log("ERROR : Base Table Information for data sources " + sourceDataSourceId + " - "
				+ destinationDataSourceId + " is not available.", Logger.FATAL);
		Logger.log("Please check the GC graph configuration file and GCMetadataPopulation.sql "
				+ "in script folder and rerun the server.", Logger.FATAL);
		return null;
	}

	/**
	 * Names of columns which can be used to join given 2 tables.
	 * @param sourceTableId source table to be joined
	 * @param targetTableId destination table to be joined
	 * @return
	 */
	public String[] getJoiningColumnNames(Long sourceTableId, Long targetTableId)
	{
		TableJoinInformation tableJoinInformation = null;
		Iterator iterator = tableJoinMetaData.iterator();
		while (iterator.hasNext())
		{
			tableJoinInformation = (TableJoinInformation) iterator.next();
			if (sourceTableId.equals(tableJoinInformation.getSourceTableId())
					&& targetTableId.equals(tableJoinInformation.getDestinationTableId()))
			{
				String[] joinColumnNames = {
						getColumnInformation(tableJoinInformation.getSourceColumnId()).getName(),
						getColumnInformation(tableJoinInformation.getDestinationColumnId())
								.getName()};
				return joinColumnNames;
			}
		}
		Logger.log("ERROR : Joining Column Information for tables "
				+ getBaseTableInformation(sourceTableId).getName() + " - "
				+ getBaseTableInformation(targetTableId).getName() + " is not available.",
				Logger.FATAL);
		Logger.log("Please check the GC graph configuration file and GCMetadataPopulation.sql "
				+ "in script folder and rerun the server.", Logger.FATAL);
		return null;
	}

	/**
	 * Gives Base Table Information based on given Name of the table 
	 * @param tableName Name of the base table
	 * @return BaseTable object - describes the base table which stores mapping Ids among 2 adjucent data sources
	 */
	public BaseTable getBaseTableInformation(String tableName)
	{
		BaseTable baseTable = null;
		Iterator iterator = baseTableMetaData.values().iterator();
		while (iterator.hasNext())
		{
			baseTable = (BaseTable) iterator.next();
			if (tableName.equals(baseTable.getName()))
			{
				return baseTable;
			}
		}
		Logger.log("ERROR : Table Information for table " + tableName + " is not available.",
				Logger.FATAL);
		Logger.log("Please check the GC graph configuration file and GCMetadataPopulation.sql "
				+ "in script folder and re-run the server.", Logger.FATAL);
		return null;
	}

	/**
	 * Gives Base Table Information based on given Id of the table 
	 * @param tableId Id of the base table
	 * @return BaseTable object - describes the base table which stores mapping Ids among 2 adjucent data sources
	 */
	public BaseTable getBaseTableInformation(Long tableId)
	{
		BaseTable baseTable = null;
		baseTable = (BaseTable) baseTableMetaData.get(tableId);
		if (baseTable != null)
		{
			return baseTable;
		}
		Logger.log("ERROR : Table Information for table " + tableId + " is not available.",
				Logger.FATAL);
		Logger.log("Please check the GC graph configuration file and GCMetadataPopulation.sql "
				+ "in script folder and re-run the server.", Logger.FATAL);
		return null;
	}

	/**
	 * Gives information about the requsted base table column.
	 * @param columnId Id of the column whose information is required.
	 * @return TableColumn object which describes coulmn in the base table. 
	 */
	public TableColumn getColumnInformation(Long columnId)
	{
		TableColumn columnInformation = null;
		Iterator iterator = columnMetaData.values().iterator();
		while (iterator.hasNext())
		{
			columnInformation = (TableColumn) iterator.next();
			if (columnId.equals(columnInformation.getId()))
			{
				return columnInformation;
			}
		}
		Logger.log("ERROR : Column Information for columnId " + columnId + " is not available.",
				Logger.FATAL);
		Logger.log("Please check the GC graph configuration file and GCMetadataPopulation.sql "
				+ "in script folder and re-run the server.", Logger.FATAL);
		return null;
	}

	/**
	 * List of column infromation objects describing all columns in the requsted base table. 
	 * @param tableId Id of the base table 
	 * @return List of TableColumn objects. Each object describes single column in the requestes base table.
	 */
	public List getAllColumns(Long tableId)
	{
		List columns = new ArrayList();
		TableColumn columnInformation = null;

		Iterator iterator = columnMetaData.values().iterator();
		while (iterator.hasNext())
		{
			columnInformation = (TableColumn) iterator.next();
			if (tableId.equals(columnInformation.getTableId()))
			{
				columns.add(columnInformation);
			}
		}
		return columns;
	}

	public DataSource getDataSource(Long dataSourceId)
	{
		return (DataSource) dataSources.get(dataSourceId);
	}

	public Map getDataSources()
	{
		return dataSources;
	}

	public Map getBaseTableMetaData()
	{
		return baseTableMetaData;
	}

	public Map getColumnMetaData()
	{
		return columnMetaData;
	}

	/**
	 * Establish data base connection using db properties specified in server.properties
	 */
	private void connectToDB()
	{
		/** set up Data Base connection object*/
		try
		{
			Logger.log("Connecting to DB server...", Logger.DEBUG);
			dbManager.connect();
			Logger.log("Connected.", Logger.DEBUG);
		}
		catch (FatalException fatal)
		{
			/** If database connection can not be established successfully it will throw 
			 * FatalException which will cause the program to terminate  */
			Logger.log("Fatal Exception occured while connecting to database", Logger.FATAL);
			Logger.log("Reason : " + fatal.getMessage(), Logger.FATAL);
			fatal.printStackTrace();
			fatal.printException();
			BaseBuilder.getInstance("edu.wustl.geneconnect.builder.GCBuilder")
					.handleFatalException();
		}
	}
}