/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.bizlogic.ONTBizLogic</p> 
 */

package edu.wustl.geneconnect.bizlogic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.dbManager.DAOException;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.geneconnect.metadata.MetadataManager;
import edu.wustl.geneconnect.util.global.GCConstants;

/**
 * ONT Business Logic
 * Implements method which call caCore APIs to execute query.
 * @author krunal_thakkar
 * @version 1.0
 */
public class ONTBizLogic implements BizLogicInterface
{

	ResultDataInterface resultData = null;

	Map selectedDataSources = null;

	private Map dataSourcesLinksMap;

	List resultDataList = null;

	/**
	 * Applies business logic on the passed input data and retunrs back the result.
	 * 
	 * @param inputData Data on which Business Logic will operate.
	 * @return ResultDataInterface Result data, which can be SuccessResultData or ValidationResultData.
	 * @throws BizLogicException
	 * @throws DAOException
	 * 
	 * @see edu.wustl.geneconnect.bizlogic.BizLogicInterface#getResult(InputDataInterface)
	 */
	public ResultDataInterface getResult(InputDataInterface inputData) throws BizLogicException,
			DAOException
	{
		//Initializing instance of ResultDataInterface
		resultData = new ResultData();

		//Initializing map of the links of Datasources
		dataSourcesLinksMap = MetadataManager.getDataSourcesLinksMap();

		//Initializing map of the data from the instance of InputDataInterface 
		Map data = inputData.getData();

		//Populating required attributes to filter valid paths from map of the data
		selectedDataSources = (HashMap) data.get(GCConstants.SELECTED_DATA_SOURCES);
		Integer ONTFilterCode = (Integer) data.get(GCConstants.ONT_FILTER_CODE);
		String pathType = (String) data.get(GCConstants.PATH_TYPE);
		String startsWithDataSource = (String) data.get(GCConstants.STARTS_WITH_DATA_SOURCE);
		String endsWtihDataSouce = (String) data.get(GCConstants.ENDS_WITH_DATA_SOURCE);

		//filtering paths for 'Path' filter criteria
		if (ONTFilterCode.intValue() == 1)
		{
			resultDataList = allPaths();
		}
		else if (ONTFilterCode.intValue() == 2)
		{
			resultDataList = subsetInputOutput();
		}
		else if (ONTFilterCode.intValue() == 3)
		{
			resultDataList = allInputSubsetOutput();
		}
		else if (ONTFilterCode.intValue() == 4)
		{
			resultDataList = allInputOutput();
		}
		else if (ONTFilterCode.intValue() == 5)
		{
			resultDataList = startsEndsWithInputOutput();
		}
		else if (ONTFilterCode.intValue() == 6)
		{
			resultDataList = startWithInputEndsWithOutput();
		}
		else if (ONTFilterCode.intValue() == 7)
		{
			resultDataList = onlyAllInputOutput();
		}
		else if (ONTFilterCode.intValue() == 8)
		{
			resultDataList = traverseInputThanOutput();
		}

		//Initializing data map to store result data
		Map resultDataMap = new HashMap();

		//putting filtered valid paths into data map of result
		resultDataMap.put(GCConstants.RESULT_DATA_LIST, filterPaths(ONTFilterCode.toString(),
				pathType, startsWithDataSource, endsWtihDataSouce));

		//setting map of result data into instance of ResultDataInterface
		resultData.setData(resultDataMap);

		//returning instance of ResultDataInterface with populated valid filterd paths
		return resultData;
	}

	/**
	 * 1
	 * All possible paths between data sources should be displayed
	 */
	private List allPaths()
	{
		List paths = new ArrayList();
		List validPaths = new ArrayList();

		paths = MetadataManager.getPaths();

		for (int i = 0; i < paths.size(); i++)
		{
			Map pathMap = (HashMap) paths.get(i);

			List pathDataSources = (ArrayList) pathMap.get(GCConstants.DATASOURCES_LIST);

			String path = "";
			for (int j = 0; j < pathDataSources.size(); j++)
			{
				path += (pathDataSources.get(j) + "-");
			}

			validPaths.add(path.substring(0, path.length() - 1));
		}

		return validPaths;
	}

