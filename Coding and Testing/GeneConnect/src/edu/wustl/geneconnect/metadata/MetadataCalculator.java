/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>ClassName: java edu.wustl.geneconnect.metadata.MetadataCalculator</p> 
 */

package edu.wustl.geneconnect.metadata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
import edu.wustl.geneconnect.metadata.domain.DataSource;

/**
 * This class will calculate all required geneconnect metadata from the GC graph file.
 * At the end , it will invoke MetadataWriter to write all the calculated metadata into the database.
 * 
 * It generates following output metadata files :
 *  		FileName 				Description <BR>
 * 1. DataSources.txt			Data Source Infomation
 * 2. DataSourceLinks.txt		Link types amongst datasouces
 * 3. Paths.txt					All possible paths
 * 4. SubPaths.txt				Path to subPath mapping.
 * 5. ONT-Ref.txt				Order Of Node Traversal (Paths considering link types)
 * 6. ONTs.txt					Order Of Node Traversal 
 * 7. PathToOntMappings.txt		Path To Ont Mappings 	
 * 
 * MetadataCalculator will be invoked by ant script as part of GC deployment(since metadata calculation is 
 * deployment time activity). 
 * 
 * @author arun_v, mahesh_nalkande
 * @version 1.0
 */
public class MetadataCalculator implements GeneConnectServerConstants
{

	private BufferedReader m_gcGraphFileReader;

	// This parameter will be read from the input config file
	private int[][] m_adjacencyMatrix;

	//  
	private int[][] m_statusMatrix;

	/**
	 * Total no. of Data sources
	 */
	private int m_numSources;

	/**
	 * Map to store DataSource info
	 * Key - Data Source ID
	 * Value - DataSource object 
	 */
	private Map m_srcNameIDPair = new HashMap();

	/**
	 * Contains multimaps, one each for a source node
	 */
	private List m_masterPathList = new ArrayList();

	/**
	 *  Used to keep track of which nodes have been (directly or indirectly) connected to the source node so far
	 */
	private List m_masterNodeList = new ArrayList();

	/**
	 * Contains mapping of each path to all sub-paths contained within the path
	 * Key is path index and value is a list of sub-path indices
	 * All paths are represented by unique path index
	 */
	private Map m_pathToSubPathMap = new HashMap();

	/**
	 * m_linkTypeMap is a map of maps below, which is used to keep track of link-types between a source and destination
	 * Key is source ID and value is a map of destination IDs and associated link types with each destination (stored as a list) 
	 */
	private Map m_linkTypeMap = new HashMap();

	
	static int PLAIN_FORMAT = 1;
	static int DB_FORMAT = 2;

	private int ontCount = 1;

	/**
	 * Main method
	 * @param args
	 */
	public static void main(String[] args)
	{
		MetadataCalculator metadataCalculator = new MetadataCalculator();
		metadataCalculator.calculateMetaData();
	}
	
	private void calculateMetaData()
	{
		try
		{
			readGraph();
		}
		catch (IOException ioex)
		{
			// throw exception object from main method?
		}

		initMasterPathList();
		initMasterNodeList();

		getDirectLinkages();

		calculateConnections(m_numSources);

		getSubPaths();

		// write to file for sqlloader to load
		writeDataSourceInformation("DataSources.txt");
		writeLinkTypes("DataSourceLinks.txt");
		writePaths("Paths.txt", DB_FORMAT);
		writeSubPaths("SubPaths-Ref.txt", "SubPaths.txt");
		writeONTs("ONT-Ref.txt", "ONTs.txt", "PathToOntMappings.txt");

		// apply filter such that only non-redundant paths appear in the list
		filterPaths();

		writePaths("NonRedundantLongestPaths.txt", PLAIN_FORMAT);

		uploadOutputDataFiles();
		
		rebuildMetaDataIndexes();
	}

