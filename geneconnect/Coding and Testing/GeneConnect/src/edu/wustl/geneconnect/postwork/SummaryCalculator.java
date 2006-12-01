/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.postwork.SummaryCalculator</p> 
 */

package edu.wustl.geneconnect.postwork;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.dataminer.server.database.DBManager;
import com.dataminer.server.log.Logger;

import edu.wustl.geneconnect.GeneConnectServerConstants;
import edu.wustl.geneconnect.domain.Gene;
import edu.wustl.geneconnect.domain.MessengerRNA;
import edu.wustl.geneconnect.domain.Protein;
import edu.wustl.geneconnect.metadata.domain.BaseTable;
import edu.wustl.geneconnect.metadata.domain.DataSource;
import edu.wustl.geneconnect.metadata.domain.GenomicIdentifierSetOntWithAllDSIds;
import edu.wustl.geneconnect.metadata.domain.Path;
import edu.wustl.geneconnect.metadata.domain.TableColumn;

/**
 * This class will be invoked during the post work of the GC server run or through "calculateSummary" ant task.
 * It is responsible for calculating all-to-all genomic links (summary for the base tables).
 * 
 * Steps followed are as follows :
 * <br>
 * Step 1: Input : "NonRedundantLongestPaths.txt" - file which contains List of all non redudant longest 
 * paths in GeneConnect graph. This file is prepared by MetaDataCalculator.  
 * 
 * Output : GENOMIC_IDENTIFIER_SET_ONT_1_U - Single unnormalized table with all genomic Identifiers and OntID.
 *     ----------------------------------------------------------------------------------------
 *     | G1  |  G2  |	G3  |	M1  |	M2  |	M3  |	P1  |	P2  |	P3  |	P4  |	OntID  | 
 *     ----------------------------------------------------------------------------------------
 * 
 * <br>
 *
 * Step 2:    
 * Input  : GENOMIC_IDENTIFIER_SET_ONT_1_U   table data - o/p of step 1
 * Output :    
 *     --------
 *     | Gene |  - Gene Table with unique gene records.  
 *     --------
 *     -----------------------------------------------------------------------------
 *     | GeneId  |	M1  |	M2  |	M3  |	P1  |	P2  |	P3  |	P4  |	OntID  | 
 *     -----------------------------------------------------------------------------
 * 
 * <br>
 *
 * Step 3:
 * Input : o/p of step 2
 * Output :
 *     --------		--------
 *     | Gene |		| mRNA |
 *     --------		--------
 *     ------------------------------------------------------------
 *     | GeneId  |   mRNA Id  | P1  | P2  |  P3  |  P4  |  OntID  | 
 *     ------------------------------------------------------------
 * 
 * <br>
 *     
 * Step 4: 
 * Input : o/p of step 3
 * Output :
 *     --------		--------	-----------	
 *     | Gene |		| mRNA |	| Protein |	
 *     --------		--------	-----------	
 *     ----------------------------------------------
 *     | GeneId  |   mRNA Id  | ProteinId |  OntID  | 
 *     ----------------------------------------------
 * 
 * <br>
 *     
 * Step 5:
 * Input : o/p of step 4
 * Output :      
 *     --------		--------	-----------	
 *     | Gene |		| mRNA |	| Protein |	
 *     --------		--------	-----------	
 *     -------------	----------------	----------------	
 *     | Gene_Mrna |	| mRNA_Protein |	| Protein_Gene |	
 *     -------------	----------------	----------------	
 *     -------------------------------------------	--------------------
 *     | SetId  | GeneId  |  mRNA Id | ProteinId |	|  SetId  | OntID  | 
 *     -------------------------------------------	--------------------
 *     
 * @author mahesh_nalkande
 * @version 1.0
 */
public class SummaryCalculator implements GeneConnectServerConstants
{

	/**
	 * Default constructor
	 */
	public SummaryCalculator()
	{
		super();
	}

	/**
	 * Metadata Manager instance used to obtain all metadata. 
	 */
	private MetadataManager metadataManager = MetadataManager.getInstance();

	/**
	 * Data Base Manager instance used for all database operations
	 */
	private DBManager dbManager = DBManager.getInstance();

	/**
	 * Summary Table Writer instance used for writing all summary table data to the o/p files 
	 * which will be uploaded to the database later on.
	 */
	private SummaryTableWriter summaryTableWriter = new SummaryTableWriter();

	/**
	 * Genomic Classes required for reflcetion
	 */
	private Class geneClass = null, messengerRNAClass = null, proteinClass = null;

	/**
	 * Genomic objects
	 */
	private Gene gene = null;
	private MessengerRNA messengerRNA = null;
	private Protein protein = null;

	/**
	 * Output file record counts
	 */
	private long opGeneFileRecordCount = 1, opMrnaFileRecordCount = 1,
			opProteinFileRecordCount = 1, opGene_MrnaFileRecordCount = 1,
			opMrna_ProteinFileRecordCount = 1, opProtein_GeneFileRecordCount = 1,
			opGenomicIdentifierSetFileRecordCount = 1;

	private int genomicIdentifierSetRecordCount, recordCount;

	/**
	 * Genomic Id set records of the current result set maintained in the memory. 
	 * Once it reaches configured limit, all records are dumped into the file
	 */
	private List genomicIdSetRecordsForCurrentResultSet;

	/**
	 * Main method - required for ant task
	 * @param args
	 */
	public static void main(String[] args)
	{
		System.out
				.println("Summary Calculator has been started. See /Logs/ErroLog.txt for details.");
		SummaryCalculator summaryCalculator = new SummaryCalculator();
		summaryCalculator.calculateSummary();
	}

	/**
	 * This method performs summary data calculation operation.  
	 */
	public void calculateSummary()
	{
		Logger.log("Summary Calculator started....", Logger.DEBUG);
		Logger.log("Calculating all-to-all genomic identifier links....", Logger.DEBUG);
		Logger.log("Total memory available : " + Runtime.getRuntime().totalMemory(), Logger.DEBUG);

		//Establish database connection
		SummaryDatabaseUtil.connectToDB();

		//Print Base table counts
		SummaryDatabaseUtil.printBaseTableCounts();

		//Create/Rebuild Base Table Indexes -- to get better performance for outer join queries on base tables
		SummaryDatabaseUtil.createBaseTableIndexes();

		//Initialize genomic classes - required for reflection
		intializeGenomicClasses();

		//Creates "_U" summary tables
		SummaryDatabaseUtil.createTemporarySummaryTables();

		//1. Populate unnormalized table GENOMIC-IDENTIFIER-SET-ONT
		readLongestPathsAndCalculateSummary();

		//2. First Ietration of GENOMIC-IDENTIFIER-SET-ONT to get unique GENEs
		removeRedundantGeneRecordsFromGenomicIdSet();

		//3. Second Iteration of GENOMIC-IDENTIFIER-SET-ONT to get unique MRNAs
		removeRedundantMrnaRecordsFromGenomicIdSet();

		//4. Third Iteration of GENOMIC-IDENTIFIER-SET-ONT to get unique PROTEINs
		removeRedundantProteinRecordsFromGenomicIdSet();

		//5. Fourth Iteration of GENOMIC-IDENTIFIER-SET-ONT to populate unique SET , mapping tables for G-M-P and ONT
		removeRedundantSetRecordsFromGenomicIdSet();

		SummaryDatabaseUtil.renameTemporaryTablesToActualSummaryTables();

		printSummaryTableCounts();

		Logger.log("Calculation of all-to-all genomic identifier links has been"
				+ " completed successfully.", Logger.INFO);
		System.out.println("Calculation of all-to-all genomic identifier links has been"
				+ " completed successfully.");
	}