	/**
	 * 2
	 * Paths having subset of Input and Output data sources available should be filtered.
	 */
	private List subsetInputOutput()
	{
		List paths = new ArrayList();
		List validPaths = new ArrayList();

		List inputDataSources = parseInputDataSourceList((HashMap) selectedDataSources
				.get(GCConstants.INPUT_DATA_SOURCES));

		List outputDataSources = parseOutputDataSourceList((HashMap) selectedDataSources
				.get(GCConstants.OUTPUT_DATA_SOURCES));

		paths = MetadataManager.getPaths();

		for (int i = 0; i < paths.size(); i++)
		{
			boolean subsetInput = false, subsetOutput = false;

			Map pathMap = (HashMap) paths.get(i);

			List pathDataSources = (ArrayList) pathMap.get(GCConstants.DATASOURCES_LIST);

			String path = "";
			for (int j = 0; j < pathDataSources.size(); j++)
			{
				if (inputDataSources.contains(pathDataSources.get(j)))
				{
					subsetInput = true;
				}

				if (outputDataSources.contains(pathDataSources.get(j)))
				{
					subsetOutput = true;
				}

				path += (pathDataSources.get(j) + "-");
			}

			if (subsetInput & subsetOutput)
				validPaths.add(path.substring(0, path.length() - 1));
		}

		return validPaths;
	}

	/**
	 * 3
	 * Paths having all the Input data sources and any of the Output data sources should be filtered
	 */
	private List allInputSubsetOutput()
	{
		List paths = new ArrayList();
		List validPaths = new ArrayList();

		List inputDataSources = parseInputDataSourceList((HashMap) selectedDataSources
				.get(GCConstants.INPUT_DATA_SOURCES));

		List outputDataSources = parseOutputDataSourceList((HashMap) selectedDataSources
				.get(GCConstants.OUTPUT_DATA_SOURCES));

		paths = MetadataManager.getPaths();

		for (int i = 0; i < paths.size(); i++)
		{
			boolean subsetOutput = false;

			Map pathMap = (HashMap) paths.get(i);

			List pathDataSources = (ArrayList) pathMap.get(GCConstants.DATASOURCES_LIST);

			if (pathDataSources.containsAll(inputDataSources))
			{
				String path = "";
				for (int j = 0; j < pathDataSources.size(); j++)
				{
					if (outputDataSources.contains(pathDataSources.get(j)))
					{
						subsetOutput = true;
					}

					path += (pathDataSources.get(j) + "-");
				}

				if (subsetOutput)
					validPaths.add(path.substring(0, path.length() - 1));
			}
		}

		return validPaths;
	}

	/**
	 * 4
	 * Paths having all inputs and outputs but may contain non-input/non-output sources at any node
	 */
	private List allInputOutput()
	{
		List paths = new ArrayList();
		List validPaths = new ArrayList();

		List inputDataSources = parseInputDataSourceList((HashMap) selectedDataSources
				.get(GCConstants.INPUT_DATA_SOURCES));

		List outputDataSources = parseOutputDataSourceList((HashMap) selectedDataSources
				.get(GCConstants.OUTPUT_DATA_SOURCES));

		paths = MetadataManager.getPaths();

		for (int i = 0; i < paths.size(); i++)
		{
			Map pathMap = (HashMap) paths.get(i);

			List pathDataSources = (ArrayList) pathMap.get(GCConstants.DATASOURCES_LIST);

			if (pathDataSources.containsAll(inputDataSources)
					& pathDataSources.containsAll(outputDataSources))
			{
				String path = "";
				for (int j = 0; j < pathDataSources.size(); j++)
				{
					path += (pathDataSources.get(j) + "-");
				}

				validPaths.add(path.substring(0, path.length() - 1));
			}
		}

		return validPaths;
	}