	/**
	 * Method to read the file that represents the GeneConnect graph
	 * This will create :
	 * 1. m_srcNameIDPair
	 * 2. m_adjacencyMatrix
	 */
	private void readGraph() throws IOException, FileNotFoundException
	{
		m_gcGraphFileReader = new BufferedReader(new FileReader("./Config/GCGraph.txt"));

		// read all comma-separated data source names from the first line
		String line = m_gcGraphFileReader.readLine();
		String[] dataSourceNames = line.split(",\\s");

		//read all comma-separated Genomic Identifier Class names from the second line
		line = m_gcGraphFileReader.readLine();
		String[] dataSourceGenomicClassNames = line.split(",\\s");

		//read all comma-separated Class And Attribute names from the third line
		line = m_gcGraphFileReader.readLine();
		String[] dataSourceClassAndAttributeNames = line.split(",\\s");

		//read all comma-separated Table And Column names from the fourth line
		line = m_gcGraphFileReader.readLine();
		String[] dataSourceTableAndColumnNames = line.split(",\\s");

		//read all comma-separated row no.s
		line = m_gcGraphFileReader.readLine();
		String[] dataSourceRowNum = line.split(",\\s");

		//read all comma-separated column no.s
		line = m_gcGraphFileReader.readLine();
		String[] dataSourceColumnNum = line.split(",\\s");

		//		read all comma-separated Output Attribute Names
		line = m_gcGraphFileReader.readLine();
		String[] dataSourceOutputAttribute = line.split(",\\s");

		// Store it in a map of integer indices vs data source objects
		for (m_numSources = 0; m_numSources < dataSourceNames.length; ++m_numSources)
		{
			String[] dataSourceClassDetails = dataSourceClassAndAttributeNames[m_numSources]
					.split(DATASOURCE_CLASS_DETAILS_DELIMITER);
			String[] dataSourceTableDetails = dataSourceTableAndColumnNames[m_numSources]
					.split(DATASOURCE_TABLE_DETAILS_DELIMITER);

			m_srcNameIDPair.put((new Long(m_numSources)), new DataSource((new Long(m_numSources)),
					dataSourceNames[m_numSources], dataSourceGenomicClassNames[m_numSources],
					dataSourceClassDetails[1], dataSourceClassDetails[2],
					dataSourceClassDetails[0], dataSourceTableDetails[0],
					dataSourceTableDetails[1], dataSourceOutputAttribute[m_numSources], Integer
							.parseInt(dataSourceRowNum[m_numSources]), Integer
							.parseInt(dataSourceColumnNum[m_numSources])));
		}

		// allocate memory + initialize with 0's the adjacency matrix
		m_adjacencyMatrix = new int[m_numSources][m_numSources];
		// allocate memory + initialize m_statusMatrix
		m_statusMatrix = new int[m_numSources][m_numSources];

		for (int row = 0; row < m_numSources; ++row)
		{
			for (int column = 0; column < m_numSources; ++column)
			{
				m_adjacencyMatrix[row][column] = 0;
				m_statusMatrix[row][column] = 0;
			}
		}

		// read the rest of the lines of data which show links between a pair of data sources
		// Each row denotes a Data source. Assign each such data source it a row ID. 
		int rowID = 0;
		String dataSourceIds[];

		while ((line = m_gcGraphFileReader.readLine()) != null)
		{
			dataSourceIds = line.split(",\\s");
			for (int index = 0; index < dataSourceIds.length; ++index)
			{
				int linkTypeID = Integer.parseInt(dataSourceIds[index]);

				if (linkTypeID == 0)
				{
					addLinkType(rowID, index, linkTypeID);
				}
				else if (linkTypeID != -1)
				{
					if ((linkTypeID & DIRECT_LINK) == DIRECT_LINK)
					{
						addLinkType(rowID, index, DIRECT_LINK);
					}
					if ((linkTypeID & INFERRED_LINK) == INFERRED_LINK)
					{
						addLinkType(rowID, index, INFERRED_LINK);
					}
					if ((linkTypeID & IDENTITY_LINK) == IDENTITY_LINK)
					{
						addLinkType(rowID, index, IDENTITY_LINK);
					}
					if ((linkTypeID & ALIGNMENT_LINK) == ALIGNMENT_LINK)
					{
						addLinkType(rowID, index, ALIGNMENT_LINK);
					}
				}

				// separately update the adjacency matrix
				if (linkTypeID == 0)
				{
					m_adjacencyMatrix[rowID][index] = 0;
				}
				else if (linkTypeID == -1)
				{
					m_adjacencyMatrix[rowID][index] = -1;
				}
				else
				{
					m_adjacencyMatrix[rowID][index] = 1;
				}
			}
			++rowID;
		}
	}

	/**
	 * Method to add a link type for a source-destination pair 
	 */
	private void addLinkType(int srcNodeID, int destNodeID, int linkTypeID)
	{
		if (linkTypeID != -1)
		{
			// if not added, add destination node and link type to src node
			Map innerMap = (Map) m_linkTypeMap.get(new Integer(srcNodeID));
			if (innerMap == null)
			{
				Map newDestLkType = new HashMap();
				List linkTypes = new ArrayList();
				linkTypes.add(new Integer(linkTypeID));
				newDestLkType.put(new Integer(destNodeID), linkTypes);
				// Add this to the master map
				m_linkTypeMap.put(new Integer(srcNodeID), newDestLkType);
			}
			else
			{
				List linkTypes = (List) innerMap.get(new Integer(destNodeID));
				if (linkTypes == null)
				{
					linkTypes = new ArrayList();
					innerMap.put(new Integer(destNodeID), linkTypes);
				}
				linkTypes.add(new Integer(linkTypeID));
			}
		}
		return;
	}

	/**
	 * Method to gather all direct linkages
	 */
	private void getDirectLinkages()
	{
		for (int i = 0; i < m_numSources; ++i)
		{
			for (int j = 0; j < m_numSources; ++j)
			{
				// direct link exists
				// update list for source node
				if (m_adjacencyMatrix[i][j] == 1)
				{
					List nodeList = getListForSrcNode(i);
					if (!nodeList.contains(new Integer(j)))
					{
						nodeList.add(new Integer(j));
					}
					// Update this info in the map
					updateDirectPath(i, j);
				}
			}
		}
	}

	/**
	 * Method to calculate all possible paths between source and every other node
	 * @param numSources
	 */
	private void calculateConnections(int numSources)
	{
		for (int i = 0; i < numSources; ++i)
		{
			for (int j = 0; j < numSources; ++j)
			{
				if (0 == m_statusMatrix[i][j])
				{
					if (m_adjacencyMatrix[i][j] == 1)
					{
						List srcNodeList = new ArrayList();
						srcNodeList.add(new Integer(i));
						getPath(srcNodeList, j);
					}
					else if (m_adjacencyMatrix[i][j] == 0)
					{
						// call recursive function to calculate the paths  
						// Put all source nodes in a list
						List srcNodeList = new ArrayList();
						srcNodeList.add(new Integer(i));
						getPath(srcNodeList, j);
					}

					// Update status matrix so that we dont come back to this node once again
					m_statusMatrix[i][j] = 1;
				}
			}
		}
	}

