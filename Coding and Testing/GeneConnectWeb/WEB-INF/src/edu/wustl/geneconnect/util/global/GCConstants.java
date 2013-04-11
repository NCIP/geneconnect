/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>ClassName: java edu.wustl.geneconnect.GeneConnectConstants</p> 
 */

package edu.wustl.geneconnect.util.global;

/**
 * Constants file for GeneConnect Web application
 * @author mahesh_nalkande
 * @version 1.0
 */
public interface GCConstants
{

	/********** Constants used by Biz logic factory ***********/
	/**
	 * Business Action Element in xml file
	 */
	String BUSINESS_ACTION_ELEMENT = "business-action";

	/**
	 * Business Logic Element Iterator
	 */
	String BUSINESS_LOGIC_ELEMENT_ITERATOR = "business-logic";

	/**
	 * Instane Type element in xml file
	 */
	String INSTANCE_TYPE_ELEMENT = "instance-type";
	/**
	 * Configuration file for GC Busines Logic factory
	 */
	String GC_BUSINESS_LOGIC_CONFIG = "GCBusinessLogic.xml";
	/*********************************************************/

	/************** Constants for target actions ************/
	String TARGET_ACTION_POPULATE = "populate";

	String TARGET_ACTION_SEARCH = "search";
	/********************************************************/

	/**************** Constants for Action forwards *********/
	String FORWARD_TO_HOME_PAGE = "homePage";
	String FORWARD_TO_SIMPLE_PAGE = "simpleSearch";
	String FORWARD_TO_RESULT_PAGE = "results";
	String FORWARD_TO_ADVANCED_PAGE = "advancedSearch";
	String FORWARD_TO_ADVANCED_SELECT_PATH_PAGE = "advancedSearchSelectPath";
	String FORWARD_TO_GRAPH_PAGE = "geneconnectGraph";
	String FORWARD_TO_ADVANCED_SEARCH_RESULT_PAGE = "advancedSearchresults";
	String FORWARD_TO_SIMPLE_SEARCH_RESULT_PAGE = "simpleSearchresults";
	String FORWARD_TO_ADVANCED_SEARCH_FOR_BATCH = "batchSearch";
	String FORWARD_TO_ERROR_PAGE= "errorPage";
	/********************************************************/

	/**************** Constants for Action forms*********/
	public static final String FORM = "FORM";
	public static final String ADVANCED_SEARCH_FORM = "advancedSearchForm";
	public static final String SIMPLE_SEARCH_FORM = "simpleSearchForm";
	/********************************************************/
	/*********** Constants for Business Logic ***************/
	String SIMPLE_SEARCH_BIZLOGIC = "SimpleSearchBizLogic";
	String ADVANCED_SEARCH_BIZLOGIC = "AdvancedSearchBizLogic";
	String ONT_BIZLOGIC = "ONTBizLogic";
	/********************************************************/

	public static final String QUERY_KEY = "queryKey";
	public static final String QUERY_KEY_MAP = "QUERY_KEY_MAP";
	public static final String SELECTED_QUERY = "SELECTED_QUERY";
	public static final String ADVANCED_SEARCH = "advancedSearch";

	public static final String FREQUENCY_MAP = "FREQUENCY_MAP";
	public static final String ADVANCED_FORM_OUTPUTDS_SUFIX_KEY = "_FrequenceValue";
	/**
	 * Constants for Search result view
	 */
	public static final String SELECTED_ONT_LIST = "SELECTED_ONT_LIST";
	public static final String RESULT_DATA_LIST = "RESULT_DATA_LIST";
	public static final String SELECTED_DATASOURCES = "SELECTED_DATASOURCES";
	public static final String COLUMN_HEADERS = "COLUMN_HEADERS";
	public static final String COLUMN_HEADERS_REMOVED = "COLUMN_HEADERS_REMOVED";
	public static final String RESULT_LIST = "RESULT_LIST";
	public static final String GENOMICIDENTIIER_SET_RESULT_LIST = "GENOMICIDENTIIER_SET_RESULT_LIST";
	public static final String DATA_SOURCES_LIST = "DATA_SOURCES_LIST";
	public static final String VALID_PATHS_LIST_FOR_DATA_SOURCES = "VALID_PATHS_LIST_FOR_DATA_SOURCES";
	public static final String DATA_SOURCES_LINKS_MAP = "DATA_SOURCES_LINKS_MAP";
	public static final String DATA_SOURCES_MAP = "DATA_SOURCES_MAP";
	public static final String INPUT_DATA_SOURCES = "INPUT_DATA_SOURCES";
	public static final String OUTPUT_DATA_SOURCES = "OUTPUT_DATA_SOURCES";
	public static final String ALREADY_SELECTED_PATHS = "ALREADY_SELECTED_PATHS";
	public static final String FREQUENCY_DISPLAY_SUFFIX = " (Frequency)";
	public static final String FREQUENCY_KEY_SUFFIX = " (Frequency)";
	public static final String SET_ID_KEY = "SET_ID";
	public static final String SET_ID_DELIM = "_";
	public static final String CONF_SCORE_KEY = "Path Score";
	public static final String CONF_SCORE_DISPLAY = "Path Score";
	public static final String SELECTED_DATA_SOURCES = "SELECTED_DATA_SOURCES";

	public static final String DELEMITER = "_";
	public static final String SEARCH_RESULT = "SearchResult.csv";
	public static final String QUERY_KEY_ALL = "All";