	/**
	 * 5
	 * Paths starting and ending with any Input/Output data sources selected on Advanced Search page
	 * and covering all Input and Output data sources selected on Advanced Search page should be filtered.
	 */
	private List startsEndsWithInputOutput()
	{
		List paths = new ArrayList();
		List validPaths = new ArrayList();

		List inputDataSources = parseInputDataSourceList((HashMap) selectedDataSources
				.get(GCConstants.INPUT_DATA_SOURCES));

		List outputDataSources = parseOutputDataSourceList((HashMap) selectedDataSources
				.get(GCConstants.OUTPUT_DATA_SOURCES));

		paths = MetadataManager.getPaths();

		for (int i = 0; i < paths.size(); i++)
		{
			Map pathMap = (HashMap) paths.get(i);

			if ((inputDataSources.contains((String) pathMap.get(GCConstants.SOURCE_DATASOURCE_ID)) || outputDataSources
					.contains((String) pathMap.get(GCConstants.SOURCE_DATASOURCE_ID)))
					& (inputDataSources.contains((String) pathMap
							.get(GCConstants.TARGET_DATASOURCE_ID)) || outputDataSources
							.contains((String) pathMap.get(GCConstants.TARGET_DATASOURCE_ID))))

			{
				List pathDataSources = (ArrayList) pathMap.get(GCConstants.DATASOURCES_LIST);

				//				Logger.out.debug("StartsWith-->"+(String)pathMap.get(GCConstants.SOURCE_DATASOURCE_ID)+" EndsWith-->"+(String)pathMap.get(GCConstants.TARGET_DATASOURCE_ID));
				if (pathDataSources.containsAll(inputDataSources)
						& pathDataSources.containsAll(outputDataSources))
				{
					String path = "";
					for (int j = 0; j < pathDataSources.size(); j++)
					{
						path += (pathDataSources.get(j) + "-");
					}

					//					Logger.out.debug("ValidPath-->"+path.substring(0, path.length()-1));
					validPaths.add(path.substring(0, path.length() - 1));
				}
			}
		}

		return validPaths;
	}

	/**
	 * 6
	 * Paths starting with any of the Input data sources and ending with any of the Output data sources 
	 * and covering all Input and Output data sources should be filtered. Path should not start with any 
	 * of the data source not available in the list of Input data sources selected on Advanced Search page 
	 * and should not end with any of the data source not available in the list of Output data sources selected 
	 * on Advanced Search page. 
	 */
	private List startWithInputEndsWithOutput()
	{
		List paths = new ArrayList();
		List validPaths = new ArrayList();

		List inputDataSources = parseInputDataSourceList((HashMap) selectedDataSources
				.get(GCConstants.INPUT_DATA_SOURCES));

		List outputDataSources = parseOutputDataSourceList((HashMap) selectedDataSources
				.get(GCConstants.OUTPUT_DATA_SOURCES));

		paths = MetadataManager.getPaths();

		for (int i = 0; i < paths.size(); i++)
		{
			Map pathMap = (HashMap) paths.get(i);

			if ((inputDataSources.contains((String) pathMap.get(GCConstants.SOURCE_DATASOURCE_ID)))
					& (outputDataSources.contains((String) pathMap
							.get(GCConstants.TARGET_DATASOURCE_ID))))
			{
				List pathDataSources = (ArrayList) pathMap.get(GCConstants.DATASOURCES_LIST);

				if (pathDataSources.containsAll(inputDataSources)
						& pathDataSources.containsAll(outputDataSources))
				{
					String path = "";
					for (int j = 0; j < pathDataSources.size(); j++)
					{
						path += (pathDataSources.get(j) + "-");
					}

					validPaths.add(path.substring(0, path.length() - 1));
				}
			}
		}

		return validPaths;
	}