	/**
	 * Recursive method to get all possible paths from srcNodeID to destNodeID, traversing backwards from colID
	 * @param srcNodeID Source data source Id
	 * @param destNodeID Destination datasource Id
	 */
	private void getPath(List srcNodeIDList, int destNodeID)
	{
		// latest source node ID is at the head of the list
		int srcNodeID = ((Integer) (srcNodeIDList.get(0))).intValue();

		// Go down the row for the given Node ID and get the direct entries
		for (int rowNum = 0; rowNum < m_numSources; ++rowNum)
		{
			if (m_adjacencyMatrix[rowNum][destNodeID] == 1)
			{
				// Check if this node has been (directly or indirectly) linked to the source node 
				if (getListForSrcNode(srcNodeID).contains(new Integer(rowNum)))
				{
					// Get path from 'scrNodeID' to 'rowNum' and append 'colID' to the path			
					// Create new path entry for 'colID' in the map by putting the above path
					updatePath(srcNodeID, destNodeID, rowNum);

					// Add entry to list if it does not exist
					if (!getListForSrcNode(srcNodeID).contains(new Integer(destNodeID)))
					{
						if (srcNodeID != destNodeID)
						{
							getListForSrcNode(srcNodeID).add(new Integer(destNodeID));
						}
					}
				}
				else
				{
					// Nothing to be done
				}
			}
			else
			// get the path for the unconnected nodes
			{
				// check for loops by making sure same node has not already occurred
				if (!srcNodeIDList.contains(new Integer(rowNum)))
				{
					srcNodeIDList.add(new Integer(rowNum));
					getPath(srcNodeIDList, rowNum);

					updatePath(rowNum, destNodeID, destNodeID);

					// Add entry to vector if it does not exist
					if (!getListForSrcNode(srcNodeID).contains(new Integer(destNodeID)))
					{
						if (srcNodeID != destNodeID)
						{
							getListForSrcNode(srcNodeID).add(new Integer(destNodeID));
						}
					}
				}
			}
		}
		return;
	}

	/**
	 * To store direct paths 
	 * @param fromNodeID
	 * @param toNodeID
	 */
	private void updateDirectPath(int fromNodeID, int toNodeID)
	{
		// fill the list with node Ids 
		List nodeList = new ArrayList();
		nodeList.add(new Integer(toNodeID));

		// store it in the map
		MultiMap pathMap = getPathsForSrc(fromNodeID);
		pathMap.put(new Integer(toNodeID), nodeList);
	}

	/**
	 * Add to multi map
	 * Key is the source node IDs
	 * @param fromNodeID
	 * @param toNodeID
	 * @param usingNodeID
	 */
	private void updatePath(int fromNodeID, int toNodeID, int usingNodeID)
	{
		if (fromNodeID == toNodeID)
		{
			return;
		}

		// Get all paths from 'fromNodeID'
		MultiMap pathMap = getPathsForSrc(fromNodeID);

		if (pathMap.containsKey(new Integer(usingNodeID)))
		{
			// Get all possible paths from 'fromNodeID' to 'usingNodeID'  
			Collection coll = (Collection) pathMap.get(new Integer(usingNodeID));

			if (null != coll)
			{
				for (Iterator iter = coll.iterator(); iter.hasNext();)
				{
					List l1 = (List) iter.next();
					List newNodeList = new ArrayList();

					// Check for cyclicity (i.e.) the path should NOT contain 'toNodeID'
					if (!l1.contains(new Integer(toNodeID)))
					{
						// Append each path to the 'toNodeID'

						for (Iterator iter1 = l1.iterator(); iter1.hasNext();)
						{
							newNodeList.add(iter1.next());
						}

						// now add 'toNodeID' as the last element
						newNodeList.add(new Integer(toNodeID));

						// Add this entry to the map after checking that it is not already present
						if (!isPathPresent(fromNodeID, toNodeID, newNodeList))
						{
							pathMap.put(new Integer(toNodeID), newNodeList);
						}
					}
				}
			}
		}
		else
		{
			// This should ideally never happen
		}
	}

	/**
	 * Initialize the master list of paths 
	 * This list contains 1 multimap for each source node
	 * Each entry in the multimap consists of one or more paths to destination nodes   
	 */
	private void initMasterPathList()
	{
		for (int i = 0; i < m_numSources; ++i)
		{
			MultiMap pathMap = new MultiValueMap();
			m_masterPathList.add(pathMap);
		}
	}

	/**
	 * Initialization 
	 *
	 */
	private void initMasterNodeList()
	{
		for (int i = 0; i < m_numSources; ++i)
		{
			List nodeList = new ArrayList();
			m_masterNodeList.add(nodeList);
		}
	}

	/**
	 * Returns the multimap for the given source node 
	 * @param srcNodeID
	 * @return
	 */
	private MultiMap getPathsForSrc(int srcNodeID)
	{
		return (MultiMap) m_masterPathList.get(srcNodeID);
	}

	/**
	 * Returns the List for the given source node
	 * @param srcNodeID
	 * @return
	 */
	private List getListForSrcNode(int srcNodeID)
	{
		return (List) m_masterNodeList.get(srcNodeID);
	}

	/**
	 * This method returns all linktypes between source and destination ID
	 */
	private List getLinkTypes(int srcID, int destID)
	{
		Map innerMap = (Map) m_linkTypeMap.get(new Integer(srcID));
		List linkTypes = (List) innerMap.get(new Integer(destID));

		return linkTypes;
	}

	/**
	 * Writes the data source vs integer index mapping into a file
	 */
	private void writeDataSourceInformation(String fileName)
	{
		try
		{
			FileWriter opFile = new FileWriter(new File(fileName));
			Iterator mapIter = m_srcNameIDPair.entrySet().iterator();
			opFile
					.write("LOAD DATA INFILE * APPEND INTO TABLE DATASOURCE FIELDS TERMINATED BY '"
							+ FIELD_DELIMITER
							+ "' "
							+ "(DATASOURCE_ID, DATASOURCE_NAME, GENOMIC_IDENTIFIER_CLASS, CLASS, ATTRIBUTE, "
							+ "ATTRIBUTE_TYPE, OUTPUT_ATTRIBUTE, TABLE_NAME, COLUMN_NAME, ROW_FOR_GRAPH,"
							+ " COL_FOR_GRAPH)\n" + "BEGINDATA\n");
			for (Iterator iter = m_srcNameIDPair.entrySet().iterator(); iter.hasNext();)
			{
				Map.Entry entry = (Map.Entry) iter.next();
				String ID = entry.getKey().toString();
				DataSource dataSource = (DataSource) entry.getValue();
				String temp = ID + FIELD_DELIMITER + dataSource.getName() + FIELD_DELIMITER
						+ dataSource.getGenomicIdentifierClassName() + FIELD_DELIMITER
						+ dataSource.getClassName() + FIELD_DELIMITER
						+ dataSource.getAttributeName() + FIELD_DELIMITER
						+ dataSource.getAttributeType() + FIELD_DELIMITER + dataSource.getOutputAttribute()
						+ FIELD_DELIMITER + dataSource.getTableName() + FIELD_DELIMITER + dataSource.getColumnName()
						+ FIELD_DELIMITER + dataSource.getRowNum() + FIELD_DELIMITER + dataSource.getColumnNum() + "\n";
				opFile.write(temp);
				opFile.flush();
			}

			opFile.close();
		}
		catch (IOException e)
		{
			System.out.println("Failed to create an output file in current directory"
					+ e.getMessage());
		}
	}