	/**
	 * Input : "NonRedundantLongestPaths.txt" - file which contains List of all non redudant longest 
	 * paths in GeneConnect graph. This file is prepared by MetaDataCalculator.  
	 * 
	 * Output : GENOMIC_IDENTIFIER_SET_ONT_1_U - Single unnormalized table with al genomic Identifiers and OntID.
	 *     ----------------------------------------------------------------------------------------
	 *     | G1  |  G2  |	G3  |	M1  |	M2  |	M3  |	P1  |	P2  |	P3  |	P4  |	OntID  | 
	 *     ----------------------------------------------------------------------------------------
	 * 
	 * This method executes the first step in Summary table calculation.
	 * It reads all the paths from the NonRedundantLongestPaths.txt. For every path it creates 
	 * an outer join query and executes it. From the result set , it forms the genomic identifier set 
	 * and finds out Ont associated with it (for every record in Result set). At the end the unnormalized table is 
	 * populated with all set-onts (found out from the result set) as well as their possible subsets. 
	 *
	 */
	private void readLongestPathsAndCalculateSummary()
	{
		//Open the output data file to store data for GENOMIC_IDENTIFIER_SET_ONT_1_U table
		summaryTableWriter.openOutputDataFiles(STEP_1_FOR_UNNORMALIZED_SET_ONT);

		//Write header into the o/p file describing table and column which will be populated using 
		//data from particular file
		summaryTableWriter.writeOutputDataFileHeaders(STEP_1_FOR_UNNORMALIZED_SET_ONT);

		try
		{
			//Read Non Redundant Longest Paths from file (produced by MetaData Calculator)
			BufferedReader longestPathsFile = new BufferedReader(new FileReader(
					NON_REDUNDANT_LONGEST_PATHS_FILE_NAME));
			String currentLongestPath = "";

			//Query buider instance -- which dynamically genrates outer join query for a given path 
			QueryBuilder queryBuilder = new QueryBuilder();

			//Base Table Names involved in the current query
			List tablesInvolvedInCurrentQuery = null;

			// For each longest path generate and execute oueter join query  
			while ((currentLongestPath = longestPathsFile.readLine()) != null)
			{
				tablesInvolvedInCurrentQuery = new ArrayList();

				//Generate oueter join query dynamically
				String query = queryBuilder.buildQuery(currentLongestPath,
						tablesInvolvedInCurrentQuery);

				Logger.log("\n Query generated for current path is " + currentLongestPath + " : "
						+ query, Logger.DEBUG);

				executeQueryAndPopulateSummaryTable(currentLongestPath, query,
						tablesInvolvedInCurrentQuery);
			}
		}
		catch (FileNotFoundException e)
		{
			Logger.log("Can not found the file 'NonRedundantLongestPaths.txt' in the "
					+ "current directory.\n", Logger.FATAL);
			System.out
					.println("Can not found the file 'NonRedundantLongestPaths.txt' in the current directory.\n");
			System.out
					.println("Run ant task calculateMetaData to create this file and run the calculateSummary "
							+ "task again. Refer readme file for the same or write to 'help@mga.wustl.edu'.");
			e.printStackTrace();
		}
		catch (IOException e)
		{
			Logger.log("IOException occured while reading the file 'NonRedundantLongestPaths.txt' "
					+ "in the current directory.\n", Logger.FATAL);
			SummaryExceptionHandler.handleException(e);
		}
		//Upload all output data files into database (summary tables)
		summaryTableWriter.uploadOutputDataFiles(STEP_1_FOR_UNNORMALIZED_SET_ONT);

		summaryTableWriter.closeOutputDataFiles(STEP_1_FOR_UNNORMALIZED_SET_ONT);

		//Print Summary table counts
		Logger.log("Table Counts after first step :", Logger.INFO);
		SummaryDatabaseUtil.printTableCounts("GENOMIC_IDENTIFIER_SET_ONT_1_U");
	}

	/**
	 * Executes the outer join query generated for current non redundant longest path
	 * and populates summary table
	 * @param currentLongestPath  String representing current non redundant longest path
	 * @param query Outer join query genrated for current path
	 * @param tablesInvolvedInCurrentQuery List of base table names involved in current path
	 */
	private void executeQueryAndPopulateSummaryTable(String currentLongestPath, String query,
			List tablesInvolvedInCurrentQuery)
	{
		try
		{
			//Temporary line : should be removed..
			//query = query + " where rownum<10000";

			//Execute generated query
			ResultSet resultSet = dbManager.executeSQLQuery(query);

			if (resultSet != null)
			{
				//count of genomic Id set added for current path
				genomicIdentifierSetRecordCount = 0;
				//Count of genomic Id set records maintained in memory. Once it reaches 
				//configured limit, all records are dumped into the file
				recordCount = 0;
				//Intialize list to hold genomic Id set records in memory 
				genomicIdSetRecordsForCurrentResultSet = new ArrayList(
						MAX_GENOMIC_ID_SET_RECORDS_TO_BE_PROCESSED);

				//Identify Genomic Id set and its logical subsets from the result set 
				//and Write those into output files
				populateSummaryTables(tablesInvolvedInCurrentQuery, resultSet);

				Logger.log("Genomic Identifier Set Record Count (for path " + currentLongestPath
						+ ") : " + genomicIdentifierSetRecordCount, Logger.DEBUG);

				//If there are any Genomic Id set records in the memory, 
				//dump them to the file and clear the list in the memory 
				if (recordCount > 0)
				{
					writeGenomicIdentifierSetOntFromList();
					genomicIdSetRecordsForCurrentResultSet.clear();
					genomicIdSetRecordsForCurrentResultSet = null;
					Runtime.getRuntime().gc();
				}
				resultSet.close();
				resultSet.getStatement().close();
			}
			else
			{
				Logger.log("\n ERROR : Path : " + currentLongestPath
						+ "\n Exception while executing query : " + query + "\n", Logger.INFO);
			}
		}
		catch (SQLException e)
		{
			Logger.log("SQLException occured while executing the outer join query for the path "
					+ currentLongestPath, Logger.FATAL);
			SummaryExceptionHandler.handleException(e);
		}
	}

