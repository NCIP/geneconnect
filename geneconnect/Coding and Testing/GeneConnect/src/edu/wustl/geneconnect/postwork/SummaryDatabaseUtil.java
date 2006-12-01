/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.postwork.SummaryDatabaseUtil</p> 
 */

package edu.wustl.geneconnect.postwork;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import com.dataminer.server.database.DBManager;
import com.dataminer.server.exception.FatalException;
import com.dataminer.server.jobmanager.BaseBuilder;
import com.dataminer.server.log.Logger;

import edu.wustl.geneconnect.GeneConnectServerConstants;
import edu.wustl.geneconnect.metadata.domain.BaseTable;
import edu.wustl.geneconnect.metadata.domain.TableColumn;

/**
 * This class contains various database utility methods such as index creation , printing table counts 
 * which are required during summary run. 
 * @author mahesh_nalkande
 * @version 1.0
 */
public class SummaryDatabaseUtil implements GeneConnectServerConstants
{

	/**
	 * Default Constructor
	 */
	public SummaryDatabaseUtil()
	{
		super();
	}

	/**
	 * Metadata Manager instance used to obtain all metadata. 
	 */
	private static MetadataManager metadataManager = MetadataManager.getInstance();

	/**
	 * Data Base Manager instance used for all database operations
	 */
	private static DBManager dbManager = DBManager.getInstance();

	/**
	 * This method create indexes on all base tables in order to improve the performance of outer join queries.
	 */
	public static void createBaseTableIndexes()
	{
		Logger.log("Creating indexes on all base tables...", Logger.INFO);
		connectToDB();
		TableColumn columnInformation = null;
		BaseTable baseTable = null;
		String indexName = "", query = "";
		ResultSet resultSet = null;
		Iterator iterator = metadataManager.getColumnMetaData().values().iterator();
		while (iterator.hasNext())
		{
			columnInformation = (TableColumn) iterator.next();
			if (columnInformation.getDataSourceId() != null)
			{
				baseTable = metadataManager.getBaseTableInformation(columnInformation.getTableId());
				indexName = baseTable.getName() + "_" + columnInformation.getId();
				createOrRebuildIndex(indexName, baseTable.getName(), columnInformation.getName());
			}
		}

		Logger.log("Base table indexes have been created successfully.", Logger.INFO);

		iterator = metadataManager.getBaseTableMetaData().values().iterator();
		while (iterator.hasNext())
		{
			//Analyze the statistics if the base table
			baseTable = (BaseTable) iterator.next();
			query = "ANALYZE TABLE " + baseTable.getName() + "  COMPUTE STATISTICS";
			dbManager.executeSQLQuery(query);
		}

		Logger.log("Base table have been analyzed successfully to compute their statistics.",
				Logger.INFO);
	}

	/**
	 * Runs the GCSummary_U_TablesCreation.sql script to create _U summary tables
	 */
	public static void createTemporarySummaryTables()
	{
		Logger.log("Creating Temporary summary tables...", Logger.INFO);
		connectToDB();
		dbManager.executeScriptFile("." + FILE_SEPARATOR + SCRIPTS_FOLDER_NAME + FILE_SEPARATOR
				+ SUMMARY_U_TABLES_CREATION_SCRIPT_FILENAME);
		Logger.log("Temporary summary tables created successfully.", Logger.INFO);
	}

	/**
	 * Runs the GCSummary_U_TablesRename.sql script to rename _U summary tables to actuals, create indexes on them 
	 * as well as to analyze their statistics
	 */
	public static void renameTemporaryTablesToActualSummaryTables()
	{
		Logger.log("Renaming Temporary summary tables to actuals and rebuilding all indexes...",
				Logger.INFO);
		connectToDB();
		dbManager.executeScriptFile("." + FILE_SEPARATOR + SCRIPTS_FOLDER_NAME + FILE_SEPARATOR
				+ SUMMARY_TABLES_RENAME_SCRIPT_FILENAME);
		Logger
				.log(
						"Finished Renaming Temporary summary tables to actuals and rebuilding all indexes.",
						Logger.INFO);
	}