	/**
	 * Writes "source, destination, linktype" information fo file
	 */
	private void writeLinkTypes(String fileName)
	{
		// Link types between data source 
		try
		{
			FileWriter opFile = new FileWriter(new File(fileName));
			opFile
					.write("LOAD DATA INFILE * APPEND INTO TABLE DATASOURCE_LINKS FIELDS TERMINATED BY '"
							+ FIELD_DELIMITER
							+ "' "
							+ " (SOURCE_DATASOURCE_ID, TARGET_DATASOURCE_ID, LINK_TYPE_ID)\n"
							+ "BEGINDATA\n");

			for (int srcNodeID = 0; srcNodeID < m_numSources; ++srcNodeID)
			{
				Map destMap = (Map) m_linkTypeMap.get(new Integer(srcNodeID));
				Iterator mapIter = destMap.entrySet().iterator();
				for (Iterator iter = destMap.entrySet().iterator(); iter.hasNext();)
				{
					Map.Entry entry = (Map.Entry) iter.next();
					String destID = new Integer(((Integer) (entry.getKey())).intValue()).toString();
					List linkTypeList = (List) entry.getValue();
					int size = linkTypeList.size();
					for (int k = 0; k < size; ++k)
					{
						Integer link = (Integer) linkTypeList.get(k);
						// write to file only if some kind of link exists between the 2 data sources
						if ((link.compareTo(new Integer(0))) != 0)
						{
							String linkType = link.toString();
							String temp = (new Integer(srcNodeID).toString()) + FIELD_DELIMITER + destID
									+ FIELD_DELIMITER + linkType + "\n";
							opFile.write(temp);
							opFile.flush();
						}
					}
				}
			}
			opFile.close();
		}
		catch (IOException e)
		{
			System.out.println("Failed to create an output file in current directory"
					+ e.getMessage());
		}
	}

	/**
	 * Create following output files which will use sqlloader to populate database tables
	 * O/p file for all paths -- populates PATH table
	 */
	private void writePaths(String fileName, int fileFormat)
	{
		// All paths 
		// This portion fills the following tables:
		// a) ALL_PATH 

		int loopCount = 1;

		try
		{
			FileWriter opFile3 = new FileWriter(new File(fileName));
			if (fileFormat == DB_FORMAT)
			{
				opFile3.write("LOAD DATA INFILE * APPEND INTO TABLE PATH FIELDS TERMINATED BY '"
						+ FIELD_DELIMITER + "' "
						+ "(PATH_ID, SOURCE_DATASOURCE_ID, PATH, TARGET_DATASOURCE_ID)\n"
						+ "BEGINDATA\n");
			}

			for (int srcID = 0; srcID < m_numSources; ++srcID)
			{
				// Get all paths for 'srcID' 
				MultiMap nodePaths = (MultiMap) m_masterPathList.get(srcID);

				Iterator mapIter = nodePaths.entrySet().iterator();

				// This 'for' loop denotes all paths from a given srcID to all possible destinations
				for (Iterator iter = nodePaths.entrySet().iterator(); iter.hasNext();)
				{
					Map.Entry entry = (Map.Entry) iter.next();
					int destID = ((Integer) (entry.getKey())).intValue();

					List nodeList = (List) entry.getValue();

					// below 'for' loop denotes all paths to a given destination with 'srcID' as starting node
					for (int i = 0; i < nodeList.size(); ++i)
					{
						StringBuffer temp = new StringBuffer();
						if (fileFormat == DB_FORMAT)
						{
							// Write Record count in the o/p 
							temp.append((new Integer(loopCount)).toString() + FIELD_DELIMITER);

							//Write source datasource node ID 
							temp.append(new Integer(srcID).toString() + FIELD_DELIMITER);
						}
						else
						{
							//Write source datasource node ID 
							temp.append(new Integer(srcID).toString() + PATH_NODES_DELIMITER);
						}

						// 'nodeIDList' contains a single path (of node IDs) excluding srcID 
						// ########################################################################
						List nodeIDList = (List) nodeList.get(i);

						for (int h = 0; h < nodeIDList.size() - 1; ++h)
						{
							//Write intermediate datasource node ID 
							temp.append(nodeIDList.get(h) + PATH_NODES_DELIMITER);
						}

						if (fileFormat == DB_FORMAT)
						{
							if (nodeIDList.size() > 1)
							{
								temp.deleteCharAt(temp.length() - 1);
								temp.append(FIELD_DELIMITER);
							}
							else
							{
								temp.append(FIELD_DELIMITER);
							}
						}

						// Write Destination datasource node ID 
						temp.append(destID);
						temp.append("\n");

						opFile3.write(temp.toString());
						opFile3.flush();

						++loopCount;
						// ########################################################################	
					} // end of path
				}
			}
		}
		catch (IOException ioex)
		{
			System.out.println("Failed to create output file in current directory"
					+ ioex.getMessage());
		}
	}