	/**
	 * 7
	 * Paths having ONLY all Input and Output data sources available should be filtered 
	 * (i.e. no paths with non-input/non-output should be filtered).
	 */
	private List onlyAllInputOutput()
	{
		List paths = new ArrayList();
		List validPaths = new ArrayList();

		List inputDataSources = parseInputDataSourceList((HashMap) selectedDataSources
				.get(GCConstants.INPUT_DATA_SOURCES));

		List outputDataSources = parseOutputDataSourceList((HashMap) selectedDataSources
				.get(GCConstants.OUTPUT_DATA_SOURCES));

		paths = MetadataManager.getPaths();

		for (int i = 0; i < paths.size(); i++)
		{
			Map pathMap = (HashMap) paths.get(i);

			List pathDataSources = (ArrayList) pathMap.get(GCConstants.DATASOURCES_LIST);

			if (pathDataSources.containsAll(inputDataSources)
					& pathDataSources.containsAll(outputDataSources))
			{
				boolean onlyInputOutput = true;

				String path = "";

				for (int j = 0; j < pathDataSources.size(); j++)
				{
					if ((inputDataSources.contains(pathDataSources.get(j)))
							|| (outputDataSources.contains(pathDataSources.get(j))))
					{
						path += (pathDataSources.get(j) + "-");
					}
					else
					{
						onlyInputOutput = false;
						break;
					}
				}

				if (onlyInputOutput)
					validPaths.add(path.substring(0, path.length() - 1));
			}
		}

		return validPaths;
	}

	/**
	 * 8
	 * Paths traversing ALL the Input data sources first and then ALL the Output data sources should be filtered.
	 */
	private List traverseInputThanOutput()
	{
		List paths = new ArrayList();
		List validPaths = new ArrayList();

		List inputDataSourceLocation = new ArrayList();
		List outputDataSourceLocation = new ArrayList();

		List inputDataSources = parseInputDataSourceList((HashMap) selectedDataSources
				.get(GCConstants.INPUT_DATA_SOURCES));

		List outputDataSources = parseOutputDataSourceList((HashMap) selectedDataSources
				.get(GCConstants.OUTPUT_DATA_SOURCES));

		paths = MetadataManager.getPaths();

		for (int i = 0; i < paths.size(); i++)
		{
			Map pathMap = (HashMap) paths.get(i);

			List pathDataSources = (ArrayList) pathMap.get(GCConstants.DATASOURCES_LIST);

			if (pathDataSources.containsAll(inputDataSources)
					& pathDataSources.containsAll(outputDataSources))
			{
				boolean onlyInputOutput = true;

				String path = "";
				for (int j = 0; j < pathDataSources.size(); j++)
				{
					if (inputDataSources.contains(pathDataSources.get(j)))
						inputDataSourceLocation.add(new Integer(j));
					else if (outputDataSources.contains(pathDataSources.get(j)))
						outputDataSourceLocation.add(new Integer(j));

					if ((inputDataSources.contains(pathDataSources.get(j)))
							|| (outputDataSources.contains(pathDataSources.get(j))))
					{
						path += (pathDataSources.get(j) + "-");
					}
					else
					{
						onlyInputOutput = false;
						break;
					}
				}

				//				Logger.out.debug( "inputDataSourceLocation size():"+inputDataSourceLocation.size()+ "outputDataSourceLocation size():"+outputDataSourceLocation.size() );
				if (onlyInputOutput)
				{
					if (((Integer) outputDataSourceLocation.get(0)).intValue() > ((Integer) inputDataSourceLocation
							.get(inputDataSourceLocation.size() - 1)).intValue())
					{
						validPaths.add(path.substring(0, path.length() - 1));
					}
				}

				inputDataSourceLocation.clear();
				outputDataSourceLocation.clear();
			}
		}

		return validPaths;
	}

