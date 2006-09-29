/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
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
	String FORWARD_TO_SIMPLE_PAGE = "simpleSearch";
	String FORWARD_TO_RESULT_PAGE = "results";
	/********************************************************/

	/*********** Constants for Business Logic ***************/
	String SIMPLE_SEARCH_BIZLOGIC = "SimpleSearchBizLogic";
	/********************************************************/

	public static final String RESULT_DATA_LIST = "RESULT_DATA_LIST";

	public static final String DATA_SOURCES_KEY = "DATA_SOURCES_KEY";
	public static final String DATASOURCE_ID = "DATASOURCE_ID";
	public static final String DATASOURCE_NAME = "DATASOURCE_NAME";
	public static final String ATTRIBUTE = "ATTRIBUTE";
	public static final String CLASS = "CLASS";
	public static final String TYPE = "ATTRIBUTE_TYPE";

	public static final String SOURCE_CLASS = "SOURCE_CLASS";
	public static final String TARGET_CLASS = "TARGET_CLASS";
	public static final String ROLE_NAME = "ROLE_NAME";

	public static final String FREQUENCY_DISPLAY_SUFFIX = " (Frequency)";
	public static final String FREQUENCY_KEY_SUFFIX = " (Frequency)";
	public static final String SET_ID_KEY = "SET_ID";
	public static final String CONF_SCORE_KEY = "Confidence Score";
	public static final String CONF_SCORE_DISPLAY = "Confidence Score";

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

}