	/**
	 * Create following output files which will use sqlloader to populate database tables
	 */
	private void writeONTs(String ontFileName, String ontFileNameForDB,
			String pathOntMappingFileName)
	{
		// All paths 
		// This portion fills the following tables:
		// a) ONT (Linked-list like table)
		// b) PATH TO ONT mapping

		int loopCount = 1;

		try
		{
			FileWriter opOntFile = new FileWriter(new File(ontFileName));
			FileWriter opOntFileForDB = new FileWriter(new File(ontFileNameForDB));
			FileWriter opPathOntMappingForDB = new FileWriter(new File(pathOntMappingFileName));

			opOntFileForDB
					.write("LOAD DATA INFILE * APPEND INTO TABLE ONT FIELDS TERMINATED BY '"
							+ FIELD_DELIMITER
							+ "' "
							+ "(PATH_ID, SOURCE_DS_ID, LINKTYPE_ID, NEXT_PATH_ID, PREV_PATH_ID)\n"
							+ "BEGINDATA\n");

			opPathOntMappingForDB
					.write("LOAD DATA INFILE * APPEND INTO TABLE PATH_ONT FIELDS TERMINATED BY '"
							+ FIELD_DELIMITER
							+ "' "
							+ "(PATH_ID, ONT_ID)\n" + "BEGINDATA\n");

			for (int srcID = 0; srcID < m_numSources; ++srcID)
			{
				// Get all paths for 'srcID' 
				MultiMap nodePaths = (MultiMap) m_masterPathList.get(srcID);

				Iterator mapIter = nodePaths.entrySet().iterator();

				// This 'for' loop denotes all paths from a given srcID to all possible destinations
				for (Iterator iter = nodePaths.entrySet().iterator(); iter.hasNext();)
				{
					Map.Entry entry = (Map.Entry) iter.next();
					int destID = ((Integer) (entry.getKey())).intValue();

					List allPathsfromCurrentSourceToDest = (List) entry.getValue();
					int size = allPathsfromCurrentSourceToDest.size();

					// below 'for' loop denotes all paths to a given destination with 'srcID' as starting node
					for (int i = 0; i < size; ++i)
					{
						// 'nodeIDList' contains a single path (of node IDs) excluding srcID 
						// ########################################################################
						List currentPath = (List) allPathsfromCurrentSourceToDest.get(i);

						// List of lists to form ONTs
						// each list will contain the same src nodes but with different link types
						List currentCompletePath = new ArrayList();
						List linkCounts = new ArrayList();

						/* ############# Form complete path including source ############# */
						currentCompletePath.add(new Integer(srcID));
						int totalIntermediateNodes = currentPath.size();
						for (int h = 0; h < totalIntermediateNodes - 1; ++h)
						{
							Integer intermediateID = (Integer) currentPath.get(h);
							currentCompletePath.add(intermediateID);
						}
						// Add Final destination source as well into the list
						currentCompletePath.add((Integer) currentPath
								.get(totalIntermediateNodes - 1));
						/* ############# Form complete path including source ############# */

						int currentPathLength = currentCompletePath.size();

						// Get link types for every pair of <source & destination> 
						for (int h = 0; h < currentPathLength - 1; ++h)
						{
							// get the link types between src and dest
							List srcDestLinks = getLinkTypes(((Integer) currentCompletePath.get(h))
									.intValue(), ((Integer) currentCompletePath.get(h + 1))
									.intValue());
							linkCounts.add(new Integer(srcDestLinks.size()));
						}

						// Get the total number of paths such that different link types between same 
						// pair of adjacent nodes appear in different paths)
						// Calculate total number of paths
						int totalPaths = 1;
						for (int m = 0; m < linkCounts.size(); m++)
						{
							totalPaths = totalPaths * ((Integer) linkCounts.get(m)).intValue();
						}

						// Intialize link type array to hold all comabination of possible link types for the current path
						int[][] linkTypesForAllCombinationsOfCurrentPath = new int[totalPaths][];
						for (int t = 0; t < totalPaths; t++)
						{
							linkTypesForAllCombinationsOfCurrentPath[t] = new int[linkCounts.size()];
						}

						int groupCount = totalPaths;
						int mainGroupCnt = 1;
						int cnt = 0;

						// Loop though all the sources in the path
						for (int n = 0; n < currentPathLength - 1; n++)
						{
							cnt = 0;

							//number of times each link needs to be repeated with main group
							groupCount = groupCount / ((Integer) linkCounts.get(n)).intValue();

							// Fetch data-structure which holds linktype indexes
							// between source and destination
							List srcDestLinks = getLinkTypes(((Integer) currentCompletePath.get(n))
									.intValue(), ((Integer) currentCompletePath.get(n + 1))
									.intValue());
							// The main group for which links type combination has to be repeated
							for (int mainG = 0; mainG < mainGroupCnt; ++mainG)
							{
								for (int j = 0; j < ((Integer) linkCounts.get(n)).intValue(); ++j)
								{
									// Repeat Number of groups for every sub-group
									for (int k = 0; k < groupCount; ++k)
									{
										linkTypesForAllCombinationsOfCurrentPath[cnt][n] = ((Integer) (srcDestLinks
												.get(j))).intValue();
										++cnt;
									}
								}
							}
							mainGroupCnt = mainGroupCnt
									* (((Integer) linkCounts.get(n)).intValue());
						}

						StringBuffer sbOntFile = new StringBuffer();
						StringBuffer sbOntDb = new StringBuffer();
						StringBuffer sbPathOntMapping = new StringBuffer();

						writeOntForGivenPath(loopCount, currentCompletePath,
								linkTypesForAllCombinationsOfCurrentPath, sbOntFile, sbOntDb,
								sbPathOntMapping);

						opOntFile.write(sbOntFile.toString());
						opOntFile.flush();

						opOntFileForDB.write(sbOntDb.toString());
						opOntFileForDB.flush();

						opPathOntMappingForDB.write(sbPathOntMapping.toString());
						opPathOntMappingForDB.flush();

						loopCount++;
						// ########################################################################	
					} // end of path
				}
			}
		}
		catch (IOException ioex)
		{
			System.out.println("Failed to create output file in current directory"
					+ ioex.getMessage());
		}
	}