	/**
	 * This method populates summary tables from the result set of currently executed outer join query
	 * It reads the resultset and identifies genomic Id sets from it. Adds each such set, logical subsets of it 
	 * and ont associated with it to the list of set data maintained the memory. This list is dumped into file 
	 * at regular interval. 
	 *  
	 * @param currentLongestPath Current non redudant longest path for which outer join query has been executed
	 * @param tablesInvolvedInCurrentQuery List of base table names involved in current outer join query 
	 * @param resultSet Result of outer join query which has been executed for currentLongestPath
	 */
	private void populateSummaryTables(List tablesInvolvedInCurrentQuery, ResultSet resultSet)
	{
		BaseTable baseTable = null;
		TableColumn tableColumn = null;
		List columns = null;
		int resultCount = 0;
		try
		{
			//Iterate over every record in the result
			while (resultSet.next())
			{
				//Column Index to iterate over all columns in the current record
				int columnIndex = 1;

				//To identify all column names in the current record , get the list of all base tables involved 
				//in the current query and then get all columns of each of the base table

				//Iterate over the list of all base table names involved in the current query
				Iterator iterator = tablesInvolvedInCurrentQuery.iterator();

				//Simultaneously also decide the path and link types traversed in order to
				//populate current genomic identifier set
				String pathTraversed = "";
				String linkTypesTraversed = "";

				//Map of values belonging to current genomic identifier set
				Map genomicIdentifierSetValues = new HashMap();

				while (iterator.hasNext())
				{
					baseTable = (BaseTable) iterator.next();
					//Names of all columns of current base table 
					columns = metadataManager.getAllColumns(baseTable.getId());
					int baseTableValuesPresent = 0;

					//Iterate over every column in the list
					Iterator columnIterator = columns.iterator();
					while (columnIterator.hasNext())
					{
						tableColumn = (TableColumn) columnIterator.next();
						//check whether the current value from resut set record belongs to any data source
						if (tableColumn.getDataSourceId() != null)
						{
							//Order of result columns mathches the order in which we are iterating the columns 
							//(query has been genrated accordingly)
							//So access the result set column in sequence using columnIndex
							if (resultSet.getObject(columnIndex) != null)
							{
								//If current value is not null , add it to map of values maintained for 
								//current Genomic set. Add (Datasource ID , value) pair the map , 
								//where Datasource Id is the id of the datasource to whom current value belongs 
								DataSource dataSource = metadataManager.getDataSource(tableColumn
										.getDataSourceId());
								genomicIdentifierSetValues.put(tableColumn.getDataSourceId(),
										getValueFromResultSet(resultSet, dataSource, columnIndex));

								baseTableValuesPresent++;
							}
						}
						else
						{ //Current value does not belongs to any Data source. So it is link type Id.
							if (resultSet.getObject(columnIndex) != null)
							{
								if (linkTypesTraversed.equals("")) //Add it to the list of link types traversed
								{
									linkTypesTraversed = resultSet.getString(columnIndex);
								}
								else
								{
									linkTypesTraversed = linkTypesTraversed + LINK_TYPES_DELIMITER
											+ resultSet.getString(columnIndex);
								}
							}
						}
						columnIndex++; //Move to the next column in the current result set row.
					}

					if (baseTableValuesPresent > 0)
					{ //If the values from the current base table are present, then add source and destination 
						//datasource Ids of the current base table to the path.  
						if (pathTraversed.equals(""))
						{
							pathTraversed = baseTable.getSourceDataSourceId()
									+ PATH_NODES_DELIMITER + baseTable.getDestinationDataSourceId();
						}
						else
						{
							pathTraversed = pathTraversed + PATH_NODES_DELIMITER
									+ baseTable.getDestinationDataSourceId();
						}
					}
				}
				/** Populate genomic id set and write Records To Output File **/
				populateGenomicIdentifierSets(genomicIdentifierSetValues, pathTraversed,
						linkTypesTraversed);
				resultCount++;
			}
		}
		catch (SQLException e)
		{
			Logger.log("SQLException occured while iterating over the outer join query result set",
					Logger.FATAL);
			SummaryExceptionHandler.handleException(e);
		}
		Logger.log("Total no. of records in the result set : " + resultCount, Logger.DEBUG);
	}

	/**
	 * Populates Genomic Identifier Set and logical subsets of it using Map of values from current result set row.
	 * @param genomicIdentifierSetValues Map of genomic Ids. (data source id, value) 
	 * @param pathTraversed Path traversed to get genomic Id set values. (comma separated list of data source ids)
	 * @param linkTypesTraversed Link types traversed to get genomic Id set values. 
	 * (comma separated list of link type ids)
	 */
	private void populateGenomicIdentifierSets(Map genomicIdentifierSetValues,
			String pathTraversed, String linkTypesTraversed)
	{
		//Obtain complete path information from the comma separated list of data source ids
		Path pathTraversedObject = metadataManager.getPath(pathTraversed);
		List pathIds = new ArrayList();

		//Add Id of current path as well of its subpaths to the list
		pathIds.add(pathTraversedObject.getId());
		String subPathIdsCSV = metadataManager.getSubPaths(pathTraversedObject.getId());
		if (!subPathIdsCSV.equals(""))
		{
			String[] subPathIdentifiers = subPathIdsCSV.split(SUBPATH_IDS_DELIMITER);

			for (int i = 0; i < subPathIdentifiers.length; i++)
			{
				pathIds.add(new Long(subPathIdentifiers[i]));
			}
		}

		Long currentPathId, currentOntId;
		String linkTypesTraversedForCurrentPath = "";

		//For every path in the list find out its associated ont using link type traversed information available 
		Iterator iterator = pathIds.iterator();
		while (iterator.hasNext())
		{
			//Using path Id, obtain comma separated list of data source ids for current path
			currentPathId = (Long) iterator.next();
			String currentPath = ((Path) metadataManager.getPath(currentPathId)).getCompletePath();

			//Populate Gene, mrna and protain objects using the values from the map.
			//Only values for those data sources which are part of current path are populated 
			populateGenomicObjects(genomicIdentifierSetValues, currentPath);

			linkTypesTraversedForCurrentPath = getLinkTypesTraversedForCurrentPath(pathTraversed,
					linkTypesTraversed, currentPath);

			String ontIdsCSV = metadataManager.getOnts(currentPathId); //List of onts for the current path
			if (!ontIdsCSV.equals(""))
			{
				String[] ontIdentifiers = ontIdsCSV.split(ONT_IDS_DELIMITER);

				for (int i = 0; i < ontIdentifiers.length; i++)
				{
					currentOntId = new Long(ontIdentifiers[i]);

					if (linkTypesTraversedForCurrentPath.equals(metadataManager
							.getOntLinkTypes(currentOntId)))
					{
						//if list of link types traversed for current path matches with list of link types of ont, 
						// write current gset and ont id to the file. 
						writeRecordsToOutputFile(currentOntId.toString());
					}
				}
			}
		}
	}