	/**
	 * This method parses map of InputDatasources and returns list of InputDatasources
	 * @param inputDataSourcesMap -  map of InputDatasources to parse
	 * @return - list of InputDatasources
	 */
	private List parseInputDataSourceList(Map inputDataSourcesMap)
	{
		List inputDataSources = new ArrayList();

		Collection keySet = inputDataSourcesMap.keySet();

		List keys = new ArrayList(keySet);

		for (int i = 0; i < keys.size(); i++)
		{
			List dataSources = (ArrayList) inputDataSourcesMap.get(keys.get(i));

			for (int j = 0; j < dataSources.size(); j++)
			{
				String dataSource = (String) dataSources.get(j);

				if (!inputDataSources.contains(dataSource))
				{
					inputDataSources.add(dataSource);
				}
			}
		}

		return inputDataSources;
	}

	/**
	 * This method parses map of OutputDatasources and returns list of OutputDatasources
	 * @param outputDataSourcesMap - map of OutputDatasources to parse
	 * @return - list of OutputDatasources
	 */
	private List parseOutputDataSourceList(Map outputDataSourcesMap)
	{
		List outputDataSources = new ArrayList();

		Collection keySet = outputDataSourcesMap.keySet();

		List keys = new ArrayList(keySet);

		for (int i = 0; i < keys.size(); i++)
		{
			if (!outputDataSources.contains(keys.get(i)))
			{
				outputDataSources.add(keys.get(i));
			}
		}

		return outputDataSources;
	}