	/**
	 * Writes following info for the given path :
	 * 1. ONT
	 * 2. PATH TO ONT mapping
	 * @param pathId Path ID of the current path.
	 * @param currentCompletePath Actual path - List of nodes(node IDs)
	 * @param linkTypesForAllCombinationsOfCurrentPath - Array defining link types to be used for generating
	 * all possible combinations for the current path   
	 * @param sbOntFile String Buffer - to write ONT info (For referance)
	 * @param sbOntDb String Buffer - to write ONT info (which will be used by sql loader to upload into db)
	 * @param sbPathOntMapping String Buffer -  to write Path to ONT mapping info
	 */
	private void writeOntForGivenPath(int pathId, List currentCompletePath,
			int[][] linkTypesForAllCombinationsOfCurrentPath, StringBuffer sbOntFile,
			StringBuffer sbOntDb, StringBuffer sbPathOntMapping)
	{
		//1. Write ONT information into the file for referance
		for (int n = 0; n < linkTypesForAllCombinationsOfCurrentPath.length; n++)
		{
			sbOntFile.append(currentCompletePath.get(0) + "");
			for (int j = 0; j < linkTypesForAllCombinationsOfCurrentPath[0].length; j++)
			{
				sbOntFile.append("_<" + linkTypesForAllCombinationsOfCurrentPath[n][j] + ">_");
				sbOntFile.append(currentCompletePath.get(j + 1));
			}
			sbOntFile.append("\n");
		}

		//2. Write ONT information into the file which will be used by sql loader to upload into db
		//Format of record : PATH_ID, SOURCE_DS_ID, LINKTYPE_ID, NEXT_PATH_ID, PREV_PATH_ID
		for (int n = 0; n < linkTypesForAllCombinationsOfCurrentPath.length; n++)
		{
			//3. Write Path to ONT mapping
			sbPathOntMapping.append(pathId + FIELD_DELIMITER + ontCount + "\n");

			//first ONT record for the path
			sbOntDb.append((ontCount++) + FIELD_DELIMITER);
			sbOntDb.append(currentCompletePath.get(0) + FIELD_DELIMITER);
			sbOntDb.append(linkTypesForAllCombinationsOfCurrentPath[n][0] + FIELD_DELIMITER);
			sbOntDb.append(ontCount + FIELD_DELIMITER);
			sbOntDb.append(FIELD_DELIMITER + "\n"); //PREV_PATH_ID will be null for 1st ONT record

			//intermediates ONT records for the path
			for (int j = 1; j < linkTypesForAllCombinationsOfCurrentPath[0].length; j++)
			{
				sbOntDb.append((ontCount++) + FIELD_DELIMITER);
				sbOntDb.append(currentCompletePath.get(j) + FIELD_DELIMITER);
				sbOntDb.append(linkTypesForAllCombinationsOfCurrentPath[n][j] + FIELD_DELIMITER);
				sbOntDb.append(ontCount + FIELD_DELIMITER);
				sbOntDb.append(ontCount - 2 + "\n");
			}
			//Last ONT record for the path
			sbOntDb.append((ontCount++) + FIELD_DELIMITER);
			sbOntDb.append(currentCompletePath.get(currentCompletePath.size() - 1) + FIELD_DELIMITER);
			sbOntDb.append(FIELD_DELIMITER); //LINKTYPE_ID will be null for last ONT record
			sbOntDb.append(FIELD_DELIMITER); //NEXT_PATH_ID will be null for last ONT record
			sbOntDb.append(ontCount - 2 + "\n");
		}
	}

	/*
	 * Method to write all path-to-subPath mappings to a file
	 */
	private void writeSubPaths(String subPathFileName, String subPathFileNameForDB)
	{
		try
		{
			FileWriter opFile = new FileWriter(new File(subPathFileName));
			FileWriter opFileForDB = new FileWriter(new File(subPathFileNameForDB));

			opFileForDB
					.write("LOAD DATA INFILE * APPEND INTO TABLE SUBPATH FIELDS TERMINATED BY '"
							+ FIELD_DELIMITER
							+ "' "
							+ "(PATH_ID, SUBPATH_ID)\n" + "BEGINDATA\n");

			Iterator mapIter = m_pathToSubPathMap.entrySet().iterator();
			for (Iterator iter = m_pathToSubPathMap.entrySet().iterator(); iter.hasNext();)
			{
				Map.Entry entry = (Map.Entry) iter.next();
				String parentPathID = ((Integer) (entry.getKey())).toString();
				String subpathID;
				StringBuffer line = new StringBuffer();
				line.append(parentPathID + FIELD_DELIMITER);

				StringBuffer lineForDB = new StringBuffer();

				List subPaths = (List) entry.getValue();

				for (int i = 0; i < subPaths.size(); ++i)
				{
					subpathID = ((Integer) subPaths.get(i)).toString();
					line.append(subpathID + SUBPATH_IDS_DELIMITER);

					lineForDB.append(parentPathID + FIELD_DELIMITER + subpathID + "\n");
				}

				// remove the last '_'
				line.deleteCharAt(line.length() - 1);

				line.append("\n");

				opFile.write(line.toString());
				opFile.flush();

				opFileForDB.write(lineForDB.toString());
				opFileForDB.flush();
			}

			opFile.close();
		}
		catch (IOException e)
		{
			System.out.println("Failed to create an output file in current directory"
					+ e.getMessage());
		}
	}