	/**
	 * Write genomic identifier set values (obatined from already popualted gene , mrna and protein object) and ontId.
	 * Set-ont records are maintained in the memory. Once it reaches configured limit,
	 * all records are dumped into the file.
	 * @param currentOntId Order of Node traversal for current genomic identifier set values
	 */
	private void writeRecordsToOutputFile(String currentOntId)
	{
		//Add current set-ont record to the list of genomic id se records in the memory.
		genomicIdSetRecordsForCurrentResultSet.add(new GenomicIdentifierSetOntWithAllDSIds(gene,
				messengerRNA, protein, Long.parseLong(currentOntId)));
		genomicIdentifierSetRecordCount++;
		recordCount++;

		if (recordCount == MAX_GENOMIC_ID_SET_RECORDS_TO_BE_PROCESSED)
		{
			//Record count of genoic id set records has reached its maximum configured peak value 
			//Dump them into the output file.
			Logger.log("Maximum limit of genomic identifier set records to be processed ("
					+ MAX_GENOMIC_ID_SET_RECORDS_TO_BE_PROCESSED + ") has been reached.",
					Logger.DEBUG);
			writeGenomicIdentifierSetOntFromList();
			recordCount = 0;
			//clear the list in the memory
			genomicIdSetRecordsForCurrentResultSet.clear();
			genomicIdSetRecordsForCurrentResultSet = null;
			Runtime.getRuntime().gc();
			genomicIdSetRecordsForCurrentResultSet = new ArrayList(
					MAX_GENOMIC_ID_SET_RECORDS_TO_BE_PROCESSED);
		}
		return;
	}

	/**
	 * Writes all genomis id set records from the memory to the output file.
	 * While adding these records , it pick ups the unique records from the list.
	 * To identify duplicate records, gset records list is sorted and every record is compared with its previous record. 
	 */
	private void writeGenomicIdentifierSetOntFromList()
	{
		Logger.log("Processing " + genomicIdSetRecordsForCurrentResultSet.size()
				+ " records to populate GenomicIdentifierSet_Ont_Step1.dat...", Logger.DEBUG);

		GenomicIdentifierSetOntWithAllDSIds genomicIdentifierSetOnt = null;
		GenomicIdentifierSetOntWithAllDSIds prevGenomicIdentifierSetOnt = new GenomicIdentifierSetOntWithAllDSIds();

		//Sort genomic id set records to easily identify duplicate records.
		Collections.sort(genomicIdSetRecordsForCurrentResultSet);

		Logger.log("Sorted " + genomicIdSetRecordsForCurrentResultSet.size()
				+ " records successfully..", Logger.DEBUG);

		long prevGenomicIdentifierSetFileRecordCount = opGenomicIdentifierSetFileRecordCount;

		Iterator iterator = genomicIdSetRecordsForCurrentResultSet.iterator();
		while (iterator.hasNext())
		{
			genomicIdentifierSetOnt = (GenomicIdentifierSetOntWithAllDSIds) iterator.next();

			//Write record to file if and only if it is not matching the previous record.
			if (!genomicIdentifierSetOnt.equals(prevGenomicIdentifierSetOnt))
			{
				summaryTableWriter.writeGenomicIdentifierSetWithAllDSIdsToFile(
						opGenomicIdentifierSetFileRecordCount, genomicIdentifierSetOnt);
				opGenomicIdentifierSetFileRecordCount++;
				prevGenomicIdentifierSetOnt = genomicIdentifierSetOnt;
			}
		}
		Logger.log(
				(opGenomicIdentifierSetFileRecordCount - prevGenomicIdentifierSetFileRecordCount)
						+ " set records have been added for current result set.", Logger.DEBUG);
		return;
	}

	/**
	 * Populate Gene, mrna and protain objects using the values from the map.
	 * Only values for those data sources which are part of current path are populated.
	 */
	private void populateGenomicObjects(Map genomicIdentifierSetValues, String pathTraversed)
	{
		gene = null;
		messengerRNA = null;
		protein = null;
		Long dataSourceId = null;

		String[] dataSourceIdsInCurrentPath = pathTraversed.split(PATH_NODES_DELIMITER);

		for (int i = 0; i < dataSourceIdsInCurrentPath.length; i++)
		{
			dataSourceId = new Long(dataSourceIdsInCurrentPath[i]);
			//Sets identifier value into Genomic object(gene, mrna or protein) using reflection.
			setIdentifierValueInGenomicClass(genomicIdentifierSetValues.get(dataSourceId),
					metadataManager.getDataSource(dataSourceId));
		}
	}

	/**
	 * Retrieves value from the result set cell. Type of value is obtained from data source information  
	 * @param resultSet Result set of current outer join query executed.
	 * @param dataSource Information of Data source whose value is represented by current cell in result set. 
	 * @param columnIndex Index of column / cell in current record of result set
	 * @return Value from the result set.  
	 */
	private Object getValueFromResultSet(ResultSet resultSet, DataSource dataSource, int columnIndex)
	{
		String[] dataType = dataSource.getAttributeType().split("\\.");
		try
		{
			if (dataType[2].equals("String"))
			{
				return resultSet.getString(columnIndex);
			}
			else if (dataType[2].equals("Long"))
			{
				return new Long(resultSet.getLong(columnIndex));
			}
			else
			{
				resultSet.getObject(columnIndex);
			}
		}
		catch (SQLException e)
		{
			Logger.log("SQLException occured while fetching the value from the outer join "
					+ "query result set.", Logger.FATAL);
			SummaryExceptionHandler.handleException(e);
		}
		return null;
	}

	/**
	 * Sets identifier value into Genomic object(gene, mrna or protein) using reflection
	 * @param identifierValue Value from genomic id set 
	 * @param dataSource Information of Data source to whom current genomic id belongs to.
	 */
	private void setIdentifierValueInGenomicClass(Object identifierValue, DataSource dataSource)
	{
		try
		{
			Class[] argType = new Class[1];
			Object[] argValues = new Object[1];
			argValues[0] = identifierValue;

			if (dataSource.getClassName().equals("Gene"))
			{
				if (gene == null)
				{
					gene = (Gene) geneClass.newInstance();
				}
				Class typeClass = Class.forName(dataSource.getAttributeType());
				argType[0] = typeClass;
				Method method = geneClass.getMethod("set"
						+ SummaryReflectionUtil.getSentenceCase(dataSource.getAttributeName()),
						argType);
				method.invoke(gene, argValues);
			}
			else if (dataSource.getClassName().equals("MessengerRNA"))
			{
				if (messengerRNA == null)
				{
					messengerRNA = (MessengerRNA) messengerRNAClass.newInstance();
				}
				Class typeClass = Class.forName(dataSource.getAttributeType());
				argType[0] = typeClass;
				Method method = messengerRNAClass.getMethod("set"
						+ SummaryReflectionUtil.getSentenceCase(dataSource.getAttributeName()),
						argType);
				method.invoke(messengerRNA, argValues);
			}
			else if (dataSource.getClassName().equals("Protein"))
			{
				if (protein == null)
				{
					protein = (Protein) proteinClass.newInstance();
				}
				Class typeClass = Class.forName(dataSource.getAttributeType());
				argType[0] = typeClass;
				Method method = proteinClass.getMethod("set"
						+ SummaryReflectionUtil.getSentenceCase(dataSource.getAttributeName()),
						argType);
				method.invoke(protein, argValues);
			}
		}
		catch (InstantiationException e)
		{
			Logger.log("InstantiationException occured while instantiating the Genomic object"
					+ "(gene, mrna or protein).", Logger.FATAL);
			SummaryExceptionHandler.handleException(e);
		}
		catch (IllegalAccessException e)
		{
			Logger.log("IllegalAccessException occured while seting the genomic identifier value "
					+ "into Genomic object(gene, mrna or protein) using reflection.", Logger.FATAL);
			SummaryExceptionHandler.handleException(e);
		}
		catch (ClassNotFoundException e)
		{
			Logger.log("ClassNotFoundException occured while instantiating the Genomic object"
					+ "(gene, mrna or protein).", Logger.FATAL);
			SummaryExceptionHandler.handleException(e);
		}
		catch (SecurityException e)
		{
			Logger.log("SecurityException occured while seting the genomic identifier value "
					+ "into Genomic object(gene, mrna or protein) using reflection.", Logger.FATAL);
			SummaryExceptionHandler.handleException(e);
		}
		catch (NoSuchMethodException e)
		{
			Logger.log("NoSuchMethodException occured while seting the genomic identifier value "
					+ "into Genomic object(gene, mrna or protein) using reflection.", Logger.FATAL);
			SummaryExceptionHandler.handleException(e);
		}
		catch (IllegalArgumentException e)
		{
			Logger.log(
					"IllegalArgumentException occured while seting the genomic identifier value "
							+ "into Genomic object(gene, mrna or protein) using reflection.",
					Logger.FATAL);
			SummaryExceptionHandler.handleException(e);
		}
		catch (InvocationTargetException e)
		{
			Logger.log(
					"InvocationTargetException occured while seting the genomic identifier value "
							+ "into Genomic object(gene, mrna or protein) using reflection.",
					Logger.FATAL);
			SummaryExceptionHandler.handleException(e);
		}
	}