	/**
	 * This method filter paths from the list of valid paths as per filter criteria passed
	 * @param ontFilterCode - indicates filter criteria for list of Paths to filter
	 * @param pathType - indicates filter criteria for type of Paths to filter
	 * @param startsWithDataSource - indicates StartWith filter criteria for all valid paths 
	 * @param endsWithDataSource - indicates EndsWith filter criteria for all valid paths
	 * @return - list of filtered paths
	 * @throws BizLogicException
	 * @throws DAOException
	 */
	private List filterPaths(String ontFilterCode, String pathType, String startsWithDataSource,
			String endsWithDataSource) throws BizLogicException, DAOException
	{
		List filteredPathsForDataSources = new ArrayList();

		List validPathsForDataSources = resultDataList;

		if (validPathsForDataSources != null)
		{
			//filtering paths for StartWith and EndsWith filter criteria
			for (int i = 0; i < validPathsForDataSources.size(); i++)
			{
				String validPath = (String) validPathsForDataSources.get(i);

				if (startsWithDataSource.equals("-1") & endsWithDataSource.equals("-1"))
				{
					filteredPathsForDataSources = validPathsForDataSources;
					break;
				}
				else
				{
					if ((startsWithDataSource.equals("-1")) & (!endsWithDataSource.equals("-1")))
					{
						if (validPath.endsWith(endsWithDataSource))
						{
							filteredPathsForDataSources.add(validPath);
						}
					}
					else if ((endsWithDataSource.equals("-1"))
							& (!startsWithDataSource.equals("-1")))
					{
						if (validPath.startsWith(startsWithDataSource))
						{
							filteredPathsForDataSources.add(validPath);
						}
					}
					else if ((!startsWithDataSource.equals("-1"))
							& (!endsWithDataSource.equals("-1")))
					{
						if (validPath.startsWith(startsWithDataSource)
								& validPath.endsWith(endsWithDataSource))
						{
							filteredPathsForDataSources.add(validPath);
						}
					}
				}
			}

			Logger.out.debug("Size before filter-->" + filteredPathsForDataSources.size());
			//Filtering Paths as per LinkType...

			//If LinkType to filter is All
			if (pathType.equals("-1"))
			{
				//Nothing to filter  :-)
			}
			//If LinkType to filter is Shortest
			else if (pathType.equals("2"))
			{
				int minNoOfNodes;

				minNoOfNodes = (((String) filteredPathsForDataSources.get(0)).length() / 2) + 1;

				for (int i = 1; i < filteredPathsForDataSources.size(); i++)
				{
					int noOfNodes = (((String) filteredPathsForDataSources.get(i)).length() / 2) + 1;
					Logger.out.debug("No. of Nodes-->" + noOfNodes + " Path-->"
							+ (String) filteredPathsForDataSources.get(i));
					if (noOfNodes < minNoOfNodes)
						minNoOfNodes = noOfNodes;
				}

				Logger.out.debug("Minimum no of Nodes-->" + minNoOfNodes);
				List shortestPathsForDataSources = new ArrayList();

				for (int i = 0; i < filteredPathsForDataSources.size(); i++)
				{
					int noOfNodes = (((String) filteredPathsForDataSources.get(i)).length() / 2) + 1;

					Logger.out.debug("NoOfNodes to check-->" + noOfNodes);

					if (noOfNodes == minNoOfNodes)
					{
						Logger.out.debug("Filtering in Shortest-->"
								+ (String) filteredPathsForDataSources.get(i));
						shortestPathsForDataSources.add(filteredPathsForDataSources.get(i));
					}
				}

				filteredPathsForDataSources = shortestPathsForDataSources;

			}
			//If LinkType to filter is Alignment=Based
			else if (pathType.equals("0"))
			{
				List alignmentPathsForDataSources = new ArrayList();

				for (int i = 0; i < filteredPathsForDataSources.size(); i++)
				{
					String filteredPath = (String) filteredPathsForDataSources.get(i);
					Logger.out.debug("Filtered Path==>" + filteredPath);

					boolean validPath = false;
					int counter = 0;

					WHILE : while (counter < (filteredPath.length() - 1))
					{
						List links = (ArrayList) dataSourcesLinksMap.get(filteredPath.substring(
								counter, counter + 3));

						for (int j = 0; j < links.size(); j++)
						{
							NameValueBean link = (NameValueBean) links.get(j);

							if (link.getValue().equals("4") || link.getValue().equals("8"))
							{
								//Logger.out.debug("Breaking while");
								validPath = true;
								break WHILE;
							}
						}
						//Logger.out.debug("Nodes->"+filteredPath.substring(counter, counter+3));

						counter += 2;
					}

					if (validPath)
					{
						Logger.out.debug("Truely valid Path-->" + filteredPath);
						alignmentPathsForDataSources.add(filteredPathsForDataSources.get(i));
					}
				}

				filteredPathsForDataSources = alignmentPathsForDataSources;
			}
			//If LinkType to filter is Non-Alignment
			else if (pathType.equals("1"))
			{
				List nonAlignmentPathsForDataSources = new ArrayList();
				for (int i = 0; i < filteredPathsForDataSources.size(); i++)
				{
					String filteredPath = (String) filteredPathsForDataSources.get(i);
					Logger.out.debug("Filtered Path==>" + filteredPath);

					boolean validPath = false;
					int counter = 0;

					WHILE : while (counter < (filteredPath.length() - 1))
					{
						List links = (ArrayList) dataSourcesLinksMap.get(filteredPath.substring(
								counter, counter + 3));

						for (int j = 0; j < links.size(); j++)
						{
							NameValueBean link = (NameValueBean) links.get(j);

							if (link.getValue().equals("1") || link.getValue().equals("2"))
							{
								//Logger.out.debug("Breaking while");
								validPath = true;
								break WHILE;
							}
						}
						//Logger.out.debug("Nodes->"+filteredPath.substring(counter, counter+3));

						counter += 2;
					}

					if (validPath)
					{
						Logger.out.debug("Truely valid Path-->" + filteredPath);
						nonAlignmentPathsForDataSources.add(filteredPathsForDataSources.get(i));
					}
				}
				filteredPathsForDataSources = nonAlignmentPathsForDataSources;
			}
		}

		Logger.out.debug("Size after filter-->" + filteredPathsForDataSources.size());

		return filteredPathsForDataSources;
	}

}