	/*
	 * Method to find if given path already exists
	 */
	private boolean isPathPresent(int srcNodeID, int destNodeID, List newNodeList)
	{
		MultiMap pathMap = getPathsForSrc(srcNodeID);
		Collection coll = (Collection) pathMap.get(new Integer(destNodeID));
		if (null != coll)
		{
			for (Iterator iter = coll.iterator(); iter.hasNext();)
			{
				int count = 0;
				List l1 = (List) iter.next();
				if (l1.size() == newNodeList.size())
				{
					// if sizes are same, compare element by element
					int size = l1.size();
					for (int i = 0; i < size; ++i)
					{
						String test1 = (l1.get(i)).toString();
						String test2 = (newNodeList.get(i)).toString();

						if (l1.get(i).equals(newNodeList.get(i)))
						{
							++count;
						}
						else
						{
							break;
						}
					}

					if (count == size)
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	/*
	 * Method to get all sub-paths for every path
	 */
	private void getSubPaths()
	{
		int pathCount = 1;
		for (int srcID = 0; srcID < m_numSources; ++srcID)
		{
			// Get all paths for 'srcID' 
			MultiMap nodePaths = (MultiMap) m_masterPathList.get(srcID);

			Iterator mapIter = nodePaths.entrySet().iterator();

			// This 'for' loop denotes all paths from a given srcID to all possible destinations
			for (Iterator iter = nodePaths.entrySet().iterator(); iter.hasNext();)
			{
				Map.Entry entry = (Map.Entry) iter.next();
				int destID = ((Integer) (entry.getKey())).intValue();

				List nodeList = (List) entry.getValue();
				int size = nodeList.size();

				// below 'for' loop denotes all paths to a given destination with 'srcID' as starting node
				for (int i = 0; i < size; ++i)
				{
					// 'nodeIDList' contains a single path (of node IDs) excluding srcID 
					List nodeIDList = (List) nodeList.get(i);

					List pathList = new ArrayList();
					pathList.add(new Integer(srcID));
					for (int k = 0; k < nodeIDList.size(); k++)
					{
						pathList.add(nodeIDList.get(k));
					}

					calculateSubPaths(pathCount, pathList);
					++pathCount;
				}
			}
		}
	}

	/*
	 * 
	 */
	private void calculateSubPaths(int pathIndex, List pathList)
	{
		int subPathIndex = 1;
		for (int srcID = 0; srcID < m_numSources; ++srcID)
		{
			// get all paths for given source node
			MultiMap pathMap = getPathsForSrc(srcID);

			Iterator mapIter = pathMap.entrySet().iterator();

			for (Iterator iter = pathMap.entrySet().iterator(); iter.hasNext();)
			{
				Map.Entry entry = (Map.Entry) iter.next();
				List nodeList = (List) entry.getValue();

				int size = nodeList.size();

				for (int i = 0; i < nodeList.size(); ++i)
				{
					List IDlist = (List) nodeList.get(i);
					// form list to be compared against
					List nodesInPath = new ArrayList();
					nodesInPath.add(new Integer(srcID));

					for (int k = 0; k < IDlist.size(); ++k)
					{
						nodesInPath.add(IDlist.get(k));
					}

					// proceed with comparison only if length(pathList) > length(nodesInPath), since 'pathList' should contain 'nodesInPath' 
					if (pathList.size() >= nodesInPath.size())
					{
						int numMatches = 0;
						// check if 'nodesInPath' is contained in 'pathList' 

						// search for first occurrence of first element of 'nodesInPath' in 'pathList'  
						Integer first = (Integer) nodesInPath.get(0);
						int indx = pathList.indexOf(first);
						if (indx != -1)
						{
							int length = nodesInPath.size();

							// compare 'length' elements from 'indx' position in 'pathList' to 'nodesInPath'  
							for (int j = indx, k = 0; ((j < pathList.size()) && (k < length)); ++j, ++k)
							{
								if (pathList.get(j).equals(nodesInPath.get(k)))
								{
									++numMatches;
								}
								else
								// if not equal, can break out of loop immediately
								{
									break;
								}
							}
						}

						// delete if following is true
						// a) 'nodesInPath' is entirely contained in 'pathList'
						// b) 'nodesInPath' is not the same as 'pathList' (so that longest non-redundant paths are not compared with itself and deleted) 
						if ((numMatches == nodesInPath.size())
								&& (nodesInPath.size() != pathList.size()))
						{
							List subPathList = (List) m_pathToSubPathMap
									.get(new Integer(pathIndex));
							if (subPathList == null)
							{
								subPathList = new ArrayList();
								subPathList.add(new Integer(subPathIndex));
								m_pathToSubPathMap.put(new Integer(pathIndex), subPathList);
							}
							else
							{
								subPathList.add(new Integer(subPathIndex));
							}
						}
					}
					++subPathIndex;
				}
			}
		}

		return;
	}

	/*
	 * Method to reduce the number of paths such that all other paths are contained within these paths 
	 */
	private void filterPaths()
	{
		int srcCount = m_numSources;
		// get paths in decreasing order of path-length
		// Get all paths (irrespective of source/starting node) of size 'm_numSources', then 'm_numSources' - 1, then 'm_numSources' - 2 and so on until 'm_numSources' - 'm_numSources' + 1 
		while (srcCount > 0)
		{
			int pathLength = srcCount - 1;
			for (int srcID = m_numSources - 1; srcID >= 0; --srcID)
			{
				List pathList = getPathsWithSpecifiedLength(srcID, pathLength);

				// For each path, delete all other paths are contained within this path, from in-memory data structure
				for (int i = 0; i < pathList.size(); ++i)
				{
					List nodeList = (List) pathList.get(i);
					deleteRedundantPaths(nodeList);
				}
			}
			srcCount -= 1;
		}
	}

	/*
	 * Method to return paths with given length, for a given src node
	 */
	private List getPathsWithSpecifiedLength(int srcID, int pathLength)
	{
		List pathList = new ArrayList();
		// get all paths for given source node
		MultiMap pathMap = getPathsForSrc(srcID);

		Iterator mapIter = pathMap.entrySet().iterator();

		for (Iterator iter = pathMap.entrySet().iterator(); iter.hasNext();)
		{
			Map.Entry entry = (Map.Entry) iter.next();
			List nodeList = (List) entry.getValue();

			int size = nodeList.size();

			for (int i = 0; i < size; ++i)
			{
				List IDList = (List) nodeList.get(i);
				if (IDList.size() == pathLength)
				{
					// create a new list that includes the source ID
					List newList = new ArrayList();
					newList.add(new Integer(srcID));
					for (int k = 0; k < IDList.size(); ++k)
					{
						newList.add(IDList.get(k));
					}

					// add this to the list of all paths
					pathList.add(newList);
				}
			}
		}

		return pathList;
	}

	/*
	 * Method to delete redundant paths (i.e.) delete all paths that completely occur in other paths 
	 */
	private void deleteRedundantPaths(List pathList)
	{
		// for each path in in-memory structure, compare list with above list 
		// if exact match between the 2 or in-memory path contained in 'pathList' above, delete the in-memory path from in-memory data structure

		for (int srcID = 0; srcID < m_numSources; ++srcID)
		{
			// get all paths for given source node
			MultiMap pathMap = getPathsForSrc(srcID);

			Iterator mapIter = pathMap.entrySet().iterator();

			for (Iterator iter = pathMap.entrySet().iterator(); iter.hasNext();)
			{
				Map.Entry entry = (Map.Entry) iter.next();
				List nodeList = (List) entry.getValue();

				int size = nodeList.size();

				for (int i = 0; i < nodeList.size(); ++i)
				{
					List IDlist = (List) nodeList.get(i);
					// form list to be compared against
					List nodesInPath = new ArrayList();
					nodesInPath.add(new Integer(srcID));

					for (int k = 0; k < IDlist.size(); ++k)
					{
						nodesInPath.add(IDlist.get(k));
					}

					// proceed with comparison only if length(pathList) > length(nodesInPath), since 'pathList' should contain 'nodesInPath' 
					if (pathList.size() >= nodesInPath.size())
					{
						int numMatches = 0;
						// check if 'nodesInPath' is contained in 'pathList' 

						// search for first occurrence of first element of 'nodesInPath' in 'pathList'  
						Integer first = (Integer) nodesInPath.get(0);
						int indx = pathList.indexOf(first);
						if (indx != -1)
						{
							int length = nodesInPath.size();

							// compare 'length' elements from 'indx' position in 'pathList' to 'nodesInPath'  
							for (int j = indx, k = 0; ((j < pathList.size()) && (k < length)); ++j, ++k)
							{
								if (pathList.get(j).equals(nodesInPath.get(k)))
								{
									++numMatches;
								}
								else
								// if not equal, can break out of loop immediately
								{
									break;
								}
							}
						}

						// delete if following is true
						// a) 'nodesInPath' is entirely contained in 'pathList'
						// b) 'nodesInPath' is not the same as 'pathList' (so that longest non-redundant paths are not compared with itself and deleted) 
						if ((numMatches == nodesInPath.size())
								&& (nodesInPath.size() != pathList.size()))
						{
							// delete from original data structure
							nodeList.remove(i);
						}
					}
				}
			}
		}
		return;
	}

	/**
	 * Uploads all output metadata files into the database
	 */
	private void uploadOutputDataFiles()
	{
		//Invoke MetadataWriter to write all the calculated metadata into the database
		MetadataWriter metadataWriter = MetadataWriter.getInstance();

		//list of all output metadata files to upload into the database
		List filesToUpload = new ArrayList();
		filesToUpload.add("DataSources.txt");
		filesToUpload.add("DataSourceLinks.txt");
		filesToUpload.add("Paths.txt");
		filesToUpload.add("SubPaths.txt");
		filesToUpload.add("ONTs.txt");
		filesToUpload.add("PathToOntMappings.txt");

		//Drop referance constraints on ONT table, while uploading the data.
		List preUploadQueries = new ArrayList();
		preUploadQueries.add("ALTER TABLE ONT DROP CONSTRAINT FK_ONT_ONT");
		preUploadQueries.add("ALTER TABLE ONT DROP CONSTRAINT FK_ONT_PREV_ONT");

		//Restore ONT table reference constraints 
		List postUploadQueries = new ArrayList();
		postUploadQueries.add("ALTER TABLE ONT ADD CONSTRAINT FK_ONT_ONT "
				+ "FOREIGN KEY (NEXT_PATH_ID) REFERENCES ONT (PATH_ID)");
		postUploadQueries.add("ALTER TABLE ONT ADD CONSTRAINT FK_ONT_PREV_ONT "
				+ "FOREIGN KEY (PREV_PATH_ID) REFERENCES ONT (PATH_ID)");

		metadataWriter.uploadDataFilesIntoDataBase(filesToUpload, preUploadQueries,
				postUploadQueries);
	}
	
	private void rebuildMetaDataIndexes()
	{
		Logger.log("Rebuilding Indexes on metadata tables...", Logger.INFO);
		DBManager dBManager = DBManager.getInstance();
		connectToDB(dBManager);
		dBManager.executeScriptFile("." + FILE_SEPARATOR + SCRIPTS_FOLDER_NAME
				+ FILE_SEPARATOR + METADATA_INDEXES_REBUILD_SCRIPT_FILENAME);
		Logger.log("Finished rebuilding Indexes on set and ont tables.", Logger.INFO);
	}
	/**
	 * Establish data base connection using db properties specified in server.properties
	 */
	private void connectToDB(DBManager dBManager)
	{
		/** set up Data Base connection object*/
		try
		{
			Logger.log("Connecting to DB server...", Logger.DEBUG);
			dBManager.connect();
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