	/**
	 * Obtains link types of current sub path from the link types of the complete path
	 * @param completePathTraversed complete path
	 * @param linkTypesTraversed link types of complete path 
	 * @param currentSubPath sub path whose link types needs to be find out
	 * @return Link types of current sub path from the link types of the complete path
	 */
	private String getLinkTypesTraversedForCurrentPath(String completePathTraversed,
			String linkTypesTraversed, String currentSubPath)
	{
		if (currentSubPath.equals(completePathTraversed))
		{
			return linkTypesTraversed;
		}
		String linkTypesTraversedClipped = "";
		int subPathIndex = completePathTraversed.indexOf(currentSubPath);
		if (subPathIndex != -1)
		{
			linkTypesTraversedClipped = linkTypesTraversed.substring(subPathIndex, subPathIndex
					+ currentSubPath.length() - 2);
			return linkTypesTraversedClipped;
		}
		else
		{
			return "";
		}
	}

	/**
	 * Initialize genomic classes - required for reflection
	 */
	private void intializeGenomicClasses()
	{
		try
		{
			geneClass = Class.forName("edu.wustl.geneconnect.domain.Gene");
			messengerRNAClass = Class.forName("edu.wustl.geneconnect.domain.MessengerRNA");
			proteinClass = Class.forName("edu.wustl.geneconnect.domain.Protein");
		}
		catch (ClassNotFoundException e)
		{
			Logger.log("ClassNotFoundException occured while instantiating the Genomic class "
					+ "(gene, mrna or protein).", Logger.FATAL);
			SummaryExceptionHandler.handleException(e);
		}
	}

	/**
	 * Step 2 : First Ietration of GENOMIC-IDENTIFIER-SET-ONT to get unique GENEs
	 * Input :
	 *     ----------------------------------------------------------------------------------------
	 *     | G1  |  G2  |	G3  |	M1  |	M2  |	M3  |	P1  |	P2  |	P3  |	P4  |	OntID  | 
	 *     ----------------------------------------------------------------------------------------
	 * Output :    
	 *     --------
	 *     | Gene |    - Gene Table with unique Gene records
	 *     --------
	 *     -----------------------------------------------------------------------------
	 *     | GeneId  |	M1  |	M2  |	M3  |	P1  |	P2  |	P3  |	P4  |	OntID  | 
	 *     -----------------------------------------------------------------------------
	 * 
	 * Steps followed :
	 * 1. To obtain the output , I/p table is sorted in the order G1, G2 and G3. 
	 * 2. Iterate over records in sorted result. 
	 * 3. Compare every gene record with its previous record , to find out whether it is redundant or not.
	 * 4. Write all unique mrna and all genomic id set records to the file and upload it to database.  
	 */
	private void removeRedundantGeneRecordsFromGenomicIdSet()
	{
		Logger.log("Step 2: Removing Redundant Gene Records From Genomic Id Set...", Logger.INFO);

		SummaryDatabaseUtil.connectToDB();
		summaryTableWriter.openOutputDataFiles(STEP_2_FOR_UNIQUE_GENES);
		summaryTableWriter.writeOutputDataFileHeaders(STEP_2_FOR_UNIQUE_GENES);
		SummaryDatabaseUtil.createAndRebuildIndexes(STEP_2_FOR_UNIQUE_GENES); // to get better performance for order by query 

		String query = SummaryDatabaseUtil.prepareQuery(STEP_2_FOR_UNIQUE_GENES); //form the query
		ResultSet resultSet = dbManager.executeSQLQuery(query);

		opGeneFileRecordCount = 1;
		opGenomicIdentifierSetFileRecordCount = 1;

		//Obtain the count of Gene columns(data sources). 
		//(This is to take care of addition of any genomic datasouerec in future) 
		int geneColumnsCount = SummaryReflectionUtil.getGeneColumnNames().size();
		int totalColumnsCount = geneColumnsCount
				+ SummaryReflectionUtil.getMrnaColumnNames().size()
				+ SummaryReflectionUtil.getProteinColumnNames().size() + 1;
		List genomicSetValues = null;
		List currentGeneValues = null, prevGeneValues = null;

		try
		{
			while (resultSet.next())
			{
				genomicSetValues = new ArrayList();

				//List to collect gene values from the current row of result set
				currentGeneValues = getObjectValues(resultSet, 1, geneColumnsCount);

				//Check if any one of gene values is present or not.
				if (currentGeneValues != null)
				{
					if (!equalValues(currentGeneValues, prevGeneValues))
					{
						//Atleast a single gene value is present in current row of result set
						// and gene values are different from previous gene record. 
						summaryTableWriter.writeGeneRecordToFile(opGeneFileRecordCount,
								currentGeneValues); //Add distinct gene record to the table
						prevGeneValues = currentGeneValues;
						opGeneFileRecordCount++;
					}
					//Add gene Id to the o/p file (o/p table data)
					genomicSetValues.add(new Long(opGeneFileRecordCount - 1));
				}
				else
				{
					//If current row from result set , don't have single Gene value, put null for GeneId in o/p file
					genomicSetValues.add(null);
				}

				//Add rest of the genomic values from result set to the o/p file
				for (int columnIndex = geneColumnsCount + 1; columnIndex <= totalColumnsCount; columnIndex++)
				{
					genomicSetValues.add(resultSet.getObject(columnIndex));
				}
				//Write all genomic values to o/p file (o/p table data)
				summaryTableWriter.writeIntermediateGenomicIdentifierSetToFile(genomicSetValues);
				opGenomicIdentifierSetFileRecordCount++;
			}
			resultSet.close();
			resultSet.getStatement().close();
		}
		catch (SQLException e)
		{
			Logger.log("SQLException occured while removing Redundant Gene Records From "
					+ "Genomic Identifier Set.", Logger.FATAL);
			SummaryExceptionHandler.handleException(e);
		}
		Logger.log("Total Count of Gene Records : " + (opGeneFileRecordCount - 1), Logger.INFO);
		Logger.log("Total Count of Intermediate Genomic Identifier Set Records : "
				+ (opGenomicIdentifierSetFileRecordCount - 1), Logger.INFO);
		SummaryDatabaseUtil.cleanUpOlderdata(STEP_2_FOR_UNIQUE_GENES);
		summaryTableWriter.uploadOutputDataFiles(STEP_2_FOR_UNIQUE_GENES);
		summaryTableWriter.closeOutputDataFiles(STEP_2_FOR_UNIQUE_GENES);

		Logger.log("Redundant Gene Records From Genomic Id Set have been removed.", Logger.INFO);

		//Print Summary table counts
		Logger.log("Table Counts after second step :", Logger.INFO);
		SummaryDatabaseUtil.printTableCounts("GENOMIC_IDENTIFIER_SET_ONT_2_U,GENE_U");
	}