	/**
	 * Creates index on intermediate table to improve the performance of order by queries
	 * @param stepCount Current step of summary execution
	 */
	public static void createAndRebuildIndexes(int stepCount)
	{
		Logger.log("Rebuilding Indexes on Intermediate Set-Ont tables...", Logger.INFO);
		connectToDB();

		switch (stepCount)
		{
			case STEP_2_FOR_UNIQUE_GENES :
				createIndexesOnIntermediateTable("GSET_ONT_1_U", "GENOMIC_IDENTIFIER_SET_ONT_1_U",
						SummaryReflectionUtil.getGeneColumnNames());
				createIndexesOnIntermediateTable("GSET_ONT_1_U", "GENOMIC_IDENTIFIER_SET_ONT_1_U",
						SummaryReflectionUtil.getMrnaColumnNames());
				createIndexesOnIntermediateTable("GSET_ONT_1_U", "GENOMIC_IDENTIFIER_SET_ONT_1_U",
						SummaryReflectionUtil.getProteinColumnNames());
				createOrRebuildIndex("GSET_ONT_1_U_PATHID", "GENOMIC_IDENTIFIER_SET_ONT_1_U",
						"PATH_ID");
				dbManager
						.executeSQLQuery("ANALYZE TABLE GENOMIC_IDENTIFIER_SET_ONT_1_U COMPUTE STATISTICS");
				break;

			case STEP_3_FOR_UNIQUE_MRNAS :
				createIndexesOnIntermediateTable("GSET_ONT_2_U", "GENOMIC_IDENTIFIER_SET_ONT_2_U",
						SummaryReflectionUtil.getMrnaColumnNames());
				dbManager
						.executeSQLQuery("ANALYZE TABLE GENOMIC_IDENTIFIER_SET_ONT_2_U COMPUTE STATISTICS");
				break;

			case STEP_4_FOR_UNIQUE_PROTEINS :
				createIndexesOnIntermediateTable("GSET_ONT_3_U", "GENOMIC_IDENTIFIER_SET_ONT_3_U",
						SummaryReflectionUtil.getProteinColumnNames());
				dbManager
						.executeSQLQuery("ANALYZE TABLE GENOMIC_IDENTIFIER_SET_ONT_3_U COMPUTE STATISTICS");
				break;

			case STEP_5_FOR_UNIQUE_SET_ONT :
				createOrRebuildIndex("GSET_ONT_4_U_GENEID", "GENOMIC_IDENTIFIER_SET_ONT_4_U",
						"GENE_ID");
				createOrRebuildIndex("GSET_ONT_4_U_MRNAID", "GENOMIC_IDENTIFIER_SET_ONT_4_U",
						"MRNA_ID");
				createOrRebuildIndex("GSET_ONT_4_U_PROTEINID", "GENOMIC_IDENTIFIER_SET_ONT_4_U",
						"PROTEIN_ID");
				createOrRebuildIndex("GSET_ONT_4_U_PATHID", "GENOMIC_IDENTIFIER_SET_ONT_4_U",
						"PATH_ID");
				dbManager
						.executeSQLQuery("ANALYZE TABLE GENOMIC_IDENTIFIER_SET_ONT_4_U COMPUTE STATISTICS");
				break;

			default :
				break;
		}
		Logger.log("Finished rebuilding indexes on Intermediate Set-Ont tables.", Logger.INFO);
	}

	/**
	 * Prepares order by queries for steps 2 - 5 , to obtain unique records 
	 * @param stepCount  Current step of summary execution
	 * @return Query String
	 */
	public static String prepareQuery(int stepCount)
	{
		String query = "";
		String columnNames = "";
		switch (stepCount)
		{
			case STEP_2_FOR_UNIQUE_GENES :
				columnNames = SummaryReflectionUtil.getGeneColumnNamesString()
						+ COLUMN_NAMES_DELIMITER + SummaryReflectionUtil.getMrnaColumnNamesString()
						+ COLUMN_NAMES_DELIMITER
						+ SummaryReflectionUtil.getProteinColumnNamesString()
						+ COLUMN_NAMES_DELIMITER + " PATH_ID";

				query = "SELECT DISTINCT " + columnNames + " FROM GENOMIC_IDENTIFIER_SET_ONT_1_U"
						+ " ORDER BY " + SummaryReflectionUtil.getGeneColumnNamesString();
				break;

			case STEP_3_FOR_UNIQUE_MRNAS :
				columnNames = "GENE_ID, " + SummaryReflectionUtil.getMrnaColumnNamesString()
						+ COLUMN_NAMES_DELIMITER
						+ SummaryReflectionUtil.getProteinColumnNamesString()
						+ COLUMN_NAMES_DELIMITER + " PATH_ID";

				query = "SELECT " + columnNames + " FROM GENOMIC_IDENTIFIER_SET_ONT_2_U"
						+ " ORDER BY " + SummaryReflectionUtil.getMrnaColumnNamesString();
				break;

			case STEP_4_FOR_UNIQUE_PROTEINS :
				columnNames = "GENE_ID, MRNA_ID, "
						+ SummaryReflectionUtil.getProteinColumnNamesString()
						+ COLUMN_NAMES_DELIMITER + " PATH_ID";

				query = "SELECT " + columnNames + " FROM GENOMIC_IDENTIFIER_SET_ONT_3_U"
						+ " ORDER BY " + SummaryReflectionUtil.getProteinColumnNamesString();
				break;

			case STEP_5_FOR_UNIQUE_SET_ONT :
				columnNames = "GENE_ID, MRNA_ID, PROTEIN_ID, PATH_ID";
				query = "SELECT DISTINCT " + columnNames + " FROM GENOMIC_IDENTIFIER_SET_ONT_4_U"
						+ " ORDER BY " + columnNames;
				break;

			default :
				break;
		}
		return query;
	}