	/**
	 * Column Names for DATASOURCE TABLE
	 */
	public static final String DATA_SOURCES_KEY = "DATA_SOURCES_KEY";
	public static final String DATASOURCE_ID = "DATASOURCE_ID";
	public static final String DATASOURCE_NAME = "DATASOURCE_NAME";
	public static final String DATASOURCE_ATTRIBUTES = "DATASOURCE_ATTRIBUTES";
	public static final String GRAPH_DATASOURCES = "GRAPH_DATASOURCES";
	public static final String GRAPH_DATASOURCES_LINKS = "GRAPH_DATASOURCES_LINKS";
	public static final String GRAPH_HIGHLIGHT_PATHS = "GRAPH_HIGHLIGHT_PATHS";
	public static final String GRAPH_HIGHLIGHT_PATHS_COUNTS = "GRAPH_HIGHLIGHT_PATHS_COUNTS";
	public static final String OUTPUT_ATTRIBUTE = "OUTPUT_ATTRIBUTE";
	public static final String ATTRIBUTE = "ATTRIBUTE";
	public static final String CLASS = "CLASS";
	public static final String GENOMIC_IDENTIFIER_CLASS = "GENOMIC_IDENTIFIER_CLASS";
	public static final String TYPE = "ATTRIBUTE_TYPE";

	public static final String ROW_FOR_GRAPH = "ROW_FOR_GRAPH";
	public static final String COL_FOR_GRAPH = "COL_FOR_GRAPH";
	public static final String DATASOURCE_LINK = "DATASOURCE_LINK";
	public static final String SOURCE_DATASOURCE_ID = "SOURCE_DATASOURCE_ID";
	public static final String TARGET_DATASOURCE_ID = "TARGET_DATASOURCE_ID";
	public static final String LINK_TYPE_ID = "LINK_TYPE_ID";
	public static final String PATH = "PATH";
	public static final String DATASOURCES_LIST = "DATASOURCES_LIST";
	public static final String ONT_FILTER_CODE = "ONT_FILTER_CODE";
	public static final String PATH_TYPE = "PATH_TYPE";
	public static final String STARTS_WITH_DATA_SOURCE = "STARTS_WITH_DATA_SOURCE";
	public static final String ENDS_WITH_DATA_SOURCE = "ENDS_WITH_DATA_SOURCE";

	/**
	 * Column Names for ROLELOOKUP TABLE
	 */
	public static final String SOURCE_CLASS = "SOURCE_CLASS";
	public static final String TARGET_CLASS = "TARGET_CLASS";
	public static final String ROLE_NAME = "ROLE_NAME";

	/**
	 * Keys for Pagenation
	 */
	public static final String SPREADSHEET_PATH_LIST = "SPREADSHEET_PATH_LIST";
	public static final String SPREADSHEET_DATA_LIST = "SPREADSHEET_DATA_LIST";
	public static final String SPREADSHEET_COLUMN_LIST = "SPREADSHEET_COLUMN_LIST";
	public static final String PAGINATION_DATA_LIST = "PAGINATION_DATA_LIST";
	public static final String PAGE_NUMBER = "pageNum";
	public static final String RESULTS_PER_PAGE = "numResultsPerPage";
	public static final String TOTAL_RESULTS = "totalResults";
	public static final String PREVIOUS_PAGE = "prevpage";
	public static final String NEXT_PAGE = "nextPage";
	public static final int ZERO = 0;
	public static final int START_PAGE = 1;
	public static final int NUMBER_RESULTS_PER_PAGE = 5;
	public static final int NUMBER_RESULTS_PER_PAGE_SEARCH = 15;
	public static final String CONFIDENCE = "confidenceScore";
	public static final String FREQUENCY = "frequency";
	public static final String SORTED_COLUMN = "sortedColumn";
	public static final String SORTED_COLUMN_INDEX = "sortedColumnIndex";
	public static final String SORTED_COLUMN_DIRECTION = "sortedColumnDirection";
	public static final String SORTED_COLUMN_ASC_ORDER = "asc";
	/**
	 * Values for null genomicIdentifier
	 */
	public static final String NO_MATCH_FOUND = "No Match Found";
	public static final String NO_MATCH_FOUND_MESSAGE = "NO_MATCH_FOUND_MESSAGE";
	public static final String NA = "N/A";

	public static final String DOAMIN_CLASSNAME_PREFIX = "edu.wustl.geneconnect.domain";
	public static final String FREQUENCY_VALUE = "FrequenceValue";

	public static final String SINGLE_QUERY_VALUE = "Single";
	public static final String BACTH_QUERY_VALUE = "Batch";
	public static final String SESSION_INVALIDATE = "SESSION_INVALIDATE";
	/*********** Constants for Home page ***************/
	String NO_OF_PARIWISE_LINKS = "NoOfPariwiseLinks";
	String NO_OF_GI_SETS = "NoOfGISets";
	String NO_OF_GRAPH_PATHS = "NoOfGraphPaths";
	/********************************************************/

	/*********** Constants for DATABASE TABLES ***************/
	String TABLE_DATASOURCE = "DATASOURCE";
	String TABLE_DATASOURCE_LINKS = "DATASOURCE_LINKS";
	String TABLE_ROLE_LOOKUP = "ROLE_LOOKUP";
	String TABLE_LINKTYPE = "LINKTYPE";
	String TABLE_PATH = "PATH";
	String TABLE_GENOMIC_IDENTIFIER_SET = "GENOMIC_IDENTIFIER_SET";
	String TABLE_ONT = "ONT";
	/********************************************************/
}