	/**
	 * Step 3 : Second Iteration of GENOMIC-IDENTIFIER-SET-ONT to get unique MRNAs
	 * Input :
	 *     --------
	 *     | Gene |
	 *     --------
	 *     -----------------------------------------------------------------------------
	 *     | GeneId  |	M1  |	M2  |	M3  |	P1  |	P2  |	P3  |	P4  |	OntID  | 
	 *     -----------------------------------------------------------------------------
	 *  
	 *  Output :    
	 *     --------		--------
	 *     | Gene |		| mRNA | - Mrna table with unique mrna records
	 *     --------		--------
	 *     ------------------------------------------------------------
	 *     | GeneId  |   mRNA Id  | P1  | P2  |  P3  |  P4  |  OntID  | 
	 *     ------------------------------------------------------------
	 * 
	 * Steps followed :
	 * 1. To obtain the output , I/p table is sorted in the order M1, M2 and M3. 
	 * 2. Iterate over records in sorted result. 
	 * 3. Compare every mrna record with its previous record , to find out whether it is redundant or not. 
	 * 4. Write all unique mrna and all genomic id set records to the file and upload it to database.  
	 */
	private void removeRedundantMrnaRecordsFromGenomicIdSet()
	{
		Logger.log("Step 3: Removing Redundant Mrna Records From Genomic Id Set...", Logger.INFO);

		SummaryDatabaseUtil.connectToDB();
		summaryTableWriter.openOutputDataFiles(STEP_3_FOR_UNIQUE_MRNAS);
		summaryTableWriter.writeOutputDataFileHeaders(STEP_3_FOR_UNIQUE_MRNAS);
		SummaryDatabaseUtil.createAndRebuildIndexes(STEP_3_FOR_UNIQUE_MRNAS); // to get better performance for order by query 

		String query = SummaryDatabaseUtil.prepareQuery(STEP_3_FOR_UNIQUE_MRNAS); //form the query
		ResultSet resultSet = dbManager.executeSQLQuery(query);

		opMrnaFileRecordCount = 1;
		opGenomicIdentifierSetFileRecordCount = 1;

		//Obtain the count of mrna columns(data sources). 
		//(This is to take care of addition of any genomic datasouerec in future) 
		int mrnaColumnsCount = SummaryReflectionUtil.getMrnaColumnNames().size();
		int totalColumnsCount = mrnaColumnsCount
				+ SummaryReflectionUtil.getProteinColumnNames().size() + 2;

		List genomicSetValues = null;
		List currentMrnaValues = null, prevMrnaValues = null;

		try
		{
			while (resultSet.next())
			{
				genomicSetValues = new ArrayList();
				genomicSetValues.add(resultSet.getObject(1));

				//List to collect mrna values from the current row of result set
				currentMrnaValues = getObjectValues(resultSet, 2, mrnaColumnsCount + 1);

				//Check if any one of mrna values is present or not.
				if (currentMrnaValues != null)
				{
					if (!equalValues(currentMrnaValues, prevMrnaValues))
					{
						//Atleast a single mrna value is present in current row of result set
						// and mrna values are different from previous mrna record. 
						summaryTableWriter.writeMrnaRecordToFile(opMrnaFileRecordCount,
								currentMrnaValues);//Add distinct mrna record to the table
						prevMrnaValues = currentMrnaValues;
						opMrnaFileRecordCount++;
					}
					//Add mrna Id to the o/p file (o/p table data)
					genomicSetValues.add(new Long(opMrnaFileRecordCount - 1));
				}
				else
				{
					//If current row from result set , don't have single Mrna value, put null for mrnaId in o/p file
					genomicSetValues.add(null);
				}
				//Add rest of the genomic values from result set to the o/p file
				for (int columnIndex = mrnaColumnsCount + 2; columnIndex <= totalColumnsCount; columnIndex++)
				{
					genomicSetValues.add(resultSet.getObject(columnIndex));
				}
				summaryTableWriter.writeIntermediateGenomicIdentifierSetToFile(genomicSetValues);
				opGenomicIdentifierSetFileRecordCount++;
			}
			resultSet.close();
			resultSet.getStatement().close();
		}
		catch (SQLException e)
		{
			Logger.log("SQLException occured while removing Redundant Mrna Records From "
					+ "Genomic Identifier Set.", Logger.FATAL);
			SummaryExceptionHandler.handleException(e);
		}

		Logger.log("Total Count of Mrna Records : " + (opMrnaFileRecordCount - 1), Logger.DEBUG);
		Logger.log("Total Count of Intermediate Genomic Identifier Set Records : "
				+ (opGenomicIdentifierSetFileRecordCount - 1), Logger.DEBUG);
		SummaryDatabaseUtil.cleanUpOlderdata(STEP_3_FOR_UNIQUE_MRNAS);
		summaryTableWriter.uploadOutputDataFiles(STEP_3_FOR_UNIQUE_MRNAS);
		summaryTableWriter.closeOutputDataFiles(STEP_3_FOR_UNIQUE_MRNAS);

		Logger.log("Redundant Mrna Records From Genomic Id Set have been removed.", Logger.INFO);

		//Print Summary table counts
		Logger.log("Table Counts after third step :", Logger.INFO);
		SummaryDatabaseUtil.printTableCounts("GENOMIC_IDENTIFIER_SET_ONT_3_U,MRNA_U");
	}