	/**
	 * Performs clean up operation, deleted older data from intermediate tables, which is no longer needed
	 * @param stepCount Current step of summary execution
	 */
	public static void cleanUpOlderdata(int stepCount)
	{
		Logger.log("Cleaning up older data...", Logger.INFO);
		String query = "";
		switch (stepCount)
		{
			case STEP_2_FOR_UNIQUE_GENES :
				break;

			case STEP_3_FOR_UNIQUE_MRNAS :
				query = "DROP TABLE GENOMIC_IDENTIFIER_SET_ONT_2_U CASCADE CONSTRAINTS";
				break;

			case STEP_4_FOR_UNIQUE_PROTEINS :
				query = "DROP TABLE GENOMIC_IDENTIFIER_SET_ONT_3_U CASCADE CONSTRAINTS";
				break;

			case STEP_5_FOR_UNIQUE_SET_ONT :
				query = "DROP TABLE GENOMIC_IDENTIFIER_SET_ONT_4_U CASCADE CONSTRAINTS";
				break;

			default :
				break;
		}
		connectToDB();
		if (!query.equals(""))
		{
			dbManager.executeSQLQuery(query);
		}
		Logger.log("Finished cleaning of older data...", Logger.INFO);
	}

	/**
	 * Creates index on intermediate table to improve the performance of order by queries 
	 * @param indexNamePrefix Prefix to be used for index names
	 * @param tableName name of the Table for which indexes have to be built 
	 * @param columnNames Names of columns on which indexes have to be built
	 */
	public static void createIndexesOnIntermediateTable(String indexNamePrefix, String tableName,
			List columnNames)
	{
		for (int i = 0; i < columnNames.size(); i++)
		{
			createOrRebuildIndex(indexNamePrefix + "_" + columnNames.get(i), tableName,
					(String) columnNames.get(i));
		}
	}

	/**
	 * Creates index on the given table
	 * @param indexName Index name
	 * @param tableName Name of the Table for which index has to be built
	 * @param columnName Name of the column on which index has to be built
	 */
	public static void createOrRebuildIndex(String indexName, String tableName, String columnName)
	{
		ResultSet resultSet = null;
		if (indexName.length() > MAX_INDEX_NAME_LENGTH)
		{
			indexName = indexName.substring(0, MAX_INDEX_NAME_LENGTH);
		}
		String query = "CREATE INDEX " + indexName + " ON " + tableName + "(" + columnName + ")";
		resultSet = dbManager.executeSQLQuery(query);
		if (resultSet == null)
		{
			//Inxed exists already. Rebuild it.
			query = "ALTER INDEX " + indexName + "   REBUILD NOPARALLEL NOLOGGING";
			dbManager.executeSQLQuery(query);
		}
	}

	/**
	 * Prints record count of all base tables.
	 * Invoked before starting summary table calculation to print count of all base tables.
	 */
	public static void printBaseTableCounts()
	{
		Logger.log("\nBASE TABLE COUNTS : ", Logger.DEBUG);
		BaseTable baseTable = null;
		Iterator iterator = metadataManager.getBaseTableMetaData().values().iterator();
		while (iterator.hasNext())
		{
			baseTable = (BaseTable) iterator.next();
			String sqlQuery = "select count(*) from " + baseTable.getName();
			//execute query to get table count
			ResultSet resultSet = dbManager.executeSQLQuery(sqlQuery);
			System.out.print("\n" + baseTable.getName() + " : ");
			try
			{
				if (resultSet != null)
				{
					while (resultSet.next())
					{
						System.out.print(resultSet.getString(1));
						Logger.log(resultSet.getString(1), Logger.DEBUG);
					}
					resultSet.close();
					resultSet.getStatement().close();
				}
			}
			catch (SQLException e)
			{
				Logger.log("SQLException occured while retrieveing the table counts", Logger.FATAL);
				SummaryExceptionHandler.handleException(e);
			}
		}
		return;
	}

	/**
	 * Prints record count of all Summary tables.
	 * Invoked after starting summary table calculation to print count of all Summary tables.
	 */
	public static void printTableCounts(String delimitedTableNames)
	{
		String[] tableNames = delimitedTableNames.split(",");
		connectToDB();

		for (int i = 0; i < tableNames.length; i++)
		{
			String sqlQuery = "select count(*) from " + tableNames[i];
			//execute query to get table count
			ResultSet resultSet = dbManager.executeSQLQuery(sqlQuery);
			try
			{
				if (resultSet != null)
				{
					while (resultSet.next())
					{
						Logger.log("\nRecord Count of table " + tableNames[i] + " : "
								+ resultSet.getString(1), Logger.INFO);
					}
					resultSet.close();
					resultSet.getStatement().close();
				}
			}
			catch (SQLException e)
			{
				Logger.log("SQLException occured while retrieveing the table counts", Logger.FATAL);
				SummaryExceptionHandler.handleException(e);
			}
		}
		return;
	}

	/**
	 * Establish data base connection using db properties specified in server.properties
	 */
	public static void connectToDB()
	{
		/** set up Data Base connection object*/
		try
		{
			Logger.log("Connecting to DB server...", Logger.DEBUG);
			dbManager.connect();
			Logger.log("Connected successfully.", Logger.DEBUG);
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