	/**
	 * Step 4 : Third Iteration of GENOMIC-IDENTIFIER-SET-ONT to get unique PROTEINs
	 * Input :
	 *     --------		--------
	 *     | Gene |		| mRNA |
	 *     --------		--------
	 *     ------------------------------------------------------------
	 *     | GeneId  |   mRNA Id  | P1  | P2  |  P3  |  P4  |  OntID  | 
	 *     ------------------------------------------------------------
	 *     
	 * Output :
	 *     --------		--------	-----------	
	 *     | Gene |		| mRNA |	| Protein | - Protein table with unique protein records	
	 *     --------		--------	-----------	
	 *     ----------------------------------------------
	 *     | GeneId  |   mRNA Id  | ProteinId |  OntID  | 
	 *     ----------------------------------------------
	 * 
	 * Steps followed :
	 * 1. To obtain the output , I/p table is sorted in the order P1, P2, P3 and P4. 
	 * 2. Iterate over records in sorted result. 
	 * 3. Compare every protein record with its previous record , to find out whether it is redundant or not. 
	 * 4. Write all unique protein and all genomic id set records to the file and upload it to database.
	 */
	private void removeRedundantProteinRecordsFromGenomicIdSet()
	{
		Logger
				.log("Step 3: Removing Redundant Protein Records From Genomic Id Set...",
						Logger.INFO);

		SummaryDatabaseUtil.connectToDB();
		summaryTableWriter.openOutputDataFiles(STEP_4_FOR_UNIQUE_PROTEINS);
		summaryTableWriter.writeOutputDataFileHeaders(STEP_4_FOR_UNIQUE_PROTEINS);
		SummaryDatabaseUtil.createAndRebuildIndexes(STEP_4_FOR_UNIQUE_PROTEINS); // to get better performance for order by query 

		String query = SummaryDatabaseUtil.prepareQuery(STEP_4_FOR_UNIQUE_PROTEINS); //form the query
		ResultSet resultSet = dbManager.executeSQLQuery(query);

		opProteinFileRecordCount = 1;
		opGenomicIdentifierSetFileRecordCount = 1;

		//Obtain the count of Protein columns(data sources). 
		//(This is to take care of addition of any genomic datasouerec in future) 
		int proteinColumnsCount = SummaryReflectionUtil.getProteinColumnNames().size();
		int totalColumnsCount = proteinColumnsCount + 3;

		List genomicSetValues = null;
		List currentProteinValues = null, prevProteinValues = null;

		try
		{
			while (resultSet.next())
			{
				genomicSetValues = new ArrayList();
				genomicSetValues.add(resultSet.getObject(1));
				genomicSetValues.add(resultSet.getObject(2));

				//List to collect protein values from the current row of result set
				currentProteinValues = getObjectValues(resultSet, 3, proteinColumnsCount + 2);

				//Check if any one of protein values is present or not.
				if (currentProteinValues != null)
				{
					if (!equalValues(currentProteinValues, prevProteinValues))
					{
						//Atleast a single protein value is present in current row of result set
						// and protein values are different from previous protein record. 
						summaryTableWriter.writeProteinRecordToFile(opProteinFileRecordCount,
								currentProteinValues);//Add distinct protein record to the table
						prevProteinValues = currentProteinValues;
						opProteinFileRecordCount++;
					}
					//Add protein Id to the o/p file (o/p table data)
					genomicSetValues.add(new Long(opProteinFileRecordCount - 1));
				}
				else
				{
					//If current row from result set , don't have single Protein value, put null for ProteinId in o/p file
					genomicSetValues.add(null);
				}
				//Add rest of the genomic values from result set to the o/p file
				for (int columnIndex = proteinColumnsCount + 3; columnIndex <= totalColumnsCount; columnIndex++)
				{
					genomicSetValues.add(resultSet.getObject(columnIndex));
				}
				summaryTableWriter.writeIntermediateGenomicIdentifierSetToFile(genomicSetValues);
				opGenomicIdentifierSetFileRecordCount++;
			}
			resultSet.close();
			resultSet.getStatement().close();
		}
		catch (SQLException e)
		{
			Logger.log("SQLException occured while removing Redundant protein Records From "
					+ "Genomic Identifier Set.", Logger.FATAL);
			SummaryExceptionHandler.handleException(e);
		}

		Logger.log("Total Count of Protein Records : " + (opProteinFileRecordCount - 1),
				Logger.INFO);
		Logger.log("Total Count of Intermediate Genomic Identifier Set Records : "
				+ (opGenomicIdentifierSetFileRecordCount - 1), Logger.DEBUG);
		SummaryDatabaseUtil.cleanUpOlderdata(STEP_4_FOR_UNIQUE_PROTEINS);
		summaryTableWriter.uploadOutputDataFiles(STEP_4_FOR_UNIQUE_PROTEINS);
		summaryTableWriter.closeOutputDataFiles(STEP_4_FOR_UNIQUE_PROTEINS);

		Logger.log("Redundant Protein Records From Genomic Id Set have been removed.", Logger.INFO);

		//Print Summary table counts
		Logger.log("Table Counts after third step :", Logger.INFO);
		SummaryDatabaseUtil.printTableCounts("GENOMIC_IDENTIFIER_SET_ONT_4_U,PROTEIN_U");
	}

	/**
	 * Step 5 : Fourth Iteration of GENOMIC-IDENTIFIER-SET-ONT to populate unique SET , mapping tables for G-M-P and ONT.
	 * Input :
	 *     --------		--------	-----------	
	 *     | Gene |		| mRNA |	| Protein |	
	 *     --------		--------	-----------	
	 *     ----------------------------------------------
	 *     | GeneId  |   mRNA Id  | ProteinId |  OntID  | 
	 *     ----------------------------------------------
	 * Output :
	 *     --------		--------	-----------	
	 *     | Gene |		| mRNA |	| Protein |	
	 *     --------		--------	-----------	
	 *     -------------	----------------	----------------	
	 *     | Gene_Mrna |	| mRNA_Protein |	| Protein_Gene |	
	 *     -------------	----------------	----------------	
	 *     -------------------------------------------	--------------------
	 *     | SetId  | GeneId  |  mRNA Id | ProteinId |	|  SetId  | OntID  | 
	 *     -------------------------------------------	--------------------
	 * 
	 * Steps followed :
	 * 1. To obtain the output , I/p table is sorted in the order GeneId, mRNA Id, ProteinId and OntID. 
	 * 2. Iterate over records in sorted result. 
	 * 3. Compare every set record with its previous record , to find out whether it is redundant or not. 
	 * 4. Write all unique genomic set, mapping record for Gene, mrna and protein (G-M, M-P and P-G tables 
	 * with redundant records) and all set-ont mapping records to the file and upload it to database.
	 */
	private void removeRedundantSetRecordsFromGenomicIdSet()
	{
		Logger.log("Removing Redundant Records From Genomic Id Set", Logger.DEBUG);

		SummaryDatabaseUtil.connectToDB();
		summaryTableWriter.openOutputDataFiles(STEP_5_FOR_UNIQUE_SET_ONT);
		summaryTableWriter.writeOutputDataFileHeaders(STEP_5_FOR_UNIQUE_SET_ONT);
		SummaryDatabaseUtil.createAndRebuildIndexes(STEP_5_FOR_UNIQUE_SET_ONT); // to get better performance for order by query 

		String query = SummaryDatabaseUtil.prepareQuery(STEP_5_FOR_UNIQUE_SET_ONT); //form the query
		ResultSet resultSet = dbManager.executeSQLQuery(query);

		opGenomicIdentifierSetFileRecordCount = 1;
		opGene_MrnaFileRecordCount = 1;
		opMrna_ProteinFileRecordCount = 1;
		opProtein_GeneFileRecordCount = 1;

		long prevGeneId = -1, prevMrnaId = -1, prevProteinId = -1;
		long currentGeneId = 0, currentMrnaId = 0, currentProteinId = 0;
		long currentOntId = 0;

		try
		{
			while (resultSet.next())
			{
				currentGeneId = resultSet.getLong(1);
				currentMrnaId = resultSet.getLong(2);
				currentProteinId = resultSet.getLong(3);
				currentOntId = resultSet.getLong(4);

				//Compare the current set record with the previous one in the sorted list to obtain distinct records
				if (currentGeneId != prevGeneId || currentMrnaId != prevMrnaId
						|| currentProteinId != prevProteinId)
				{
					if (currentGeneId != 0 && currentMrnaId != 0) //write gene-mrna mapping
						summaryTableWriter.writeGeneMrnaRecordToFile(opGene_MrnaFileRecordCount,
								currentGeneId, currentMrnaId);

					if (currentMrnaId != 0 && currentProteinId != 0) //write mrna-protein mapping
						summaryTableWriter.writeMrnaProteinRecordToFile(
								opMrna_ProteinFileRecordCount, currentMrnaId, currentProteinId);

					if (currentProteinId != 0 && currentGeneId != 0) //write protein-gene mapping
						summaryTableWriter.writeProteinGeneRecordToFile(
								opProtein_GeneFileRecordCount, currentProteinId, currentGeneId);

					summaryTableWriter.writeGenomicIdentifierSetToFile(
							opGenomicIdentifierSetFileRecordCount, currentGeneId, currentMrnaId,
							currentProteinId); // write genomic set record
					summaryTableWriter.writeSetOntMapping(opGenomicIdentifierSetFileRecordCount,
							currentOntId); //write set-ont record

					opGenomicIdentifierSetFileRecordCount++;
					opGene_MrnaFileRecordCount++;
					opMrna_ProteinFileRecordCount++;
					opProtein_GeneFileRecordCount++;

					prevGeneId = currentGeneId;
					prevMrnaId = currentMrnaId;
					prevProteinId = currentProteinId;
				}
				else
				{
					//if the current genomic set record is same as previous , write set-ont mapping only
					summaryTableWriter.writeSetOntMapping(
							opGenomicIdentifierSetFileRecordCount - 1, currentOntId);
				}
			}
			resultSet.close();
			resultSet.getStatement().close();
		}
		catch (SQLException e)
		{
			Logger.log("SQLException occured while removing Redundant Genomic "
					+ "Identifier Set records.", Logger.FATAL);
			SummaryExceptionHandler.handleException(e);
		}
		Logger.log("Total Count of Genomic Identifier Set Records : "
				+ (opGenomicIdentifierSetFileRecordCount - 1), Logger.INFO);
		SummaryDatabaseUtil.cleanUpOlderdata(STEP_5_FOR_UNIQUE_SET_ONT);
		summaryTableWriter.uploadOutputDataFiles(STEP_5_FOR_UNIQUE_SET_ONT);
		summaryTableWriter.closeOutputDataFiles(STEP_5_FOR_UNIQUE_SET_ONT);
		Logger.log("Redundant Records From Genomic Id Set have been removed.", Logger.DEBUG);

		//Print Summary table counts
		Logger.log("Table Counts after fifth step :", Logger.INFO);
		SummaryDatabaseUtil
				.printTableCounts("GENE_MRNA_U,MRNA_PROTEIN_U,PROTEIN_GENE_U,GENOMIC_IDENTIFIER_SET_U,SET_ONT_U");
	}

	/**
	 * Returns the list of genomic values from the given result set record(for particular set of columns only). 
	 * @param resultSet Result set from which values needs to be extracted
	 * @param startingColumnIndex Index of column in the result set from which required genomic values begins 
	 * @param columnsCount Index of column in the result set at which required genomic values ends 
	 * @return List of genomic values from the given result set,
	 * 		 null if no value is present for the asked genomic columns
	 */
	private List getObjectValues(ResultSet resultSet, int startingColumnIndex, int columnsCount)
	{
		List currentValues = new ArrayList();
		boolean atleastOneValuePresent = false;
		try
		{
			for (int columnIndex = startingColumnIndex; columnIndex <= columnsCount; columnIndex++)
			{
				if (resultSet.getObject(columnIndex) != null)
					atleastOneValuePresent = true;
				currentValues.add(resultSet.getObject(columnIndex));
			}
		}
		catch (SQLException e)
		{
			Logger.log("SQLException occured while fetching the value from "
					+ "Genomic Identifier result set.", Logger.FATAL);
			SummaryExceptionHandler.handleException(e);
		}
		if (!atleastOneValuePresent)
			currentValues = null;
		return currentValues;
	}

	/**
	 * Compares lists of values. 
	 * @param sourceValues Source list 
	 * @param targetValues Target list 
	 * @return true if values from both the list are equal, false otherwise.
	 */
	private boolean equalValues(List sourceValues, List targetValues)
	{
		if (targetValues == null)
			return false;
		if (sourceValues.size() != targetValues.size())
			return false;
		for (int i = 0; i < sourceValues.size(); i++)
		{
			if (sourceValues.get(i) == null && targetValues.get(i) == null)
				continue;
			if (sourceValues.get(i) == null || targetValues.get(i) == null)
				return false;
			if (!sourceValues.get(i).equals(targetValues.get(i)))
				return false;
		}
		return true;
	}

	/**
	 * Prints record count of all Summary tables.
	 * Invoked after starting summary table calculation to print count of all Summary tables.
	 */
	private void printSummaryTableCounts()
	{
		String summaryTableNames = "GENE,MRNA,PROTEIN,GENE_MRNA,MRNA_PROTEIN,PROTEIN_GENE,GENOMIC_IDENTIFIER_SET,SET_ONT";
		Logger.log("\nRecord Counts: ", Logger.DEBUG);
		Logger.log("GENE 				 : " + opGeneFileRecordCount, Logger.DEBUG);
		Logger.log("MRNA				 : " + opMrnaFileRecordCount, Logger.DEBUG);
		Logger.log("PROTEIN 			 : " + opProteinFileRecordCount, Logger.DEBUG);

		Logger.log("GENE_MRNA 			 : " + opGene_MrnaFileRecordCount, Logger.DEBUG);
		Logger.log("MRNA_PROTEIN 	 	 : " + opMrna_ProteinFileRecordCount, Logger.DEBUG);
		Logger.log("PROTEIN_GENE 	 	 : " + opProtein_GeneFileRecordCount, Logger.DEBUG);

		Logger.log("GENOMIC IDENTIFIER SET  : " + opGenomicIdentifierSetFileRecordCount,
				Logger.DEBUG);

		Logger.log("\nSUMMARY TABLE COUNTS : ", Logger.DEBUG);
		SummaryDatabaseUtil.printTableCounts(summaryTableNames);
		return;
	}

	
}